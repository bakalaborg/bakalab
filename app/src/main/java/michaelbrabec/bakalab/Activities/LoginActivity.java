package michaelbrabec.bakalab.Activities;


import android.content.Intent;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import michaelbrabec.bakalab.Interfaces.Callback;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.Utils.Login;
import michaelbrabec.bakalab.Utils.SharedPrefHandler;

public class LoginActivity extends AppCompatActivity implements Callback {

    TextView statusText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Toast.makeText(LoginActivity.this, "Ještě ne", Toast.LENGTH_SHORT).show();
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

                    Login login = new Login(Objects.requireNonNull(textBakalari.getText()).toString(),
                            Objects.requireNonNull(textJmeno.getText()).toString(),
                            Objects.requireNonNull(textHeslo.getText()).toString(), LoginActivity.this,
                            LoginActivity.this);

                    login.getResult();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.fill_in), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startBakalari(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCallbackFinish(Object result) {

        if (result.equals("success")) {
            startBakalari();
        } else {
            statusText.setText((String)result);
            progressBar.setVisibility(View.GONE);
        }
    }
}
