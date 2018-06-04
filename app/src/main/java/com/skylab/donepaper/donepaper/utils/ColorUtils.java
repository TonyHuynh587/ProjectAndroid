package com.skylab.donepaper.donepaper.utils;

import android.util.Log;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.model.Status;

public class ColorUtils {

    public static int defineColor(String status) {

        Log.e("abc", status);

        if (status.equals(Status.ORDER_DELIVERED.toString()) || status.equals(Status.ORDER_COMPLETE.toString())) {
            return R.color.statusDelivered;
        } else if (status.equals(Status.ORDER_UNPAID.toString())) {
            return R.color.statusUnpaid;
        } else if (status.equals(Status.ORDER_IN_PROGRESS.toString()) || status.equals(Status.ORDER_IN_REVISION.toString())) {
            return R.color.statusBlue;
        } else {
            return R.color.statusDefault;
        }

    }
}
