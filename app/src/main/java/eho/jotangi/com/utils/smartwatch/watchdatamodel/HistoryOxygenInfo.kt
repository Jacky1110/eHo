package eho.jotangi.com.utils.smartwatch.watchdatamodel

import com.google.gson.annotations.SerializedName

class HistoryOxygenInfo {
    @SerializedName(KEY_START_TIME)
    var startTime // 血压测试时间
            : Long = 0

    @SerializedName(KEY_OOValue)
    var ooValue // 收缩压
             = 0

    constructor() {}
    constructor(startTime: Long, ooValue: Int) {
        this.startTime = startTime
        this.ooValue = ooValue
    }

    companion object {
        const val KEY_START_TIME = "StartTime"
        const val KEY_OOValue = "OOValue"
    }
}