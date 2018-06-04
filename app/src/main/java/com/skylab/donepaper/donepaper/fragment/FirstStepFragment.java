package com.skylab.donepaper.donepaper.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.data.OrderManager;
import com.skylab.donepaper.donepaper.fragment.abstracted.AbstractBaseFragment;
import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;
import com.skylab.donepaper.donepaper.material.PaperDetailsDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;


public class FirstStepFragment extends AbstractBaseFragment {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_LIBRARY = 2;
    private static final int REQUEST_ATTACHMENT = 3;
    private static final int REQUEST_CAMERA_PERMISSION = 5;
    private static final int REQUEST_STORAGE_PERMISSION = 6;

    private String serviceType = "writing";

    @Inject
    public OrderManager mOrderManager;

    public static FirstStepFragment newInstance() {
        return new FirstStepFragment();
    }

    private AppCompatButton paperTypeButton, paperSubjectButton, paperCitationButton, paperAttachment;
    private ImageButton attachmentCloseButton;
    private int mSelectedType = 0;
    private int mSelectedSubject;
    private int mSelectedCitation;

    private String mCurrentPhotoPath;

    private PaperDetailsDialog dialog;

    private View mRootView;
    private EditText paperTitle;
    private EditText paperInstructions;

    @Inject
    public NavigationManager mNavigationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getActivity().getApplication()).getMainComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_first_step, container, false);
        }

        RadioButton writingServiceType = (RadioButton) mRootView.findViewById(R.id.writing_type);
        RadioButton revisingServiceType = (RadioButton) mRootView.findViewById(R.id.revising_type);

        paperTypeButton = (AppCompatButton) mRootView.findViewById(R.id.paper_type);

        paperSubjectButton = (AppCompatButton) mRootView.findViewById(R.id.paper_subject);

        paperTitle = (EditText) mRootView.findViewById(R.id.paper_title);

        paperCitationButton = (AppCompatButton) mRootView.findViewById(R.id.paper_citation);

        paperInstructions = (EditText) mRootView.findViewById(R.id.paper_instructions);

        paperAttachment = (AppCompatButton) mRootView.findViewById(R.id.paper_upload);

        attachmentCloseButton = (ImageButton) mRootView.findViewById(R.id.attachment_close_button);

        AppCompatButton continueButton = (AppCompatButton) mRootView.findViewById(R.id.continue_button);

        paperTypeButton.setText(mNavigationManager.getDefaultPaperType());
        paperSubjectButton.setText(mNavigationManager.getDefaultPaperSubject());
        paperCitationButton.setText(mNavigationManager.getDefaultCitationStyle());

        writingServiceType.setOnClickListener(this);
        revisingServiceType.setOnClickListener(this);
        paperTypeButton.setOnClickListener(this);
        paperSubjectButton.setOnClickListener(this);
        paperCitationButton.setOnClickListener(this);
        paperAttachment.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        paperTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPaperTitle();
                }
            }
        });

        paperInstructions.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPaperInstruction();
                }
            }
        });

        attachmentCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOrderManager.setAttachmentUri(null);
                paperAttachment.setText(R.string.attach_your_file);
                attachmentCloseButton.setVisibility(View.GONE);
            }
        });

        dialog = new PaperDetailsDialog();

        return mRootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.writing_type:
                serviceType = "writing";
                break;
            case R.id.revising_type:
                serviceType = "revising";
                break;
            case R.id.paper_type:

                Bundle bundle = new Bundle();
                bundle.putInt("send_selected_item", mSelectedType);
                bundle.putInt("dialog_type", R.id.paper_type);

                dialog.setArguments(bundle);
                dialog.setOnItemSelectedListener(new PaperDetailsDialog.DialogListener() {
                    @Override
                    public void onItemSelected(int which) {
                        String text = mNavigationManager.getPaperTypes().get(which);
                        paperTypeButton.setText(text);
                        mSelectedType = which;
                    }
                });
                dialog.show(getFragmentManager(), "paperType");
                break;
            case R.id.paper_subject:

                bundle = new Bundle();
                bundle.putInt("send_selected_item", mSelectedSubject);
                bundle.putInt("dialog_type", R.id.paper_subject);

                dialog.setArguments(bundle);
                dialog.setOnItemSelectedListener(new PaperDetailsDialog.DialogListener() {
                    @Override
                    public void onItemSelected(int which) {
                        String text = mNavigationManager.getPaperSubjects().get(which);
                        paperSubjectButton.setText(text);
                        mSelectedSubject = which;
                    }
                });
                dialog.show(getFragmentManager(), "paperSubject");
                break;
            case R.id.paper_citation:

                bundle = new Bundle();
                bundle.putInt("send_selected_item", mSelectedCitation);
                bundle.putInt("dialog_type", R.id.paper_citation);

                dialog.setArguments(bundle);
                dialog.setOnItemSelectedListener(new PaperDetailsDialog.DialogListener() {
                    @Override
                    public void onItemSelected(int which) {
                        String text = mNavigationManager.getCitationStyles().get(which);
                        paperCitationButton.setText(text);
                        mSelectedCitation = which;
                    }
                });
                dialog.show(getFragmentManager(), "paperSCitation");
                break;
            case R.id.paper_upload:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);

                } else {

                    bundle = new Bundle();
                    bundle.putInt("send_selected_item", -1);
                    bundle.putInt("dialog_type", R.id.paper_upload);

                    dialog.setArguments(bundle);
                    dialog.setOnItemSelectedListener(new PaperDetailsDialog.DialogListener() {
                        @Override
                        public void onItemSelected(int which) {

                            switch (which) {
                                case 0:
                                    selectPhoto();
                                    break;
                                case 1:
                                    takePhoto();
                                    break;
                                case 2:
                                    attachFile();
                                    break;
                            }
                        }
                    });
                    dialog.show(getFragmentManager(), "paperAttachment");
                }
                break;
            case R.id.continue_button:
                checkPaperTitle();
                checkPaperInstruction();
                if (mOrderManager.isStepOneValid()) {
                    mNavigationManager.nextFragment();
                }

                break;
        }
    }

    @Override
    public void onPause() {
        String paperType = mNavigationManager.getPaperTypes().get(mSelectedType);
        String paperSubject = mNavigationManager.getPaperSubjects().get(mSelectedSubject);
        String title = paperTitle.getText().toString();
        String citationStyle = mNavigationManager.getCitationStyles().get(mSelectedCitation);
        String instructions = paperInstructions.getText().toString();

        mOrderManager.updateFromStepOne(serviceType, paperType, paperSubject,
                title, citationStyle, instructions, mOrderManager.getAttachmentUri());

        super.onPause();
    }

    private void attachFile() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.skylab.donepaper.donepaper.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            }

        }
    }

    private void selectPhoto() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.skylab.donepaper.donepaper.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    }
                }

            } else {
                Toast.makeText(getActivity(), "Please allow Camera permission to use this feature",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getActivity(), "Storage access allowed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(mCurrentPhotoPath);
                paperAttachment.setText(f.getName());
                mOrderManager.setAttachmentUri(Uri.fromFile(f));
                galleryAddPic();
                attachmentCloseButton.setVisibility(View.VISIBLE);

            } else if (requestCode == REQUEST_LIBRARY || requestCode == REQUEST_ATTACHMENT && data.getData() != null) {
                Uri attachmentUri = data.getData();
                try (Cursor cursor = getActivity().getContentResolver()
                        .query(attachmentUri, null, null, null, null, null)) {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (cursor != null && cursor.moveToFirst()) {

                        // Note it's called "Display Name".  This is
                        // provider-specific, and might not necessarily be the file name.
                        String displayName = cursor.getString(
                                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.i("FirstFragment", "Display Name: " + displayName);
                        paperAttachment.setText(displayName);
                    }
                }
                mOrderManager.setAttachmentUri(attachmentUri);
                attachmentCloseButton.setVisibility(View.VISIBLE);

            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mOrderManager.getAttachmentUri());
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkPaperInstruction() {
        mOrderManager.setPaperInstruction(paperInstructions.getText().toString());
        if (paperInstructions.getText().toString().isEmpty()
                && mOrderManager.getAttachmentUri() == null) {

            paperInstructions.setError("Either instruction or attachment should be filled");
        }
    }

    private void checkPaperTitle() {
        mOrderManager.setPaperTitle(paperTitle.getText().toString());
        if (paperTitle.getText().toString().isEmpty()) {
            paperTitle.setError("Title cannot be empty");
        }
    }

}
