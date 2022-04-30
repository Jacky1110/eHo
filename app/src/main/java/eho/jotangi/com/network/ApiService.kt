package eho.jotangi.com.network

import eho.jotangi.com.network.request.*
import eho.jotangi.com.network.response.*
import eho.jotangi.com.utils.JotangiUtilConstants
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    /**
     * 公告
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/News/GetList")
    fun newsGetList(
        @Body newsRequest: NewsRequest
    ): Single<NewsResponse>

    /**
     * 優惠活動
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Banner/GetList")
    fun bannerGetList(
        @Body appRequest: AppRequest = AppRequest()
    ): Single<BannerResponse>

    /**
     * 優惠商品
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Product/GetBestSalesProductList")
    fun productGetBestSalesList(
        @Body appRequest: AppRequest = AppRequest()
    ): Single<BestSalesProductResponse>

    /**
     * 我的紅利點數
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Member/AppMyPoint")
    fun memberAppMyPoint(
        @Body memberRequest: MemberRequest = MemberRequest()
    ): Single<MemberAppMyPointResponse>

    /**
     * 我的預約
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Therapy/MyNextBooking")
    fun myNextBooking(
        @Body memberRequest: MemberRequest = MemberRequest()
    ): Single<MyNextBookingResponse>

    /**
     * 最新一筆測量數值 (手環資訊)
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Health/GetWristBandSummary")
    fun getWristBandSummary(
        @Body memberRequest: MemberRequest
    ): Single<GetWristBandSummaryResponse>

    /**
     * 最新一筆測量數值 (EECP)
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Health/GetEECPData")
    fun getEECPData(
        @Body memberRequest: MemberRequest
    ): Single<GetEECPDataResponse>

    /**
     * 查詢gobody 血管檢測日期
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Health/GetDataDate")
    fun getDataDate(
        @Body indexRequest: IndexRequest
    ): Single<DateResponse>

    /**
     * 查詢gobody資訊
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Health/GetBodyMeasureRawData")
    fun getBodyMeasureRawData(
        @Body indexRequest2: IndexRequest2
    ): Single<GoBodyDataResponse>

    /**
     * 查詢血管檢測資訊
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Health/GetVesselData")
    fun getVesselData(
        @Body indexRequest2: IndexRequest2
    ): Single<VesselDateResponse>

    /**
     * 加入/移除 我的最愛
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Store/AddCollection")
    fun addStoreCollection(
        @Body favRequest: FavRequest
    ): Single<BaseResponse>

    /**
     * 推薦碼
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Member/GetMyMemberCode")
    fun getMyMemberCode(
        @Body memberRequest: MemberRequest = MemberRequest()
    ): Single<GetMyMemberCodeResponse>

    /**
     * 會員資料
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Member/GetMemberInfo")
    fun getMyMemberInfo(
        @Body memberRequest: MemberRequest = MemberRequest()
    ): Single<MyMemberInfoResponse>

    /**
     * 購物車數量
     */
    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
    @POST("api/Shopping/GetShopcartCount")
    fun getShopCartCount(
        @Body memberRequest: MemberRequest = MemberRequest()
    ): Single<GetShopCartCountResponse>

    /**
     * 上傳照片
     */
    @POST("api/Member/FileUpload")
    suspend fun getUserPicUpload(
        @Body memberRequest: RequestBody
    ): Single<BaseResponse>

    /**
     * 下載照片
     */
    @POST("api/Member/AppSaveDetail")
    fun getMyMemberPicture(
        @Body memberRequest: PicRequest
    ): Single<BaseResponse>


//    @Headers(JotangiUtilConstants.HEADER_ACCEPT)
//    @POST("api/Member/FileUpload")
//    fun getMyMemberPicture(
//        @Body memberRequest: MemberRequest = MemberRequest()
//    ): Single<MyMemberPictureResponse>
}

