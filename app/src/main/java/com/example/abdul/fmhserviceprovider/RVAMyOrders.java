package com.example.abdul.fmhserviceprovider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RVAMyOrders extends RecyclerView.Adapter<RecyclerViewHolderMyOrders> {
	Context context;
	private List<MyOrdersData> allRequestsData = new ArrayList<MyOrdersData>();
	
	public RVAMyOrders(List<MyOrdersData> allRequestsData) {
		this.allRequestsData = allRequestsData;
	}
	
	@NonNull
	@Override
	public RecyclerViewHolderMyOrders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		
		LayoutInflater inflater = LayoutInflater.from(context = viewGroup.getContext());
		View itemView = inflater.inflate(R.layout.my_orders_layout, viewGroup, false);
		
		return new RecyclerViewHolderMyOrders(itemView,
				new RecyclerViewHolderMyOrders.ViewHolderClicks() {
					@Override
					public void onOrderClick(final TextView ID, TextView name, TextView date, TextView time, TextView total) {
						
						final ProgressDialog progressDialog = new ProgressDialog(context);
						progressDialog.setMessage(All.LOADING_MSG);
						
						AlertDialog alertDialog = new AlertDialog.Builder(context).create();
						alertDialog.setTitle("Order ID: "+ID.getText().toString());
						alertDialog.setMessage("Do you want to mark this order as Paid and Completed?\n\n" +
								name.getText().toString() +"\n"+
								total.getText().toString() + "\n"+
								date.getText().toString() + "\n" + time.getText().toString()
						);
						alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										
										StringRequest stringRequest = new StringRequest(
												Request.Method.POST,
												"http://" + All.LOCALHOST + "/fixmyhome/wp-json/wc/v2/orders/" + ID.getText().toString(),
												new Response.Listener<String>() {
													@Override
													public void onResponse(String response) {
														progressDialog.dismiss();
														context.startActivity(new Intent(context, MyOrders.class));
													}
												},
												new Response.ErrorListener() {
													@Override
													public void onErrorResponse(VolleyError error) {
														progressDialog.dismiss();
														All.handleVolleyError(context, error);
													}
												}
										){
											@Override
											public Map<String, String> getHeaders() throws AuthFailureError {
												String basicToken = All.getSharedSTR(All.BASIC_TOKEN, context);
												Map<String, String> headers = new HashMap<String, String>();
												headers.put("Authorization", "Bearer " + basicToken);
												return headers;
											}
											
											@Override
											public byte[] getBody() throws AuthFailureError {
												String body =
														"{\n" +
																"\t\"status\": \"completed\"\n" +
																"}";
												return body.getBytes();
											}
											
											@Override
											public String getBodyContentType() {
												return "application/json; charset=utf-8";
											}
										
										};
										All.executeRequest(context, stringRequest);
										progressDialog.show();
										
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
				});
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerViewHolderMyOrders recyclerViewHolderMyOrders, int i) {
		
		recyclerViewHolderMyOrders.goneID.setText(allRequestsData.get(i).getOrderID());
		
		recyclerViewHolderMyOrders.tvID.setText("Order ID: " + allRequestsData.get(i).getOrderID());
		recyclerViewHolderMyOrders.tvDate.setText("Date: " + allRequestsData.get(i).getOrderDate());
		recyclerViewHolderMyOrders.tvDescription.setText("Details: " + allRequestsData.get(i).getOrderDescription());
		recyclerViewHolderMyOrders.tvStatus.setText("Status: " + allRequestsData.get(i).getOrderStatus());
		recyclerViewHolderMyOrders.tvTime.setText("Time: " + allRequestsData.get(i).getOrderTime());
		recyclerViewHolderMyOrders.tvTotal.setText("Total Amount: " + allRequestsData.get(i).getOrderTotal());
		recyclerViewHolderMyOrders.tvCustomerName.setText("Customer: " + allRequestsData.get(i).getCustomerName());
		recyclerViewHolderMyOrders.tvCustomerAddress.setText("Address: " + allRequestsData.get(i).getCustomerAddress());
		recyclerViewHolderMyOrders.tvCustomerPhone.setText("Phone: " + allRequestsData.get(i).getCustomerPhone());
		
	}
	
	@Override
	public int getItemCount() {
		return allRequestsData.size();
	}
}

class RecyclerViewHolderMyOrders extends RecyclerView.ViewHolder implements View.OnClickListener {
	
	public TextView tvID;
	public TextView goneID;
	public TextView tvStatus;
	public TextView tvCustomerName;
	public TextView tvCustomerAddress;
	public TextView tvCustomerPhone;
	public TextView tvTotal;
	public TextView tvDate;
	public TextView tvTime;
	public TextView tvDescription;
	
	public ViewHolderClicks mListener;
	
	public RecyclerViewHolderMyOrders(View itemView, ViewHolderClicks listener) {
		super(itemView);
		this.mListener = listener;
		
		tvID = itemView.findViewById(R.id.all_requests_id);
		goneID = itemView.findViewById(R.id.all_requests_gone_id);
		tvStatus = itemView.findViewById(R.id.all_requests_status);
		tvCustomerName = itemView.findViewById(R.id.all_requests_customer_name);
		tvTotal = itemView.findViewById(R.id.all_requests_total);
		tvDate = itemView.findViewById(R.id.all_requests_date);
		tvTime = itemView.findViewById(R.id.all_requests_time);
		tvDescription = itemView.findViewById(R.id.all_requests_details);
		tvCustomerAddress = itemView.findViewById(R.id.all_requests_address);
		tvCustomerPhone = itemView.findViewById(R.id.all_requests_phone);
		
		itemView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		mListener.onOrderClick(goneID, tvCustomerName, tvDate, tvTime, tvTotal);
	}
	
	public static interface ViewHolderClicks {
		void onOrderClick(TextView ID, TextView name, TextView date, TextView time, TextView total);
	}
}
