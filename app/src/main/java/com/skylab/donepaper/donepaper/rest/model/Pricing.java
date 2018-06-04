
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Pricing {

    @SerializedName("highschool")
    @Expose
    private PriceItems highschool;
    @SerializedName("college")
    @Expose
    private PriceItems college;
    @SerializedName("university")
    @Expose
    private PriceItems university;

    public ArrayList<Double> getHighschoolPriceList() {
        return highschool.toArrayList();
    }

    public ArrayList<Double> getCollegePriceList() {
        return college.toArrayList();
    }

    public ArrayList<Double> getUniversityPriceList() {
        return university.toArrayList();
    }

    public class PriceItems {

        @SerializedName("12")
        @Expose
        private double halfDayPrice;
        @SerializedName("24")
        @Expose
        private double oneDayPrice;
        @SerializedName("36")
        @Expose
        private double oneAndAHalfDayPrice;
        @SerializedName("48")
        @Expose
        private double twoDaysPrice;
        @SerializedName("72")
        @Expose
        private double threeDaysPrice;
        @SerializedName("120")
        @Expose
        private double fiveDaysPrice;

        public double getHalfDayPrice() {
            return halfDayPrice;
        }

        public double getOneDayPrice() {
            return oneDayPrice;
        }

        public double getOneAndAHalfDayPrice() {
            return oneAndAHalfDayPrice;
        }

        public double getTwoDaysPrice() {
            return twoDaysPrice;
        }

        public double getThreeDaysPrice() {
            return threeDaysPrice;
        }

        public double getFiveDaysPrice() {
            return fiveDaysPrice;
        }

        public ArrayList<Double> toArrayList(){
            ArrayList<Double> list = new ArrayList<>();
            list.add(this.halfDayPrice);
            list.add(this.oneDayPrice);
            list.add(this.oneAndAHalfDayPrice);
            list.add(this.twoDaysPrice);
            list.add(this.threeDaysPrice);
            list.add(this.fiveDaysPrice);
            return list;
        }
    }
}
