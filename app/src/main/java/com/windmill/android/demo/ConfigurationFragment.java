package com.windmill.android.demo;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class ConfigurationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.configuration_layout, container, false);

        Button saveBtn = view.findViewById(R.id.save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadConfiguration();
    }

    private void loadConfiguration() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);
        TextView appIdView = getView().findViewById(R.id.appIdText);
        TextView appKeyView = getView().findViewById(R.id.appKeyText);
        TextView splashPlacementIdView = getView().findViewById(R.id.splashPlacementIdText);
        TextView rewardPlacementIdView = getView().findViewById(R.id.rewardPlacementIdText);
        TextView fullScreenPlacementIdView = getView().findViewById(R.id.fullScreenPlacementIdText);
        TextView nativeUnifiedPlacementIdView = getView().findViewById(R.id.nativeUnifiedPlacementIdText);

        CheckBox halfSplash = getView().findViewById(R.id.halfSplash);
        TextView userIdView = getView().findViewById(R.id.userIdText);
        TextView appTitle = getView().findViewById(R.id.APP_Title);
        TextView appDesc = getView().findViewById(R.id.APP_Desc);

        appIdView.setText(sharedPreferences.getString(Constants.CONF_APPID, Constants.app_id));
        appKeyView.setText(sharedPreferences.getString(Constants.CONF_APPKEY, Constants.app_key));
        splashPlacementIdView.setText(sharedPreferences.getString(Constants.CONF_SPLASH_PLACEMENTID, Constants.splash_placement_id));
        rewardPlacementIdView.setText(sharedPreferences.getString(Constants.CONF_REWARD_PLACEMENTID, Constants.reward_placement_id));
        fullScreenPlacementIdView.setText(sharedPreferences.getString(Constants.CONF_FULLSCREEN_PLACEMENTID, Constants.fullScreen_placement_id));
        nativeUnifiedPlacementIdView.setText(sharedPreferences.getString(Constants.CONF_UNIFIED_NATIVE_PLACEMENTID, Constants.native_unified_placement_id));

        userIdView.setText(sharedPreferences.getString(Constants.CONF_USERID, "-1"));
        appTitle.setText(sharedPreferences.getString(Constants.CONF_APP_TITLE, "APP_TITLE"));
        appDesc.setText(sharedPreferences.getString(Constants.CONF_APP_DESC, "APP_DESC"));
        halfSplash.setChecked(sharedPreferences.getBoolean(Constants.CONF_HALF_SPLASH, false));

    }

    private void onSave() {
        TextView appIdView = getView().findViewById(R.id.appIdText);
        TextView appKeyView = getView().findViewById(R.id.appKeyText);
        TextView splashPlacementIdView = getView().findViewById(R.id.splashPlacementIdText);
        TextView rewardPlacementIdView = getView().findViewById(R.id.rewardPlacementIdText);
        TextView fullScreenPlacementIdView = getView().findViewById(R.id.fullScreenPlacementIdText);
        TextView nativeUnifiedPlacementIdView = getView().findViewById(R.id.nativeUnifiedPlacementIdText);

        TextView userIdView = getView().findViewById(R.id.userIdText);
        TextView appTitle = getView().findViewById(R.id.APP_Title);
        TextView appDesc = getView().findViewById(R.id.APP_Desc);
        CheckBox halfSplash = getView().findViewById(R.id.halfSplash);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CONF_APPID, String.valueOf(appIdView.getText()));
        editor.putString(Constants.CONF_APPKEY, String.valueOf(appKeyView.getText()));
        editor.putString(Constants.CONF_SPLASH_PLACEMENTID, String.valueOf(splashPlacementIdView.getText()));
        editor.putString(Constants.CONF_REWARD_PLACEMENTID, String.valueOf(rewardPlacementIdView.getText()));
        editor.putString(Constants.CONF_FULLSCREEN_PLACEMENTID, String.valueOf(fullScreenPlacementIdView.getText()));
        editor.putString(Constants.CONF_UNIFIED_NATIVE_PLACEMENTID, String.valueOf(nativeUnifiedPlacementIdView.getText()));

        editor.putString(Constants.CONF_USERID, String.valueOf(userIdView.getText()));
        editor.putString(Constants.CONF_APP_TITLE, String.valueOf(appTitle.getText()));
        editor.putString(Constants.CONF_APP_DESC, String.valueOf(appDesc.getText()));
        editor.putBoolean(Constants.CONF_HALF_SPLASH, halfSplash.isChecked());

        editor.apply();

    }

}
