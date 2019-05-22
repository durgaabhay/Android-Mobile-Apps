package project1.group1.restaurantbooking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import project1.group1.restaurantbooking.data.CustomerResponse;

public class InqueueActivity extends AppCompatActivity {

    String token;

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    CustomerResponse customerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inqueue);

        if(getIntent().getExtras()!=null){
            token = getIntent().getExtras().getString("userToken");
            customerInfo = (CustomerResponse)getIntent().getExtras().getSerializable("tableInfo");
        }

        mRecyclerView = findViewById(R.id.inqueue_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(customerInfo != null){
            mAdapter = new InqueueTableAdapter(getApplicationContext(),customerInfo,token);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }
}
