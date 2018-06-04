package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestOrder {

    @SerializedName("service_type")
    @Expose
    private String serviceType;
    @SerializedName("paper_type")
    @Expose
    private String paperType;
    @SerializedName("paper_subject")
    @Expose
    private String paperSubject;
    @SerializedName("paper_name")
    @Expose
    private String paperName;
    @SerializedName("citation_type")
    @Expose
    private String citationType;
    @SerializedName("instructions")
    @Expose
    private String instructions;
    @SerializedName("academic_level")
    @Expose
    private String academicLevel;
    @SerializedName("spacing")
    @Expose
    private String spacing;
    @SerializedName("num_pages")
    @Expose
    private String numPages;
    @SerializedName("deadline")
    @Expose
    private String deadline;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;

    public RequestOrder() {

    }

    public RequestOrder setServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public RequestOrder setPaperType(String paperType) {
        this.paperType = paperType;
        return this;
    }

    public RequestOrder setPaperSubject(String paperSubject) {
        this.paperSubject = paperSubject;
        return this;
    }

    public RequestOrder setPaperName(String paperName) {
        this.paperName = paperName;
        return this;
    }

    public RequestOrder setCitationType(String citationType) {
        this.citationType = citationType;
        return this;
    }

    public RequestOrder setInstructions(String instructions) {
        this.instructions = instructions;
        return this;
    }

    public RequestOrder setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
        return this;
    }

    public RequestOrder setSpacing(String spacing) {
        this.spacing = spacing;
        return this;
    }

    public RequestOrder setNumPages(int numPages) {
        this.numPages = String.valueOf(numPages);
        return this;
    }

    public RequestOrder setDeadline(int deadline) {
        this.deadline = String.valueOf(deadline);
        return this;
    }

    public RequestOrder setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getPaperType() {
        return paperType;
    }

    public String getPaperSubject() {
        return paperSubject;
    }

    public String getPaperName() {
        return paperName;
    }

    public String getCitationType() {
        return citationType;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public String getSpacing() {
        return spacing;
    }

    public String getNumPages() {
        return numPages;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getCouponCode() {
        return couponCode;
    }
}
