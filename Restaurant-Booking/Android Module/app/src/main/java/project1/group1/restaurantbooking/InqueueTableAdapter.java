package project1.group1.restaurantbooking;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;

import project1.group1.restaurantbooking.data.CustomerResponse;
import project1.group1.restaurantbooking.data.Customer;

class InqueueTableAdapter extends RecyclerView.Adapter<InqueueTableAdapter.ViewHolder> {


    CustomerResponse customerInfo;
    Customer customer;
    Context c;
    String authToken;
    private final OkHttpClient client = new OkHttpClient();

    public InqueueTableAdapter(Context c, CustomerResponse customerInfo,String authToken) {
        this.c = c;
        this.customerInfo = customerInfo;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_inqueue, parent, false);
        InqueueTableAdapter.ViewHolder viewHolder = new InqueueTableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        customer = customerInfo.getResult().get(position);
        viewHolder.custName.setText(customer.getUserName());
        viewHolder.status.setText(customer.getTableStatus());
        viewHolder.phoneNumber.setText(customer.getPhoneNumber());
        viewHolder.noOfSeats.setText(customer.getNoOfSeats());
        viewHolder.assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("phoneNumber",viewHolder.phoneNumber.getText().toString());
                jsonObject.addProperty("noOfSeats", viewHolder.noOfSeats.getText().toString());
                jsonObject.addProperty("userName", viewHolder.custName.getText().toString());
                RequestBody formBody = RequestBody.create(JSON,jsonObject.toString());
                viewHolder.custName.setText("");
                viewHolder.status.setText("");
                viewHolder.phoneNumber.setText("");
                viewHolder.noOfSeats.setText("");
                final Request request = new Request.Builder().url(URLConstants.URL_ASSIGN_TABLE)
                        .header("Authorization","Bearer " +authToken)
                        .addHeader("Content-Type","application/json")
                        .post(formBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d("demo", "Failure assigning table : " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if(response.isSuccessful()){
                            Log.d("demo", "Table re-assigned successfully: " + response.body().string());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerInfo.getResult().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView custName,noOfSeats,checkInTime,status,phoneNumber;
        Customer tableData;
        Button assign;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tableData = tableData;
            custName = itemView.findViewById(R.id.txtQueueCustomerName);
            noOfSeats = itemView.findViewById(R.id.txtQueueNoOfSeats);
            status = itemView.findViewById(R.id.txtQueueTableStatus);
            assign = itemView.findViewById(R.id.btnQueueAssign);
            phoneNumber = itemView.findViewById(R.id.txtQueuePhoneNumber);
        }
    }
}
