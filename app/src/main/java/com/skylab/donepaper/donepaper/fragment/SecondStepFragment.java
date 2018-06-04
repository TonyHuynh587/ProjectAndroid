package com.skylab.donepaper.donepaper.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.adapter.PriceListAdapter;
import com.skylab.donepaper.donepaper.data.OrderManager;
import com.skylab.donepaper.donepaper.fragment.abstracted.AbstractBaseFragment;
import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.Coupon;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondStepFragment extends AbstractBaseFragment {

    private static final int[] deadlineValues = {12, 24, 36, 48, 72, 120};

    private View rootView;

    //Academic level
    private SegmentedGroup levelSegmentedButtonGroup;
    private RadioButton highSchoolLevel, collegeLevel, universityLevel;

    //Number of pages
    private int pages;
    private Button subButton, plusButton;
    private TextView pagesText, wordsText;
    private SegmentedGroup spaceSegmentedButtonGroup;
    private RadioButton singleButton, doubleButton;

    //Deadline
    private ListView priceListView;
    private Button contactText;
    private PriceListAdapter priceListAdapter;

    //Coupon
    private EditText couponInput;
    private Button applyButton;
    private TextView couponSuccess;

    //Total price
    private TextView totalPriceText;
    private Button continueButton;

    @Inject
    public NavigationManager mNavigationManager;

    @Inject
    public OrderManager mOrderManager;

    private boolean isDoubleSpacing;
    private int mSelectedPrice;
    private double couponPercent = 0;

    public static SecondStepFragment newInstance() {
        return new SecondStepFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getActivity().getApplication()).getMainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_second_step, container, false);
            initViews();
        }

        //set default segmented button
        if (mNavigationManager.getDefaultSpacingOptions().equals("double")) {
            switch (mNavigationManager.getDefaultAcademicLevel()) {
                case "highschool":
                    levelSegmentedButtonGroup.check(R.id.high_school_level);
                    priceListAdapter.setPriceList(mNavigationManager.getHighSchoolPriceList());
                    break;
                case "college":
                    levelSegmentedButtonGroup.check(R.id.college_level);
                    priceListAdapter.setPriceList(mNavigationManager.getCollegePriceList());
                    break;
                case "university":
                    levelSegmentedButtonGroup.check(R.id.university_level);
                    priceListAdapter.setPriceList(mNavigationManager.getUniversityPriceList());
                    break;
            }
        } else {
            switch (mNavigationManager.getDefaultAcademicLevel()) {
                case "highschool":
                    levelSegmentedButtonGroup.check(R.id.high_school_level);
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getHighSchoolPriceList()));
                    Log.e("Test List Price", doubleValue(mNavigationManager.getHighSchoolPriceList()).toString());
                    break;
                case "college":
                    levelSegmentedButtonGroup.check(R.id.college_level);
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getCollegePriceList()));
                    break;
                case "university":
                    levelSegmentedButtonGroup.check(R.id.university_level);
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getUniversityPriceList()));
                    break;
            }
        }

        //set default spacing
        if (mNavigationManager.getDefaultSpacingOptions().equals("double")) {
            spaceSegmentedButtonGroup.check(R.id.double_page);
            isDoubleSpacing = true;
        }

        //set default number of pages
        pages = mNavigationManager.getDefaultNumberOfPages();
        pagesText.setText(String.valueOf(pages));

        //set default words
        wordsText.setText(String.valueOf(pages * mNavigationManager.getWordsPerPage()));

        //caculate price
        caculatePrice();

        levelSegmentedButtonGroup.check(R.id.college_level);

        highSchoolLevel.setOnClickListener(this);
        collegeLevel.setOnClickListener(this);
        universityLevel.setOnClickListener(this);

        contactText.setOnClickListener(this);

        subButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);

        singleButton.setOnClickListener(this);
        doubleButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        priceListView.setAdapter(priceListAdapter);
        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedPrice = position;
                priceListAdapter.setSelectedPosition(position);
                priceListAdapter.notifyDataSetChanged();
                caculatePrice();
            }
        });

        return rootView;
    }

    private ArrayList<Double> doubleValue(ArrayList<Double> arrayList) {
        ArrayList<Double> newArraylist = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            newArraylist.add(arrayList.get(i) * 2);
        }
        return newArraylist;
    }

    private void initViews() {

        levelSegmentedButtonGroup = (SegmentedGroup) rootView.findViewById(R.id.level_segmented_button);
        highSchoolLevel = (RadioButton) rootView.findViewById(R.id.high_school_level);
        collegeLevel = (RadioButton) rootView.findViewById(R.id.college_level);
        universityLevel = (RadioButton) rootView.findViewById(R.id.university_level);

        subButton = (Button) rootView.findViewById(R.id.sub_button);
        plusButton = (Button) rootView.findViewById(R.id.plus_button);
        pagesText = (TextView) rootView.findViewById(R.id.num_of_pages_text);
        wordsText = (TextView) rootView.findViewById(R.id.num_of_words_text);
        spaceSegmentedButtonGroup = (SegmentedGroup) rootView.findViewById(R.id.spacing_segmented_button);
        singleButton = (RadioButton) rootView.findViewById(R.id.single_page);
        doubleButton = (RadioButton) rootView.findViewById(R.id.double_page);

        priceListView = (ListView) rootView.findViewById(R.id.price_per_deadline_list);
        contactText = (Button) rootView.findViewById(R.id.contact_text);

        priceListAdapter = new PriceListAdapter(getActivity(), mNavigationManager.getDeadlineList(),
                mNavigationManager.getHighSchoolPriceList());

        couponInput = (EditText) rootView.findViewById(R.id.coupon_input_text);
        applyButton = (Button) rootView.findViewById(R.id.apply_button);
        couponSuccess = (TextView) rootView.findViewById(R.id.coupon_success_text);

        totalPriceText = (TextView) rootView.findViewById(R.id.total_price_text);
        continueButton = (Button) rootView.findViewById(R.id.continue_button);
    }

    private void subPage() {
        if (pages > 1) {
            pages--;
            pagesText.setText(String.valueOf(pages));
            //caculate words
            wordsText.setText(String.valueOf(mNavigationManager.getWordsPerPage() * pages));
            mNavigationManager.setDefaultNumberOfPages(pages);
        }
    }

    private void plusPage() {
        pages++;
        pagesText.setText(String.valueOf(pages));
        //caculate words
        wordsText.setText(String.valueOf(mNavigationManager.getWordsPerPage() * pages));
        mNavigationManager.setDefaultNumberOfPages(pages);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_page:
                switch (mNavigationManager.getDefaultAcademicLevel()) {
                    case "highschool":
                        priceListAdapter.setPriceList(doubleValue(mNavigationManager.getHighSchoolPriceList()));
                        break;
                    case "college":
                        priceListAdapter.setPriceList(doubleValue(mNavigationManager.getCollegePriceList()));
                        break;
                    case "university":
                        priceListAdapter.setPriceList(doubleValue(mNavigationManager.getUniversityPriceList()));
                        break;
                }
                isDoubleSpacing = false;
                mNavigationManager.setDefaultSpacingOptions("single");
                caculatePrice();
                break;
            case R.id.double_page:
                isDoubleSpacing = true;
                mNavigationManager.setDefaultSpacingOptions("double");
                switch (mNavigationManager.getDefaultAcademicLevel()) {
                    case "highschool":
                        priceListAdapter.setPriceList(mNavigationManager.getHighSchoolPriceList());
                        break;
                    case "college":
                        priceListAdapter.setPriceList(mNavigationManager.getCollegePriceList());
                        break;
                    case "university":
                        priceListAdapter.setPriceList(mNavigationManager.getUniversityPriceList());
                        break;
                }
                caculatePrice();
                break;
            case R.id.high_school_level:
                if (mNavigationManager.getDefaultSpacingOptions().equals("double")) {
                    priceListAdapter.setPriceList(mNavigationManager.getHighSchoolPriceList());
                } else {
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getHighSchoolPriceList()));
                }
                mNavigationManager.setDefaultAcademicLevel("highschool");
                caculatePrice();
                break;
            case R.id.college_level:
                if (mNavigationManager.getDefaultSpacingOptions().equals("double")) {
                    priceListAdapter.setPriceList(mNavigationManager.getCollegePriceList());
                } else {
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getCollegePriceList()));
                }
                mNavigationManager.setDefaultAcademicLevel("college");
                caculatePrice();
                break;
            case R.id.university_level:
                if (mNavigationManager.getDefaultSpacingOptions().equals("double")) {
                    priceListAdapter.setPriceList(mNavigationManager.getUniversityPriceList());
                } else {
                    priceListAdapter.setPriceList(doubleValue(mNavigationManager.getUniversityPriceList()));
                }
                mNavigationManager.setDefaultAcademicLevel("university");
                caculatePrice();
                break;
            case R.id.sub_button:
                subPage();
                caculatePrice();
                break;
            case R.id.plus_button:
                plusPage();
                caculatePrice();
                break;
            case R.id.apply_button:
                Call<DPResponse<Coupon>> call = DonePaperClient.getApiService().getCouponInfo(couponInput.getText().toString().trim());
                call.enqueue(new Callback<DPResponse<Coupon>>() {
                    @Override
                    public void onResponse(Call<DPResponse<Coupon>> call, Response<DPResponse<Coupon>> response) {
                        if (response.isSuccessful()) {
                            couponPercent = (double) response.body().getData().getValue() / 100;
                            if (response.body().getData().getValue() != 0) {
                                couponSuccess.setVisibility(View.VISIBLE);
                                String percentCoupon = response.body().getData().getValue() + "%";
                                showNotiDialog("Success", "Discount applied: " + percentCoupon);
                                couponSuccess.setText(percentCoupon);
                            } else {
                                couponSuccess.setVisibility(View.GONE);
                                showNotiDialog("Error", "Your coupon is invalid or expire");
                            }

                            caculatePrice();
                            Log.e("ProcessOrder", response.body().getData().getValue() + "");
                        } else {
                            Log.e("Error: ", response.body().getErrors().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<DPResponse<Coupon>> call, Throwable throwable) {
                        Log.e("Error: ", throwable.getMessage());
                    }
                });
                break;
            case R.id.contact_text:
                gotoContact();
                break;
            case R.id.continue_button:
                mNavigationManager.nextFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        int number = Integer.parseInt(pagesText.getText().toString());
        String spacing = "double";
        switch (spaceSegmentedButtonGroup.getCheckedRadioButtonId()) {
            case R.id.single_page:
                spacing = "single";
                break;
            case R.id.double_page:
                spacing = "double";
                break;
        }
        int deadline = deadlineValues[mSelectedPrice];
        String couponCode = couponInput.getText().toString();
        String academicLevel = "";
        switch (levelSegmentedButtonGroup.getCheckedRadioButtonId()) {
            case R.id.high_school_level:
                academicLevel = "highschool";
                break;
            case R.id.college_level:
                academicLevel = "college";
                break;
            case R.id.university_level:
                academicLevel = "university";
                break;
        }
        mOrderManager.updateFromStepTwo(academicLevel, number, spacing, deadline, couponCode);

        super.onPause();
    }

    private void gotoContact() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@donepaper.com"});
        try {
            startActivity(Intent.createChooser(i, "contact@donepaper.com"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void caculatePrice() {
        double total = 0;
        switch (levelSegmentedButtonGroup.getCheckedRadioButtonId()) {
            case R.id.high_school_level:
                if (isDoubleSpacing) {
                    total = mNavigationManager.getHighSchoolPriceList().get(mSelectedPrice) * pages;
                } else {
                    total = 2 * mNavigationManager.getHighSchoolPriceList().get(mSelectedPrice) * pages;
                }
                break;
            case R.id.college_level:
                if (isDoubleSpacing) {
                    total = mNavigationManager.getCollegePriceList().get(mSelectedPrice) * pages;
                } else {
                    total = 2 * mNavigationManager.getCollegePriceList().get(mSelectedPrice) * pages;
                }
                break;
            case R.id.university_level:
                if (isDoubleSpacing) {
                    total = mNavigationManager.getUniversityPriceList().get(mSelectedPrice) * pages;
                } else {
                    total = 2 * mNavigationManager.getUniversityPriceList().get(mSelectedPrice) * pages;
                }
                break;
        }

        if (couponPercent != 0) {
            total = total * (1 - couponPercent);
        }
        String totalPrice = "$" + new DecimalFormat("##.##").format(total);
        totalPriceText.setText(totalPrice);
        mNavigationManager.setTotalPrice(totalPrice);
    }

    private void showNotiDialog(String title, String content) {

        final Dialog confirmDialogLayout = new Dialog(getActivity());
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

}
