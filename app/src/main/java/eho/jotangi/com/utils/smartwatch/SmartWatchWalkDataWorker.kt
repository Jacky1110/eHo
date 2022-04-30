package eho.jotangi.com.utils.smartwatch

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.gson.Gson
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import eho.jotangi.com.utils.SharedPreferencesUtil
import eho.jotangi.com.utils.smartwatch.apirequest.*
import eho.jotangi.com.utils.smartwatch.watchdatamodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


open class SmartWatchWalkDataWorker(context: Context, workerParams: WorkerParameters) :
    SmartWatchBaseWorker(context, workerParams) {

    override fun getHistoryData() {
        Timber.d("$TAG, getHistoryData(), start")
        doneSignal2 = CountDownLatch(1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getThenUploadWalkHistoryData()
                //getSportHistoryData()
                getThenUploadSleepHistoryData()
                getThenUploadHeartRateHistoryData()
                getThenUploadBloodBPHistoryData()
                getThenUploadHealthHistoryData()
                //getThenUploadOxygenHistoryData()
                doneSignal2!!.countDown()
                Timber.d("$TAG, getHistoryData(), scope end")
            }catch (e:Exception){
                Log.d("eeee",e.toString())
                doneSignal2!!.countDown()
                isSuccess = false
            }
        }
        doneSignal2!!.await()
        Timber.d("$TAG, getHistoryData(), end")
    }

    /**
     * getThenUploadWalkHistoryData()
     */
    suspend fun getThenUploadWalkHistoryData() {
        val datalist = getWalkHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                uploadWalkHistoryData(datalist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x540)
            }
        }
    }
    private fun getWalkHistoryData() : List<HistorySportInfo>? {
        Timber.d("$TAG, getWalkHistory")
        var datalist: List<HistorySportInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistorySport
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getWalkHistory succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getWalkHistory, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historySportInfoListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getWalkHistory succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getWalkHistory, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }

    private fun getSportHistoryData() : List<HistorySportInfo>? {
            Timber.d("$TAG, getSportHistoryData")
        val datalist: List<HistorySportInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            0x2d//Constants.DATATYPE.Health_HistorySport
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getSportHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getSportHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        //datalist = YCBTDataHelper.historySportInfoListFromObject(response.data)
                        //if (datalist != null) {
                        //    isSuccess = true;
                        //}
                    }
                } else {
                    Timber.d("$TAG, getSportHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getSportHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await()

        return datalist
    }

    @SuppressLint("CheckResult")
    private suspend fun uploadWalkHistoryData(datalist : List<HistorySportInfo>?) : Boolean {
        if (datalist == null) return true;

        Timber.d("$TAG, uploadWalkHistoryData start")
        isSuccess = true
        val lasttime = SharedPreferencesUtil.instances.getWatchLongValue(SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_STEP_TIME)
        var newlasttime = 0L
        datalist.forEach {
            item ->
            if (item.sportStartTime > lasttime) {
                Timber.d("$TAG, uploadWalkHistoryData finish, $item")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val starttime = sdf.format(Date(item.sportStartTime))
                val endtime = sdf.format(Date(item.sportEndTime))

                val resp = apiRepository.stepUpload(
                    StepRequest(
                        memberCode,
                        starttime, endtime, item.sportStep, item.sportCalorie, item.sportDistance
                    )
                )
                Timber.d("$TAG, uploadWalkHistoryData finish, $item, resp=$resp")

                if (!resp.code.equals(SmartWatchConstants.WATCH_API_RESP_OK)) isSuccess = false
                else {
                    if (item.sportStartTime > newlasttime) newlasttime = item.sportStartTime
                }
            }
        }
        if (newlasttime > lasttime) {
            SharedPreferencesUtil.instances.setWatchLongValue(
                SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_STEP_TIME,
                newlasttime)
        }
        Timber.d("$TAG, uploadWalkHistoryData end")
        return isSuccess
    }

    /**
     * getThenUploadSleepHistoryData
     */
    suspend fun getThenUploadSleepHistoryData() {
        val datalist = getSleepHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                isSuccess = uploadSleepHistoryData(datalist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x541)
            }
        }
    }

    private fun getSleepHistoryData() : List<HistorySleepInfo>? {
        Timber.d("$TAG, getSleepHistoryData")
        var datalist: List<HistorySleepInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistorySleep
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getSleepHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getSleepHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historySleepInfoListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getSleepHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getSleepHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }

    @SuppressLint("CheckResult")
    private suspend fun uploadSleepHistoryData(datalist : List<HistorySleepInfo>?) : Boolean {
        if (datalist == null) return true;

        Timber.d("$TAG, uploadSleepHistoryData start")
        isSuccess = true
        val lasttime = SharedPreferencesUtil.instances.getWatchLongValue(
            SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_SLEEP_TIME)
        var newlasttime = 0L
        datalist.forEach {
                item ->
            if (item.startTime > lasttime) {
                Timber.d("$TAG, uploadSleepHistoryData finish, $item")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val starttime = sdf.format(Date(item.startTime))
                val endtime = sdf.format(Date(item.endTime))
                val sleepdata = Gson().toJson(item.sleepData)

                //doneSignal = CountDownLatch(1)
                val resp = apiRepository.sleepUpload(
                    SleepRequest(
                        memberCode,
                        starttime, endtime,
                        item.deepSleepCount,
                        item.lightSleepCount,
                        item.deepSleepTotal,
                        item.lightSleepTotal,
                        sleepdata
                    )
                )
                Timber.d("$TAG, uploadSleepHistoryData finish, $item, resp=$resp")

                if (!resp.code.equals(SmartWatchConstants.WATCH_API_RESP_OK)) isSuccess = false
                else {
                    if (item.startTime > newlasttime) newlasttime = item.startTime
                }
            }
        }
        if (newlasttime > lasttime) {
            SharedPreferencesUtil.instances.setWatchLongValue(
                SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_SLEEP_TIME,
                newlasttime)
        }
        Timber.d("$TAG, uploadSleepHistoryData end")

        return isSuccess
    }

    /**
     * getThenUploadHeartRateHistoryData
     */
    suspend fun getThenUploadHeartRateHistoryData() {
        val datalist = getHeartRateHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                uploadHeartRateHistoryData(datalist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x542)
            }
        }
    }

    private fun getHeartRateHistoryData() : List<HistoryHeartInfo>? {
        Timber.d("$TAG, getHeartRateHistoryData")
        var datalist: List<HistoryHeartInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistoryHeart
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getHeartRateHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getHeartRateHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historyHeartInfosListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getHeartRateHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getHeartRateHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }

    @SuppressLint("CheckResult")
    private suspend fun uploadHeartRateHistoryData(datalist : List<HistoryHeartInfo>?) : Boolean {
        if (datalist == null) return true;

        Timber.d("$TAG, uploadHeartRateHistoryData start")
        isSuccess = true
        val lasttime = SharedPreferencesUtil.instances.getWatchLongValue(
            SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_HR_TIME)
        var newlasttime = 0L
        datalist.forEach {
                item ->
            if (item.heartStartTime > lasttime) {
                Timber.d("$TAG, uploadHeartRateHistoryData finish, $item")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val starttime = sdf.format(Date(item.heartStartTime))
                //doneSignal = CountDownLatch(1)
                val resp = apiRepository.heartRateUpload(
                    HeartRateRequest(
                        memberCode,
                        starttime, item.heartValue
                    )
                )
                Timber.d("$TAG, uploadHeartRateHistoryData finish, $item, resp=$resp")

                if (!resp.code.equals(SmartWatchConstants.WATCH_API_RESP_OK)) isSuccess = false
                else {
                    if (item.heartStartTime > newlasttime) newlasttime = item.heartStartTime
                }
            }
        }
        if (newlasttime > lasttime) {
            SharedPreferencesUtil.instances.setWatchLongValue(
                SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_HR_TIME,
                newlasttime)
        }
        Timber.d("$TAG, uploadHeartRateHistoryData end")

        return isSuccess
    }

    /**
     * getThenUploadBloodBPHistoryData
     */
    suspend fun getThenUploadBloodBPHistoryData() {
        val datalist = getBloodBPHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                uploadBloodBPHistoryData(datalist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x543)
            }
        }
    }

    private fun getBloodBPHistoryData() : List<HistoryBloodBPInfo>? {
        Timber.d("$TAG, getBloodBPHistoryData")
        var datalist: List<HistoryBloodBPInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistoryBlood
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getBloodBPHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getBloodBPHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historyBloodInfosListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getBloodBPHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getBloodBPHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }

    @SuppressLint("CheckResult")
    private suspend fun uploadBloodBPHistoryData(datalist : List<HistoryBloodBPInfo>?) : Boolean {
        if (datalist == null) return true;

        Timber.d("$TAG, uploadBloodBPHistoryData start")
        isSuccess = true
        val lasttime = SharedPreferencesUtil.instances.getWatchLongValue(
            SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_BP_TIME)
        var newlasttime = 0L
        datalist.forEach {
                item ->
            if (item.bloodStartTime > lasttime) {
                Timber.d("$TAG, uploadBloodBPHistoryData, $item")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val starttime = sdf.format(Date(item.bloodStartTime))
                //doneSignal = CountDownLatch(1)
                val resp = apiRepository.bpUpload(
                    BPRequest(
                        memberCode,
                        starttime, item.bloodDBP, item.bloodSBP
                    )
                )
                Timber.d("$TAG, uploadBloodBPHistoryData finish, $item, resp=$resp")

                if (!resp.code.equals(SmartWatchConstants.WATCH_API_RESP_OK)) isSuccess = false
                else {
                    if (item.bloodStartTime > newlasttime) newlasttime = item.bloodStartTime
                }
            }
        }
        if (newlasttime > lasttime) {
            SharedPreferencesUtil.instances.setWatchLongValue(
                SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_BP_TIME,
                newlasttime)
        }
        Timber.d("$TAG, uploadBloodBPHistoryData end")

        return isSuccess
    }

    /**
     * getThenUploadOxygenHistoryData
     */
    suspend fun getThenUploadOxygenHistoryData() {
        val datalist = getOxygenHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                uploadOxygenHistoryData(datalist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x544)
            }
        }
    }

    private fun getOxygenHistoryData() : List<HistoryOxygenInfo>? {
        Timber.d("$TAG, getOxygenHistoryData")
        var datalist: List<HistoryOxygenInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistoryBloodOxygen
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getOxygenHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getOxygenHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historyOxyInfosListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getOxygenHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getOxygenHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }

    @SuppressLint("CheckResult")
    private suspend fun uploadOxygenHistoryData(datalist : List<HistoryOxygenInfo>?) : Boolean {
        if (datalist == null) return true;
        Timber.d("$TAG, uploadOxygenHistoryData start")

        isSuccess = true
        val lasttime = SharedPreferencesUtil.instances.getWatchLongValue(
            SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_OXYGEN_TIME)
        var newlasttime = 0L
        datalist.forEach {
                item ->
            if (item.startTime > lasttime) {
                Timber.d("$TAG, uploadOxygenHistoryData, $item")

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val starttime = sdf.format(Date(item.startTime))
                //doneSignal = CountDownLatch(1)
                val resp = apiRepository.oxygenUpload(
                    OxygenRequest(
                        memberCode,
                        starttime, item.ooValue
                    )
                )
                Timber.d("$TAG, uploadOxygenHistoryData finish, $item, resp=$resp")

                if (!resp.code.equals(SmartWatchConstants.WATCH_API_RESP_OK)) isSuccess = false
                else {
                    if (item.startTime > newlasttime) newlasttime = item.startTime
                }
            }
        }
        if (newlasttime > lasttime) {
            SharedPreferencesUtil.instances.setWatchLongValue(
                SharedPreferencesUtil.instances.KEY_LAST_UPLOADD_OXYGEN_TIME,
                newlasttime)
        }
        Timber.d("$TAG, uploadOxygenHistoryData end")

        return isSuccess
    }

    private suspend fun getThenUploadHealthHistoryData() {
        val datalist = getHealthHistoryData();

        if (memberCode == null) return

        if (isSuccess) {
            if (datalist != null) {
                val oxygenlist: MutableList<HistoryOxygenInfo> = ArrayList()
                datalist.forEach { item ->
                    val oxy = HistoryOxygenInfo(item.startTime, item.oOValue)
                    oxygenlist.add(oxy)
                }
                uploadOxygenHistoryData(oxygenlist)
            }
            if (isSuccess) {
                delHistoryDataOnWatch(0x544)
            }
        }
    }


    private suspend fun getHealthHistoryData() : List<HistoryHealthInfo>? {
        Timber.d("$TAG, getHealthHistoryData")
        var datalist: List<HistoryHealthInfo>? = null;

        doneSignal = CountDownLatch(1)

        YCBTClient.healthHistoryData(
            Constants.DATATYPE.Health_HistoryAll
        ) { code, status, hashMap ->

            if (code == Constants.CODE.Code_OK) {
                Timber.d("$TAG, getHealthHistoryData succss")
                if (hashMap != null) {
                    Timber.d("$TAG, getHealthHistoryData, data=$hashMap")
                    val response: YCBTDataResponse? = YCBTDataHelper.YCBTDataResponseFromMap(hashMap as HashMap<String?, Any?>?)
                    if (response != null) {
                        datalist = YCBTDataHelper.historyHealthInfosListFromObject(response.data)
                        if (datalist != null) {
                            isSuccess = true;
                        }
                    }
                } else {
                    Timber.d("$TAG, getHealthHistoryData succss, nodata")
                    isSuccess = true;
                }
            } else {
                Timber.d("$TAG, getHealthHistoryData, fail, code=$code")
            }
            doneSignal?.countDown()
        }

        doneSignal!!.await(5,TimeUnit.SECONDS)

        return datalist
    }


}