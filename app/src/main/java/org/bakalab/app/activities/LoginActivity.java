package org.bakalab.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import org.bakalab.app.R;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.items.LoginResponse;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.Login;

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

        databaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusText.setText(R.string.placeholder);
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

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
            startBakalari();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
