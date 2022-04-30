package eho.jotangi.com.utils.smartwatch

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.*
import com.yucheng.ycbtsdk.YCBTClient
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WatchUtils private constructor() {
    private val KEY_SYNC_WATCH_WORKER_NAME = "watchperiod"
    private lateinit var appContext : Context

    companion object {
        val instance by lazy { WatchUtils() }
    }

    fun init(context: Context) {
        appContext = context
    }

    fun isBluetoothEnabled() : Boolean {
        val bleAdapter = BluetoothAdapter.getDefaultAdapter()
        return (bleAdapter !=null && bleAdapter.isEnabled)
    }

    fun initWatchSDK() {
        YCBTClient.initClient(appContext,true)
    }

    fun cancelAllWorker() {
        WorkManager.getInstance(appContext)
            .cancelAllWorkByTag(KEY_SYNC_WATCH_WORKER_NAME)
    }

    fun cancelPeriodWorker() {
        WorkManager.getInstance(appContext)
            .cancelUniqueWork(KEY_SYNC_WATCH_WORKER_NAME)
    }

    fun initPeriodWorker() {
        Timber.d("initPeriodWorker()")
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val periodRequest = PeriodicWorkRequest.Builder(
            SmartWatchWalkDataWorker::class.java,
            3, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(appContext)
            .enqueueUniquePeriodicWork(
                KEY_SYNC_WATCH_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodRequest)
    }

    fun initOneTimeWorker(delay:Long, unit: TimeUnit) {
        Timber.d("initOneTimeWorker()")
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequest.Builder(SmartWatchWalkDataWorker::class.java)
                //.setInputData(
                //    Data.Builder()
                //        .putBoolean(SmartWatchConstants.KEY_WORKER_REPEAT, repeat)
                //        .putLong(SmartWatchConstants.KEY_WORKER_REPEAT_DURATION, 2)
                //        .build()
                //)
                .addTag(KEY_SYNC_WATCH_WORKER_NAME)
                .setInitialDelay(delay, unit)
                .build()
        WorkManager
            .getInstance(appContext)
            .enqueue(uploadWorkRequest)

    }

    @SuppressLint("ServiceCast")
    fun isNetWorkOnline(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Timber.d("Internet, NetworkCapabilities.TRANSPORT_CELLULAR")
                return 1
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Timber.d("Internet, NetworkCapabilities.TRANSPORT_WIFI")
                return 2
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Timber.d("Internet, NetworkCapabilities.TRANSPORT_ETHERNET")
                return 3
            }
        }
        return 0
    }

    fun clipTimeFormatSecond(timestr:String?):String {
        try {
            timestr?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val t1 = sdf.parse(timestr)
                t1?.let {
                    return sdf2.format(t1)
                }
            }
        } catch (ex:Exception) {

        }
        return "-"
    }
}