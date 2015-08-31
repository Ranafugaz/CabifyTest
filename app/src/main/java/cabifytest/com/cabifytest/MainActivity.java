package cabifytest.com.cabifytest;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/** Solution by Javier Pradana
 * Main activity extends ListActivity to control the list of the activity
 * Implements methods for click and select object on lists (country and trips)
 *
 */
public class MainActivity extends ListActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, OnMapReadyCallback{

    //Prices per KM
    final double priceKM_ES=1.5;
    final double priceKM_MX=14;
    final double priceKM_PE=2.5;

    //Prices per min
    final double priceMIN_ES=0.5;
    final double priceMIN_MX=2.5;
    final double priceMIN_PE=0.5;

    //Prices per KM array
    double pricesKM[]= {priceKM_ES,priceKM_MX,priceKM_PE};

    //Prices per Min array
    double pricesMIN[]= {priceMIN_ES,priceMIN_MX,priceMIN_PE};

    //List for the Adapter of object Trip to show on the activity
    List<Trip> lista= new ArrayList<Trip>();

    //Variable for the map
    GoogleMap mMap;

    //Discount
    final double discount = 0.1;

    //Country names for the spinner
    String[] country = {"Spain","Mexico","Peru"};

    //Polyline decoding precision. Use 1e6 for osm and 1e5 for google maps
    final double precision = 1e6;

    //File name for the Trip Json in the assets directory
    final String file_name = "journeys.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set-up spinner
        spinnersetup();

        //Set-up List
        listsetup();

        //Set-up MAp
        mapsetup();

    }


    /*
     * Set-up the spinner with the 3 countries
     */
    private void spinnersetup() {

        //Spinner from layout
        Spinner spinner = (Spinner) findViewById(R.id.country_spinner);

        //Create and initate List
        ArrayList<Country> countryList = new ArrayList<Country>();

        //Add items to show in the List. Better implementation with case for the flags
        countryList.add(new Country(country[0],getResources().getDrawable(R.drawable.spain)));
        countryList.add(new Country(country[1],getResources().getDrawable(R.drawable.mexico)));
        countryList.add(new Country(country[2],getResources().getDrawable(R.drawable.peru)));

        //Create ArrayAdapter for the spinner using Country as object
        ArrayAdapter<Country> adapter = new CountryListArrayAdapter(MainActivity.this, countryList);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Response to select item
        spinner.setOnItemSelectedListener(this);
    }



    /*Called by the spinner when item is selected
     *
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //According to the item selected we read the JSON and download the info
        new setuplist(position).execute();

    }

    private void listsetup() {

        //On item click a google map will be shown
        getListView().setOnItemClickListener(this);

    }


    /* Called when selected a route from the listView
     * Update the Google map with the initial and end marker
     * Decode the polyline and draw it on the map
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Check if lista has been initialized
        if (lista!=null) {

            //Clear map
            mMap.clear();

            //Get coordinates
            double lat1=lista.get(position).getLat1();
            double lat2=lista.get(position).getLat2();
            double lng1=lista.get(position).getLng1();
            double lng2=lista.get(position).getLng2();


            //Add market with the start point
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat1, lng1))
                    .title("Start"));

            //Add marker with the end point
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat2, lng2))
                    .title("End"));

            //Decode polyline
            String line = lista.get(position).getPolyline();

            //Get the list of points on the polyline
            List<Point> decodedPath = new PolylineDecoder().decode(line,precision);

            //Transform the list of points to LatLng list
            List<LatLng> polyline = new ArrayList<>();
            for(Point item : decodedPath){
                polyline.add(new LatLng(item.x,item.y));
            }

            //Add the points to the map
            mMap.addPolyline(new PolylineOptions().addAll(polyline).color(Color.parseColor("#0D5875")));

            //Create a bound to show the route on the map and move the camera
            LatLngBounds bounds = LatLngBounds.builder().include(new LatLng(lat1, lng1)).include(new LatLng(lat2, lng2)).build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing to do
    }


    //Function to connect to the Google map with the Api
    private void mapsetup() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //When the map is ready initliaze the mMap
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }


    /* Decode the JSON for different countries
     * Retrieve the information with osm about the route for each trip
     * Save all the information in a List and send it to the adapter
     * @param position
     *
     */
    public class setuplist extends AsyncTask<Void,Void,Void>
    {


        //Position of the country selected in the spinner
        private int position=0;

        //Boolean to check erros
        boolean json = false;
        boolean io=false;

        //Constructor with the country selected
        public setuplist(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            //Clear the list
            lista.clear();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //Create Array Adapter
            ArrayAdapter<Trip> adapter = new ListArrayAdapter(MainActivity.this, lista);

            //Set Adapter to the list
            setListAdapter(adapter);

            //Order the list from minor duration time
            Collections.sort(lista, new Comparator<Trip>() {
                @Override
                public int compare(Trip left, Trip Right) {
                    return left.getDuration() - (Right.getDuration());
                }
            });

            //In case no routs are found we show a AlertDialog
            if (json) new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No routes found")
                    .setCancelable(true)
                    .show();


            //In case of server error are found we show a AlertDialog
            if (io) new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Server error")
                    .setCancelable(true)
                    .show();

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //FIRST the file with the Journeys is Read
                //Define an InputStream
                InputStream is = getAssets().open(file_name);

                //Read the file and save as string
                String contentAsString = readIt(is, 1);

                //Prepare to uncode JSON
                JSONObject jObject = new JSONObject(contentAsString);
                JSONArray aJsonRoute = jObject.getJSONArray("journeys");

                //Parse JSON
                for (int i=0;i<aJsonRoute.length();i++) {
                    String region = aJsonRoute.getJSONObject(i).getString("region");

                    //If the region does not correspond to the country selected continue
                    if (region.equals("ES")&&position!=0)continue;
                    if (region.equals("MX")&&position!=1)continue;
                    if (region.equals("PE")&&position!=2)continue;

                    //Read the coordinates
                    String lat1 = aJsonRoute.getJSONObject(i).getJSONArray("start_loc").getString(0);
                    String lng1 = aJsonRoute.getJSONObject(i).getJSONArray("start_loc").getString(1);
                    String lat2 = aJsonRoute.getJSONObject(i).getJSONArray("end_loc").getString(0);
                    String lng2 = aJsonRoute.getJSONObject(i).getJSONArray("end_loc").getString(1);

                    //SECOND connect the server and retrieve the route information
                    //Prepare link for the connection and encode
                    String link = "http://router.project-osrm.org/viaroute?loc="+lat1+","+lng1+"&loc="+lat2+","+lng2+"&instructions=false&alt=false";
                    URL url = new URL(link);

                    //Open the connection
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    //Set connection parameters
                    conn.setReadTimeout(20000 /* milliseconds */);
                    conn.setConnectTimeout(25000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();

                    //Get the response
                    is = conn.getInputStream();

                    // Convert the InputStream into a string
                    String contentAsString2 = readIt(is, 1);

                    //Prepare to uncode JSON
                    JSONObject route = new JSONObject(contentAsString2);
                    JSONObject routeInfo = route.getJSONObject("route_summary");

                    //Retrieve duration, distance and polyline
                    int duration = routeInfo.getInt("total_time");
                    int distance = routeInfo.getInt("total_distance");
                    String polyLine = route.getString("route_geometry");

                    //Calculation of rounded prices
                    double price_km= Math.round(pricesKM[position] * distance/1000 *10)/10;
                    double price_min = Math.round(pricesMIN[position] * duration / 60*10)/10;

                    //Select the highest price
                    double price;
                    if (price_km>price_min)  price= price_km;
                    else price=price_min;

                    //Calculation of rounded total price with discount
                    double total_price= Math.round((1-discount)*price*10)/10;

                    //Adding to the list of the new object Trip
                    lista.add(new Trip(
                            distance,
                            duration,
                            price,
                            discount,
                            total_price,
                            polyLine,
                            Double.parseDouble(lat1),
                            Double.parseDouble(lng1),
                            Double.parseDouble(lat2),
                            Double.parseDouble(lng2),
                            position
                    ));

                    //Disconnect the connection
                    conn.disconnect();

                }
            } catch (JSONException e) {
                //Set-up of error trigger
                json=true;
                e.printStackTrace();

            } catch (IOException e) {
                //Set-up of error triger
                io=true;
                e.printStackTrace();
            }

            return null;
        }

        /* Read the file and return it as String
         * @param stream
         * @param len
         */
        public String readIt(InputStream stream, int len) throws IOException {
            //Create reader
            Reader reader = null;

            //Definition as InputStream with the origin and text type UTF8
            reader = new InputStreamReader(stream, "UTF-8");

            //Char of lenght 1
            char[] buffer = new char[len];

            //String Buffer
            StringBuffer sreader = new StringBuffer("");

            //Add the String buffer characteres 1 by 1
            while (reader.read(buffer)!=-1){
                sreader.append(buffer);
            }
            return new String(sreader);
        }
    }



}
