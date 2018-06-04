package com.skylab.donepaper.donepaper.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skylab.donepaper.donepaper.R;
import com.skylab.donepaper.donepaper.rest.DonePaperClient;
import com.skylab.donepaper.donepaper.rest.model.DPResponse;
import com.skylab.donepaper.donepaper.rest.model.OrderData;
import com.skylab.donepaper.donepaper.rest.model.ResponseDownload;
import com.skylab.donepaper.donepaper.utils.DateUtils;

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
    private Context context;
    private static final int TYPE_TEXT_USER = 0;
    private static final int TYPE_TEXT_SENDER = 1;
    private static final int TYPE_FILE_USER = 2;
    private static final int TYPE_FILE_SENDER = 3;
    private int uid;
    private DownloadManager downloadManager;
    private List<OrderData.PostsBean> mMessageListData;
    private String token;

    public ChatMessageAdapter(Context context, List<OrderData.PostsBean> listMessage, int uid, DownloadManager downloadManager, String token) {
        this.context = context;
        this.mMessageListData = listMessage;
        this.uid = uid;
        this.downloadManager = downloadManager;
        this.token = token;
    }

    public void updateRefreshingData(List<OrderData.PostsBean> newMessageList) {
        this.mMessageListData.clear();
        this.mMessageListData.addAll(newMessageList);
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        View messageView;
        switch (viewType) {
            case TYPE_TEXT_USER:
                layout = R.layout.row_chat_receiver;
                messageView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                return new MessageViewHolder(messageView);

            case TYPE_TEXT_SENDER:
                layout = R.layout.row_chat_sender;
                messageView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                return new MessageViewHolder(messageView);
            case TYPE_FILE_USER:
                layout = R.layout.row_chat_receiver_with_file;
                messageView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                return new MessageViewHolder(messageView);
            case TYPE_FILE_SENDER:
                layout = R.layout.row_chat_sender_with_file;
                messageView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                return new MessageViewHolder(messageView);
            default:
                return null;

        }

    }

    public void addMoreMessage(OrderData.PostsBean newMessage) {
        mMessageListData.add(newMessage);
        notifyDataSetChanged();
    }

    public void addMoreFileMessage(OrderData.PostsBean post) {

        OrderData.PostsBean messageWithFile = new OrderData.PostsBean();
        messageWithFile.setAttachment_file_name(post.getAttachment_file_name());
        messageWithFile.setHasAttachment(true);
        messageWithFile.setAuthorId(uid);
        messageWithFile.setContent(post.getContent());
        messageWithFile.setDatetime(post.getDatetime());
        mMessageListData.add(messageWithFile);

        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // ChatMessage chatMessage = mMessageListData.get(position);
        final OrderData.PostsBean message = mMessageListData.get(position);

        switch(holder.getItemViewType()){
            case TYPE_TEXT_SENDER :
                holder.messageText.setText(String.format("%s: %s", message.getAuthor(), message.getContent()));
                break;
            case TYPE_TEXT_USER:
                holder.messageText.setText(message.getContent());
                break;
            case TYPE_FILE_USER:
                //User getting message with file
                if (message.getContent().length() > 0) {
                    holder.contentText.setText(message.getContent());
                }
                SpannableString content = new SpannableString(message.getAttachment_file_name().toString());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                holder.messageText.setText(content);

                holder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDownloadLink(message.getId(), (String) message.getAttachment_file_name());
                    }
                });

                holder.iconDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDownloadLink(message.getId(), (String) message.getAttachment_file_name());
                    }
                });
                break;
            case TYPE_FILE_SENDER:
                //Admin message with file
                if (message.getContent().length() > 0) {
                    holder.contentText.setText(String.format("%s: %s", message.getAuthor(), message.getContent()));
                } else {
                    holder.contentText.setText(String.format("%s: ", message.getAuthor()));
                }
                SpannableString content2 = new SpannableString(message.getAttachment_file_name().toString());
                content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
                holder.messageText.setText(content2);
                holder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDownloadLink(message.getId(), (String) message.getAttachment_file_name());

                    }
                });
                holder.iconDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDownloadLink(message.getId(), (String) message.getAttachment_file_name());
                    }
                });
                break;
        }
        try {
            holder.timeText.setText(DateUtils.convertDateForMessage(message.getDatetime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void DownloadData (Uri uri, String filename) {

        // Create request for android download manager
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(filename);

        //Setting description of request
        request.setDescription("Download from donepaper");
        downloadManager.enqueue(request);

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

    }

    private void clickDownloadLink(int postId, final String filename){
        Toast.makeText(context, "File download started", Toast.LENGTH_SHORT).show();
        Call<DPResponse<ResponseDownload>> downloadCall = DonePaperClient.getApiService().getDownloadLink(postId,token);
        downloadCall.enqueue(new Callback<DPResponse<ResponseDownload>>() {
            @Override
            public void onResponse(Call<DPResponse<ResponseDownload>> call, Response<DPResponse<ResponseDownload>> response) {
                    ResponseDownload download = response.body().getData();
                Uri item_uri = Uri.parse
                        (download.getUrl());
                DownloadData(item_uri,filename);
            }

            @Override
            public void onFailure(Call<DPResponse<ResponseDownload>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        OrderData.PostsBean message = mMessageListData.get(position);
        final boolean isUserLogging = message.getAuthorId() == uid;

        if (isUserLogging) {
            if (message.checkHasAttachment()) return TYPE_FILE_USER;
            else return TYPE_TEXT_USER;
        } else {
            if (message.checkHasAttachment()) return TYPE_FILE_SENDER;
            else return TYPE_TEXT_SENDER;
        }

    }

    @Override
    public int getItemCount() {
        return mMessageListData.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconDownload;
        private TextView messageText;
        private TextView contentText;
        private TextView timeText;

        MessageViewHolder(View itemView) {
            super(itemView);
            iconDownload = (ImageView) itemView.findViewById(R.id.message_ic_download);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            contentText = (TextView) itemView.findViewById(R.id.message_file_content_text);
            timeText = (TextView) itemView.findViewById(R.id.message_time);
        }
    }

}

