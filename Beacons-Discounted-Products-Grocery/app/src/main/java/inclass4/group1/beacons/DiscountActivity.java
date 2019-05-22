package inclass4.group1.beacons;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DiscountActivity extends AppCompatActivity {
    private BeaconManager beaconManager;
    private BeaconRegion region;

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;

    ProgressDialog progressDialog;

    TextView section;
    DiscountInfo discountInfo;
    DiscountInfo allDiscounts;

    private final OkHttpClient client = new OkHttpClient();


    public static String loadLifestyle = "http://localhost:3000/lifestyleDiscounts";
    public static String loadProduce = "http://localhost:3000/produceDiscounts";
    public static String loadGrocery = "http://localhost:3000/groceryDiscounts";

    public Beacon closestBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        setTitle("Smart Discounts");

        section = findViewById(R.id.textView);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        progressDialog = new ProgressDialog(DiscountActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Loading discounts!");
        loadAllDiscounts();

        beaconManager = new BeaconManager(this);
        region = new BeaconRegion("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {

                if(!list.isEmpty()){
                    try{
                        Beacon foundBeacon = list.get(0);
                        closestBeacon = foundBeacon;
                        Log.d("demo", "closest beacon : " + closestBeacon);
                        do{
                            if(closestBeacon.getMajor() == 1564 && closestBeacon.getMinor() == 34409){
                                section.setText("Discounts of Produce");
                                loadDiscounts(loadProduce);
                            }else if(closestBeacon.getMajor() == 55125 && closestBeacon.getMinor() == 738){
                                section.setText("Discounts of Grocery");
                                loadDiscounts(loadGrocery);
                            }else if(closestBeacon.getMajor() == 59599 && closestBeacon.getMinor() == 33091){
                                section.setText("Discounts of Lifestyle");
                                loadDiscounts(loadLifestyle);
                            }else{
                                section.setText("All discounts");
                                mAdapter = new DiscountAdapter(getApplicationContext(),allDiscounts);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        }while(closestBeacon != foundBeacon);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    section.setText("All discounts");
                    mAdapter = new DiscountAdapter(getApplicationContext(),allDiscounts);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadAllDiscounts() {
        try {
            String readFile = readJsonFile();
            Gson gson = new Gson();
            allDiscounts = gson.fromJson(readFile,DiscountInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readJsonFile() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.discount);
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
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
        beaconManager.setBackgroundScanPeriod(3000,1000);
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    private void loadDiscounts(String loadURL){
        Request request = new Request.Builder().url(loadURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("test", "onFailure:"  + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            discountInfo = gson.fromJson(result,DiscountInfo.class);
                            mAdapter = new DiscountAdapter(getApplicationContext(),discountInfo);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
