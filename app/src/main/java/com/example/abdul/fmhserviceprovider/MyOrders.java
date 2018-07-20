package com.example.abdul.fmhserviceprovider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrders extends BaseActivity {
	ProgressDialog progressDialog;
	Context context;
	private RecyclerView RVListAllOrders;
	private RVAMyOrders adapter;
	private RecyclerView.LayoutManager layoutManager;
	private List<MyOrdersData> dataList = new ArrayList<>();
	List<String> orderID = new ArrayList<String>();
	List<String> orderStatus = new ArrayList<String>();
	List<String> customerName = new ArrayList<String>();
	List<String> customerPhone = new ArrayList<String>();
	List<String> customerAddress = new ArrayList<String>();
	List<String> orderTotal = new ArrayList<String>();
	List<String> orderDate = new ArrayList<String>();
	List<String> orderTime = new ArrayList<String>();
	List<String> orderDescription = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);
		context = MyOrders.this;
		
		if ( !All.checkLoginStatus(context) ) {
			finish();
			startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(
					All.getSharedSTR(All.VENDOR_NAME, context) + "'s Orders"
			);
		
		setRecyclerView();
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(All.LOADING_MSG);
		
		final String basicToken = All.getSharedSTR(All.BASIC_TOKEN, context);
//		String userEmail = All.getSharedSTR(All.VENDOR_EMAIL, context);
//		String vendorID = All.getSharedSTR(All.VENDOR_ID, context);
		
		String vendor_display_name = All.getSharedSTR(All.VENDOR_DISPLAY_NAME, context);
		
		// if there is white space in the name then it must be replaced by "%20" to be sent with URI
		if (vendor_display_name.indexOf(' ') != -1 )
			vendor_display_name = vendor_display_name.replace(" ", "%20");
		
		StringRequest stringRequest = new StringRequest(
				Request.Method.GET,
				"http://" + All.LOCALHOST + "/fixmyhome/wp-json/wc/v2/orders?search=" + vendor_display_name,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						progressDialog.dismiss();
						Log.v("Allresuests:", response);
						if ( response.length() > 10)
							setResponse(response);
						else
							Toast.makeText(context, response, Toast.LENGTH_LONG).show();
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						All.handleVolleyError(context, error);
						progressDialog.dismiss();
					}
				}
		) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + basicToken);
				return headers;
			}
		};
		int MY_SOCKET_TIMEOUT_MS = 30000;
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				MY_SOCKET_TIMEOUT_MS,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		progressDialog.show();
		All.executeRequest(context, stringRequest);
	}
	
	private void setResponse(String response) {
		if ( !response.isEmpty() ) {
			Gson gson = new Gson();
			List orderObjects = (List) gson.fromJson(response, List.class);
//			String[] postContent = new String[orderObjects.size()];
			for (int i = 0; i < orderObjects.size(); ++i) {
				Map<String, Object> anObject = (Map<String, Object>) orderObjects.get(i);
				
				String id = (Double)anObject.get("id") + "";
				id = id.substring(0, id.indexOf('.'));
				orderID.add( id );
				orderStatus.add( (String)anObject.get("status"));
				
				Map<String, Object> billing = (Map<String, Object>)anObject.get("billing");
				String name = (String)billing.get("first_name");
				name += " " + (String)billing.get("last_name");
				customerName.add( name );
				
				String address = (String)billing.get("address_1");
				customerAddress.add(address);
				
				String phone = (String)billing.get("phone");
				customerPhone.add(phone);
				
				List line_items = (List) anObject.get("line_items");
				Map<String, Object> line_object = (Map<String, Object>)line_items.get(0);
				
				orderTotal.add( (String)line_object.get("total") );
				
				List meta_data = ((List)line_object.get("meta_data") );
				orderTime.add( (String)((Map<String, Object>)meta_data.get(0)).get("value") ); // time
				orderDate.add( (String)((Map<String, Object>)meta_data.get(1)).get("value") ); // date
				orderDescription.add( (String)((Map<String, Object>)meta_data.get(2)).get("value") ); // description
			}
			
			for (int i=0; i<orderID.size(); i++ ) {
				dataList.add(
						new MyOrdersData(
								orderID.get(i),
								orderStatus.get(i),
								customerName.get(i),
								customerAddress.get(i),
								customerPhone.get(i),
								orderTotal.get(i),
								orderTime.get(i),
								orderDate.get(i),
								orderDescription.get(i)
						)
				);
			}
			
			setRecyclerView();
		}
		else {
			Toast.makeText(context, "No Response String !", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setRecyclerView() {
		RVListAllOrders = findViewById(R.id.recycler);
		RVListAllOrders.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		RVListAllOrders.setLayoutManager(layoutManager);
		adapter = new RVAMyOrders(dataList);
		
		RVListAllOrders.setAdapter(adapter);
	}
}
