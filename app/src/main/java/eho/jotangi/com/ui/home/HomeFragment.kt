package eho.jotangi.com.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import eho.jotangi.com.*
import eho.jotangi.com.databinding.FragmentHomeBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.network.response.BannerDataResponse
import eho.jotangi.com.network.response.BestSalesProductDataResponse
import eho.jotangi.com.network.response.GoBodyData
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.utils.JotangiUtilConstants
import eho.jotangi.com.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null

    private var promotionsPagerAdapter: PromotionsPagerAdapter? = null
    private var homeProductAdapter: HomeProductAdapter? = null

    private var bannerList = mutableListOf<BannerDataResponse>()
    private var productList = mutableListOf<BestSalesProductDataResponse>()

    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? {
        return binding.toolbar
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLaunch) {
            findNavController().navigate(R.id.action_nav_home_to_nav_splash)
            isLaunch = false
        }

        setupToolbarHome()

        mainViewModel.memberAppMyPointData.observe(viewLifecycleOwner) {
            val point = it?.Data

            if (!SharedPreferencesUtil.instances.isLogin() || point == null) {
                binding.tvRewardPoint.setText(R.string.none_data)
            } else {
                binding.tvRewardPoint.text = "$point"
            }
        }

        mainViewModel.newsText.observe(viewLifecycleOwner) {
            it?.let { news ->

                binding.tvAnnouncement.text = news
                Log.d(TAG, "tvAnnouncement:" + news + "---")
//4/29新增快訊小喇叭
                if (news.trim().isEmpty()) {
                    Log.d(TAG, "INVISIBLE: ")
                    binding.ivAnnouncement.visibility = View.INVISIBLE
                } else {
                    Log.d(TAG, "VISIBLE: ")
                    binding.ivAnnouncement.visibility = View.VISIBLE
                }
            }
        }


        mainViewModel.editFavMsg.observe(viewLifecycleOwner) {
            it?.let { msg ->
                //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                mainViewModel.editFavMsg.value = null
            }
        }

        /*
        mainViewModel.getWristBandSummaryData.observe(viewLifecycleOwner, {
            it?.let { data ->
                data.spo2?.let { spo2 ->
                    binding.tvTabBandBloodOxygen.text = spo2
                }

                data.heart?.let { heart ->
                    binding.tvTabBandHeartRate.text = heart
                }

                data.step_of_day?.let { step_of_day ->
                    binding.tvTabBandSteps.text = step_of_day
                }
            }
        })
        */

        mainViewModel.dayTotalSteps.observe(viewLifecycleOwner) {
            it?.let { data ->
                showWatchUI()
                binding.tvTabBandSteps.text = data.toString()
            }
        }

        mainViewModel.last7HeartRateData.observe(viewLifecycleOwner) {
            it?.let { data ->
                showWatchUI()
                if (data.isNotEmpty()) {
                    val last = data.get(0)
                    last.heartValue?.let { heartValue ->
                        binding.tvTabBandHeartRate.text = heartValue
                    }
                } else {
                    hideWatchUI()
                }
            }
        }

        mainViewModel.lastOxygenData.observe(viewLifecycleOwner) { data ->
            data.OOValue?.let { OOValue ->
                showWatchUI()
                binding.tvTabBandBloodOxygen.text = OOValue
            }
        }

        mainViewModel.getEECPData.observe(viewLifecycleOwner) { data ->
            val SPO2 = data?.SPO2
            val updatetime =
                Regex(pattern = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}""").find(data?.treatmenttime.toString())?.value
            val HR = data?.HR

            if (!SharedPreferencesUtil.instances.isLogin()
                || (SPO2.isNullOrEmpty() && updatetime.isNullOrEmpty() && HR.isNullOrEmpty())
            ) {
                binding.clExaminationNone.visibility = View.VISIBLE
                binding.clExamination.visibility = View.GONE
            } else {
                binding.clExaminationNone.visibility = View.GONE
                binding.clExamination.visibility = View.VISIBLE

                SPO2?.let { SPO2 ->
                    binding.tvTabEECPBloodOxygen.text = SPO2
                }

                updatetime?.let { updatetime ->
                    binding.tvTabEECPDate.text = updatetime
                }

                HR?.let { HR ->
                    binding.tvTabEECPHeartbeat.text = HR
                }
            }
        }

        mainViewModel.myNextBookingData.observe(viewLifecycleOwner) { data ->

            val booking_datetime = data?.booking_datetime
            Log.d(TAG, "booking_datetime: + ${booking_datetime}")
            val store_address = data?.store_address
            Log.d(TAG, "store_address: + ${store_address}")
            //4/28更新
            val store_name = data?.store_name
            Log.d(TAG, "store_name: + ${store_name}")
            val store_phone = data?.store_phone
            Log.d(TAG, "store_phone: + ${store_phone}")


            if (!SharedPreferencesUtil.instances.isLogin()
                || (booking_datetime.isNullOrEmpty() && store_address.isNullOrEmpty() && store_phone.isNullOrEmpty())
            ) {
                binding.clReservation.visibility = View.GONE
                binding.clReservationNone.visibility = View.VISIBLE
            } else {
                binding.clReservation.visibility = View.VISIBLE
                binding.clReservationNone.visibility = View.GONE

                booking_datetime?.let { binding.tvTabReservationDate.text = it }
                // 4/28更新顯示店名
                store_name?.let { binding.tvTabReservationStore.text = it }
                store_phone?.let { binding.tvTabReservationTel.text = it }
            }
        }


        mainViewModel.myGoBodyData.observe(viewLifecycleOwner, object : Observer<GoBodyData> {
            override fun onChanged(t: GoBodyData?) {
                Log.d("eeedcc", t.toString())
                val time =
                    Regex(pattern = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}""").find(t?.measuretime.toString())?.value
                val body_age = t?.composition?.body_age?.value?.toInt().toString()
                val oil = t?.composition?.vfi?.value?.toInt().toString()
                if (!SharedPreferencesUtil.instances.isLogin()
                    || (time.isNullOrEmpty() || body_age.isNullOrEmpty() || oil.isNullOrEmpty())
                ) {
                    binding.clGobody.visibility = View.GONE
                    binding.clGobodyNone.visibility = View.VISIBLE
                } else {
                    binding.clGobody.visibility = View.VISIBLE
                    binding.clGobodyNone.visibility = View.GONE
                    time?.let { binding.tvTabGobodyDate.text = it }
                    body_age?.let { binding.tvTabGobodyAge.text = it }
                    oil?.let { binding.tvTabGobodyBodyOil.text = it }
                }
            }
        })

        mainViewModel.myVesselData.observe(viewLifecycleOwner) { data ->
            val time: String? =
                Regex(pattern = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}""").find(data?.measuretime.toString())?.value
            val asi_l: String? =
                Regex(pattern = """\d{2}""").find(data?.brachial_asi_l.toString())?.value
            val asi_r: String? =
                Regex(pattern = """\d{2}""").find(data?.brachial_asi_r.toString())?.value
            val abi_l: String? =
                Regex(pattern = """\d{1}.\d{1}""").find(data?.abi_l.toString())?.value
            val abi_r: String? =
                Regex(pattern = """\d{1}.\d{1}""").find(data?.abi_r.toString())?.value

            if (!SharedPreferencesUtil.instances.isLogin()
                || (time.isNullOrEmpty() || asi_l.isNullOrEmpty() && asi_r.isNullOrEmpty() && abi_l.isNullOrEmpty() && abi_r.isNullOrEmpty())
            ) {
                binding.clBloodv.visibility = View.GONE
                binding.clBloodvNone.visibility = View.VISIBLE
            } else {
                binding.clBloodv.visibility = View.VISIBLE
                binding.clBloodvNone.visibility = View.GONE
                time?.let { binding.tvTabBloodvDate.text = it }
                asi_l?.let { binding.tvTabBloodvASIL.text = asi_l }
                asi_r?.let { binding.tvTabBloodvASIR.text = asi_r }
                abi_l?.let { binding.tvTabBloodvABIL.text = abi_l }
                abi_r?.let { binding.tvTabBloodvABIR.text = abi_r }
            }

        }

        binding.apply {
            cvExamination.setOnClickListener {
                if (binding.clExaminationNone.visibility == View.VISIBLE) {
                    openWeb(JotangiUtilConstants.WebUrl.RESERVATION_INDEX)
                } else {
                    openWeb(JotangiUtilConstants.WebUrl.HEALTH_EECP)
                }
            }

            cvHealthWristBand.setOnClickListener {
                if (SharedPreferencesUtil.instances.getWatchMac() != null) {
                    mActivity?.navigationWristBandPage()
                } else {
                    mActivity?.navigationWristBandNotBindingPage()
                }
            }

            btnTabReservationNone.setOnClickListener {
                mActivity?.navigationBookingPage()
            }

            cvGobody.setOnClickListener {
                if (binding.clGobodyNone.visibility == View.VISIBLE)
                    openWeb(JotangiUtilConstants.WebUrl.RESERVATION_INDEX)
                else
                    openWeb(JotangiUtilConstants.WebUrl.HEALTH_GOBODY)
            }

            cvBloodV.setOnClickListener {
                if (binding.clBloodvNone.visibility == View.VISIBLE)
                    openWeb(JotangiUtilConstants.WebUrl.RESERVATION_INDEX)
                else
                    openWeb(JotangiUtilConstants.WebUrl.HEALTH_VESSEL)
            }

            tvTabReservationRecord.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.RESERVATION_RECORD)
            }

            tvTabReservationTel.setOnClickListener {
                intentUrl(
                    requireContext(),
                    "${JotangiUtilConstants.TEL}${tvTabReservationTel.text}"
                )
            }

            tvTabReservationStore.setOnClickListener {
                intentUrl(
                    requireContext(),
                    "${JotangiUtilConstants.MAPS_SEARCH}${tvTabReservationStore.text}"
                )
            }

            tvRewardInstruction.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.DESCRIPT_POINT)
            }
        }

        initPromotionPager()
        initProducts()
    }


    fun showWatchUI() {
        binding.tvWatchNoData.visibility = View.GONE
        binding.constraintHasWatchdata.visibility = View.VISIBLE
    }

    fun hideWatchUI() {
        binding.constraintHasWatchdata.visibility = View.GONE
        binding.tvWatchNoData.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.d("eee", "home_onresume")
        CoroutineScope(Dispatchers.IO).launch {
            getWristBandData()
        }
        mainViewModel.memberAppMyPoint()
        mainViewModel.myNextBooking()
    }

    private suspend fun getWristBandData() {
        val endTime = Date()
        val calendar = Calendar.getInstance()
        calendar.setTime(endTime)
        calendar.add(Calendar.DATE, -1)
        val startTime = calendar.time
        calendar.add(Calendar.DATE, -6)
        val oneWeekAgo = calendar.time
        calendar.add(Calendar.MONTH, -3)
        val threeMonthAgo = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startstr = sdf.format(startTime)
        val endstr = sdf.format(endTime)
        val start2str = sdf.format(oneWeekAgo)
        val start3str = sdf.format(threeMonthAgo)
        val accountid = SharedPreferencesUtil.instances.getMemberCode()
        Timber.d("$TAG, getWristBandData(), accountid=$accountid")
        if (accountid != null) {
            mainViewModel.getFootSteps(startstr, endstr, accountid)
            mainViewModel.getHeartRate(start3str, endstr, accountid)
            mainViewModel.getBP(start3str, endstr, accountid)
            mainViewModel.getOxygen(start3str, endstr, accountid)
        }
    }


    private fun initPromotionPager() {
        mainViewModel.bannerList.observe(viewLifecycleOwner) {
            it?.filterNotNull()?.let { data ->
                bannerList.clear()
                bannerList.addAll(data)
                promotionsPagerAdapter?.notifyDataSetChanged()

                if (bannerList.size > 1) {
                    binding.vpPromotions.currentItem = 0
                    binding.ivPromotionsNext.visibility = View.VISIBLE
                }
            }
        }

        promotionsPagerAdapter = PromotionsPagerAdapter(bannerList)
        promotionsPagerAdapter?.promotionItemClick = {
//            it.web_url?.let { url ->
//                openWeb(url, title = it.title)
//            }
//            openWeb(JotangiUtilConstants.WebUrl.DM_VIEWER)
            openWeb(url = "${JotangiUtilConstants.WebUrl.DM_VIEWER}?${"banner_id"}=${(it.banner_id)}")

        }

        binding.vpPromotions.apply {
            adapter = promotionsPagerAdapter
            setShowSideItems(
                resources.getDimension(R.dimen.pageMargin).toInt(),
                resources.getDimension(R.dimen.pagerOffset).toInt()
            )

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    Timber.e("OnPageChangeCallback -> position: $position")

                    if (bannerList.size > 1) {
                        when (position) {
                            0 -> {
                                binding.ivPromotionsPrevious.visibility = View.GONE
                                binding.ivPromotionsNext.visibility = View.VISIBLE
                            }
                            bannerList.size - 1 -> {
                                binding.ivPromotionsPrevious.visibility = View.VISIBLE
                                binding.ivPromotionsNext.visibility = View.GONE
                            }
                            else -> {
                                binding.ivPromotionsPrevious.visibility = View.VISIBLE
                                binding.ivPromotionsNext.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })
        }

        binding.ivPromotionsPrevious.setOnClickListener {
            val currPos: Int = binding.vpPromotions.currentItem
            if (currPos != 0) {
                binding.vpPromotions.currentItem = currPos - 1
            }
        }

        binding.ivPromotionsNext.setOnClickListener {
            val currPos: Int = binding.vpPromotions.currentItem
            if ((currPos + 1) < bannerList.size) {
                binding.vpPromotions.currentItem = currPos + 1
            }
        }
    }

    private fun initProducts() {

        mainViewModel.productList.observe(viewLifecycleOwner) {
            it?.filterNotNull()?.let { data ->
                productList.clear()
                productList.addAll(data)
                homeProductAdapter?.notifyDataSetChanged()
            }
        }

        homeProductAdapter = HomeProductAdapter(productList)
        homeProductAdapter?.productItemClick = {
            openWeb(url = "${JotangiUtilConstants.WebUrl.GOOD_IN}?${encodeProductId(it.product_id)}")
        }
        homeProductAdapter?.productFavClick = {
            if (mActivity?.checkLogin() == true) {
                mainViewModel.editFavorite(it.product_id)
            }
        }

        binding.rvProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            adapter = homeProductAdapter

            val snapHelper = GravitySnapHelper(Gravity.START)
            snapHelper.attachToRecyclerView(this)
        }

        binding.tvProductMore.setOnClickListener {
            mActivity?.navigationMallPage()
        }
    }


}