package eho.jotangi.com

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.jotangi.baseutils.DialogUtils
import eho.jotangi.com.databinding.ActivityMainBinding
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleDeviceToAppDataResponse
import eho.jotangi.com.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.yucheng.ycbtsdk.response.BleDataResponse
import eho.jotangi.com.utils.*
import eho.jotangi.com.utils.smartwatch.watchdatamodel.YCBTDataHelper
import eho.jotangi.com.utils.smartwatch.watchdatamodel.YCBTDataResponse
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class MainActivity : AppCompatActivity() {
    private var TAG = "MainActivity"

    private val flagScheduleSyncWatch:AtomicBoolean = AtomicBoolean(false)

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private var navView: BottomNavigationView? = null

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNav()

        mainViewModel.newsGetList()
        mainViewModel.bannerGetList()
        mainViewModel.productGetBestSalesList()

        refreshMemberInfoOnHome()

        displayIntentData()

        // 有綁定手環的才需要藍芽
        val mac = SharedPreferencesUtil.instances.getWatchMac()
        if (mac != null) {
            if (!WatchUtils.instance.isBluetoothEnabled()) {
                requestBluetoothEnable()
            } else {
                initWatchSDK()
            }
        }

        // 這裡面會去檢查定位權限,
        getLastLocation()

        checkBluetooth()
    }

    private fun checkBluetooth(){ //android 12+ need
        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("test006", "${it.key} = ${it.value}")
                }
            }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var url = intent?.data.toString()
        if(url!=null && url.isNotEmpty()){
            try {
                Log.d(TAG, "displayIntentData(), onNewIntent")
                Log.d(TAG, "displayIntentData(), $url")
                var status = url.split("||")
                var isSuccess = status[1].split("@")[1]
                var backtype = status[1].split("@")[2]
                var replaceHead = "https" + status[0].substring(status[0].indexOf(":"))
                var final = replaceHead.split("?url=")
                var ff = final[1].split("@")
                var finalURL =
                    final[0] + ":10443" + ff[0] + "&" + ff[1] + "&" + ff[2] + "&" + isSuccess + "&" + backtype
                Log.d(TAG, "displayIntentData(), $finalURL")
                openWeb(finalURL)
            }catch (e:Exception){
                Log.d("onNewIntent_error",e.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("$TAG, onStart")

        //scheduleOneTimeWorker()

    }

    override fun onStop() {
        super.onStop()
        Timber.d("$TAG, onStop")
        flagScheduleSyncWatch.compareAndSet(true, false)
        //scheduleSyncWatch()
    }

    /**
     * onResume
     *      每次App被喚起到前景時同步一次手環資料到後台
     */
    override fun onResume() {
        super.onResume()
        Timber.d("$TAG, onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("$TAG, onPause")

    }

    private fun setupNav() {
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController?.let {nController->
            navView?.setupWithNavController(nController)

            nController.addOnDestinationChangedListener { _, destination, _ ->

                Timber.e("DestinationChangedListener -> destination: $destination")
                when (destination.id) {
                    R.id.nav_home,
                    R.id.nav_booking,
                    R.id.nav_map,
                    R.id.nav_web,
                    R.id.nav_store,
                    R.id.nav_watch_info,
                    R.id.nav_watch_scan,
                    R.id.nav_notification,
                    R.id.nav_cart,
                    R.id.nav_wristband,
                    R.id.nav_mall -> {
                        showBottomNav()
                    }
                    R.id.nav_member -> {
                        checkLogin()
                        showBottomNav()
                    }
                    else -> hideBottomNav()
                }
            }
        }
    }

    fun openCart(){
        navController?.navigate(R.id.nav_cart)
    }

    fun openWeb(url: String, title: String? = null){
        val bundle = Bundle()
        bundle.putString(BundleKey.TITLE, title)
        bundle.putString(BundleKey.URL, url)
        navController?.navigate(R.id.nav_web, bundle)
    }

    fun openNotification(){
        navController?.navigate(R.id.nav_notification)
    }

    fun openRecommendedCode(){
        if (checkLogin()) {
            SharedPreferencesUtil.instances.getMemberCode()?.let { code ->
                AlertDialog.Builder(this).apply {
                    setTitle(R.string.recommended_code)
                    setMessage(code)
                    setNegativeButton(R.string.copy) { dialog, _ ->
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText("member code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(this@MainActivity, R.string.copied, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    setCancelable(true)
                }.create().show()
            }?:mainViewModel.getMyMemberCode()
        }
    }

    private fun showBottomNav() {
        navView?.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        navView?.visibility = View.GONE
    }

    private fun openLoginPage(){
        navController?.popBackStack(R.id.nav_home, false)
        navController?.navigate(R.id.nav_login)
    }

    fun navigationMallPage(){
        navController?.popBackStack(R.id.nav_home, false)
        navController?.navigate(R.id.nav_mall)
    }

    fun navigationBookingPage(){
        navController?.popBackStack(R.id.nav_home, false)
        navController?.navigate(R.id.nav_booking)
    }

    fun navigationWristBandNotBindingPage(){
        navController?.popBackStack(R.id.nav_home, false)
        openWeb(JotangiUtilConstants.WebUrl.WRISTBAND_NOT_BINDING)
    }

    fun navigationWristBandPage(){
        navController?.popBackStack(R.id.nav_home, false)
        navController?.navigate(R.id.nav_wristband)
    }

    override fun onDestroy() {
        super.onDestroy()

        isLaunch = true
    }

    fun checkLogin(): Boolean{
        return if (SharedPreferencesUtil.instances.isLogin()){
            true
        }else{
            openLoginPage()
            false
        }
    }

    fun backHome() {
        navController?.popBackStack(R.id.nav_home, true)
        navController?.navigate(R.id.nav_home)
    }

    fun logout(){
        SharedPreferencesUtil.instances.logout()
        mainViewModel.editFavMsg.value = null
        mainViewModel.getEECPData.value = null
        mainViewModel.myNextBookingData.value = null
        mainViewModel.myVesselData.value = null
        mainViewModel.myGoBodyData.value = null
        mainViewModel.memberAppMyPointData.value = null
        mainViewModel.numberShoppingCart.value = null
        watchLogout()
    }

    fun watchLogout(){
        YCBTClient.disconnectBle()
        SharedPreferencesUtil.instances.setWatchMac(null)
        SharedPreferencesUtil.instances.setWatchName(null)
        mainViewModel.clearWatchData()
    }

    fun refreshMemberInfoOnHome(){
        mainViewModel.memberAppMyPoint()
        mainViewModel.myNextBooking()
        //mainViewModel.getWristBandSummary()
        mainViewModel.getEECPData()
        mainViewModel.refreshHeadShotPath()
        mainViewModel.refreshUserName()
        mainViewModel.getShopCartCount()
        mainViewModel.getMyMemberCode()
        mainViewModel.getDataDate("body_measure")
        mainViewModel.getDataDate("vessel")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ){
            when(requestCode){
                JotangiUtilConstants.PERMISSION_LOCATION_REQUEST_CODE -> {
                    getLastLocation()
                }
                JotangiUtilConstants.PERMISSION_CAMERA_REQUEST_CODE -> {
                    checkCameraPermission(this)
                }
            }
        }
    }

    fun getLastLocation(){
        if (!LocationHelper.checkLocationPermission(this)){
            Timber.e("LOCATION-> checkLocationPermission: false")
            LocationHelper.requestLocationPermissions(this)
            return
        }
        Timber.e("LOCATION-> getLastLocation")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    location?.let {
                        Timber.e("LOCATION-> lat: ${location.latitude}, lng: ${location.longitude}")
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        LocationHelper.setSharePreferenceLocation(location)
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun displayIntentData() {
        //val islogin: Boolean = ProjSharePreference.getLoginState(this)
        val intent = intent
        Log.d(TAG, "displayIntentData(), intent=$intent")
        val action = intent.action
        Log.d(TAG, "displayIntentData(), action=$action")
        val data = intent.data
        Log.d(TAG, "displayIntentData(), data=$data")
        val strdata = intent.dataString
        Log.d(TAG, "displayIntentData(), strdata=$strdata")
        if (data != null) {
            val scheme = data.scheme //
            Log.d(TAG, "displayIntentData(), scheme=$scheme")
            val host = data.host //
            Log.d(TAG, "displayIntentData(), host=$host")
            val params = data.pathSegments
            if (params != null) {
                for (i in params.indices) {
                    Log.d(TAG, "displayIntentData(), p" + i + "=" + params[i])
                }
            }

        } else {
        }

    }

    fun requestBluetoothEnable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        reqestBluetoothLuncher.launch(intent)
    }

    var reqestBluetoothLuncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Timber.d(TAG, "onActivityResult")
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                //val path = data.getStringExtra("path")
            }

            initWatchSDK()
        }
    }

    private fun initWatchSDK() {
        val mac = SharedPreferencesUtil.instances.getWatchMac()
        Timber.d("$TAG, initWatchSDK(), mac=$mac")
        if (mac != null) {
            val bindmac = YCBTClient.getBindDeviceMac()
            val bindname = YCBTClient.getBindDeviceName()

            if(YCBTClient.connectState()!=Constants.BLEState.ReadWriteOK) {
                YCBTClient.disconnectBle()
                YCBTClient.connectBle(mac, BleConnectResponse { code ->
                    Timber.d("$TAG, connectBle, mac=$bindmac, ret code=$code")
                    if (code == Constants.CODE.Code_OK) {
                        settingWatchLanguage(0x0c)
                        scheduleOneTimeWorker()
                    }
                })
            }else{
                scheduleOneTimeWorker()
            }
        }
    }

    private fun scheduleOneTimeWorker() {
        val mac = SharedPreferencesUtil.instances.getWatchMac()
        if (mac != null) {
            Timber.d("$TAG, scheduleOneTimeWorker(), mac=$mac")
            if (flagScheduleSyncWatch.compareAndSet(false, true)) {
                Timber.d("$TAG, scheduleOneTimeWorker(), cancelAllWorker")
                WatchUtils.instance.cancelAllWorker()
                Timber.d("$TAG, scheduleOneTimeWorker(), initOneTimeWorker")
                WatchUtils.instance.initOneTimeWorker(3, TimeUnit.SECONDS)
            } else {
                Timber.d("$TAG, scheduleOneTimeWorker(), set flag fail, mac=$mac")
            }
        } else {
            Timber.d("$TAG, scheduleOneTimeWorker(), mac=null")
        }
    }

    private fun scheduleSyncWatch() {
        WatchUtils.instance.initPeriodWorker()
    }

    private fun getHistoryData() {
        YCBTClient.healthHistoryData(
            0x02//0x2D
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getOxygenHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getOxygenHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {

                    }
                } else {
                    Timber.d("$TAG, getOxygenHistoryData succss, nodata")
                }
            } else {
                Timber.d("$TAG, getOxygenHistoryData, fail, code=$code")
            }
        }

    }
    /**
     *  0x00:英语 0x01: 中文 0x02: 俄语 0x03: 德语 0x04: 法语 0x05: 日语 0x06: 西班牙语
     *  0x07: 意大利语 0x08: 葡萄牙文 0x09: 韩文 0x0A: 波兰文 0x0B: 马来文 0x0C: 繁体中文 0xFF:其它
     */
    private fun settingWatchLanguage(language : Int) {
        YCBTClient.settingLanguage(language, BleDataResponse { code, ratio, hashMap ->
            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, settingWatchLanguage, ok, language=$language")
            } else {
                Timber.d("$TAG, settingWatchLanguage, error, language=$language, ret=$code")
            }
        })
    }

    /**
     *  只有dataType欄位, 與文件不符, 無法用來判斷目前手錶語言
     *
     */
    private fun getScreenInfo() {
        YCBTClient.getScreenInfo { i, v, hashMap ->
            if (i == 0) {
                val prelang = SharedPreferencesUtil.instances.getWatchIntValue(
                    SharedPreferencesUtil.instances.KEY_WATCH_LANGUAGE,
                    0x0C    // 中文
                )
                if (hashMap != null) {
                    val currentScreenDisplayLevel =
                        hashMap["currentScreenDisplayLevel"] as Int //当前屏幕显示等级
                    val currentScreenOffTime = hashMap["currentScreenOffTime"] as Int //当前屏幕息屏时间
                    val currentLanguageSettings = hashMap["currentLanguageSettings"] as Int //当前语言设置
                    val CurrentWorkingMode = hashMap["CurrentWorkingMode"] as Int //当前工作模式
                    if (currentLanguageSettings != prelang) {
                        settingWatchLanguage(prelang)
                    }
                }
            }
        }
    }

    private fun getWatchHistoryOutline() {
        YCBTClient.getHistoryOutline { code, v, hashMap ->
            if (code == Constants.CODE.Code_OK) {
                if (hashMap != null) {
                    Timber.d("$TAG, getWatchHistoryOutline, data=$hashMap")
                    val sleepNum = hashMap["SleepNum"] as Int //睡眠条数
                    val sleepTotalTime = hashMap["SleepTotalTime"] as Int //睡眠总条数
                    val heartNum = hashMap["HeartNum"] as Int //心率条数
                    val sportNum = hashMap["SportNum"] as Int //运动条数
                    val bloodNum = hashMap["BloodNum"] as Int //血压条数
                    val bloodOxygenNum = hashMap["BloodOxygenNum"] as Int //血氧条数
                    val tempHumidNum = hashMap["TempHumidNum"] as Int //温湿度条数
                    val tempNum = hashMap["TempNum"] as Int //体温条数
                    val ambientLightNum = hashMap["AmbientLightNum"] as Int //环境光条数
                }
            }
        }
    }

    var toAppDataResponse =
        BleDeviceToAppDataResponse { dataType, dataMap ->
            Timber.e("$TAG, BleDeviceToAppDataResponse, datatype=$dataType, ")
            Timber.e(dataMap.toString())
        }
}