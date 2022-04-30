package eho.jotangi.com.ui.wristband.bp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.databinding.FragmentBloodpressureDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.BPData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.BpAdapter
import eho.jotangi.com.ui.wristband.OxyAdapter

class BpDetailFragment : BaseFragment(), SearchView.OnQueryTextListener{
    private var _binding: FragmentBloodpressureDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<BPData>()
    private val b_dataList = arrayListOf<BPData>()
    private lateinit var bpAdapter: BpAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBloodpressureDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        bpAdapter = BpAdapter(dataList)

        binding.rvBloodpressure.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = bpAdapter
        }
        binding.searchView.setOnQueryTextListener(this)

        updateBP()
    }

    private fun updateBP() {
        mainViewModel.last7BPData.observe(viewLifecycleOwner, { datas ->
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
            bpAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar",newText)
            if(b_dataList!=null){
                var ndataList = b_dataList.filter { it.bloodStartTime!!.startsWith(newText)}
                var sortedData = ndataList.sortedByDescending { it.bloodStartTime }
                dataList.clear()
                dataList.addAll(sortedData)
                bpAdapter.notifyDataSetChanged()
            }
        }
        return true
    }

}