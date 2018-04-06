package com.buttercell.msaidizi.msaidizi;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.buttercell.msaidizi.model.Msaidizi;
import com.buttercell.msaidizi.model.Request;
import com.buttercell.msaidizi.model.Service;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MsaidiziRequests extends Fragment {
    private static final String TAG = "MsaidiziRequests";
    @BindView(R.id.request_list)
    RecyclerView requestList;
    Unbinder unbinder;


    Msaidizi msaidizi;

    FirebaseRecyclerAdapter<Request, RequestViewHolder> adapter;

    public MsaidiziRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_msaidizi_requests, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Paper.init(getContext());

        if (Paper.book().read("currentMsaidizi") != null) {
            msaidizi = Paper.book().read("currentMsaidizi");
        }


        getActivity().setTitle("Requests");

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Msaidizi")
                .child(id)
                .child("userRequests");

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, final int position, @NonNull final Request model) {
                holder.setUserImage(model.getUserImage(), getContext());
                holder.setUserName(model.getUserName());
                holder.setRequestDate(model.getRequestDate());
                holder.btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Here at" + position);

                        FirebaseDatabase.getInstance().getReference("Client")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            if (postSnapshot.child("userName").getValue().equals(model.getUserName())) {
                                                String key = postSnapshot.getKey();
                                                showMoreDialog(key, model,adapter.getRef(position).getKey());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                    }
                });

            }

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.request_layout, parent, false);

                return new RequestViewHolder(view);
            }
        };

        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList.setAdapter(adapter);
    }

    private void showMoreDialog(final String key, final Request model, final String requestId) {
        new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_assignment_white)
                .setTitle("Accept this job from " + model.getUserName())
                .setMessage(model.getUserName() + " says " + model.getUserComments())
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Client").child(key).child("myServices").child(requestId);
                        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final Service service = new Service(msaidizi.getUserName(), id, msaidizi.getUserImage(), "Pending", "N/A", msaidizi.getUserSkill());
                        ref.setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Service added!", Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView btnMore;

        public RequestViewHolder(View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnMore);


            itemView.setOnCreateContextMenuListener(this);
        }

        public void setUserImage(String image, Context context) {
            CircleImageView imageView = itemView.findViewById(R.id.userImage);
            Glide.with(context).load(image).into(imageView);
        }

        public void setUserName(String name) {
            TextView txtUserName = itemView.findViewById(R.id.userName);
            txtUserName.setText(name);
        }

        public void setRequestDate(String date) {
            TextView txtStartDate = itemView.findViewById(R.id.requestDate);
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date oldDate = sdf.parse(date);
                Log.d(TAG, "Date " + sdf.format(oldDate));

                SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd");
                Date newDate = sdf1.parse(oldDate.toString());

                txtStartDate.setText("Date: " + sdf1.format(newDate).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an action");
            menu.add(0, 0, getAdapterPosition(), "Deny");

        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String key = adapter.getRef(item.getOrder()).getKey();


        if (item.getItemId() == 0) {
            deleteRequest(key);
        }

        return super.onContextItemSelected(item);
    }

    private void deleteRequest(String key) {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Msaidizi").child(id).child("userRequests").child(key);
        ref.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Request Deleted!", Toast.LENGTH_SHORT).show();
            }
        });
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
}
