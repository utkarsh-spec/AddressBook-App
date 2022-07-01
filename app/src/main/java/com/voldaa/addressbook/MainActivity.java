package com.voldaa.addressbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    static double latitude;
    static double longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    DrawerLayout drawerLayout;
    NavigationView menuNavigationView;
    Toolbar toolbar;
    static TextView toolbarTitle;

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1,new FragmentFirst()).commit();
        }else{
            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyTheme_Dark);

        SharedPreferences prefs = getSharedPreferences("com.example.srushtee.dummy", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("service_status", true);

        if(switchState){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.MyTheme_Dark);
        }else{
            setTheme(R.style.MyTheme_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        // toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        // navigation drawer menu
        menuNavigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // menu options
        Menu menu = menuNavigationView.getMenu();
        // menu item listner

        menuNavigationView.setNavigationItemSelectedListener(this);
        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.item1:
                        fragment = new FragmentFirst();
                        break;
                    case R.id.item2:
                        fragment = new MapsFragment();
                        break;
                    case R.id.item3:
                        fragment = new SettingsFragment();
                        break;
                }
                loadFragment(fragment,item.getItemId());
//
                return true;
            }
        });

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

    }

    private void loadFragment(Fragment fragment,int id) {
    // replace fragment
        if(id==R.id.item2){
            MapsFragment.ispinned = false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, fragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            askLocationPermission();
        }
        else{
            // permission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1,new FragmentFirst()).commit();
            MapsFragment.ispinned = false;
        }
    }

    public void askLocationPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        return true;
    }
}