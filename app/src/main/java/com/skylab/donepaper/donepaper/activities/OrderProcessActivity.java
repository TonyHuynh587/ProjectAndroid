package com.skylab.donepaper.donepaper.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.data.OrderManager;
import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.FormInfoData;
import com.skylab.donepaper.donepaper.utils.InternetConnection;
import com.skylab.donepaper.donepaper.view.StepsProgressBar;

import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.FIRST_STEP_INDEX;
import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.SECOND_STEP_INDEX;
import static com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager.THIRD_STEP_INDEX;

public class OrderProcessActivity extends AbstractBaseAcitivity implements NavigationManager.NavigationListener {
    private StepsProgressBar progressBar;

    @Inject
    public NavigationManager mNavigationManager;

    @Inject
    public OrderManager mOrderManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        super.onCreate(savedInstanceState);
        mNavigationManager.setNavigationListener(this);
        if (InternetConnection.checkConnection(getApplicationContext())) {
            Call<DPResponse<FormInfoData>> call = DonePaperClient.getApiService().getFormInfo();
            showProgress(true);
            call.enqueue(new Callback<DPResponse<FormInfoData>>() {
                @Override
                public void onResponse(Call<DPResponse<FormInfoData>> call, Response<DPResponse<FormInfoData>> response) {
                    showProgress(false);
                    if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {

                        FormInfoData formInfoData = response.body().getData();

                        mNavigationManager.pourData(formInfoData);

                        //init fragments
                        mNavigationManager.init(getFragmentManager());

                    } else {
                        Toast.makeText(OrderProcessActivity.this, "response fail", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<FormInfoData>> call, Throwable throwable) {
                    showProgress(false);
                    Toast.makeText(OrderProcessActivity.this, "fail:" + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(OrderProcessActivity.this, "No connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_process;
    }

    @Override
    protected void initViews() {
        //setup Toolbar
        Toolbar orderProcessToolbar = (Toolbar) findViewById(R.id.order_process_toolbar);
        this.setSupportActionBar(orderProcessToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        orderProcessToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (StepsProgressBar) findViewById(R.id.steps_progress_bar);
        progressBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOrderManager.isStepOneValid()) {
            switch (v.getId()) {
                case R.id.step_one_layout:
                    mNavigationManager.switchFragment(FIRST_STEP_INDEX);
                    break;
                case R.id.step_two_layout:
                    mNavigationManager.switchFragment(SECOND_STEP_INDEX);
                    break;
                case R.id.step_three_layout:
                    mNavigationManager.switchFragment(THIRD_STEP_INDEX);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mNavigationManager.reset();
        mOrderManager.clearOrder();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mNavigationManager.isFirstStep()) {
            showExitDialog();
        } else {
            mNavigationManager.previousFragment();
        }

    }

    protected void showExitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Cancel order?").setCancelable(true).setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).setNegativeButton(android.R.string.cancel, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackstackChanged() {
        progressBar.setCurrentStep(mNavigationManager.getCurrentStep());
        if (getSupportActionBar() != null) {
            if (mNavigationManager.isFirstStep()) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            } else {
                getSupportActionBar().setHomeAsUpIndicator(0);
            }
        }

    }
}
