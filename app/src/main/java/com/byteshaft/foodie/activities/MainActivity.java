package com.byteshaft.foodie.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.byteshaft.foodie.R;
import com.byteshaft.foodie.fragments.ImagesFragment;
import com.byteshaft.foodie.fragments.UploadFragment;
import com.byteshaft.foodie.utils.Helpers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MyPREFERENCES = "MyPrefs";
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if(!Helpers.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
        setContentView(R.layout.activity_main);
        if (Helpers.isUserLoggedIn()) {
            loadFragment(new UploadFragment());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public static MainActivity getInstance() {
        return instance;
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

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(Helpers.isUserLoggedIn());
        if(!Helpers.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
    }

    public void closeApplication() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        startActivity(startMain);
        MainActivity.this.finish();
    }

    // Method to load the fragment required Fragment as parameter
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_upload_image:
                loadFragment(new UploadFragment());
                break;

            case R.id.nav_gallery:
                loadFragment(new ImagesFragment());
                break;
            case R.id.nav_logout:
                showLogoutDialog();


        }
        item.setCheckable(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedpreferences = Helpers.getPreferenceManager();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        closeApplication();
                        if (!Helpers.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            Helpers.userLogin(false);
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
