package com.paulavasile.dineunite;

import com.paulavasile.dineunite.data.DinnerTableBooking;
import com.paulavasile.dineunite.fragments.DinnerEventsFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class DinnerEventsActivity extends BaseActivity {

	long BookingID;
	DinnerEventsFragment dinnerEventsFragment;

	public static final String UPDATE_EVENT_ACTION = "com.paulavasile.dineunite.updateevents";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dinnerevents);

		setTopTitle(R.string.menu_dinner_events);
		initTopbar();

		Intent intent = getIntent();
		BookingID = intent.getLongExtra("BookingID", 0);

		showContent();

		// Register Broadcast Receiver
		try {
			registerReceiver(updateTableBroadcastReceiver, new IntentFilter(UPDATE_EVENT_ACTION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -- Broadcast Receiver to process the exit actions --
	private BroadcastReceiver updateTableBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			DinnerTableBooking tableInfo = serviceIntent.getParcelableExtra("table_info");
			if (tableInfo != null && dinnerEventsFragment != null) {
				dinnerEventsFragment.updateBookingInfo(tableInfo);
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(updateTableBroadcastReceiver);
	};
	
	private void showContent() {
		dinnerEventsFragment = DinnerEventsFragment.createInstance(BookingID);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content, dinnerEventsFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (dinnerEventsFragment != null) {
			dinnerEventsFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e("DinnerEvnets", "onNewIntent");
		BookingID = intent.getLongExtra("BookingID", 0);
		// showContent();
		updateBookingInfo();
	}

	public void updateBookingInfo() {
		if (dinnerEventsFragment != null) {
			dinnerEventsFragment.updateBookingInfo(BookingID);
		}
	}
}
