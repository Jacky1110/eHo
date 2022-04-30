package eho.jotangi.com.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by SeanChang on 2021/11/03.
 */

@Parcelize
data class OxygenRequest (
    var memberId: String? = "",
    var startTime: String? = "",
    var OOValue: Int = 0
): Parcelable