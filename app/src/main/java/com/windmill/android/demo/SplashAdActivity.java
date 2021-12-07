package com.windmill.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class SplashAdActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private CheckBox fullScreen;
    private CheckBox selfLogo;
    private boolean isFullScreen, isSelfLogo;
    private String placementId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.splash_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        fullScreen = findViewById(R.id.cb_fullscreen);
        selfLogo = findViewById(R.id.cb_self_logo);

        fullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFullScreen = isChecked;
            }
        });

        selfLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelfLogo = isChecked;
            }
        });
        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        placementId = stringArray[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("lance", "------onItemSelected------" + position);
        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        placementId = stringArray[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("lance", "------onNothingSelected------");
    }

    public void ButtonClick(View view) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("isFullScreen", isFullScreen);
        intent.putExtra("isSelfLogo", isSelfLogo);
        intent.putExtra("placementId", placementId);
        intent.putExtra("need_start_main_activity", false);
        switch (view.getId()) {
            case R.id.bt_load_show:
                intent.putExtra("isLoadAndShow", true);
                startActivity(intent);
                break;
            case R.id.bt_load_only:
                intent.putExtra("isLoadAndShow", false);
                startActivity(intent);
                break;
        }
    }

}