package com.windmill.android.demo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.windmill.android.demo.natives.NativeAdActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainFragment extends Fragment {

    private TextView logTextView;
    private String[] mLogs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        return view;
    }

    private void bindButton(@IdRes int id, final Class clz) {
        getActivity().findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), clz);
                //激励视频代码位id
                if (v.getId() == R.id.loadRewardAd) {
//                    intent.putExtra("horizontal_rit", "901121184");
//                    intent.putExtra("vertical_rit", "901121375");
                }
                //全屏视频代码位id
                if (v.getId() == R.id.loadFullScreenAd) {
//                    intent.putExtra("horizontal_rit", "901121430");
//                    intent.putExtra("vertical_rit", "901121365");
                }
                //开屏代码位id
                if (v.getId() == R.id.splash_button) {
                    intent.putExtra("isLoadAndShow", false);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindButton(R.id.loadRewardAd, RewardVideoActivity.class);
        bindButton(R.id.loadFullScreenAd, InterstitialActivity.class);
        bindButton(R.id.splash_button, SplashActivity.class);
        bindButton(R.id.native_button, NativeAdActivity.class);

        View view = getView();
        Button configurationBtn = view.findViewById(R.id.configuration_button);
        configurationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configuration();
            }
        });

        logTextView = view.findViewById(R.id.logView);
        //fix longClick crash with textView
        logTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button deviceId = view.findViewById(R.id.deviceId_button);
        deviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceId();
            }
        });

        if (mLogs != null && mLogs.length > 0) {
            for (int i = 0; i < mLogs.length; i++) {
                logMessage(mLogs[i]);
            }
        }
    }

    public void setLogs(String[] logs) {
        mLogs = logs;

        if (mLogs != null && mLogs.length > 0 && logTextView != null) {
            for (int i = 0; i < mLogs.length; i++) {
                logMessage(mLogs[i]);
            }
        }
    }

    private void showDeviceId() {
        DeviceIdFragment deviceIdFragment = new DeviceIdFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, deviceIdFragment);
        transaction.addToBackStack("deviceId");
        transaction.commit();
    }

    private void configuration() {
        ConfigurationFragment configurationFragment = new ConfigurationFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, configurationFragment);
        transaction.addToBackStack("configuration");
        transaction.commit();
    }

    private static SimpleDateFormat dateFormat = null;

    private static SimpleDateFormat getDateTimeFormat() {

        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }

    private void logMessage(String message) {
        Date date = new Date();
        logTextView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
