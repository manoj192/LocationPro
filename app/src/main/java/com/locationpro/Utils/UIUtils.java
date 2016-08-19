/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.locationpro.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A collection of utility methods, all static.
 */
public class UIUtils {

    public static void displayToast(Context context, String name) {
        Toast toast = Toast.makeText(context, "",
                Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setPadding(25, 5, 25, 5);
        view.setBackgroundResource(android.R.color.black);

        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setText(name);
        text.setTextAppearance(context,
                android.R.style.TextAppearance_Small);
        text.setGravity(Gravity.LEFT | Gravity.CENTER);
        text.setPadding(5, 5, 5, 5);
        text.setTextColor(Color.WHITE);
        toast.show();
    }

    public static String getStringFromEditText(EditText e) {
        return e.getText().toString().trim();

    }

	/*
	 * Making sure public utility methods remain static
	 */
	private UIUtils() {
	}

    public static void handleException(Context context, Exception e) {}


    @SuppressWarnings("deprecation")
	/**
	 * Returns the screen/display size
	 *
	 * @param ctx
	 * @return
	 */
	public static Point getDisplaySize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		return new Point(width, height);
	}

	/**
	 * Shows an error dialog with a given text message.
	 * 
	 * @param context
	 * @param errorString
	 */
	public static final void showErrorDialog(Context context, String errorString) {
		new AlertDialog.Builder(context)
				.setTitle("Error")
				.setMessage(errorString)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	/**
	 * Shows an error dialog with a text provided by a resource ID
	 * 
	 * @param context
	 * @param resourceId
	 */
	public static final void showErrorDialog(Context context, int resourceId) {
		new AlertDialog.Builder(context)
				.setTitle("Error")
				.setMessage(context.getString(resourceId))
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	/**
	 * Shows an "Oops" error dialog with a text provided by a resource ID
	 * 
	 * @param context
	 * @param resourceId
	 */
	public static final void showOopsDialog(Context context, int resourceId) {
		new AlertDialog.Builder(context)
				.setTitle("OOPS")
				.setMessage(context.getString(resourceId))
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})

				.create().show();
	}

	/**
	 * A utility method to handle a few types of exceptions that are commonly
	 * thrown by the cast APIs in this library. It has special treatments for
	 * {@link TransientNetworkDisconnectionException},
	 * {@link NoConnectionException} and shows an "Oops" dialog conveying
	 * certain messages to the user. The following resource IDs can be used to
	 * control the messages that are shown:
	 * <p>
	 * <ul>
	 * <li><code>R.string.connection_lost_retry</code></li>
	 * <li><code>R.string.connection_lost</code></li>
	 * <li><code>R.string.failed_to_perform_action</code></li>
	 * </ul>
	 * 
	 * @param context
	 * @param e
	 */

	/**
	 * Gets the version of app.
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionString = null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0 /* basic info */);
			versionString = info.versionName;
		} catch (Exception e) {
			// do nothing
		}
		return versionString;
	}

	/**
	 * Shows a (long) toast.
	 * 
	 * @param context
	 * @param resourceId
	 */
	public static void showToast(Context context, int resourceId) {
		Toast.makeText(context, context.getString(resourceId),
                Toast.LENGTH_LONG).show();
	}

    public static void showToast(Context context, String resourceId) {
        Toast.makeText(context, resourceId,
                Toast.LENGTH_LONG).show();
    }
    public static String getCurrentDate(Context context){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }
    public static Date ConvertDate(Context context,String string){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date=null;
        try {
             date= dateFormat.parse(string.trim());
        }catch (ParseException e){
System.out.println("Exception in parse"+e.toString());
        }
        return date;
    }
    public static String  ConvertDouble(Double value){
        DecimalFormat format = new DecimalFormat("0.00");
        String formatted = format.format(value);
        return formatted;
    }
}