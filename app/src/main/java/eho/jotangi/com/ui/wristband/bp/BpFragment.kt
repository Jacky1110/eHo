package eho.jotangi.com.ui.wristband.bp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import com.yucheng.ycbtsdk.YCBTClient
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentBloodpressureBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.ui.wristband.view.HorizontalColorBar
import eho.jotangi.com.utils.SharedPreferencesUtil
import eho.jotangi.com.utils.smartwatch.WatchUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class BpFragment : BaseFragment(){
    private var _binding: FragmentBloodpressureBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBloodpressureBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
        setupToolbarDocument(GoType.BPDETAL)
        mainViewModel.getWatchInfo()

        binding.hcbvSBP.setType(HorizontalColorBar.Type.SBP)
        binding.hcbvDBP.setType(HorizontalColorBar.Type.DBP)

        updateBPData()

        binding.bnOxygenStart.setOnClickListener {
            val mac = SharedPreferencesUtil.instances.getWatchMac()
            if(mac!=null){
                pickerDialog()
            }else{
                Toast.makeText(context,"請先綁定手環",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickerDialog(){
        var dialog = AlertDialog.Builder(requireContext())
        var view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bp_picker,null)
        dialog.apply {
            setCancelable(false)
            setTitle("")
            setView(view)
            var picker1 = view.findViewById(R.id.picker1) as NumberPicker
            picker1.apply {
                minValue = 60
                maxValue = 250
                wrapSelectorWheel = false
            }
            var picker2 = view.findViewById(R.id.picker2) as NumberPicker
            picker2.apply {
                minValue = 40
                maxValue = 150
                wrapSelectorWheel = false
            }
            setPositiveButton("確認") { dialogInterface, i ->
                Log.d("eee", picker1.value.toString() + " , " + picker2.value.toString())
                YCBTClient.appBloodCalibration(
                    picker1.value,
                    picker2.value
                ) { i, fl, hashMap ->
                    Log.d("eee", "$i , $fl , ${hashMap.get("data").toString()}")
                    if (i == 0 && hashMap != null) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d("eee", "$i , $fl , ${hashMap.get("data").toString()}")
                            Toast.makeText(requireContext(), "校正完成", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            setNegativeButton("取消", { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            show()
        }
    }

    private fun updateBPData() {
        mainViewModel.last7BPData.observe(viewLifecycleOwner,{
            it?.let { data ->
                if (data.isNotEmpty()) {
                    // mainViewModel在取資料時有依照時間排序, 最近的在前
                    val last = data.get(0)
                    Timber.d("$TAG, BP -> $data")
                    if (last.bloodDBP != null && last.bloodSBP != null) {
                        binding.tvBPValue.text = String.format("%s/%s", last.bloodSBP, last.bloodDBP)
//                        binding.tvBPValue.text = String.format("%s/%s", last.bloodDBP, last.bloodSBP)
                        binding.hcbvDBP.setDataValue(last.bloodDBP!!.toInt())
                        binding.hcbvSBP.setDataValue(last.bloodSBP!!.toInt())
                    }
                    binding.tvBPTime.text = WatchUtils.instance.clipTimeFormatSecond(last.bloodStartTime)

                    val co = data.size
                    val dbps = data.take(7).map { it.bloodDBP?.toInt() }.filterNotNull().toIntArray()
                    val sbps = data.take(7).map { it.bloodSBP?.toInt() }.filterNotNull().toIntArray()
                    binding.cbcvBP.setDataValue(dbps, sbps)
                }
            }
        })



    }

}