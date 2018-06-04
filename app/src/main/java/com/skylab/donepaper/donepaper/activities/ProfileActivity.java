package com.skylab.donepaper.donepaper.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.DeviceTokenReponse;
import com.skylab.donepaper.donepaper.rest.model.TokenData;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AbstractBaseAcitivity{
    Toolbar toolbar;
    TextInputEditText email, username, oldPassword, newPassword;
    Button saveProfileButton;

    @Inject
    public UserManager mUserManager;

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        super.onCreate(savedInstanceState);

        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<TokenData>> call = DonePaperClient.getApiService().getProfile(mUserManager.getUser().getToken());
            call.enqueue(new Callback<DPResponse<TokenData>>() {
                @Override
                public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                    if (response.isSuccessful()) {
                        email.setText(response.body().getData().getEmail());
                        username.setText(response.body().getData().getName());
                        oldPassword.requestFocus();
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<TokenData>> call, Throwable t) {
                    showNotiDialog("Error", t.getMessage());
                }
            });
        } else {
            showNotiDialog("Error", "No internet connection");
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initViews() {
        initView();
        initToolbar();
    }

    public void initToolbar() {
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.profile_activity_toolbar);
        email = (TextInputEditText) findViewById(R.id.txt_email);
        username = (TextInputEditText) findViewById(R.id.txt_username);
        oldPassword = (TextInputEditText) findViewById(R.id.txt_old_pass);
        newPassword = (TextInputEditText) findViewById(R.id.txt_new_pass);
        saveProfileButton = (Button) findViewById(R.id.btn_profile_save);
        //set Text
        email.setText(mUserManager.getUser().getEmail());
        username.setText(mUserManager.getUser().getName());
        oldPassword.requestFocus();

        //set on click
        saveProfileButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu_toolbar, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_profile_save:
                updateProfile();
                break;
        }
    }

    private void updateProfile() {
        String emailProfile = email.getText().toString().trim();
        String usernameProfile = username.getText().toString().trim();
        String newPass;
        String oldPass;
        if (newPassword.getText().toString().trim().isEmpty()){
            newPass = null;
            oldPass = null;
        } else {
            newPass = newPassword.getText().toString().trim();
            oldPass = oldPassword.getText().toString().trim();
        }
        Call<DPResponse<TokenData>> call = DonePaperClient.getApiService().updateProfile(mUserManager.getUser().getToken(),
                emailProfile, usernameProfile, oldPass, newPass);
        call.enqueue(new Callback<DPResponse<TokenData>>() {
            @Override
            public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")){
                    mUserManager.clear();
                    mUserManager.init(response.body().getData(), sharedPreferences);
                    showNotiDialog("Success", "Your information has been updated");
                } else {
                    showNotiDialog("Error", response.body().getErrors().get(0).getMsg());
                }
            }

            @Override
            public void onFailure(Call<DPResponse<TokenData>> call, Throwable throwable) {
                Toast.makeText(ProfileActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                dialogLogOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeDeviceToken() {
        Call<DPResponse<DeviceTokenReponse>> call = DonePaperClient.getApiService().removeDeviceToken(FirebaseInstanceId.getInstance().getToken(), 2);
        call.enqueue(new Callback<DPResponse<DeviceTokenReponse>>() {
            @Override
            public void onResponse(Call<DPResponse<DeviceTokenReponse>> call, Response<DPResponse<DeviceTokenReponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this, OriginActivity.class));
                }
            }

            @Override
            public void onFailure(Call<DPResponse<DeviceTokenReponse>> call, Throwable t) {

            }
        });
    }

    private void dialogLogOut(){
        AlertDialog.Builder dialogExit = new AlertDialog.Builder(this);
        dialogExit.setMessage("Do you want to log out?");
        dialogExit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserManager.clear();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                removeDeviceToken();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = dialogExit.create();
        alertDialog.show();
    }
}
