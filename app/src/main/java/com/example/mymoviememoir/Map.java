package com.example.mymoviememoir;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.networkconnection.NetworkConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map extends Fragment implements OnMapReadyCallback {
    NetworkConnection networkConnection;
    SupportMapFragment mapFragment;
    Person person;
    GoogleMap googleMap;


    public Map(Person person) {
        this.person = person;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map, container, false);
        networkConnection = new NetworkConnection();



        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //prepare data for map -> home
        String homeAddress = person.getAddress();
        LatLng personHome = getLocationFromAddress(getContext(),homeAddress);

        GetAllCinemaInfoTask getAllCinemaInfoTask = new GetAllCinemaInfoTask();
        getAllCinemaInfoTask.execute();

        if(personHome != null){
            googleMap.addMarker(new MarkerOptions().position(personHome).title("My Home").icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        } else {
            Toast.makeText(getContext(),"Invalid User Address Information",Toast.LENGTH_LONG).show();
        }
    }


    public LatLng getLocationFromAddress(Context context,String strAddress){
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try{
            address = coder.getFromLocationName(strAddress, 1);
//            address = coder.getFromLocationName("City,3001", 1);
//            Log.i("test: ",String.valueOf(address.get(0)));
            if(address.size() == 0){
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(),location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }


    private class GetAllCinemaInfoTask extends AsyncTask<Void,Void, ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(Void... params){
            return networkConnection.getAllCinemas();
        }

        @Override
        protected void onPostExecute(ArrayList<String> cinemas){
            for(int index = 0; index < cinemas.size(); index++){
                String info = cinemas.get(index);
                String name = info.substring(0,info.indexOf("<"));
                String postcode = info.substring(info.indexOf("<")+1,info.indexOf(">"));
                String address = name + "," + postcode;


                LatLng cinemaAddress = getLocationFromAddress(getContext(),address);
                if(index == 0){
                    float zoomLevel = (float) 10.0;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cinemaAddress,zoomLevel));
                }

                if(cinemaAddress != null){
                    googleMap.addMarker(new MarkerOptions().position(cinemaAddress).title(name).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                }
            }


        }
    }


}
