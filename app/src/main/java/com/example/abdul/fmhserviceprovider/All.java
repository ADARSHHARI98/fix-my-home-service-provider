package com.example.abdul.fmhserviceprovider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class All {
	public static String LOCALHOST_PHY = "192.168.1.100";
	public static String LOCALHOST_VIR = "10.0.2.2";
	public static String LOCALHOST;
	public static String TOKEN = "TOKEN";
	static String LOADING_MSG = "Please Wait";
	static String BASIC_TOKEN = "BASIC_TOKEN";
	static String VENDOR_EMAIL = "VENDOR_EMAIL";
	static String VENDOR_ID = "VENDOR_ID";
	static String VENDOR_NAME = "VENDOR_NAME";
	static String VENDOR_DISPLAY_NAME = "VENDOR_DISPLAY_NAME";
	static String VENDOR_PIC = "VENDOR_PIC";
	
	public static void setLOCALHOST(boolean isVirtual) {
		LOCALHOST = isVirtual? LOCALHOST_VIR: LOCALHOST_PHY;
	}
	
	public static void handleVolleyError(Context context, VolleyError error) {
		error.printStackTrace();
		
		if (error instanceof NoConnectionError) {
			Toast.makeText(context, "No Connection Found !", Toast.LENGTH_SHORT).show();
		}
		else if (error instanceof NetworkError) {
			Toast.makeText(context, "Network Error !", Toast.LENGTH_SHORT).show();
		}
		else if (error instanceof ServerError) {
			Toast.makeText(context, "Server Error !", Toast.LENGTH_SHORT).show();
		}
		else if (error instanceof AuthFailureError) {
			Toast.makeText(context, "Authentication Failure !", Toast.LENGTH_SHORT).show();
		}
		else if (error instanceof TimeoutError) {
			Toast.makeText(context, "Timeout !", Toast.LENGTH_LONG).show();
		}
		else if (error instanceof ParseError) {
			Toast.makeText(context, "Parse Error !", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static List<String> getStringList(String sourceSTR, String startOfSearch, String endOfSearch) {
		List<String> stringList = new ArrayList<>();
		if (sourceSTR == null || sourceSTR.isEmpty()) {
			stringList.add("");
			return stringList;
		}
		
		Pattern p = Pattern.compile(startOfSearch);
		Matcher m = p.matcher(sourceSTR);
		while (m.find()) {
//			String temp = sourceSTR.substring( isFromStart? m.start(): m.end() );
			String temp = sourceSTR.substring(m.end());
			
			Pattern pp = Pattern.compile(endOfSearch);
			Matcher mm = pp.matcher(temp);
			
			String temp2 = "";
			
			if (mm.find()) {
				temp2 = temp.substring(0,
//						isTillEnd? mm.end() : mm.start()
						mm.start()
				);
			}
			stringList.add(temp2);
			
			if (stringList.size() < 1) { // if no match was found.
				stringList.add("");
			}
		}
		return stringList;
	}
	
	public static void setSharedSTR(String key, String value, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value).apply();
	}
	
	public static String getSharedSTR(String key, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//		Toast.makeText(context,sharedPreferences.getStringList(All.TOKEN, ""),
//				Toast.LENGTH_SHORT).show();
		return sharedPreferences.getString(key, "");
	}
	
	public static void changeLocalhost(Context context) {
		final EditText input = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setView(input);
		
		alertDialog.setTitle("New Localhost IP");
		alertDialog.setMessage("");
		
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Change",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						All.LOCALHOST = input.getText().toString().trim();
						dialog.dismiss();
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}
	
	public static void executeRequest(Context context, Request request) {
		String url = request.getUrl();
		if (url != null && !url.isEmpty())
			Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(context, "URL not set", Toast.LENGTH_SHORT).show();
		
		Volley.newRequestQueue(context).add(request);
	}
	
	public static boolean checkLoginStatus(Context context) {
		String Token = All.getSharedSTR(All.TOKEN, context);
		String Username = All.getSharedSTR(All.VENDOR_NAME, context);
		
		if (Token.isEmpty() || Username.isEmpty()) {
			Toast.makeText(context, "Session not found, please Login first.", Toast.LENGTH_LONG).show();
			return false;
		}
		else
			return true;
	}
}
