package project1.group1.restaurantbooking.data;

import java.io.Serializable;
import java.util.ArrayList;

public class TableResponse implements Serializable {
    ArrayList<Table> result;

    public TableResponse() {
    }

    @Override
    public String toString() {
        return "TableResponse{" +
                "result=" + result +
                '}';
    }

    public ArrayList<Table> getResult() {
        return result;
    }

    public void setResult(ArrayList<Table> result) {
        this.result = result;
    }
}
