package inclass4.group1.beacons;

public class Discounts {

//    private double _id;
    String discount,name,photo,price,region;

    public Discounts() {
    }

    @Override
    public String toString() {
        return "Discounts{" +
//                "_id=" + _id +
                ", discount='" + discount + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", price='" + price + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

/*
    public double get_id() {
        return _id;
    }

    public void set_id(double _id) {
        this._id = _id;
    }
*/

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
