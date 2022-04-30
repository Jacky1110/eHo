package eho.jotangi.com.ui.wristband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.SleepData
import eho.jotangi.com.utils.smartwatch.WatchUtils
import java.text.SimpleDateFormat
import java.util.*

class SleepAdapter(private val mData : List<SleepData>) :RecyclerView.Adapter<SleepAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sleep,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var tvSleepHr:TextView = v.findViewById(R.id.tv_sleep_hr)
        var tvSleepMin:TextView = v.findViewById(R.id.tv_sleep_min)
        var tvSleepDate:TextView = v.findViewById(R.id.tv_sleep_date)

        fun bind(model:SleepData){
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            var hour = 0
            var minute = 0
            try {
                val start = sdf.parse(model.startTime)
                val stop = sdf.parse(model.endTime)
                val diff = stop.time - start.time
                var seconds = diff / 1000
                hour = (seconds / 3600).toInt()
                seconds %= 3600
                minute = (seconds / 60).toInt()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }


            tvSleepHr.text = hour.toString()
            tvSleepMin.text = minute.toString()
            tvSleepDate.text = WatchUtils.instance.clipTimeFormatSecond(model.startTime)
        }
    }
}

