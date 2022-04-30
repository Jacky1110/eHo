package eho.jotangi.com

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Message
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yucheng.ycbtsdk.AITools
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.response.BleDataResponse
import com.yucheng.ycbtsdk.response.BleRealDataResponse
import eho.jotangi.com.network.response.*
import eho.jotangi.com.ui.wristband.ecg.EcgDataCallback
import eho.jotangi.com.utils.SharedPreferencesUtil
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainViewModel(var mainRepository: MainRepository): ViewModel() {

    val newsText = MediatorLiveData<String>()
    val memberAppMyPointData = MediatorLiveData<MemberAppMyPointResponse>()
    val myNextBookingData = MediatorLiveData<MyNextBookingDataResponse>()
    val myVesselData = MediatorLiveData<VesselDate>()
    val myGoBodyData = MediatorLiveData<GoBodyData>()
    val getWristBandSummaryData = MediatorLiveData<GetWristBandSummaryData>()
    val getEECPData = MediatorLiveData<GetEECPData>()

    val dayFootStepsData = MediatorLiveData<List<FootStepsData>>()
    val last7FootStepsData = MediatorLiveData<List<FootStepsData>>()
    val lastFootStepsData = MediatorLiveData<FootStepsData>()
    val dayTotalSteps = MediatorLiveData<Int?>()
    val weekTotalStepsData = MediatorLiveData<List<FootStepsData>>()
    val dayTotalCalories = MediatorLiveData<Int?>()
    val dayTotalMeters = MediatorLiveData<Int?>()

    val last7EcgData = MediatorLiveData<List<EcgData>>()

    val last7HeartRateData = MediatorLiveData<List<HeartRateData>>()
    val lastHeartRateData = MediatorLiveData<HeartRateData>()
    val last7HeartRateValue = MediatorLiveData<List<Int?>>()

    val last7OxygenDatas = MediatorLiveData<List<OxygenData>>()
    val lastOxygenData = MediatorLiveData<OxygenData>()
    val minOxygen = MediatorLiveData<Int>()
    val maxOxygen = MediatorLiveData<Int>()
    val rangeOxygen = MediatorLiveData<String>()
    val last7OxygenValue = MediatorLiveData<List<Int?>>()

    val last7BPData = MediatorLiveData<List<BPData>>()
    val lastBPData = MediatorLiveData<BPData>()

    //val SleepRecord = MediatorLiveData<List<DaySleepData>>()
    val daySleepData = MediatorLiveData<List<SleepData>>()
    val weekdaySleepData = MediatorLiveData<List<DaySleepData>>()
    val daySleepDetailData = MediatorLiveData<List<SleepDetailData>>()
    val totalSleepHour = MediatorLiveData<Int>()
    val totalSleepMinute = MediatorLiveData<Int>()
    val lastSleepStartTime = MediatorLiveData<String>()
    val lastSleepEndTime = MediatorLiveData<String>()
    val lastSleepQuality = MediatorLiveData<Int>()

    val getSportData = MediatorLiveData<List<SportData>>()

    val headshotPath = MediatorLiveData<String>()
    val userName = MediatorLiveData<String>()

    val bannerList = MediatorLiveData<List<BannerDataResponse?>>()
    val productList = MediatorLiveData<List<BestSalesProductDataResponse?>>()

    val editFavMsg = MediatorLiveData<String>()

    val getPict = MediatorLiveData<String>()

    val loading = MutableLiveData<Boolean>()

    val numberShoppingCart = MutableLiveData<Int>()

    val bondingWatch = MutableLiveData<Boolean>()
    val watchBatteryLevel = MutableLiveData<Int>(100)

    val userPicMessage = MutableLiveData<String>()

    private fun <T> toggleLoading(): SingleTransformer<T, T> {
        return SingleTransformer { single ->
            single.doOnSubscribe {
                GlobalScope.launch(Dispatchers.Main) {
                    loading.value = true
                }
            }.doFinally {
                GlobalScope.launch(Dispatchers.Main) {
                    loading.value = false
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun newsGetList() {
        mainRepository.newsGetList()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(toggleLoading())
            .subscribe({
                it.Data?.getOrNull(0)?.title?.let { title ->
                    newsText.value = title
                }
                Timber.e("newsGetList -> $it")
            }, {
                Timber.e("newsGetList -> $it")
            })
    }

    @SuppressLint("CheckResult")
    fun bannerGetList() {
        mainRepository.bannerGetList()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(toggleLoading())
            .subscribe({
                it.Data?.let { data ->
                    bannerList.value = data
                }
                Timber.e("bannerGetList -> $it")
            }, {
                Timber.e("bannerGetList -> $it")
            })
    }

    @SuppressLint("CheckResult")
    fun productGetBestSalesList() {
        mainRepository.productGetBestSalesList()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(toggleLoading())
            .subscribe({
                it.Data?.let { data ->
                    productList.value = data
                }
                Timber.e("productGetBestSalesList -> $it")
            }, {
                Timber.e("productGetBestSalesList -> $it")
            })
    }

    @SuppressLint("CheckResult")
    fun memberAppMyPoint() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.memberAppMyPoint()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    memberAppMyPointData.value = it
                    Timber.e("memberAppMyPoint -> $it")
                }, {
                    Timber.e("memberAppMyPoint -> $it")
                })
        }
    }

    @SuppressLint("CheckResult")
    fun getMyMemberCode() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getMyMemberCode()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    SharedPreferencesUtil.instances.setMemberCode(it?.Data)
                    Timber.e("getMyMemberCode -> $it")
                }, {
                    Timber.e("getMyMemberCode -> $it")
                })
        }
    }

    @SuppressLint("CheckResult")
    fun getMyMemberInfo() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getMyMemberInfo()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("getMyMemberInfo -> ${it.Data}")
                    SharedPreferencesUtil.instances.setAccountName(it.Data?.member_name)
                    refreshUserName()
                }, {
                    Timber.e("getMyMemberInfo -> $it")
                })
        }
    }


    @SuppressLint("CheckResult")
    fun getShopCartCount() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getShopCartCount()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("getShopCartCount -> $it")
                    numberShoppingCart.value = it?.Data?.num ?: 0
                }, {
                    Timber.e("getShopCartCount -> $it")
                })
        }
    }

    @SuppressLint("CheckResult")
    fun myNextBooking() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.myNextBooking()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    it.Data?.let {
                        myNextBookingData.value = it
                    }
                    Timber.e("myNextBooking -> $it")
                }, {
                    Timber.e("myNextBooking -> $it")
                })
        }
    }

    @SuppressLint("CheckResult")
    fun getWristBandSummary() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getWristBandSummary()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    it.Data?.let { data ->
                        getWristBandSummaryData.value = data
                    }
                    Timber.e("getWristBandSummary -> $it")
                }, {
                    Timber.e("getWristBandSummary -> $it")
                })
        }
    }

    @SuppressLint("CheckResult")
    fun getEECPData() {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getEECPData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    it.Data?.let { data ->
                        getEECPData.value = data
                    }
                    Timber.e("getWristBandSummary -> $it")
                }, {
                    Timber.e("getWristBandSummary -> $it")
                })
        } else {
            Timber.d("getEECPData, not login")
        }
    }

    @SuppressLint("CheckResult")
    fun getDataDate(category: String) {
        if (SharedPreferencesUtil.instances.isLogin()) {
            mainRepository.getDataDate(category)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("getDataDate -> ${it.Data}")
                    if (it.Data.isNotEmpty()) {
                        getIndexHealData(category, it.Data[0])
                    }
                }, {
                    Timber.e("getDataDate_t -> $it")
                })
        } else {
            Timber.d("getDataDate, not login")
        }
    }

    @SuppressLint("CheckResult")
    fun getIndexHealData(category: String, date: String) {
        if (category == "vessel") {
            mainRepository.getVesselData(date)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("getIndexHealData -> ${it.Data}")
                    it.Data?.let {
                        myVesselData.value = it
                    }
                }, {
                    Timber.e("getIndexHealData -> $it")
                })
        } else {
            mainRepository.getBodyMeasureRawData(date)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("getIndexHealData -> $it")
                    it.Data?.let {
                        myGoBodyData.value = it
                    }
                }, {
                    Timber.e("getIndexHealData -> $it")
                })
        }
    }

    fun clearWatchData() {
        dayTotalSteps.value = null
        dayFootStepsData.value = listOf()
        last7FootStepsData.value = listOf()
        lastFootStepsData.value = FootStepsData()
        dayTotalMeters.value = null
        dayTotalCalories.value = null
        daySleepData.value = listOf()
        daySleepDetailData.value = listOf()
        lastSleepEndTime.value = ""
        lastSleepQuality.value = 0
        lastSleepStartTime.value = ""
        totalSleepHour.value = 0
        totalSleepMinute.value = 0
        last7HeartRateData.value = listOf()
        lastHeartRateData.value = HeartRateData()
        last7HeartRateValue.value = listOf()
        lastBPData.value = BPData()
        last7BPData.value = listOf()
        lastOxygenData.value = OxygenData()
        maxOxygen.value = 0
        minOxygen.value = 0
        rangeOxygen.value = ""
        last7OxygenDatas.value = listOf()
        last7OxygenValue.value = listOf()
        last7EcgData.value = listOf()
        weekdaySleepData.value = listOf()
        weekTotalStepsData.value = listOf()
    }

    @SuppressLint("CheckResult")
    suspend fun getFootSteps(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getWristBandFootSteps(startTime, endTime, memberId).data
        Timber.d("footsteps_cc -> $data")
        if (data != null && data.isNotEmpty()) {
            data = data.sortedByDescending { it.sportStartTime }
            dayFootStepsData.postValue(data!!)
            val totalsteps: Int = data.map { it.sportStep?.toInt() }.filterNotNull().sum()
            val totalcalories: Int = data.map { it.sportCalorie?.toInt() }.filterNotNull().sum()
            val totalmeters: Int = data.map { it.sportDistance?.toInt() }.filterNotNull().sum()
            dayTotalSteps.postValue(totalsteps)
            dayTotalCalories.postValue(totalcalories)
            dayTotalMeters.postValue(totalmeters)
            val list = data.take(7)
            last7FootStepsData.postValue(list!!)
            lastFootStepsData.postValue(list.get(0))
        }
    }

    @SuppressLint("CheckResult")
    suspend fun getFootSteps2(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getWristBandFootSteps(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            var map = HashMap<String, Int>()
            for (d in data) {
                var date = d.sportEndTime!!.split(" ")[0]
                if (map.containsKey(date)) {
                    var step = map.get(date)!!.toInt()
                    step += d.sportStep!!.toInt()
                    map[date] = step
                } else
                    map[date] = d.sportStep!!.toInt()
            }
            var dates = map.keys
            var result = mutableListOf<FootStepsData>()
            for (date in dates) {
                result.add(FootStepsData(sportEndTime = date, sportStep = map[date].toString()))
            }
            result = result.sortedBy { it.sportEndTime }.toMutableList()
            weekTotalStepsData.postValue(result)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun getHeartRate(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getHeartRate(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.heartStartTime }
            last7HeartRateData.postValue(list!!)
            if (list != null && list.isNotEmpty()) {
                lastHeartRateData.postValue(list.get(0))
                try {
                    val values = list.map { it.heartValue?.toInt() }
                    last7HeartRateValue.postValue(values)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    suspend fun getECG(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getECG(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.ecgStartTime }.take(7)
            last7EcgData.postValue(list)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun getOxygen(startTime: String, endTime: String, memberId: String) {
        var min = 0
        var max = 0
        val data = mainRepository.getOxygen(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.startTime }
            if (list != null && list.isNotEmpty()) {
                last7OxygenDatas.postValue(list)
                lastOxygenData.postValue(list.get(0))
                min = 100
                max = 0
                try {
                    val values = list.map { it.OOValue?.toInt() }
                    last7OxygenValue.postValue(values)

                    list.forEach { item ->
                        val v = item.OOValue?.toInt()
                        if (v != null) {
                            if (v < min) min = v
                        }
                        if (v != null) {
                            if (v > max) max = v
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        minOxygen.postValue(min)
        maxOxygen.postValue(max)
        rangeOxygen.postValue(String.format(Locale.getDefault(), "%d~%d", min, max))
    }

    @SuppressLint("CheckResult")
    suspend fun getSleep(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getSleep(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val yestodaystr = getYestodayString()
            data = data.sortedByDescending { it.startTime }
            val data2 = data.filter { item -> item.endTime!!.compareTo(yestodaystr) > 0 }
            if (data2 != null && data2.isNotEmpty()) {
                lastSleepEndTime.postValue(data2[0].endTime!!)
                lastSleepStartTime.postValue(data2.last().startTime!!)
            } else {
                lastSleepEndTime.postValue(data[0].endTime!!)
                lastSleepStartTime.postValue(data[0].startTime!!)
            }
        } else {
            lastSleepEndTime.postValue("")
            lastSleepStartTime.postValue("")
        }
        daySleepData.postValue(data!!)
    }

    @SuppressLint("CheckResult")
    suspend fun getSleepDetail(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getSleepDetail(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val yestodaystr = getYestodayString()
            data = data.sortedByDescending { it.sleepStartTime }
            data = data.filter { item ->
                isBeforeTime(item, yestodaystr)
            }
            if (data != null && data.isNotEmpty()) {
                var totalseconds = 0
                try {
                    data.forEach { item ->
                        totalseconds += item.sleepLen?.toInt()!!
                    }
                    var minute = totalseconds / 60
                    val hour = minute / 60
                    minute = minute % 60
                    totalSleepHour.postValue(hour)
                    totalSleepMinute.postValue(minute)

                    lastSleepStartTime.value?.let {
                        lastSleepEndTime.value?.let { it1 ->
                            calculateSleepQuality(
                                it,
                                it1, totalseconds
                            )
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            daySleepDetailData.postValue(data!!)
        }
//        else {
//            totalSleepHour.postValue(0)
//            totalSleepMinute.postValue(0)
//            lastSleepEndTime.postValue("")
//        }
    }

    @SuppressLint("CheckResult")
    suspend fun getSleepDetail2(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getSleepDetail(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
//            val yestodaystr = getYestodayString()
            data = data.sortedBy { it.sleepStartTime }
            Log.d("getSleepDetail2", data.toString())
//            data = data.filter { item ->
//                isBeforeTime(item, yestodaystr)
//            }
            if (data != null && data.isNotEmpty()) {
                var totalseconds = 0
                var deepseconds = 0
                var tmap = HashMap<String, Int>()
                var dmap = HashMap<String, Int>()
                try {
                    data.forEach { item ->
                        //Log.d("getSleepDetail2",item.sleepStartTime.toString())
                        //Log.d("getSleepDetail2",item.sleepType.toString())
                        var date = item.sleepStartTime.toString().split(" ")[0]
                        if (tmap.containsKey(date)) {
                            var num = tmap.get(date)!!
                            num += item.sleepLen?.toInt()!!
                            tmap.put(date, num)
                        } else {
                            //  var dd = intArrayOf()
//                            if(item.sleepType == "241"){
//                                deepseconds+=item.sleepLen?.toInt()!!
//                            }
                            //  totalseconds += item.sleepLen?.toInt()!!
                            tmap.put(date, item.sleepLen?.toInt()!!)
                        }
                    }
                    data.forEach { item ->
                        //Log.d("getSleepDetail2",item.sleepStartTime.toString())
                        //Log.d("getSleepDetail2",item.sleepType.toString())
                        var date = item.sleepStartTime.toString().split(" ")[0]
                        if (dmap.containsKey(date)) {
                            if (item.sleepType == "241" || item.sleepType == "0xF1") {
                                var num = dmap.get(date)!!
                                num += item.sleepLen?.toInt()!!
                                dmap.put(date, num)
                            }
                        } else {
                            //  var dd = intArrayOf()
//                            if(item.sleepType == "241"){
//                                deepseconds+=item.sleepLen?.toInt()!!
//                            }
                            //  totalseconds += item.sleepLen?.toInt()!!
                            if (item.sleepType == "241" || item.sleepType == "0xF1")
                                dmap.put(date, item.sleepLen?.toInt()!!)
                        }
                    }
                    var stmap = tmap.toSortedMap()
                    var sdmap = dmap.toSortedMap()
                    Log.d("getSleepDetail2_total", stmap.toString())
                    Log.d("getSleepDetail2_deep", sdmap.toString())
                    var result = mutableListOf<DaySleepData>()
                    for (key in stmap.keys) {
                        var thour = stmap.get(key)!! / 3600
                        result.add(DaySleepData(key, 0, thour))
                    }
                    for (key in sdmap.keys) {
                        var dhour = sdmap.get(key)!! / 3600
                        result.add(DaySleepData(key, 1, dhour))
                    }
                    weekdaySleepData.postValue(result)

                    //var minute = totalseconds / 60
                    //val hour = minute / 60

//                    totalSleepHour.postValue(hour)
//                    totalSleepMinute.postValue(minute)
//
//                    lastSleepStartTime.value?.let {
//                        lastSleepEndTime.value?.let { it1 ->
//                            calculateSleepQuality(
//                                it,
//                                it1, totalseconds
//                            )
//                        }
//                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            // daySleepDetailData.postValue(data!!)
        }
    }

    private fun calculateSleepQuality(startstr: String, endstr: String, seconds: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            val start = sdf.parse(startstr)
            val end = sdf.parse(endstr)
            val diff = (end.time - start.time) / 1000
            val quality: Int = (seconds * 100 / diff).toInt()
            lastSleepQuality.postValue(quality)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun isBeforeTime(item: SleepDetailData, timestr: String): Boolean {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(item.sleepStartTime)
            val calendar = Calendar.getInstance()
            calendar.time = date
            item.sleepLen?.let { calendar.add(Calendar.SECOND, it.toInt()) }
            val endtime = calendar.time
            val endstr = sdf.format(endtime)

            return endstr.compareTo(timestr) > 0
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }

    private fun getYestodayString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val yestoday = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val yestodaystr = sdf.format(yestoday)
        return yestodaystr
    }

    @SuppressLint("CheckResult")
    suspend fun getBP(startTime: String, endTime: String, memberId: String) {
        val data = mainRepository.getBP(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.bloodStartTime }
            last7BPData.postValue(list!!)
            lastBPData.postValue(list!!.get(0))
        }
    }

    @SuppressLint("CheckResult")
    suspend fun getSport(startTime: String, endTime: String, memberId: String) {
        var data = mainRepository.getSport(startTime, endTime, memberId).data
        if (data != null && data.isNotEmpty()) {
            getSportData.postValue(data!!)
        }
    }

    @SuppressLint("CheckResult")
    fun editFavorite(id: String?) {
        if (SharedPreferencesUtil.instances.isLogin() && !id.isNullOrEmpty()) {
            mainRepository.editFav(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    it.Message?.let { msg ->
                        editFavMsg.value = msg
                    }

                    Timber.e("editFavorite($id) -> $it")
                }, {
                    Timber.e("editFavorite($id) -> $it")
                })
        }
    }
    //4/26
    fun getPicture(admin_name: String, Mode: String, picture_url: String ){
        if(SharedPreferencesUtil.instances.isLogin() && !admin_name.isNullOrEmpty() && !Mode.isNullOrEmpty() && !picture_url.isNullOrEmpty())
            mainRepository.getPic(admin_name, Mode, picture_url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.isSuccess?.let { msg ->
                        getPict.value = msg.toString()
                    }
                    Timber.e("getPicture($admin_name , $Mode , $picture_url) -> $it")

                }, {
                    Timber.e("getPicture($$admin_name , $Mode , $picture_url) -> $it")
                })
    }


    fun refreshHeadShotPath() {
        headshotPath.postValue(SharedPreferencesUtil.instances.getAccountHeadShot())
    }

    fun refreshUserName() {
        userName.value = SharedPreferencesUtil.instances.getAccountName()
    }

    fun setBondingWatch(bl: Boolean) {
        bondingWatch.postValue(bl)
    }

    fun setWatchBatteryLevel(level: Int) {
        watchBatteryLevel.postValue(level)
    }

    fun getWatchInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val state = YCBTClient.connectState()
            if (state == Constants.BLEState.ReadWriteOK) {
                doGetDeviceInfo()
            } else {
                val mac = SharedPreferencesUtil.instances.getWatchMac()
                if (mac != null) {
                    YCBTClient.disconnectBle()
                    YCBTClient.connectBle(mac) { code ->
                        if (code == Constants.CODE.Code_OK) {
                            doGetDeviceInfo()
                        }
                    }
                }
            }
        }
    }

    fun getWatchBatteryLevel(): Int? {
        return watchBatteryLevel.value
    }

    private fun doGetDeviceInfo() {
        YCBTClient.getDeviceInfo { code, ratio, resultMap ->
            Timber.d("BleScanReceiver::doGetDeviceInfo, code=$code, ratio=$ratio")
            if (resultMap != null) {
                Timber.d("BleScanReceiver::doGetDeviceInfo, data=$resultMap")
                val dataObj = resultMap.get("data")
                if (dataObj is Map<*, *>) {
                    val level: Int = dataObj.get("deviceBatteryValue") as Int
                    setWatchBatteryLevel(level)
                }
            }
        }
    }

    fun appECGTest(callback: EcgDataCallback.Start) {
        Log.d("eeeECG", "appECGTest")
        var aitool = AITools.getInstance()
        aitool.init()
        aitool.setAIDiagnosisHRVNormResponse {

        }
        YCBTClient.appEcgTestStart({ i, fl, hashMap ->
        }, { i, hashMap ->
            hashMap.let {
                try {
                    when (i) {
                        Constants.DATATYPE.Real_UploadECG -> {
                            val tData = hashMap["data"] as ArrayList<Int>
                            //Log.d("eee",tData.toString())
                            //var aa = aitool.ecgRealWaveFiltering(listToBytes(tData))
                            for (i in 0 until tData.size) {
                                var value = tData[i].toFloat()
                                callback.receiveECG(value)
                            }
                        }
                        Constants.DATATYPE.Real_UploadECGHrv -> {
                            val tData = hashMap["data"] as Float
                            callback.receiveHRV(tData.toInt())
                        }
                        Constants.DATATYPE.Real_UploadBlood -> {
                            val heart = hashMap["heartValue"] as Int
                            val tDBP = hashMap["bloodDBP"] as Int
                            val tSBP = hashMap["bloodSBP"] as Int
                            callback.receiveBlood(heart, tDBP, tSBP)
                        }
                        else -> {
                            callback.receiveBadSignal()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("eeeECG_ERROR", e.toString())
                }
            }
        })
    }

    fun appECGTestEnd(callback: EcgDataCallback.End) {
        try {
            YCBTClient.appEcgTestEnd { i, fl, hashMap ->
                callback.receive(fl)
            }
        } catch (e: Exception) {
            Log.d("eeeECG_ERROR", e.toString())
        }
    }

    fun listToBytes(data: ArrayList<Int>): ByteArray {
        val bytes =
            data.foldIndexed(ByteArray(data.size)) { i, a, v -> a.apply { set(i, v.toByte()) } }
        return bytes
    }

    suspend fun userPic(pic: File) {
        Log.d("it name", pic.name)
        if (SharedPreferencesUtil.instances.isLogin()){
            mainRepository.userPic(pic)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(toggleLoading())
                .subscribe({
                    Timber.e("海波浪 -> ${it.Message}")
                    if (it.Message?.isNotEmpty() == true) {
                        Log.d("海波浪 -> ${it.Message}", "~~")
                        userPicMessage.value = it.Message.toString()
//                        SharedPreferencesUtil.instances.setAccountPic(it.Message)

                    }
                }, {
                    Timber.e("getDataData_t -> $it")
                })
    }else {
        Timber.d("getDataData, not login")
        }
    }
}