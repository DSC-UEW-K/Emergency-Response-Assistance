package com.emperor95.era.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emperor95.era.ChatActivity;
import com.emperor95.era.R;
import com.emperor95.era.pojo.ChatList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import java.util.Arrays;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private FirebaseAuth auth;
    private CollectionReference collectionReference;
    private FirestoreRecyclerAdapter<ChatList, ChatListHolder> adapter;
    private FirestoreRecyclerOptions<ChatList> options;

    private RecyclerView recyclerView;
    private TextView textView4;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        auth = FirebaseAuth.getInstance();
        collectionReference = FirebaseFirestore.getInstance().collection("chats");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        textView4 = root.findViewById(R.id.textView4);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL));

        loadCHatList();

        return root;
    }

    void loadCHatList(){
        Query query = collectionReference.whereArrayContainsAny("user", Arrays.asList(auth.getUid()));
        options = new FirestoreRecyclerOptions.Builder<ChatList>()
                .setQuery(query, ChatList.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatList, ChatListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatListHolder holder, int position, @NonNull ChatList model) {
                String id = getSnapshots().getSnapshot(position).getId();
                String companion_id = id.replace(auth.getUid(),"");
//                holder.text.setText(companion_id);

//                HashMap<String, Object> lm = (HashMap<String, Object>) getSnapshots().getSnapshot(position).get("last_message");
//                holder.last_message.setText(lm.get("message").toString());
                getCompanionDetails(companion_id, holder);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("userID", id.replace(auth.getUid(), ""));
                        startActivity(intent);
                    }
                });

                textView4.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
                return new ChatListHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        adapter.stopListening();
        super.onDestroyView();
    }

    private void getCompanionDetails(String companion, ChatListHolder holder){
        FirebaseFirestore.getInstance().collection("Doctors").document(companion).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = "Dr. " + task.getResult().get("name");
                            holder.text.setText(name);
//                            Glide.with(getActivity()).load(user.getOne()).into(holder.imageView);
                        }
                    }
                });
    }

    public static class ChatListHolder extends RecyclerView.ViewHolder{

        private TextView text, last_message;
        private ImageView imageView;

        public ChatListHolder(@NonNull View view) {
            super(view);

            text =itemView.findViewById(R.id.textView16);
            last_message =itemView.findViewById(R.id.textView17);
            imageView =itemView.findViewById(R.id.imageView3);
        }
    }
}
