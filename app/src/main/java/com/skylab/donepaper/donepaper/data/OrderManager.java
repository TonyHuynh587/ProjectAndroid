package com.skylab.donepaper.donepaper.data;

import android.net.Uri;

import com.skylab.donepaper.donepaper.rest.RetrofitHelper;
import com.skylab.donepaper.donepaper.rest.model.RequestOrder;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;


public class OrderManager {

    private Uri mAttachmentUri;

    private RequestOrder order = new RequestOrder();

    private int id;
    private String paperTitle = "";
    private String paperInstruction = "";

    public RequestOrder getOrder() {
        return order;
    }

    public void updateFromStepOne(String serviceType, String paperType, String subject, String name,
                                  String citationType, String instructions, Uri attachmentUri) {
        order.setServiceType(serviceType)
                .setPaperType(paperType)
                .setPaperSubject(subject)
                .setPaperName(name)
                .setCitationType(citationType)
                .setInstructions(instructions);
        this.mAttachmentUri = attachmentUri;
    }

    public void updateFromStepTwo(String level, int number, String spacing, int deadline, String code) {
        order.setAcademicLevel(level)
                .setNumPages(number)
                .setSpacing(spacing)
                .setDeadline(deadline)
                .setCouponCode(code);
    }

    public void clearOrder() {
        order = new RequestOrder();
        mAttachmentUri = null;
        paperTitle = "";
        paperInstruction = "";
    }

    public boolean hasAttachment() {
        return mAttachmentUri != null;
    }

    public Uri getAttachmentUri() {
        return mAttachmentUri;
    }

    public void setAttachmentUri(Uri mAttachmentUri) {
        this.mAttachmentUri = mAttachmentUri;
    }

    public String getPaperInstruction() {
        return paperInstruction;
    }

    public void setPaperInstruction(String paperInstruction) {
        this.paperInstruction = paperInstruction;
    }

    public Map<String, RequestBody> requestBodyMap() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("service_type", RetrofitHelper.createPartFromString(order.getServiceType()));
        map.put("paper_type", RetrofitHelper.createPartFromString(order.getPaperType()));
        map.put("paper_subject", RetrofitHelper.createPartFromString(order.getPaperSubject()));
        map.put("paper_name", RetrofitHelper.createPartFromString(order.getPaperName()));
        map.put("citation_style", RetrofitHelper.createPartFromString(order.getCitationType()));
        if (order.getInstructions() != null) {
            map.put("instructions", RetrofitHelper.createPartFromString(order.getInstructions()));
        }
        map.put("academic_level", RetrofitHelper.createPartFromString(order.getAcademicLevel()));
        map.put("spacing", RetrofitHelper.createPartFromString(order.getSpacing()));
        map.put("num_pages", RetrofitHelper.createPartFromString(String.valueOf(order.getNumPages())));
        map.put("deadline", RetrofitHelper.createPartFromString(String.valueOf(order.getDeadline())));
        if (order.getCouponCode() != null) {
            map.put("coupon_code", RetrofitHelper.createPartFromString(order.getCouponCode()));
        }
        return map;
    }

    public int getId() {
        return id;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public boolean isStepOneValid() {

        if (paperTitle.trim().isEmpty()) {
            return false;
        }

        if (mAttachmentUri == null && paperInstruction.trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
