package eho.jotangi.com.utils.smartwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eho.jotangi.com.databinding.FragmentNotificationSettingBinding
import eho.jotangi.com.databinding.ToolbarBinding
import eho.jotangi.com.ui.BaseFragment
import eho.jotangi.com.utils.SharedPreferencesUtil

class NotificationSettingFragment :BaseFragment(){
    private var _binding:FragmentNotificationSettingBinding?=null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding? = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationSettingBinding.inflate(inflater,container,false)
        binding.apply {
            switchPhone.apply {
                var key = "phone"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchMsg.apply {
                var key = "msg"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchMail.apply {
                var key = "mail"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchWeChat.apply {
                var key = "wechat"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchQQ.apply {
                var key = "qq"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchWeibo.apply {
                var key = "weibo"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchFB.apply {
                var key = "facebook"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchTwitter.apply {
                var key = "twitter"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchMessenger.apply {
                var key = "messenger"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchWhatsAPP.apply {
                var key = "whatsapp"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchLinkedIn.apply {
                var key = "linkedin"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchInstagram.apply {
                var key = "instagram"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchSkype.apply {
                var key = "skype"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchLine.apply {
                var key = "line"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchSnapchat.apply {
                var key = "snapchat"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
            switchAppNotif.apply {
                var key = "appnotif"
                isChecked = getStatus(key)
                setOnClickListener {
                    setStatus(key)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithBack(isShowLogo = true)

    }

    private fun getStatus(key:String):Boolean{
        return SharedPreferencesUtil.instances.getNotifyStatus(key)
    }

    private fun setStatus(key:String){
        SharedPreferencesUtil.instances.setNotifyStatus(key)
    }

}