package eho.jotangi.com.ui.wristband.heartrate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.databinding.FragmentHeartRateBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.view.ColorBarChartView
import eho.jotangi.com.utils.smartwatch.WatchUtils
import timber.log.Timber

class HeartRateFragment :BaseFragment(){
    private var _binding:FragmentHeartRateBinding?= null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentHeartRateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.HEARTRATE)

        binding.cbcvHR.setType(ColorBarChartView.Type.HR)

        updateHeartRateData()
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
        mainViewModel.last7HeartRateValue.observe(viewLifecycleOwner, {
            binding.cbcvHR.setDataValue(it.take(7))
        })

    }

}