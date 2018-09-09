package org.sailcbi.FoKiosk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LandingActivity extends Activity {
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mContentView = findViewById(R.id.fullscreen_content);

        if(mDevicePolicyManager.isDeviceOwnerApp(getPackageName())){
            SetCosuPolicies.setDefaultCosuPolicies(mDevicePolicyManager, mAdminComponentName, getPackageName(), true);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    R.string.not_device_owner,Toast.LENGTH_SHORT)
                    .show();
        }

        if(mDevicePolicyManager.isLockTaskPermitted(this.getPackageName())){
            ActivityManager am = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() ==
                    ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask();
            }
        }
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // Uncomment to print a ticket
        // PrintDriver.print(this);
    }
}