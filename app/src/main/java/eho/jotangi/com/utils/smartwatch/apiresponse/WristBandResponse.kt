package eho.jotangi.com.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize



@Parcelize
data class FootStepsResponse(
    var data: List<FootStepsData> ?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class FootStepsData(
    var sportStartTime: String ?=null,
    var sportEndTime: String ?=null,
    var sportStep: String ?=null,
    var sportCalorie: String ?=null,
    var sportDistance: String ?=null
):Parcelable

@Parcelize
data class OxygenResponse(
    var data: List<OxygenData>?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class OxygenData(
    var startTime: String ?=null,
    var OOValue: String ?=null
):Parcelable

@Parcelize
data class SleepDetailResponse(
    var data: List<SleepDetailData>?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class SleepDetailData(
    var sleepStartTime : String ?=null,
    var sleepType : String ?=null,
    var sleepLen: String ?=null
):Parcelable


@Parcelize
data class SleepResponse(
    var data: List<SleepData>?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
open class SleepData(
    var startTime: String ?=null,
    var endTime: String ?=null,
    var deepSleepCount: String ?=null,
    var lightSleepCount : String ?=null,
    var deepSleepTotal : String ?=null,
    var lightSleepTotal  : String ?=null
):Parcelable

@Parcelize
data class SleepRecord (
    var sleepDetailDatas : List<SleepDetailData>?= listOf()
):Parcelable, SleepData()

@Parcelize
data class DaySleepData(
    var date : String?= null,
    var type: Int = 0,
    var totalHour : Int = 0
):Parcelable

@Parcelize
data class HeartRateResponse(
    var data: List<HeartRateData>?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class HeartRateData(
    var heartStartTime: String ?=null,
    var heartValue: String ?=null
):Parcelable

@Parcelize
data class BPResponse(
    var data: List<BPData>?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class BPData(
    var bloodStartTime : String ?=null,
    var bloodDBP : String ?=null,
    var bloodSBP  : String ?=null
):Parcelable

@Parcelize
data class SportResponse(
    var data: List<SportData> ?= listOf()
):Parcelable,BaseWristBandResponse()

@Parcelize
data class SportData(
    var sportStartTime: String ?=null,
    var sportType : String ?=null,
    var sportStep: String ?=null,
    var sportCalorie: String ?=null,
    var sportDistance: String ?=null
):Parcelable

@Parcelize
data class EcgData(
    var ecgStartTime: String ?= null,
    var ecgValue: String ?=null,
    var hr: Int = 0,
    var dbp: Int = 0,
    var sbp: Int = 0,
    var hrv:Int = 0
):Parcelable

@Parcelize
data class EcgResponse(
    var data: List<EcgData> ?= listOf()
):Parcelable,BaseWristBandResponse()