package eho.jotangi.com.network.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class NewsRequest (
    var website_id: String? = "",
    var keyword: String? = "",
    var releasestartdate: String? = "",
    var releaseenddate: String? = "",
    var length: Int = 5,
    var page: Int = 0,
    var isPagination: Boolean = true
): Parcelable