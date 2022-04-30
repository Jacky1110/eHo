package eho.jotangi.com.ui.wristband.ecg

import android.R.attr
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import eho.jotangi.com.databinding.FragmentEcgDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import android.R.attr.data
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.network.response.EcgData


class EcgDetailFragment :BaseFragment(), SearchView.OnQueryTextListener{
    //private val args:EcgDetailFragmentArgs by navArgs()
    private var _binding:FragmentEcgDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<EcgData>()
    private val b_dataList = arrayListOf<EcgData>()
    private lateinit var ecgAdapter: EcgAdapter
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEcgDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        ecgAdapter = EcgAdapter(dataList)
        binding.rvEcg.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            adapter = ecgAdapter
        }

        mainViewModel.last7EcgData.observe(viewLifecycleOwner,{ datas ->
            dataList.clear()
            b_dataList.clear()
            dataList.addAll(datas)
            b_dataList.addAll(datas)
            binding.pageNodata.pageEmpty.apply {
                visibility = if(dataList.size == 0)
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
            ecgAdapter.notifyDataSetChanged()
        })

        binding.searchView.setOnQueryTextListener(this)

        //setupToolbarDocument(GoType.ECG)

        /*binding.ecgChart.apply {
            setTouchEnabled(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toString()
                    }
                }
            }
            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawLabels(false)
                granularity = 1f
            }
            setBackgroundColor(Color.WHITE)
            legend.isEnabled = false
            description.isEnabled = false
            data = args.data.data
            scaleY = 0.25f
            invalidate()
        }*/
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar",newText)
            if(b_dataList!=null){
                var ndataList = b_dataList.filter { it.ecgStartTime!!.startsWith(newText)}
                var sortedData = ndataList.sortedByDescending { it.ecgStartTime }
                dataList.clear()
                dataList.addAll(sortedData)
                ecgAdapter.notifyDataSetChanged()
            }
        }
        return true
    }
}