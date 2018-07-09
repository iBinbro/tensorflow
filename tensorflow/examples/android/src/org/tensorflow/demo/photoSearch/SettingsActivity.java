package org.tensorflow.demo.photoSearch;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.tensorflow.demo.R;

import java.util.prefs.PreferenceChangeListener;

/**
 * Created by mgo983 on 5/30/17.
 */

public class SettingsActivity extends PreferenceActivity
    implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference("search_engine"));
    }

    private void bindPreferenceSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
        .getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(),""));
    }
    @Override
    public  boolean onPreferenceChange(Preference preference, Object value){
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }else{
                preference.setSummary(stringValue);
            }
            return true;
        }

}
