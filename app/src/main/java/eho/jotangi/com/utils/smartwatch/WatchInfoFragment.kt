package eho.jotangi.com.utils.smartwatch

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.response.BleDataResponse
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentWatchInfoBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.MemberFragmentDirections
import eho.jotangi.com.utils.DialogUtils
import eho.jotangi.com.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class WatchInfoFragment  : BaseFragment() {
    private var _binding: FragmentWatchInfoBinding? = null
    private val binding get() = _binding!!

    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBack(R.string.watchsetting, isShowLogo = true)
//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().navigate(R.id.nav_member)
//            }
//        })

        mainViewModel.watchBatteryLevel.observe(viewLifecycleOwner, { level ->
            binding.tvBatteryLevel.text = String.format(Locale.getDefault(), "%d", level)
            binding.ivBatteryLevel.power = level
        })

        binding.tvUnbindWatch.setOnClickListener {
            //YCBTClient.disconnectBle()
            doRemoveDevice()
            GlobalScope.launch(Dispatchers.Main) {
                findNavController().navigate(WatchInfoFragmentDirections.actionNavWatchInfoToNavWatchScan())
            }
        }

        binding.tvTimeFormatContent.apply {
            text = if (SharedPreferencesUtil.instances.getWatchTimeFormat() == "24")
                getString(R.string.time_format_24_setting)
            else
                getString(R.string.time_format_12_setting)
        }

        binding.tvNotification.setOnClickListener {
            gotoNotificationAccessSetting()
        }

        binding.tvNotificationSetting.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                findNavController().navigate(WatchInfoFragmentDirections.actionNavWatchInfoToNavNotificationSetting())
            }
        }

        binding.tvTimeFormat.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setItems(arrayOf(getString(R.string.time_format_24_setting), getString(R.string.time_format_12_setting))) { _, pos ->
                    YCBTClient.settingUnit(0,0,0,pos){ i, fl, hashMap ->
                        if(i==0){
                            CoroutineScope(Dispatchers.Main).launch {
                                when (pos) {
                                    0 -> {
                                        SharedPreferencesUtil.instances.setWatchTimeFormat("24")
                                        binding.tvTimeFormatContent.text = getString(R.string.time_format_24_setting)
                                    }else -> {
                                        SharedPreferencesUtil.instances.setWatchTimeFormat("12")
                                        binding.tvTimeFormatContent.text = getString(R.string.time_format_12_setting)
                                    }
                                }
                            }
                        }
                    }
                }.show()
        }

        binding.tvReboot.setOnClickListener {
            DialogUtils.showMyDialog2(it.rootView as ViewGroup,R.string.watch_reboot,R.string.watch_reboot_sure,"",R.string.confirm
            ) {
                YCBTClient.appShutDown(0x03) { i, fl, hashMap ->

                }
            }
        }

    }

    private fun doRemoveDevice() {
        WatchUtils.instance.cancelAllWorker()
        YCBTClient.disconnectBle()
        //val mac = SharedPreferencesUtil.instances.getWatchMac()
        //val bleadapter = BluetoothAdapter.getDefaultAdapter()
        SharedPreferencesUtil.instances.setWatchMac(null)
        SharedPreferencesUtil.instances.setWatchName(null)
    }

    override fun onResume() {
        super.onResume()
        val mac = SharedPreferencesUtil.instances.getWatchMac()
        binding.tvWatchMac.text = mac

        val name = SharedPreferencesUtil.instances.getWatchName()
        binding.tvWatchName.text = name

        doGetDeviceInfo()
        if(notificationListenerEnable()){
            binding.tvNotificationContent.text = "已授權"
        }else{
            binding.tvNotificationContent.text = "未授權"
        }
    }

    private fun notificationListenerEnable(): Boolean {
        var enable = false
        val packageName: String = requireContext().packageName
        val flat: String = Settings.Secure.getString(requireContext().contentResolver, "enabled_notification_listeners")
        if (flat != null) {
            enable = flat.contains(packageName)
        }
        return enable
    }

    private fun gotoNotificationAccessSetting() {
        try {
            var intent =  Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            requireContext().startActivity(intent)
        } catch(e:Exception) {

        }
    }

    private fun doGetDeviceInfo() {
        mainViewModel.getWatchInfo()
    }
}