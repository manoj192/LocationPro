package com.locationpro;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Address_s extends AsyncTask<String, String, String> {

	private static Context mContext;
	private static LatLng mPosition;
	private static String Address = "";
	Geocoder geocoder;
	private double latitude;
	private double longitude;
	List<android.location.Address> addresses;

	public Address_s(Context context, LatLng position) {
		mContext = context;
		mPosition = position;
		latitude = mPosition.latitude;
		longitude = mPosition.longitude;
		geocoder = new Geocoder(mContext, Locale.getDefault());
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				Address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+".";
			}
		} catch (IOException e) {
			Log.e("Address_s", "IO " + e.getMessage());
			Address = "";
		} catch (NullPointerException e) {
			Log.e("Address_s", "Null " + e.getMessage());
			Address = "";
		}
		return Address;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		result = Address;
	}
}
