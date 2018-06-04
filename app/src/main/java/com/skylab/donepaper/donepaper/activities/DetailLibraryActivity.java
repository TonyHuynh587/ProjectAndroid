package com.skylab.donepaper.donepaper.activities;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.adapter.DetailLibraryAdapter;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.FileDetailItemLibrary;
import com.skylab.donepaper.donepaper.rest.model.FileLibraryResponse;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailLibraryActivity extends AbstractBaseAcitivity {

    private ListView listDetailFile;
    private ProgressBar loadingProgress, loadmoreProgress;
    private DetailLibraryAdapter detailLibraryAdapter;

    private static final int LIMIT_PAGE = 25;
    private int page = 1;
    private int totalPages;
    private int subId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingProgress.setVisibility(View.VISIBLE);
        listDetailFile.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0) {
                    if (page < totalPages) {
                        loadmoreProgress.setVisibility(View.VISIBLE);
                        page++;
                        contactServer(page);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail_library;
    }

    @Override
    protected void initViews() {
        //init toolbar
        Toolbar libDetailToolbar = (Toolbar) findViewById(R.id.detail_library_toolbar);
        this.setSupportActionBar(libDetailToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        libDetailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView titleToolbar = (TextView) libDetailToolbar.findViewById(R.id.detail_library_title);
        listDetailFile = (ListView)findViewById(R.id.detail_lib_list);
        List<FileDetailItemLibrary> arraylistFile = new ArrayList<>();
        loadingProgress = (ProgressBar)findViewById(R.id.detail_library_progress);
        loadmoreProgress = (ProgressBar)findViewById(R.id.load_more_detail_lib_progress);

        detailLibraryAdapter = new DetailLibraryAdapter(arraylistFile, this);
        listDetailFile.setAdapter(detailLibraryAdapter);

        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            titleToolbar.setText(bd.getString("subName"));
            subId = bd.getInt("subId");
            contactServer(page);
        }

        listDetailFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse(detailLibraryAdapter.getArrayList().get(position).getUrl());
                String fileName = detailLibraryAdapter.getArrayList().get(position).getFileName();
                Toast.makeText(DetailLibraryActivity.this, "File download started", Toast.LENGTH_SHORT).show();
                DownloadData(uri, fileName);
            }
        });
    }

    private void contactServer(int page) {
        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<FileLibraryResponse>> call = DonePaperClient.getApiService().getFileLibrary(LIMIT_PAGE, page, subId);
            call.enqueue(new Callback<DPResponse<FileLibraryResponse>>() {
                @Override
                public void onResponse(Call<DPResponse<FileLibraryResponse>> call, Response<DPResponse<FileLibraryResponse>> response) {
                    if (response.isSuccessful()) {
                        loadingProgress.setVisibility(View.GONE);
                        loadmoreProgress.setVisibility(View.GONE);
                        detailLibraryAdapter.addFileItem(response.body().getData().getListFile());
                        totalPages = response.body().getData().getTotalPages();
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<FileLibraryResponse>> call, Throwable throwable) {
                    loadingProgress.setVisibility(View.GONE);
                    loadmoreProgress.setVisibility(View.GONE);
                    showNotiDialog("Error", throwable.getMessage());
                }
            });
        } else {

            showNotiDialog("Error", "No internet connection");
        }
    }

    private void DownloadData (Uri uri, String filename) {

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(filename);

        //Setting description of request
        request.setDescription("Download from donepaper");
        downloadManager.enqueue(request);

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

    }

    @Override
    public void onClick(View v) {

    }
}
