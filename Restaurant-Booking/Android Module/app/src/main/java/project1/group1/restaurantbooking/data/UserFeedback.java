package project1.group1.restaurantbooking.data;

import java.io.Serializable;
import java.util.ArrayList;

public class UserFeedback implements Serializable {

    private ArrayList<Feedback> result;

    public UserFeedback() {
    }

    @Override
    public String toString() {
        return "UserFeedback{" +
                "result=" + result +
                '}';
    }

    public ArrayList<Feedback> getResult() {
        return result;
    }

    public void setResult(ArrayList<Feedback> result) {
        this.result = result;
    }


    public class Feedback implements Serializable {
        String phoneNumber, feedback;

        public Feedback() {
        }

        @Override
        public String toString() {
            return "Feedback{" +
                    "phoneNumber='" + phoneNumber + '\'' +
                    ", feedback='" + feedback + '\'' +
                    '}';
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }
}
