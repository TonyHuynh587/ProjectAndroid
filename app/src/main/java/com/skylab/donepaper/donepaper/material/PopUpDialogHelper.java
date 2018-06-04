package com.skylab.donepaper.donepaper.material;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.adapter.PopupAdapter;
import com.skylab.donepaper.donepaper.model.PopUpItem;

import java.util.ArrayList;
import java.util.List;

public class PopUpDialogHelper {
    private Dialog popupDialog;
    private List<PopUpItem> list = new ArrayList<>();
    private boolean isMultiCheckAllow = false;
    private PopUpDialogListener mlistener;

    public void setIsMultiCheckAllow(boolean isMultiCheckAllow) {
        this.isMultiCheckAllow = isMultiCheckAllow;
    }

    public void close(){
        if(popupDialog != null) popupDialog.cancel();
    }

    public void show(Activity activity, ArrayList<PopUpItem> listItem, String title) {
        if (popupDialog == null) {
            popupDialog = new Dialog(activity);

        }

        initPopup(activity, title, listItem);

        if(!activity.isFinishing())
        {
            //show dialog
            try {
                popupDialog.show();
            } catch(Exception e){
                // WindowManager$BadTokenException will be caught and the app would not display
                // the 'Force Close' message
                Log.e("helper","bull shit");
            }

        }else{
            return;
        }

    }

    public void setListener(PopUpDialogListener listener) {
        mlistener = listener;
    }

    public void initPopup(Activity activity, String title, final ArrayList<PopUpItem> arrayList) {
        final PopupAdapter adapter = new PopupAdapter(activity, arrayList);
        popupDialog.setContentView(R.layout.popup_layout);
        TextView popupTitle = (TextView) popupDialog.findViewById(R.id.popup_title);
        ListView popupList = (ListView) popupDialog.findViewById(R.id.popup_list_item);
        popupTitle.setText(title);
        popupList.setAdapter(adapter);
        popupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isMultiCheckAllow){
                    for (PopUpItem item : arrayList){
                        item.setSelected(false);
                    }
                }
                if (arrayList.get(position).isSelected()) {
                    arrayList.get(position).setSelected(false);
                } else {
                    arrayList.get(position).setSelected(true);
                }
                adapter.notifyDataSetChanged();

                if (mlistener != null) {
                    mlistener.userSelected(arrayList);
                }

                popupDialog.dismiss();


            }
        });
    }

    public interface PopUpDialogListener {
        public void userSelected(List<PopUpItem> list);
    }

}

