package eho.jotangi.com.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentNotificationDetailBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment

class NotificationDetailFragment : BaseFragment() {

    private val args: NotificationDetailFragmentArgs by navArgs()
    private var _binding: FragmentNotificationDetailBinding? = null
    private val binding get() = _binding!!

    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBack(R.string.notification, isShowLogo = true)

        args.notification?.apply {
            binding.tvTitle.text = title
            binding.tvContent.text = content
        }
    }
}