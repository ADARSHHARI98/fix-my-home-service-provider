package com.example.abdul.fmhserviceprovider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.change_ip:
				All.changeLocalhost(BaseActivity.this);
				return true;
			case R.id.login:
				startActivity(new Intent(BaseActivity.this, LoginActivity.class));
				return true;
			case R.id.all_requests:
				startActivity(new Intent(BaseActivity.this, MyOrders.class));
				return true;
			case R.id.restart:
				startActivity(new Intent(BaseActivity.this, MainActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
