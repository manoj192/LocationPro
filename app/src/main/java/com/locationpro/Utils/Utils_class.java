package com.locationpro.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils_class {

	public static final String mAPI = "http://travlore-momentustech.rhcloud.com/";
	public static Double Lat = 0.0;
	public static Double Lng = 0.0;
	public static Double MapLatitude = 0.0;
	public static Double MapLongitude = 0.0;
	public static Double MapLat = 0.0;
	public static Double MapLng = 0.0;
	public static double Latitude;
	public static double Longitude;
	public static String mAddress = "";
	public static Boolean whatsappcilck = false;
	public static Boolean fbclick = false;
	public static Boolean fromsplash = false;
	public static Boolean twitclick = false;
	public static Boolean secclick = false;
	public static Intent mIntent = null;
	public static Bitmap mBitmap = null;
	public static int mFinalWidth, mFinalHeight;
	public static int bound_width;
	public static int bound_height;
	public static final int ACTION_TAKE_PICTURE = 110;
	public static final int ACTION_GALLERY = 120;
	public static final int SHOW_PROGRESS_DIALOG = 0x00;
	public static final int STOP_PROGRESS_DIALOG = 0x01;
	public static final int SUCCESS_CITY = 0x04;
	public static final int mGoNextActivity = 100;
	public static int frommenuclick;
	public static String logintype = "";
	public static String mResponse = "";
	public static String search_key = "";
	public static String muser_id = "";
	public static String muser_key = "";
	public static String mEmail;
	public static String mFirstName;
	public static String mLastName;
	public static String busar;
	public static String mFullName;
	public static String mGender;
	public static String mLocation;
	public static String mProfileimg;
	public static String mDOB;
	public static String mUserType;
	public static String mStatus;
	public static String mPostal;
	public static String mAddOne;
	public static String mAddTwo;
	public static String mUserName;
	public static String mLocalResultCounts = "";
	public static String mUserId = "";
	public static String geoCountryname = "";
	public static String type = "";
	public static String geogetCityname = "";
	public static boolean mLangFlag1;
	public static String subfromlist = "";
	public static String arname = "";
	public static String catfromhome = "";
	public static String idfromhome = "";
	public static String subfromid = "";
	public static String from_class = "";

	public static int new_release;
	public static int featured_movies;
	public static int recently_added;
	public static int telugu;
	public static int most_popular;
	public static String file_path;
	public static int tocomment = 0;
	public static int count;

	// Registration Fields//

	public static String mEmailId;
	public static String mPassword;
	public static String mFName;
	public static String mLName;
	public static String mPhoneNumber;
	public static String mAddress1;
	public static String mAddress2;
	public static String mCity;
	public static String mPostelCode;
	public static String mDob;
	public static String mLocationNames = "";
	public static String mAreaNames = "";
	public static String mContactNumber;
	public static boolean mLocationActivity;
	public static String mFromRateThis = "";
	public static String mBusinessCode;
	public static String mBusinessId;
	public static String mBusinessNames;
	public static String mDeviceId = "";
	public static Locale mLocale;
	public static boolean mFromHome;
	public static boolean mFromSubCat;
	public static boolean mFromBusiList;
	public static boolean mFromSubToBusi;
	public static boolean mFromHonmeToSub;
	public static boolean mFromHomeSearch;
	public static boolean mFromSubSearch;
	public static boolean mFromBusiSearch;
	public static String mAreaCode;
	public static String mBusiName;
	public static String mCategoryId;
	public static String mCategoryName;
	public static String mCategoryNameAr;
	public static String latlat = "0";
	public static String latlog = "0";
	public static String mSearchType;
	public static String arcat;
	public static boolean mSearchCount;
	private static SharedPreferences mSharedpreference;
	private final static String ISFIRSTTIMEAPPINSTALL = "ISFIRSTTIMEAPPINSTALL";
	private static Editor mEditor;
	public static boolean mToTimeRule;
	public static boolean mToLocationRule;

	public static String webcontent;

	public static HashMap<String, String> mUserData = new HashMap<String, String>();
	public static ArrayList<HashMap<String, String>> mMyBusinessRatings = new ArrayList<HashMap<String, String>>();

	public static boolean mReferesh;



	public static boolean isOnlineAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static boolean isEmailValid(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches())
			return true;
		else
			return false;
	}
	public static void hideKeypad(Context context, View view) {
		final InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void displayToast(Context context, String name) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setPadding(25, 5, 25, 5);
		view.setBackgroundResource(android.R.color.black);

		TextView text = (TextView) view.findViewById(android.R.id.message);
		text.setText(name);
		text.setTextAppearance(context, android.R.style.TextAppearance_Small);
		text.setGravity(Gravity.LEFT | Gravity.CENTER);
		text.setPadding(5, 5, 5, 5);
		text.setTextColor(Color.WHITE);
		toast.show();
	}

	@SuppressWarnings("deprecation")


	public static boolean isOnline(Context con) {

		ConnectivityManager cm = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Log.i("netInfo", "" + netInfo);
			return true;
		}
		con.startActivity(new Intent(Settings.ACTION_SETTINGS));
		return false;
	}

	public static BitmapFactory.Options calculateInSampleSize(int reqWidth,
			int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		options.inSampleSize = inSampleSize;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		return options;
	}

	public void getScaledDimension(int imgWdith, int imgHeight, int i) {

		int original_width = imgWdith;
		int original_height = imgHeight;
		if (i == 0) {
			scaleDown(imgWdith, imgHeight, 760);
		} else {
			scaleDown(imgWdith, imgHeight, 612);
		}

		mFinalWidth = original_width;
		mFinalHeight = original_height;

		// first check if we need to scale width
		if (original_width > bound_width) {
			// scale width to fit
			mFinalWidth = bound_width;
			// scale height to maintain aspect ratio
			mFinalHeight = (mFinalWidth * original_height) / original_width;
		}

		// then check if we need to scale even with the new height
		if (mFinalHeight > bound_height) {
			// scale height to fit instead
			mFinalHeight = bound_height;
			// scale width to maintain aspect ratio
			mFinalWidth = (mFinalHeight * original_width) / original_height;
		}
	}

	public static void scaleDown(int width, int height, float maxImageSize) {
		float ratio = Math.min(maxImageSize / width, maxImageSize / height);
		bound_width = Math.round(ratio * width);
		bound_height = Math.round(ratio * height);
	}

	public Bitmap decodeFile2(File f) {
		try {
			System.gc();
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			final int REQUIRED_SIZE = 200;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = getScale(o, mFinalWidth, mFinalHeight);
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	private int getScale(BitmapFactory.Options o, int w, int h) {
		final int height1 = o.outHeight;
		final int width1 = o.outWidth;
		int inSampleSize = 1;

		if (height1 > h || width1 > w) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height1 / (float) h);
			final int widthRatio = Math.round((float) width1 / (float) w);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static int Check(String date) {

		long time = Long.valueOf(date);
		Date firstdate = new Date(time * 1000);
		Date seconDate = new Date(Calendar.getInstance().getTimeInMillis());
		return (int) ((firstdate.getTime() - seconDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static boolean getFirstTimeAppInstall() {
		return mSharedpreference.getBoolean(ISFIRSTTIMEAPPINSTALL, false);
	}

	public static void setFirstTimeAppInstall(boolean isFirstTimeAppInstall) {
		mEditor.putBoolean(ISFIRSTTIMEAPPINSTALL, isFirstTimeAppInstall);
		mEditor.commit();
	}

	public static Bitmap filetobitmap(String filepath) {

		byte[] encodeByte = Base64.decode(filepath, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                encodeByte.length);
		return bitmap;

	}

	

}
