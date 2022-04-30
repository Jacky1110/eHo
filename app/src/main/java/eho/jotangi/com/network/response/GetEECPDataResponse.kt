package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class GetEECPDataResponse (
    var Data: GetEECPData?
): Parcelable, BaseResponse()

@Parcelize
data class GetEECPData (
    var iid: String?,
    var eid: String?,
    var phone: String?,
    var SPO2: String?,
    var HR: String?,
    var HP: String?,
    var LP: String?,
    var DSA: String?,
    var DSP: String?,
    var RR: String?,
    var minHR: String?,
    var maxHR: String?,
    var minSPO2: String?,
    var maxSPO2: String?,
    var treatmenttime: String?,
    var treatment_date: String?,
    var treatment_time: String?,
    var updatetime: String?
): Parcelable

/*
{
    "isSuccess": true,
    "Data": {
        "iid": null,
        "eid": null,
        "phone": null,
        "SPO2": "62",
        "HR": "72",
        "HP": "0",
        "LP": "0",
        "DSA": "2.25",
        "DSP": "1.96",
        "RR": "714",
        "minHR": "",
        "maxHR": "",
        "minSPO2": "",
        "maxSPO2": "",
        "treatmenttime": "2020-09-28 20:08:42",
        "treatment_date": "2020-09-28",
        "treatment_time": "20:08:42",
        "updatetime": "2021-07-18 10:10:10"
    },
    "Message": ""
}
 */