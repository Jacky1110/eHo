package eho.jotangi.com.utils.smartwatch.watchdatamodel

import com.google.gson.annotations.SerializedName
import eho.jotangi.com.utils.smartwatch.watchdatamodel.HistorySleepInfo
import eho.jotangi.com.utils.smartwatch.watchdatamodel.HistorySleepDetailInfo

class HistorySleepInfo {
    @SerializedName(KEY_START_TIME)
    var startTime // -- 睡眠开始时间
            : Long = 0

    @SerializedName(KEY_END_TIME)
    var endTime // -- 睡眠结束时间
            : Long = 0

    @SerializedName(KEY_DEEP_SLEEP_COUNT)
    var deepSleepCount // -- 深睡次数
            = 0

    @SerializedName(KEY_LIGHT_SLEEP_COUNT)
    var lightSleepCount // -- 浅睡次数
            = 0

    @SerializedName(KEY_DEEP_SLEEP_TOTAL)
    var deepSleepTotal // -- 深睡总时长 单位分钟
            = 0

    @SerializedName(KEY_LIGHT_SLEEP_TOTAL)
    var lightSleepTotal // -- 浅睡总时长 单位分钟
            = 0

    @SerializedName(KEY_SLEEP_DATA)
    var sleepData // -- 睡眠详细数据集合
            : List<HistorySleepDetailInfo>? = null

    constructor() {}
    constructor(
        startTime: Long,
        endTime: Long,
        deepSleepCount: Int,
        lightSleepCount: Int,
        deepSleepTotal: Int,
        lightSleepTotal: Int,
        sleepData: List<HistorySleepDetailInfo>?
    ) {
        this.startTime = startTime
        this.endTime = endTime
        this.deepSleepCount = deepSleepCount
        this.lightSleepCount = lightSleepCount
        this.deepSleepTotal = deepSleepTotal
        this.lightSleepTotal = lightSleepTotal
        this.sleepData = sleepData
    }

    companion object {
        const val DEEP_SLEEP = 0xF1
        const val LIGHT_SLEEP = 0xF2
        const val KEY_START_TIME = "startTime"
        const val KEY_END_TIME = "endTime"
        const val KEY_DEEP_SLEEP_COUNT = "deepSleepCount"
        const val KEY_LIGHT_SLEEP_COUNT = "lightSleepCount"
        const val KEY_DEEP_SLEEP_TOTAL = "deepSleepTotal"
        const val KEY_LIGHT_SLEEP_TOTAL = "lightSleepTotal"
        const val KEY_SLEEP_DATA = "sleepData"
    }
}