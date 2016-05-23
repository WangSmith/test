package com.paulavasile.dineunite.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * IntentService for fetching the address for a given location.
 */
public class FetchAddressIntentService extends IntentService {

	protected ResultReceiver mReceiver;

	public FetchAddressIntentService() {
		super("FetchAddressIntentService");
	}

	/**
	 * Creates an IntentService. Invoked by your subclass's constructor.
	 * 
	 * @param name
	 *            Used to name the worker thread, important only for debugging.
	 */
	public FetchAddressIntentService(String name) {
		super("FetchAddressIntentService");
	}

	/**
	 * Fetches the address for a given location.
	 * 
	 * @param intent
	 *            The value passed to {@link #startService(Intent)}.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		String errorMessage = "";

		// Get receiver to send the results to
		mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

		if (mReceiver == null) {
			Log.e("Geofencing", "No receiver received. There is nowhere to send the results.");
			return;
		}

		// Get location
		Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

		if (location == null) {
			errorMessage = "No location specified.";
			deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
			return;
		}

		// Get geocoder
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			if (geocoder.isPresent()) {

                List<Address> addresses = null;

                // Fetch address
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (IllegalArgumentException illegalArgumentException) {
                    Log.e("Geofencing", errorMessage + ". " + "Latitude = " + location.getLatitude()
                            + ", Longitude = " + location.getLongitude(), illegalArgumentException);
                }

                if (addresses == null || addresses.size() == 0) {
                    errorMessage = "No address found at location.";
                    deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
                } else {
                    Address address = addresses.get(0);

                    // Way 3
                    String addressLine = address.getAddressLine(0);
                    String city = address.getLocality();
                    String state = address.getAdminArea();
                    String country = address.getCountryName();
                    String postalCode = address.getPostalCode();

                    // Only if available else return NULL
                    String currentAddresLines = addressLine;
                    if (!TextUtils.isEmpty(currentAddresLines)) {
                        if (!TextUtils.isEmpty(city) && !currentAddresLines.contains(city))
                            currentAddresLines += " " + city;
                        if (!TextUtils.isEmpty(state) && !currentAddresLines.contains(state))
                            currentAddresLines += " " + state;
                        if (!TextUtils.isEmpty(postalCode) && !currentAddresLines.contains(postalCode))
                            currentAddresLines += "," + postalCode;

                        deliverResultToReceiver(Constants.SUCCESS_RESULT, currentAddresLines);
                    } else {
                        if (address.getThoroughfare() != null) {
                            deliverResultToReceiver(Constants.SUCCESS_RESULT, address.getThoroughfare());
                        } else {
                            deliverResultToReceiver(Constants.SUCCESS_RESULT, address.getFeatureName());
                        }
                    }
                }
            } else {
                errorMessage = "No geocoder present.";
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            }
		}

	}

	/**
	 * Send the results back to the receiver.
	 * 
	 * @param resultCode
	 *            The result code to send back to the receiver.
	 * @param message
	 *            The message to send back to the receiver.
	 */
	private void deliverResultToReceiver(int resultCode, String message) {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.RESULT_DATA_KEY, message);
		mReceiver.send(resultCode, bundle);
	}

	/**
	 * Constants for {@link FetchAddressIntentService}.
	 */
	public final class Constants {
		/**
		 * Constant to indicate a success.
		 */
		public static final int SUCCESS_RESULT = 0;
		/**
		 * Constant to indicate a failure.
		 */
		public static final int FAILURE_RESULT = 1;
		/**
		 * Constant for the package name.
		 */
		public static final String PACKAGE_NAME = "nl.svendubbeld.car";
		/**
		 * Constant for the receiver extra of the Intent.
		 */
		public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
		/**
		 * Constant for the result data.
		 */
		public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
		/**
		 * Constant for the location extra of the Intent.
		 */
		public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
	}
}
