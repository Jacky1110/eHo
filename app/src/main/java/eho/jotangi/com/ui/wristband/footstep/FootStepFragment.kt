package eho.jotangi.com.ui.wristband.footstep

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.databinding.FragmentFootstepBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.view.ColorBarChartView
import eho.jotangi.com.utils.smartwatch.WatchUtils
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FootStepFragment :BaseFragment(){
    private var _binding: FragmentFootstepBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFootstepBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.FOOTDETAIL)

        binding.cbcvSTEP.setType(ColorBarChartView.Type.STEP)

        updateFootStepData()
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

                last.sportStartTime?.let { sportstarttime ->
                    last.sportEndTime?.let { sportendtime ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val t1 = sdf.parse(sportstarttime)
                        val t2 = sdf.parse(sportendtime)
                        val diff = t2.time - t1.time
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
                        var minutes = seconds / 60f
                        binding.tvSportTime.text =
                            String.format(Locale.getDefault(), "%.1f", minutes)
                    }
                    binding.tvSportStepTime.text = WatchUtils.instance.clipTimeFormatSecond(sportstarttime)
                }
            }
        })
        mainViewModel.weekTotalStepsData.observe(viewLifecycleOwner,{ //show 整週
            it?.let {  data ->
                try {
                    val sdf = SimpleDateFormat("MM/dd")
                    val currentDate = sdf.format(Date())
                    val c = Calendar.getInstance()
                    c.time = Date()
                    val index_day = c.get(Calendar.DAY_OF_WEEK)
                    Log.d("eeewww", currentDate)
                    Log.d("eeewww", index_day.toString())
                    var result = arrayOfNulls<Int>(7)
                    var result2 = arrayOfNulls<String>(7)
                    c.firstDayOfWeek = Calendar.MONDAY
                    c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

                    for (i in 0 until 7) {
                        result2[i] = sdf.format(c.time)
                        Log.d("eeewww", result2[i].toString())
                        c.add(Calendar.DATE, 1)
                    }

                    for (d in data) {
                        Log.d("eeewww", d.sportEndTime.toString())
                        Log.d("eeewww", d.sportStep.toString())
                        var process = d.sportEndTime!!
                        var sd = process.split("-")
                        var date = sd[1] + "/" + sd[2]
                        for (i in 0 until result2.size) {
                            if (result2[i] == date) {
                                result[i] = d.sportStep!!.toInt()
                            }
                        }
                        for (i in 0 until result.size) {
                            if (result[i] == null) {
                                result[i] = 0
                            }
                        }
                    }
                    binding.cbcvSTEP.apply {
                        setDateLabel(result2.toMutableList())
                        setDataValue(result.toMutableList())
                    }
                }catch (e:Exception){

                }
            }
        })

        /*mainViewModel.weekTotalStepsData.observe(viewLifecycleOwner,{ //有多少show多少
            it?.let {  data ->
                var result = mutableListOf<Int>()
                var result2 = mutableListOf<String>()
                for(d in data){
                    Log.d("eeewww",d.sportEndTime.toString())
                    Log.d("eeewww",d.sportStep.toString())
                    result.add(d.sportStep!!.toInt())
                    var process = d.sportEndTime!!
                    var sd = process.split("-")
                    result2.add(sd[1]+"/"+sd[2])
                }
                binding.cbcvSTEP.apply {
                    setDateLabel(result2)
                    setDataValue(result)
                }

            }
        })*/
    }


}