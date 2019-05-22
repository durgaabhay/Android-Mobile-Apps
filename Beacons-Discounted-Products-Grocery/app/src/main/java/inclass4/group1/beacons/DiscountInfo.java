package inclass4.group1.beacons;

import java.util.ArrayList;

public class DiscountInfo {

    ArrayList<Discounts> data;

    @Override
    public String toString() {
        return "DiscountInfo{" +
                "data=" + data +
                '}';
    }

    public ArrayList<Discounts> getData() {
        return data;
    }

    public void setData(ArrayList<Discounts> data) {
        this.data = data;
    }
}
