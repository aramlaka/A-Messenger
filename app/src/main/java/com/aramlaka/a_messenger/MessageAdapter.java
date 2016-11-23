package com.aramlaka.a_messenger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>  {

    private ArrayList<Message> messages;
    private Context context;
    private SetMessages setMessages;
    private PrettyTime p;

    public MessageAdapter(ArrayList<Message> messages, Context context, SetMessages setMessages) {
        this.messages = messages;
        this.context = context;
        this.setMessages = setMessages;
        p = new PrettyTime();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView deleteMessageButton;
        public ImageView addCommentButton;
        public ImageView messageImage;
        public TextView messageText;
        public TextView messageAuthor;
        public TextView dateText;

        public ViewHolder(View mv) {
            super(mv);

            deleteMessageButton = (ImageView) mv.findViewById(R.id.deleteMessageButton);
            addCommentButton = (ImageView) mv.findViewById(R.id.addCommentButton);
            messageImage = (ImageView) mv.findViewById(R.id.messageImage);
            messageText = (TextView) mv.findViewById(R.id.messageText);
            messageAuthor = (TextView) mv.findViewById(R.id.messageUser);
            dateText = (TextView) mv.findViewById(R.id.dateText);
        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View mv = inflater.inflate(R.layout.item_message, parent, false);

        return new MessageAdapter.ViewHolder(mv);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        TextView messageText = holder.messageText;
        TextView messageAuthor = holder.messageAuthor;
        TextView dateText = holder.dateText;
        ImageView deleteMessageButton = holder.deleteMessageButton;
        ImageView messageImage = holder.messageImage;

        Message message = messages.get(position);
        messageText.setText(message.getBody());
        messageAuthor.setText(message.getAuthor());
        dateText.setText(p.format(message.getDate()));

        deleteMessageButton.setOnClickListener(new DeleteMessageOnClick(message, setMessages));

        if (message.getImageUrl() != null) {
            Picasso.with(context).load(message.getImageUrl()).into(messageImage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface SetMessages {
        void setMessages(ArrayList<Message> messages);
        void deleteMessage(Message message);
    }

    public class DeleteMessageOnClick implements View.OnClickListener {
        private Message message;
        private SetMessages sm;

        public DeleteMessageOnClick(Message message, SetMessages sm) {
            this.message = message;
            this.sm = sm;
        }

        @Override
        public void onClick(View view) {
            sm.deleteMessage(message);

            Log.d("debug", "clicky");
        }
    }
}
