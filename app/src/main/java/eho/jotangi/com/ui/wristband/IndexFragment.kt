package eho.jotangi.com.ui.wristband

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.databinding.FragmentWristbandIndexBinding
import eho.jotangi.com.ui.wristband.view.HorizontalColorBar
import eho.jotangi.com.utils.SharedPreferencesUtil
import eho.jotangi.com.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class IndexFragment : BaseFragment() {
    private var _binding: FragmentWristbandIndexBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWristbandIndexBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        binding.hcbvDBP.setType(HorizontalColorBar.Type.DBP)
        binding.hcbvSBP.setType(HorizontalColorBar.Type.SBP)
        binding.hcbvOxygen.setType(HorizontalColorBar.Type.OXYGEN)
        binding.hcbvECG.setType(HorizontalColorBar.Type.ECG)
        binding.hcbvECG.setDataValue(0)

        binding.cvFootstep.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavFootstep())
        }
        binding.cvSleep.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavSleep())
        }
        binding.cvHeartRate.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavHeartRate())
            //findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavHeartRateAllDay())
        }
        binding.cvBp.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavBp())
        }
        binding.cvOxy.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavOxy())
        }
        binding.cvECG.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavEcg())
        }

        //drawChart()

        binding.btnSport.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavSport())
        }
        binding.laWalk.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavWalk())
        }
        binding.laRun.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavRun())
        }
        binding.layBike.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavBike())
        }
        binding.laJump.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavJump())
        }
        binding.laBadminton.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavBadminton())
        }
        binding.laBasketball.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavBasketball())
        }
        binding.laSoccer.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavSoccer())
        }
        binding.laSwim.setOnClickListener {
            findNavController().navigate(IndexFragmentDirections.actionNavWristbandToNavSwim())
        }
        updateFootStepData()
        updateHeartRateData()
        updateBPData()
        updateOxygenData()
        updateSleepData()
        updateEcgData()
    }

    /*fun drawChart(){
        var value_entries = mutableListOf<Entry>()
        value_entries.add(Entry(0f,0f))
//        value_entries.add(Entry(1f,10f))
//        value_entries.add(Entry(2f,100f))
//        value_entries.add(Entry(3f,30f))
//        value_entries.add(Entry(4f,50f))
//        value_entries.add(Entry(5f,10f))

        var value_dataset = LineDataSet(value_entries,null)
        value_dataset.apply {
            setColor(Color.rgb(250,106,131))
            setValueTextColor(Color.BLACK)
            setDrawCircles(false)
            setDrawValues(false)
        }
        binding.ecgChart.apply {
            setTouchEnabled(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                setDrawAxisLine(true)
                setDrawGridLines(true)

//                axisMinimum = 0f
//                axisMaximum = 100f
                setLabelCount(40)
                //granularity = 0.5f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toString()
                    }
                }
            }
            axisRight.apply {
                isEnabled = false
                setDrawAxisLine(true)
                setDrawGridLines(true)
            }

            axisLeft.apply {
                setDrawLabels(false)
                setDrawGridLines(true)

//                axisMinimum = 0f
//                axisMaximum = 100f
                setLabelCount(8)
                //granularity = 0.5f
            }
            setBackgroundColor(Color.WHITE)
            legend.isEnabled = false
            description.isEnabled = false
            data = LineData(value_dataset)
            invalidate()
        }
    }*/

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            getWristBandData()
        }
    }

    private suspend fun getWristBandData(){
        val endTime = Date()
        val calendar = Calendar.getInstance()
        calendar.setTime(endTime)
        calendar.add(Calendar.DATE, -1)
        val startTime = calendar.time
        calendar.add(Calendar.DATE, -6)
        val oneWeekAgo = calendar.time
        calendar.add(Calendar.MONTH, -3)
        val threeMonthAgo = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startstr = sdf.format(startTime)
        val endstr = sdf.format(endTime)
        val start2str = sdf.format(oneWeekAgo)
        val start3str = sdf.format(threeMonthAgo)
        val accountid = SharedPreferencesUtil.instances.getMemberCode()
        Timber.d("$TAG, getWristBandData(), accountid=$accountid startstr=$startstr start2str=$start2str endstr=$endstr")
        if (accountid != null) {
            mainViewModel.getFootSteps(startstr, endstr, accountid)
            mainViewModel.getFootSteps2(start3str, endstr, accountid)
            mainViewModel.getHeartRate(start3str, endstr, accountid)
            mainViewModel.getBP(start3str, endstr, accountid)
            mainViewModel.getOxygen(start3str, endstr, accountid)
            mainViewModel.getSleep(start3str, endstr, accountid)
            mainViewModel.getSleepDetail(startstr, endstr, accountid)
            mainViewModel.getSleepDetail2(start2str, endstr, accountid)
            mainViewModel.getECG(start3str, endstr, accountid)
        }
    }



    private fun updateFootStepData() {
        mainViewModel.dayTotalSteps.observe(viewLifecycleOwner,{
            it?.let { data ->
                binding.tvStepTotal.text = data.toString()
            }
        })
        mainViewModel.dayTotalCalories.observe(viewLifecycleOwner,{
            it?.let { data ->
                binding.tvSportCalorie.text = data.toString()
            }
        })
        mainViewModel.dayTotalMeters.observe(viewLifecycleOwner,{
            it?.let { data ->
                binding.tvWalkDistance.text = data.toString()
            }
        })
        mainViewModel.lastFootStepsData.observe(viewLifecycleOwner,{
            it?.let { last ->
                Timber.d("$TAG, footsteps -> $last")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                last.sportStartTime?.let { sportstarttime ->
                    val t1 = sdf.parse(sportstarttime)
                    last.sportEndTime?.let { sportendtime ->
                        val t2 = sdf.parse(sportendtime)
                        val diff = t2.time - t1.time
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
                        var minutes = seconds / 60f
                        binding.tvSportTime.text =
                            String.format(Locale.getDefault(), "%.1f", minutes)
                    }
                    t1?.let {
                        binding.tvSportStepTime.text = WatchUtils.instance.clipTimeFormatSecond(sportstarttime)
                    }
                }
            }
        })
    }

    private fun updateEcgData(){
        mainViewModel.last7EcgData.observe(viewLifecycleOwner,{
            it?.let {  last ->
                if(last.isNotEmpty()) {
                    binding.tvECGTime.text = WatchUtils.instance.clipTimeFormatSecond(it[0].ecgStartTime)
                    binding.tvECGValue.text = it[0].hrv.toString()
                    binding.hcbvECG.setDataValue(it[0].hrv)
                }
            }
        })
    }

    private fun updateHeartRateData() {
        mainViewModel.lastHeartRateData.observe(viewLifecycleOwner,{
            it?.let { last ->
                Timber.d("$TAG, heartrate -> $last")
                last.heartValue?.let { hrv ->
                    hrv.toIntOrNull()?.let { it1 ->
                        binding.tvHeartRateValue.text = hrv
                        binding.cbvHeartRate.setDataValue(it1) }
                }
                last.heartStartTime?.let { starttime ->
                    binding.tvHeartRateTime.text = WatchUtils.instance.clipTimeFormatSecond(starttime)
                }
            }
        })
    }

    private fun updateBPData() {
        mainViewModel.lastBPData.observe(viewLifecycleOwner,{
            it?.let { last ->
                Timber.d("$TAG, BP -> $last")
                if (last.bloodDBP != null && last.bloodSBP != null) {
                    binding.tvBPValue.text = String.format("%s/%s", last.bloodDBP, last.bloodSBP)
                    binding.hcbvDBP.setDataValue(last.bloodDBP!!.toInt())
                    binding.hcbvSBP.setDataValue(last.bloodSBP!!.toInt())
                }
                binding.tvBPTime.text = WatchUtils.instance.clipTimeFormatSecond(last.bloodStartTime)
            }
        })
    }

    private fun updateOxygenData() {
        mainViewModel.lastOxygenData.observe(viewLifecycleOwner,{ data ->
            data.OOValue?.let { OOValue ->
                binding.tvOxygenValue.text = OOValue
                binding.hcbvOxygen.setDataValue(OOValue.toInt())
            }
            binding.tvOxygenTime.text = WatchUtils.instance.clipTimeFormatSecond(data.startTime)
        })
        mainViewModel.rangeOxygen.observe(viewLifecycleOwner, { oostr ->
            binding.tvOxygenRange.text = oostr
        })

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
    }

}