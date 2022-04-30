package eho.jotangi.com.utils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *Created by Luke Liu on 2021/8/20.
 */

@Parcelize
data class NotificationObj (
    var title: String? = null,
    var content: String? = null,
    var time: String? = null,
    var isRead: Boolean = false
): Parcelable