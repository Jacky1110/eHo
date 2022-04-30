package eho.jotangi.com.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import eho.jotangi.com.R
import eho.jotangi.com.utils.model.NotificationObj

class NotificationAdapter(private val mData: List<NotificationObj>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    var notificationItemClick: (NotificationObj) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var ivPhoto: ImageView = v.findViewById(R.id.ivPhoto)
        var tvTitle: TextView = v.findViewById(R.id.tvTitle)
        var tvContent: TextView = v.findViewById(R.id.tvContent)
        var tvTime: TextView = v.findViewById(R.id.tvTime)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {

        val data = mData[position]

        holder.apply {
            tvTitle.text = data.title
            tvContent.text = data.content
            tvTime.text = data.time
            ivPhoto.setImageResource(if (data.isRead) R.drawable.ic_baseline_mark_email_read_24 else R.drawable.ic_baseline_mark_email_unread_24)

            itemView.setOnClickListener {
                notificationItemClick.invoke(data)
            }
        }
    }
}