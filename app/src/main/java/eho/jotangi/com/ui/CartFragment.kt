package eho.jotangi.com.ui

import android.os.Bundle
import android.view.View
import eho.jotangi.com.utils.JotangiUtilConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartFragment : BaseWebFragment() {
    override fun onJsSetTitle(title: String?) {
        GlobalScope.launch(Dispatchers.Main){
            binding.toolbar.apply {
                tvToolTitle.text = title
                tvToolTitle.visibility = View.GONE
                ivToolLogo.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBack(isShowLogo = true)
        setupWeb(JotangiUtilConstants.WebUrl.SHOPPING_CART)
    }
}