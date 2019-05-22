package project1.group1.restaurantbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import project1.group1.restaurantbooking.data.CustomerResponse;
import project1.group1.restaurantbooking.data.TableResponse;
import project1.group1.restaurantbooking.data.UserFeedback;

public class UserActivity extends AppCompatActivity {

    String token;
    private final OkHttpClient client = new OkHttpClient();
    TableResponse tableResponse, inQueue;
    CustomerResponse customerResponse;
    UserFeedback feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        loadSharedPreferences();
        findViewById(R.id.btnPlaceOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placeOrder = new Intent(UserActivity.this,PlaceOrderActivity.class);
                startActivity(placeOrder);
            }
        });

        findViewById(R.id.btnViewReservations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchReservedTables();
            }
        });

        findViewById(R.id.btnQueue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCustomersInQueue();
                Intent inqueue = new Intent(UserActivity.this,InqueueActivity.class);
                startActivity(inqueue);
            }
        });

        findViewById(R.id.btnViewTables).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTableInfo();
            }
        });

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(UserActivity.this,AddCustomerActivity.class);
                startActivity(search);
            }
        });

        findViewById(R.id.btnFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCustomerFeedback();
            }
        });
    }

    private void fetchCustomerFeedback() {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        Log.d("demo", "token value is : " + token);
        final Request request = new Request.Builder().url(URLConstants.URL_CUSTOMER_FEEDBACK)
                .header("Authorization","Bearer " +token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "failure reading customer feedback : " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Gson gson = new Gson();
                    feedback = gson.fromJson(result.toString(),UserFeedback.class);
                    Log.d("demo", "User feedbacks coming out : " + feedback.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent feedbackIntent = new Intent(UserActivity.this,FeedbackActivity.class);
                            feedbackIntent.putExtra("feedbacks",feedback);
                            startActivity(feedbackIntent);
                        }
                    });
                }
            }
        });
    }

    private void fetchCustomersInQueue() {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        Log.d("demo", "token value is : " + token);
        final Request request = new Request.Builder().url(URLConstants.URL_INQUEUE_CUSTOMERS)
                .header("Authorization","Bearer " +token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Log.d("demo", "Inqueue customers: " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            customerResponse = gson.fromJson(result, CustomerResponse.class);
                            Log.d("demo", "inqueue after gson: " + customerResponse);
                            if(customerResponse == null){
                                Toast.makeText(getApplicationContext(),"There are no customers inqueue",Toast.LENGTH_SHORT).show();
                            }else{
                                Intent reservations = new Intent(UserActivity.this,InqueueActivity.class);
                                reservations.putExtra("userToken",token);
                                reservations.putExtra("tableInfo" , customerResponse);
                                startActivity(reservations);
                            }

                        }
                    });
                }
            }
        });
    }

    private void fetchReservedTables() {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        Log.d("demo", "token value is : " + token);
        final Request request = new Request.Builder().url(URLConstants.URL_RESERVED_TABLES)
                .header("Authorization","Bearer " +token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Log.d("demo", "onResponse: " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            tableResponse = gson.fromJson(result, TableResponse.class);
                            Log.d("demo", "run: " + tableResponse.toString());
                            Intent reservations = new Intent(UserActivity.this,ReservationActivity.class);
                            reservations.putExtra("userToken",token);
                            reservations.putExtra("tableInfo" , tableResponse);
                            startActivity(reservations);
                        }
                    });
                }
            }
        });
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("My_Pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        token = sharedPreferences.getString("userToken","");
        Log.d("demo", "loadSharedPreferences: " + token);
    }

    private void fetchTableInfo() {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        Log.d("demo", "token value is : " + token);
        final Request request = new Request.Builder().url(URLConstants.URL_TABLE_DATA)
                .header("Authorization","Bearer " +token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    Log.d("demo", "onResponse: " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            tableResponse = gson.fromJson(result, TableResponse.class);
                            Log.d("demo", "run: " + tableResponse.toString());
                            Intent viewTables = new Intent(UserActivity.this,ViewTablesActivity.class);
                            viewTables.putExtra("userToken",token);
                            viewTables.putExtra("tableInfo" , tableResponse);
                            startActivity(viewTables);
                        }
                    });
                }
            }
        });
    }
}
