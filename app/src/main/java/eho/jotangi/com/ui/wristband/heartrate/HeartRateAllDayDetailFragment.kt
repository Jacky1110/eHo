package eho.jotangi.com.ui.wristband.heartrate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.databinding.FragmentHeartRateAllDayDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.HeartRateData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.HeartRateAdapter

class HeartRateAllDayDetailFragment:BaseFragment() {
    private var _binding: FragmentHeartRateAllDayDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<HeartRateData>()
    private lateinit var hrAdapter: HeartRateAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeartRateAllDayDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        hrAdapter = HeartRateAdapter(dataList)
        binding.rvHeartRate.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hrAdapter
        }

        updateHeartRate()
    }

    private fun updateHeartRate() {
        mainViewModel.last7HeartRateData.observe(viewLifecycleOwner, { datas ->
            dataList.clear()
            dataList.addAll(datas)
            hrAdapter.notifyDataSetChanged()
        })
    }
}