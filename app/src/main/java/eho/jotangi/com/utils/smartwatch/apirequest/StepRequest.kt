package eho.jotangi.com.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by SeanChang on 2021/11/03.
 */

@Parcelize
data class StepRequest (
    var memberId: String? = "",
    var sportStartTime: String? = "",
    var sportEndTime: String? = "",
    var sportStep: Int = 0,
    var sportCalorie: Int = 0,
    var sportDistance: Int = 0
): Parcelable