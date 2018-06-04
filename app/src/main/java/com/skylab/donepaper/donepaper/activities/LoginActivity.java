package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.TokenData;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AbstractBaseAcitivity {

    TextInputEditText emailInputText, passInputText;
    TextInputLayout emailInputLayout, passInputLayout;
    TextView resetPassText, signUpText;
    Button loginButton;
    ImageView closeButton;

    @Inject
    public UserManager mUserManager;

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        resetPassText.setOnClickListener(this);
        signUpText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        emailInputLayout = (TextInputLayout) findViewById(R.id.email_input_layout);
        passInputLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        emailInputText = (TextInputEditText) findViewById(R.id.email_input_text);
        passInputText = (TextInputEditText) findViewById(R.id.pass_input_text);
        resetPassText = (TextView) findViewById(R.id.reset_pass_text);
        signUpText = (TextView) findViewById(R.id.sign_up_text);
        loginButton = (Button) findViewById(R.id.login_button);
        closeButton = (ImageView) findViewById(R.id.close_button_image);
    }

    public void gotoSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (!checkEmail(emailInputText, emailInputLayout)) {
                    emailInputText.setAnimation(animShake);
                    emailInputText.startAnimation(animShake);
                    vib.vibrate(200);
                } else if (!checkPassword(passInputText, passInputLayout)) {
                    passInputText.setAnimation(animShake);
                    passInputText.startAnimation(animShake);
                    vib.vibrate(200);
                } else {
                    contactServer();
                }
                break;
            case R.id.reset_pass_text:
                gotoResetPass();
                break;
            case R.id.sign_up_text:
                gotoSignUp();
                break;
            case R.id.close_button_image:
                onBackPressed();
                break;
        }
    }

    private void gotoResetPass() {
        startActivity(new Intent(this, ForgotPassActivity.class));
    }

    private void contactServer() {
        String email = emailInputText.getText().toString().trim();
        String password = passInputText.getText().toString().trim();

        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<TokenData>> call = DonePaperClient.getApiService().login(email, password);
            call.enqueue(new Callback<DPResponse<TokenData>>() {
                @Override
                public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                    if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                        mUserManager.init(response.body().getData(), sharedPreferences);

                        sendDeviceToken(response.body().getData().getToken());
                        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showNotiDialog("Error", response.body().getErrors().get(0).getMsg());
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<TokenData>> call, Throwable throwable) {
                    Log.e("Error: ", throwable.getMessage());
                }
            });
        } else {
            showNotiDialog("Error", "No internet connection");
        }
    }
}
