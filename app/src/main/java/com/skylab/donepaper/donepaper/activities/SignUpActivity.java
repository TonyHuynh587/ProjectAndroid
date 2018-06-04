package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
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

public class SignUpActivity extends AbstractBaseAcitivity {

    private ImageView closeImage;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passInputLayout;
    private TextInputEditText nameInputText, emailInputText, passInputText;
    private TextView signinText;
    private Button signupButton;


    @Inject
    public UserManager mUserManager;

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        signupButton.setOnClickListener(this);
        signinText.setOnClickListener(this);
        closeImage.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initViews() {
        closeImage = (ImageView) findViewById(R.id.close_button_image);
        nameInputText = (TextInputEditText) findViewById(R.id.sign_up_name_input_text);
        emailInputText = (TextInputEditText) findViewById(R.id.sign_up_email_input_text);
        passInputText = (TextInputEditText) findViewById(R.id.sign_up_pass_input_text);
        TextInputLayout nameInputLayout = (TextInputLayout) findViewById(R.id.name_input_layout);
        emailInputLayout = (TextInputLayout) findViewById(R.id.email_input_layout);
        passInputLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        signinText = (TextView) findViewById(R.id.sign_in_text);
        signupButton = (Button) findViewById(R.id.sign_up_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                if (!checkEmail(emailInputText, emailInputLayout)) {
                    emailInputText.setAnimation(animShake);
                    emailInputText.startAnimation(animShake);
                    vib.vibrate(200);
                } else if (!checkPassword(passInputText, passInputLayout)) {
                    passInputText.setAnimation(animShake);
                    passInputLayout.startAnimation(animShake);
                    vib.vibrate(200);
                } else {
                    contactServer();
                }

                break;
            case R.id.sign_in_text:
                gotoLogin();
                break;
            case R.id.close_button_image:
                onBackPressed();
                break;
        }
    }

    private void contactServer() {
        String email = emailInputText.getText().toString().trim();
        String name = nameInputText.getText().toString().trim();
        String pass = passInputText.getText().toString().trim();

        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<TokenData>> call = DonePaperClient.getApiService().signup(email, name, pass);
            call.enqueue(new Callback<DPResponse<TokenData>>() {
                @Override
                public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                    if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                        mUserManager.init(response.body().getData(), sharedPreferences);
                        sendDeviceToken(response.body().getData().getToken());
                        Log.e("Sign up", response.body().getData().getToken());
                        Toast.makeText(SignUpActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                        gotoProcessOrder();
                    } else {
                        Log.e("error: ", response.message());
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<TokenData>> call, Throwable throwable) {
                    Log.e("error: ", throwable.getMessage());
                }
            });
        } else {
            showNotiDialog("Error", "No internet connection");
        }
    }

    private void gotoLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private void gotoProcessOrder() {
        setResult(RESULT_OK);
        startActivity(new Intent(SignUpActivity.this, OrderProcessActivity.class));
        finish();
    }
}
