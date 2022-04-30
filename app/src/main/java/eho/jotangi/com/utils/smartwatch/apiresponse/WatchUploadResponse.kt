package eho.jotangi.com.utils.smartwatch.apiresponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Sean Chang on 2021/11/03.
 */
/*
{
    "code": "0x200",
    "status": "true",
    "responseMessage": "上傳成功"
}
 */
@Parcelize
data class WatchUploadResponse (
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
): Parcelable



