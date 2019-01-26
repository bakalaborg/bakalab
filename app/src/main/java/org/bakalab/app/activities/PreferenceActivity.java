package org.bakalab.app.activities;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.bakalab.app.R;
import org.bakalab.app.fragments.SettingsFragment;
import org.bakalab.app.utils.SharedPrefHandler;

public class PreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String theme = SharedPrefHandler.getString(this, "theme");
        if (theme.equals("0")){
            setTheme(R.style.Bakalab_NoActionBar);
        } else if (theme.equals("1")) {
            setTheme(R.style.Bakalab_NoActionBar_Dark);
        } else {
            setTheme(R.style.Bakalab_NoActionBar);
        }

        setContentView(R.layout.activity_preference);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}
