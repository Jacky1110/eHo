package eho.jotangi.com.utils.smartwatch

import eho.jotangi.com.network.AppClientManager
import eho.jotangi.com.utils.smartwatch.apirequest.*
import eho.jotangi.com.utils.smartwatch.apiresponse.WatchUploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody




class WatchApiRepository {

    suspend fun stepUpload(stepRequest: StepRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(stepRequest.memberId)
        map["sportStartTime"] = toRequestBody(stepRequest.sportStartTime)
        map["sportEndTime"] = toRequestBody(stepRequest.sportEndTime)
        map["sportStep"] = toRequestBody(stepRequest.sportStep.toString())
        map["sportCalorie"] = toRequestBody(stepRequest.sportCalorie.toString())
        map["sportDistance"] = toRequestBody(stepRequest.sportDistance.toString())

        return AppClientManager.instance.watchservice.stepUpload(map)
    }

    suspend fun sleepUpload(sleepRequest: SleepRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(sleepRequest.memberId)
        map["startTime"] = toRequestBody(sleepRequest.startTime)
        map["endTime"] = toRequestBody(sleepRequest.endTime)
        map["deepSleepCount"] = toRequestBody(sleepRequest.deepSleepCount.toString())
        map["lightSleepCount"] = toRequestBody(sleepRequest.lightSleepCount.toString())
        map["deepSleepTotal"] = toRequestBody(sleepRequest.deepSleepTotal.toString())
        map["lightSleepTotal "] = toRequestBody(sleepRequest.lightSleepTotal.toString())
        map["sleepData"] = toRequestBody(sleepRequest.sleepData)

        return AppClientManager.instance.watchservice.sleepUpload(map)
    }

    suspend fun heartRateUpload(heartRateRequest: HeartRateRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(heartRateRequest.memberId)
        map["heartStartTime"] = toRequestBody(heartRateRequest.heartStartTime)
        map["heartValue"] = toRequestBody(heartRateRequest.heartValue.toString())

        return AppClientManager.instance.watchservice.heartRateUpload(map)
    }

    suspend fun bpUpload(bpRequest: BPRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(bpRequest.memberId)
        map["bloodStartTime"] = toRequestBody(bpRequest.bloodStartTime)
        map["bloodDBP"] = toRequestBody(bpRequest.bloodDBP.toString())
        map["bloodSBP"] = toRequestBody(bpRequest.bloodSBP.toString())

        return AppClientManager.instance.watchservice.bpUpload(map)
    }

    suspend fun ecgUpload(ecgRequest: ECGRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(ecgRequest.memberId)
        map["ecgStartTime"] = toRequestBody(ecgRequest.ecgStartTime)
        map["ecgValue"] = toRequestBody(ecgRequest.ecgValue)
        map["hr"] = toRequestBody(ecgRequest.hr.toString())
        map["dbp"] = toRequestBody(ecgRequest.dbp.toString())
        map["sbp"] = toRequestBody(ecgRequest.sbp.toString())
        map["hrv"] = toRequestBody(ecgRequest.hrv.toString())

        return AppClientManager.instance.watchservice.ecgUpload(map)
    }

    suspend fun oxygenUpload(oxygenRequest: OxygenRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(oxygenRequest.memberId)
        map["startTime"] = toRequestBody(oxygenRequest.startTime)
        map["OOValue"] = toRequestBody(oxygenRequest.OOValue.toString())

        return AppClientManager.instance.watchservice.oxygenUpload(map)
    }

    suspend fun sportUpload(sportRequest: SportRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["sportStartTime"] = toRequestBody(sportRequest.sportStartTime)
        map["sportType"] = toRequestBody(sportRequest.sportType.toString())
        map["sportStep"] = toRequestBody(sportRequest.sportStep.toString())
        map["sportCalorie"] = toRequestBody(sportRequest.sportCalorie.toString())
        map["sportDistance"] = toRequestBody(sportRequest.sportDistance.toString())
        map["sportHR"] = toRequestBody(sportRequest.sportHR.toString())

        return AppClientManager.instance.watchservice.sportUpload(map)
    }

    fun toRequestBody(value: String?): RequestBody? {
        return value?.let {  RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
    }
}