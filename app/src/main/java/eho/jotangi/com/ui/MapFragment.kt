package eho.jotangi.com.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.ktx.awaitMap
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentMapBinding

class MapFragment : BaseFragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun getToolBar() = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarHome()

        val mapFragment = parentFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        lifecycleScope.launchWhenCreated {
            val googleMap = mapFragment?.awaitMap()
        }
    }
}