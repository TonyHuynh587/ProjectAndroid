package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.rest.model.ArticleItem;

import java.text.ParseException;
import java.util.List;

public class ArticlesListAdapter extends BaseAdapter {

    private List<ArticleItem> arrayList;
    private LayoutInflater mInflater;

    public ArticlesListAdapter(List<ArticleItem> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        } else {
            return 0;
        }
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
            convertView = mInflater.inflate(R.layout.row_article, parent, false);
            holder = new ViewHolder();
            holder.titleText = (TextView)convertView.findViewById(R.id.title_article_text);
            holder.subtitleText = (TextView)convertView.findViewById(R.id.subtitle_article_text);
            holder.dateArticleText = (TextView)convertView.findViewById(R.id.date_article_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.titleText.setText(arrayList.get(position).getTitle());
        holder.subtitleText.setText(arrayList.get(position).getExcerpt());
        try {
            holder.dateArticleText.setText(arrayList.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView titleText, subtitleText, dateArticleText;
    }

    public void addArticleItem(List<ArticleItem> arrayArticle) {
        arrayList.addAll(arrayArticle);
        notifyDataSetChanged();
    }

    public List<ArticleItem> getArrayList() {
        return arrayList;
    }
}
