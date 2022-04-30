package eho.jotangi.com.utils.smartwatch

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentWatchScanBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.utils.SharedPreferencesUtil
import timber.log.Timber
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import eho.jotangi.com.utils.DialogUtils
import java.util.concurrent.TimeUnit

class WatchScanFragment : BaseFragment() {

    private var _binding: FragmentWatchScanBinding? = null
    private val binding get() = _binding!!
    private val deviceList = arrayListOf<ScanDeviceBean>()
    private lateinit var watchScanAdapter: WatchScanDeviceAdapter
    private var isBonding = false
    private var watchmac : String?=null
    private var watchname : String?=null
    private var mac:String?=null

    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchScanBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBack(R.string.watchsetting, isShowLogo = true)
        initDeviceList()

        binding.tvRescan.setOnClickListener{
            startScanDevices()
        }

    }

    override fun onResume() {
        super.onResume()
        isBonding = false
        registerScanReceiver()
        mac = SharedPreferencesUtil.instances.getWatchMac()
        if (!WatchUtils.instance.isBluetoothEnabled()) {
            requestBluetoothEnable()
        } else {
            //if (mac == null) {
            if (YCBTClient.isScaning()) {
                YCBTClient.stopScanBle()
            }
                startScanDevices()
            //}
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterScanReceiver()
        if (YCBTClient.isScaning()) {
            YCBTClient.stopScanBle()
        }
        binding.progressbar.visibility = View.GONE
    }

    private fun startScanDevices() {
        binding.tvNone.visibility = View.GONE
        binding.tvRescan.visibility = View.GONE
        binding.progressbar.visibility = View.VISIBLE
        watchmac = null
        watchname = null
        deviceList.clear()
        //addBondedDevice()
        watchScanAdapter.notifyDataSetChanged()
        YCBTClient.startScanBle({ code, scanBean ->
            Timber.d("$TAG, startScanDevices(), code=$code")
            if (code == Constants.CODE.Code_OK){
                if (scanBean != null) {
                    Timber.d("$TAG, startScanDevices(), found ${scanBean.deviceMac}, ${scanBean.deviceName}, ${scanBean.deviceRssi}")
                    addDeviceToList(scanBean)
                }
            }
            else /*if (code == Constants.CODE.Code_TimeOut)*/ {
                activity?.runOnUiThread {
                    binding.progressbar.visibility = View.GONE
                    if (deviceList.size == 0) {
                        binding.tvNone.visibility = View.VISIBLE
                        binding.tvRescan.visibility = View.VISIBLE
                    }
                }
            }
        }, 10)
    }

    private fun addDeviceToList(scanBean: ScanDeviceBean) {
        var found = false
        if (deviceList.size > 0) {
            deviceList.forEach { item ->
                if (item.deviceMac.equals(scanBean.deviceMac)) {
                    found = true
                }
            }
        }
        if (!found) {
            deviceList.add(scanBean)
            watchScanAdapter.notifyItemChanged(deviceList.size - 1)
        }
    }

    private fun doStopScan() {
        YCBTClient.stopScanBle()
    }

    private fun addBondedDevice() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bleDevices = bluetoothAdapter.bondedDevices
        if (bleDevices != null && bleDevices.size > 0) {
            bleDevices.forEach { device ->
                val addr = device.address
                val name = device.name
                val type = device.type

                // todo: 如果使用者自己改名會被過濾掉
                if (name.startsWith("E80DL")) {
                    val scanBean = ScanDeviceBean()
                    scanBean.deviceMac = addr
                    scanBean.deviceName = name
                    addDeviceToList(scanBean)
                }
            }
        }
    }

    private fun initDeviceList() {
        watchScanAdapter = WatchScanDeviceAdapter(deviceList)
        //addBondedDevice()
        watchScanAdapter.itemClick = { it ->
            doStopScan()
            Timber.d("$TAG, device clicked, ${it.deviceMac}, ${it.deviceName}, ${it.deviceRssi}")
            val oldmac = SharedPreferencesUtil.instances.getWatchMac()
            if (!it.deviceMac.equals(oldmac)) {
                Timber.d("$TAG, device clicked, disconnect previous device")
                Timber.d("$TAG, device clicked, connect device, ${it.deviceMac}")
                watchmac = it.deviceMac
                watchname = it.deviceName
                doBondDevice(it.deviceMac)
            } else {
                val state = YCBTClient.connectState()
                if (state == Constants.BLEState.ReadWriteOK) {
                    SharedPreferencesUtil.instances.setWatchMac(it.deviceMac)
                    SharedPreferencesUtil.instances.setWatchName(it.deviceName)
                    switchToWatchInfo()
                } else {
                    doConnectDevice(it.deviceMac)
                }
            }
        }

        binding.rvWatch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = watchScanAdapter
        }
    }

    private fun doBondDevice(mac: String) {
        // todo: error control
        val bleAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = bleAdapter.getRemoteDevice(mac)
        if (device != null) {
            when (device.bondState) {
                BluetoothDevice.BOND_NONE -> {
                    Timber.d("$TAG, doBondDevice(), not bonded, $mac")
                    isBonding = true
                    mainViewModel.setBondingWatch(true)
                    device.createBond()
                }
                BluetoothDevice.BOND_BONDED -> {
                    Timber.d("$TAG, doBondDevice(), already bonded, $mac")
                    doConnectDevice(mac)
                }
            }
        }
    }

    private fun doConnectDevice(mac: String) {
        activity?.runOnUiThread {
            binding.progressbar.visibility = View.GONE
            binding.progressbar.visibility = View.VISIBLE
        }
        YCBTClient.connectBle(mac) { code ->
            Timber.d("doConnectDevice(), code=$code")
            activity?.runOnUiThread {
                binding.progressbar.visibility = View.GONE
            }
            if (code == Constants.CODE.Code_OK) {
                Timber.d("doConnectDevice(), connected")
                SharedPreferencesUtil.instances.setWatchMac(watchmac)
                SharedPreferencesUtil.instances.setWatchName(watchname)
                WatchUtils.instance.cancelAllWorker()
                WatchUtils.instance.initOneTimeWorker(3, TimeUnit.SECONDS)
                switchToWatchInfo()
            } else if (code == Constants.CODE.Code_Failed) {
                Timber.d("doConnectDevice(), fail")
                activity?.runOnUiThread {
                    DialogUtils.showMyDialog2(
                        binding.root,
                        0,
                        R.string.errmsg_cannot_connect_watch,
                        mac,
                        R.string.button_ok
                    ) {
                        Timber.d("doConnectDevice(), fail, user clicked ok")
                        startScanDevices()
                    }
                }
            }
        }
    }

    private fun switchToWatchInfo() {
        activity?.runOnUiThread {
            findNavController().navigate(WatchScanFragmentDirections.actionNavWatchScanToNavWatchInfo())
        }
    }

    private fun registerScanReceiver() {
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context?.registerReceiver(scanRceiver, filter)
    }

    private fun unregisterScanReceiver() {
        context?.unregisterReceiver(scanRceiver)
6    }

    private val scanRceiver = object  : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                when (device!!.bondState) {
                    BluetoothDevice.BOND_BONDING -> Timber.d("BleScanReceiver::onReceive, BOND_BONDING, mac=${device.address}")
                    BluetoothDevice.BOND_BONDED -> {
                        if (device.address.equals(mac)) {
                            Timber.d("BleScanReceiver::onReceive, BOND_BONDED, mac=${device.address}")
                            mainViewModel.setBondingWatch(false)
                            doConnectDevice(device.address)
                        }
                    }
                    BluetoothDevice.BOND_NONE -> Timber.d("BleScanReceiver::onReceive, BOND_NONE, mac=${device.address}")
                    else -> {

                    }
                }

            }
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

            //WatchUtils.instance.initWatchSDK()
            startScanDevices()
        }
    }

}