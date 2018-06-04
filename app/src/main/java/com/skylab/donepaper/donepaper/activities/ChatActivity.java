package com.skylab.donepaper.donepaper.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.activities.abstracted.AbstractBaseAcitivity;
import com.skylab.donepaper.donepaper.adapter.ChatMessageAdapter;
import com.skylab.donepaper.donepaper.data.UserManager;
import com.skylab.donepaper.donepaper.material.PopUpDialogHelper;
import com.skylab.donepaper.donepaper.model.PopUpItem;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.RetrofitHelper;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.utils.InternetConnection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AbstractBaseAcitivity {

    private final String TAG = this.getClass().getSimpleName();
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_LIBRARY = 2;
    private static final int REQUEST_ATTACHMENT = 3;
    private static final int REQUEST_CAMERA_PERMISSION = 5;
    private static final int REQUEST_STORAGE_PERMISSION = 6;


    //Toolbar
    Toolbar toolbar;

    //Recycler View
    RecyclerView recyclerChatView;

    //Chat message adapter
    ChatMessageAdapter messageAdapter;

    //List data
    List<OrderData.PostsBean> listPostMessages;

    int order_id;

    //Button
    Button btnSend;

    //Edit text
    EditText txtMessage;

    //Downlaod manager
    DownloadManager downloadManager;

    //Toast
    Toast toast;

    //Swipe to refresh
    SwipeRefreshLayout swipeRefreshLayout;

    //Image Button
    ImageButton btnAttachFile;

    //String
    private String mCurrentPhotoPath;

    //Uri
    private Uri attachmentUri;

    //PopUp Dialog
    PopUpDialogHelper popUpHelper;

    //List popup item
    ArrayList<PopUpItem> listItemChooseFile;

    //String name
    String displayName;

    //Layout
    RelativeLayout messageUploadLayout;

    //TextView
    TextView chatMessagChooser;

    //flag check if there is attached file or not
    boolean flagAttachment = false;

    //Post message
    OrderData.PostsBean postMessage;

    //Menu itme
    MenuItem refreshItem;
    //
    @Inject
    public UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initViews() {
        ((DonePaperApplication) getApplication()).getMainComponent().inject(this);
        Intent i = getIntent();
        showProgress(true);
        if (i.hasExtra("PostMessages") && i.getSerializableExtra("PostMessages") != null && mUserManager != null) {
            getDownLoadService();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                onStop();
            }
            listPostMessages = (ArrayList<OrderData.PostsBean>) i.getSerializableExtra("PostMessages");
            initView();
            order_id = i.getIntExtra("order_id", 0);
            Log.e(TAG, listPostMessages.toString());
            initToolbar();

            //Set adapter
            messageAdapter = new ChatMessageAdapter(this, listPostMessages, mUserManager.getUser().getId(), downloadManager, mUserManager.getUser().getToken());
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setStackFromEnd(true);

            recyclerChatView.setLayoutManager(manager);
            recyclerChatView.setAdapter(messageAdapter);
            popUpHelper = new PopUpDialogHelper();
            initPopUp();
            showProgress(false);
        } else {
            showProgress(false);
            finish();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        popUpHelper.close();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");

    }
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        popUpHelper.close();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getDownLoadService() {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }


    public void initPopUp() {
        listItemChooseFile = new ArrayList<>();
        listItemChooseFile.add(new PopUpItem("Photo", true));
        listItemChooseFile.add(new PopUpItem("Camera", false));
        listItemChooseFile.add(new PopUpItem("Attach File", false));

        popUpHelper.setListener(new PopUpDialogHelper.PopUpDialogListener() {

            @Override
            public void userSelected(List<PopUpItem> list) {
                for (PopUpItem item : list) {
                    if (item.isSelected()) {
                        switch (item.getName()) {
                            case "Photo":
                                selectPhoto();
                                break;
                            case "Camera":
                                takePhoto();
                                break;
                            case "Attach File":
                                attachFile();
                                break;
                        }
                    }
                }
            }
        });

    }

    public void initToolbar() {
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu_toolbar, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshMessage();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void displayToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                flagAttachment = true;
                final File f = new File(mCurrentPhotoPath);
                attachmentUri = Uri.fromFile(f);
                messageUploadLayout.setVisibility(View.VISIBLE);
                chatMessagChooser.setText(f.getName());

            } else if (requestCode == REQUEST_LIBRARY || requestCode == REQUEST_ATTACHMENT && data.getData() != null) {
                flagAttachment = true;
                attachmentUri = data.getData();
                try (Cursor cursor = this.getContentResolver()
                        .query(attachmentUri, null, null, null, null, null)) {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (cursor != null && cursor.moveToFirst()) {

                        // Note it's called "Display Name".  This is
                        // provider-specific, and might not necessarily be the file name.
                        displayName = cursor.getString(
                                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.i("Chat activity ", "Display Name: " + displayName);
                        messageUploadLayout.setVisibility(View.VISIBLE);
                        chatMessagChooser.setText(displayName);
//                        paperAttachment.setText(displayName);
                    }
                }


            }
        }

    }

    public void sendMessageWithAttachment(){
        MultipartBody.Part filePart = RetrofitHelper.prepareFilePart(this, "files[]", attachmentUri);
        Call<DPResponse<OrderData.PostsBean>> message = DonePaperClient.getApiService()
                .sendMessageWithAttachment(mUserManager.getUser().getToken(),
                        RetrofitHelper.createPartFromString(String.valueOf(order_id)),
                        RetrofitHelper.createPartFromString(txtMessage.getText().toString()),
                        filePart);

        message.enqueue(new Callback<DPResponse<OrderData.PostsBean>>() {
            @Override
            public void onResponse(Call<DPResponse<OrderData.PostsBean>> call, Response<DPResponse<OrderData.PostsBean>> response) {
                Log.e(TAG, "chat send file step 1 ");
                showProgress(false);
                if(response.isSuccessful() && Objects.equals(response.body().getResult(), "ok")) {
                    messageAdapter.addMoreFileMessage(response.body().getData());
                    recyclerChatView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    refreshMessage();
                }

            }

                    @Override
                    public void onFailure(Call<DPResponse<OrderData.PostsBean>> call, Throwable throwable) {
                        showProgress(false);
                        throwable.printStackTrace();
                        Toast.makeText(ChatActivity.this, "Fail to send message" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayToast("Permission granted");
                    // permission was granted, yay! do the
                    // calendar task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                }
                break;
            }
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(this,
                                    "com.skylab.donepaper.donepaper.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                        }
                    }

                } else {
                    Toast.makeText(this, "Please allow Camera permission to use this feature",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: do what?
                    Log.e(TAG, "permission");
                }
                break;
            }


        }
    }

    public void sendMessage(String message) {
        Call<DPResponse<OrderData.PostsBean>> sendMessage = DonePaperClient.getApiService().sendMessage(mUserManager.getUser().getToken(), order_id, message);
        sendMessage.enqueue(new Callback<DPResponse<OrderData.PostsBean>>() {
            @Override
            public void onResponse(Call<DPResponse<OrderData.PostsBean>> call, Response<DPResponse<OrderData.PostsBean>> response) {
                if (response.body().getData() != null) {
                    showProgress(false);
                    OrderData.PostsBean messageResponse = response.body().getData();
                    messageAdapter.addMoreMessage(messageResponse);
                    recyclerChatView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<DPResponse<OrderData.PostsBean>> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });

    }

    public void refreshMessage() {


        Call<DPResponse<OrderData>> getOrderDetail = DonePaperClient.getApiService().getOrderDetailByOrderId(order_id, mUserManager.getUser().getToken());
        getOrderDetail.enqueue(new Callback<DPResponse<OrderData>>() {
            @Override
            public void onResponse(Call<DPResponse<OrderData>> call, Response<DPResponse<OrderData>> response) {
                if (response.body().getData() != null) {
                    OrderData orderDetail = response.body().getData();
                    messageAdapter.updateRefreshingData(orderDetail.getPosts());
                }

            }

            @Override
            public void onFailure(Call<DPResponse<OrderData>> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    private void attachFile() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimeTypes = {"application/pdf", "application/msword", "image/*", "text/plain",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_ATTACHMENT);
        }
    }


    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.skylab.donepaper.donepaper.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(attachmentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }


    private void selectPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        } else {
            Intent intentLibrary = new Intent(Intent.ACTION_GET_CONTENT);
            intentLibrary.addCategory(Intent.CATEGORY_OPENABLE);
            intentLibrary.setType("image/*");
            intentLibrary.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intentLibrary, "Select Picture"),
                    REQUEST_LIBRARY);
        }
    }
    ImageButton removeFile;
    public void initView() {
        postMessage = new OrderData.PostsBean();
        toolbar = (Toolbar) findViewById(R.id.chat_activity_toolbar);
        recyclerChatView = (RecyclerView) findViewById(R.id.recycler_chat);
        btnSend = (Button) findViewById(R.id.send);
        txtMessage = (EditText) findViewById(R.id.msg);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_container);
        btnAttachFile = (ImageButton) findViewById(R.id.chat_attach_file);
        chatMessagChooser = (TextView) findViewById(R.id.chat_message_file_choose);
        messageUploadLayout = (RelativeLayout) findViewById(R.id.chat_message_upload);
        removeFile = (ImageButton) findViewById(R.id.chat_message_remove_button);

        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popUpHelper != null) {
                    popUpHelper.show(ChatActivity.this, listItemChooseFile, "Choose status");
                }

            }
        });

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessage();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        removeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentUri = null;
                flagAttachment = false;
                messageUploadLayout.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                if (InternetConnection.checkConnection(ChatActivity.this) && txtMessage.getText().length() > 0) {
                    if(!flagAttachment){
                        sendMessage(txtMessage.getText().toString());
                        txtMessage.setText("");
                        recyclerChatView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    }else{
                        if(attachmentUri != null){
                            showProgress(true);
                            messageUploadLayout.setVisibility(View.GONE);
                            sendMessageWithAttachment();
                            flagAttachment = false;
                            txtMessage.setText("");
                        }

                    }

                } else {
                    displayToast("Message cannot be empty");
                }
                break;
            default:
                break;
        }
    }
}
