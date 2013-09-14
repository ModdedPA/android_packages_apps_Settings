/*
 * Copyright (C) 2013 ParanoidAndroid Project
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

package com.android.settings.paranoid;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Toolbar extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_QUICK_PULL_DOWN = "quick_pulldown";
    private static final String KEY_AM_PM_STYLE = "am_pm_style";
    private static final String KEY_SHOW_CLOCK = "show_clock";
    private static final String KEY_CIRCLE_BATTERY = "circle_battery";
    private static final String KEY_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_MAX_NOTIF = "status_bar_max_notifications";
    private static final String NAV_BAR_TABUI_MENU = "nav_bar_tabui_menu";
    private static final String STATUS_BAR_DONOTDISTURB = "status_bar_donotdisturb";
    private static final String NAV_BAR_CATEGORY = "toolbar_navigation";
    private static final String NAV_BAR_CONTROLS = "navigation_bar_controls";
    private static final String KEY_HARDWARE_KEYS = "hardware_keys";
    private static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
    private static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";  
    private static final String PREF_USE_ALT_RESOLVER = "use_alt_resolver";
    private static final String KEY_SCREEN_ON_NOTIFICATION_LED = "screen_on_notification_led";

    private ListPreference mAmPmStyle;
    private ListPreference mListViewAnimation;
    private ListPreference mListViewInterpolator;  
    private ListPreference mStatusBarMaxNotif;
    private CheckBoxPreference mQuickPullDown;
    private CheckBoxPreference mShowClock;
    private CheckBoxPreference mCircleBattery;
    private CheckBoxPreference mStatusBarNotifCount;
    private CheckBoxPreference mMenuButtonShow;
    private CheckBoxPreference mStatusBarDoNotDisturb;
    private CheckBoxPreference mUseAltResolver;
    private CheckBoxPreference mScreenOnNotificationLed;
    private PreferenceScreen mNavigationBarControls;
    private PreferenceCategory mNavigationCategory;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.tool_bar_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mContext = getActivity();

        mQuickPullDown = (CheckBoxPreference) prefSet.findPreference(KEY_QUICK_PULL_DOWN);
        mQuickPullDown.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_QUICK_PULLDOWN, 0) == 1);

	//ListView Animations
        mListViewAnimation = (ListPreference) findPreference(KEY_LISTVIEW_ANIMATION);
        int listviewanimation = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.LISTVIEW_ANIMATION, 1);
        mListViewAnimation.setValue(String.valueOf(listviewanimation));
        mListViewAnimation.setSummary(mListViewAnimation.getEntry());
        mListViewAnimation.setOnPreferenceChangeListener(this);

        mListViewInterpolator = (ListPreference) findPreference(KEY_LISTVIEW_INTERPOLATOR);
        int listviewinterpolator = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.LISTVIEW_INTERPOLATOR, 0);
        mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
        mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
        mListViewInterpolator.setOnPreferenceChangeListener(this);

        mUseAltResolver = (CheckBoxPreference) findPreference(PREF_USE_ALT_RESOLVER);
        mUseAltResolver.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.ACTIVITY_RESOLVER_USE_ALT, 0) == 1);

        int statusScreenOnNotificationLed = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_ON_NOTIFICATION_LED, 1);

        mScreenOnNotificationLed = (CheckBoxPreference) findPreference(KEY_SCREEN_ON_NOTIFICATION_LED);
        mScreenOnNotificationLed.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SCREEN_ON_NOTIFICATION_LED, 0) == 1);

        mShowClock = (CheckBoxPreference) prefSet.findPreference(KEY_SHOW_CLOCK);
        mShowClock.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1);

        mCircleBattery = (CheckBoxPreference) prefSet.findPreference(KEY_CIRCLE_BATTERY);
        mCircleBattery.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.STATUS_BAR_CIRCLE_BATTERY, 0) == 1);

        mAmPmStyle = (ListPreference) prefSet.findPreference(KEY_AM_PM_STYLE);
        int amPmStyle = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM_STYLE, 2);
        mAmPmStyle.setValue(String.valueOf(amPmStyle));
        mAmPmStyle.setSummary(mAmPmStyle.getEntry());
        mAmPmStyle.setOnPreferenceChangeListener(this);

        mStatusBarMaxNotif = (ListPreference) prefSet.findPreference(STATUS_BAR_MAX_NOTIF);
        int maxNotIcons = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.MAX_NOTIFICATION_ICONS, 2);
        mStatusBarMaxNotif.setValue(String.valueOf(maxNotIcons));
        mStatusBarMaxNotif.setOnPreferenceChangeListener(this);

        mNavigationCategory = (PreferenceCategory) prefSet.findPreference(NAV_BAR_CATEGORY);

        mMenuButtonShow = (CheckBoxPreference) prefSet.findPreference(NAV_BAR_TABUI_MENU);
        mMenuButtonShow.setChecked((Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NAV_BAR_TABUI_MENU, 0) == 1));

        mNavigationBarControls = (PreferenceScreen) prefSet.findPreference(NAV_BAR_CONTROLS);

        try {
            if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.TIME_12_24) != 12) {
                mAmPmStyle.setEnabled(false);
                mAmPmStyle.setSummary(R.string.status_bar_am_pm_info);
            }
        } catch (SettingNotFoundException e) {
            // This will hurt you, run away
        }

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(KEY_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getInt(getActivity().getContentResolver(), 
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1);

        mStatusBarDoNotDisturb = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_DONOTDISTURB);
        mStatusBarDoNotDisturb.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_DONOTDISTURB, 0) == 1));

        if (!Utils.isTablet()) {
            prefSet.removePreference(mStatusBarMaxNotif);
            prefSet.removePreference(mMenuButtonShow);
            prefSet.removePreference(mStatusBarDoNotDisturb);

            if(!Utils.hasNavigationBar()) {
                prefSet.removePreference(mNavigationCategory);
            }
        } else {
            mNavigationCategory.removePreference(mNavigationBarControls);
            prefSet.removePreference(mQuickPullDown);
        }

        // Only show the hardware keys config on a device that does not have a navbar
        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);
        if(deviceKeys==15) {
             PreferenceScreen HARDWARE =(PreferenceScreen) prefSet.findPreference(KEY_HARDWARE_KEYS);
             prefSet.removePreference(HARDWARE);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mShowClock) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_CLOCK, mShowClock.isChecked()
                    ? 1 : 0);
        } else if (preference == mCircleBattery) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY, mCircleBattery.isChecked()
                    ? 1 : 0);
        } else if (preference == mQuickPullDown) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_QUICK_PULLDOWN, mQuickPullDown.isChecked()
                    ? 1 : 0);
        } else if (preference == mStatusBarNotifCount) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, mStatusBarNotifCount.isChecked()
                    ? 1 : 0);
        } else if (preference == mMenuButtonShow) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_TABUI_MENU, mMenuButtonShow.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mStatusBarDoNotDisturb) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_DONOTDISTURB,
                    mStatusBarDoNotDisturb.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mUseAltResolver) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ACTIVITY_RESOLVER_USE_ALT,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mScreenOnNotificationLed) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_ON_NOTIFICATION_LED,
                    mScreenOnNotificationLed.isChecked() ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAmPmStyle) {
            int statusBarAmPmSize = Integer.valueOf((String) newValue);
            int index = mAmPmStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.STATUS_BAR_AM_PM_STYLE, statusBarAmPmSize);
            mAmPmStyle.setSummary(mAmPmStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarMaxNotif) {
            int maxNotIcons = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MAX_NOTIFICATION_ICONS, maxNotIcons);
            return true;
	} else if (preference == mListViewAnimation) {
            int listviewanimation = Integer.valueOf((String) newValue);
            int index = mListViewAnimation.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LISTVIEW_ANIMATION,
                    listviewanimation);
            mListViewAnimation.setSummary(mListViewAnimation.getEntries()[index]);
            return true;
        } else if (preference == mListViewInterpolator) {
            int listviewinterpolator = Integer.valueOf((String) newValue);
            int index = mListViewInterpolator.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LISTVIEW_INTERPOLATOR,
                    listviewinterpolator);
            mListViewInterpolator.setSummary(mListViewInterpolator.getEntries()[index]);
            return true;
        }
        return false;
    }
}
