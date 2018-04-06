package com.buttercell.msaidizi.client;

import android.content.Intent;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Client;
import com.buttercell.msaidizi.model.Review;
import com.buttercell.msaidizi.model.Service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ServiceDetails extends AppCompatActivity implements RatingDialogListener {

    private static final String TAG = "ServiceDetails";
    Client client;
    Service service;
    String serviceKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().getExtras() != null) {
            client = (Client) getIntent().getSerializableExtra("currentClient");
            service = (Service) getIntent().getSerializableExtra("currentService");
            serviceKey = getIntent().getStringExtra("serviceKey");

        }

        rateService();

    }

    private void rateService() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate " + service.getServicer() + " Service")
                .setDescription("Give your Feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.colorWhite)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(this)
                .show();

    }


    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        final Review review = new Review(client.getUserName(), client.getUserImage(), value, serviceKey, comments);

        //Set for Client Service Done
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refClient = FirebaseDatabase.getInstance().getReference("Client").child(id).child("myServices").child(serviceKey);
        refClient.child("serviceStatus").setValue("Finished").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Set Review for Msaidizi
                DatabaseReference refMsaidizi = FirebaseDatabase.getInstance().getReference("Msaidizi").child(service.getServicerId()).child("userReviews").child(serviceKey);
                refMsaidizi.setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(ServiceDetails.this, "Msaidizi rated!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(ServiceDetails.this, ClientHome.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ServiceDetails.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public void onNegativeButtonClicked() {
            startActivity(new Intent(ServiceDetails.this,ClientHome.class));
            finish();
    }
}
