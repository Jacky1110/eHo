package eho.jotangi.com.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class NewsResponse (
    var Data: List<NewsDataResponse?>?
): Parcelable, BaseResponse()

@Parcelize
data class NewsDataResponse (
    var title: String?
): Parcelable

@Parcelize
data class BannerResponse (
    var Data: List<BannerDataResponse?>?
): Parcelable, BaseResponse()

@Parcelize
data class BannerDataResponse (
    var banner_id: String?,
    var website_id: String?,
    var website_name: String?,
    var title: String?,
    var content: String?,
    var enabled: String?,
    var enabled_name: String?,
    var expire_flag: String?,
    var expire_flag_name: String?,
    var start_date: String?,
    var end_date: String?,
    var picture_url: String?,
    var web_url: String?,
    var seq: String?,
    var modified_user: String?,
    var modified_date: String?,
    var RowNo: String?,
    var TotalNumber: String?
): Parcelable

@Parcelize
data class BestSalesProductResponse (
    var Data: List<BestSalesProductDataResponse?>?
): Parcelable, BaseResponse()

@Parcelize
data class BestSalesProductDataResponse (
    var product_id: String?,
    var product_name: String?,
    var price: String?,
    var resume: String?,
    var picture_url: String?,
    var isFavorite: Boolean?
): Parcelable

@Parcelize
data class GetMyMemberCodeResponse (
    var Data: String?
): Parcelable, BaseResponse()

@Parcelize
data class GetShopCartCountResponse (
    var Data: GetShopCartCountDataResponse?
): Parcelable, BaseResponse()


@Parcelize
data class GetShopCartCountDataResponse (
    var num: Int?
): Parcelable

@Parcelize
data class MyMemberPictureResponse(
    var Data: MyMemberPictureResponse?
): Parcelable, BaseResponse()