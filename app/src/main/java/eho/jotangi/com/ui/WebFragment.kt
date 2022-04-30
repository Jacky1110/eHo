package eho.jotangi.com.ui

import android.os.Bundle
import android.view.View
import eho.jotangi.com.utils.BundleKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class WebFragment : BaseWebFragment() {
    override fun onJsSetTitle(title: String?) {
        GlobalScope.launch(Dispatchers.Main){
            binding.toolbar.apply {
                title?.let {
                    tvToolTitle.text = title
                    tvToolTitle.visibility = View.GONE
                    ivToolLogo.visibility = View.VISIBLE
                }?: kotlin.run{
                    tvToolTitle.visibility = View.GONE
                    ivToolLogo.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("arguments: ${arguments}")
        arguments?.let {
//            it.getString(BundleKey.TITLE)?.let { title ->
//                setupToolbarWithBack(title = title, isShowLogo = true)
//            }?: setupToolbarWithBack(isShowLogo = true)
            //setupToolbarHome()

            it.getString(BundleKey.URL)?.let { url ->
                setupWeb(url)
            }
        }
    }


}