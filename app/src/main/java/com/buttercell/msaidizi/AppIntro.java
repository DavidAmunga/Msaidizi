package com.buttercell.msaidizi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.buttercell.msaidizi.client.ClientHome;
import com.buttercell.msaidizi.client.ClientMain;
import com.buttercell.msaidizi.msaidizi.MsaidiziHome;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class AppIntro extends AppCompatActivity {
    private static final String TAG = "AppIntro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        Paper.init(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Timer().schedule(new TimerTask() {
                                 @Override
                                 public void run() {

                                     if (Paper.book().read("userId") != null) {
                                         String uid = Paper.book().read("userId");


                                         DatabaseReference refClient = FirebaseDatabase.getInstance().getReference("Client").child(uid);
                                         refClient.addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(DataSnapshot dataSnapshot) {
                                                 if (dataSnapshot.exists()) {
                                                     startActivity(new Intent(getApplicationContext(), ClientHome.class));
                                                     finish();
                                                 } else {
                                                     startActivity(new Intent(getApplicationContext(), MsaidiziHome.class));
                                                     finish();
                                                 }
                                             }

                                             @Override
                                             public void onCancelled(DatabaseError databaseError) {

                                             }
                                         });

                                     }
                                     else {
                                         startActivity(new Intent(getApplicationContext(), ClientMain.class));
                                         finish();
                                     }
                                 }


                             }
                , 2000);

    }
}
