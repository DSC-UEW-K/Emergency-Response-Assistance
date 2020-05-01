package com.emperor95.era;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emperor95.era.pojo.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private Context context;
    private FirebaseAuth auth;

    public ChatAdapter(Context context, List<Object> list, FirebaseAuth auth){
        this.context = context;
        this.list = list;
        this.auth = auth;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.chat_date) {
            return new DateHolder(view);
        } else {
            return new ChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list.get(position) instanceof Long) {
            DateHolder dateHolder = (DateHolder)holder;
            String chat_date = MyDateUtils.isToday(((Long) list.get(position))) ? "TODAY" : MyDateUtils.formatDate(((Long) list.get(position)));
            dateHolder.chat_time.setText(chat_date);

        } else {
            ChatHolder chatHolder = (ChatHolder) holder;
            Chat model = (Chat) list.get(position);

            chatHolder.message.setText(model.getMsg());
//        holder.chat_time.setText(chat_date);

            String time = "";
            if (model.getTime() != null) {
                time = MyDateUtils.formatTimeWithMarker(model.getTime().getTime());
            }
            chatHolder.time.setText(time);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Check if the item at position is sender
    private boolean isUser(int position) {
        return TextUtils.equals(((Chat)list.get(position)).getFrom(), auth.getUid());
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Long) {
            return R.layout.chat_date;
        }
        if (isUser(position)) {
            return R.layout.item_right_chat;
        } else {
            return R.layout.item_left_chat;
        }
    }

    public static class ChatHolder extends RecyclerView.ViewHolder{

        public TextView message;
        public TextView time;

        public ChatHolder(@NonNull View view) {
            super(view);

            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

    public static class DateHolder extends RecyclerView.ViewHolder{
        public TextView chat_time;

        public DateHolder(@NonNull View view) {
            super(view);

            chat_time = itemView.findViewById(R.id.text_group_chat_date);
        }
    }
}
