package eho.jotangi.com.ui.wristband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.HeartRateData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.utils.smartwatch.WatchUtils

class HeartRateAdapter(private val mData : List<HeartRateData>) :RecyclerView.Adapter<HeartRateAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_heart_rate,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v:View): RecyclerView.ViewHolder(v){
        var tvHeartRateNum:TextView = v.findViewById(R.id.tv_heart_rate_num)
        var tvHeartRateDate:TextView = v.findViewById(R.id.tv_heart_rate_date)

        fun bind(model:HeartRateData){
            tvHeartRateNum.text = model.heartValue.toString()
            tvHeartRateDate.text = WatchUtils.instance.clipTimeFormatSecond(model.heartStartTime)
        }
    }

}
