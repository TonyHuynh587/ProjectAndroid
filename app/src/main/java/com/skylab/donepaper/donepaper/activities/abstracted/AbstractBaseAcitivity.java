package com.skylab.donepaper.donepaper.activities.abstracted;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.OriginActivity;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.DeviceTokenReponse;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Abstract class for all activities
 * Created by HungNguyen on 4/17/17.
 */

public abstract class AbstractBaseAcitivity extends AppCompatActivity implements View.OnClickListener {

    protected AlertDialog progressDialog;
    protected Animation animShake;
    protected Vibrator vib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //loading progressDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_progress_bar);
        builder.setCancelable(false);
        progressDialog = builder.create();
        initViews();
    }

    protected abstract int getLayoutResourceId();

    protected abstract void initViews();

    public void showProgress(final Boolean show){
        if (show && progressDialog.getWindow() != null) {
            progressDialog.show();
            progressDialog.getWindow().setLayout(350, 350);
        } else {
            progressDialog.cancel();
        }
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !(password.trim().isEmpty() || password.contains(" "));
    }

    protected boolean checkEmail(TextInputEditText emailInputText, TextInputLayout emailInputLayout){
        String email = emailInputText.getText().toString().trim();

        if (isValidEmail(email)) {
            emailInputLayout.setErrorEnabled(false);
            return true;
        } else {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Invalid Email");
            requestFocus(emailInputText);
            return false;
        }
    }

    protected boolean checkPassword(TextInputEditText passInputText, TextInputLayout passInputLayout){
        String password = passInputText.getText().toString();
        if (isValidPassword(password)) {
            passInputLayout.setErrorEnabled(false);
            return true;
        } else {
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password cannot be empty");
            requestFocus(passInputText);
            return false;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    protected void showNotiDialog(String title, String content) {

        final Dialog confirmDialogLayout = new Dialog(this);
        confirmDialogLayout.setContentView(R.layout.dialog_noti_layout);
        TextView titleDialog = (TextView) confirmDialogLayout.findViewById(R.id.noti_title_dialog_text);
        TextView contentDialog = (TextView) confirmDialogLayout.findViewById(R.id.noti_des_dialog_text);
        Button confirmDialog = (Button) confirmDialogLayout.findViewById(R.id.confirm_button);
        if (confirmDialogLayout.getWindow() != null)
            confirmDialogLayout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleDialog.setText(title);
        contentDialog.setText(content);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogLayout.dismiss();
            }
        });

        confirmDialogLayout.show();
    }

    protected void showLogoutDialog(String title, String content, final Context context) {

        final Dialog confirmDialogLayout = new Dialog(this);
        confirmDialogLayout.setContentView(R.layout.dialog_noti_layout);
        TextView titleDialog = (TextView) confirmDialogLayout.findViewById(R.id.noti_title_dialog_text);
        TextView contentDialog = (TextView) confirmDialogLayout.findViewById(R.id.noti_des_dialog_text);
        Button confirmDialog = (Button) confirmDialogLayout.findViewById(R.id.confirm_button);
        if (confirmDialogLayout.getWindow() != null)
            confirmDialogLayout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleDialog.setText(title);
        contentDialog.setText(content);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, OriginActivity.class));
                confirmDialogLayout.dismiss();
            }
        });

        confirmDialogLayout.show();
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

    public static void sendDeviceToken(String userToken){
        Call<DPResponse<DeviceTokenReponse>> call = DonePaperClient.getApiService()
                .registerDeviceTokenWithId(userToken, FirebaseInstanceId.getInstance().getToken(), 2, 2);
        call.enqueue(new Callback<DPResponse<DeviceTokenReponse>>() {
            @Override
            public void onResponse(Call<DPResponse<DeviceTokenReponse>> call, Response<DPResponse<DeviceTokenReponse>> response) {
                if (response.isSuccessful()){
                    Log.e("DeviceTokenLogin", response.body().getData().getDeviceToken() + " 123");
                }
            }

            @Override
            public void onFailure(Call<DPResponse<DeviceTokenReponse>> call, Throwable throwable) {

            }
        });
    }

}
