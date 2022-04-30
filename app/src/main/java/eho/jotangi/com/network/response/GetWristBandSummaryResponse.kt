package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class GetWristBandSummaryResponse (
    var Data: GetWristBandSummaryData?
): Parcelable, BaseResponse()

@Parcelize
data class GetWristBandSummaryData (
    var account_id: String?,
    var heart: String?,
    var sp: String?,
    var dp: String?,
    var spo2: String?,
    var step_of_day: String?,
    var distance_of_day: String?,
    var calories_of_day: String?,
    var data_date: String?
): Parcelable

/*
{
    "isSuccess": true,
    "Data": {
        "account_id": "1292",
        "heart": "75",
        "sp": "110",
        "dp": "75",
        "spo2": "98",
        "step_of_day": null,
        "distance_of_day": null,
        "calories_of_day": null,
        "data_date": null
    },
    "Message": ""
}
 */