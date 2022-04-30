package eho.jotangi.com.ui.wristband.ecg

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.response.BleDataResponse
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentEcgBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.utils.DialogUtils
import eho.jotangi.com.utils.SharedPreferencesUtil
import eho.jotangi.com.utils.smartwatch.WatchApiRepository
import eho.jotangi.com.utils.smartwatch.WatchUtils
import eho.jotangi.com.utils.smartwatch.apirequest.ECGRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EcgFragment :BaseFragment(){
    private var _binding:FragmentEcgBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar
    private var btn_status = true
    private var progressNum = 0
    //private var sendData:LineData? = null
    private var count = 0
    protected val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEcgBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.ECG)
        mainViewModel.getWatchInfo()

        binding.bnECGStart.apply {
            setOnClickListener {
                if(SharedPreferencesUtil.instances.getWatchMac()!=null &&
                        YCBTClient.connectState() == Constants.BLEState.ReadWriteOK) {
                    if (btn_status) {

                        DialogUtils.showMyDialog(rootView as ViewGroup?,R.string.ecg_start_title,R.string.ecg_start,"",R.string.ecg_start_ok
                        ) {
                            binding.tvEcgHrv.text = "- -"
                            binding.tvEcgHeart.text = "- -"
                            binding.tvEcgBlood.text = "- -/- -"
                            binding.tvProgress.text = "0%"
                            binding.ecgGoodTag.visibility = View.INVISIBLE
                            initChart()
                            binding.ecgProgressBar.progress = 0
                            progressNum = 0
                            count = 0
                            setImageDrawable(resources.getDrawable(R.drawable.ic_ecg_stop))
                            btn_status = false
                            mainViewModel.appECGTest(object : EcgDataCallback.Start {
                                override fun receiveECG(value: Float) {
                                    Log.d("appECGTest_ecg", value.toString())
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        updateGraph(value)
                                    }
                                }

                                override fun receiveHRV(value: Int) {
                                    Log.d("appECGTest_hrv", value.toString())
                                    binding.tvEcgHrv.text = value.toString()
                                }

                                override fun receiveBlood(
                                    heartValue: Int,
                                    bloodDBP: Int,
                                    bloodSBP: Int
                                ) {
                                    binding.tvEcgHeart.text = heartValue.toString()
                                    binding.tvEcgBlood.text = "$bloodDBP/$bloodSBP"
                                }

                                override fun receiveBadSignal() {
                                    binding.ecgGoodTag.visibility = View.INVISIBLE
                                }
                            })
                        }

                    } else {
                        setImageDrawable(resources.getDrawable(R.drawable.ic_ecg_play))
                        btn_status = true
                        try {
                            mainViewModel.appECGTestEnd(object : EcgDataCallback.End {
                                override fun receive(value: Float) {
                                    Log.d("appECGTestEnd", value.toString() + "ggg")
                                }

                            })
                        }catch (e:Exception){
                            Log.d("eeeECG_ERROR", e.toString())
                        }
                    }
                }else{
                    Toast.makeText(context,"請先綁定手環",Toast.LENGTH_SHORT).show()
                }
            }
        }

        initChart()
    }

    private fun updateProgress(){
        binding.ecgProgressBar.apply {
            if(progress/150!=100){
                progressNum+=1
                incrementProgressBy(progressNum)
                binding.tvProgress.text = (progress/150).toString()+"%"
            }else{
                uploadECG(binding.tvEcgHeart.text.toString(),
                    binding.tvEcgBlood.text.toString().split("/")[0],
                    binding.tvEcgBlood.text.toString().split("/")[1],
                    binding.tvEcgHrv.text.toString())

                //binding.tvEcgHrv.text = "- -"
                binding.tvProgress.text = "0%"
                //binding.tvEcgHeart.text = "- -"
                //binding.tvEcgBlood.text = "- -/- -"
                binding.ecgGoodTag.visibility = View.INVISIBLE
                binding.bnECGStart.setImageDrawable(resources.getDrawable(R.drawable.ic_ecg_play))
                btn_status = true
                progress = 0
                progressNum = 0
                count = 0
                try {
                    mainViewModel.appECGTestEnd(object : EcgDataCallback.End {
                        override fun receive(value: Float) {
                            Log.d("appECGTestEnd", value.toString())
                        }
                    })
                }catch (e:Exception){
                    Log.d("eeeECG_ERROR", e.toString())
                }

                //findNavController().navigate(EcgFragmentDirections.actionNavEcgToNavEcgDetail(LineDataModel(sendData!!)))
                //initChart()
                //sendData = null
            }
        }
    }

    private fun getECG(){
        CoroutineScope(Dispatchers.IO).launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -1)
            val oneYearAgo = calendar.time
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val start3str = sdf.format(oneYearAgo)
            val accountid = SharedPreferencesUtil.instances.getMemberCode()
            val endTime = Date()
            val endstr = sdf.format(endTime)
            if(accountid!=null)
                mainViewModel.getECG(start3str,endstr,accountid)
        }
    }

    private fun uploadECG(hr:String,dbp:String,sbp:String,hrv:String){
        Log.d("eeeECG","$hr $dbp $sbp $hrv")
        if(hrv!="- -" && hrv!="0" && hr!="- -" && hr!="0" && dbp!="- -" && dbp!="0" && sbp!="- -" && sbp!="0") {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            CoroutineScope(Dispatchers.IO).launch {
                val date = sdf.format(Date())
                val resp = apiRepository.ecgUpload(ECGRequest(
                    SharedPreferencesUtil.instances.getMemberCode(),
                    date,
                    "123,456",
                    hr.toInt(),
                    dbp.toInt(),
                    sbp.toInt(),
                    hrv.toInt()
                ))
                if(resp.code == "0x0200"){
                    Log.d("eeeECG","ecg upload success")
                    getECG()
                }else{
                    Log.d("eeeECG","ecg upload failed ${resp.responseMessage}")
                }
            }
        }
    }

    private fun updateGraph(value: Float) {
        try {
            binding.ecgGoodTag.visibility = View.VISIBLE
            val data: LineData = binding.ecgChart.data
            val set: LineDataSet? = data.getDataSetByIndex(0) as LineDataSet?
            //val set2: LineDataSet? = data.getDataSetByIndex(0) as LineDataSet?
            if (set != null) {
                data.addEntry(
                    Entry(
                        set.entryCount.toFloat(),
                        value
                    ), 0
                )
                /*sendData?.addEntry(
                    Entry(
                        set2!!.entryCount.toFloat(),
                        value
                    ), 0
                )*/
                if (set.entryCount >= 1000) {
                    set.removeFirst()
                    for (i in 0 until set.entryCount) {
                        val entryToChange = set.getEntryForIndex(i)
                        entryToChange.x = entryToChange.x - 1
                    }
                }
                binding.ecgChart.notifyDataSetChanged()
                binding.ecgChart.invalidate()
                if(count>70) {
                    updateProgress()
                    count = 0
                }
                else
                    count++
            }
        }catch (e:Exception){
            Log.d("eee",e.toString())
        }
    }

    private fun initChart(){
        try {
            var value_entries = mutableListOf<Entry>()
            value_entries.add(Entry(0f, 0f))


            var value_dataset = LineDataSet(value_entries, null)
            value_dataset.apply {
                setColor(Color.rgb(250, 106, 131))
                setValueTextColor(Color.BLACK)
                setDrawCircles(false)
                setDrawValues(false)
            }
            binding.ecgChart.apply {
                isDragDecelerationEnabled = false
                setTouchEnabled(false)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawLabels(false)
                    axisMinimum = 0f
                    axisMaximum = 1000f
                    setLabelCount(500)
                   // granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toString()
                        }
                    }
                }
                axisRight.isEnabled = false
                axisLeft.apply {
                    setDrawLabels(false)
                    //granularity = 1f
                    axisMinimum = -55000f
                    axisMaximum = 55000f
                    setLabelCount(500)
                }
                setBackgroundColor(Color.WHITE)
                legend.isEnabled = false
                description.isEnabled = false
                data = LineData(value_dataset)
                //if(sendData==null)
                    //sendData = data
                invalidate()
            }

        }catch (e:Exception){
            Log.d("eee",e.toString())
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            mainViewModel.appECGTestEnd(object : EcgDataCallback.End {
                override fun receive(value: Float) {
                    Log.d("appECGTestEnd", value.toString() + "end work")
                }
            })
        }catch (e:Exception){
            Log.d("eeeECG_ERROR", e.toString())
        }
        WatchUtils.instance.initOneTimeWorker(3, TimeUnit.SECONDS)
    }

    override fun onResume() {
        super.onResume()
        Log.d("appECGTestEnd","onresume")
        WatchUtils.instance.cancelAllWorker()
    }
}