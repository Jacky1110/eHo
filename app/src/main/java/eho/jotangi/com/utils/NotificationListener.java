package eho.jotangi.com.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"Notification removed");
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
                //Ignore the notification
                return;
            }
            String appName = sbn.getPackageName();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            Log.i(TAG, "Notification posted " + appName);
            Log.i(TAG, "Notification posted " + title);
            if(!checkNotify(appName) || !checkNotify(title))
                return;

            CharSequence content = extras.getCharSequence("android.text");
            int type = 0x03;
            String message = "";
            if(content==null){
                message = title;
                title = "";
            }else{
                message = content.toString();
            }

            Log.i(TAG, "Notification posted " + message);
            YCBTClient.appSengMessageToDevice(type, title, message, new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    Log.i(TAG, "Notification posted " + i);
                }
            });

        }catch (Exception e){
            Log.i(TAG, "Notification Exception " + e.toString());
        }
    }

    private Boolean checkNotify(String data){
        return SharedPreferencesUtil.Companion.getInstances().checkNotify(data);
    }
}
