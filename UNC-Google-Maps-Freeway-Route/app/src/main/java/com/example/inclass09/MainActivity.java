package com.example.inclass09;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    Markers m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String readFile = readJsonFile();
            Gson gson = new Gson();
            m = gson.fromJson(readFile,Markers.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private String readJsonFile() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.trip);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        String jsonString = writer.toString();
        return jsonString;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        PolylineOptions rectOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng coordinates;
        int total = m.getPoints().size();

        for(int i = 0; i < m.getPoints().size(); i++){
            coordinates = new LatLng(m.getPoints().get(i).getLatitude(),m.getPoints().get(i).getLongitude());
            rectOptions.add(coordinates).color(Color.GREEN);
//            coordinates = new LatLng(m.getPoints().get(i).getLatitude(),m.getPoints().get(i).getLongitude());
            builder.include(coordinates);
        }
        mMap.addPolyline(rectOptions);

        mMap.addMarker(new MarkerOptions().position(new LatLng(m.getPoints().get(0).getLatitude(),m.getPoints().get(0).getLongitude())).title("Start Location"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(m.getPoints().get(total/2 + 1).getLatitude(),m.getPoints().get(total/2 + 1).getLongitude())).title("Through Route"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(m.getPoints().get(total-1).getLatitude(),m.getPoints().get(total-1).getLongitude())).title("End Location"));

        LatLngBounds bounds = builder.build();
        int padding = (int) (0.1*getResources().getDisplayMetrics().widthPixels);
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.getMaxZoomLevel();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels,
                padding));
    }


}
