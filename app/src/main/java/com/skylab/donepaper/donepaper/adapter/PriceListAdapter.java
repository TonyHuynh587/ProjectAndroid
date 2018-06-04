package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;

import java.util.ArrayList;

public class PriceListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Double> mPriceList;
    private ArrayList<String> mDeadlineList;
    private int mSelectedPrice;

    public PriceListAdapter(Context mContext, ArrayList<String> deadlineList, ArrayList<Double> priceList) {
        this.mContext = mContext;
        this.mDeadlineList = deadlineList;
        this.mPriceList = priceList;
    }

    @Override
    public int getCount() {
        return mPriceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPriceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_price_per_deadline_list, parent, false);
            holder = new ViewHolder();
            holder.hourText = (TextView)convertView.findViewById(R.id.row_hour_text);
            holder.pricePerDeadlineText = (TextView)convertView.findViewById(R.id.row_price_per_page_text);
            holder.tickImage = (ImageView)convertView.findViewById(R.id.row_tick_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.hourText.setText(mDeadlineList.get(position));
        String pricePerDeadline = "$" + String.valueOf(mPriceList.get(position)) + "/page";
        holder.pricePerDeadlineText.setText(pricePerDeadline);

        if(position == mSelectedPrice){
            holder.tickImage.setVisibility(View.VISIBLE);
        }else{
            holder.tickImage.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setPriceList(ArrayList<Double> priceList){
        mPriceList = priceList;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPrice = selectedPosition;
    }

    private static class ViewHolder{
        TextView hourText, pricePerDeadlineText;
        ImageView tickImage;
    }

}
