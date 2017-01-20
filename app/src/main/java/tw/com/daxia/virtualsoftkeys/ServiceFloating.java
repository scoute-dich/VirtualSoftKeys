package tw.com.daxia.virtualsoftkeys;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
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

    int longClickDuration = 2000;
    boolean isLongPress = false;


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

        ImageButton ib = new ImageButton(this);
        ib.setLayoutParams(new ViewGroup.LayoutParams(72, 72));
        ib.setBackgroundResource(R.drawable.button_background);
        ib.setImageResource(R.drawable.checkbox_blank_circle_outline);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                200, 100, WindowManager.LayoutParams.TYPE_PHONE,
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
                isLongPress = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
            }
            public void onSwipeRight() {
                isLongPress = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);

            }
            public void onSwipeLeft() {
                isLongPress = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
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