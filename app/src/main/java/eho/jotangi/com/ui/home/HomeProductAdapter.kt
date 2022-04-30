package eho.jotangi.com.ui.home

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eho.jotangi.com.R
import eho.jotangi.com.network.response.BestSalesProductDataResponse
import eho.jotangi.com.utils.JotangiUtilConstants
import java.text.DecimalFormat

class HomeProductAdapter(private val mData: List<BestSalesProductDataResponse>) :
    RecyclerView.Adapter<HomeProductAdapter.ViewHolder>() {

    var productItemClick: (BestSalesProductDataResponse) -> Unit = {}
    var productFavClick: (BestSalesProductDataResponse) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_product, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var ivPhoto: ImageView = v.findViewById(R.id.ivPhoto)
        var ivFavorite: ImageView = v.findViewById(R.id.ivFavorite)
        var tvTitle: TextView = v.findViewById(R.id.tvTitle)
        var tvPrice: TextView = v.findViewById(R.id.tvPrice)
        var tvSuggestedPrice: TextView = v.findViewById(R.id.tvSuggestedPrice)
        var space: Space = v.findViewById(R.id.space)
        var clickStatus = false
    }

    override fun onBindViewHolder(holder: HomeProductAdapter.ViewHolder, position: Int) {
        val data = mData[position]
        Log.d("eeePVPV",data.toString())

        holder.apply {
            space.visibility = if (mData.size-1 > position) View.GONE else View.VISIBLE
            clickStatus = data.isFavorite!!
            if (clickStatus)
                ivFavorite.setImageResource(R.drawable.ic_heart_f)
            else
                ivFavorite.setImageResource(R.drawable.ic_heart)

            tvTitle.text = data.product_name
            tvPrice.text = "$${DecimalFormat("#,###").format(data.price?.toFloatOrNull()?.toInt())?: JotangiUtilConstants.NONE_DATA}"
            //tvPrice.text = "$${123456789}"
            //tvSuggestedPrice.text = "$${123456789}"
            tvSuggestedPrice.visibility = View.INVISIBLE
            //tvSuggestedPrice.text = "$${data.price?.toFloatOrNull()?.toInt()?: JotangiUtilConstants.NONE_DATA}"
            //tvSuggestedPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

            data.picture_url?.let {
                Glide.with(itemView.context).load("${JotangiUtilConstants.DOMAIN}$it").into(ivPhoto)
            }

            ivFavorite.setOnClickListener {
                if(!clickStatus) {
                    ivFavorite.setImageResource(R.drawable.ic_heart_f)
                    clickStatus = true
                }else{
                    ivFavorite.setImageResource(R.drawable.ic_heart)
                    clickStatus = false
                }
                productFavClick.invoke(data)
            }

            itemView.setOnClickListener {
                productItemClick.invoke(data)
            }
        }
    }
}