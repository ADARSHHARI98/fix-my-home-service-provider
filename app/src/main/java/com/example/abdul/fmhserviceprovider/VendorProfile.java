package com.example.abdul.fmhserviceprovider;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VendorProfile extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vendor_profile);
		
		String name;
		String pic;
		String email;
		
		name = All.getSharedSTR(All.VENDOR_NAME, VendorProfile.this);
		Log.v("vendornamec: ", name);
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle("Welcome " + name);
		
		ImageView profilePic = findViewById(R.id.imageViewVendor);
		pic = All.getSharedSTR(All.VENDOR_PIC, VendorProfile.this);
		Log.v("vendorpic: ", pic);
		Picasso.get().load(pic).into(profilePic);
		
		Log.v("vendorID:", All.getSharedSTR(All.VENDOR_ID, VendorProfile.this));
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		
		Display display = null;
		if (wm != null) {
			display = wm.getDefaultDisplay();
		}
		if (display != null) {
			display.getMetrics(metrics);
		}
		Integer width = metrics.widthPixels / 2; // 50% width
		Integer height = metrics.heightPixels / 3; // 33% height
		
		profilePic.getLayoutParams().width = width;
		profilePic.getLayoutParams().height = height;
//		profilePic.requestLayout();
		
		email = All.getSharedSTR(All.VENDOR_EMAIL, VendorProfile.this);
		TextView userEmail = findViewById(R.id.textViewVendorEmail);
		
		userEmail.setText("Email: ");
		userEmail.append(email);
	}
}
