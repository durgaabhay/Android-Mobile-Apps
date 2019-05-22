package project1.group1.restaurantbooking.data;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerResponse implements Serializable {

    ArrayList<Customer> result;

    public CustomerResponse() {
    }

    @Override
    public String toString() {
        return "CustomerResponse{" +
                "result=" + result +
                '}';
    }

    public ArrayList<Customer> getResult() {
        return result;
    }

    public void setResult(ArrayList<Customer> result) {
        this.result = result;
    }
}
