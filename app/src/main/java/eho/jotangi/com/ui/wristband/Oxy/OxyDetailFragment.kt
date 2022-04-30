package eho.jotangi.com.ui.wristband.Oxy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.databinding.FragmentOxyDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.OxyAdapter

class OxyDetailFragment : BaseFragment(), SearchView.OnQueryTextListener{
    private var _binding: FragmentOxyDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<OxygenData>()
    private val b_dataList = arrayListOf<OxygenData>()
    private lateinit var oxygenAdapter: OxyAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOxyDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        oxygenAdapter = OxyAdapter(dataList)
        binding.rvOxy.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = oxygenAdapter
        }

        binding.searchView.setOnQueryTextListener(this)

        updateOxygen()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateOxygen() {
        mainViewModel.last7OxygenDatas.observe(viewLifecycleOwner, { datas ->
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
            oxygenAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar",newText)
            if(b_dataList!=null){
                var ndataList = b_dataList.filter { it.startTime!!.startsWith(newText)}
                var sortedData = ndataList.sortedByDescending { it.startTime }
                dataList.clear()
                dataList.addAll(sortedData)
                oxygenAdapter.notifyDataSetChanged()
            }
        }
        return true
    }
}