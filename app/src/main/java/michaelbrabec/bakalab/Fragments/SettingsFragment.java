package michaelbrabec.bakalab.Fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import michaelbrabec.bakalab.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
