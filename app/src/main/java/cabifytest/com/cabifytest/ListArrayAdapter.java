package cabifytest.com.cabifytest;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pradana on 06/05/15.
 * Adapter used by the List
 */
public class ListArrayAdapter extends ArrayAdapter<Trip>  {

    private final List<Trip> list;
    private final Activity context;

    //Currencies to show next to the prices
    String[] currencies = {"€","$","PER"};

    static class ViewHolder {
        protected TextView duration;
        protected TextView distance;
        protected TextView price;
        protected TextView discount;
        protected TextView total_price;
    }

    public ListArrayAdapter(Activity context, List<Trip> list) {
        super(context, R.layout.activity_journey_row, list);
        this.context = context;
        this.list = list;
    }

    @Override //don't override if you don't want the default spinner to be a two line view
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return initView(position, convertView);
    }


    public View initView(int position, View convertView) {
        View view = null;

        //Identify subviews on the layout to set-up
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.activity_journey_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.duration = (TextView) view.findViewById(R.id.duration);
            viewHolder.distance = (TextView) view.findViewById(R.id.distance);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.discount = (TextView) view.findViewById(R.id.discount);
            viewHolder.total_price = (TextView) view.findViewById(R.id.total_price);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        //Set the values on the views
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.duration.setText(Integer.toString(Math.round(list.get(position).getDuration()/60))+" min");
        holder.distance.setText(Integer.toString(Math.round(list.get(position).getDistance()/1000))+ " km");

        //Set prices and discount according to country
        int country = list.get(position).getCountry();
        holder.price.setText(Double.toString(list.get(position).getPrice())+currencies[country]);
        holder.discount.setText("(-"+Double.toString(list.get(position).getDiscount())+"%)");
        holder.total_price.setText(Double.toString(list.get(position).getTotal_price())+currencies[country]);
        return view;
    }

}