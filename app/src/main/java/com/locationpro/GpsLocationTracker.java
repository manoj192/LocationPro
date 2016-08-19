package com.locationpro;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.ContextThemeWrapper;


import com.locationpro.Utils.PrefUtil;


import java.util.Timer;
import java.util.TimerTask;

public class GpsLocationTracker extends Service implements SensorEventListener,LocationListener {

    /**
     * context of calling class
     */
    private Context mContext;

    /**
     * flag for gps status
     */
    private boolean isGpsEnabled = false;

    /**
     * flag for network status
     */
    private boolean isNetworkEnabled = false;

    /**
     * flag for gps
     */
    private boolean canGetLocation = false;

    /**
     * location
     */
    private Location mLocation;

    /**
     * latitude
     */
    private double mLatitude;
    /**
     * longitude
     */
    private double mLongitude;
    /**
     * min distance change to get location update
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    /**
     * min time for location update 60000 = 1min
     */
    private static final long MIN_TIME_FOR_UPDATE = 60000;
    /**
     * location manager
     */
    private LocationManager mLocationManager;
    private Timer mTimer = new Timer();
    /**
     * @param mContext constructor of the class
     */
    public GpsLocationTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();

    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mTimer.schedule(new updateloc(), 4000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class updateloc extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mLatitude != 0.0 && mLongitude != 0.0) {
                PrefUtil.saveSession("PLAT", "" + getLatitude(), mContext);
                PrefUtil.saveSession("PLNG", "" + getLongitude(), mContext);
            }
        }
    }
    /**
     * @return location
     */
    public Location getLocation() {
        System.out.println("GPS Gettting Lat Lng getLocation");

        try {

            System.out.println("GPS Gettting Lat Lng try");
            mLocationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

			/* getting status of the gps */
            isGpsEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            System.out.println("GPS Gettting POP isGpsEnabled " + isGpsEnabled);

			/* getting status of network provider */
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {

				/* no location provider enabled */
            } else {

                this.canGetLocation = true;

				/* getting location from network provider */
                if (isNetworkEnabled) {
                    System.out.println("GPS Gettting Lat Lng isNetworkEnabled");

                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                    if (mLocationManager != null) {

                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (mLocation != null) {
                            System.out
                                    .println("GPS Gettting Lat Lng mLocation");
                            mLatitude = mLocation.getLatitude();

                            mLongitude = mLocation.getLongitude();
                        }
                    }
                    /* if gps is enabled then get location using gps */
                    if (isGpsEnabled) {
                        if (mLocation == null) {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                            if (mLocationManager != null) {
                                mLocation = mLocationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (mLocation != null) {
                                    System.out
                                            .println("GPS Gettting Lat Lng isGpsEnabled mLocation");
                                    mLatitude = mLocation.getLatitude();
                                    mLongitude = mLocation.getLongitude();
                                }

                            }
                        }

                    } else {
                        System.out
                                .println("GPS Gettting Lat Lng isGpsEnabled else mLocation");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("GPS Gettting Lat Lng ex");
            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * call this function to stop using gps in your application
     */
    public void stopUsingGps() {

        if (mLocationManager != null) {

            //mLocationManager.removeUpdates(GpsLocationTracker.this);

        }
    }

    /**
     * @return latitude
     * <p/>
     * function to get latitude
     */
    public double getLatitude() {

        if (mLocation != null) {

            mLatitude = mLocation.getLatitude();
            System.out.print("current location lat and long" +mLatitude);
        }
        return mLatitude;
    }

    /**
     * @return longitude function to get longitude
     */
    public double getLongitude() {

        if (mLocation != null) {

            mLongitude = mLocation.getLongitude();
            System.out.print("current location lat and long" +mLongitude);

        }

        return mLongitude;
    }

    /**
     * @return to check gps or wifi is enabled or not
     */
    public boolean canGetLocation() {

        return this.canGetLocation;
    }

    /**
     * function to prompt user to open settings to enable gps
     */
    public void showSettingsAlert() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(mContext, R.style.AppTheme));

        mAlertDialog.setTitle("Gps Disabled");

        mAlertDialog.setMessage("gps is not enabled . do you want to enable ?");

        mAlertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(mIntent);
            }
        });

        mAlertDialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        final AlertDialog mcreateDialog = mAlertDialog.create();
        mcreateDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        //if (PrefUtil.getData("TaxiStatus", mContext).equals("" + 3)) {
        Intent intent1 = new Intent("android.intent.action.MAIN");
        intent1.putExtra("lati", location.getLatitude());
        intent1.putExtra("longi", location.getLongitude());
        mContext.sendBroadcast(intent1);
        //}
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}