package project1.group1.restaurantbooking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;

import project1.group1.restaurantbooking.data.Table;
import project1.group1.restaurantbooking.data.TableResponse;

class ReservationTableAdapter extends RecyclerView.Adapter<ReservationTableAdapter.ViewHolder> {

    TableResponse tableInfo;
    Table table;
    Context c;
    String authToken;
    private final OkHttpClient client = new OkHttpClient();

    public ReservationTableAdapter(Context applicationContext, TableResponse tableInfo, String token) {
        this.c = applicationContext;
        this.tableInfo = tableInfo;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public ReservationTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_reserve_tables, parent, false);
        ReservationTableAdapter.ViewHolder viewHolder = new ReservationTableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReservationTableAdapter.ViewHolder viewHolder, int position) {
        table = tableInfo.getResult().get(position);
        viewHolder.custName.setText(table.getUserName());
        viewHolder.tableNo.setText(table.getTableNumber());
        PrettyTime pt = new PrettyTime();
        viewHolder.checkInTime.setText(table.getBookingDate());
        viewHolder.status.setText(table.getStatus());
        viewHolder.phoneNumber.setText(table.getPhoneNumber());
        viewHolder.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("demo", "checkout click: " + viewHolder.tableNo.getText().toString());
                viewHolder.custName.setText("");
                viewHolder.phoneNumber.setText("");
                viewHolder.checkInTime.setText("");
                viewHolder.status.setText("READY");
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("tableNumber",viewHolder.tableNo.getText().toString());
                jsonObject.addProperty("phoneNumber",viewHolder.phoneNumber.getText().toString());
                RequestBody formBody = RequestBody.create(JSON,jsonObject.toString());
                final Request request = new Request.Builder().url(URLConstants.URL_CHECKOUT_CUSTOMER)
                        .header("Authorization","Bearer " +authToken)
                        .addHeader("Content-Type","application/json")
                        .post(formBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d("demo", "Error checking out customer: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String result = response.body().string();
                            Log.d("demo", "After checking out : " + result);
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return tableInfo.getResult().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView custName,tableNo,checkInTime,status,phoneNumber,noOfSeats;
        Table tableData;
        Button checkOut;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tableData = tableData;
            custName = itemView.findViewById(R.id.txtReserveCustomerName);
            tableNo = itemView.findViewById(R.id.txtReserveTableNumber);
            checkInTime = itemView.findViewById(R.id.txtReserveCheckInTime);
            status = itemView.findViewById(R.id.txtReserveTableStatus);
            checkOut = itemView.findViewById(R.id.btnReserveCheckOut);
            phoneNumber = itemView.findViewById(R.id.txtReservePhoneNumber);
        }
    }
}
