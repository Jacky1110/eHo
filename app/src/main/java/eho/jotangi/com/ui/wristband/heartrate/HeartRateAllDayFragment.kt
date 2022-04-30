package eho.jotangi.com.ui.wristband.heartrate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.databinding.FragmentHeartRateAllDayBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment

class HeartRateAllDayFragment:BaseFragment() {
   private var _binding:FragmentHeartRateAllDayBinding?=null
   private val binding get() = _binding!!
   override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeartRateAllDayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.HEARTRATE_ALLDAY)
        binding.tvHeartRate1.background.level = 4000
    }
}