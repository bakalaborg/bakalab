package michaelbrabec.bakalab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.utils.BakaTools;
import michaelbrabec.bakalab.utils.SharedPrefHandler;

public class MainActivity extends AppCompatActivity {

    Float defaultElevation;
    NavigationView navigationView;
    Toolbar toolbar;
    NavController navController;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BakaTools.getToken(this) == null) {
            startLogin();
        }

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        defaultElevation = getResources().getDimension(R.dimen.toolbar_elevation);


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
        navController.restoreState(savedInstanceState);
        NavigationUI.setupWithNavController(navigationView, navController);
        /* Set top level fragment = dont show up button */
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                        R.id.mainScreenFragment,
                        R.id.znamkyFragment,
                        R.id.ukolyFragment,
                        R.id.rozvrhFragment)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.ukolyFragment) {
                    toolbar.setElevation(0);
                } else {
                    toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
                }
            }
        });

        String loginJmeno = SharedPrefHandler.getString(this, "loginJmeno");

        TextView navJmeno = navigationView.getHeaderView(0).findViewById(R.id.loginJmeno);

        navJmeno.setText(loginJmeno);


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

    /* this method is used by the menu xml, its workaround for fading animation when using navigation controller */
    public void openSettings(MenuItem item) {
        startActivity(new Intent(MainActivity.this, PreferenceActivity.class));
        drawer.closeDrawers();
    }
}
