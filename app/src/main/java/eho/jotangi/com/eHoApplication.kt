package eho.jotangi.com

import android.app.Application
import android.util.Log
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleDataResponse
import com.yucheng.ycbtsdk.response.BleDeviceToAppDataResponse
import eho.jotangi.com.network.AppClientManager
import eho.jotangi.com.utils.SharedPreferencesUtil
import eho.jotangi.com.utils.smartwatch.WatchUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 *Created by Luke Liu on 2021/8/18.
 */

class eHoApplication: Application() {
    private var TAG = "eHoApplication";

    override fun onCreate() {
        super.onCreate()

        SharedPreferencesUtil.instances.init(this)
        setupTimber()

        WatchUtils.instance.init(this)

        AppClientManager.instance.init()

        startKoin {
            androidContext(this@eHoApplication)
            modules(appModule)
        }

        WatchUtils.instance.initWatchSDK()

        //var mac = SharedPreferencesUtil.instances.getWatchMac() //
        //if (mac == null) {  // for test only
        //    mac = "F2:CA:B6:91:CE:6C"
        //    SharedPreferencesUtil.instances.setWatchMac(mac)
        //}

        //YCBTClient.initClient(this,true);
        //YCBTClient.registerBleStateChange(bleConnectResponse)
        //YCBTClient.deviceToApp(toAppDataResponse);
        //doConnectDevice(mac)
        //initWorker(true);
        //WatchUtils.instance.initPeriodWorker()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    inner class CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
            throwable?.printStackTrace()
        }
    }



    var toAppDataResponse =
        BleDeviceToAppDataResponse { dataType, dataMap ->
            Timber.e("$TAG, BleDeviceToAppDataResponse, datatype=$dataType, ")
            Timber.e(dataMap.toString())
        }


    var bleConnectResponse =
        BleConnectResponse { code ->
            //            Toast.makeText(MyApplication.this, "i222=" + var1, Toast.LENGTH_SHORT).show();
            Log.e("bleConnectResponse, ","code=$code")
            if (code == Constants.BLEState.Disconnect) {
                //                thirdConnect = false;
                //                BangleUtil.getInstance().SDK_VERSIONS = -1;
                //                EventBus.getDefault().post(new BlueConnectFailEvent());
                /*if(SPUtil.getBindedDeviceMac() != null && !"".equals(SPUtil.getBindedDeviceMac())){
                          YCBTClient.connectBle(SPUtil.getBindedDeviceMac(), new BleConnectResponse() {
                              @Override
                              public void onConnectResponse(int code) {

                              }
                          });
                      }*/
            } else if (code == Constants.BLEState.Connected) {
            } else if (code == Constants.BLEState.ReadWriteOK) {
                //                thirdConnect = true;
                //                BangleUtil.getInstance().SDK_VERSIONS = 3;
                //                Log.e("deviceconnect", "?????????????????????????????????");
                //                setBaseOrder();
                //EventBus.getDefault().post(new ConnectEvent());
            } else {
                //code == Constants.BLEState.Disconnect
                //                thirdConnect = false;
                //                BangleUtil.getInstance().SDK_VERSIONS = -1;
                //                EventBus.getDefault().post(new ConnectEvent());
            }
        }

    private fun doConnectDevice(mac: String) {
        YCBTClient.connectBle(mac) { code ->
            Timber.d("$TAG, doConnectDevice(), code=$code")
            if (code == Constants.CODE.Code_OK) {
                // YCBTClient???????????????????????????, state???????????????ReadWriteOK,
                // ???????????????????????????????????????, ??????
                //int bleState = YCBTClient.connectState();
                //if (bleState == Constants.BLEState.Connected){ //????????????
                Timber.d("$TAG, doConnectDevice(), connected")
                doGetHistory(Constants.DATATYPE.Health_HistorySport, bleHistoryResponse)

                //doGetDeviceInfo(bleDeviceInfoResponse);
                //getNowStep(bleDataResponse);
                //YCBTClient.healthHistoryData();
                /*YCBTClient.getRealBloodOxygen(new BleDataResponse() {
                        @Override
                        public void onDataResponse(int i, float v, HashMap hashMap) {

                            if(i == 0){
                                if (hashMap != null) {
                                    Log.d(TAG, "getRealBloodOxygen, data=" + hashMap.toString());
                                    int bloodOxygenIsTest = (int) hashMap.get("bloodOxygenIsTest");//0x00: ???????????? 0x01: ??????????????????
                                    int bloodOxygenValue = (int) hashMap.get("bloodOxygenValue");//????????? 0-100

                                }
                            }
                        }
                    });
                    */

                //} else {
                //    Log.d(TAG, "doConnectDevice(), state="+bleState);
                //}
            } else if (code == Constants.CODE.Code_Failed) {
            }
        }
    }

    /**
     * dataType (?????? 0x0502,??????0x0504,??????,0x0506, ??????0x0508, 0x0509
     * ???????????????????????????????????????????????????hrvcvrr??????,
     * ??????0x051A, ?????????0x051C, ??????0x051E, ?????????0x0520, ??????????????????0x0529)
     *
     * dataResponse (?????? 0x0502???????????????) :
     * sportStartTime -- ????????????
     * sportEndTime -- ????????????
     * sportStep -- ????????????
     * sportCalorie -- ???????????????
     * sportDistance -- ????????????
     *
     * dataResponse (?????? 0x0504???????????????) :
     * startTime -- ??????????????????
     * endTime -- ??????????????????
     * deepSleepCount -- ????????????
     * lightSleepCount -- ????????????
     * deepSleepTotal -- ??????????????? ????????????
     * lightSleepTotal -- ??????????????? ????????????
     * sleepData -- ????????????????????????
     * sleepType -- 0xF1:?????? 0xF2:??????
     * sleepStartTime -- ???????????????
     * sleepLen -- ???????????? ?????????
     *
     * dataResponse (?????? 0x0506???????????????) :
     * heartStartTime -- ??????????????????
     * heartValue -- ?????????
     *
     * dataResponse (?????? 0x0508???????????????) :
     * bloodStartTime -- ??????????????????
     * bloodDBP -- ?????????
     * bloodSBP -- ?????????
     *
     * dataResponse (?????? 0x0509???????????????) :
     * startTime -- ????????????
     * stepValue -- ??????
     * heartValue -- ?????????
     * DBPValue -- ?????????
     * SBPValue -- ?????????
     * OOValue -- ??????
     * respiratoryRateValue -- ?????????
     * hrvValue -- hrv
     * cvrrValue -- cvrr t
     * tempIntValue -- ??????????????????
     * tempFloatValue -- ??????????????????
     */
    var bleHistoryResponse =
        BleDataResponse { code, ratio, hashMap ->
            Timber.d("$TAG, bleStepHistoryResponse, code=$code, ratio=$ratio")
            if (code == Constants.CODE.Code_OK) {
                if (hashMap != null) {
                    Timber.d("$TAG, bleStepHistoryResponse, data=$hashMap")
                    /*
                    val response: YCBTDataResponse = YCBTDataHelper.YCBTDataResponseFromMap(hashMap)
                    if (response != null) {
                        try {
                            when (response.getDataType()) {
                                Constants.DATATYPE.Health_HistorySport ->
                                    var  datalist
                                    : MutableList<HistorySportInfo>
                                    ?
                                    = YCBTDataHelper.historySportInfoListFromObject(response.getData())
                                else -> {
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                     */
                } else {
                    Timber.d("$TAG, bleStepHistoryResponse, data=null")
                }
            }
        }

    private fun doGetHistory(type: Int, dataResponse: BleDataResponse) {
        YCBTClient.healthHistoryData(type, dataResponse)
    }
}