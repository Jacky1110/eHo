package eho.jotangi.com.utils.smartwatch

import android.content.Context
import androidx.work.*
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.Constants.BLEState.Connecting
import com.yucheng.ycbtsdk.YCBTClient
import eho.jotangi.com.utils.SharedPreferencesUtil
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody
import java.util.concurrent.atomic.AtomicBoolean


val flagWorking: AtomicBoolean = AtomicBoolean(false)

open class SmartWatchBaseWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    protected var TAG = javaClass.simpleName
    protected var doneSignal : CountDownLatch? = null
    protected var doneSignal2 : CountDownLatch? = null
    protected var isSuccess = false;
    protected var mac : String? = null
    protected var memberCode : String? = null

    protected val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }

    override fun doWork(): Result {
        Timber.d("$TAG, doWork, start")
        if (!flagWorking.compareAndSet(false, true)) {
            Timber.d("$TAG, doWork, already running")
            return Result.success()
        }

        if (WatchUtils.instance.isBluetoothEnabled()) {
            Timber.d("$TAG, doWork")
            mac = SharedPreferencesUtil.instances.getWatchMac()
            val dur = inputData.getLong(SmartWatchConstants.KEY_WORKER_REPEAT_DURATION, 5)
            Timber.d("$TAG, doWork, mac=$mac, dur=$dur")
            if (mac == null) {
                isSuccess = true
            } else {
                if (!isWatchConnected()) {
                    Timber.d("$TAG, doWork, mac=$mac, watch disconnected")
                    isSuccess = false
                } else {
                    isSuccess = false;
                    memberCode = SharedPreferencesUtil.instances.getMemberCode()

                    getHistoryData()

                }
            }
        } else {
            isSuccess = false
        }
        scheduleNextWorker()
        if (isSuccess) {
            Timber.d("$TAG, doWork success")
            flagWorking.compareAndSet(true, false)
            return Result.success()
        } else {
            Timber.d("$TAG, doWork failure")
            flagWorking.compareAndSet(true, false)
            return Result.failure()
        }
    }


    protected open fun scheduleNextWorker() {
        WatchUtils.instance.initOneTimeWorker(10, TimeUnit.MINUTES)
    }

    protected open fun getHistoryData() {

    }

    protected open fun isWatchConnected() : Boolean {
        isSuccess = false
        val state = YCBTClient.connectState()
        if (state != Constants.BLEState.ReadWriteOK) {
            doneSignal = CountDownLatch(1)
            YCBTClient.disconnectBle()
            YCBTClient.connectBle(mac) { code ->
                Timber.d("$TAG, doConnectDevice(), code=$code")
                if (code == Constants.CODE.Code_OK) {
                    isSuccess = true
                } else {
                }
                doneSignal!!.countDown()
            }
            doneSignal!!.await(5,TimeUnit.SECONDS)
        } else {
            isSuccess = true
        }
        return isSuccess
    }

    protected open fun delHistoryDataOnWatch(type : Int) : Boolean {
        Timber.d("$TAG, delHistoryDataOnWatch(), type=$type")
        isSuccess = false;
        doneSignal = CountDownLatch(1)
        YCBTClient.deleteHealthHistoryData(
            type
        ) { code, status, hashMap ->
            Timber.d("$TAG, delHistoryDataOnWatch(), type=$type, code=$code")
            if (code == Constants.CODE.Code_OK) { //delete success
                isSuccess = true;
            }
            doneSignal?.countDown()
        }
        doneSignal!!.await(5,TimeUnit.SECONDS)

        return isSuccess
    }

    override fun onStopped() {
        super.onStopped()
        Timber.d("$TAG, onStopped")
        flagWorking.compareAndSet(true, false)
    }
}