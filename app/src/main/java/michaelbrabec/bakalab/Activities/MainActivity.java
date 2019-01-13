package michaelbrabec.bakalab.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import michaelbrabec.bakalab.Fragments.UkolyFragment;
import michaelbrabec.bakalab.Fragments.ZnamkyFragment;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.Utils.BakaTools;
import michaelbrabec.bakalab.Utils.RozvrhFragment;
import michaelbrabec.bakalab.Utils.SharedPrefHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AppBarLayout appBarLayout;
    Float defaultElevation;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BakaTools.getToken(this) == null) {
            startLogin();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = findViewById(R.id.app_bar_layout);
        defaultElevation = getResources().getDimension(R.dimen.toolbar_elevation);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String loginJmeno = SharedPrefHandler.getString(this, "loginJmeno");

        TextView navJmeno = navigationView.getHeaderView(0).findViewById(R.id.loginJmeno);

        navJmeno.setText(loginJmeno);

        if (loginJmeno.contains("Jovan")) {
            setTitle("Jovane uč se");
        } else {
            setTitle(R.string.ukoly);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new UkolyFragment()).commit();


    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sign_out) {
            BakaTools.resetToken();
            if (getSharedPreferences("cz.michaelbrabec.fossbakalari", MODE_PRIVATE).edit().clear().commit()) {
                startLogin();
            } else {
                Toast.makeText(this, "Nastala neznámá chyba", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setTitle(item.getTitle());

        if (!navigationView.getMenu().findItem(id).isChecked()) {

            if (id == R.id.nav_ukoly) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new UkolyFragment()).commit();

            } else if (id == R.id.nav_rozvrh) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new RozvrhFragment()).commit();

            } else if (id == R.id.nav_znamky) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new ZnamkyFragment()).commit();
            }

            if (id == R.id.nav_ukoly) {
                appBarLayout.setElevation(0);
            } else {
                appBarLayout.setElevation(defaultElevation);
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
