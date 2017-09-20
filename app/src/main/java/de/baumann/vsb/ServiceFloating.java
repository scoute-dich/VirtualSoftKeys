/*
    This file is part of the VSB.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
    file except in compliance with the License. You may obtain a copy of the License at:
    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software distributed under
    the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions and
    limitations under the License.

    This app is a strongly modified version of the great app "VirtualSoftKeys" developed from "erttyy8821".
    It is also under the Apache License. You can find the source code on Github:
    https://github.com/erttyy8821/VirtualSoftKeys
 */

package de.baumann.vsb;

import android.accessibilityservice.AccessibilityService;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.Toast;


public class ServiceFloating extends AccessibilityService  {

    private boolean checkSystemAlertWindowPermission() {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return Settings.canDrawOverlays(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int size = sharedPref.getInt("size", 70);
        final String vsb_click = sharedPref.getString("vsb_click", getString(R.string.action_home));
        final String vsb_clickLong = sharedPref.getString("vsb_clickLong", getString(R.string.action_screen));
        final String vsb_swipeUp = sharedPref.getString("vsb_swipeUp", getString(R.string.action_power));
        final String vsb_swipeLeft = sharedPref.getString("vsb_swipeLeft", getString(R.string.action_notifications));
        final String vsb_swipeRight = sharedPref.getString("vsb_swipeRight", getString(R.string.action_quickSettings));
        final String vsb_clickDouble = sharedPref.getString("vsb_clickDouble", getString(R.string.action_quickSettings));

        ImageButton ib = new ImageButton(this);
        ib.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        if (sharedPref.getString("visible", "true").equals("true")) {
            ib.setBackgroundResource(R.drawable.button_background);
        } else {
            ib.setBackgroundResource(R.drawable.button_background_invisible);
        }


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                size, size, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 0;

        windowManager.addView(ib, params);

        ib.setOnClickListener(new DoubleClickListener() {

            boolean doubleClick = false;

            @Override
            public void onSingleClick() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!doubleClick) {
                            actions(vsb_click);
                            doubleClick = false;
                        }
                    }
                }, 250);
            }

            @Override
            public void onDoubleClick() {

                actions(vsb_clickDouble);
                doubleClick = true;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleClick = false;
                    }
                }, 250);
            }
        });

        ib.setOnTouchListener(new OnSwipeTouchListener(ServiceFloating.this) {

            public void onSwipeTop() {
                actions(vsb_swipeUp);
            }
            public void onSwipeRight() {
                actions(vsb_swipeRight);
            }
            public void onSwipeLeft() {
                actions(vsb_swipeLeft);
            }
        });

        ib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                actions(vsb_clickLong);
                return true;
            }
        });
    }

    private void actions (String action) {
        if (action.equals(getString(R.string.action_home))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        } else if (action.equals(getString(R.string.action_back))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        } else if (action.equals(getString(R.string.action_recents))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        } else if (action.equals(getString(R.string.action_notifications))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
        } else if (action.equals(getString(R.string.action_quickSettings))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
        } else if (action.equals(getString(R.string.action_power))) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
        } else if (action.equals(getString(R.string.action_screen))) {
            try {
                DevicePolicyManager mDevicePolicyManager;
                ComponentName mComponentName;
                mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                mComponentName = new ComponentName(ServiceFloating.this.getApplicationContext(), MyAdminReceiver.class);
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    mDevicePolicyManager.lockNow();
                }

            } catch (Exception e) {
                // do something
            }
        } else if (action.equals(getString(R.string.action_volume))) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            } else {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audio.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
            }
        } else if (action.equals(getString(R.string.action_nothing))) {
            // do nothing
            Log.i("OnScreenGesture", "Nothing to do");
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Check permission & orientation
        boolean canDrawOverlays = checkSystemAlertWindowPermission();
        if (canDrawOverlays) {
            Log.i("OnScreenGesture", "Is running normal");
        } else {
            Toast.makeText(this, getString(R.string.Toast_allow_system_alert_first), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Experimental features for smart hidden

    }

    @Override
    public void onInterrupt() {
        //Do nothing
    }

    abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick();
            } else {
                onSingleClick();
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick();
        public abstract void onDoubleClick();
    }
}