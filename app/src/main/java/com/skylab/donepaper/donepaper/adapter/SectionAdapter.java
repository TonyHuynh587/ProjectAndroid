package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import com.skylab.donepaper.donepaper.R;

import com.skylab.donepaper.donepaper.activities.OrderDetailActivity;
import com.skylab.donepaper.donepaper.model.SectionModelData;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.utils.ColorUtils;

import java.util.List;

public class SectionAdapter extends SectionedRecyclerViewAdapter<SectionedViewHolder> {
    private Context context;
    private List<SectionModelData> allData;


    public SectionAdapter(List<SectionModelData> allData, Context context) {
        this.allData = allData;
        this.context = context;

    }

    @Override
    public int getSectionCount() {
        return allData.size();
    }

    @Override
    public int getItemCount(int i) {
        return allData.get(i).getAllItemsInSection().size();
    }

    @Override
    public void onBindHeaderViewHolder(SectionedViewHolder sectionedViewHolder, int i, boolean b) {
        String sectionName = allData.get(i).getHeaderTitle();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) sectionedViewHolder;
        sectionViewHolder.sectionTitle.setText(sectionName);
    }

    public void swapData(List<SectionModelData> data) {
        for(SectionModelData a : data){
            Log.e("Adapter",a.getHeaderTitle());
        }
        allData.clear();
        allData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(SectionedViewHolder sectionedViewHolder, int i, int i1, int i2) {
        List<OrderData> itemInSection = allData.get(i).getAllItemsInSection();
        OrderData itemName = itemInSection.get(i1);
        int color = ColorUtils.defineColor(itemName.getStatus());
        ItemViewHolder itemViewHolder = (ItemViewHolder) sectionedViewHolder;

        String id = String.valueOf(itemName.getId());
        String money = "$" + String.valueOf(itemName.getPrice());
        String deadline = "Deadline: " + itemName.getDeadline() + " hours";
        itemViewHolder.orderId.setText(id);
        itemViewHolder.orderId.setTypeface(null, Typeface.BOLD);
        itemViewHolder.orderDeadline.setText(deadline);
        itemViewHolder.orderPrice.setText(money);
        itemViewHolder.orderPrice.setTypeface(null, Typeface.BOLD);
        itemViewHolder.orderInstruction.setText(itemName.getName());
        itemViewHolder.orderStatus.setText(itemName.getStatus());
        itemViewHolder.orderStatus.setTextColor(ContextCompat.getColor(context, color));
        itemViewHolder.orderStatusItem.setBackgroundColor(ContextCompat.getColor(context, color));

    }

    @Override
    public SectionedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:

                layout = R.layout.row_header_order_list;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                return new SectionViewHolder(v);

            case VIEW_TYPE_ITEM:
                layout = R.layout.row_item_order_list;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                return new ItemViewHolder(v);

            default:
                return null;

        }


    }


    private class SectionViewHolder extends SectionedViewHolder {


        final TextView sectionTitle;

        private SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.header_section);
        }
    }

    // ItemViewHolder Class for Items in each Section
    private class ItemViewHolder extends SectionedViewHolder {

        final TextView orderId, orderPrice, orderStatus, orderInstruction, orderDeadline;
        final View orderStatusItem;

        private ItemViewHolder(View itemView) {
            super(itemView);
            orderId = (TextView) itemView.findViewById(R.id.order_id);
            orderPrice = (TextView) itemView.findViewById(R.id.order_price);
            orderStatus = (TextView) itemView.findViewById(R.id.order_status);
            orderInstruction = (TextView) itemView.findViewById(R.id.order_instruction);
            orderDeadline = (TextView) itemView.findViewById(R.id.order_deadline);
            orderStatusItem = itemView.findViewById(R.id.order_status_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailIntent = new Intent(context, OrderDetailActivity.class);

                    orderDetailIntent.putExtra("OrderId", Integer.parseInt(orderId.getText().toString().replace("ID • ", "")));
                    orderDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(orderDetailIntent);

                }
            });

        }

    }
}

