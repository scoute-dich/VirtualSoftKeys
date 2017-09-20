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

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";
    private final static String MY_GIT_HUB_URL = "https://github.com/scoute-dich/VirtualSoftKeys";
    private final static String permissionDialogTAG = "permissionDialog";

    private SharedPreferences sharedPref;

    private static final int ADMIN_INTENT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int size = sharedPref.getInt("size", 70);
        String vsb_click = sharedPref.getString("vsb_click", getString(R.string.action_home));
        String vsb_clickLong = sharedPref.getString("vsb_clickLong", getString(R.string.action_screen));
        String vsb_swipeUp = sharedPref.getString("vsb_swipeUp", getString(R.string.action_power));
        String vsb_swipeLeft = sharedPref.getString("vsb_swipeLeft", getString(R.string.action_notifications));
        String vsb_swipeRight = sharedPref.getString("vsb_swipeRight", getString(R.string.action_quickSettings));
        String vsb_clickDouble = sharedPref.getString("vsb_clickDouble", getString(R.string.action_quickSettings));

        TextView help = (TextView) findViewById(R.id.help);
        help.setText(textSpannable(getString(R.string.text_help)));

        TextView about = (TextView) findViewById(R.id.about);
        about.setText(textSpannable(getString(R.string.text_about)));
        about.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.buttonRestart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.Intent iMain = new android.content.Intent();
                iMain.setClassName(MainActivity.this, "de.baumann.vsb.MainActivity");
                iMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent piMain = PendingIntent.getActivity(MainActivity.this, 2, iMain, 0);
                //Following code will restart your application after 1 second
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, piMain);
                //This will finish your activity manually
                finish();
                //This will stop your application and take out from it.
                System.exit(2); //Prevents app from freezing
                System.exit(1); // kill off the crashed app
            }
        });

        findViewById(R.id.buttonOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL));
                startActivity(browserIntent);
            }
        });

        findViewById(R.id.buttonRemoveLauncher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.remove_launcher)
                        .setMessage(R.string.remove_launcher_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Removes the launcher icon
                                PackageManager p = getPackageManager();
                                p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
            }
        });

        Switch switch_vis = (Switch) findViewById(R.id.switch_vis);
        if (sharedPref.getString("visible", "true").equals("true")){
            switch_vis.setChecked(true);
        } else {
            switch_vis.setChecked(false);
        }
        switch_vis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    sharedPref.edit().putString("visible", "true").apply();
                }else{
                    sharedPref.edit().putString("visible", "false").apply();
                }

            }
        });

        final Spinner sp_high = (Spinner)findViewById(R.id.spinner_high);

        if (size == 50) {
            sp_high.setSelection(0);
        } else if (size == 60) {
            sp_high.setSelection(1);
        } else if (size == 70) {
            sp_high.setSelection(2);
        } else if (size == 80) {
            sp_high.setSelection(3);
        } else if (size == 90) {
            sp_high.setSelection(4);
        } else if (size == 100) {
            sp_high.setSelection(5);
        } else if (size == 110) {
            sp_high.setSelection(6);
        } else if (size == 120) {
            sp_high.setSelection(7);
        }

        sp_high.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
                int high= Integer.parseInt(sp_high.getSelectedItem().toString());
                sharedPref.edit().putInt("size", high).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        Spinner sp_click = (Spinner)findViewById(R.id.spinner_click);
        setSpinner(sp_click, vsb_click, "vsb_click");

        Spinner sp_clickLong = (Spinner)findViewById(R.id.spinner_clickLong);
        setSpinner(sp_clickLong, vsb_clickLong, "vsb_clickLong");

        Spinner sp_clickDouble = (Spinner)findViewById(R.id.spinner_clickDouble);
        setSpinner(sp_clickDouble, vsb_clickDouble, "vsb_clickDouble");

        Spinner sp_swipeUp = (Spinner)findViewById(R.id.spinner_swipeUp);
        setSpinner(sp_swipeUp, vsb_swipeUp, "vsb_swipeUp");

        Spinner sp_swipeLeft = (Spinner)findViewById(R.id.spinner_swipeLeft);
        setSpinner(sp_swipeLeft, vsb_swipeLeft, "vsb_swipeLeft");

        Spinner sp_swipeRight = (Spinner)findViewById(R.id.spinner_swipeRight);
        setSpinner(sp_swipeRight, vsb_swipeRight, "vsb_swipeRight");

    }

    private void setSpinner (final Spinner spinner, String action, final String pref) {

        if (action.equals(getString(R.string.action_home))) {
            spinner.setSelection(0);
        } else if (action.equals(getString(R.string.action_back))) {
            spinner.setSelection(1);
        } else if (action.equals(getString(R.string.action_recents))) {
            spinner.setSelection(2);
        } else if (action.equals(getString(R.string.action_notifications))) {
            spinner.setSelection(3);
        } else if (action.equals(getString(R.string.action_quickSettings))) {
            spinner.setSelection(4);
        } else if (action.equals(getString(R.string.action_power))) {
            spinner.setSelection(5);
        } else if (action.equals(getString(R.string.action_screen))) {
            spinner.setSelection(6);

            DevicePolicyManager mDevicePolicyManager;
            ComponentName mComponentName;
            mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            mComponentName = new ComponentName(this.getApplicationContext(), MyAdminReceiver.class);
            boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
            if (!isAdmin) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,getString(R.string.app_deviceManager));
                startActivityForResult(intent, ADMIN_INTENT);
            }

        } else if (action.equals(getString(R.string.action_volume))) {
            spinner.setSelection(7);
        } else if (action.equals(getString(R.string.action_nothing))) {
            spinner.setSelection(8);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
                sharedPref.edit().putString(pref, spinner.getSelectedItem().toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
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

    private static SpannableString textSpannable(String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }
}
