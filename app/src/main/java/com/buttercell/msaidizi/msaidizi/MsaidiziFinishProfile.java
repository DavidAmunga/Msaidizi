package com.buttercell.msaidizi.msaidizi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Msaidizi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MsaidiziFinishProfile extends AppCompatActivity {

    @BindView(R.id.txtUserName)
    TextView txtUserName;
    @BindView(R.id.txtUserImage)
    CircleImageView txtUserImage;
    @BindView(R.id.txt_bio)
    EditText txtBio;
    @BindView(R.id.btnFinish)
    Button btnFinish;

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Overlock-Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_msaidizi_finish_profile);
        ButterKnife.bind(this);
        Toast.makeText(MsaidiziFinishProfile.this, "One more step!", Toast.LENGTH_SHORT).show();

        Paper.init(this);

        Msaidizi msaidizi = Paper.book().read("currentMsaidizi");

        txtUserName.setText(msaidizi.getUserName());


        txtUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
    }

    @OnClick(R.id.btnFinish)
    public void onViewClicked() {
        final String bio = txtBio.getText().toString().trim();
        if (!TextUtils.isEmpty(bio) && mImageUri != null) {
            final SpotsDialog waitingDialog = new SpotsDialog(MsaidiziFinishProfile.this);
            waitingDialog.show();

            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            StorageReference filepath = FirebaseStorage.getInstance().getReference("UserImages").child(userId + ".jpg");
            filepath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    String imageUrl = task.getResult().getDownloadUrl().toString();

                    waitingDialog.dismiss();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Msaidizi").child(userId);
                    ref.child("userBio").setValue(bio);
                    ref.child("userImage").setValue(imageUrl);


                    Toast.makeText(MsaidiziFinishProfile.this, "Success!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MsaidiziFinishProfile.this, MsaidiziHome.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    waitingDialog.dismiss();
                    Toast.makeText(MsaidiziFinishProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            txtUserImage.setImageURI(mImageUri);
        }
    }
}
