package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.rest.model.PaypalInfo;
import com.skylab.donepaper.donepaper.utils.ColorUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.skylab.donepaper.donepaper.R.id.text_view_order_detail_revision_left_value;

public class OrderDetailActivity extends AbstractBaseAcitivity {
    private Toolbar toolbar;
    private TextView titleToolbar;
    private TextView orderNumber,date,status,deadline;
    private TextView name,subject,type,citationStyle,instruction,serviceType;
    private TextView pages,spacing,revisionLeft,academyLevel;
    private TextView total;
    private WebView webView;
    private Button paypalPay;
    private String TAG = this.getClass().getSimpleName();
    private List<OrderData.PostsBean> listPostMessages;
    private PaypalInfo paypalInfo;

    @Inject
    public UserManager mUserManager;

    OrderData orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initViews() {
        initView();
        initToolbar();
        showProgress(true);
    }


    public void initToolbar(){
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent.hasExtra("OrderId")){

            int orderId = intent.getExtras().getInt("OrderId");
            Call<DPResponse<OrderData>> call = DonePaperClient.getApiService().getOrderDetailByOrderId(orderId,mUserManager.getUser().getToken());
            call.enqueue(new Callback<DPResponse<OrderData>>() {
                @Override
                public void onResponse(Call<DPResponse<OrderData>> call, Response<DPResponse<OrderData>> response) {
                    if(response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")){
                        orderData = response.body().getData();
                        setDataToLayout(orderData);
                        listPostMessages = orderData.getPosts();
                        paypalInfo = response.body().getData().getPaypalInfo();
                        if(paypalInfo.getInputs() != null && paypalInfo.getInputs().size() > 0){

                            //Set config data for posting params to paypal
                            final String url = paypalInfo.getEndpoint();
                            StringBuilder stringBuffer = new StringBuilder();
                            for(PaypalInfo.Input i : paypalInfo.getInputs()){
                                stringBuffer.append(i.getName()).append("=").append(i.getValue()).append("&");
                            }
                            String postData = stringBuffer.toString();
                            if(postData.endsWith("&"))
                            {
                                postData = postData.substring(0,postData.length() - 1);
                            }
                            //-------
                            final String finalPostData = postData;
                            Log.e(TAG,finalPostData);
                            //Set onclick paypal payment
                            paypalPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    webView.postUrl(url, finalPostData.getBytes());
                                }
                            });

                        }else{
                            paypalPay.setVisibility(View.GONE);
                        }

                    }

                    showProgress(false);
                }

                @Override
                public void onFailure(Call<DPResponse<OrderData>> call, Throwable throwable) {
                    Log.e(TAG,throwable.getMessage());
                    showProgress(false);
                    showNotiDialog("Error", throwable.getMessage());
                }
            });

        }else{
            finish();
        }
    }

    public void setDataToLayout(OrderData data){
        orderNumber.setText(String.valueOf(data.getId()));
        date.setText(convertIntToDate(Integer.parseInt(data.getDatetime())));
        int color = ColorUtils.defineColor(orderData.getStatus());

        GradientDrawable gradientDrawable = (GradientDrawable)status.getBackground();
        gradientDrawable.setStroke(5,ContextCompat.getColor(this,color));

        //
        status.setText(data.getStatus());
        status.setTextColor(ContextCompat.getColor(this,color));
        String deadlineDetail = data.getDeadline() + " hours";
        deadline.setText(deadlineDetail);
        name.setText(data.getName());
        subject.setText(data.getSubject());
        type.setText(data.getPaper_type());
        citationStyle.setText(data.getCitation_style());
        instruction.setText(data.getInstructions());
        serviceType.setText(data.getService_type());
        pages.setText(String.valueOf(data.getNum_pages()));
        spacing.setText(data.getSpacing());
        academyLevel.setText(data.getAcademic_level());
        revisionLeft.setText(String.valueOf(data.getRevisions_left()));
        String totalPrice = "Total: $" + data.getPrice();
        total.setText(totalPrice);
    }

    public void initView(){
        webView = new WebView(this);
        toolbar = (Toolbar) findViewById(R.id.order_detail_activity_toolbar);
        titleToolbar = (TextView) findViewById(R.id.order_detail_title_toolbar);
        orderNumber = (TextView) findViewById(R.id.text_view_order_detail_number_value);
        date = (TextView) findViewById(R.id.text_view_order_detail_date_value);
        status = (TextView) findViewById(R.id.text_view_order_detail_status_value);
        deadline = (TextView) findViewById(R.id.text_view_order_detail_deadline_value);
        name = (TextView) findViewById(R.id.text_view_order_detail_name_value);
        subject = (TextView) findViewById(R.id.text_view_order_detail_subject_value);
        type = (TextView) findViewById(R.id.text_view_order_detail_type_value);
        citationStyle = (TextView) findViewById(R.id.text_view_order_detail_citation_style_value);
        instruction = (TextView) findViewById(R.id.text_view_order_detail_instruction_value);
        serviceType = (TextView) findViewById(R.id.text_view_order_detail_service_type_value);
        pages = (TextView) findViewById(R.id.text_view_order_detail_pages_value);
        spacing = (TextView) findViewById(R.id.text_view_order_detail_spacing_value);
        revisionLeft = (TextView) findViewById(text_view_order_detail_revision_left_value);
        academyLevel = (TextView) findViewById(R.id.text_view_order_detail_academy_level_value);
        total = (TextView) findViewById(R.id.order_detail_total);
        paypalPay = (Button) findViewById(R.id.pay_with_paypal_button);

    }

    public String convertIntToDate(int datetime){
        Date date = new Date(datetime*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format of your date
        return sdf.format(date);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_detail_menu_toolbar, menu);
        MenuItem chatItem = menu.findItem(R.id.action_chat);
        if(getIntent().hasExtra("isPreview") && getIntent().getExtras().getBoolean("isPreview")){
            titleToolbar.setText(R.string.order_detail_preview);
            chatItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_chat){
            Intent chatIntent = new Intent(OrderDetailActivity.this,ChatActivity.class);
            chatIntent.putExtra("order_id",orderData.getId());
            chatIntent.putExtra("PostMessages", (Serializable)listPostMessages);
            startActivity(chatIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
