package com.skylab.donepaper.donepaper.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.ForgotPassActivity;
import com.skylab.donepaper.donepaper.activities.LoginActivity;
import com.skylab.donepaper.donepaper.activities.OrderDetailActivity;
import com.skylab.donepaper.donepaper.activities.OrderProcessActivity;
import com.skylab.donepaper.donepaper.activities.TermsActivity;
import com.skylab.donepaper.donepaper.data.OrderManager;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.fragment.abstracted.AbstractBaseFragment;
import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.RetrofitHelper;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.DeviceTokenReponse;
import com.skylab.donepaper.donepaper.rest.model.ResponseOrder;
import com.skylab.donepaper.donepaper.rest.model.TokenData;

import java.util.Objects;

import javax.inject.Inject;

import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity.isValidEmail;
import static com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity.isValidPassword;

public class ThirdStepFragment extends AbstractBaseFragment {
    SegmentedGroup typeMemberSegmentedButton;
    RadioButton returnMemberButton, newMemberButton;
    Button placeOrderButton;

    TextView totalPriceText, totalPriceTextNot;

    //Return customer
    LinearLayout returnCustomerLayout;
    EditText emailInputText, passInputText;
    TextView termsOfServiceText, forgotPassText;
    TextInputLayout emailInputLayout, passInputLayout;

    //New customer
    LinearLayout newCustomerLayout;
    EditText nameInputText, newEmailInputText, newPassInputText, confirmPassInputText;
    TextView newTermsOfServiceText;
    TextInputLayout newEmailInputLayout, newPassInputLayout, nameInputLayout;

    //logged fragment
    TextView loggedTermsOfServiceText, chooseAnotherAccountText, userText, emailText;

    NotLoggedListener notLoggedListener = new NotLoggedListener();

    View mRootView;

    public static final int CHANGE_ACCOUNT_CODE = 3;

    @Inject
    public OrderManager mOrderManager;

    @Inject
    public UserManager mUserManager;

    @Inject
    public NavigationManager mNavigationManager;

    @Inject
    public SharedPreferences sharedPreferences;

    public static ThirdStepFragment newInstance() {
        return new ThirdStepFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getActivity().getApplication()).getMainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mUserManager.getUser() == null) {
            mRootView = inflater.inflate(R.layout.fragment_third_step_not_logged, container, false);
            initNotLogInViews();
        } else {
            mRootView = inflater.inflate(R.layout.fragment_third_step_logged, container, false);
            initLoggedViews();
        }

        return mRootView;
    }

    private void initNotLogInViews() {
        typeMemberSegmentedButton = (SegmentedGroup) mRootView.findViewById(R.id.type_member_segmented_button);
        returnMemberButton = (RadioButton) mRootView.findViewById(R.id.return_customer);
        newMemberButton = (RadioButton) mRootView.findViewById(R.id.new_customer);
        placeOrderButton = (Button) mRootView.findViewById(R.id.place_order_button);
        totalPriceTextNot = (TextView) mRootView.findViewById(R.id.total_price_step3_not);

        //Return customer
        returnCustomerLayout = (LinearLayout) mRootView.findViewById(R.id.return_customer_layout);
        emailInputText = (EditText) mRootView.findViewById(R.id.email_input_text);
        passInputText = (EditText) mRootView.findViewById(R.id.pass_input_text);
        termsOfServiceText = (TextView) mRootView.findViewById(R.id.terms_of_service_text);
        forgotPassText = (TextView) mRootView.findViewById(R.id.forgot_pass_text);
        emailInputLayout = (TextInputLayout) mRootView.findViewById(R.id.email_input_layout);
        passInputLayout = (TextInputLayout) mRootView.findViewById(R.id.return_pass_input_layout);

        //New customer
        newCustomerLayout = (LinearLayout) mRootView.findViewById(R.id.new_customer_layout);
        nameInputText = (EditText) mRootView.findViewById(R.id.name_input_text);
        newEmailInputText = (EditText) mRootView.findViewById(R.id.new_email_input_text);
        newPassInputText = (EditText) mRootView.findViewById(R.id.new_pass_input_text);
        confirmPassInputText = (EditText) mRootView.findViewById(R.id.confirm_pass_input_text);
        newTermsOfServiceText = (TextView) mRootView.findViewById(R.id.new_terms_of_service_text);
        nameInputLayout = (TextInputLayout) mRootView.findViewById(R.id.name_input_layout);
        newEmailInputLayout = (TextInputLayout) mRootView.findViewById(R.id.new_email_input_layout);
        newPassInputLayout = (TextInputLayout) mRootView.findViewById(R.id.new_pass_input_layout);

        //Set listener
        returnMemberButton.setOnClickListener(notLoggedListener);
        newMemberButton.setOnClickListener(notLoggedListener);
        termsOfServiceText.setOnClickListener(notLoggedListener);
        newTermsOfServiceText.setOnClickListener(notLoggedListener);

        forgotPassText.setOnClickListener(notLoggedListener);
        placeOrderButton.setOnClickListener(notLoggedListener);

        totalPriceTextNot.setText(mNavigationManager.getTotalPrice());
    }

    private void initLoggedViews() {
        placeOrderButton = (Button) mRootView.findViewById(R.id.place_order_button);
        userText = (TextView) mRootView.findViewById(R.id.current_user_name_text);
        emailText = (TextView) mRootView.findViewById(R.id.current_user_email_text);
        loggedTermsOfServiceText = (TextView) mRootView.findViewById(R.id.logged_terms_of_service_text);
        chooseAnotherAccountText = (TextView) mRootView.findViewById(R.id.choose_another_account_text);
        totalPriceText = (TextView) mRootView.findViewById(R.id.total_price_step3);

        userText.setText(mUserManager.getUser().getName());
        emailText.setText(mUserManager.getUser().getEmail());
        totalPriceText.setText(mNavigationManager.getTotalPrice());

        placeOrderButton.setOnClickListener(this);
        loggedTermsOfServiceText.setOnClickListener(this);
        chooseAnotherAccountText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.place_order_button:
                placeOrder();
                break;
            case R.id.logged_terms_of_service_text:
                gotoTermsOfService();
                break;
            case R.id.choose_another_account_text:
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), CHANGE_ACCOUNT_CODE);
                break;
        }
    }

    private void gotoTermsOfService() {
        startActivity(new Intent(getActivity(), TermsActivity.class));
    }

    private void gotoForgotPass() {
        startActivity(new Intent(getActivity(), ForgotPassActivity.class));
    }

    private void showReturnCustomerLayout() {
        newCustomerLayout.setVisibility(View.GONE);
        returnCustomerLayout.setVisibility(View.VISIBLE);
    }

    private void showNewCustomerLayout() {
        newCustomerLayout.setVisibility(View.VISIBLE);
        returnCustomerLayout.setVisibility(View.GONE);
    }

    private class NotLoggedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_customer:
                    showReturnCustomerLayout();
                    break;
                case R.id.new_customer:
                    showNewCustomerLayout();
                    break;
                case R.id.forgot_pass_text:
                    gotoForgotPass();
                    break;
                case R.id.terms_of_service_text:
                    gotoTermsOfService();
                    break;
                case R.id.new_terms_of_service_text:
                    gotoTermsOfService();
                    break;
                case R.id.place_order_button:
                    ((OrderProcessActivity) getActivity()).showProgress(true);
                    Call<DPResponse<TokenData>> call = null;
                    if (returnMemberButton.isChecked()) {
                        if (isReturnCustomerNotValid()) {
                            ((OrderProcessActivity) getActivity()).showProgress(false);
                        } else {
                            call = DonePaperClient.getApiService()
                                    .login(emailInputText.getText().toString().trim(),
                                            passInputText.getText().toString().trim());
                        }
                    } else if (newMemberButton.isChecked()) {
                        if (isNewCustomerNotValid()) {
                            ((OrderProcessActivity) getActivity()).showProgress(false);
                        } else {
                            call = DonePaperClient.getApiService().signup(newEmailInputText.getText().toString(),
                                    nameInputText.getText().toString(), newPassInputText.getText().toString());
                        }
                    }
                    if (call != null) {
                        call.enqueue(new Callback<DPResponse<TokenData>>() {

                            @Override
                            public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                                ((OrderProcessActivity) getActivity()).showProgress(false);

                                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                                    mUserManager.init(response.body().getData(), sharedPreferences);
                                    sendDeviceToken(response.body().getData().getToken());
                                    placeOrder();
                                    Log.e("Sign up", response.body().getData().getToken());
                                } else {
                                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                                    Log.e("error: ", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<DPResponse<TokenData>> call, Throwable throwable) {
                                ((OrderProcessActivity) getActivity()).showProgress(false);
                                Log.e("error: ", throwable.getMessage());
                                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }
    }

    private void sendDeviceToken(String userToken){
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

    private void placeOrder() {
        MultipartBody.Part filePart = null;

        if (mOrderManager.hasAttachment()) {
            filePart = RetrofitHelper.prepareFilePart(getActivity(), "files[]",
                    mOrderManager.getAttachmentUri());
        }

        Call<DPResponse<ResponseOrder>> call = DonePaperClient.getApiService()
                .createOrder(
                        mUserManager.getUser().getToken(),
                        mOrderManager.requestBodyMap(),
                        filePart
                );


        call.enqueue(new Callback<DPResponse<ResponseOrder>>() {
            @Override
            public void onResponse(Call<DPResponse<ResponseOrder>> call, Response<DPResponse<ResponseOrder>> response) {
                ((OrderProcessActivity) getActivity()).showProgress(false);
                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                    Log.e("Response", response.toString());
                    ((OrderProcessActivity) getActivity()).showProgress(false);
                    gotoPreviewOrder(response.body().getData().getId());
                } else {
                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DPResponse<ResponseOrder>> call, Throwable throwable) {
                ((OrderProcessActivity) getActivity()).showProgress(false);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoPreviewOrder(int id) {

        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("isPreview",true);
        intent.putExtra("OrderId", id);
        startActivity(intent);
        getActivity().finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_ACCOUNT_CODE && resultCode == RESULT_OK) {
            userText.setText(mUserManager.getUser().getName());
            emailText.setText(mUserManager.getUser().getEmail());
        }
    }

    private boolean checkEmail(EditText emailInputText, TextInputLayout emailInputLayout) {
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

    private boolean checkPassword(EditText passInputText, TextInputLayout passInputLayout) {
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

    private boolean checkName(EditText nameInputText, TextInputLayout nameInputLayout) {
        if (!nameInputText.getText().toString().isEmpty()) {
            nameInputLayout.setErrorEnabled(false);
            return true;
        } else {
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError("Name cannot be empty");
            requestFocus(nameInputText);
            return false;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean isReturnCustomerNotValid() {
        return !checkEmail(emailInputText, emailInputLayout) || !checkPassword(passInputText, passInputLayout);
    }

    private boolean isNewCustomerNotValid() {

        return !checkName(nameInputText, nameInputLayout)
                || !checkEmail(newEmailInputText, newEmailInputLayout)
                || !checkPassword(newPassInputText, newPassInputLayout);
    }
}

