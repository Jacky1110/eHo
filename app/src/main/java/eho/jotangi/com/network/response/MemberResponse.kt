package eho.jotangi.com.network.response

import android.os.Parcelable
import eho.jotangi.com.network.request.MemberRequest
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class MemberAppMyPointResponse (
    var Data: Int? = null
): Parcelable, BaseResponse()

@Parcelize
data class MyNextBookingResponse (
    var Data: MyNextBookingDataResponse? = null
): Parcelable, BaseResponse()

@Parcelize
data class MyNextBookingDataResponse (
    var booking_id: String? = null,
    var booking_datetime: String? = null,
    var store_name: String? = null,
    var store_phone: String? = null,
    var store_address: String? = null
): Parcelable, BaseResponse()

@Parcelize
data class MyMemberInfoResponse(
    var Data:MemberInfoData? = null
):Parcelable, BaseResponse()



@Parcelize
data class MemberInfoData(
    var member_id:String? = null,
    var member_name:String? = null,
    var id:String? = null,
    var sex:String? = null,
    var birthday:String? = null,
    var body_height:String? = null,
    var body_weight:String? = null,
    var mobile:String? = null,
    var address:String? = null,
    var email:String? = null,
    var member_no:String? = null,
    var picture_url:String? = null
):Parcelable

@Parcelize
data class PicRequest(
    val admin_name: String? = null,
    val Mode: String? = null,
    val picture_url: String? = null
):Parcelable,MemberRequest()

