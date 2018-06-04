package com.skylab.donepaper.donepaper.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.adapter.RecentSearchAdapter;
import com.skylab.donepaper.donepaper.adapter.SectionAdapter;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.material.PopUpDialogHelper;
import com.skylab.donepaper.donepaper.model.PopUpItem;
import com.skylab.donepaper.donepaper.model.SectionModelData;
import com.skylab.donepaper.donepaper.model.Status;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.rest.model.SearchResults;
import com.skylab.donepaper.donepaper.rest.model.TokenData;
import com.skylab.donepaper.donepaper.utils.DateUtils;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AbstractBaseAcitivity implements SearchView.OnQueryTextListener {

    private final String TAG = this.getClass().getSimpleName();
    private TextView nameDrawerText, emailDrawerText;

    ImageView searchBack;

    //List
    ArrayList<PopUpItem> statusList;
    List<OrderData> orderList;
    ArrayList<String> listSearchRecently;
    ArrayList<SectionModelData> orderDisplayList;

    //Dialog
    Dialog statusDialog;
    PopUpDialogHelper popUpHelper;

    //Searchview
    SearchView searchView;

    //Toolbar
    Toolbar toolbar;

    //recycler view
    RecyclerView searchRecentlyListRecycleView;
    RecyclerView listOrderMainRecyclerView;
    RecyclerView searchOrderFromServerRecyclerView;

    LinearLayout linearLayout;

    //Adapter
    SectionAdapter mainFilterAdapterRecycler;
    SectionAdapter searchAdapterRecycler;
    RecentSearchAdapter recentSearchAdapter;

    //Button
    FrameLayout layoutCreateOrder;
    AppCompatButton btnCreateOrder;
    Button btnFrom, btnTo, btnStatus;

    //menu item
    MenuItem search;

    //drawer layout
    private DrawerLayout drawerLayout;

    //Set time unix timestamp
    int dateTo = DateUtils.currentTimeUnixTimestamp();
    int dateFrom = DateUtils.getDateMonthsAgoWithUnixTimestamp(6);

    //---Layout
    RelativeLayout layoutRecentSearch, layoutSearchView;

    //Runnable
    final Runnable r = new Runnable() {
        public void run() {
            loadSuggest();
        }
    };

    //Handler
    final Handler handler = new Handler();

    //String
    String stringSearch = "";

    //-----Token
    String token = "";

    //--Swipe to refresh
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    public UserManager mUserManager;

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        super.onCreate(savedInstanceState);

        loadUserFromLocal();

        if (mUserManager.getUser() == null) {
            startActivity(new Intent(this, OriginActivity.class));
            finish();
        }

        //Set name/email
        if (mUserManager.getUser() != null) {
            nameDrawerText.setText(mUserManager.getUser().getName());
            emailDrawerText.setText(mUserManager.getUser().getEmail());
        }

    }

    private void loadUserFromLocal() {
        String token = sharedPreferences.getString("token_data", null);

        // convert string to JSON Object
        Gson gson = new Gson();
        TokenData tokenData;
        // Get user with JSON Object
        if (token != null) {
            tokenData = gson.fromJson(token, TokenData.class);
            sendDeviceToken(tokenData.getToken());
            mUserManager.init(tokenData, sharedPreferences);
            //check token is valid
            Call<DPResponse<TokenData>> call = DonePaperClient.getApiService().getProfile(mUserManager.getUser().getToken());
            call.enqueue(new Callback<DPResponse<TokenData>>() {
                @Override
                public void onResponse(Call<DPResponse<TokenData>> call, Response<DPResponse<TokenData>> response) {
                    if (response.body().getResult().equals("failed")) {
                        showLogoutDialog("Invalid Token", response.body().getErrors().get(0).getMsg(), MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<TokenData>> call, Throwable t) {
                    showNotiDialog("Error", t.getMessage());
                }
            });
        }
    }

    public void initEnum() {
        ArrayList<Status> enumList =
                new ArrayList<>(Arrays.asList(Status.values()));

        for (Status s : enumList) {
            if (statusList == null) {
                statusList = new ArrayList<>();
                statusList.add(new PopUpItem("All", false));
            }

            statusList.add(new PopUpItem(s.toString(), false));
        }
        statusList.get(0).setSelected(true);
        //Init dialog in init enum
        statusDialog = new Dialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUserFromLocal();

        if (mUserManager.getUser() == null) {
            finish();

        } else {
            populateUser();
            orderList = new ArrayList<>();
            initEnum();
            initViews();
            initToolbar();

            setUpTimeDefault();
            setUpStatusDefault();
            if (InternetConnection.checkConnection(this)) {
                initDefaultDataFromServer();
            }
        }

        if (mUserManager.getListRecentSearch() != null && mUserManager.getListRecentSearch().size() > 0) {

            recentSearchAdapter.swapData(mUserManager.getListRecentSearch());
        }

    }

    public void setUpStatusDefault() {
        btnStatus.setText(R.string.main_all_text);
    }

    public void initToolbar() {
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerClosed(final View drawerView) {
                    super.onDrawerClosed(drawerView);
                    supportInvalidateOptionsMenu();
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    supportInvalidateOptionsMenu();
                }
            };
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.setDrawerIndicatorEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });
        }
    }

    public void populateUser() {
        token = mUserManager.getUser().getToken();
    }

    public void setUpTimeDefault() {

        Date dateFromCount = DateUtils.getDateMonthsAgo(6);

        Date date = new Date();

        btnFrom.setText("From: " + new SimpleDateFormat("MM/dd/yy").format(dateFromCount).toString());
        btnTo.setText("To: " + new SimpleDateFormat("MM/dd/yy").format(date).toString());
    }

    public void initDefaultDataFromServer() {
        showProgress(true);
        Call<DPResponse<SearchResults>> call = DonePaperClient.getApiService().getListOrderByTime(token, dateFrom, dateTo, "All");
        call.enqueue(new Callback<DPResponse<SearchResults>>() {
            @Override
            public void onResponse(Call<DPResponse<SearchResults>> call, Response<DPResponse<SearchResults>> response) {
                showProgress(false);
                List<SectionModelData> modelList;
                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                    List<OrderData> orderList = response.body().getData().getListOrderData();
                    modelList = convertListOrderToSectionList(orderList);
                    //----- populate Data

                    mainFilterAdapterRecycler = new SectionAdapter(modelList, getApplicationContext());
                    GridLayoutManager manager =
                            new GridLayoutManager(MainActivity.this, 1);
                    listOrderMainRecyclerView.setLayoutManager(manager);
                    mainFilterAdapterRecycler.setLayoutManager(manager);
                    mainFilterAdapterRecycler.shouldShowHeadersForEmptySections(false);
                    listOrderMainRecyclerView.setAdapter(mainFilterAdapterRecycler);
                }
            }

            @Override
            public void onFailure(Call<DPResponse<SearchResults>> call, Throwable throwable) {
                throwable.printStackTrace();
                showProgress(false);
            }
        });
    }

    public void refreshDataFromServer() throws ParseException {
        String parseFromString = btnFrom.getText().toString();
        parseFromString = parseFromString.replace("From: ", "");
        String parseToString = btnTo.getText().toString();
        parseToString = parseToString.replace("To: ", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Etc/GMT+12"));

        int dateFrom = (int) (simpleDateFormat.parse(parseFromString.trim()).getTime() / 1000L);
        int dateTo = (int) (simpleDateFormat.parse(parseToString.trim()).getTime() / 1000L);
        if (dateFrom == dateTo) {
            dateTo = dateTo + 86400;
        }

        final String status = btnStatus.getText().toString();

        Call<DPResponse<SearchResults>> call = DonePaperClient.getApiService().getListOrderByTime(token, dateFrom, dateTo, status);
        call.enqueue(new Callback<DPResponse<SearchResults>>() {
            @Override
            public void onResponse(Call<DPResponse<SearchResults>> call, Response<DPResponse<SearchResults>> response) {
                swipeRefreshLayout.setRefreshing(false);
                List<SectionModelData> modelList;
                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")
                        && response.body().getData() != null) {
                    List<OrderData> orderList = response.body().getData().getListOrderData();
                    modelList = convertListOrderToSectionList(orderList);

                    //----- populate Data
                    mainFilterAdapterRecycler.swapData(modelList);
                } else {
                    Toast.makeText(MainActivity.this, response.body().getErrors().get(0).getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DPResponse<SearchResults>> call, Throwable throwable) {
                throwable.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void filterWithTime() throws ParseException {
        String parseFromString = btnFrom.getText().toString();
        parseFromString = parseFromString.replace("From: ", "");
        String parseToString = btnTo.getText().toString();
        parseToString = parseToString.replace("To: ", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));

        int dateFrom = (int) (simpleDateFormat.parse(parseFromString.trim()).getTime() / 1000L);
        int dateTo = (int) (simpleDateFormat.parse(parseToString.trim()).getTime() / 1000L);
        if (dateFrom == dateTo) {
            dateTo = dateTo + 86400;
        }

        String status = btnStatus.getText().toString();

        showProgress(true);
        Call<DPResponse<SearchResults>> call = DonePaperClient.getApiService().getListOrderByTime(token, dateFrom, dateTo, status);
        call.enqueue(new Callback<DPResponse<SearchResults>>() {
            @Override
            public void onResponse(Call<DPResponse<SearchResults>> call, Response<DPResponse<SearchResults>> response) {
                showProgress(false);
                if (response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")
                        && response.body().getData() != null) {
                    List<SectionModelData> listFilter = convertListOrderToSectionList(response.body().getData().getListOrderData());
                    mainFilterAdapterRecycler.swapData(listFilter);
                } else {
                    Toast.makeText(MainActivity.this, response.body().getErrors().get(0).getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DPResponse<SearchResults>> call, Throwable throwable) {
                throwable.printStackTrace();
                showProgress(false);
            }
        });
    }

    public String convertDateToDesignFormat(String sectionHeader) throws ParseException {
        return new SimpleDateFormat("MMM d").format(new Date(Integer.parseInt(sectionHeader) * 1000L));
    }

    private List<SectionModelData> convertListOrderToSectionList(List<OrderData> orderList) {
        HashMap<String, List<OrderData>> map = null;

        try {
            map = populateDataInSameDate(orderList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Put date in same month
        List<SectionModelData> list = new ArrayList<>();
        for (Map.Entry<String, List<OrderData>> entry : map.entrySet()) {
            String key = entry.getKey();
            SectionModelData model = new SectionModelData();
            model.setHeaderTitle(key);
            List<OrderData> value = entry.getValue();
            model.setAllItemsInSection(value);
            list.add(model);
        }
        //Sort month
        Collections.sort(list, new Comparator<SectionModelData>() {
            DateFormat f = new SimpleDateFormat("MMM d");

            @Override
            public int compare(SectionModelData o1, SectionModelData o2) {
                try {
                    return f.parse(o2.getHeaderTitle()).compareTo(f.parse(o1.getHeaderTitle()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        return list;
    }

    private HashMap<String, List<OrderData>> populateDataInSameDate(List<OrderData> orderList) throws ParseException {
        HashMap<String, List<OrderData>> map = new HashMap<>();
        for (OrderData o : orderList) {
            if (map.containsKey(convertDateToDesignFormat(o.getDatetime()))) {
                map.get(convertDateToDesignFormat(o.getDatetime())).add(o);
            } else {
                map.put(convertDateToDesignFormat(o.getDatetime()), new ArrayList<OrderData>());
                map.get(convertDateToDesignFormat(o.getDatetime())).add(o);
            }
        }
        return map;
    }

    public void closeSearch() {
        layoutRecentSearch.setVisibility(View.GONE);
        layoutSearchView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        layoutCreateOrder.setVisibility(View.VISIBLE);
        listOrderMainRecyclerView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        search.setVisible(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void openSearch() {
        layoutRecentSearch.setVisibility(View.VISIBLE);
        layoutSearchView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
        layoutCreateOrder.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchRecentlyListRecycleView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        listOrderMainRecyclerView.setVisibility(View.GONE);
        search.setVisible(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        searchView.setIconified(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        closeSearch();
                        return false;
                    }
                });
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu_toolbar, menu);
        search = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (String s : recentSearchAdapter.getRecentSearch()) {
            Log.e("aaaa", s);
        }
        mUserManager.cacheRecentSearch(recentSearchAdapter.getRecentSearch());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

        //Init view
        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        btnFrom = (Button) findViewById(R.id.button_from_filter);
        btnTo = (Button) findViewById(R.id.button_to_filter);
        btnStatus = (Button) findViewById(R.id.button_status_filter);
        btnCreateOrder = (AppCompatButton) findViewById(R.id.main_create_order_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.root_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_side_menu);
        nameDrawerText = (TextView) findViewById(R.id.name_drawer_text);
        emailDrawerText = (TextView) findViewById(R.id.email_drawer_text);
        layoutCreateOrder = (FrameLayout) findViewById(R.id.create_order_layout);

        //side menu
        RelativeLayout btnProfile = (RelativeLayout) findViewById(R.id.field1_drawer);
        RelativeLayout btnArticleList = (RelativeLayout) findViewById(R.id.field2_drawer);
        RelativeLayout btnLibrary = (RelativeLayout) findViewById(R.id.field3_drawer);
        RelativeLayout btnOrder = (RelativeLayout) findViewById(R.id.field0_drawer);

        //init Search view
        searchBack = (ImageView) findViewById(R.id.search_back);
        searchView = (SearchView) findViewById(R.id.search_view_bar);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        searchRecentlyListRecycleView = (RecyclerView) findViewById(R.id.search_recent_recycler_view);
        listOrderMainRecyclerView = (RecyclerView) findViewById(R.id.order_filter_list);


        //-----------Search Recently
        layoutSearchView = (RelativeLayout) findViewById(R.id.search_view_layout);
        layoutRecentSearch = (RelativeLayout) findViewById(R.id.recent_search_layout);
        listSearchRecently = new ArrayList<>();

        recentSearchAdapter = new RecentSearchAdapter(this, listSearchRecently);
        LinearLayoutManager managerLinear = new LinearLayoutManager(this);
        searchRecentlyListRecycleView.setLayoutManager(managerLinear);
        searchRecentlyListRecycleView.setAdapter(recentSearchAdapter);
        recentSearchAdapter.setSearchView(searchView);

        //-------- SearchView From server


        searchOrderFromServerRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view_result);
        orderDisplayList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchAdapterRecycler = new SectionAdapter(orderDisplayList, this);
        searchOrderFromServerRecyclerView.setLayoutManager(mLayoutManager);
        searchOrderFromServerRecyclerView.setAdapter(searchAdapterRecycler);

        linearLayout = (LinearLayout) findViewById(R.id.status_filter_layout);
        popUpHelper = new PopUpDialogHelper();

        //Swipe to Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_filter_swipe_container);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetConnection.checkConnection(MainActivity.this)) {
                    try {
                        refreshDataFromServer();
                    } catch (ParseException e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Set button listener
        btnFrom.setOnClickListener(this);
        btnTo.setOnClickListener(this);
        btnStatus.setOnClickListener(this);
        btnCreateOrder.setOnClickListener(this);
        searchBack.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnArticleList.setOnClickListener(this);
        btnLibrary.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_from_filter:
                // do something
                if (!InternetConnection.checkConnection(this)) {
                    Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                DateUtils.setDate(btnFrom, this);
                DateUtils.setListener(new DateUtils.DateUtilsListener() {
                    @Override
                    public void dateSelected(String dateSelected) {
                        try {
                            filterWithTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                break;
            case R.id.button_to_filter:
                if (!InternetConnection.checkConnection(this)) {
                    Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                // do something else
                DateUtils.setDate(btnTo, this);
                DateUtils.setListener(new DateUtils.DateUtilsListener() {
                    @Override
                    public void dateSelected(String dateSelected) {
                        try {
                            filterWithTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                break;
            case R.id.button_status_filter:
                if (!InternetConnection.checkConnection(this)) {
                    Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                // i'm lazy, do nothing
                popUpHelper.show(this, statusList, "Status");
                popUpHelper.setListener(new PopUpDialogHelper.PopUpDialogListener() {

                    @Override
                    public void userSelected(List<PopUpItem> list) {
                        for (PopUpItem item : list) {
                            if (item.isSelected()) {
                                btnStatus.setText(item.getName());
                            }
                        }
                        try {
                            filterWithTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });


                break;
            case R.id.main_create_order_button:
                Intent createOrder = new Intent(MainActivity.this, OrderProcessActivity.class);
                startActivity(createOrder);
                break;
            case R.id.search_back:
                closeSearch();
                break;
            case R.id.field1_drawer:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.field2_drawer:
                startActivity(new Intent(MainActivity.this, ArticlesActivity.class));
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.field3_drawer:
                startActivity(new Intent(MainActivity.this, ListSubLibActivity.class));
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.field0_drawer:
                startActivity(new Intent(MainActivity.this, OrderProcessActivity.class));
                drawerLayout.closeDrawer(Gravity.START);
                break;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        recentSearchAdapter.addRecentlySearch(query);

        return false;
    }

    public void gettingSearchResultFromServer(String query) {
        if (InternetConnection.checkConnection(this)) {
            Call<DPResponse<SearchResults>> callSearchOrder = DonePaperClient.getApiService().
                    getListOrderByKeyword(token, query, "All");

            callSearchOrder.enqueue(new Callback<DPResponse<SearchResults>>() {
                @Override
                public void onResponse(Call<DPResponse<SearchResults>> call, Response<DPResponse<SearchResults>> response) {
                    if (response.body().getData() != null) {
                        List<OrderData> listReponse = response.body().getData().getListOrderData();
                        List<SectionModelData> listModel = convertListOrderToSectionList(listReponse);

                        searchAdapterRecycler.swapData(listModel);
                    }
                }

                @Override
                public void onFailure(Call<DPResponse<SearchResults>> call, Throwable throwable) {
                    throwable.printStackTrace();

                }
            });

        }
    }

    //Avoiding user search input continiously
    @Override
    public boolean onQueryTextChange(final String newText) {
        if (newText.trim().length() == 0) {
            layoutRecentSearch.setVisibility(View.VISIBLE);
            searchOrderFromServerRecyclerView.setVisibility(View.GONE);
            btnCreateOrder.setVisibility(View.GONE);

        } else {
            layoutRecentSearch.setVisibility(View.GONE);
            searchOrderFromServerRecyclerView.setVisibility(View.VISIBLE);
            btnCreateOrder.setVisibility(View.GONE);
            stringSearch = newText;
            handler.removeCallbacks(r);
            handler.postDelayed(r, 500);

        }
        return true;
    }

    public void loadSuggest() {
        if (InternetConnection.checkConnection(this)) {
            gettingSearchResultFromServer(stringSearch);
        } else {
            handler.removeCallbacks(r);
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
        }
    }
}
