package com.emperor95.era;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;

import com.emperor95.era.pojo.Chat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private CollectionReference chat_node;

    private String one_to_one, id, uid;
    private ArrayList<Object> list = new ArrayList<>();
    private ChatAdapter adapter;
    long date = 0;
    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private ImageButton imageButton;
    private TextInputEditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        imageButton = findViewById(R.id.imageButton);
        edtMessage = findViewById(R.id.edt_message);

        id = getIntent().getExtras().getString("id", "");
        uid = getIntent().getExtras().getString("userID", "");

        auth = FirebaseAuth.getInstance();
        chat_node = FirebaseFirestore.getInstance().collection("chats");//.child(one_to_one);
        one_to_one = setOneToOneChat(Objects.requireNonNull(auth.getUid()), uid);

        layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        adapter = new ChatAdapter(ChatActivity.this, list, auth);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        imageButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtMessage.getText().toString().trim())) {
                return;
            }
//
            Chat chat = new Chat(Objects.requireNonNull(edtMessage.getText()).toString().trim(), auth.getUid());

            edtMessage.setText("");
            chat_node.document(one_to_one).collection("messages").document().set(chat);

            //TODO
            List<String> list = new ArrayList<>();
            list.add(auth.getUid());
            list.add(uid);

            Map<String, Object> map = new HashMap<>();
            map.put("user",list);
            map.put("last_message", chat);
            chat_node.document(one_to_one).set(map);
        });

        loadChat();

    }

    void loadChat(){
        chat_node.document(one_to_one).collection("messages")
                .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Chat chat = doc.getDocument().toObject(Chat.class);
                        if (date == 0) {
//                                date = chat.getTimestamp().getTime();
//                        date = chat.getTimestamp().getTime();
//                        date = ((Date)doc.getDocument().getDate("timestamp", behavior)).getTime();
                            date = (doc.getDocument().getTimestamp("time", behavior)).toDate().getTime();
                            list.add(date);
                        } else {
//                                if (!MyDateUtils.hasSameDate(date, chat.getTimestamp().getTime())) {
//                                    date = chat.getTimestamp().getTime();
                            if (!MyDateUtils.hasSameDate(date, (doc.getDocument().getTimestamp("time", behavior)).toDate().getTime())) {
                                date = (doc.getDocument().getTimestamp("time", behavior)).toDate().getTime();
                                list.add(date);
                            }
                        }
                        list.add(chat);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            }
        });
    }


    /**Function setsup endpoint for one to one chat**/
    private String setOneToOneChat(String uid1, String uid2)
    {
        int compare = uid1.compareTo(uid2);
        //Check if user1’s id is less than user2's
        if(compare < 0){
            return uid1+uid2;
        }
        else{
            return uid2+uid1;
        }

    }

}
