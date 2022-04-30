package eho.jotangi.com.utils.smartwatch

import eho.jotangi.com.network.response.*
import eho.jotangi.com.utils.smartwatch.apiresponse.WatchUploadResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface WatchApiService {

    /**
     * 上傳步數資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_WALK_UPLOAD)
    suspend fun stepUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳睡眠資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_SLEEP_UPLOAD)
    suspend fun sleepUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳睡眠資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_HEART_RATE_UPLOAD)
    suspend fun heartRateUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳血壓資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_BP_UPLOAD)
    suspend fun bpUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳血氧資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_OXYGEN_UPLOAD)
    suspend fun oxygenUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳ECG資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_ECG_UPLOAD)
    suspend fun ecgUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳運動資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST(SmartWatchConstants.WATCH_API_SPORT_UPLOAD)
    suspend fun sportUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    @FormUrlEncoded
    @POST("JTG_Get_Steps")
    suspend fun getFootSteps(@Field("startTime")startTime : String,
                             @Field("endTime")endTime : String,
                             @Field("memberId")memberId : String) :FootStepsResponse

    @FormUrlEncoded
    @POST("JTG_Get_HR")
    suspend fun getHeartRate(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) :HeartRateResponse

    @FormUrlEncoded
    @POST("JTG_Get_Oxygen")
    suspend fun getOxygen(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) :OxygenResponse

    @FormUrlEncoded
    @POST("JTG_Get_Sleep")
    suspend fun getSleep(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) : SleepResponse

    @FormUrlEncoded
    @POST("JTG_Get_SleepData")
    suspend fun getSleepDetail(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) : SleepDetailResponse

    @FormUrlEncoded
    @POST("JTG_Get_BP")
    suspend fun getBP(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) : BPResponse

    @FormUrlEncoded
    @POST("JTG_Get_Sport")
    suspend fun getSport(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) : SportResponse

    @FormUrlEncoded
    @POST("JTG_Get_ECG")
    suspend fun getECG(
        @Field("startTime")startTime : String,
        @Field("endTime")endTime : String,
        @Field("memberId")memberId : String) : EcgResponse
}