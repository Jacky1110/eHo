package eho.jotangi.com.ui.wristband.sleep

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.databinding.FragmentSleepBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.DaySleepData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.view.HorizontalColorBar
import eho.jotangi.com.ui.wristband.view.ColorBarChartView
import eho.jotangi.com.ui.wristband.view.HeartRateColorBar
import eho.jotangi.com.utils.smartwatch.WatchUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class SleepFragment :BaseFragment(){
    private var _binding: FragmentSleepBinding?= null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSleepBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.SLEEP)
        setupUI()
        updateSleepData()
    }

    private fun setupUI(){
        binding.hcbvSleep.setType(HorizontalColorBar.Type.SLEEP)
        //binding.sleepBar3.setType(ColorBarChartView.Type.SLEEP)
    }

    private fun updateSleepData() {
        mainViewModel.lastSleepEndTime.observe(viewLifecycleOwner, {data ->
            binding.tvSleepTime.text = WatchUtils.instance.clipTimeFormatSecond(data)
        })
        mainViewModel.totalSleepHour.observe(viewLifecycleOwner, {data ->
            if(data == 0)
                binding.tvSleepHour.text = "-"
            else
                binding.tvSleepHour.text = data.toString()
        })
        mainViewModel.totalSleepMinute.observe(viewLifecycleOwner, {data ->
            if(data == 0)
                binding.tvSleepMinute.text = "-"
            else
                binding.tvSleepMinute.text = data.toString()
        })
        mainViewModel.daySleepDetailData.observe(viewLifecycleOwner, { list ->
            val start = mainViewModel.lastSleepStartTime.value
            val stop = mainViewModel.lastSleepEndTime.value
            binding.shcbvSleep.setDatalist(list, start, stop)
        })
        mainViewModel.lastSleepQuality.observe(viewLifecycleOwner, {data ->
            if(data == 0)
                binding.tvSleepQuality.text = "-"
            else
                binding.tvSleepQuality.text = data.toString()
        })

        mainViewModel.weekdaySleepData.observe(viewLifecycleOwner,{
            it?.let { datas ->
                try {
                    var sortedData = datas.sortedBy { it.date }
                    Log.d("weekdaysleepdata", sortedData.toString())
                    var totalD = mutableListOf<DaySleepData>()
                    var deepD = mutableListOf<DaySleepData>()
                    for (d in datas) {
                        if (d.type == 0)
                            totalD.add(d)
                        else
                            deepD.add(d)
                    }
                    var tA = IntArray(7)
                    var dA = IntArray(7)
                    val sdf = SimpleDateFormat("MM/dd")
                    var dateLabel = arrayOfNulls<String?>(tA.size)
                    val c = Calendar.getInstance()
                    c.time = Date()
                    c.firstDayOfWeek = Calendar.MONDAY
                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                    for(i in 0 until 7){
                        dateLabel[i] = sdf.format(c.time)
                        Log.d("weekdaysleepdata",dateLabel[i].toString())
                        c.add(Calendar.DATE,1)
                    }

                    for(i in 0 until dateLabel.size){
                        var flag = false
                        for(t in totalD){
                            var date = t.date!!.split("-")
                            var rdate = date[1]+"/"+date[2]
                            if(rdate == dateLabel[i]){
                                tA[i] = t.totalHour
                                flag = true
                            }
                        }
                        if(!flag)
                            tA[i] = 0
                    }

                    for(i in 0 until dateLabel.size){
                        var flag = false
                        for(d in deepD){
                            var date = d.date!!.split("-")
                            var rdate = date[1]+"/"+date[2]
                            if(rdate == dateLabel[i]){
                                dA[i] = d.totalHour
                                flag = true
                            }
                        }
                        if(!flag)
                            dA[i] = 0
                    }

                    for(i in 0 until 7){
                        Log.d("weekdaysleepdata_tA","index:"+i.toString())
                        Log.d("weekdaysleepdata_tA",tA[i].toString())
                    }
                    for(i in 0 until 7){
                        Log.d("weekdaysleepdata_dA","index:"+i.toString())
                        Log.d("weekdaysleepdata_dA",dA[i].toString())
                    }

                    binding.sleepBar3.setDateLabel(dateLabel)
                    binding.sleepBar3.setDataValue(tA, dA)
                }catch (e:Exception){
                    Log.d("weekdaysleepdata", e.toString())
                }
            }
        })


        /*mainViewModel.weekdaySleepData.observe(viewLifecycleOwner,{
            it?.let { datas ->
                try {
                    var sortedData = datas.sortedBy { it.date }
                    Log.d("weekdaysleepdata", sortedData.toString())
                    var totalD = mutableListOf<DaySleepData>()
                    var deepD = mutableListOf<DaySleepData>()
                    for (d in datas) {
                        if (d.type == 0)
                            totalD.add(d)
                        else
                            deepD.add(d)
                    }
                    var tA = IntArray(totalD.size)
                    var dA = IntArray(totalD.size)
                    var dateLabel = arrayOfNulls<String?>(tA.size)
                    for (i in tA.indices) {
                        tA[i] = totalD[i].totalHour
                        var date = totalD[i].date!!.split("-")
                        var result_date = date[1] + "/" + date[2]
                        dateLabel[i] = result_date
                    }

                    for (i in tA.indices) {
                        var date = totalD[i].date
                        var flag = false
                        for (j in deepD.indices) {
                            if (date == deepD[j].date) {
                                flag = true
                                dA[i] = deepD[j].totalHour
                                break
                            }
                        }
                        if (!flag) {
                            dA[i] = 0
                        }
                    }

                    binding.sleepBar3.setDateLabel(dateLabel)
                    binding.sleepBar3.setDataValue(tA, dA)
                }catch (e:Exception){
                    Log.d("weekdaysleepdata", e.toString())
                }
            }
        })*/

    }

}