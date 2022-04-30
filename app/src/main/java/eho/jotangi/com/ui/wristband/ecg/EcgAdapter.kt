package eho.jotangi.com.ui.wristband.ecg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.EcgData
import eho.jotangi.com.utils.smartwatch.WatchUtils

class EcgAdapter(private val mData:List<EcgData>):RecyclerView.Adapter<EcgAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcgAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ecg,parent,false))
    }

    override fun onBindViewHolder(holder: EcgAdapter.ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        var heart:TextView = v.findViewById(R.id.tv_ecg_heart)
        var bp:TextView = v.findViewById(R.id.tv_ecg_bp)
        var hrv:TextView = v.findViewById(R.id.tv_ecg_hrv)
        var date:TextView = v.findViewById(R.id.tv_ecg_date)

        fun bind(model: EcgData){
            heart.text = model.hr.toString()
            bp.text = model.dbp.toString()+"/"+model.sbp
            hrv.text = model.hrv.toString()
            date.text = WatchUtils.instance.clipTimeFormatSecond(model.ecgStartTime)
        }
    }

}