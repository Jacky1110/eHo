package eho.jotangi.com.utils.smartwatch

import eho.jotangi.com.network.response.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WristBandService {
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
}