package eho.jotangi.com.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECGRequest (
    var memberId: String? = "",
    var ecgStartTime: String? = "",
    var ecgValue: String? = "",
    var hr: Int = 0,
    var dbp: Int = 0,
    var sbp: Int = 0,
    var hrv: Int = 0
): Parcelable