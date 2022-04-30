package eho.jotangi.com

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64.encodeToString
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import eho.jotangi.com.utils.JotangiUtilConstants
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

/**
 *Created by Luke Liu on 2021/8/17.
 */

var isLaunch = true
var currentPhotoPath: String? = null

fun showToast(ctx: Context, content: String?){
    content?.let {
        Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
    }
}

fun showDefaultToast(ctx: Context){
    Toast.makeText(ctx, ctx.getString(R.string.error_default), Toast.LENGTH_SHORT).show()
}

fun showDlgConfirm(ctx: Context, title: String? = null, msg: String, cancelable: Boolean = true) {
    AlertDialog.Builder(ctx).apply {
        title?.let { setTitle(title) }
        setMessage(msg)
        setNegativeButton(R.string.confirm) { dialog, _ ->
            dialog.dismiss()
        }
        setCancelable(cancelable)
    }.create().show()
}

fun showDialogTwoButton(
    activity: Activity,
    dialogMsg: String,
    left_res: String = activity.getString(R.string.confirm),
    left_listener: DialogInterface.OnClickListener,
    right_res: String = activity.getString(R.string.cancel),
    right_listener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dlg, _ -> dlg.dismiss() }
)
{
    if (!activity.isFinishing) {
        AlertDialog.Builder(activity).apply {
            setMessage(dialogMsg)
            setCancelable(false)
            setNegativeButton(left_res, left_listener)
            setPositiveButton(right_res, right_listener)
        }.create().show()
    }
}

fun ViewPager2.setShowSideItems(pageMarginPx : Int, offsetPx : Int) {

    clipToPadding = false
    clipChildren = false
    offscreenPageLimit = 3

    setPageTransformer { page, position ->

        val offset = position * -(2 * offsetPx + pageMarginPx)
        if (this.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.translationX = -offset
            } else {
                page.translationX = offset
            }
        } else {
            page.translationY = offset
        }
    }

}

fun dialogImage(activity: Activity) {
    val builder = AlertDialog.Builder(activity)
    with(builder)
    {
        setTitle(context.getString(R.string.choose_one_photo))
        setItems(arrayOf(context.getString(R.string.go_album), context.getString(R.string.open_camera))) { dialog, which ->
            when (which) {
                0 -> pickFromGallery(activity)
                1 -> checkCameraPermission(activity)
            }
            dialog.dismiss()
        }
        show()
    }
}

fun pickFromGallery(activity: Activity) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
        .setType("image/*")
        .addCategory(Intent.CATEGORY_OPENABLE)
    val mimeTypes =
        arrayOf("image/jpeg", "image/png")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    activity.startActivityForResult(intent, JotangiUtilConstants.PHOTO_FROM_GALLERY)
}

fun checkCameraPermission(activity: Activity) {
    val permissions = arrayOf(
        android.Manifest.permission.CAMERA
    )

    if (hasPermissions(activity, *permissions)) {
        dispatchTakePictureIntent(activity)
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                activity, permissions,
                JotangiUtilConstants.PERMISSION_CAMERA_REQUEST_CODE
            )
        }
    }
}

private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
    }
    return true
}

fun dispatchTakePictureIntent(activity: Activity) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        try {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile(activity)
            } catch (ex: IOException) {
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    activity,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activity.startActivityForResult(takePictureIntent, JotangiUtilConstants.PHOTO_FROM_CAMERA)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.error_camera, Toast.LENGTH_SHORT).show()
        }
    }
}

@Throws(IOException::class)
fun copyFile(context: Context, uri: Uri, onSuccess: () -> Unit) {
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(createImageFile(context)).use { output ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
            onSuccess.invoke()
        }
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "$timeStamp", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
}

fun encodeProductId(productId: String?): String{
    productId?.let {
        URLEncoder.encode("productId=$productId&showCartBtn=Y", "UTF-8")?.let { urlEncode ->
            encodeToString(urlEncode.toByteArray(), android.util.Base64.DEFAULT)?.let { base64Encode ->
                Timber.e("encodeProductId -> productId: $productId, urlEncode: $urlEncode, base64Encode: $base64Encode")
                return base64Encode
            }
        }
    }
    return ""
}

fun intentUrl(context: Context, url: String, onFailure: () -> Unit = { }) {
    Timber.e("Intent Url -> $url")
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.parse(url)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onFailure.invoke()
    }
}