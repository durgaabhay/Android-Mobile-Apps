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

import project1.group1.restaurantbooking.data.Table;
import project1.group1.restaurantbooking.data.UserFeedback;

public class FeedbackTableAdapter extends RecyclerView.Adapter<FeedbackTableAdapter.ViewHolder> {

    Context c;
    UserFeedback userFeedback;
    UserFeedback.Feedback feedback;

    public FeedbackTableAdapter(Context c, UserFeedback userFeedback) {
        this.userFeedback = userFeedback;
        this.c = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_feedback_view, parent, false);
        FeedbackTableAdapter.ViewHolder viewHolder = new FeedbackTableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        feedback = userFeedback.getResult().get(position);
        Log.d("demo", "onBindViewHolder: " + feedback.getPhoneNumber());
        viewHolder.phoneNumber.setText(feedback.getPhoneNumber());
        viewHolder.feedback.setText(feedback.getFeedback());
    }

    @Override
    public int getItemCount() {
        return userFeedback.getResult().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView phoneNumber,feedback;

        public ViewHolder(final View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.txtFeedbackPhoneNumber);
            feedback = itemView.findViewById(R.id.txtCustomerFeedback);
        }
    }
}
