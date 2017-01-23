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

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;


/**
 * Created by baumann on 2016/8/27
 */
public class PermissionDialog extends DialogFragment implements View.OnClickListener {


    private boolean systemAlertPermission, accessibilityPermission;
    private Button But_intent_system_alert, But_intent_accessibility;

    public static PermissionDialog newInstance(boolean systemAlertPermission, boolean accessibilityPermission) {
        Bundle args = new Bundle();
        PermissionDialog fragment = new PermissionDialog();
        args.putBoolean("systemAlertPermission", systemAlertPermission);
        args.putBoolean("accessibilityPermission", accessibilityPermission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemAlertPermission = getArguments().getBoolean("systemAlertPermission", false);
        accessibilityPermission = getArguments().getBoolean("accessibilityPermission", false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        //noinspection ConstantConditions
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_permission, container);
        But_intent_system_alert = (Button) rootView.findViewById(R.id.But_intent_system_alert);
        But_intent_system_alert.setOnClickListener(this);
        But_intent_accessibility = (Button) rootView.findViewById(R.id.But_intent_accessibility);
        But_intent_accessibility.setOnClickListener(this);
        initButton();
        return rootView;
    }


    private void initButton() {
        if (!systemAlertPermission && !accessibilityPermission) {
            Log.i("OnScreenGesture", "Is running normal");
            //Use layout default value
        } else if (systemAlertPermission && !accessibilityPermission) {
            But_intent_system_alert.setText(getString(R.string.Permission_allowed));
            But_intent_system_alert.setEnabled(false);
            But_intent_accessibility.setText(getString(R.string.Permission_goto_page));
            But_intent_accessibility.setEnabled(true);
        } else if (!systemAlertPermission) {
            //User change the Permission without this dialog
            But_intent_system_alert.setText(getString(R.string.Permission_allow_system_alert_first_and_restart_service));
            But_intent_system_alert.setEnabled(true);
        }
    }

    private void gotoSettingPage() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void gotoDrawOverlaysPage() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_intent_system_alert:
                gotoDrawOverlaysPage();
                break;
            case R.id.But_intent_accessibility:
                gotoSettingPage();
                break;
        }
        this.dismiss();
    }


}
