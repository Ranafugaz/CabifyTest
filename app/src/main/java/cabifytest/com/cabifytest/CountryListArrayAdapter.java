/**
 * Created by Pradana on 06/05/15.
 * Array Adapter used by the spinner
 */
package cabifytest.com.cabifytest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CountryListArrayAdapter extends ArrayAdapter<Country> {

    //List to show on the spinner
    private final List<Country> list;

    private final Activity context;

    static class ViewHolder {
        protected TextView country;
        protected ImageView flag;
    }

    public CountryListArrayAdapter(Activity context, List<Country> list) {
        super(context, R.layout.activity_countrycode_row, list);
        this.context = context;
        this.list = list;
    }

    @Override //don't override if you don't want the default spinner to be a two line view
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView);
    }

    //Method required for spinners
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return initView(position, convertView);
    }


    public View initView(int position, View convertView) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();

            //Inflate the correspondent layout
            view = inflator.inflate(R.layout.activity_countrycode_row, null);
            final ViewHolder viewHolder = new ViewHolder();

            //Get the Textview and the image of the layout
            viewHolder.country = (TextView) view.findViewById(R.id.country);
            viewHolder.flag = (ImageView) view.findViewById(R.id.flag);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        //Set text and image
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.country.setText(list.get(position).getCountry());
        holder.flag.setImageDrawable(list.get(position).getFlag());
        return view;
    }

}