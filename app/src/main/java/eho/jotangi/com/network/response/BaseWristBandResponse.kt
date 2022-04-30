package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
open class BaseWristBandResponse (
    var responseMessage: String? = null,
    var code: String? = null,
    var status: String? = null
): Parcelable