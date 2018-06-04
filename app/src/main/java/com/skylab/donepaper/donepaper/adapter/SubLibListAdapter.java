package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.rest.model.LibrarySubjectItem;

import java.util.List;

public class SubLibListAdapter extends BaseAdapter {

    private List<LibrarySubjectItem> arrayList;
    private LayoutInflater mInflater;

    public SubLibListAdapter(List<LibrarySubjectItem> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_subject_library, parent, false);
            holder = new ViewHolder();
            holder.titleText = (TextView)convertView.findViewById(R.id.subject_library_text);
            holder.subtitleText = (TextView)convertView.findViewById(R.id.count_library_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.titleText.setText(arrayList.get(position).getSubject());
        holder.subtitleText.setText(arrayList.get(position).getFileCount());

        return convertView;
    }

    private class ViewHolder {
        TextView titleText, subtitleText;
    }

    public void addSubjectItem(List<LibrarySubjectItem> arraySubLib) {
        arrayList.addAll(arraySubLib);
        notifyDataSetChanged();
    }

    public List<LibrarySubjectItem> getArrayList() {
        return arrayList;
    }
}
