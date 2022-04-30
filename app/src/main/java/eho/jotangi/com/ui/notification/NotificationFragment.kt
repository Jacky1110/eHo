package eho.jotangi.com.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import eho.jotangi.com.R
import eho.jotangi.com.databinding.FragmentNotificationBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.utils.model.NotificationObj

class NotificationFragment : BaseFragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter

    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithBack(R.string.notification, isShowLogo = true)
        initNotificationList()
    }

    private fun initNotificationList(){
        //TODO Testing
        val lists = arrayListOf<NotificationObj>()
        /*for(i in 10..50 ) {
            lists.add(
                NotificationObj(
                    title = "$i${getString(R.string.text_10)}",
                    content = getString(R.string.text_100),
                    isRead = i > 30,
                    time = "2021-10-20 12:$i"
                )
            )
        }*/

        binding.notfiyEmpty.apply {
            visibility = if(lists.size == 0)
                View.VISIBLE
            else
                View.INVISIBLE
        }

        notificationAdapter = NotificationAdapter(lists)
        notificationAdapter.notificationItemClick = {
            findNavController().navigate(NotificationFragmentDirections.actionNavNotificationToNavNotificationDetail(it))
        }

        binding.rvNotification.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }
}