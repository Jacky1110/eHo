package eho.jotangi.com.ui.wristband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.BPData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.utils.smartwatch.WatchUtils
import java.util.*

class BpAdapter(private val mData : List<BPData>)  :
    RecyclerView.Adapter<BpAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BpAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bp,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        var tvbpNum: TextView = v.findViewById(R.id.tv_bp_num)
        var tvbpDate: TextView = v.findViewById(R.id.tv_bp_date)

        fun bind(model:BPData){
            tvbpNum.text = String.format(Locale.getDefault(), "%s/%s", model.bloodDBP, model.bloodSBP)
            tvbpDate.text = WatchUtils.instance.clipTimeFormatSecond(model.bloodStartTime)
        }
    }
}
