package com.buttercell.msaidizi.msaidizi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buttercell.msaidizi.AboutApp;
import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.common.Common;
import com.buttercell.msaidizi.model.Msaidizi;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MsaidiziHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
      {

    private static final String TAG = "MsaidiziHome";

    Msaidizi msaidizi;

    Switch availableSwitch;

    //    Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    GeoFire geoFire;

    DatabaseReference onlineRef;
    DatabaseReference currentUserRef;


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
        setContentView(R.layout.activity_msaidizi_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Paper.init(this);

        if (Paper.book().read("currentMsaidizi") != null) {
            msaidizi = Paper.book().read("currentMsaidizi");
        } else {
            startActivity(new Intent(MsaidiziHome.this, MsaidiziMain.class));
            finish();
        }

        View headerView = navigationView.getHeaderView(0);
        CircleImageView headerImage = (CircleImageView) headerView.findViewById(R.id.imageView);
        TextView txtUserName = (TextView) headerView.findViewById(R.id.txtUsername);
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);
        setNavDetails(txtUserName, txtEmail, headerImage);

        displaySelectedScreen(R.id.nav_requests);




        //        Prescense System
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        currentUserRef = FirebaseDatabase.getInstance().getReference("MsaidiziAvailable").child(userId);


        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Remove on connected
                currentUserRef.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //        Geo Fire
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("MsaidiziAvailable");
        geoFire = new GeoFire(refAvailable);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setNavDetails(TextView txtUserName, TextView txtEmail, CircleImageView headerImage) {

        String name = msaidizi.getUserName();
        String email = msaidizi.getUserEmail();

        txtUserName.setText(name);
        txtEmail.setText(email);
        Glide.with(this).load(msaidizi.getUserImage()).into(headerImage);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.msaidizi_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
//            case R.id.nav_dashboard:
//                fragment = new MsaidiziDashboard();
//                break;
//            case R.id.nav_profile:
//                fragment = new MsaidiziProfile();
//                break;
            case R.id.nav_requests:
                fragment = new MsaidiziRequests();
                break;
            case R.id.nav_reviews:
                fragment = new MsaidiziReviews();
                break;
//            case R.id.nav_about:
//                fragment = new AboutApp();
//                break;
            case R.id.nav_log_out:
                FirebaseAuth.getInstance().signOut();
                Paper.book().destroy();
                startActivity(new Intent(getBaseContext(), MsaidiziMain.class));
                finish();
                break;

        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_msaidizi, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


}
