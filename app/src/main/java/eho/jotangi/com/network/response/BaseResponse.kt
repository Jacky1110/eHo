package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
open class BaseResponse (
    var Message: String? = null,
    var isSuccess: Boolean? = null
): Parcelable