package com.paulavasile.dineunite.gcm;

import org.apache.http.util.TextUtils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.paulavasile.dineunite.DinnerEventsActivity;
import com.paulavasile.dineunite.ProfileActivity;
import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.data.BookingPrivacyTypes;
import com.paulavasile.dineunite.data.BookingTypes;
import com.paulavasile.dineunite.data.DinnerTableBooking;
import com.paulavasile.dineunite.data.RestaurantTable;
import com.paulavasile.dineunite.data.TableBookingGuest;
import com.paulavasile.dineunite.data.TableBookingHost;
import com.paulavasile.dineunite.data.TablePromotions;

/**
 * Created by xiaomingming on 16. 4. 22..
 */
public class GcmIntentService extends IntentService {
	public static final String TAG = "icelancer";
	public static int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;

	public GcmIntentService() {
		// Used to name the worker thread, important only for debugging.
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				
				Log.i(TAG, "Received: " + extras.toString());

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
					String title = extras.getString("title", "");
				}
				int type = parseInt(extras.getString("type"));
				String msg = null;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
					msg = extras.getString("message", "");
				}

				if (type == 1) {
					// DinnerTableBooking information
					DinnerTableBooking bookingInformation = getBookingInfo(intent);

					// Send Broadcast
					Intent broadcastIntent = new Intent(DinnerEventsActivity.UPDATE_EVENT_ACTION);
					broadcastIntent.putExtra("table_info", bookingInformation);
					sendBroadcast(broadcastIntent);

					if (TextUtils.isEmpty(msg)) {
						msg = "Booking event was updated.";
					}
					sendNotification(type, msg);
					
				} else if (type == 2) {
					// Profile related information
					if (TextUtils.isEmpty(msg)) {
						msg = "Please complete the missing fields to get those dinner invites!\nDineUnite Team";
					}
					sendNotification(type, msg);
				}
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private int parseInt(String string) {
		int value = 0;
		try {
			value = Integer.parseInt(string);
		} catch (NumberFormatException e) {
		}
		return value;
	}

	private DinnerTableBooking getBookingInfo(Intent intent) {
		DinnerTableBooking newTableInfo = new DinnerTableBooking();

		newTableInfo.BookingID = parseInt(intent.getStringExtra("BookingID"));
		newTableInfo.BookingDate = intent.getStringExtra("BookingDate");
		newTableInfo.TimeFrom = intent.getStringExtra("TimeFrom");

		newTableInfo.TimeTo = intent.getStringExtra("TimeTo");
		newTableInfo.FirstName = intent.getStringExtra("FirstName");
		newTableInfo.LastName = intent.getStringExtra("LastName");
		newTableInfo.Phone = intent.getStringExtra("Phone");
		newTableInfo.TitleOptionID = parseInt(intent.getStringExtra("TitleOptionID"));
		newTableInfo.TableID = parseInt(intent.getStringExtra("TableID"));
		newTableInfo.Table = RestaurantTable.parseObjectFromString(intent.getStringExtra("Table"));
		newTableInfo.Promotion = TablePromotions.parseObjectFromString(intent
				.getStringExtra("Promotion"));
		newTableInfo.BookingType = BookingTypes.parseObjectFromString(intent
				.getStringExtra("BookingType"));
		newTableInfo.BookingPrivacyType = BookingPrivacyTypes.parseObjectFromString(intent
				.getStringExtra("BookingPrivacyType"));
		newTableInfo.JoinedSeatsCount = parseInt(intent.getStringExtra("JoinedSeatsCount"));
		newTableInfo.BookedSeatsCount = parseInt(intent.getStringExtra("BookedSeatsCount"));
		newTableInfo.RemainingSeatsCount = parseInt(intent.getStringExtra("RemainingSeatsCount"));
		newTableInfo.IsConfirmed = intent.getBooleanExtra("IsConfirmed", false);
		newTableInfo.IsCancelled = intent.getBooleanExtra("IsCancelled", false);
		newTableInfo.IsExpired = intent.getBooleanExtra("IsExpired", false);
		newTableInfo.AmIHost = intent.getBooleanExtra("AmIHost", false);
		newTableInfo.AmIGuest = intent.getBooleanExtra("AmIGuest", false);
		newTableInfo.RemaininDays = parseInt(intent.getStringExtra("RemaininDays"));
		newTableInfo.RemainingHours = parseInt(intent.getStringExtra("RemainingHours"));
		newTableInfo.RemainingMinutes = parseInt(intent.getStringExtra("RemainingMinutes"));
		newTableInfo.BookingRef = intent.getStringExtra("BookingRef");
		newTableInfo.Host = TableBookingHost.parseObjectFromString(intent.getStringExtra("Host"));
		newTableInfo.Guests = TableBookingGuest
				.parseListFromString(intent.getStringExtra("Guests"));
		newTableInfo.RestaurantID = parseInt(intent.getStringExtra("RestaurantID"));
		newTableInfo.RestaurantName = intent.getStringExtra("RestaurantName");
		newTableInfo.RestaurantImage = intent.getStringExtra("RestaurantImage");
		newTableInfo.PostCode = intent.getStringExtra("PostCode");
		newTableInfo.RestaurantAddress = intent.getStringExtra("RestaurantAddress");
		newTableInfo.RestaurantDistance = intent.getDoubleExtra("RestaurantDistance", 0);
		newTableInfo.RestaurantLatitude = intent.getDoubleExtra("RestaurantLatitude", 0);
		newTableInfo.RestaurantLongitude = intent.getDoubleExtra("RestaurantLongitude", 0);

		return newTableInfo;
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(int type, String msg) {

		// Show Notification
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = null;
		if (type == 1) {
			intent = new Intent(this, DinnerEventsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		} else if (type == 2) {
			intent = new Intent(this, ProfileActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		}

		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(getString(R.string.app_name))
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
		mBuilder.setAutoCancel(true);
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
	}

}
