package com.example.liveclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoEngineProfile;

public class MainActivity extends AppCompatActivity {


    long appID =757710733;
    String appSign = "816834a255376f270b8881ed54d68512acce932656d1ce07c65c96a4b58fa01e";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("My Live Videos App");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(MainActivity.this,R.color.blue)));
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.blue));



        findViewById(R.id.start_live_streaming).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomID = ((EditText)findViewById(R.id.login_room_id)).getText().toString();
                if(roomID.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please input the room ID", Toast.LENGTH_LONG).show();
                    return;
                }
                // Before starting a live streaming, request the camera and recording permissions.
                requestPermissionIfNeeded(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Toast.makeText( MainActivity.this, "All permissions have been granted.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                            String userID = generateRandomID();
                            String userName = "user_" + userID;
                            intent.putExtra("userID", userID);
                            intent.putExtra("userName", userName);
                            intent.putExtra("roomID", roomID);
                            intent.putExtra("isHost", true);
                            startActivity(intent);
                        } else {
                            Toast.makeText( MainActivity.this, "Some permissions have not been granted.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        findViewById(R.id.watch_live_streaming).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomID = ((EditText)findViewById(R.id.login_room_id)).getText().toString();
                if(roomID.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please input the room ID", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                String userID = generateRandomID();
                String userName = "user_" + userID;
                if(roomID.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please input the room ID", Toast.LENGTH_LONG).show();
                    return;
                }
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                intent.putExtra("roomID", roomID);
                intent.putExtra("isHost", false);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyEngine();
    }

    void createEngine() {
        ZegoEngineProfile profile = new ZegoEngineProfile();

        // Get your AppID and AppSign from ZEGOCLOUD Console
        //[My Projects -> AppID] : https://console.zegocloud.com/project
        profile.appID = appID;
        profile.appSign = appSign;
        profile.scenario = ZegoScenario.BROADCAST; // General scenario.
        profile.application = getApplication();
        ZegoExpressEngine.createEngine(profile, null);
    }


    // destroy engine
    private void destroyEngine() {
        ZegoExpressEngine.destroyEngine(null);
    }

    private static String generateRandomID() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < 6) {
            int nextInt = random.nextInt(10);
            if (builder.length() == 0 && nextInt == 0) {
                continue;
            }
            builder.append(nextInt);
        }
        return builder.toString();
    }

    private void requestPermissionIfNeeded(List<String> permissions, RequestCallback requestCallback) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(this).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.permission_explain_mic);
                    }
                } else {
                    message = this.getString(R.string.permission_explain_camera_mic);
                }
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.settings_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.settings_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.settings_mic);
                    }
                } else {
                    message = this.getString(R.string.settings_camera_mic);
                }
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.settings),
                    getString(R.string.cancel));
        }).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                 @NonNull List<String> deniedList) {
                if (requestCallback != null) {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                }
            }
        });
    }
}