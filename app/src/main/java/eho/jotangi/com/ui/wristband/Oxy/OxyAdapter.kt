package eho.jotangi.com.ui.wristband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.utils.smartwatch.WatchUtils

class OxyAdapter(private val mData : List<OxygenData>)  :
    RecyclerView.Adapter<OxyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OxyAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_oxy,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        var tvoxyNum: TextView = v.findViewById(R.id.tv_oxy_num)
        var tvoxyDate: TextView = v.findViewById(R.id.tv_oxy_date)

        fun bind(model: OxygenData){
            tvoxyNum.text = model.OOValue.toString()
            tvoxyDate.text = WatchUtils.instance.clipTimeFormatSecond(model.startTime)
        }
    }
}
