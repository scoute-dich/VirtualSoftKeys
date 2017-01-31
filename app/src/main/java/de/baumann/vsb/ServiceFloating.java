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
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
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

    private final int longClickDuration = 2000;
    private boolean isLongPress = false;

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

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            }
        });

        ib.setOnTouchListener(new OnSwipeTouchListener(ServiceFloating.this) {

            public void onSwipeTop() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongPress = false;
                    }
                }, 1000);
            }
            public void onSwipeRight() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongPress = false;
                    }
                }, 1000);
            }
            public void onSwipeLeft() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongPress = false;
                    }
                }, 1000);
            }
        });

        ib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPress = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLongPress) {
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
                        }
                    }
                }, longClickDuration);
                // TODO Auto-generated method stub
                return true;
            }
        });
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

}