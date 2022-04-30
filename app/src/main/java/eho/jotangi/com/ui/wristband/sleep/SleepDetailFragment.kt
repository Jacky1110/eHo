package eho.jotangi.com.ui.wristband.sleep

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.databinding.FragmentSleepDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.SleepData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.SleepAdapter

class SleepDetailFragment: BaseFragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentSleepDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<SleepData>()
    private val b_dataList = arrayListOf<SleepData>()
    private lateinit var sleepAdapter: SleepAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSleepDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        sleepAdapter = SleepAdapter(dataList)
        binding.rvSleep.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            adapter = sleepAdapter
        }

        binding.searchView.setOnQueryTextListener(this)

        updateSleep()
    }
    private fun updateSleep() {
        mainViewModel.daySleepData.observe(viewLifecycleOwner, { datas ->
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
            sleepAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar",newText)
            if(b_dataList!=null){
                var ndataList = b_dataList.filter { it.endTime!!.startsWith(newText)}
                var sortedData = ndataList.sortedByDescending { it.endTime }
                dataList.clear()
                dataList.addAll(sortedData)
                sleepAdapter.notifyDataSetChanged()
            }
        }
        return true
    }
}