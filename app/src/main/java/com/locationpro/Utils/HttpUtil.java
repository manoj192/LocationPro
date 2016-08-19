package com.locationpro.Utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by asanthan on 12/1/2014.
 */
public class HttpUtil {

    public static String apiGETResponse(String urlstring) {
        String result = "";
        StringBuilder jsonResults = new StringBuilder();
        HttpURLConnection con = null;
       /* HttpResponse response;
		HttpClient myClient = new DefaultHttpClient();
		HttpGet myConnection = new HttpGet(urlstring);
		try {
			response = myClient.execute(myConnection);

			result = EntityUtils
					.toString(response.getEntity(), "UTF-8");

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			System.out.println("sssssss"+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("sssssssss"+e.toString());
		}
		return result;*/
        try {

            URL url;
            InputStream is = null;
            url = new URL(urlstring);
            con = (HttpURLConnection) url.openConnection();
            con.connect();
             is = con.getInputStream();
           result=convertStreamToString(is);
            if (result != null)
                return result;
            else
                return "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return jsonResults.toString();
    }



    public static String apiPOSTResponse(String url, String postParams) {
		InputStream is = null;
		  String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
				StringEntity se = new StringEntity(postParams);
				httpPost.setEntity(se);
			} catch (UnsupportedEncodingException e) {

			}
			httpPost.setHeader("Content-type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			HttpResponse httpResponse = httpclient.execute(httpPost);
			is = httpResponse.getEntity().getContent();
			if (is != null)
				result = convertStreamToString(is);
			else
				result = "Did not work!";
		} catch (ClientProtocolException e) {
			Log.d("tag", "ClientProtocolException :" + e.getMessage());
		} catch (IOException e) {
			System.out.println("json excpetion" + e.toString());
		}

		return result;
	}

	public static String convertStreamToString(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			Log.i("STREAM1", "STEA");

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");

				}
				Log.i("SB", sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {

					is.close();
				} catch (IOException e) {
				}
			}
			Log.d("tag", "values : " + sb.toString());

			return sb.toString();
		} catch (NullPointerException e) {
			// TODO: handle exception
			return "";
		}
	}
}
