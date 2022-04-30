package eho.jotangi.com.network.request

import android.os.Parcelable
import eho.jotangi.com.utils.SharedPreferencesUtil
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
open class MemberRequest (
    var account_id: String? = SharedPreferencesUtil.instances.getAccountId()
): Parcelable, AppRequest()

@Parcelize
open class AppRequest (
    var isApp: Boolean = true
): Parcelable

@Parcelize
data class FavRequest (
    val id: String,
    val mode: String = "product"
): Parcelable, MemberRequest()

@Parcelize
data class IndexRequest(
    val category:String
):Parcelable,MemberRequest()


@Parcelize
data class IndexRequest2(
    val data_date:String
):Parcelable,MemberRequest()
/*
{
    "id": 545,
    "account_id": 1311,
    "mode": "product",
    "isApp": true
}
 */