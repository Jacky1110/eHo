package eho.jotangi.com.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jotangi.baseutils.BitmapUtils
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import eho.jotangi.com.*
import eho.jotangi.com.databinding.FragmentMemberBinding
import eho.jotangi.com.utils.JotangiUtilConstants
import eho.jotangi.com.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MemberFragment : BaseFragment() {

    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!

    private var bitmap: Bitmap? = null

    override fun getToolBar() = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarHome()

        binding.apply {
            sivPhoto.setOnClickListener {
                getUserHeadImage()
                //mActivity?.let { dialogImage(it) }
            }

            ivTabReservationRecord.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.RESERVATION_RECORD)
            }

            ivTabMeasurementRecord.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.HEALTH_INDEX)
            }

            ivTabOrder.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.ORDER_INDEX)
            }

            ivTabFavorite.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.MY_FAVORITE)
            }

            tvMemberInfo.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.MEMBER)
            }
            tvPassword.setOnClickListener{
                openWeb(JotangiUtilConstants.WebUrl.PASSWORD)
            }

            tvCoupon.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.COUPON)
            }

            tvRewardRecord.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.BEAN)
            }

            tvWatchSetting.setOnClickListener {
                val mac = SharedPreferencesUtil.instances.getWatchMac()
                if (mac == null || YCBTClient.connectState() != Constants.BLEState.ReadWriteOK) {
                    findNavController().navigate(MemberFragmentDirections.actionNavMemberToNavWatchScan())
                } else {
                    findNavController().navigate(MemberFragmentDirections.actionNavMemberToNavWatchInfo())
                }
            }

            tvTermsPolicy.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.PRIVACY_POLICY)
            }

            tvMyTicket.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.TICKET_INDEX)
            }

            tvMyFv.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.MYFV_INDEX)
            }

            tvFaq.setOnClickListener {
                openWeb(JotangiUtilConstants.WebUrl.FAQ)
            }

            tvLogout.setOnClickListener {
                mActivity?.logout()
                mActivity?.backHome()
            }

            tvVersion.text = BuildConfig.VERSION_NAME
        }

        mainViewModel.headshotPath.observe(viewLifecycleOwner, { path ->
            //Glide.with(requireContext()).load(path?:R.drawable.ic_img_user).into(binding.sivPhoto)
            reloadUserHeadShot(path)

        })

        mainViewModel.userName.observe(viewLifecycleOwner, { name ->
            binding.tvName.text = name
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun reloadUserHeadShot(path:String?) {
        Glide.with(requireContext()).load(path?:R.drawable.ic_img_user).into(binding.sivPhoto)

        if (path != null) {
            bitmap = BitmapUtils.loadAndRotateBitmap(path)
            binding.sivPhoto.setImageBitmap(bitmap)

        //4/19新增 by Jacky
            val bmap: Bitmap = BitmapUtils.loadAndRotateBitmap(path)
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.userPic(
                    bitmapToFile(bmap)
                )
                //4/26新增
                mainViewModel.userPicMessage.observe(viewLifecycleOwner, { data ->
                    mainViewModel.editFavorite(SharedPreferencesUtil.instances.getAccountName())
                })
            }


        } else {
            binding.sivPhoto.setImageResource(R.drawable.ic_img_user)
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val wrapper = ContextWrapper(context)
        var file: File? = null

        //Initialize a new file instance to save bitmap object
        file = wrapper.getDir("Image", Context.MODE_PRIVATE)
        file.createNewFile()
        file = File(file, "${UUID.randomUUID()}.jpg")
        Log.i("bitmapToFile", file.name)

        try {
           //Compress the bitmap and save in jpg format/ 壓縮圖轉為JPEG
           val stream: OutputStream = FileOutputStream(file)
           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
           stream.flush()
           stream.close()
        } catch ( e: IOException){
        } finally {
            file
        }
        return file
    }


    var getUserHeadLuncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        Timber.d("onActivityResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val path = data.getStringExtra("path")
                SharedPreferencesUtil.instances.setAccountHeadShot(path)
                mainViewModel.refreshHeadShotPath()
                if (path != null) {
                    reloadUserHeadShot(path)
                }
            }
        }
    }

    private fun getUserHeadImage() {
        val intent = Intent(context, CropHeadImageActivity::class.java)
        getUserHeadLuncher.launch(intent)
    }


    private fun dialogImage(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            val items =
                if (mainViewModel.headshotPath.value.isNullOrEmpty())
                    arrayOf(getString(R.string.go_album), getString(R.string.open_camera))
                else
                    arrayOf(getString(R.string.go_album), getString(R.string.open_camera), getString(R.string.delete_photo))

            setTitle(context.getString(R.string.choose_one_photo))
            setItems(items) { dialog, which ->
                when (which) {
                    0 -> pickFromGallery(activity)
                    1 -> checkCameraPermission(activity)
                    2 -> {
                        SharedPreferencesUtil.instances.setAccountHeadShot(null)
                        mainViewModel.refreshHeadShotPath()
                    }
                }
                dialog.dismiss()
            }
            show()
        }
    }
}

