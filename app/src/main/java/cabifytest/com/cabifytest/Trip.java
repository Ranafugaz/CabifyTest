package cabifytest.com.cabifytest;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pradana on 28/08/15.
 * Used to save the information about each trip and show it on the list activity
 */
public class Trip{


    private int distance, duration;
    private double price, discount, total_price;
    String polyline;
    double lat1,lng1,lat2,lng2;
    int country;

    public Trip(int distance, int duration, double price, double discount, double total_price, String polyline, double lat1, double lng1, double lat2, double lng2,int country) {
        this.distance = distance;
        this.duration = duration;
        this.price = price;
        this.discount = discount;
        this.total_price = total_price;
        this.polyline = polyline;
        this.lat1 = lat1;
        this.lng1 = lng1;
        this.lat2 = lat2;
        this.lng2 = lng2;
        this.country = country;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal_price() {
        return total_price;
    }

    public String getPolyline() {
        return polyline;
    }

    public double getLat1() {
        return lat1;
    }

    public double getLng1() {
        return lng1;
    }

    public double getLat2() {
        return lat2;
    }

    public double getLng2() {
        return lng2;
    }

    public int getCountry() {
        return country;
    }


}
