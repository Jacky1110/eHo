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
                //                Log.e("deviceconnect", "蓝牙连接成功，全局监听");
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
                // YCBTClient內部可能有其他動作, state可能會變成ReadWriteOK,
                // 所以回覆成功就可以做其他事, 不同
                //int bleState = YCBTClient.connectState();
                //if (bleState == Constants.BLEState.Connected){ //连接成功
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
                                    int bloodOxygenIsTest = (int) hashMap.get("bloodOxygenIsTest");//0x00: 未测心氧 0x01: 正在测试心氧
                                    int bloodOxygenValue = (int) hashMap.get("bloodOxygenValue");//血氧值 0-100

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
     * dataType (步数 0x0502,睡眠0x0504,心率,0x0506, 血压0x0508, 0x0509
     * 同步所有的包括步数睡眠心率血压血氧hrvcvrr温度,
     * 血氧0x051A, 温湿度0x051C, 体温0x051E, 环境光0x0520, 手环脱落记录0x0529)
     *
     * dataResponse (参数 0x0502的返回数据) :
     * sportStartTime -- 开始时间
     * sportEndTime -- 结束时间
     * sportStep -- 运动步数
     * sportCalorie -- 运动卡路里
     * sportDistance -- 运动距离
     *
     * dataResponse (参数 0x0504的返回数据) :
     * startTime -- 睡眠开始时间
     * endTime -- 睡眠结束时间
     * deepSleepCount -- 深睡次数
     * lightSleepCount -- 浅睡次数
     * deepSleepTotal -- 深睡总时长 单位分钟
     * lightSleepTotal -- 浅睡总时长 单位分钟
     * sleepData -- 睡眠详细数据集合
     * sleepType -- 0xF1:深睡 0xF2:浅睡
     * sleepStartTime -- 开始时间戳
     * sleepLen -- 睡眠时长 单位秒
     *
     * dataResponse (参数 0x0506的返回数据) :
     * heartStartTime -- 心率测试时间
     * heartValue -- 心率值
     *
     * dataResponse (参数 0x0508的返回数据) :
     * bloodStartTime -- 血压测试时间
     * bloodDBP -- 收缩压
     * bloodSBP -- 舒张压
     *
     * dataResponse (参数 0x0509的返回数据) :
     * startTime -- 测试时间
     * stepValue -- 步数
     * heartValue -- 心率值
     * DBPValue -- 收缩压
     * SBPValue -- 舒张压
     * OOValue -- 血氧
     * respiratoryRateValue -- 呼吸率
     * hrvValue -- hrv
     * cvrrValue -- cvrr t
     * tempIntValue -- 温度整数部分
     * tempFloatValue -- 温度小数部分
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