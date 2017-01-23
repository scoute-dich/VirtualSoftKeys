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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";
    private final static String MY_GIT_HUB_URL = "https://github.com/scoute-dich/VirtualSoftKeys";
    private final static String permissionDialogTAG = "permissionDialog";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView help = (TextView) findViewById(R.id.help);
        help.setText(textSpannable(getString(R.string.text_help)));

        TextView about = (TextView) findViewById(R.id.about);
        about.setText(textSpannable(getString(R.string.text_about)));

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
