package cabifytest.com.cabifytest;

import android.graphics.drawable.Drawable;

/**
 * Created by Pradana on 28/08/15.
 * Used by the spinner.
 * Each country has a drawable flag and a name
 */

//Class for the spinner Adapter
public class Country {

    //Variable for the country name
    private String country;

    //Drawable for the flag
    private Drawable flag;

    public Country(String country, Drawable flag){
        this.country = country;
        this.flag = flag;
    }

    public String getCountry() {
        return country;
    }

    public Drawable getFlag() {
        return flag;
    }
}
