package eho.jotangi.com.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import eho.jotangi.com.R
import eho.jotangi.com.network.response.BannerDataResponse
import eho.jotangi.com.utils.JotangiUtilConstants
import timber.log.Timber

class PromotionsPagerAdapter(private val mData: List<BannerDataResponse>) :
    RecyclerView.Adapter<PromotionsPagerAdapter.AdViewHolder>() {

    var promotionItemClick: (BannerDataResponse) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        return AdViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_promotion, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val data = mData[position]
        Timber.d("data:$data")

        holder.apply {
            data.picture_url?.let {
                //sivPhoto.scaleType = ImageView.ScaleType.FIT_CENTER
                Glide.with(itemView.context).load("${JotangiUtilConstants.DOMAIN}$it").into(sivPhoto)
            }

            tvTitle.text = data.title
            tvContent.text = data.content

            itemView.setOnClickListener {
                promotionItemClick.invoke(data)
            }
        }
    }

    inner class AdViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var sivPhoto: ShapeableImageView = v.findViewById(R.id.sivPhoto)
        var tvTitle: TextView = v.findViewById(R.id.tvTitle)
        var tvContent: TextView = v.findViewById(R.id.tvContent)
    }
}
