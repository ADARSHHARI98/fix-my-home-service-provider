package com.example.abdul.fmhserviceprovider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email and password.
 */
public class LoginActivity extends BaseActivity {
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		
		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage(All.LOADING_MSG);
		
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle("Login");
	}
	
	public void onSubmit(View v) {
		final String username = mEmailView.getText().toString().trim();
		final String password = mPasswordView.getText().toString();
		if (username.isEmpty()) {
			Toast.makeText(this, "Username is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.isEmpty()) {
			Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		StringRequest stringRequest = new StringRequest(
				Request.Method.POST,
				"http://" + All.LOCALHOST + "/fixmyhome/wp-json/jwt-auth/v1/token",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						
						if (response.length() < 10) {
							progressDialog.dismiss();
							Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
							return;
						}
//						String[] props = {"token", "user_email", "user_nicename", "user_display_name"};
						
						All.setSharedSTR(All.TOKEN, getProperty(response, "token"), LoginActivity.this);
						
						String nice_name = getProperty(response, "user_nicename");
						// vendor email will be used later.
						All.setSharedSTR(All.VENDOR_EMAIL, getProperty(response, "user_email"), LoginActivity.this);
						
						String display_name = getProperty(response, "user_display_name");
						// vendor display name will be used in searching for his/her orders
						All.setSharedSTR(All.VENDOR_DISPLAY_NAME, display_name, LoginActivity.this);
						
						String url = "http://" + All.LOCALHOST + "/fixmyhome/wp-json/wp/v2/users?slug=" + nice_name;
						
						StringRequest stringRequest1 = new StringRequest(
								Request.Method.GET,
								url,
								new Response.Listener<String>() {
									@Override
									public void onResponse(String response) {
										progressDialog.dismiss();
										Log.v("nice_name_response:", response);
										
										if (response.length() < 10) {
											Toast.makeText(LoginActivity.this, "ID / Email not found.", Toast.LENGTH_SHORT).show();
										}
										
										String role = All.getStringList(response, "\"description\":\"", "\"").get(0);
										
										if ( !role.equals("vendor") ) { // if its role is not equals to "vendor" then its not a vendor or worker
											Toast.makeText(LoginActivity.this, "ID / Email not recognized.", Toast.LENGTH_SHORT).show();
											return;
										}
										
										All.setSharedSTR(All.VENDOR_PIC,
												All.getStringList(response, "\"96\":\"", "\"").get(0),
												LoginActivity.this
										);
										All.setSharedSTR(All.VENDOR_NAME,
												All.getStringList(response, "\"name\":\"", "\"").get(0), LoginActivity.this);
										
										All.setSharedSTR(All.VENDOR_ID,
												All.getStringList(response, "\"id\":", ",").get(0), LoginActivity.this);
										
										startActivity(new Intent(LoginActivity.this, VendorProfile.class));
									}
								},
								new Response.ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError error) {
										All.handleVolleyError(LoginActivity.this, error);
										progressDialog.dismiss();
									}
								}
						) {
							@Override
							public Map<String, String> getHeaders() throws AuthFailureError {
								Map<String, String> mHeaders = new HashMap<>();
								String authToken = "Bearer " + All.getSharedSTR(All.BASIC_TOKEN, LoginActivity.this);
								mHeaders.put("Authorization", authToken);
								return mHeaders;
							}
						};
						All.executeRequest(LoginActivity.this, stringRequest1);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						All.handleVolleyError(LoginActivity.this, error);
						progressDialog.dismiss();
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> mParams = new HashMap<>();
				mParams.put("username", username);
				mParams.put("password", password);
				return mParams;
			}
			
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> mHeaders = new HashMap<>();
				mHeaders.put("Content-Type", "application/x-www-form-urlencoded");
				return mHeaders;
			}
		};
		progressDialog.show();
		All.executeRequest(LoginActivity.this, stringRequest);
	}
	
	private String getProperty(String source, String key) {
		String value = "";
		try {
			JSONObject object = new JSONObject(source);
			value = object.getString(key);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (value != null) {
			return value;
		}
		return "";
	}
}

