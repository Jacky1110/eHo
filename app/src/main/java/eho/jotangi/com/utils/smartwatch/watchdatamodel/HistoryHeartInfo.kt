package eho.jotangi.com.utils.smartwatch.watchdatamodel

import com.google.gson.annotations.SerializedName
import eho.jotangi.com.utils.smartwatch.watchdatamodel.HistoryHeartInfo

class HistoryHeartInfo {
    @SerializedName(KEY_HEART_START_TIME)
    var heartStartTime // -- 心率测试时间
            : Long = 0

    @SerializedName(KEY_HEART_VALUE)
    var heartValue // -- 心率测试时间
            = 0

    constructor() {}
    constructor(heartStartTime: Long, heartValue: Int) {
        this.heartStartTime = heartStartTime
        this.heartValue = heartValue
    }

    companion object {
        const val KEY_HEART_START_TIME = "heartStartTime"
        const val KEY_HEART_VALUE = "heartValue"
    }
}