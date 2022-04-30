package eho.jotangi.com.utils

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

/**
 *Created by Luke Liu on 2021/8/19.
 */

class SharedPreferencesUtil private constructor() {

    private lateinit var spInit: SharedPreferences
    private lateinit var spMember: SharedPreferences
    private lateinit var spWatch: SharedPreferences
    private lateinit var spNotify: SharedPreferences

    companion object {
        val instances: SharedPreferencesUtil = SharedPreferencesUtil()
    }

    fun init(context: Context) {
        spInit = context.getSharedPreferences("appInit", Context.MODE_PRIVATE)
        spMember = context.getSharedPreferences("memberInfo", Context.MODE_PRIVATE)
        spWatch = context.getSharedPreferences("watch", Context.MODE_PRIVATE)
        spNotify = context.getSharedPreferences("notify", Context.MODE_PRIVATE)
    }

    private val KEY_ACCOUNT_INFO = "key_account_info"
    private val KEY_ACCOUNT_ID = "key_account_id"
    private val KEY_ACCOUNT_PIC = "key_account_pic"
    private val KEY_ACCOUNT_NAME = "key_account_name"
    private val KEY_HEADSHOT = "key_headshot"
    private val KEY_LOCATION_LAT = "lat"
    private val KEY_LOCATION_LON = "lon"
    private val KEY_MEMBER_CODE = "key_member_code"
    val KEY_WATCH_MAC = "key_watch_mac"
    val KEY_WATCH_NAME = "key_watch_name"
    val KEY_LAST_UPLOADD_OXYGEN_TIME = "key_lut_oxygen"
    val KEY_LAST_UPLOADD_BP_TIME = "key_lut_bp"
    val KEY_LAST_UPLOADD_HR_TIME = "key_lut_hr"
    val KEY_LAST_UPLOADD_SLEEP_TIME = "key_lut_sleep"
    val KEY_LAST_UPLOADD_STEP_TIME = "key_lut_step"
    val KEY_LAST_UPLOADD_SPORT_TIME = "key_lut_sport"
    val KEY_WATCH_LANGUAGE = "key_watch_language"
    val KEY_WATCH_TIME_FORMAT = "key_watch_time_format"


    /**
     * Member
     */
    fun setAccountInfo(info: String?) {
        Timber.e("SharedPreferencesUtil -> setAccountInfo(id: $info)")
        info?.let { spMember.edit().putString(KEY_ACCOUNT_INFO, it).apply() }
    }

    fun getAccountInfo(): String? {
        val info = spMember.getString(KEY_ACCOUNT_INFO, null)
        Timber.e("SharedPreferencesUtil -> getAccountInfo(id: $info)")
        return info
    }

    fun setAccountId(id: String?) {
        Timber.e("SharedPreferencesUtil -> setAccountId(id: $id)")
        id?.let { spMember.edit().putString(KEY_ACCOUNT_ID, it).apply() }
    }

    fun getAccountId(): String? {
        val id = spMember.getString(KEY_ACCOUNT_ID, null)
        Timber.e("SharedPreferencesUtil -> getAccountId(id: $id)")
        return id
    }

    //4/25新增set/get
    fun setAccountPic(pic: String?){
        Timber.e("SharedPreferencesUtil -> setAccountPic(pic: $pic)")
        pic?.let { spMember.edit().putString(KEY_ACCOUNT_PIC, it).apply() }
    }

    fun getAccountPic(): String?{
        val pic = spMember.getString(KEY_ACCOUNT_PIC, null)
        Timber.e("SharedPreferencesUtil -> setAccountPic(pic: $pic)")
        return pic
    }

    fun setAccountName(name: String?) {
        Timber.e("SharedPreferencesUtil -> setAccountName(name: $name)")
        name?.let { spMember.edit().putString(KEY_ACCOUNT_NAME, it).apply() }
    }

    fun getAccountName(): String? {
        val name = spMember.getString(KEY_ACCOUNT_NAME, null)
        Timber.e("SharedPreferencesUtil -> getAccountName(name: $name)")
        return name
    }

    private fun getAccountHeadShotKey(): String {
        return "${KEY_HEADSHOT}_${getAccountId()}"
    }

    fun setAccountHeadShot(path: String? = null) {
        Timber.e("SharedPreferencesUtil -> setAccountHeadShot(Path: $path)")
        spMember.edit().putString(getAccountHeadShotKey(), path).apply()
    }

    fun getAccountHeadShot(): String? {
        val path = spMember.getString(getAccountHeadShotKey(), null)
        Timber.e("SharedPreferencesUtil -> getAccountHeadShot(path: $path)")
        return path
    }

    fun setMemberCode(code: String?) {
        Timber.e("SharedPreferencesUtil -> setMemberCode(id: $code)")
        code?.let { spMember.edit().putString(KEY_MEMBER_CODE, it).apply() }
    }

    fun getMemberCode(): String? {
        val code = spMember.getString(KEY_MEMBER_CODE, null)
        Timber.e("SharedPreferencesUtil -> getMemberCode(id: $code)")
        return code
    }

    fun setUserLongitude(longitude: String): Boolean {
        return spInit.edit().putString(KEY_LOCATION_LON, longitude).commit()
    }

    fun getUserLongitude(): String {
        return spInit.getString(KEY_LOCATION_LON, "0").toString()
    }

    fun setUserLatitude(latitude: String): Boolean {
        return spInit.edit().putString(KEY_LOCATION_LAT, latitude).commit()
    }

    fun getUserLatitude(): String {
        return spInit.getString(KEY_LOCATION_LAT, "0").toString()
    }

    fun isLogin(): Boolean {
        return getAccountInfo()?.isNotBlank() == true
    }

    fun logout() {
        spMember.edit().clear().apply()
    }

    fun setWatchMac(mac: String?) {
        Timber.e("SharedPreferencesUtil -> setWatchMac(id: $mac)")
        //mac?.let { spWatch.edit().putString(KEY_WATCH_MAC, it).apply() }
        spWatch.edit().putString(KEY_WATCH_MAC, mac).apply()
    }

    fun getWatchMac(): String? {
        val mac = spWatch.getString(KEY_WATCH_MAC, null)
        Timber.e("SharedPreferencesUtil -> getWatchMac(id: $mac)")
        return mac
    }

    fun setWatchTimeFormat(format: String) {
        spWatch.edit().putString(KEY_WATCH_TIME_FORMAT, format).apply()
    }

    fun getWatchTimeFormat(): String {
        val format = spWatch.getString(KEY_WATCH_TIME_FORMAT, "24")
        return format!!
    }

    fun setWatchName(name: String?) {
        Timber.e("SharedPreferencesUtil -> setWatchName(id: $name)")
        name?.let { spWatch.edit().putString(KEY_WATCH_NAME, it).apply() }
    }

    fun getWatchName(): String? {
        val name = spWatch.getString(KEY_WATCH_NAME, null)
        Timber.e("SharedPreferencesUtil -> getWatchName(id: $name)")
        return name
    }

    fun setWatchLongValue(key: String, value: Long) {
        Timber.e("SharedPreferencesUtil -> setWatchLongValue(key:$key, value: $value)")
        spWatch.edit().putLong(key, value).commit()
    }

    fun getWatchLongValue(key: String): Long {
        val value = spWatch.getLong(key, 0)
        Timber.e("SharedPreferencesUtil -> getWatchLongValue(key:$key, value: $value)")
        return value
    }

    fun getNotifyStatus(key: String): Boolean {
        return spNotify.getBoolean(key, true)
    }

    fun setNotifyStatus(key: String) {
        spNotify.edit().putBoolean(key, !getNotifyStatus(key)).commit()
    }

    fun checkNotify(key: String): Boolean {
        var data = spNotify.all
        for (inkey in data.keys) {
            if (key.toLowerCase().contains(inkey)) {
                return getNotifyStatus(inkey)
            }
        }
        return true
    }

    fun setWatchIntValue(key: String, value: Int) {
        Timber.e("SharedPreferencesUtil -> setWatchIntValue(key:$key, value: $value)")
        spWatch.edit().putInt(key, value).commit()
    }

    fun getWatchIntValue(key: String, default: Int): Int {
        val value = spWatch.getInt(key, default)
        Timber.e("SharedPreferencesUtil -> getWatchIntValue(key:$key, value: $value)")
        return value
    }

    fun setWatchStringValue(key: String, value: String) {
        Timber.e("SharedPreferencesUtil -> setWatchStringValue(key:$key, value: $value)")
        spWatch.edit().putString(key, value).commit()
    }

    fun getWatchStringValue(key: String): String? {
        val value = spWatch.getString(key, "")
        Timber.e("SharedPreferencesUtil -> getWatchStringValue(key:$key, value: $value)")
        return value
    }

}