package eho.jotangi.com.ui.wristband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.FootStepsData
import eho.jotangi.com.network.response.OxygenData
import eho.jotangi.com.utils.smartwatch.WatchUtils

class FootStepAdapter(private val mData : List<FootStepsData>) :RecyclerView.Adapter<FootStepAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FootStepAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_footstep,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v:View) :RecyclerView.ViewHolder(v){
        var tvFootStepNum:TextView = v.findViewById(R.id.tv_footstep_num)
        var tvFootStepDate:TextView = v.findViewById(R.id.tv_footstep_date)

        fun bind(model:FootStepsData){
            tvFootStepNum.text = model.sportStep.toString()
            //tvFootStepDate.text = WatchUtils.instance.clipTimeFormatSecond(model.sportEndTime)
            tvFootStepDate.text = model.sportEndTime
        }
    }
}
