package com.emperor95.era.ui.emergencies;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emperor95.era.EmergencyActivity;
import com.emperor95.era.MainActivity;
import com.emperor95.era.R;
import com.emperor95.era.ReportActivity;
import com.emperor95.era.pojo.Emergency;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EmergencyFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnReport;
    private Button btnCall;

    private FirebaseFirestore firestore;
    private FirestoreRecyclerOptions<Emergency> options;
    private FirestoreRecyclerAdapter<Emergency, EmergencyHolder> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL));

        btnReport = root.findViewById(R.id.button);
        btnCall = root.findViewById(R.id.btnCall);
//        location();

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog();
//                location();
                startActivity(new Intent(getActivity(), ReportActivity.class));
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(getContext(), "Allow Phone Permission", Toast.LENGTH_LONG).show();
                    }
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                0);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:112"));
                    startActivity(callIntent);
                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        options = new FirestoreRecyclerOptions.Builder<Emergency>()
                .setQuery(firestore.collection("Emergency_Cases"), Emergency.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Emergency, EmergencyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EmergencyHolder holder, int position, @NonNull Emergency model) {
                final String id = getSnapshots().getSnapshot(position).getId();

                holder.textView.setText(model.getTitle());
                String im = TextUtils.isEmpty(model.getIcon()) ?
                        "https://firebasestorage.googleapis.com/v0/b/dsc-uew-k-1b5d3.appspot.com/o/blood.png?alt=media&token=18fb0097-a8e3-4e97-844e-759dfdaa67e9" :
                        model.getIcon();
                Glide.with(getActivity()).load(im).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EmergencyActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("name", model.getTitle());
                        intent.putExtra("image", model.getIcon());

                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public EmergencyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency, parent, false);
                return new EmergencyHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        adapter.stopListening();
        super.onPause();
    }

    /*private AlertDialog alertDialog;
    private EditText name;
    private Button getBtnReportNow;
    private ProgressBar progressBar;
    private SupportMapFragment googleMap;
    public void showDialog(){
        final View content = getLayoutInflater().inflate(R.layout.dialog_report, null);

        FragmentManager fm = getChildFragmentManager();

//        googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
        googleMap = ((SupportMapFragment) fm.findFragmentById(R.id.map));
        googleMap.getMapAsync(this);

        *//*MapsInitializer.initialize(getActivity().getApplicationContext());

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(38.7222524, -9.139336599999979))
                .title("MyLocation")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.common_full_open_on_phone)));
//                        .fromResource(R.drawable.ic_mobileedge_navpoint)));

        // Move the camera instantly  with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng( 38.7222524, -9.139336599999979), 15));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);*//*

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setView(content)
//                .setTitle(getResources().getString(R.string.password_recovery_text))
//                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                googleMap.onDestroy();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }*/


    public static class EmergencyHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public EmergencyHolder(@NonNull View view) {
            super(view);
            imageView = itemView.findViewById(R.id.imageView2);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
