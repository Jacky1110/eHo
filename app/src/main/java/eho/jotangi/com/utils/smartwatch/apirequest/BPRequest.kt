package eho.jotangi.com.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by SeanChang on 2021/11/03.
 */

@Parcelize
data class BPRequest (
    var memberId: String? = "",
    var bloodStartTime: String? = "",
    var bloodDBP: Int = 0,
    var bloodSBP: Int = 0
): Parcelable