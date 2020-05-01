package com.emperor95.era;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.emperor95.era.pojo.EmDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class EmergencyActivity extends AppCompatActivity {

    private CollectionReference collectionReference;
    private FirestoreRecyclerOptions<EmDetails>  options;
    private FirestoreRecyclerAdapter<EmDetails, Holder> adapter;

    private String id = "", image = "", name = "";

    private RecyclerView recyclerView;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        collectionReference = FirebaseFirestore.getInstance().collection("Emergency_Cases");
        id = Objects.requireNonNull(getIntent().getExtras()).getString("id", "");
        image = Objects.requireNonNull(getIntent().getExtras()).getString("image", "");
        name = Objects.requireNonNull(getIntent().getExtras()).getString("name", "");

        getSupportActionBar().setTitle(name);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(EmergencyActivity.this, RecyclerView.VERTICAL, false);

        imageView3 = findViewById(R.id.imageView3);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        Glide.with(EmergencyActivity.this).load(image).into(imageView3);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = collectionReference.document(id).collection("steps");
        options = new FirestoreRecyclerOptions.Builder<EmDetails>()
                .setQuery(query.orderBy("stepNum"), EmDetails.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<EmDetails, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull EmDetails model) {
                holder.text.setText(model.getDescription());

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound((model.getStepNum()) + "", R.color.colorPrimary);

                holder.imageView.setImageDrawable(drawable);
            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_em_detail, parent, false);
                return new Holder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        adapter.stopListening();
        super.onDestroy();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        private TextView text;
        private ImageView imageView;

        public Holder(@NonNull View view) {
            super(view);

            text = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView5);
        }
    }
}
