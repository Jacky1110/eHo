package eho.jotangi.com.ui.wristband.Oxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import eho.jotangi.com.databinding.FragmentOxyBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.view.ColorBarChartView
import eho.jotangi.com.ui.wristband.view.HorizontalColorBar
import timber.log.Timber
import com.yucheng.ycbtsdk.response.BleDataResponse
import com.yucheng.ycbtsdk.response.BleRealDataResponse
import eho.jotangi.com.utils.smartwatch.WatchUtils
import eho.jotangi.com.utils.smartwatch.watchdatamodel.HistoryHealthInfo
import eho.jotangi.com.utils.smartwatch.watchdatamodel.YCBTDataHelper
import eho.jotangi.com.utils.smartwatch.watchdatamodel.YCBTDataResponse
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.schedule


class OxyFragment : BaseFragment(){
    private var _binding: FragmentOxyBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOxyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.OXYDETAIL)

        binding.hcbvOxygen.setType(HorizontalColorBar.Type.OXYGEN)
        binding.cbcvOxygen.setType(ColorBarChartView.Type.OXYGEN)

        binding.bnOxygenStart.setOnClickListener {
            doStartGetRealOxygen()
            //getHealthHistoryData()
        }

        updateOxygenData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateOxygenData() {
        mainViewModel.lastOxygenData.observe(viewLifecycleOwner,{ data ->
            data.OOValue?.let { OOValue ->
                binding.tvOxygenValue.text = OOValue
                binding.hcbvOxygen.setDataValue(OOValue.toInt())
            }
            data.startTime?.let { starttime ->
                binding.tvOxygenTime.text = WatchUtils.instance.clipTimeFormatSecond(starttime)
            }
        })

        mainViewModel.last7OxygenValue.observe(viewLifecycleOwner,{
            binding.cbcvOxygen.setDataValue(it.take(7))
        })
    }

    private fun doStartGetRealOxygen() {
    /*
    YCBTClient.appEcgTestStart({ code, ratio, hashMap ->
        Timber.d("$TAG, doStartGetRealOxygen() data respond, code=$code")
        if (code == Constants.CODE.Code_OK) {
            if (hashMap != null) {
                Timber.d("$TAG, doStartGetRealOxygen() data resp, hashmap=$hashMap")
            }
        }}
    ) { dataType, dataMap ->
        Timber.d("$TAG, doStartGetRealOxygen() real resp, code=$dataType")
        if (dataMap != null) {
            Timber.d("$TAG, doStartGetRealOxygen()real resp, dataMap=$dataMap")
        }
        if (dataType == Constants.DATATYPE.Real_UploadHeart) {
            //心率数据 dataMap
        } else if (dataType == Constants.DATATYPE.Real_UploadBlood) {
            //血压数据 dataMap
        } else if (dataType == Constants.DATATYPE.Real_UploadPPG) {
            //实时PPG数据  dataMap
            val ppgBytes = dataMap["data"] as ByteArray
        } else if (dataType == Constants.DATATYPE.Real_UploadECG) {

        } else if (dataType == Constants.DATATYPE.Real_UploadECGHrv) {
        } else if (dataType == Constants.DATATYPE.Real_UploadECGRR) {
        }
    }

    Timer().schedule(15000) {
        YCBTClient.appEcgTestEnd { code, ratio, hashMap ->
            Timber.d("$TAG, appEcgTestEnd(), code=$code")
            if (code == Constants.CODE.Code_OK) {
                if (hashMap != null) {
                    Timber.d("$TAG, appEcgTestEnd(), hashmap=$hashMap")
                }
            }
        }
    }


    YCBTClient.getAllRealDataFromDevice { code, ratio, hashMap ->
        Timber.d("$TAG, doStartGetRealOxygen(), code=$code")
        if (code == Constants.CODE.Code_OK) {
            if (hashMap != null) {
                Timber.d("$TAG, doStartGetRealOxygen(), hashmap=$hashMap")


     */
    YCBTClient.getRealBloodOxygen { code, ratio, hashMap ->
        Timber.d("$TAG, doStartGetRealOxygen(), code=$code")
        if (code == Constants.CODE.Code_OK) {
            if (hashMap != null) {
                Timber.d("$TAG, doStartGetRealOxygen(), hashmap=$hashMap")
            }
        }
    }
}

private fun getHealthHistoryData() : List<HistoryHealthInfo>? {
Timber.d("$TAG, getHealthHistoryData")
var datalist: List<HistoryHealthInfo>? = null;

YCBTClient.healthHistoryData(
    Constants.DATATYPE.Health_HistoryAll
) { code, status, hashMap ->

    if (code == Constants.CODE.Code_OK) {
        Timber.d("$TAG, getHealthHistoryData succss")
        if (hashMap != null) {
            Timber.d("$TAG, getHealthHistoryData, data=$hashMap")
            val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
            if (response != null) {
                datalist = YCBTDataHelper.historyHealthInfosListFromObject(response.data)
                if (datalist != null) {
                }
            }
        } else {
            Timber.d("$TAG, getHealthHistoryData succss, nodata")
        }
    } else {
        Timber.d("$TAG, getHealthHistoryData, fail, code=$code")
    }
}

return datalist
}

}