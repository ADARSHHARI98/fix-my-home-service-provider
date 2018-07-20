package com.example.abdul.fmhserviceprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
	private Integer basicTokenCounter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		oneTimeCode();
		
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle("Service Provider-Fix My Home");
		
		basicTokenCounter = 0;
		
		getAndSaveBasicTOKEN(); // to check internet connectivity and set basic working token.
	}
	
	private void getAndSaveBasicTOKEN() {
		StringRequest stringRequest = new StringRequest(
				Request.Method.POST,
				"http://" + All.LOCALHOST + "/fixmyhome/wp-json/jwt-auth/v1/token",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						
						try {
							String value;
							JSONObject object = new JSONObject(response);
							value = object.getString("token");
							All.setSharedSTR(All.BASIC_TOKEN, value, MainActivity.this);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						All.handleVolleyError(MainActivity.this, error);
						Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
						if (basicTokenCounter < 3) {
							getAndSaveBasicTOKEN();
						}
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> mParams = new HashMap<>();
				mParams.put("username", "admin");
				mParams.put("password", "admin");
				return mParams;
			}
		};
		All.executeRequest(MainActivity.this, stringRequest);
		basicTokenCounter++;
	}
	
	private void oneTimeCode() {
			Log.v("MODEL:", Build.MODEL);
			if ((Build.MODEL).equals("Android SDK built for x86")) {
				All.setLOCALHOST(true);
			} else {
				All.setLOCALHOST(false);
			}
	}
	
	public void myOrdersButton (View v) {
		startActivity(new Intent(MainActivity.this, MyOrders.class));
	}
	public void loginButton (View v) {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}
}
