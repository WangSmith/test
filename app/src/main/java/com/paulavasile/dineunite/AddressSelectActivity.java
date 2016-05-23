package com.paulavasile.dineunite;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.paulavasile.dineunite.utils.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class AddressSelectActivity extends BaseActivity implements
		GoogleApiClient.OnConnectionFailedListener {

	private EditText keywordEdt;
	private View search_clear;
	private UpdateHandler updateHandler;
	private String currentKeyword;

	protected GoogleApiClient mGoogleApiClient;
	
	private ListView dataLv;
	private DataAdapter adapter;
	private ArrayList<AutocompletePrediction> mResultList = new ArrayList<>();
	private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
	
	private String TAG = "SelectAddress";
	
	private static class UpdateHandler extends Handler {
		public static final int UPDATE = 0;

		private WeakReference<AddressSelectActivity> mWeakActivity;

		public UpdateHandler(AddressSelectActivity activity) {
			mWeakActivity = new WeakReference<AddressSelectActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AddressSelectActivity activity = mWeakActivity.get();
			if (activity == null) {
				return;
			}

			if (msg.what == UPDATE) {
				activity.updateAddressList();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_address);

		initTopbar();
		setTopTitle(R.string.select_location);
		hideShareBtn();
		
		// Check Google PlayService
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, 0 /* clientId */, this).addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.build();

		// Update Handler
		updateHandler = new UpdateHandler(this);

		// Search Elements
		keywordEdt = (EditText) findViewById(R.id.search_edit);
		search_clear = findViewById(R.id.search_clear);
		search_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				keywordEdt.setText("");
			}
		});

		keywordEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				updateHandler.removeMessages(0);
				updateHandler.sendEmptyMessageDelayed(0, 1500);
				currentKeyword = keywordEdt.getText().toString();
				if (TextUtils.isEmpty(currentKeyword)) {
					search_clear.setVisibility(View.GONE);
				} else {
					search_clear.setVisibility(View.VISIBLE);
				}
			}
		});

		dataLv = (ListView) findViewById(R.id.dataLv);
		adapter = new DataAdapter(this);
		dataLv.setAdapter(adapter);
		dataLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					AutocompletePrediction prediction = mResultList.get(position); 
					String address = prediction.getDescription();
					address = address.trim();
					
					/*Intent intent = getIntent();
					intent.putExtra("new_location", address);
					setResult(RESULT_OK, intent);
					finish();*/
					
					// Using Google Api function
					try {
						String domainParam = URLEncoder.encode(address, "utf-8");
						String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", domainParam);
						ReadTask downloadTask = new ReadTask(address);
						downloadTask.execute(url);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Update Current Location List
	private void updateAddressList() {
		if (TextUtils.isEmpty(currentKeyword)) {
			return;
		}
		
		new GetData().execute("");
	}

	private class DataAdapter extends BaseAdapter {

		Context mContext;

		public DataAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			if (mResultList == null)
				return 0;
			return mResultList.size();
		}

		@Override
		public AutocompletePrediction getItem(int position) {
			if (mResultList == null)
				return null;
			return mResultList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_location, null);
			}

			AutocompletePrediction item = getItem(position);
			TextView locationNameTv = (TextView) convertView.findViewById(R.id.locationNameTv);
			//locationNameTv.setText(item.getPrimaryText(STYLE_BOLD).toString() + " " + item.getSecondaryText(STYLE_BOLD).toString());
			locationNameTv.setText(item.getDescription());
			locationNameTv.setTypeface(spinnerTitleFont);
			//locationNameTv.setSelected(true);
			return convertView;
		}
	}

	/**
	 * Submits an autocomplete query to the Places Geo Data Autocomplete API.
	 * Results are returned as frozen AutocompletePrediction objects, ready to
	 * be cached. objects to store the Place ID and description that the API
	 * returns. Returns an empty list if no results were found. Returns null if
	 * the API client is not available or the query did not complete
	 * successfully. This method MUST be called off the main UI thread, as it
	 * will block until data is returned from the API, which may include a
	 * network request.
	 * 
	 * @param constraint
	 *            Autocomplete query string
	 * @return Results from the autocomplete API or null if the query was not
	 *         successful.
	 * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
	 * @see AutocompletePrediction#freeze()
	 */
	private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
		if (mGoogleApiClient.isConnected()) {
			Log.i(TAG, "Starting autocomplete query for: " + constraint);

			// Submit the query to the autocomplete API and retrieve a
			// PendingResult that will
			// contain the results when the query completes.
			PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
					.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), null,
							null);

			// This method should have been called off the main UI thread. Block
			// and wait for at most 60s
			// for a result from the API.
			AutocompletePredictionBuffer autocompletePredictions = results.await(60,
					TimeUnit.SECONDS);

			// Confirm that the query completed successfully, otherwise return
			// null
			final Status status = autocompletePredictions.getStatus();
			if (!status.isSuccess()) {
				/*Toast.makeText(cntx, "Error contacting API: " + status.toString(),
						Toast.LENGTH_SHORT).show();*/
				Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
				autocompletePredictions.release();
				return null;
			}

			Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
					+ " predictions.");

			// Freeze the results immutable representation that can be stored
			// safely.
			return DataBufferUtils.freezeAndClose(autocompletePredictions);
		}
		Log.e(TAG, "Google API client is not connected for autocomplete query.");
		return null;
	}

	private class GetData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... url) {
			mResultList = getAutocomplete(currentKeyword);
			/*try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			hideProgressDialog();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}
	
	private class ReadTask extends AsyncTask<String, Void, String> {
		String address = "";
		String mErrorMsg = "";
		
		public ReadTask(String address) {
			this.address = address;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				data = Utility.requestServerGet(url[0]);
			} catch (Exception e) {
				mErrorMsg = e.getMessage();
				return "";
			}
			return data;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			hideProgressDialog();
			
			if (TextUtils.isEmpty(result)) {
				showMessage(mErrorMsg);
			} else {
				try {
					JSONObject jsonResult = new JSONObject(result);
					if (jsonResult.has("results")) {
						JSONArray resultsArray = jsonResult.getJSONArray("results");
						
						if (resultsArray != null && resultsArray.length() > 0) {
							JSONObject featureObj = resultsArray.getJSONObject(0);
							if (featureObj.has("geometry")) {
								JSONObject geometryObj = featureObj.getJSONObject("geometry");
								if (geometryObj.has("location")) {
									JSONObject locationObj = geometryObj.getJSONObject("location");
									Intent intent = getIntent();
									intent.putExtra("new_location", address);
									intent.putExtra("lat", locationObj.getDouble("lat"));
									intent.putExtra("lng", locationObj.getDouble("lng"));
									setResult(RESULT_OK, intent);
									finish();
									return;
								}
							}
						}
					}
					msg(R.string.error_get_formatted_address);
				} catch(JSONException e) {
					msg(e.getMessage());
				} catch(Exception e) {
					msg(e.getMessage());
				}
			}
		}
	}
}	
