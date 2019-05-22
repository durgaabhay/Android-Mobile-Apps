package project1.group1.restaurantbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import project1.group1.restaurantbooking.data.Table;

public class PlaceOrderActivity extends AppCompatActivity {

    EditText customerName, customerEmail, phoneNumber;
    private final OkHttpClient client = new OkHttpClient();
    JSONObject tokenObject;
    String token,noOfSeats;
    ProgressDialog progressDialog;
    RadioGroup rg;
    RadioButton rb1,rb2,rb3,rb4,rb5,rb6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        loadSharedPreferences();
        customerName = findViewById(R.id.txtDispCustomerName);
        customerEmail = findViewById(R.id.txtCustomerEmail);
        phoneNumber = findViewById(R.id.txtPhoneNumber);
        rg = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);
        rb5 = findViewById(R.id.radioButton5);
        rb6 = findViewById(R.id.radioButton6);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().get("customerName") != null) {
                customerName.setText((String)getIntent().getExtras().get("customerName"));
            }
            if(getIntent().getExtras().get("customerEmail") != null) {
                customerEmail.setText((String)getIntent().getExtras().get("customerEmail"));
            }
            if(getIntent().getExtras().get("customerPhoneNumber") != null){
                phoneNumber.setText((String)getIntent().getExtras().get("customerPhoneNumber"));
            }
        }

        findViewById(R.id.btnOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(PlaceOrderActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Looking up for tables.. Please wait");
                progressDialog.show();
                placeOrder();
            }
        });
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("My_Pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        token = sharedPreferences.getString("userToken","");
    }

    private void placeOrder() {
        int id = rg.getCheckedRadioButtonId();
        RadioButton seats = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        noOfSeats = seats.getText().toString();
        Log.d("demo", "number of seats: " + noOfSeats);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userName",customerName.getText().toString());
        jsonObject.addProperty("email",customerEmail.getText().toString());
        jsonObject.addProperty("phoneNumber",phoneNumber.getText().toString());
        jsonObject.addProperty("noOfSeats",noOfSeats);
        RequestBody formBody = RequestBody.create(JSON,jsonObject.toString());

        final Request request = new Request.Builder().url(URLConstants.URL_PLACE_ORDER)
                .header("Authorization","Bearer " +token)
                .addHeader("Content-Type","application/json")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if(response.isSuccessful()){
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if(response.code() ==200){
                                Toast.makeText(getApplicationContext(),"Table assigned to customer",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Customer in queue.Message sent!",Toast.LENGTH_SHORT).show();
                            }
                            Intent backToUsersScreen = new Intent(PlaceOrderActivity.this,UserActivity.class);
                            startActivity(backToUsersScreen);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
