package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.adapter.ArticlesListAdapter;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.ArticleItem;
import com.skylab.donepaper.donepaper.rest.model.ArticlesResponse;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesActivity extends AbstractBaseAcitivity {

    private ListView articleList;
    private ProgressBar loadmoreProgressBar, listArticleProgressBar;
    private ArticlesListAdapter mArticlesListAdapter;
    private static final int LIMIT_PAGE = 25;
    private static final int EXCERPT_CHARACTERS = 90;
    private int page = 1;
    private int totalPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listArticleProgressBar.setVisibility(View.VISIBLE);

        articleList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0) {
                    if (page < totalPage) {
                        loadmoreProgressBar.setVisibility(View.VISIBLE);
                        page++;
                        contactServer(page);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_articles;
    }

    @Override
    protected void initViews() {

        //init toolbar
        Toolbar articleToolbar = (Toolbar) findViewById(R.id.article_toolbar);
        this.setSupportActionBar(articleToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        articleToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadmoreProgressBar = (ProgressBar)findViewById(R.id.load_more_progress);
        listArticleProgressBar = (ProgressBar)findViewById(R.id.list_article_progress);
        List<ArticleItem> arrayListArticle = new ArrayList<>();
        articleList = (ListView)findViewById(R.id.article_list);
        mArticlesListAdapter = new ArticlesListAdapter(arrayListArticle, this);
        articleList.setAdapter(mArticlesListAdapter);
        contactServer(page);

        //item click
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mArticlesListAdapter.getArrayList().get(position).getUrl();
                Intent intent = new Intent(ArticlesActivity.this, WebViewArticleActivity.class);
                intent.putExtra("Url", url);
                startActivity(intent);
            }
        });

    }

    private void contactServer(int page) {
        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<ArticlesResponse>> call = DonePaperClient.getApiService().getArticlesList(LIMIT_PAGE, page, EXCERPT_CHARACTERS);
            call.enqueue(new Callback<DPResponse<ArticlesResponse>>() {
                @Override
                public void onResponse(Call<DPResponse<ArticlesResponse>> call, Response<DPResponse<ArticlesResponse>> response) {
                    if (response.isSuccessful()){
                        listArticleProgressBar.setVisibility(View.GONE);
                        mArticlesListAdapter.addArticleItem(response.body().getData().getArticlesArrayList());
                        totalPage = response.body().getData().getTotalPages();
                        loadmoreProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<ArticlesResponse>> call, Throwable throwable) {
                    listArticleProgressBar.setVisibility(View.GONE);
                    loadmoreProgressBar.setVisibility(View.GONE);
                    showNotiDialog("Error", throwable.getMessage());
                }
            });
        } else {
            showNotiDialog("Error", "No internet connection");
        }
    }

    @Override
    public void onClick(View v) {

    }
}
