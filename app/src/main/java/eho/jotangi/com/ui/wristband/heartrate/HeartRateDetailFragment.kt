package eho.jotangi.com.ui.wristband.heartrate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.databinding.FragmentHeartRateDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.HeartRateData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.HeartRateAdapter
import eho.jotangi.com.ui.wristband.OxyAdapter

class HeartRateDetailFragment :BaseFragment(), SearchView.OnQueryTextListener{
    private var _binding: FragmentHeartRateDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<HeartRateData>()
    private val b_dataList = arrayListOf<HeartRateData>()
    private lateinit var hrAdapter: HeartRateAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeartRateDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        hrAdapter = HeartRateAdapter(dataList)
        binding.rvHeartRate.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = hrAdapter
        }

        binding.searchView.setOnQueryTextListener(this)

        updateHeartRate()
    }

    private fun updateHeartRate() {
        mainViewModel.last7HeartRateData.observe(viewLifecycleOwner, { datas ->
            dataList.clear()
            b_dataList.clear()
            dataList.addAll(datas)
            b_dataList.addAll(datas)
            binding.pageNodata.pageEmpty.apply {
                visibility = if(dataList.size==0)
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
            hrAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar",newText)
            if(b_dataList!=null){
                var ndataList = b_dataList.filter { it.heartStartTime!!.startsWith(newText)}
                var sortedData = ndataList.sortedByDescending { it.heartStartTime }
                dataList.clear()
                dataList.addAll(sortedData)
                hrAdapter.notifyDataSetChanged()
            }
        }
        return true
    }

}