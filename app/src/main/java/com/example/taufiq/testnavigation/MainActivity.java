package com.example.taufiq.testnavigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Restaurant.OnFragmentInteractionListener,
        Chinese.OnFragmentInteractionListener,
        MeatLover.OnFragmentInteractionListener {
        Fragment fragment = null;
        NavigationView navigationView;
        Boolean doubleBackToExitPressedOnce = false;
        private static final int PERMISSION_REQUEST_CODE = 200;
        LocationFinder finder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!checkPermission()){
            requestPermission();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Restaurant();
        fragmentTransaction.replace(R.id.main_frame, fragment).commit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.nav_res);
//        Restaurant fragment = new Restaurant();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.main_frame, fragment)
//                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        finder = new LocationFinder(MainActivity.this);
        if(finder.canGetLocation()){
            double latitude = finder.getLatitude();
            double longitude = finder.getLongitude();

            Toast.makeText(this, "Longitude: " + longitude + " Latitude: " + latitude,
                    Toast.LENGTH_SHORT).show();

        }else{
            finder.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (checkNavigationMenuItem() != 0)
            {   this.doubleBackToExitPressedOnce = false;
                Log.v("Back", String.valueOf(checkNavigationMenuItem()));
                navigationView.setCheckedItem(R.id.nav_res);
                fragment = new Restaurant();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.main_frame, fragment).commit();
                FragmentManager manager = getSupportFragmentManager();
                if (manager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                    manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
            else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    Log.v("Back", String.valueOf(doubleBackToExitPressedOnce));
                    System.exit(0);
                    return;
                }
                Log.v("Back", String.valueOf(doubleBackToExitPressedOnce));
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

     private int checkNavigationMenuItem() {
         Menu menu = navigationView.getMenu();
         for (int i = 0; i < menu.size(); i++) {
             if (menu.getItem(i).isChecked())
                 return i;
         }
         return -1;
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_res) {
            navigationView.setCheckedItem(R.id.nav_res);
            Restaurant restaurant= new Restaurant();

            //Change is here
            if(fragmentManager.findFragmentById(R.id.nav_res)!=null){
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .show(fragmentManager.findFragmentById(R.id.nav_res)).commit();
            }
            else{
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.main_frame,restaurant).addToBackStack(getResources().getString(R.string.res)).commit();
            }
        } else if (id == R.id.nav_chinese) {
            navigationView.setCheckedItem(R.id.nav_chinese);
            Chinese chinese = new Chinese();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.main_frame,chinese).addToBackStack("Chinese").commit();

        } else if (id == R.id.nav_meat) {
            navigationView.setCheckedItem(R.id.nav_meat);
            MeatLover meatLover = new MeatLover();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.main_frame,meatLover).addToBackStack("MeatLover").commit();

        } else if (id == R.id.nav_cakes) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

         @Override
         public void onFragmentInteraction(Uri uri) {
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted)
                        Toast.makeText(getApplicationContext(), "Location Accepted", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "Please accept location permission from settings.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to location permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("CANCEL", null)
                .create()
                .show();
    }
 }
