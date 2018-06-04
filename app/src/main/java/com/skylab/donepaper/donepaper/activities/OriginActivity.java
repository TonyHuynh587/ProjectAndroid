package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.DeviceTokenReponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OriginActivity extends AbstractBaseAcitivity {

    private static final int TO_LOGIN = 0;
    private static final int TO_ORDER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_origin;
    }

    @Override
    protected void initViews() {

    }

    public void gotoLogin(View view) {
        startActivityForResult(new Intent(this, LoginActivity.class), TO_LOGIN);
    }

    public void gotoOrderProcess(View view) {
        Call<DPResponse<DeviceTokenReponse>> call = DonePaperClient.getApiService()
                .registerDeviceTokenWithoutId(FirebaseInstanceId.getInstance().getToken(), 2, 2);
        call.enqueue(new Callback<DPResponse<DeviceTokenReponse>>() {
            @Override
            public void onResponse(Call<DPResponse<DeviceTokenReponse>> call, Response<DPResponse<DeviceTokenReponse>> response) {
                if (response.isSuccessful()){
                    Log.e("DeviceToken", response.body().getData().getDeviceToken() + " 123");
                }
            }

            @Override
            public void onFailure(Call<DPResponse<DeviceTokenReponse>> call, Throwable throwable) {

            }
        });
        startActivityForResult(new Intent(this, OrderProcessActivity.class), TO_ORDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TO_LOGIN){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (resultCode == RESULT_OK && requestCode == TO_ORDER){
            finish();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
