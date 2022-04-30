package eho.jotangi.com.ui.wristband.footstep

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yucheng.ycbtsdk.YCBTClient
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentFootstepDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.FootStepsData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.FootStepAdapter
import eho.jotangi.com.ui.wristband.OxyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FootStepDetailFragment :BaseFragment(){
    private var _binding: FragmentFootstepDetailBinding?= null
    private val binding get() = _binding!!
    private val dataList = arrayListOf<FootStepsData>()
    private val b_dataList = arrayListOf<FootStepsData>()
    private lateinit var stepAdapter: FootStepAdapter

    override fun getToolBar(): ToolbarBinding?= binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFootstepDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

        stepAdapter = FootStepAdapter(dataList)
        binding.rvFootstep.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            adapter = stepAdapter
        }

        binding.editTextDateStart.setOnClickListener {
            pickerDialog(0)
        }
        binding.editTextDateEnd.setOnClickListener {
            pickerDialog(1)
        }
        binding.btnSearch.setOnClickListener {
            if(b_dataList.size!=0){
                dataList.clear()
                dataList.addAll(
                    b_dataList.filter {
                        it.sportEndTime!! >= binding.editTextDateStart.text.toString() &&
                        it.sportEndTime!! <= binding.editTextDateEnd.text.toString()
                    }
                )
                stepAdapter.notifyDataSetChanged()
            }
        }
        binding.btnReset.setOnClickListener {
            binding.editTextDateStart.setText("")
            binding.editTextDateEnd.setText("")
        }

        updateFootSteps()
    }

    private fun pickerDialog(type:Int){
        var dialog = AlertDialog.Builder(requireContext())
        var view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_picker,null)
        dialog.apply {
            setCancelable(false)
            setTitle("")
            setView(view)
            var picker1 = view.findViewById(R.id.picker1) as NumberPicker
            picker1.apply {
                minValue = 2020
                maxValue = 2022
                wrapSelectorWheel = false
            }
            var picker2 = view.findViewById(R.id.picker2) as NumberPicker
            picker2.apply {
                minValue = 1
                maxValue = 12
                wrapSelectorWheel = false
            }
            var picker3 = view.findViewById(R.id.picker3) as NumberPicker
            picker3.apply {
                minValue = 1
                maxValue = 31
                wrapSelectorWheel = false
            }
            setPositiveButton("確認") { dialogInterface, i ->
                var year = picker1.value.toString()
                var month = picker2.value.toString()
                var day = picker3.value.toString()
                if(month.length==1) month = "0$month"
                if(day.length==1) day = "0$day"
                Log.d("eee",  "$year-$month-$day")
                if(type==0)
                    binding.editTextDateStart.setText("$year-$month-$day")
                else
                    binding.editTextDateEnd.setText("$year-$month-$day")
            }
            setNegativeButton("取消") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            show()
        }
    }

    private fun updateFootSteps() {
        mainViewModel.weekTotalStepsData.observe(viewLifecycleOwner,{ datas ->
            dataList.clear()
            b_dataList.clear()
            var sortedData = datas.sortedByDescending { it.sportEndTime }
            dataList.addAll(sortedData)
            b_dataList.addAll(sortedData)
            binding.pageNodata.pageEmpty.apply {
                visibility = if(dataList.size==0)
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
            stepAdapter.notifyDataSetChanged()
        })

        /*mainViewModel.dayFootStepsData.observe(viewLifecycleOwner, { datas ->
            dataList.clear()
            dataList.addAll(datas)
            binding.pageNodata.pageEmpty.apply {
                visibility = if(dataList.size==0)
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
            stepAdapter.notifyDataSetChanged()
        })*/
    }
}