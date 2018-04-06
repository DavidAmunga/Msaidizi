package com.buttercell.msaidizi.msaidizi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.client.ClientMain;
import com.buttercell.msaidizi.model.Msaidizi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MsaidiziMain extends AppCompatActivity {
    private static final String TAG = "MsaidiziMain";

    FirebaseAuth mAuth;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    List<String> categoryList = new ArrayList<>();
    @BindView(R.id.txt_user_home)
    TextView txtUserHome;

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
        setContentView(R.layout.activity_msaidizi_main);

        mAuth = FirebaseAuth.getInstance();


        ButterKnife.bind(this);

        Paper.init(this);


        if (Paper.book().read("currentMsaidizi") != null) {
            final SpotsDialog waitingDialog = new SpotsDialog(MsaidiziMain.this);

            startActivity(new Intent(MsaidiziMain.this, MsaidiziHome.class));
            finish();

        }


        String[] myResArray = getResources().getStringArray(R.array.list_category);
        categoryList = Arrays.asList(myResArray);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        txtUserHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClientMain.class));
                finish();
            }
        });
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_msaidizi_register, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);
        final MaterialEditText edtPay = register_layout.findViewById(R.id.edtPay);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialSpinner spinnerCategory = register_layout.findViewById(R.id.spinnerCategory);


        spinnerCategory.setItems(categoryList);

        spinnerCategory.getSelectedIndex();

        dialog.setView(register_layout);

//        Set button

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

//                Check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter Phone", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtName.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter username", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPay.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter pay per hour", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final SpotsDialog waitingDialog = new SpotsDialog(MsaidiziMain.this);
                waitingDialog.show();
//                Register now


                final String userName = edtName.getText().toString().trim();
                final String email = edtEmail.getText().toString().trim();
                final String phone = edtPhone.getText().toString().trim();
                final int pay = Integer.parseInt(edtPay.getText().toString().trim());
                final String skill = categoryList.get(spinnerCategory.getSelectedIndex());


                if (skill == null) {
                    Snackbar.make(rootLayout, "Please select a skill", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {

                    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
//                                Save driver to db

                                    final Msaidizi msaidizi = new Msaidizi(userName, email, phone, skill, "none", "none", pay);
                                    final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

//                                  Using Msaidizi ID as key
                                    FirebaseDatabase.getInstance().getReference("Msaidizi").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(msaidizi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            waitingDialog.dismiss();
                                            Snackbar.make(rootLayout, "Registered successfully", Snackbar.LENGTH_SHORT).show();
                                            Paper.book().write("currentMsaidizi", msaidizi);
                                            Paper.book().write("userId", uid);
                                            startActivity(new Intent(MsaidiziMain.this, MsaidiziFinishProfile.class));
                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    waitingDialog.dismiss();
                                                    Snackbar.make(rootLayout, "Failed " + e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    waitingDialog.dismiss();
                                    Snackbar.make(rootLayout, "Failed " + e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();

                                }
                            });
                }
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign in");
        dialog.setMessage("Please use email to sign in");


        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);


        dialog.setView(login_layout);

//        Set button

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final String email = edtEmail.getText().toString().trim();
                final String pass = edtPassword.getText().toString().trim();

//                Set disable button Sign in if is processing
                btnSignIn.setEnabled(false);

//                Check validation
                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 6) {
                    Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(MsaidiziMain.this);
                waitingDialog.show();


                final DatabaseReference refMsaidizi = FirebaseDatabase.getInstance().getReference("Msaidizi");


//                Sign in
                mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        final String uid = authResult.getUser().getUid();

                        refMsaidizi.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Msaidizi msaidizi = dataSnapshot.getValue(Msaidizi.class);
                                    Paper.book().write("currentMsaidizi", msaidizi);
                                    Paper.book().write("userId", uid);
                                    waitingDialog.dismiss();
                                    Toast.makeText(MsaidiziMain.this, "Welcome!" + msaidizi.getUserName(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MsaidiziMain.this, MsaidiziHome.class));
                                    finish();
                                } else {
                                    waitingDialog.dismiss();
                                    Snackbar.make(rootLayout, "Sorry, Msaidizi does not exist", Snackbar.LENGTH_SHORT)
                                            .show();

                                    btnSignIn.setEnabled(true);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                })
                        .

                                addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        waitingDialog.dismiss();
                                        Snackbar.make(rootLayout, "Failed " + e.getLocalizedMessage(), Snackbar.LENGTH_SHORT)
                                                .show();

//                                Activate button
                                        btnSignIn.setEnabled(true);
                                    }
                                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
