package eho.jotangi.com.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by SeanChang on 2021/11/03.
 */

@Parcelize
data class HeartRateRequest (
    var memberId: String? = "",
    var heartStartTime: String? = "",
    var heartValue: Int = 0
): Parcelable