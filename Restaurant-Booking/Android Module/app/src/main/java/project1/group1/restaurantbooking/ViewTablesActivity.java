package project1.group1.restaurantbooking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import project1.group1.restaurantbooking.data.TableResponse;

public class ViewTablesActivity extends AppCompatActivity {

    String token;

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    TableResponse tableInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tables);

        if(getIntent().getExtras()!=null){
            token = getIntent().getExtras().getString("userToken");
            tableInfo = (TableResponse)getIntent().getExtras().getSerializable("tableInfo");
        }

        mRecyclerView = findViewById(R.id.alacarteView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AlacarteTableAdapter(getApplicationContext(),tableInfo,token);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
