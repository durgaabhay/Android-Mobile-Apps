package project1.group1.restaurantbooking;

public class URLConstants {

    public static final String api="http://10.216.28.15:3000/";
    public static final String URL_LOGIN = api+"admin/login";
    public static final String URL_RESERVED_TABLES = api+"reserve/reservationTableStatus" ;
    public static final String URL_INQUEUE_CUSTOMERS = api+"reserve/inqueue";
    public static final String URL_TABLE_DATA = api+"reserve/alacarteTableStatus";
    public static final String URL_PLACE_ORDER = api+"customer/placeOrder";
    public static final String URL_CHECKOUT_CUSTOMER = api+"customer/checkOut";
    public static final String URL_ASSIGN_TABLE = api+"customer/placeOrder";
    public static final String URL_CUSTOMER_FEEDBACK = api+"customer/feedback";
    public static final String URL_CUSTOMER_SEARCH = api+"customer/searchCustomer";
}
