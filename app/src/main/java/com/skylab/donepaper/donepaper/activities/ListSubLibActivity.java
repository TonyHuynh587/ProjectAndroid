package com.skylab.donepaper.donepaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.adapter.SubLibListAdapter;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.LibrarySubjectItem;
import com.skylab.donepaper.donepaper.rest.model.SubjectsLibraryResponse;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListSubLibActivity extends AbstractBaseAcitivity {

    private ProgressBar loadingProgress;
    private SubLibListAdapter subLibListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_list_sub_lib;
    }

    @Override
    protected void initViews() {

        //init toolbar
        Toolbar articleToolbar = (Toolbar) findViewById(R.id.library_toolbar);
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

        ListView listSubLib = (ListView) findViewById(R.id.sub_lib_list);
        ProgressBar loadmoreProgress = (ProgressBar) findViewById(R.id.load_more_sub_lib_progress);
        loadingProgress = (ProgressBar) findViewById(R.id.list_sub_lib_progress);
        List<LibrarySubjectItem> arraylistSubLib = new ArrayList<>();
        subLibListAdapter = new SubLibListAdapter(arraylistSubLib, this);
        listSubLib.setAdapter(subLibListAdapter);
        contactSerVer();

        //on click item subject
        listSubLib.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListSubLibActivity.this, DetailLibraryActivity.class);
                intent.putExtra("subName", subLibListAdapter.getArrayList().get(position).getSubject());
                intent.putExtra("subId", subLibListAdapter.getArrayList().get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void contactSerVer() {
        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<SubjectsLibraryResponse>> call = DonePaperClient.getApiService().getSubjectListLibrary();
            call.enqueue(new Callback<DPResponse<SubjectsLibraryResponse>>() {
                @Override
                public void onResponse(Call<DPResponse<SubjectsLibraryResponse>> call, Response<DPResponse<SubjectsLibraryResponse>> response) {
                    if (response.isSuccessful()) {
                        loadingProgress.setVisibility(View.GONE);
                        subLibListAdapter.addSubjectItem(response.body().getData().getListSubjecLibrary());
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<SubjectsLibraryResponse>> call, Throwable throwable) {
                    loadingProgress.setVisibility(View.GONE);
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
