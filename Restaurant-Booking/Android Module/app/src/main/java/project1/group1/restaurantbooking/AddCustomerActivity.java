package project1.group1.restaurantbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import project1.group1.restaurantbooking.data.Customer;

public class AddCustomerActivity extends AppCompatActivity {

    EditText customerContact;
    String authToken;
    private final OkHttpClient client = new OkHttpClient();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        customerContact = findViewById(R.id.txtSearchNumber);
        loadSharedPreferences();

        findViewById(R.id.btnCustomerSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customerContact == null || customerContact.length()<12){
                    Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog = new ProgressDialog(AddCustomerActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("Finding customer information");
                    progressDialog.show();
                    searchCustomer(customerContact.getText().toString());
                }
            }
        });
    }
    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("My_Pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        authToken = sharedPreferences.getString("userToken","");
        Log.d("demo", "loadSharedPreferences: " + authToken);
    }

    private void searchCustomer(String mobileNumber) {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phoneNumber",mobileNumber);
        RequestBody formBody = RequestBody.create(JSON,jsonObject.toString());
        Log.d("demo", "Place order: " + formBody.toString());
        final Request request = new Request.Builder().url(URLConstants.URL_CUSTOMER_SEARCH)
                .header("Authorization","Bearer " +authToken)//replace the ip here
                .addHeader("Content-Type","application/json")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("demo", "Failure reading customer: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d("demo", "onResponse: " + response.code());
                    final String customer = response.body().string();
                    Log.d("demo", "customer read : " + customer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(customer.length()>0) {
                                Gson gson = new Gson();
                                Customer[] customerInfo = gson.fromJson(customer,Customer[].class);
                                Log.d("demo", "Customer Info: " + customerInfo.toString());
                                Intent makeOrder = new Intent(AddCustomerActivity.this,PlaceOrderActivity.class);
                                if(customerInfo.length == 0){
                                    makeOrder.putExtra("customerName","");
                                    makeOrder.putExtra("customerEmail","");
                                    makeOrder.putExtra("customerPhoneNumber",customerContact.getText().toString());
                                }else{
                                    makeOrder.putExtra("customerName",customerInfo[0].getUserName());
                                    makeOrder.putExtra("customerEmail",customerInfo[0].getEmail());
                                    makeOrder.putExtra("customerPhoneNumber",customerInfo[0].getPhoneNumber());
                                }
                                startActivity(makeOrder);
                            }
                        }
                    });
                }
            }
        });
    }
}
