package com.voldaa.addressbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment implements GoogleMap.OnMapLongClickListener {
    public static GoogleMap mMap;
    public static LocationManager locationManager;
    private Button addLocationButton;
    private Button findPathButton;
    private Button mapViewButton;
    private Boolean isOnfind;
    public static LocationListener locationListener;
    public static LatLng userLocation;
    public LatLng onFindLocation;
    public static boolean ispinned;
    public static LatLng findlocation;
    private SearchView searchEditText;
    private int viewCount;
    LinearLayout mapsBottomSheetId;
    EditText mapsBottomsheetEditText;


    public void setMapLong(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public  void findListPlace(LatLng pinLocation){
        isOnfind = true;
        findPathButton.setVisibility(View.VISIBLE);
        onFindLocation = new LatLng(pinLocation.latitude, pinLocation.longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(pinLocation).title(getAddress(pinLocation.latitude, pinLocation.longitude)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pinLocation, 15));
        locationManager.removeUpdates(locationListener);
    }

    public void centerMapOnLocation(Location location, String title) {
        isOnfind = false;
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        locationManager.removeUpdates(locationListener);
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap=googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setOnMapLongClickListener(MapsFragment.this);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    // location permission granted
                    mMap.setMyLocationEnabled(true);
                    if (!ispinned) {
                        addLocationButton.setVisibility(View.VISIBLE);
                        findPathButton.setVisibility(View.INVISIBLE);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        centerMapOnLocation(lastKnownLocation, "location");

                    }else{
                        addLocationButton.setVisibility(View.INVISIBLE);
//                        findPathButton.setVisibility(View.VISIBLE);
                        findListPlace(findlocation);
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_maps, container, false);

        MainActivity.toolbarTitle.setText("Map View");

        addLocationButton = (Button)view.findViewById(R.id.addLocationButton);
        searchEditText = (SearchView) view.findViewById(R.id.searchEditText);
        findPathButton = (Button)view.findViewById(R.id.findPathButton);
        mapViewButton = (Button)view.findViewById(R.id.mapViewButton);
        mapsBottomSheetId = view.findViewById(R.id.mapsBottomsheet);

        viewCount = 1;
        mapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewCount<4){
                    viewCount++;
                }else {
                    viewCount = 1;
                }
                mMap.setMapType(viewCount);
            }
        });

        findPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTrack(findlocation);
            }
        });

        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchEditText.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getContext());
                    try{
                        addressList = geocoder.getFromLocationName(location,1);

                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Not Found!", Toast.LENGTH_SHORT).show();
                    }
                    if(addressList.size()>0){
                        Address address = addressList.get(0);
                        findlocation = new LatLng(address.getLatitude(),address.getLongitude());
                        findListPlace(findlocation);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        centerMapOnLocation(location, "Your location");
                    }
                };
            }else{
                Toast.makeText(getContext(), "Network connecton failed", Toast.LENGTH_SHORT).show();
            }
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    ispinned = true;

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    double lat = 0.0;
                    double lng = 0.0;
                    LatLng latLng = userLocation;
                    if (isOnfind) {
                        latLng = onFindLocation;
                        lat = onFindLocation.latitude;
                        lng = onFindLocation.longitude;
                    } else {
                        latLng = userLocation;
                        lat = userLocation.latitude;
                        lng = userLocation.longitude;
                    }
                    openBottomSheetDialog(latLng,getAddress(lat, lng));
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onMapLongClick(@NonNull  LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng.latitude,latLng.longitude)));
        openBottomSheetDialog(latLng,getAddress(latLng.latitude,latLng.longitude));
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public void displayTrack(LatLng destination){
        String lati = String.valueOf(destination.latitude);
        String longi = String.valueOf(destination.longitude);
        Log.i("destination",destination.toString());
        try{
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir//"+lati+","+longi+"/@"+lati+","+longi+",12z");
            Intent intent = new Intent((Intent.ACTION_VIEW),uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent((Intent.ACTION_VIEW),uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    private void openBottomSheetDialog(LatLng latLng, String address){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                R.layout.maps_bottomsheet_layout,
                mapsBottomSheetId
        );
        mapsBottomsheetEditText = bottomSheetView.findViewById(R.id.mapsBottomsheetEditText);

        bottomSheetView.findViewById(R.id.mapsBottonsheetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // listener
                FragmentFirst.places.add(new StaticRVModel(address));
                FragmentFirst.locations.add(latLng);
                String titleList = mapsBottomsheetEditText.getText().toString();
                FragmentFirst.title.add(new StaticRVModel(titleList));
                PreferenceHelper.saveData();

                bottomSheetDialog.dismiss();
                Toast.makeText(getContext(), "location saved", Toast.LENGTH_SHORT).show();
            }
        });
        TextView discription = bottomSheetView.findViewById(R.id.mapsDiscriptionTextView);
        discription.setText(address);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}