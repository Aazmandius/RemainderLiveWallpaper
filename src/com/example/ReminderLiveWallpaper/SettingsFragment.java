package com.example.ReminderLiveWallpaper;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.example.ReminderLiveWallpaperExample.R;

/**
 * Created by User on 21.12.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}