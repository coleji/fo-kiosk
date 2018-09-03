package org.sailcbi.FoKiosk;

import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.UserManager;
import android.provider.Settings;

class SetCosuPolicies {
    public static void setDefaultCosuPolicies(DevicePolicyManager dpm, ComponentName adminComponentName, String packageName, boolean active){
        // set user restrictions
        setUserRestriction(dpm, adminComponentName, UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(dpm, adminComponentName, UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(dpm, adminComponentName, UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(dpm, adminComponentName, UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(dpm, adminComponentName, UserManager.DISALLOW_ADJUST_VOLUME, active);

        // disable keyguard and status bar
        dpm.setKeyguardDisabled(adminComponentName, active);
        dpm.setStatusBarDisabled(adminComponentName, active);

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(dpm, adminComponentName, active);

        // set system update policy
        if (active){
            dpm.setSystemUpdatePolicy(adminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            dpm.setSystemUpdatePolicy(adminComponentName,
                    null);
        }

        // set this Activity as a lock task package

        dpm.setLockTaskPackages(adminComponentName,
                active ? new String[]{packageName} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            dpm.addPersistentPreferredActivity(
                    adminComponentName, intentFilter, new ComponentName(
                            packageName, LandingActivity.class.getName()));
        } else {
            dpm.clearPackagePersistentPreferredActivities(
                    adminComponentName, packageName);
        }
    }

    private static void setUserRestriction(DevicePolicyManager dpm, ComponentName adminComponentName, String restriction, boolean disallow){
        if (disallow) {
            dpm.addUserRestriction(adminComponentName,
                    restriction);
        } else {
            dpm.clearUserRestriction(adminComponentName,
                    restriction);
        }
    }

    private static void enableStayOnWhilePluggedIn(DevicePolicyManager dpm, ComponentName adminComponentName, boolean enabled){
        if (enabled) {
            dpm.setGlobalSetting(
                    adminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            dpm.setGlobalSetting(
                    adminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }
}
