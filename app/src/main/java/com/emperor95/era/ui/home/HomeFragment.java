package com.emperor95.era.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.emperor95.era.R;
import com.emperor95.era.pojo.Tips;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private CollectionReference collectionReference;
    private FirestoreRecyclerOptions<Tips> options;
    private FirestoreRecyclerAdapter<Tips, TipsHolder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        collectionReference = FirebaseFirestore.getInstance().collection("Tips");

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(RecyclerView.VERTICAL);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFirestore();
    }

    private void initFirestore(){
        options = new FirestoreRecyclerOptions.Builder<Tips>()
                .setQuery(collectionReference.orderBy("time", Query.Direction.DESCENDING), Tips.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tips, TipsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TipsHolder holder, int position, @NonNull Tips model) {

//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa", Locale.ENGLISH);
//                holder.date.setText(simpleDateFormat.format(model.getTimestamp())); authur

                String date = DateUtils.getRelativeTimeSpanString(model.getTime().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();

                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.time.setText(date);
                holder.author.setText(model.getAuthor());

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                int color = generator.getRandomColor();
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(position + "", color);

                holder.image.setImageDrawable(drawable);
            }

            @NonNull
            @Override
            public TipsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tips, parent, false);

                return new TipsHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        adapter.stopListening();
        super.onDestroy();
    }

    public static class TipsHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView description;
        private TextView time;
        private ImageView image;
        private TextView author;

        public TipsHolder(@NonNull View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.imageView);
            author = itemView.findViewById(R.id.author);
        }
    }

}
