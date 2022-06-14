package eho.jotangi.com.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import eho.jotangi.com.databinding.FragmentWebviewBinding
import eho.jotangi.com.intentUrl
import eho.jotangi.com.utils.JotangiUtilConstants
import eho.jotangi.com.utils.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.net.URISyntaxException

abstract class BaseWebFragment : BaseFragment() {

    abstract fun onJsSetTitle(title: String? = null)

    private var _binding: FragmentWebviewBinding? = null
    val binding get() = _binding!!

    val skipTitle = listOf("購物車", "填寫付款資料", "輸入信用卡資訊", "預約服務", "訂單明細", "我的票券", "優惠券", "紅利點數")

    var isWebCanGoBack = true

    override fun getToolBar() = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Timber.e("handleOnBackPressed -> isEnabled: $isEnabled")

                    if(binding.webView.canGoBack() && isWebCanGoBack){
                        binding.webView.goBack()
                    }else{
                        isEnabled = false
                        onBackPressed()
                    }
                }
            }
        )
    }

    fun setupWeb(url: String){
        binding.webView.run {
            with(settings){
                javaScriptEnabled = true
                domStorageEnabled = true

            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    Timber.e("shouldOverrideUrlLoading: url = ${request.url}")
                    request.url?.toString()?.let { url ->
                        when{
                            url.startsWith(JotangiUtilConstants.TEL) || url.contains(JotangiUtilConstants.MAPS) -> {
                                intentUrl(
                                    context = requireContext(),
                                    url = url
                                )
                                return true
                            }
                            else -> {
                                return processIntentScheme(view, url)
                            }
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Timber.e("onPageStarted: url = $url")
                    binding.progressBar.visibility = View.VISIBLE
                    when {
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_INDEX) == true -> {
                            setupToolbarHome()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_GOBODY) == true -> {
                            setupToolbarDocument2(GoType.GOBODY)
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_GOBODY_HISTORY) == true -> {
                            setupToolbarWithBack2()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_EECP) == true -> {
                            setupToolbarDocument2(GoType.EECP)
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_EECP_HISTORY) == true -> {
                            setupToolbarWithBack2()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_VESSEL) == true -> {
                            setupToolbarDocument2(GoType.VESSEL)
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_VESSEL_HISTORY) == true -> {
                            setupToolbarWithBack2()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.HEALTH_VESSEL_INTRO) == true -> {
                            setupToolbarWithBack2()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.DM_VIEWER) == true -> {
                            setupToolbarWithBack(isShowLogo = true)
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.SHOPPING_CART) == true -> {
                            setupToolbarWithBack(isShowLogo = true)
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.PRIVACY_POLICY) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.MEMBER) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.TICKET_INDEX) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.MYFV_INDEX) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.BEAN) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.FAQ) == true -> {
                            setupToolbarWithBack3()
                        }
                        url?.contains(JotangiUtilConstants.WebUrl.DESCRIPT_POINT) == true -> {
                            setupToolbarWithBack3()
                        }
                        else -> {
                            setupToolbarHome()
                        }
                    }
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    Timber.e("onPageFinished: url = $url")
                    binding.progressBar.visibility = View.GONE

                    if(url?.contains(JotangiUtilConstants.WebUrl.HEALTH_INDEX) == true){
                        onJsSetTitle()
                    }
                }

                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    Timber.w("shouldInterceptRequest: requestHeaders = ${request?.requestHeaders}")
                    return super.shouldInterceptRequest(view, request)
                }
            }

            webChromeClient = object : WebChromeClient() {

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Timber.w("onConsoleMessage = message: ${consoleMessage?.message()}")
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    Timber.e("onJsBeforeUnload -> $url, $message, $result")
                    return super.onJsBeforeUnload(view, url, message, result)
                }

                override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                    Timber.e("onJsPrompt -> $url, $message, $result")
                    return super.onJsPrompt(view, url, message, defaultValue, result)
                }

                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    Timber.e("onJsAlert -> $url, $message, $result")
                    return super.onJsAlert(view, url, message, result)
                }

                override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    Timber.e("onJsConfirm -> $url, $message, $result")
                    return super.onJsConfirm(view, url, message, result)
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Timber.w("onProgressChanged -> $newProgress")
                    binding.progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                    super.onProgressChanged(view, newProgress)
                }
            }

            loadUrl(url)
            binding.progressBar.visibility = View.VISIBLE

            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun jsCall_Login(accountInfo: String?) { //{"accountinfo":[227,"M"]}
                    Timber.e("jsCall_Login -> accountInfo: $accountInfo")

                    SharedPreferencesUtil.instances.setAccountInfo(accountInfo)

                    try {
                        val jsonObject = JSONObject(accountInfo)
                        val array = jsonObject.getJSONArray("accountinfo")
                        val accountId = array.getInt(0)
                        if (accountId != 0) {
                            SharedPreferencesUtil.instances.setAccountId(accountId.toString())
                        }
                        SharedPreferencesUtil.instances.setAccountName(array.getString(2))

                        GlobalScope.launch(Dispatchers.Main) {
                            mActivity?.refreshMemberInfoOnHome()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                @JavascriptInterface
                fun jsCall_UpdateAccountInfo(){
                    //Timber.e("jsCall_member -> accountInfo: $accountInfo")
                    Timber.e("jsCall_UpdateAccountInfo ->")
                    Log.d("eee","jsCall_UpdateAccountInfo")
                    mainViewModel.getMyMemberInfo()
                }

                @JavascriptInterface
                fun jsCall_getAccountData(): String? {
                    Timber.e("jsCall_getAccountData")
                    return SharedPreferencesUtil.instances.getAccountInfo()
                }

                @JavascriptInterface
                fun jsCall_Location(): String? {
                    val lat = SharedPreferencesUtil.instances.getUserLatitude()
                    val lng = SharedPreferencesUtil.instances.getUserLongitude()
                    Timber.e("jsCall_Location -> $lng,$lat")
                    return "$lng,$lat"
                }

                @JavascriptInterface
                fun jsCall_Back() {
                    Timber.e("jsCall_Back")
                    GlobalScope.launch(Dispatchers.Main) {
                        isWebCanGoBack = false
                        delay(1000)
                        onBackPressed()
                    }
                }

                @JavascriptInterface
                fun jsCall_GoHome() {
//                    val intent = Intent(context, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
                    Timber.e("jsCall_GoHome")
                }

                @JavascriptInterface
                fun jsCall_setShoppingCartCount() {
                    mainViewModel.getShopCartCount()
                    Timber.e("jsCall_setShoppingCartCount")
                }

                @JavascriptInterface
                fun jsCall_addCalendar(data: String?) {
                    Timber.e("jsCall_addCalendar -> data: $data")
                }

                @JavascriptInterface
                fun jsCall_setTitle(title: String?) {
                    Timber.e("jsCall_setTitle -> title_txt: $title")
                    skipTitle.find { it == title }?.let {
                        return
                    }
                    onJsSetTitle(title)
                }

                @JavascriptInterface
                fun jsCall_goWristBandPage() {
                    Timber.e("jsCall_goWristBandPage")
                    GlobalScope.launch(Dispatchers.Main) {
                        findNavController().navigate(WebFragmentDirections.actionNavWebToNavWristband())
                    }
                }

            }, "hamels")
        }
    }

    private fun processIntentScheme(view: WebView, url: String?) : Boolean{
        Timber.e("processIntentScheme(), url= $url")
        return if (url != null) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url)
                return true
            } else if (url.startsWith("line://")) {
                view.context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
                return true
            } else if (url.startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    val existPackage = view.context.packageManager.getLaunchIntentForPackage(
                        intent.getPackage()!!
                    )
                    if (existPackage != null) {
                        startActivity(intent)
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage())
                        startActivity(marketIntent)
                    }
                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (url.startsWith("market://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    intent?.let { startActivity(it) }
                    return true
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            } else { // unhandled url scheme
                view.context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
                return true
            }
            false
        } else {
          false
        }
    }

}