package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DPResponse<T> {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("data")
    @Expose
    private T data;
    @SerializedName("errors")
    @Expose
    private List<Error> errors = null;

    public String getResult() {
        return result;
    }

    public T getData() {
        return data;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public class Error{
        private String code;
        private String msg;

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
