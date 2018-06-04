package com.skylab.donepaper.donepaper.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public interface DateUtilsListener{
        void dateSelected(String dateSelected);

    }

    private static Calendar cal = Calendar.getInstance();
    private static int yearTemp = cal.get(Calendar.YEAR);
    private static int monthTemp = cal.get(Calendar.MONTH);
    private static int dayTemp = cal.get(Calendar.DAY_OF_MONTH);
    private static DateUtilsListener listener;

    public static void setListener(DateUtilsListener listener){ DateUtils.listener = listener;}


    public static void setDate(final Button button, Context context){

        DatePickerDialog datePickerDialog;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(context, dateListener(button), year, month, day);
        datePickerDialog.show();
    }

    private static DatePickerDialog.OnDateSetListener dateListener(final Button button){

        DatePickerDialog.OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                String preffix;
                Log.e("dateutils",button.getText().toString());
                if(button.getText().toString().contains("From")){
                    preffix = "From: ";
                    if(yearTemp < year){
                        return;
                    }else if(yearTemp == year){
                        if(monthOfYear > monthTemp){
                            return;
                        }else if ( monthOfYear == monthTemp){
                            if(dayOfMonth > dayTemp){
                                return;
                            }
                        }

                    }


                }else{
                    yearTemp = year;
                    monthTemp = monthOfYear;
                    dayTemp = dayOfMonth;

                    preffix = "To: ";
                }
                String month = monthOfYear < 10? "0"+String.valueOf(monthOfYear + 1):String.valueOf(monthOfYear + 1);
                button.setText(preffix + month + "/" + dayOfMonth + "/"
                        + year);
                if (listener != null) {
                    listener.dateSelected(month + "/" + dayOfMonth + "/" + year);
                }
            }
        };
        return odsl;
    }

    public static Date getDateMonthsAgo(int numOfMonthsAgo)
    {
        Calendar c = Calendar.getInstance();
        Date time=new Date();
        c.setTime(time);
        c.add(Calendar.MONTH, -1 * numOfMonthsAgo);
        return c.getTime();
    }

    public static int getDateMonthsAgoWithUnixTimestamp(int numOfMonthsAgo){

        Calendar c = Calendar.getInstance();
        Date time=new Date((long)currentTimeUnixTimestamp()*1000);
        c.setTime(time);
        c.add(Calendar.MONTH, -1 * numOfMonthsAgo);
        Date monthAbstract = c.getTime();
        long unixTime = (long) monthAbstract.getTime()/1000;
        return (int) unixTime;
    }
    public static int currentTimeUnixTimestamp(){
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public static String convertDateToNormalFormat(String timestemp) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(Integer.parseInt(timestemp) * 1000L));
    }

    public static String convertDateForMessage(String timestemp) throws ParseException {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date(Integer.parseInt(timestemp) * 1000L));
    }

    public static String convertDateToMonthFormat(String timestemp) throws ParseException {
        return new SimpleDateFormat("MMM d").format(new Date(Integer.parseInt(timestemp) * 1000L));
    }
}
