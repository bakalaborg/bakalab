package org.bakalab.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import org.bakalab.app.R;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.interfaces.SkolyAPI;
import org.bakalab.app.items.LoginResponse;
import org.bakalab.app.items.skoly.Mesto;
import org.bakalab.app.items.skoly.MestoSkola;
import org.bakalab.app.items.skoly.Skola;
import org.bakalab.app.items.skoly.SkolyRoot;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.Login;
import org.bakalab.app.utils.SharedPrefHandler;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class LoginActivity extends AppCompatActivity implements Callback {

    TextView statusText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (BakaTools.getToken(this) != null) {
            BakaTools.resetToken();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        }

        final TextInputEditText textBakalari = findViewById(R.id.textBakalari);
        final TextInputEditText textJmeno = findViewById(R.id.textJmeno);
        final TextInputEditText textHeslo = findViewById(R.id.textHeslo);
        final Button databaze = findViewById(R.id.databaze_button);
        statusText = findViewById(R.id.textUnderButton);
        progressBar = findViewById(R.id.progress);
        final Button buttonLogin = findViewById(R.id.buttonLogin);

        databaze.setOnClickListener(v -> getSchoolCities());


        buttonLogin.setOnClickListener(view -> {

            if (!Objects.requireNonNull(textHeslo.getText()).toString().isEmpty() &&
                    !Objects.requireNonNull(textJmeno.getText()).toString().isEmpty() &&
                    !Objects.requireNonNull(textBakalari.getText()).toString().isEmpty()) {
                statusText.setText("");

                progressBar.setVisibility(View.VISIBLE);

                Login login = new Login(LoginActivity.this);
                login.execute(Objects.requireNonNull(textBakalari.getText()).toString(),
                        Objects.requireNonNull(textJmeno.getText()).toString(),
                        Objects.requireNonNull(textHeslo.getText()).toString());

            } else {
                statusText.setText(getString(R.string.fill_in));
            }
        });

    }

    private void getSchoolCities() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sluzby.bakalari.cz/api/v1/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        SkolyAPI skolyAPI = retrofit.create(SkolyAPI.class);

        Call<SkolyRoot> call = skolyAPI.getMesta();

        call.enqueue(new retrofit2.Callback<SkolyRoot>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<SkolyRoot> call, Response<SkolyRoot> response) {
                if (!response.isSuccessful()) {
                    Log.d("response.isSuccessful", response.message());
                    return;
                }
                if (response.body() != null) {
                    getSchools(response.body().mesta);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<SkolyRoot> call, Throwable t) {
                Log.d("onFailure()", t.getMessage());

            }
        });
    }

    private void getSchools(List<MestoSkola> mesta) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sluzby.bakalari.cz/api/v1/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        SkolyAPI skolyAPI = retrofit.create(SkolyAPI.class);

        for (MestoSkola m:
                mesta) {
            if (m.name == null) continue;
            Call<Mesto> call = skolyAPI.getMesto(m.name);

            call.enqueue(new retrofit2.Callback<Mesto>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Mesto> call, Response<Mesto> response) {
                    if (!response.isSuccessful()) {
                        Log.d("response.isSuccessful", response.message());
                        return;
                    }
                    if (response.body() != null) {
                        displayAllSchools(response.body());
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Mesto> call, Throwable t) {
                    Log.d("onFailure()", t.getMessage());

                }
            });
        }
    }

    private void displayAllSchools(Mesto mesto) {
        Log.e("name:", mesto.name);
        for (Skola s : mesto.skoly) {
            Log.e(" skola", s.name);
            Log.e(" url", s.schoolUrl);
        }
    }

    private void startBakalari() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCallbackFinish(Object result) {

        LoginResponse loginResponse = (LoginResponse) result;

        if (!loginResponse.wasSuccessful()) {
            statusText.setText(loginResponse.getErrorMessage());
            progressBar.setVisibility(View.GONE);
        } else {

            String[] successResponse = loginResponse.getSuccessResponse();
            // I am using this instead of handler because this is a lot of strings and it could be pretty slow to apply after each one
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("loginJmeno", successResponse[0]);
            editor.putString("loginSkola", successResponse[1]);
            editor.putString("loginTrida", successResponse[2]);
            editor.putString("loginRocnik", successResponse[3]);
            editor.putString("loginModuly", successResponse[4]);
            editor.putString("loginTyp", successResponse[5]);
            editor.putString("loginStrtyp", successResponse[6]);
            editor.putString("tokenBase", successResponse[7]);
            editor.putString("bakalariUrl", successResponse[8]);
            editor.apply();
            SharedPrefHandler.setString(this, "made", BakaTools.getToken(this));
            startBakalari();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
