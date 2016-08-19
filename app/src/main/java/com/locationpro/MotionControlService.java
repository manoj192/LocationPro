package com.locationpro;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;


import com.locationpro.Utils.HttpUtil;
import com.locationpro.Utils.PrefUtil;
import com.locationpro.Utils.UIUtils;
import com.locationpro.Utils.Utils_class;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MotionControlService extends Service implements SensorEventListener, LocationListener {

    private static final String TAG = "MotionControlService";

    public static final String BROADCAST_ACTION = "com.politephone.android";

    private ArrayList<String> mTypeArray = new ArrayList<String>();

    private float mGZ = 0;//gravity acceleration along the z axis

    private int mEventCountSinceGZChanged = 0;

    private static final int MAX_COUNT_GZ_CHANGE = 10;

    SensorManager mSensorManager;

    boolean mStarted;

    AudioManager am;

    private Task retryTask;

    Timer myTimer, myTimer1;

    private boolean timerRunning = false;

    private long RETRY_TIME = 60000;

    private long START_TIME = 1000;

    private Looper mServiceLooper;

    Handler handler;

    private ServiceHandler mServiceHandler;
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

    StringBuilder sb;

    final static private long SEVEn_DAYS = 604800000;

    PendingIntent pi;

    BroadcastReceiver br;

    AlarmManager mAlaram;
    Intent intent;

    private Boolean mPriorityCheck;


    @Override
    public void onLocationChanged(Location location) {

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        PrefUtil.saveSession("latitude_1","" +location.getLatitude(),getApplicationContext());
        PrefUtil.saveSession("longitude_1",""+location.getLongitude(),getApplicationContext());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        intent = new Intent(BROADCAST_ACTION);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new Task(), START_TIME, RETRY_TIME);
        this.mContext = getApplicationContext();
        timerRunning = true;
        getLocation();
        setup();
        if (PrefUtil.getSession("placeCategory", getApplicationContext()).toString().length() > 0) {
            try {
                if (Integer.parseInt(PrefUtil.getSession("frequencyint", getApplicationContext())) >= 15) {
                    myTimer.scheduleAtFixedRate(new Task1(), 0, Integer.parseInt(PrefUtil.getSession("frequency", getApplicationContext())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new Place_Category().execute();
        }


    }


    public class Task extends TimerTask {
        @Override
        public void run() {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.MINUTE);
            int hours = c.get(Calendar.HOUR);
            String mCurrentTime = "";
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (seconds < 10) {
                if (hours == 0) {
                    mCurrentTime = " " + "12" + "h0" + seconds;
                } else {
                    mCurrentTime = " " + hours + "h0" + seconds;
                }

            } else {
                if (hours == 0) {
                    mCurrentTime = " " + "12" + "h" + seconds;
                } else {
                    mCurrentTime = " " + hours + "h" + seconds;

                }

            }
            //Change Sound profile based on dates
            if (PrefUtil.getSession("timerule", getApplicationContext()).equals("true")) {
                    for (int i1 = 0; i1 < PrefUtil.loadArray(getApplicationContext(), "Fromdatearray").size(); i1++) {
                        try {
                            if (PrefUtil.loadArray(getApplicationContext(), "Fromdatearray").get(i1).contains(date)) {
                                if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i1).contains(mCurrentTime)) {

                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i1).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i1).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i1).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {

                                }
                            }else{


                                        Calendar calendar = Calendar.getInstance();
                                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                                        mPriorityCheck = false;

                                        switch (day) {

                                            case Calendar.SUNDAY:

                                                for (int i = 0; i <= PrefUtil.loadArray(getApplicationContext(), "sundayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "sundayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            System.out.println("selected timeee 2" + PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i));
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }

                                                    }
                                                }


                                            case Calendar.MONDAY:

                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "mondayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "mondayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }


                                                    }
                                                }


                                            case Calendar.TUESDAY:

                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "tuesdayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "tuesdayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }

                                                    }
                                                }


                                            case Calendar.WEDNESDAY:

                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "wednesdayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "wednesdayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }

                                                    }
                                                }


                                            case Calendar.THURSDAY:
                                                System.out.println("selected timeee" + PrefUtil.loadArray(getApplicationContext(), "timearray") + "currenttime" + mCurrentTime);
                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "thrusdayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "thrusdayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }

                                                    }
                                                }


                                            case Calendar.FRIDAY:

                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "fridayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "fridayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }


                                                    }
                                                }


                                            case Calendar.SATURDAY:


                                                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "saturdayarray").size(); i++) {
                                                    if (PrefUtil.loadArray(getApplicationContext(), "saturdayarray").size() > 0) {

                                                        if (PrefUtil.loadArray(getApplicationContext(), "timearray").get(i).contains(mCurrentTime)) {
                                                            mPriorityCheck = true;
                                                            if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                                PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                                PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                                            } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                                PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                                            }
                                                        } else {
                                                            mPriorityCheck = false;
                                                        }

                                                    }
                                                }


                                        }
                                    }


                        } catch (Exception e) {
                            System.out.println("Exception in array" + e.toString());
                        }

                    }




                //Change Sound profile based on Time and repeating days

            }

            //Change sound profile based on location
            if (PrefUtil.getSession("locationrule", getApplicationContext()).equals("true")) {

                if (PrefUtil.loadArray(getApplicationContext(), "locationlat").size() > 0 && PrefUtil.loadArray(getApplicationContext(), "locationlong").size() > 0) {
                    try {
                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "locationlat").size(); i++) {
                            Double latitude = getLatitude();
                            System.out.println("Time Array in " + latitude);
                            Double longitude = getLongitude();
                            String mFromlatitude = UIUtils.ConvertDouble(latitude);
                            String mTolatitude = UIUtils.ConvertDouble(Double.parseDouble(PrefUtil.loadArray(getApplicationContext(), "locationlat").get(i)));
                            String mFromlongitude = UIUtils.ConvertDouble(longitude);
                            String mTolongitude = UIUtils.ConvertDouble(Double.parseDouble(PrefUtil.loadArray(getApplicationContext(), "locationlong").get(i)));
                            if (mFromlatitude.equals(mTolatitude) && mFromlongitude.equals(mTolongitude)) {
                                //Assign priority to location rules
                                mPriorityCheck = true;

                                if (PrefUtil.loadArray(getApplicationContext(), "profilearrayloc").get(i).equals("loud")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    PrefUtil.saveArrayinpos(getApplicationContext(), PrefUtil.loadArray(getApplicationContext(), "profilearrayloc"), "profilearrayloc", i, "loud");
                                } else if (PrefUtil.loadArray(getApplicationContext(), "profilearrayloc").get(i).equals("silent")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    PrefUtil.saveSession("profile", "silent", getApplicationContext());
                                    PrefUtil.saveArrayinpos(getApplicationContext(), PrefUtil.loadArray(getApplicationContext(), "profilearrayloc"), "profilearrayloc", i, "silent");

                                } else if (PrefUtil.loadArray(getApplicationContext(), "profilearrayloc").get(i).equals("vibrate")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    PrefUtil.saveSession("profile", "vibrate", getApplicationContext());
                                    PrefUtil.saveArrayinpos(getApplicationContext(), PrefUtil.loadArray(getApplicationContext(), "profilearrayloc"), "profilearrayloc", i, "vibrate");

                                }
                            } else {
                                mPriorityCheck = false;
                            }
                        }
                    } catch (Exception e) {

                    }
                }


            }

            //Back to normal
            if (PrefUtil.loadArray(getApplicationContext(), "todatearray").size() > 0) {

                for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "todatearray").size(); i++) {
                    if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }

                }

            } else {

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                mPriorityCheck = false;
                switch (day) {
                    case Calendar.SUNDAY:

                        for (int i = 0; i <= PrefUtil.loadArray(getApplicationContext(), "sundayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "sundayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    System.out.println("selected timeee 2" + PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i));
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }

                            }
                        }


                    case Calendar.MONDAY:

                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "mondayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "mondayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }


                            }
                        }


                    case Calendar.TUESDAY:

                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "tuesdayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "tuesdayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }

                            }
                        }


                    case Calendar.WEDNESDAY:

                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "wednesdayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "wednesdayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }

                            }
                        }


                    case Calendar.THURSDAY:

                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "thursdayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "thursdayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }

                            }
                        }


                    case Calendar.FRIDAY:

                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "fridayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "fridayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }


                            }
                        }


                    case Calendar.SATURDAY:


                        for (int i = 0; i < PrefUtil.loadArray(getApplicationContext(), "saturdayarray").size(); i++) {
                            if (PrefUtil.loadArray(getApplicationContext(), "saturdayarray").size() > 0) {

                                if (PrefUtil.loadArray(getApplicationContext(), "totimearray").get(i).contains(mCurrentTime)) {
                                    mPriorityCheck = true;
                                    if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        PrefUtil.saveSession("profile", "loud", getApplicationContext());
                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        PrefUtil.saveSession("profile", "silent", getApplicationContext());

                                    } else if (PrefUtil.loadArray(getApplicationContext(), "profilearray").get(i).equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        PrefUtil.saveSession("profile", "vibrate", getApplicationContext());

                                    }
                                } else {
                                    mPriorityCheck = false;
                                }

                            }
                        }
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        if (myTimer != null) {
            myTimer.cancel();
        }
        timerRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       /* Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);*/
        if (!timerRunning) {
            myTimer = new Timer();
            myTimer.scheduleAtFixedRate(new Task(), START_TIME, RETRY_TIME);
            timerRunning = true;
        }
        //sensor event to detect facedown of a mopbile

        if (PrefUtil.getSession("mStarted", getApplicationContext()).equals("true")) {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
            mStarted = true;
        } else {
            try {
                mSensorManager.unregisterListener(this);
            } catch (Exception e) {

            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (PrefUtil.getSession("mStarted", getApplicationContext()).equals("true")) {
            if (type == Sensor.TYPE_ACCELEROMETER) {
                float gz = event.values[2];
                if (mGZ == 0) {
                    mGZ = gz;
                } else {
                    if ((mGZ * gz) < 0) {
                        mEventCountSinceGZChanged++;
                        if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                            if (gz > 0) {
                                Log.d(TAG, "now screen is facing up.");
//<<<<<<< HEAD
                                //   am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                if (PrefUtil.getSession("profile", getApplicationContext()).equals("loud")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                } else if (PrefUtil.getSession("profile", getApplicationContext()).equals("silent")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                } else if (PrefUtil.getSession("profile", getApplicationContext()).equals("vibrate")) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                }
                                intent.putExtra("key", PrefUtil.getSession("profile", getApplicationContext()));

                            } else if (gz < 0) {
                                Log.d(TAG, "now screen is facing down.");
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                                intent.putExtra("key", "silent");


//=======
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            } else if (gz < 0) {
                                Log.d(TAG, "now screen is facing down.");
                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//>>>>>>> e1ac3d9574b163fc1c4e30de15ff60ddb9ef4563
                            }
                            sendBroadcast(intent);
                        }
                    } else {
                        if (mEventCountSinceGZChanged > 0) {
                            mGZ = gz;
                            mEventCountSinceGZChanged = 0;
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

			/* getting status of the gps */
            isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            /* getting status of network provider */
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

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
                            System.out.println("GPS Gettting Lat Lng mLocation");
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
                                    System.out.println("GPS Gettting Lat Lng isGpsEnabled mLocation");
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


    public void stopUsingGps() {

        if (mLocationManager != null) {

            mLocationManager.removeUpdates(MotionControlService.this);

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
        }
        return mLatitude;
    }

    /**
     * @return longitude function to get longitude
     */
    public double getLongitude() {

        if (mLocation != null) {

            mLongitude = mLocation.getLongitude();

        }

        return mLongitude;
    }

    /**
     * @return to check gps or wifi is enabled or not
     */
    public boolean canGetLocation() {

        return this.canGetLocation;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTypeArray.clear();
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                data = HttpUtil.apiGETResponse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mLatitude + "," + mLongitude + "&radius=10&types=&sensor=true&key=" + AppConstants.API_KEY);
                JSONObject mResultObj = new JSONObject(data);
                JSONArray mArray = mResultObj.getJSONArray("results");
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < mArray.length(); i++) {
                    JSONObject mObj = mArray.getJSONObject(i);
                    JSONArray mArray2 = mObj.getJSONArray("types");
                    for (int j = 0; j < mArray2.length(); j++) {
                        mTypeArray.add(mArray2.getString(j));
                    }


                }
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            Log.v("Background Task", "" + mTypeArray.toString());
            try {
                JSONObject json = new JSONObject(PrefUtil.getSession("placeCategory", getApplicationContext()));
                JSONArray mDataArray = json.getJSONArray("data");
                for (int i = 0; i < mDataArray.length(); i++) {
                    JSONObject Obj = mDataArray.getJSONObject(i);
                    JSONArray mMainTypeArray = Obj.getJSONArray("types");
                    JSONArray mSubcategories = Obj.getJSONArray("subcategories");

                    try {
                        for (int k = 0; k < mSubcategories.length(); k++) {
                            JSONObject mSubObj = mSubcategories.getJSONObject(k);
                            JSONArray mSubTypeArray = mSubObj.getJSONArray("types");
                            for (int l = 0; l < mSubTypeArray.length(); l++) {
                                if (mTypeArray.contains(mSubcategories.getString(l))) {
                                    String mSubProfile = mSubObj.getString("profile");
                                    System.out.println("mPriorityCheck" + mPriorityCheck);
                                    if (mPriorityCheck == false) {
                                        if (mSubProfile.equals("loud")) {
                                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        } else if (mSubProfile.equals("silent")) {
                                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        } else if (mSubProfile.equals("vibrate")) {
                                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                        }
                                    }
                                } else {
                                    mSubObj.getString("profile");
                                }
                            }
                        }
                    } catch (Exception e) {
                        for (int j = 0; j < mMainTypeArray.length(); j++) {
                            if (mTypeArray.contains(mMainTypeArray.getString(j))) {
                                String mMainProfile = Obj.getString("profile");
                                System.out.println("mPriorityCheck 1" + mPriorityCheck);
                                if (mPriorityCheck == false) {
                                    if (mMainProfile.equals("loud")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    } else if (mMainProfile.equals("silent")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    } else if (mMainProfile.equals("vibrate")) {
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    }
                                }

                            }

                        }
                    }

                }
            } catch (Exception e) {
            }
        }
    }


    public class Task1 extends TimerTask {
        @Override
        public void run() {
            if (Integer.parseInt(PrefUtil.getSession("frequencyint", getApplicationContext())) > 0) {
                try {
                    PlacesTask placesTask = new PlacesTask();
                    // Invokes the "doInBackground()" method of the class PlaceTask
                    placesTask.execute("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Place_Category extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = Utils_class.mAPI + "api/v1/place_category_list";
            try {
                JSONObject json = new JSONObject(HttpUtil.apiGETResponse(url));
                PrefUtil.saveSession("placeCategory", "" + json.toString(), getApplicationContext());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        @SuppressLint("InlinedApi")
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mAlaram.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + SEVEn_DAYS,
                    pi);
            try {

                String freqInt = PrefUtil.getSession("frequencyint", getApplicationContext());
                if (freqInt != null &&
                        freqInt.trim() != "" &&
                        Integer.parseInt(freqInt) > 15) {

                    myTimer.scheduleAtFixedRate(new Task1(), 0, Integer.parseInt(PrefUtil.getSession("frequency", getApplicationContext())));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                new Place_Category().execute();
            }
        };
        registerReceiver(br, new IntentFilter("com.politephone.android"));
        pi = PendingIntent.getBroadcast(this, 0,
                new Intent("com.politephone.android"), 0);
        mAlaram = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
    }

}

