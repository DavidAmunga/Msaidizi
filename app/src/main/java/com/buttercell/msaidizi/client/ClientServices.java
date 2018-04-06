package com.buttercell.msaidizi.client;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Client;
import com.buttercell.msaidizi.model.Review;
import com.buttercell.msaidizi.model.Service;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientServices extends Fragment{

    private static final String TAG = "ClientServices";
    @BindView(R.id.services_list)
    RecyclerView servicesList;
    @BindView(R.id.fabRequest)
    FloatingActionButton fabRequest;
    Unbinder unbinder;


    String serviceKey = "";

    Client client;

    Service currentService;


    FirebaseRecyclerAdapter<Service, ServicesViewHolder> adapter;

    public ClientServices() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_services, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Services");

        Paper.init(getContext());

        if (Paper.book().read("currentUser") != null) {
            client = Paper.book().read("currentUser");
        }

        fabRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MsaidiziServices.class));
            }
        });


        servicesList.setHasFixedSize(true);
        servicesList.setLayoutManager(new LinearLayoutManager(getContext()));


        getActivity().setTitle("Services");

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Client")
                .child(id)
                .child("myServices");


        FirebaseRecyclerOptions<Service> options =
                new FirebaseRecyclerOptions.Builder<Service>()
                        .setQuery(query, Service.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Service, ServicesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServicesViewHolder holder, int position, @NonNull Service model) {
                holder.setUserImage(model.getServicerImage(), getContext());
                holder.setUserName(model.getServicer());
                holder.setStatus(model.getServiceStatus());
                holder.setUserSkill(model.getServicerSkill());

                holder.btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @Override
            public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.service_layout, parent, false);

                return new ServicesViewHolder(view);
            }
        };
        servicesList.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String key = adapter.getRef(item.getOrder()).getKey();

        if (item.getItemId() == 0) {
            serviceKey = key;
            currentService = adapter.getItem(item.getOrder());

            Log.d(TAG, "onContextItemSelected: "+currentService.getServicer());
            Intent intent=new Intent(getContext(),ServiceDetails.class);
            intent.putExtra("currentService",currentService);
            intent.putExtra("serviceKey",serviceKey);
            intent.putExtra("currentClient",client);
            startActivity(intent);

        }

        return super.onContextItemSelected(item);
    }


    public static class ServicesViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView btnMore;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnMore);


            itemView.setOnCreateContextMenuListener(this);
        }

        public void setUserImage(String image, Context context) {
            CircleImageView imageView = itemView.findViewById(R.id.userImage);
            Glide.with(context).load(image).into(imageView);
        }

        private void setUserName(String name) {
            TextView txtUserName = itemView.findViewById(R.id.userName);
            txtUserName.setText(name);
        }

        private void setUserSkill(String skill) {
            TextView txtUserSkill = itemView.findViewById(R.id.userSkill);
            txtUserSkill.setText(skill);
        }

        private void setStatus(String status) {
            TextView txtServiceStatus = itemView.findViewById(R.id.service_status);
            txtServiceStatus.setText(status);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an action");
            menu.add(0, 0, getAdapterPosition(), "Rate");

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
