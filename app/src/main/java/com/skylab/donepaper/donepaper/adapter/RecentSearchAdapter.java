package com.skylab.donepaper.donepaper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.rest.model.OrderData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.SearchViewHolder>{
    private final int VIEW_TYPE_RECENT = 0;
    private Context context;
    private ArrayList<String> recentSearch;
    private ArrayList<String> searchSuggest = new ArrayList<>();
    private SearchView searchView;
    private ArrayList<String> recentSearchCopy;
    private HashMap<String,OrderData> searchValue;

    public RecentSearchAdapter(Context context, ArrayList<String> recentSearch) {
        this.context = context;
        this.recentSearch = recentSearch;
        searchValue = new HashMap<>();
        recentSearchCopy = new ArrayList<>();
        recentSearchCopy.addAll(recentSearch);
    }


    public RecentSearchAdapter(Context context, ArrayList<String> recentSearch, ArrayList<String> searchSuggest) {
        this.context = context;
        this.recentSearch = recentSearch;
        this.searchSuggest.addAll(searchSuggest);
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View searchView;
        int layout;
        if(viewType == VIEW_TYPE_RECENT){
            layout = R.layout.row_search_recent_item;
            searchView = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new SearchViewHolder(searchView);
        }
        return null;
    }


    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case VIEW_TYPE_RECENT:
                holder.searchText.setText(recentSearch.get(position));
                holder.searchText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // search
                        String temp = holder.searchText.getText().toString();
                        searchView.setQuery(temp,false);


                    }
                });
                holder.textOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp = holder.searchText.getText().toString();
                        searchView.setQuery(temp,false);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recentSearch.size();
    }

    public ArrayList<String> getRecentSearch() {
        return recentSearch;
    }

    public void swapData(List<String> listFromCache){
        if(listFromCache != null){
            this.recentSearch.clear();
            this.recentSearch.addAll(listFromCache);
            notifyDataSetChanged();
        }
    }

    public void addRecentlySearch(String query){
        recentSearch.add(0,query);
        notifyDataSetChanged();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{

        private TextView searchText;
        private ImageView textOn;

        private SearchViewHolder(View itemView) {
            super(itemView);
            searchText = (TextView) itemView.findViewById(R.id.search_text);
            textOn = (ImageView) itemView.findViewById(R.id.search_text_on);
        }
    }

}
