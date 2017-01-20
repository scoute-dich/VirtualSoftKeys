package tw.com.daxia.virtualsoftkeys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String TAG = "MainActivity";
    private final static String MY_GIT_HUB_URL = "https://github.com/erttyy8821/VirtualSoftKeys";
    private final static String permissionDialogTAG = "permissionDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView IV_my_github = (ImageView) findViewById(R.id.IV_my_github);
        IV_my_github.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        boolean drawOverlays = checkSystemAlertWindowPermission();
        boolean accessibility = isAccessibilitySettingsOn();
        if (!drawOverlays || !accessibility) {
            clearOldDialogFragment();
            PermissionDialog permissionDialog = PermissionDialog.newInstance(drawOverlays, accessibility);
            permissionDialog.show(this.getSupportFragmentManager(), permissionDialogTAG);
        }
    }


    private void clearOldDialogFragment(){
        @SuppressLint("CommitTransaction") FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(MainActivity.permissionDialogTAG);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkSystemAlertWindowPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            //accessibility is Enable
            if (BuildConfig.DEBUG) {
                Log.i(TAG, e.getMessage());
            }
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(this.getPackageName().toLowerCase());
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.IV_my_github: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL));
                startActivity(browserIntent);
                break;
            }
        }
    }
}
