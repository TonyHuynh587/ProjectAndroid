package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lastchick on 5/10/17.
 */

public class PaypalInfo {

    @SerializedName("endpoint")
    String endpoint;

    List<Input> inputs;

    public String getEndpoint() {
        return endpoint;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public class Input {

        private String type;
        private String name;
        private String value;

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
