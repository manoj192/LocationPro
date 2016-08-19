package com.locationpro;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locationpro.Utils.HttpUtil;
import com.locationpro.Utils.PrefUtil;
import com.locationpro.Utils.Utils_class;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,ResultCallback<LocationSettingsResult> {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    static GoogleMap mGoogleMap;
        private LinearLayout mTick;
        private GpsLocationTracker mGpsLocationTracker;
        private double P_latitude;
        private double P_longitude;
        private double P_latitude1;
        private double P_longitude1;
        private AddressResultReceiver mResultReceiver;
        PendingIntent pendingIntent;
        MediaPlayer thePlayer;
        private FrameLayout mMap_lay;
        private LinearLayout mParentlay;
        private boolean gps_enabled;
        private boolean mAddressRequested;
       private String mAddressOutput;
       private GoogleApiClient mGoogleApiClient;
       private Marker hamburg;
       LatLng HAMBURG;



        private static ArrayList<String> resultList1;
        private ArrayList<String> resultList;

        private static final String LOG_TAG = "ExampleApp";

        private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
        private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
        private static final String OUT_JSON = "/json";


        static final LatLng TutorialsPoint = new LatLng(11, 70);
        BitmapDescriptor icon;
        private float Zoom;
        String updated_address;
        private CameraPosition mPrePosition;
        private static final int API_CALLING = 888;
        AutoCompleteTextView autoCompView;
        boolean setAddress;
        MapView mapView;
        SupportMapFragment fragment;
        private Button cancel;
        private AudioManager am;
        private Button confirm_location;
        private BroadcastReceiver br;
        private String mPos = "";
      private Location mLastLocation;
    protected static final String TAG = "location-settings";
    private boolean mSatisfied = false;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
     private AlarmManager mAlaram;
    JSONObject location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_based_rules);
        PrefUtil.getSession("PLAT", getApplicationContext());
        PrefUtil.getSession("PLNG", getApplicationContext());
        mMap_lay=(FrameLayout)findViewById(R.id.map_lay);
        mParentlay=(LinearLayout)findViewById(R.id.mParentLay);
        cancel=(Button)findViewById(R.id.cancel);
        autoCompView=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        confirm_location=(Button)findViewById(R.id.confirm_location);
//        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        mResultReceiver = new AddressResultReceiver(new Handler());
            new Search_Result1().execute("");
        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        mAddressRequested = false;
        mAddressOutput = "";
        gps_enabled = lm
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        buildGoogleApiClient();
        createLocationRequest();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thePlayer.stop();
            }
        });

        confirm_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (mPos == null || mPos == "") {
                    if (autoCompView.getText().toString().length() > 0) {
                        if (P_latitude1 > 0 && P_longitude1 > 0) {
                            onLocationChanged2();

                        }

                    }


                }
            }
        });
        mMap_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils_class.hideKeypad(MainActivity.this, mParentlay);
            }
        });
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String str = (String) parent.getItemAtPosition(position);
                autoCompView.setText(str);

                if (str.length() > 0) {
                    new Search_Result().execute(str);
                }


            }
        });
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMap();
            }
        }, 500);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }
    private void loadMap() {

            if(mGoogleMap==null){
                mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                MapsInitializer.initialize(MainActivity.this);
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                mGpsLocationTracker = new GpsLocationTracker(MainActivity.this);
                mGpsLocationTracker.getLocation();
                mGpsLocationTracker.canGetLocation();
            }


        if (Utils_class.MapLatitude != 0.0) {
            try {
                Utils_class.mAddress = new Address_s(MainActivity.this, new LatLng(
                        Utils_class.MapLatitude, Utils_class.MapLongitude)).execute().get();
                autoCompView.setText(Utils_class.mAddress);
                LatLng MaplatLng = new LatLng(Utils_class.MapLatitude, Utils_class.MapLongitude);
                Utils_class.Lat = Utils_class.MapLatitude;
                Utils_class.Lng = Utils_class.MapLongitude;
                mGoogleMap.addMarker(new MarkerOptions().position(MaplatLng).title(Utils_class.mAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MaplatLng, 15f));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (Utils_class.MapLat != 0.0 && Utils_class.MapLatitude == 0.0) {
            try {
                Utils_class.mAddress = new Address_s(MainActivity.this, new LatLng(
                        Utils_class.MapLat, Utils_class.MapLng)).execute().get();
                autoCompView.setText(Utils_class.mAddress);
                LatLng MlatLng = new LatLng(Utils_class.MapLat, Utils_class.MapLng);
                mGoogleMap.addMarker(new MarkerOptions().position(MlatLng).title(Utils_class.mAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MlatLng, 15f));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            if (P_latitude == 0.0) {
                if (mGpsLocationTracker.canGetLocation()) {
                    P_latitude = mGpsLocationTracker.getLatitude();
                    P_longitude = mGpsLocationTracker.getLongitude();
                    Utils_class.Lat = P_latitude;
                    Utils_class.Lng = P_longitude;
                    LatLng latLng = new LatLng(P_latitude, P_longitude);
                    try {
                        Utils_class.mAddress = new Address_s(MainActivity.this, new LatLng(
                                P_latitude, P_longitude)).execute().get();
                        autoCompView.setText(Utils_class.mAddress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(Utils_class.mAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
                    System.out.println("P_latitude" + P_latitude);
                    System.out.println("P_longitude" + P_longitude);
                } else {
                    mGoogleMap.setMyLocationEnabled(true);
                    mGpsLocationTracker.getLocation();
                    mGpsLocationTracker.canGetLocation();
                    P_latitude = mGpsLocationTracker.getLatitude();
                    P_longitude = mGpsLocationTracker.getLongitude();
                    LatLng latLng = new LatLng(P_latitude, P_longitude);
                    Utils_class.Lat = P_latitude;
                    Utils_class.Lng = P_longitude;
                    try {
                        Utils_class.mAddress = new Address_s(MainActivity.this, new LatLng(
                                P_latitude, P_longitude)).execute().get();
                        autoCompView.setText(Utils_class.mAddress);
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(Utils_class.mAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
    }
    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + AppConstants.API_KEY);
            sb.append("&components=country:in");

            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            System.out.println("json obj response url" + url);

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            System.out.println("json obj response" + jsonObj);


            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            System.out.println("geo coder api call" + predsJsonArray);
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString(
                        "description"));

                resultList.add(predsJsonArray.getJSONObject(i).getString(
                        "description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "NO geocode available", Toast.LENGTH_LONG).show();
                return;
            }
            if (mAddressRequested) {
                startIntentService();
            }
        }

    }

    private void startIntentService() {
        Intent intent = new Intent(this, Address_s.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        intent.putExtra("getlocation", "0");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                mSatisfied = true;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG,
                        "Location settings are not satisfied. Show the user a dialog to"
                                + "upgrade location settings ");

                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG,
                        "Location settings are inadequate, and cannot be fixed here. Dialog "
                                + "not created.");
                mSatisfied = false;
                break;
        }

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
            implements Filterable {


        public GooglePlacesAutocompleteAdapter(Context context,
                                               int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        //new Search_Result1().execute(constraint.toString());
                        try{
                            resultList = autocomplete(constraint.toString());
                            System.out.print("result list arraylist" +  resultList);

                            // Assign the data to the FilterResults
                            filterResults.values = resultList;
                            filterResults.count = resultList.size();

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

            };

            return filter;
        }
    }
    private class Search_Result extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                    + params[0].trim() + "+CA&sensor=true_or_false";
            try {

                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(HttpUtil.apiGETResponse(url
                        .replace(" ", ",%20")));
                System.out.println("response from server" + jsonObj);
                JSONArray predsJsonArray = jsonObj.getJSONArray("results");

                resultList1 = new ArrayList<String>(predsJsonArray.length());
                System.out.println("geo coder api call" + predsJsonArray);
                for (int i = 0; i < predsJsonArray.length(); i++) {

                    JSONObject geomet = predsJsonArray.getJSONObject(i);
                    JSONObject geomet1 = geomet.getJSONObject("geometry");
                     location = geomet1.getJSONObject("location");
                    if (location.getDouble("lat") > 0.0) {
                        P_latitude1 = location.getDouble("lat");
                    } else {
                        P_latitude1 = P_latitude;
                    }
                    if (location.getDouble("lng") > 0.0) {
                        P_longitude1 = location.getDouble("lng");
                    } else {
                        P_longitude1 = P_longitude;
                    }

                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);
            }

            return true;
        }

        @Override
        @SuppressLint("InlinedApi")
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    P_latitude1, P_longitude1), 17f));
             HAMBURG = new LatLng(P_latitude1, P_longitude1);
            hamburg = mGoogleMap.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("").icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.pin2)));
            System.out.print("latitude of seleted text" + P_latitude1);
            System.out.print("longitude of selected text" +P_longitude1);



        }
    }
    private class Search_Result1 extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                new GooglePlacesAutocompleteAdapter(getApplicationContext(), R.layout.list_item);
            } catch (Exception e) {

            }

            return true;
        }

        @Override
        @SuppressLint("InlinedApi")
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try {
                autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getApplicationContext(),
                        R.layout.list_item));
            }catch (Exception e){

            }


        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            System.out.println("address : " + mAddressOutput);
            if (resultCode != Constants.SUCCESS_RESULT) {
                //UIUtils.showSnackBar(mDateTime, "Couldn't fetch the current location");
                finish();
            }
            mAddressRequested = false;
        }
    }
    private void onLocationChanged2() {
        System.out.println("current latitude"+P_latitude);
        System.out.println("entered latitude"+P_latitude1);
        System.out.println("current longitude"+P_longitude);
        System.out.println("entered longitude"+P_longitude1);
        String currlat=String.format("%.2f", P_latitude);
        System.out.print("CURRENT LATI"+currlat);
        String enterlat=String.format("%.2f", P_latitude1);
        String currlon=String.format("%.2f", P_longitude);
        String enteredlon=String.format("%.2f", P_longitude1);

        if (currlat.equals(enterlat) && currlon.equals(enteredlon)) {
        Toast.makeText(getApplicationContext(), "location reacH", Toast.LENGTH_LONG).show();
        start();
    } else {
        Toast.makeText(getApplicationContext(), "location DENIED", Toast.LENGTH_LONG).show();
    }

    }
 /*  public void    starttrip(){

       ArrayList<String> mlocationlat1 = new ArrayList<String>();
       ArrayList<String> mlocationlong1 = new ArrayList<String>();
       double lat=0.0;
       double lon=0.0;
       LatLng HAMBURG = new LatLng(lat,lon);
     *//*  hamburg = mGoogleMap.addMarker(new MarkerOptions().position(HAMBURG)
               .title("").icon(BitmapDescriptorFactory
                       .fromResource(R.drawable.pin)));*//*
       if (autoCompView.getText().toString().length() > 0) {

           if (P_latitude1 > 0 && P_longitude1 > 0) {

               PrefUtil.saveSession("latitude", "" + P_latitude1, getApplicationContext());
               PrefUtil.saveSession("longitude", "" + P_longitude1, getApplicationContext());
               if (PrefUtil.loadArray(getApplicationContext(), "locationlat").size() > 0) {
                   mlocationlat1= PrefUtil.loadArray(getApplicationContext(), "locationlat");
               }
               mlocationlat1.add("" + P_latitude1);
               PrefUtil.saveArray(getApplicationContext(), mlocationlat1, "locationlat");

               if (PrefUtil.loadArray(getApplicationContext(), "locationlong").size() > 0) {
                   mlocationlong1 = PrefUtil.loadArray(getApplicationContext(), "locationlong");
               }
               mlocationlong1.add("" + P_longitude1);
               PrefUtil.saveArray(getApplicationContext(), mlocationlong1, "locationlong");


                   Toast.makeText(getApplicationContext(), "location find",Toast.LENGTH_LONG).show();


           }
       }
   }*/
    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
         thePlayer = MediaPlayer.create(getApplicationContext(),R.raw.song);
        try {
            thePlayer.setVolume((float)(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0),
                    (float) (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        thePlayer.start();


    }
   /* public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // For our recurring task, we'll just display a message
            Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        }
    }*/

   }



