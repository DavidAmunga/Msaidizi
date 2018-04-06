package com.buttercell.msaidizi.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.buttercell.msaidizi.AboutApp;
import com.buttercell.msaidizi.R;
import com.buttercell.msaidizi.model.Client;
import com.buttercell.msaidizi.msaidizi.MsaidiziDashboard;
import com.buttercell.msaidizi.msaidizi.MsaidiziHome;
import com.buttercell.msaidizi.msaidizi.MsaidiziMain;
import com.buttercell.msaidizi.msaidizi.MsaidiziProfile;
import com.buttercell.msaidizi.msaidizi.MsaidiziRequests;
import com.buttercell.msaidizi.msaidizi.MsaidiziReviews;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ClientHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Client client;

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Overlock-Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_client_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);

        if(Paper.book().read("currentUser")!=null)
        {
            client=Paper.book().read("currentUser");
        }
        else
        {
            startActivity(new Intent(ClientHome.this,ClientMain.class)); finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        CircleImageView headerImage = (CircleImageView) headerView.findViewById(R.id.imageView);
        TextView txtUserName = (TextView) headerView.findViewById(R.id.txtUsername);
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);
        setNavDetails(txtUserName,txtEmail,headerImage);

        displaySelectedScreen(R.id.nav_services);
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

        String name = client.getUserName();
        String email = client.getUserEmail();

        txtUserName.setText(name);
        txtEmail.setText(email);
        Glide.with(this).load(client.getUserImage()).into(headerImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

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
//                fragment = new ClientDashboard();
//                break;
//            case R.id.nav_profile:
//                fragment = new ClientProfile();
//                break;
            case R.id.nav_services:
                fragment = new ClientServices();
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
            ft.replace(R.id.content_frame_client, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
