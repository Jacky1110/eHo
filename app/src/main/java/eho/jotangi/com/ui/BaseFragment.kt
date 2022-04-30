package eho.jotangi.com.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eho.jotangi.com.MainActivity
import eho.jotangi.com.MainViewModel
import eho.jotangi.com.R
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.wristband.Oxy.OxyFragmentDirections
import eho.jotangi.com.ui.wristband.bp.BpFragmentDirections
import eho.jotangi.com.ui.wristband.ecg.EcgFragmentDirections
import eho.jotangi.com.ui.wristband.footstep.FootStepFragmentDirections
import eho.jotangi.com.ui.wristband.heartrate.HeartRateAllDayFragmentDirections
import eho.jotangi.com.ui.wristband.heartrate.HeartRateFragmentDirections
import eho.jotangi.com.ui.wristband.sleep.SleepFragmentDirections
import eho.jotangi.com.utils.JotangiUtilConstants
import eho.jotangi.com.utils.SharedPreferencesUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class BaseFragment : Fragment() {
    protected var TAG = javaClass.simpleName

    abstract fun getToolBar(): ToolbarBinding?

    var mActivity: MainActivity? = null
    val mainViewModel: MainViewModel by viewModel()

    enum class GoType {
        FOOTDETAIL, SLEEP, HEARTRATE, HEARTRATE_ALLDAY, BPDETAL, OXYDETAIL, ECG,
        GOBODY, EECP, VESSEL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            mActivity = (activity as MainActivity)
        } catch (e: ClassCastException) {
            Timber.e("$e")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToolBar()?.apply {
            ivToolLogo.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.nav_home)
                }
            }
        }
    }

    fun setShoppingCartNumber(num: Int?) {
        getToolBar()?.apply {
            tvToolNumber.apply {
                setBadgeCount(num ?: 0)
            }
        }
    }

    fun setupToolbarWithBack(
        titleRes: Int? = null,
        title: String? = null,
        isShowLogo: Boolean = false
    ) {
        getToolBar()?.apply {
            tvToolTitle.text = titleRes?.let { getString(it) } ?: title
            ivToolBtn1.visibility = View.GONE
            ivToolBtn2.visibility = View.GONE
            tvToolNumber.visibility = View.GONE
            llRecommendedCode.apply {
                visibility = View.GONE
                setOnClickListener {
                    mActivity?.openRecommendedCode()
                }
            }
            ivToolBack.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    mActivity?.onBackPressed()
                }
            }
            if (isShowLogo) {
                tvToolTitle.visibility = View.GONE
                ivToolLogo.visibility = View.VISIBLE
            }
        }
    }

    fun setupToolbarWithBack2(
    ) {
        getToolBar()?.apply {
            ivToolBtn1.visibility = View.GONE
            ivToolBtn2.visibility = View.GONE
            ivToolLogo.visibility = View.VISIBLE
            llRecommendedCode.apply {
                visibility = View.GONE
                setOnClickListener {
                    mActivity?.openRecommendedCode()
                }
            }
            tvToolNumber.visibility = View.GONE
            ivToolBack.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    mActivity?.onBackPressed()
                }
            }
        }
    }

    fun setupToolbarWithBack3(
    ) {
        getToolBar()?.apply {
            ivToolBtn1.visibility = View.VISIBLE
            ivToolBtn2.visibility = View.VISIBLE
            setupToolbarBtn(ivToolBtn1, R.drawable.ic_bell) {
                mActivity?.openNotification()
            }

            setupToolbarBtn(ivToolBtn2, R.drawable.ic_shopping_cart) {
                mActivity?.openCart()
            }
            if (SharedPreferencesUtil.instances.isLogin())
                tvToolNumber.visibility = View.VISIBLE

            ivToolLogo.visibility = View.VISIBLE
            llRecommendedCode.apply {
                visibility = View.GONE
                setOnClickListener {
                    mActivity?.openRecommendedCode()
                }
            }
            ivToolBack.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    mActivity?.onBackPressed()
                }
            }

            if (view != null) {
                mainViewModel.numberShoppingCart.observe(viewLifecycleOwner, {
                    Timber.e("numberShoppingCart: $it")
                    setShoppingCartNumber(it)
                })
            }
        }
    }

    fun setupToolbarDocument(type: GoType) {
        getToolBar()?.apply {
            setupToolbarBtn(ivToolBtn1, R.drawable.ic_document) {
                when (type) {
                    GoType.FOOTDETAIL -> findNavController().navigate(FootStepFragmentDirections.actionNavFootstepToNavFootstepDetail())
                    GoType.SLEEP -> findNavController().navigate(SleepFragmentDirections.actionNavSleepToNavSleepDetail())
                    GoType.HEARTRATE -> findNavController().navigate(HeartRateFragmentDirections.actionNavHeartRateToNavHeartRateDetail())
                    GoType.HEARTRATE_ALLDAY -> findNavController().navigate(
                        HeartRateAllDayFragmentDirections.actionNavHeartRateAllDayToNavHeartRateAllDayDetail()
                    )
                    GoType.BPDETAL -> findNavController().navigate(BpFragmentDirections.actionNavBpToNavBpDetail())
                    GoType.OXYDETAIL -> findNavController().navigate(OxyFragmentDirections.actionNavOxyToNavOxyDetail())
                    GoType.ECG -> findNavController().navigate(EcgFragmentDirections.actionNavEcgToNavEcgDetail())
                }
            }
        }
    }

    fun setupToolbarDocument2(type: GoType) {
        getToolBar()?.apply {
            ivToolBtn2.visibility = View.GONE
            llRecommendedCode.apply {
                visibility = View.GONE
                setOnClickListener {
                    mActivity?.openRecommendedCode()
                }
            }
            tvToolNumber.visibility = View.GONE
            ivToolBack.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    mActivity?.onBackPressed()
                }
            }
            setupToolbarBtn(ivToolBtn1, R.drawable.ic_document) {
                when (type) {
                    GoType.EECP -> openWeb(JotangiUtilConstants.WebUrl.HEALTH_EECP_HISTORY)
                    GoType.GOBODY -> openWeb(JotangiUtilConstants.WebUrl.HEALTH_GOBODY_HISTORY)
                    GoType.VESSEL -> openWeb(JotangiUtilConstants.WebUrl.HEALTH_VESSEL_HISTORY)
                }
            }
        }
    }

    fun setupToolbarHome() {
        getToolBar()?.apply {
            ivToolLogo.visibility = View.VISIBLE
            if (SharedPreferencesUtil.instances.isLogin())
                tvToolNumber.visibility = View.VISIBLE

            llRecommendedCode.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    mActivity?.openRecommendedCode()
                }
            }

            ivToolBack.visibility = View.GONE

            setupToolbarBtn(ivToolBtn1, R.drawable.ic_bell) {
                mActivity?.openNotification()
            }

            setupToolbarBtn(ivToolBtn2, R.drawable.ic_shopping_cart) {
                mActivity?.openCart()
            }
            if (view != null) {
                mainViewModel.numberShoppingCart.observe(viewLifecycleOwner, {
                    Timber.e("numberShoppingCart: $it")
                    setShoppingCartNumber(it)
                })
            }
        }
    }

    fun setupToolbarBtn(iv: ImageView?, res: Int?, onClick: () -> Unit) {
        iv?.apply {
            visibility = View.VISIBLE
            res?.let { setImageResource(it) }
            setOnClickListener {
                onClick.invoke()
            }
        }
    }

    fun onBackPressed() {
        mActivity?.onBackPressed()
    }

    fun openWeb(url: String, title: String? = null, titleRes: Int? = null) {
        if (url.isEmpty())
            return

        val mTitle = titleRes?.let { getString(it) } ?: title
        mActivity?.openWeb(url, mTitle)
    }
}
