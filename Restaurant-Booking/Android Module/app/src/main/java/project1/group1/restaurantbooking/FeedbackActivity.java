package project1.group1.restaurantbooking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import project1.group1.restaurantbooking.data.UserFeedback;

public class FeedbackActivity extends AppCompatActivity {

    UserFeedback userFeedback;
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if(getIntent().getExtras()!= null){
            userFeedback = (UserFeedback)getIntent().getExtras().get("feedbacks");
            Log.d("demo", "inside feedabck : " + userFeedback.toString());
        }

        mRecyclerView = findViewById(R.id.feedback_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedbackTableAdapter(getApplicationContext(),userFeedback);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
