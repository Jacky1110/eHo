package eho.jotangi.com.utils.smartwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import eho.jotangi.com.R
import eho.jotangi.com.utils.model.NotificationObj
import eho.jotangi.com.utils.smartwatch.watchdatamodel.WatchInfo

class WatchScanDeviceAdapter(private val mData: List<ScanDeviceBean>) :
    RecyclerView.Adapter<WatchScanDeviceAdapter.ViewHolder>() {

    var itemClick: (ScanDeviceBean) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchScanDeviceAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_watch_scan, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var ivPhoto: ImageView = v.findViewById(R.id.ivPhoto)
        var tvWatchName: TextView = v.findViewById(R.id.tvWatchName)
        var tvWatchMac: TextView = v.findViewById(R.id.tvWatchMac)
    }

    override fun onBindViewHolder(holder: WatchScanDeviceAdapter.ViewHolder, position: Int) {

        val data = mData[position]

        holder.apply {
            tvWatchName.text = data.deviceName
            tvWatchMac.text = data.deviceMac

            itemView.setOnClickListener {
                itemClick.invoke(data)
            }
        }
    }
}