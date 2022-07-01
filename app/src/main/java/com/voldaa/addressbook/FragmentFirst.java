package com.voldaa.addressbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FragmentFirst extends Fragment implements  StaticRVadapter.OnplaceListener, RecyclerViewAdapter2.OnRv2Listener,
        StaticRVadapter.OnLongClickListener {

    private StaticRVadapter staticRVadapter;
    private RecyclerViewAdapter2 recyclerViewAdapter2;
    private RecyclerView recyclerView2;
    static  ArrayList<StaticRVModel> places = new ArrayList<StaticRVModel>();
    static  ArrayList<LatLng> locations = new ArrayList<LatLng>();

    static  ArrayList<StaticRVModel> title = new ArrayList<>();
    ArrayList<String> savedPlaces = new ArrayList<>();
    ArrayList<String> savedTitle = new ArrayList<>();
    String[] nearbyList;
    public static SharedPreferences sharedPreferences;
    TextView bottomSheetDiscriptionText;
    TextView sheetTitleEditText;
    LinearLayout bottomSheetId;


    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);

        MainActivity.toolbarTitle.setText("");

        sharedPreferences = getActivity().getSharedPreferences("package com.voldaa.find",Context.MODE_PRIVATE);
        getdata();
        nearbyList = new String[]{"atm", "bank", "hospital","hotel", "gym", "movie_theater", "restaurant"};
        recyclerView2AddItems(view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        staticRVadapter = new StaticRVadapter(title,this,this);
        recyclerView.setAdapter(staticRVadapter);

        bottomSheetId = view.findViewById(R.id.bottomSheet);
        return view;
    }

    private void recyclerView2AddItems(View view) {
        ArrayList<RecyclerViewModel2> item2 = new ArrayList<>();
        item2.add(new RecyclerViewModel2(R.drawable.ic_atm,"ATM"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_bank,"BANK"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_hospital,"HOSPITAL"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_hotel,"HOTEL"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_gym,"GYM"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_movie,"Movie"));
        item2.add(new RecyclerViewModel2(R.drawable.ic_restaurant,"Restaurant"));

        recyclerView2 = (RecyclerView)view.findViewById(R.id.recyclerview2);
        recyclerViewAdapter2 = new RecyclerViewAdapter2(item2, this);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(recyclerViewAdapter2);
    }

    @Override
    public void onPlaceClick(int position) {
        String[] splitResult = places.get(position).getText().split(",");

        MapsFragment.findlocation = locations.get(position);
        MapsFragment.ispinned = true;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                R.layout.stativ_rv_bottomsheet_layout,
                bottomSheetId
        );
        bottomSheetView.findViewById(R.id.bottomSheetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragment1,new MapsFragment()).commit();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.bottomSheetShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, places.get(position).getText());
                startActivity(Intent.createChooser(intent,"share via"));
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDiscriptionText = bottomSheetView.findViewById(R.id.discriptionTextView);
        bottomSheetDiscriptionText.setText(places.get(position).getText());
        bottomSheetDialog.setContentView(bottomSheetView);
        sheetTitleEditText = bottomSheetView.findViewById(R.id.sheetTitleEditText);
        sheetTitleEditText.setText(splitResult[0]);
        bottomSheetDialog.show();
    }

    @Override
    public void onRv2ItemClick(int position) {
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/search/" + nearbyList[position] + "near" + "me/@" + MainActivity.latitude +
                        "," + MainActivity.longitude + ",12z");
            Intent intent = new Intent((Intent.ACTION_VIEW), uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent((Intent.ACTION_VIEW), uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void getdata(){
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();
        savedPlaces.clear();
        title.clear();
        savedTitle.clear();

        try {
            savedPlaces = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<String>())));
            savedTitle =  (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("title",ObjectSerializer.serialize(new ArrayList<String>())));
        }catch(Exception e){
            e.printStackTrace();
        }

        if (savedPlaces.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            if (savedPlaces.size() == latitudes.size() && savedPlaces.size() == longitudes.size()) {
                for (int i = 0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                    places.add(new StaticRVModel(savedPlaces.get(i)));
                    if(savedTitle.get(i).equals("")){
                        title.add(new StaticRVModel(savedPlaces.get(i)));
                    }else {
                        title.add(new StaticRVModel(savedTitle.get(i)));
                    }
                }
            }
        }
    }

    @Override
    public void onLongClick(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Selected Address")
                .setMessage(places.get(position).getText())
                .setPositiveButton("Delete This Address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        places.remove(position);
                        locations.remove(position);
                        title.remove(position);
                        staticRVadapter.notifyDataSetChanged();
//                           to save data permanently
                        PreferenceHelper.saveData();
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        staticRVadapter.notifyDataSetChanged();
                        getdata();
                    }
                })
                .create()
                .show();
    }
}