package eho.jotangi.com.utils

/**
 *Created by Luke Liu on 2021/8/18.
 */

object JotangiUtilConstants {

    const val NONE_DATA = "--"
    const val DOMAIN = "https://swcoasttest.jotangi.net:10443/" //測試機
//    const val DOMAIN = "https://eho.jotangi.net:10443/" //正式機

    const val DOMAIN2 = "http://swcoasttest.jotangi.net:8080/health/"
//    const val DOMAIN2 = "http://eho.jotangi.net:8080/health/"

    const val HEADER_ACCEPT = "Accept: application/json"

    const val PHOTO_FROM_GALLERY = 0
    const val PHOTO_FROM_CAMERA = 1
    const val PERMISSION_CAMERA_REQUEST_CODE = 4
    const val PERMISSION_LOCATION_REQUEST_CODE = 5

    const val TEL = "tel:"
    const val MAPS_SEARCH = "https://maps.google.com/?q="
    const val MAPS = "maps.google.com"

    object WebUrl {
        val MALL = "${DOMAIN}Good_index.html"
        val RESERVATION_CALENDAR = "${DOMAIN}Reservation_calendar.html"
        val RESERVATION_INDEX = "${DOMAIN}Reservation_index.html"
        val STORE = "${DOMAIN}storelist_map.html"

//        val SHOPPING_CART = "${DOMAIN}Good_cart_store.html"
        val SHOPPING_CART = "${DOMAIN}Good_cart_1.html"
        val NOTICE = "${DOMAIN}notice.html"

        val LOGIN = "${DOMAIN}login.html"

        val HEALTH_INDEX = "${DOMAIN}health_index.html"

        val PRIVACY_POLICY = "${DOMAIN}privacy_policy.html"
        val FAQ = "${DOMAIN}qa.html"
        val BEAN = "${DOMAIN}onebean.html"
        val COUPON = "${DOMAIN}coupon.html"
//        val ORDER_INQUIRY = "${DOMAIN}order_inquiry.html"
        val ORDER_INDEX = "${DOMAIN}order_index.html"
        val FAVORITE = "${DOMAIN}favorite.html"
        val MEMBER = "${DOMAIN}member.html"
        val PASSWORD = "${DOMAIN}changepwd.html"
        val RESERVATION_RECORD = "${DOMAIN}reservation_record.html"
        val TICKET_INDEX = "${DOMAIN}Ticket_index.html"
        val MYFV_INDEX = "${DOMAIN}MemberTree.html"
        val GOOD_IN = "${DOMAIN}Good_in.html"
        val HEALTH_EECP = "${DOMAIN}health_eecp.html"
        val HEALTH_EECP_HISTORY = "${DOMAIN}health_eecp_history.html"
        val MY_FAVORITE = "${DOMAIN}myfavorite.html"
        val HEALTH_VESSEL = "${DOMAIN}health_vessel.html"
        val HEALTH_VESSEL_HISTORY = "${DOMAIN}health_vessel_history.html"
        val HEALTH_VESSEL_INTRO = "${DOMAIN}health_vessel_intro.html"
        val HEALTH_GOBODY = "${DOMAIN}health_body_data.html"
        val HEALTH_GOBODY_HISTORY = "${DOMAIN}health_body_history.html"
        val WRISTBAND_NOT_BINDING = "${DOMAIN}wristbandNotBinding.html"
        val DESCRIPT_POINT = "${DOMAIN}descriptPoint.html"
        val DM_VIEWER = "${DOMAIN}DMviewer.html"
    }
}