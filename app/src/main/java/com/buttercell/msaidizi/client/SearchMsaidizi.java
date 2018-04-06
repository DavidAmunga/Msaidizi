package com.buttercell.msaidizi.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Client;
import com.buttercell.msaidizi.model.Msaidizi;
import com.buttercell.msaidizi.model.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gigamole.library.PulseView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SearchMsaidizi extends AppCompatActivity {

    private static final String TAG = "SearchMsaidizi";

    @BindView(R.id.pv)
    PulseView pv;
    @BindView(R.id.msaidizi_list)
    RecyclerView msaidiziList;

    int radius = 1; //1 Km

    boolean isMsaidiziFound = false;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    private String msaidiziId = "";

    Client client;

    String skill = "";

    FirebaseRecyclerAdapter<Msaidizi, MsaidiziViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_msaidizi);
        ButterKnife.bind(this);

        Paper.init(this);

        if (Paper.book().read("currentUser") != null) {
            client = Paper.book().read("currentUser");
        }

        //  pv.startPulse();


        if (getIntent().getExtras() != null) {
            skill = getIntent().getExtras().getString("skill");

        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(skill + "s");


        Query msaidiziQuery = FirebaseDatabase.getInstance()
                .getReference("Msaidizi")
                .orderByChild("userSkill")
                .equalTo(skill);

        FirebaseRecyclerOptions<Msaidizi> options =
                new FirebaseRecyclerOptions.Builder<Msaidizi>()
                        .setQuery(msaidiziQuery, Msaidizi.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Msaidizi, MsaidiziViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MsaidiziViewHolder holder, final int position, @NonNull Msaidizi model) {

                holder.setUserImage(model.getUserImage(), getApplicationContext());
                holder.setUserName(model.getUserName());
                holder.setUserSkill(model.getUserSkill());
                holder.setUserPayRate(String.valueOf(model.getPayrate()));

                holder.btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Here at" + position);
                        sendRequestToMsaidizi(adapter.getRef(position).getKey());
                    }
                });
            }

            @Override
            public MsaidiziViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.msaidizi_layout, parent, false);

                return new MsaidiziViewHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        msaidiziList.setAdapter(adapter);


        msaidiziList.setHasFixedSize(true);
        msaidiziList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    public static class MsaidiziViewHolder extends RecyclerView.ViewHolder {

        ImageView btnMore;

        public MsaidiziViewHolder(View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

        public void setUserImage(String image, Context context) {
            CircleImageView imageView = itemView.findViewById(R.id.userImage);
            Glide.with(context).load(image).into(imageView);
        }

        public void setUserSkill(String skill) {
            TextView txtUserSkill = itemView.findViewById(R.id.userSkill);
            txtUserSkill.setText(skill);
        }

        public void setUserPayRate(String payRate) {
            TextView txtUserSkill = itemView.findViewById(R.id.userPayRate);
            txtUserSkill.setText("KES " + payRate + "/hr");
        }

        public void setUserName(String name) {
            TextView txtUserName = itemView.findViewById(R.id.userName);
            txtUserName.setText(name);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void sendRequestToMsaidizi(final String msaidiziId) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Msaidizi").child(msaidiziId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name = dataSnapshot.child("userName").getValue().toString();
                String skill = dataSnapshot.child("userSkill").getValue().toString();

                new LovelyTextInputDialog(SearchMsaidizi.this, R.style.AppTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Request services from " + name)
                        .setMessage("Describe your problem to the " + skill)
                        .setIcon(R.drawable.ic_person_white)
                        .setInputFilter("Please insert something", new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                return !text.isEmpty();
                            }
                        })
                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {

                                Date now = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                String formattedDate = df.format(now);


                                Request request = new Request(client.getUserName(), client.getUserImage(), text, formattedDate, "unaccepted");
                                ref.child("userRequests").push().setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        adapter.stopListening();
                                        Toast.makeText(SearchMsaidizi.this, "Sent Request to " + name, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @OnClick(R.id.pv)
    public void onViewClicked() {
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);
//
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (searchView.isSearchOpen()) {
//            searchView.closeSearch();
//        } else {
//            super.onBackPressed();
//        }
//    }
//

}
