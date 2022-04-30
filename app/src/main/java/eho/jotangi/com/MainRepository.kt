package eho.jotangi.com

import eho.jotangi.com.network.AppClientManager
import eho.jotangi.com.network.request.*
import eho.jotangi.com.network.response.*
import eho.jotangi.com.utils.SharedPreferencesUtil
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 *Created by Luke Liu on 3/3/21.
 */

class MainRepository {

    fun newsGetList(): Single<NewsResponse> {
        return AppClientManager.instance.service.newsGetList(
            NewsRequest()
        )
    }

    fun bannerGetList(): Single<BannerResponse> {
        return AppClientManager.instance.service.bannerGetList()
    }

    fun productGetBestSalesList(): Single<BestSalesProductResponse> {
        return AppClientManager.instance.service.productGetBestSalesList()
    }

    fun memberAppMyPoint(): Single<MemberAppMyPointResponse> {
        return AppClientManager.instance.service.memberAppMyPoint()
    }

    fun myNextBooking(): Single<MyNextBookingResponse> {
        return AppClientManager.instance.service.myNextBooking()
    }

    fun getWristBandSummary(): Single<GetWristBandSummaryResponse> {
        return AppClientManager.instance.service.getWristBandSummary(
            MemberRequest()
        )
    }

    fun getEECPData(): Single<GetEECPDataResponse> {
        return AppClientManager.instance.service.getEECPData(
            MemberRequest()
        )
    }

    fun getDataDate(category:String): Single<DateResponse>{
        return AppClientManager.instance.service.getDataDate(
            IndexRequest(category)
        )
    }

    fun getBodyMeasureRawData(date:String): Single<GoBodyDataResponse>{
        return AppClientManager.instance.service.getBodyMeasureRawData(
            IndexRequest2(date)
        )
    }

    fun getVesselData(date:String): Single<VesselDateResponse>{
        return AppClientManager.instance.service.getVesselData(
            IndexRequest2(date)
        )
    }

    fun editFav(id: String): Single<BaseResponse> {
        return AppClientManager.instance.service.addStoreCollection(
            FavRequest(id)
        )
    }

    //4/26
    fun getPic(admin_name: String, Mode: String, picture_url: String ): Single<BaseResponse>{
        return AppClientManager.instance.service.getMyMemberPicture(
            PicRequest(admin_name, Mode, picture_url)
        )
    }

    fun getShopCartCount(): Single<GetShopCartCountResponse> {
        return AppClientManager.instance.service.getShopCartCount()
    }

    fun getMyMemberCode(): Single<GetMyMemberCodeResponse> {
        return AppClientManager.instance.service.getMyMemberCode()
    }

    fun getMyMemberInfo(): Single<MyMemberInfoResponse>{
        return AppClientManager.instance.service.getMyMemberInfo()
    }


    suspend fun getWristBandFootSteps(startTime:String,endTime:String,memberId:String): FootStepsResponse{
        return try {
            AppClientManager.instance.watchservice.getFootSteps(startTime, endTime, memberId)
        }catch (e:Exception){
            FootStepsResponse();
        }
    }

    suspend fun getHeartRate(startTime:String,endTime:String,memberId:String): HeartRateResponse{
        return try {
            AppClientManager.instance.watchservice.getHeartRate(startTime, endTime, memberId)
        }catch (e:Exception){
            HeartRateResponse()
        }
    }

    suspend fun getOxygen(startTime:String,endTime:String,memberId:String): OxygenResponse{
        return try {
            AppClientManager.instance.watchservice.getOxygen(startTime, endTime, memberId)
        }catch (e:Exception){
            OxygenResponse()
        }
    }

    suspend fun getSleep(startTime:String,endTime:String,memberId:String): SleepResponse{
        return try {
            AppClientManager.instance.watchservice.getSleep(startTime, endTime, memberId)
        }catch (e:Exception){
            SleepResponse()
        }
    }

    suspend fun getSleepDetail(startTime:String,endTime:String,memberId:String): SleepDetailResponse{
        return try {
            AppClientManager.instance.watchservice.getSleepDetail(startTime, endTime, memberId)
        }catch (e:Exception){
            SleepDetailResponse()
        }
    }

    suspend fun getBP(startTime:String,endTime:String,memberId:String): BPResponse{
        return try {
            AppClientManager.instance.watchservice.getBP(startTime, endTime, memberId)
        }catch (e:Exception){
            BPResponse()
        }
    }

    suspend fun getSport(startTime:String,endTime:String,memberId:String): SportResponse{
        return try {
            AppClientManager.instance.watchservice.getSport(startTime, endTime, memberId)
        }catch (e:Exception){
            SportResponse()
        }
    }

    suspend fun getECG(startTime:String,endTime:String,memberId:String):EcgResponse{
        return try{
            AppClientManager.instance.watchservice.getECG(startTime, endTime, memberId)
        }catch (e:Exception){
            EcgResponse()
        }
    }
    suspend fun userPic(pic: File) : Single<BaseResponse>{
        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
        multipart.addFormDataPart(
        name = "pic",
        filename = pic.name,
        body = pic.asRequestBody("image/*".toMediaType())
        )
        SharedPreferencesUtil.instances.getAccountId()
            ?.let { multipart.addFormDataPart("account_id", it) }
        return AppClientManager.instance.service.getUserPicUpload(multipart.build())
    }

}