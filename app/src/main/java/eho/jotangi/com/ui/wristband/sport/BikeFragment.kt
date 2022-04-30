package eho.jotangi.com.ui.wristband.sport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentBikeBinding
import eho.jotangi.com.databinding.FragmentRunBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment


class BikeFragment : BaseFragment() {
    private lateinit var _binding: FragmentBikeBinding
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBikeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)
    }
}