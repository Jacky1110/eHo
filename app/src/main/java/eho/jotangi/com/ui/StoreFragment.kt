package eho.jotangi.com.ui

import android.os.Bundle
import android.view.View
import eho.jotangi.com.utils.JotangiUtilConstants

class StoreFragment : BaseWebFragment() {
    override fun onJsSetTitle(title: String?) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarHome()
        setupWeb(JotangiUtilConstants.WebUrl.RESERVATION_INDEX)
    }
}