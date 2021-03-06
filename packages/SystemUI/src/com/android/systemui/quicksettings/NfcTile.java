/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.quicksettings;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;


public class NfcTile extends QuickSettingsTile {

    private static String TAG = "NfcTile";
    private static NfcAdapter mNfcAdapter;
    private static final int NFC_ADAPTER_UNKNOWN = -100;
    public static QuickSettingsTile mInstance;

    public static QuickSettingsTile getInstance(Context context, LayoutInflater inflater,
            QuickSettingsContainerView container, final QuickSettingsController qsc, Handler handler, String id) {
        mInstance = null;
        mInstance = new NfcTile(context, inflater, container, qsc);
        return mInstance;
    }

    public NfcTile(Context context, LayoutInflater inflater,
            QuickSettingsContainerView container,
            QuickSettingsController qsc) {
        super(context, inflater, container, qsc);

        setTileState(getNfcState());

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleState();
                applyNfcChanges();
            }
        };

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent("android.settings.NFC_SETTINGS");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startSettingsActivity(intent);
                return true;
            }
        };

        qsc.registerAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED, this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        applyNfcChanges();
    }

    private void applyNfcChanges() {
        setTileState(getNfcState());
        updateQuickSettings();
    }

    protected void toggleState() {
        int state = getNfcState();
        switch (state) {
            case NfcAdapter.STATE_TURNING_ON:
            case NfcAdapter.STATE_ON:
                mNfcAdapter.disable();
                break;
            case NfcAdapter.STATE_TURNING_OFF:
            case NfcAdapter.STATE_OFF:
                mNfcAdapter.enable();
                break;
        }
    }

    private void setTileState(int state) {

        switch (state) {
        case NfcAdapter.STATE_TURNING_ON:
        case NfcAdapter.STATE_ON:
            mDrawable = R.drawable.ic_qs_nfc_on;
            mLabel = mContext.getString(R.string.quick_settings_nfc);
            break;
        case NfcAdapter.STATE_TURNING_OFF:
        case NfcAdapter.STATE_OFF:
        default:
            mDrawable = R.drawable.ic_qs_nfc_off;
            mLabel = mContext.getString(R.string.quick_settings_nfc_off);
            break;
        }
    }

    private int getNfcState() {
        if (mNfcAdapter == null) {
            try {
                mNfcAdapter = NfcAdapter.getNfcAdapter(mContext);
            } catch (UnsupportedOperationException e) {
                return NFC_ADAPTER_UNKNOWN;
            }
        }
        return mNfcAdapter.getAdapterState();
    }

}
