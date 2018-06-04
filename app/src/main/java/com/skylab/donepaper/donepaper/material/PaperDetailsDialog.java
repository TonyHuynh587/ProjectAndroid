package com.skylab.donepaper.donepaper.material;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.DonePaperApplication;
import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.adapter.PaperDetailsAdapter;
import com.skylab.donepaper.donepaper.fragment.navigation.NavigationManager;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

public class PaperDetailsDialog extends DialogFragment {

    public static final String[] attachmentActionsList = {"Photo", "Camera", "Attach File"};

    private DialogListener mListener;

    private int mSelectedItem;

    @Inject
    public NavigationManager mNavigationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DonePaperApplication) getActivity().getApplication()).getMainComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItem = getArguments().getInt("send_selected_item");
        String title = "";
        ArrayList<String> list = new ArrayList<>();
        switch (getArguments().getInt("dialog_type")) {
            case R.id.paper_type:
                title = getResources().getString(R.string.paper_type);
                list = mNavigationManager.getPaperTypes();
                break;
            case R.id.paper_subject:
                title = getResources().getString(R.string.paper_subject);
                list = mNavigationManager.getPaperSubjects();
                break;
            case R.id.paper_citation:
                title = getResources().getString(R.string.citation_style);
                list = mNavigationManager.getCitationStyles();
                break;
            case R.id.paper_upload:
                title = getResources().getString(R.string.attachment);
                list = new ArrayList<String>(Arrays.asList(attachmentActionsList));
                break;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        TextView textView = (TextView) view.findViewById(R.id.dialog_title);
        textView.setText(title);

        ListView listView = (ListView) view.findViewById(R.id.dialog_list);
        PaperDetailsAdapter adapter = new PaperDetailsAdapter(getActivity(), list, mSelectedItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = position;
                if (mListener != null)
                    mListener.onItemSelected(position);
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    public void setOnItemSelectedListener(DialogListener listener) {
        this.mListener = listener;
    }

    public interface DialogListener {
        public void onItemSelected(int which);
    }
}
