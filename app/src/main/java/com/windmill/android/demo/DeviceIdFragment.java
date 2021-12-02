package com.windmill.android.demo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.windmill.android.demo.deviceid.AdvertisingIdClient;
import com.windmill.android.demo.deviceid.OaidHelper;
import com.windmill.sdk.WindMillAd;

public class DeviceIdFragment extends Fragment {
    private TextView imeiView, gaidView, oaidView, sigUidView;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            gaidView.setText((String) msg.obj);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deviceid_layout, container, false);

        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        imeiView = getView().findViewById(R.id.imei_Text);
        gaidView = getView().findViewById(R.id.gaid_Text);
        oaidView = getView().findViewById(R.id.oaid_Text);
        sigUidView = getView().findViewById(R.id.sig_uid_Text);
        loadId();
    }

    private void loadId() {

        /**
         * 获取imei
         */
        String imei = getIMEI(getActivity());
        if (!TextUtils.isEmpty(imei)) {
            imeiView.setText(imei);
        }
        /**
         * 获取oaid
         */
        try {
            int i = new OaidHelper(new OaidHelper.AppIdsUpdater() {
                @Override
                public void OnIdsAvalid(@NonNull final String ids) {
                    if (!TextUtils.isEmpty(ids)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oaidView.setText(ids);
                            }
                        });
                    }
                }
            }).CallFromReflect(getActivity());
            Log.d("sigmob", "load oaid return value: " + i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 获取gaid
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                String advertiserId = AdvertisingIdClient.getGAID(getActivity());
                if (!TextUtils.isEmpty(advertiserId)) {
                    handler.sendMessage(handler.obtainMessage(0x001, advertiserId));
                }
            }
        }).start();

        /**
         * 获取uid
         */

        String uid = WindMillAd.sharedAds().getWindUid();
        if (!TextUtils.isEmpty(uid)) {
            sigUidView.setText(uid);
        }

    }


    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        try {
            String deviceUniqueIdentifier = null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceUniqueIdentifier = tm.getImei();

                if (TextUtils.isEmpty(deviceUniqueIdentifier)) {
                    try {
                        return tm.getDeviceId();
                    } catch (Throwable ignored) {
                        ignored.printStackTrace();
                    }

                    return tm.getMeid();
                }
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }
            if (deviceUniqueIdentifier != null) {
                return deviceUniqueIdentifier;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
