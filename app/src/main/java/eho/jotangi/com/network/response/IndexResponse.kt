package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateResponse (
    var Data:List<String> = listOf()
):Parcelable,BaseResponse()

@Parcelize
data class GoBodyDataResponse(
    var Data:GoBodyData? = null
):Parcelable,BaseResponse()

@Parcelize
data class GoBodyData(
    var updatetime:String ?= null,
    var measuretime:String ?= null,
    var composition:CompositionData ?= null
):Parcelable

@Parcelize
data class CompositionData(
    var weight:CompositionDataDetail ?= null,
    var fat:CompositionDataDetail ?= null,
    var ffm:CompositionDataDetail ?= null,
    var mineral:CompositionDataDetail ?= null,
    var muscle:CompositionDataDetail ?= null,
    var protein:CompositionDataDetail ?= null,
    var tbw:CompositionDataDetail ?= null,
    var segmental_muscle:SegmentalMmuscleData ?= null,
    var segmental_fat:SegmentalMmuscleData ?= null,
    var whfr:CompositionDataDetail ?= null,
    var pbf:CompositionDataDetail ?= null,
    var smm:CompositionDataDetail ?= null,
    var vfi:CompositionDataDetail ?= null,
    var strong_index:CompositionDataDetail ?= null,
    var bmr:CompositionDataDetail ?= null,
    var tee:CompositionDataDetail ?= null,
    var bmi:CompositionDataDetail ?= null,
    var body_age:CompositionDataDetail ?= null,
    var body_age_offset:CompositionDataDetail ?= null,
    var figure:CompositionDataDetail ?= null,

):Parcelable

@Parcelize
data class SegmentalMmuscleData(
    var la:CompositionDataDetail ?= null,
    var ll:CompositionDataDetail ?= null,
    var ra:CompositionDataDetail ?= null,
    var rl:CompositionDataDetail ?= null,
    var tr:CompositionDataDetail ?= null
):Parcelable

@Parcelize
data class CompositionDataDetail(
    var value:Float ?= null,
    var water_drop:Float ?= null,
    var grade:Int ?= null,
    var prv:Array<Float> ?= null,
    var stars:Float ?= null,
    var map:Array<Float> ?= null,
    var posture:Float ?= null,
    var evaluation:EvaluationData ?= null,

):Parcelable

@Parcelize
data class EvaluationData(
    var fat:CompositionDataDetail ?= null,
    var abdominal_fat:CompositionDataDetail ?= null,
    var weakness:CompositionDataDetail ?= null,
    var nutrition:CompositionDataDetail ?= null,
    var muscle_control:CompositionDataDetail ?= null,
    var fat_control:CompositionDataDetail ?= null,
    var body_score:CompositionDataDetail ?= null,
    var scroe:CompositionDataDetail ?= null,
    var calcium:CompositionDataDetail ?= null
):Parcelable

@Parcelize
data class VesselDateResponse (
    var Data:VesselDate? = null
):Parcelable,BaseResponse()

@Parcelize
data class VesselDate(
    var id:String ?= null,
    var heart:String?=null,
    var brachial_asi_r:String?=null,
    var brachial_sys_r:String?=null,
    var brachial_dia_r:String?=null,
    var brachial_asi_l:String?=null,
    var brachial_sys_l:String?=null,
    var brachial_dia_l:String?=null,
    var ankle_asi_r:String?=null,
    var ankle_sys_r:String?=null,
    var ankle_dia_r:String?=null,
    var ankle_asi_l:String?=null,
    var ankle_sys_l:String?=null,
    var ankle_dia_l:String?=null,
    var abi_r:String?=null,
    var abi_l:String?=null,
    var pwv_r:String?=null,
    var pwv_l:String?=null,
    var updatetime:String?=null,
    var measuretime:String?=null,
):Parcelable