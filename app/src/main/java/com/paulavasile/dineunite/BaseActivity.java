package com.paulavasile.dineunite;

import java.util.List;

import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.utils.Utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {

	protected Context cntx;
	protected SharedPreferences settings;
	protected MyApplication myApp;

	protected ProgressDialog progress;

	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;

	protected Typeface spinnerTitleFont;
	protected Typeface spinnerItemsFont;

	// For exit action
	protected static final String GOTO_HOME_ACTION = "com.paulavasile.dineunite.gotohome";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION_CODES.FROYO < Build.VERSION.SDK_INT) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll()
					.build();
			StrictMode.setThreadPolicy(policy);
		}

		super.onCreate(savedInstanceState);

		cntx = this;
		myApp = (MyApplication) getApplication();

		imageLoader = myApp.getImageLoader();
		options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		settings = getSharedPreferences("puntatrackingrefs", 0);

		// Progress Dialog
		progress = new ProgressDialog(this);
		progress.setMessage(getString(R.string.loading));
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);

		// Spinner Fonts
		spinnerTitleFont = getRegularTypeface();
		spinnerItemsFont = getRegularTypeface();

		// Register Broadcast Receiver
		try {
			registerReceiver(goToHomeBroadcastReceiver, new IntentFilter(GOTO_HOME_ACTION));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// -- Broadcast Receiver to process the exit actions --
	private BroadcastReceiver goToHomeBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			boolean bExit = serviceIntent.getBooleanExtra("quitApp", false);
			if (bExit) {
				finish();
			} else {
				if (!isHomeScreen()) {
					finish();
				}
			}
		}
	};

	protected boolean isHomeScreen() {
		return false;
	}

	protected void exitMyApp() {
		Intent intent = new Intent(GOTO_HOME_ACTION);
		intent.putExtra("quitApp", true);
		sendBroadcast(intent);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(goToHomeBroadcastReceiver);
		super.onDestroy();
	}

	// ----------- ProgressDialog ---------------------------------------
	public void showProgressDialog() {
		if (progress.isShowing())
			return;

		progress.show();
		progress.setContentView(R.layout.dialog_loading);
	}

	public void hideProgressDialog() {
		try {
			if (progress.isShowing())
				progress.dismiss();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	protected boolean networkIsAvailable(boolean showErrorMessage) {
		if (Utility.hasConnection(cntx)) {
			return true;
		} else {
			if (showErrorMessage) {
				showMessage(R.string.network_not_available);
			}
			return false;
		}
	}

	// ----------- Show Messages ----------------------------------------
	public void showMessage(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
	}

	public void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public void msg(int msgId) {
		msg(getString(msgId));
	}

	public void msg(String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(cntx);
		alert.setMessage(msg);
		alert.setPositiveButton(getString(R.string.alert_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alert.show();
	}

	public void closeMsg(String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(cntx);
		alert.setMessage(msg);
		alert.setPositiveButton(getString(R.string.alert_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alert.show();
	}

	// Init Topbar
	protected void initTopbar() {
		View backBtn = findViewById(R.id.left_button);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}

		View shareBtn = findViewById(R.id.right_button);
		if (shareBtn != null) {
			shareBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					shareContent();
				}
			});
		}

		View exitBtn = findViewById(R.id.exit_button);
		if (exitBtn != null) {
			exitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					exitMyApp();
				}
			});
		}
	}

	public void shareContent() {
		// Content somethine here
		try {
			// Uri imageUri = Uri.fromFile(imageFile);
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
			shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
			// shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			shareIntent.setType("text/plain");
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			String chooserTitle = getResources().getString(R.string.menu_share);
			startActivity(Intent.createChooser(shareIntent, chooserTitle));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void shareContent(String msg) {
//		GlobalHelper.showAlertDialog(BaseActivity.this, msg);
		ShareDialog dialog = new ShareDialog(this, msg);
		dialog.show();
/*		// Content somethine here
		try {


			// Uri imageUri = Uri.fromFile(imageFile);
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
			shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
			// shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			shareIntent.setType("text/plain");
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			String chooserTitle = getResources().getString(R.string.menu_share);
			startActivity(Intent.createChooser(shareIntent, chooserTitle));
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

	// Hide Back Button
	protected void hideBackBtn() {
		View backBtn = findViewById(R.id.left_button);
		if (backBtn != null) {
			backBtn.setVisibility(View.GONE);
		}
	}

	// Hide Back Button
	protected void hideShareBtn() {
		View shareBtn = findViewById(R.id.right_button);
		if (shareBtn != null) {
			shareBtn.setVisibility(View.GONE);
		}
	}

	// ------ Set Titles -------------------------------------------------
	public void setTopTitle(int resId) {
		TextView titleTv = (TextView) findViewById(R.id.title_name);
		if (titleTv != null) {
			titleTv.setSelected(true);
			titleTv.setVisibility(View.VISIBLE);
			titleTv.setText(resId);
			setViewTypeface(titleTv, getRegularTypeface());
		}
	}

	protected void setTopTitle(String title) {
		TextView titleTv = (TextView) findViewById(R.id.title_name);
		if (titleTv != null) {
			titleTv.setSelected(true);
			titleTv.setVisibility(View.VISIBLE);
			titleTv.setText(title);
			setViewTypeface(titleTv, getRegularTypeface());
		}
	}

	// Remove EditText Keyboard
	public void hideKeyboard(EditText et) {
		if (et != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public DisplayImageOptions getImageOption() {
		return options;
	}

	// Set Font
	public void setViewTypeface(View view, Typeface font) {
		if (view == null || font == null)
			return;
		if (view instanceof TextView) {
			((TextView) view).setTypeface(font);
		} else if (view instanceof EditText) {
			((EditText) view).setTypeface(font);
		} else if (view instanceof Button) {
			((Button) view).setTypeface(font);
		}
	}


	public Typeface getRegularTypeface() {
		return myApp.getLatoRegularFont();
	}

	public class CustomFontSpinnerAdapter extends ArrayAdapter<String> {

		// (In reality I used a manager which caches the Typeface objects)
		CustomFontSpinnerAdapter(Context context, int resource, List<String> items) {
			super(context, resource, items);
		}

		// Affects default (closed) state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView, parent);
			view.setTypeface(spinnerTitleFont);
			return view;
		}

		// Affects opened state of the spinner
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getDropDownView(position, convertView, parent);
			view.setTypeface(spinnerItemsFont);
			return view;
		}
	}

	protected void saveUserInformation(String responseString) {
		try {
			JSONObject responseObj = new JSONObject(responseString);
			// UserID
			if (responseObj.has("UserID")) {
				settings.edit().putInt("UserID", responseObj.getInt("UserID")).commit();
			}

			// TitleOptionID
			if (responseObj.has("TitleOptionID")) {
				settings.edit().putString("TitleOptionID", responseObj.getString("TitleOptionID"))
						.commit();
			}

			// Title
			if (responseObj.has("Title")) {
				settings.edit().putString("Title", responseObj.getString("Title")).commit();
			}

			// FirstName
			if (responseObj.has("FirstName")) {
				settings.edit().putString("FirstName", responseObj.getString("FirstName")).commit();
			}

			// LastName
			if (responseObj.has("LastName")) {
				settings.edit().putString("LastName", responseObj.getString("LastName")).commit();
			}

			// Email
			if (responseObj.has("Email")) {
				settings.edit().putString("Email", responseObj.getString("Email")).commit();
			}

			// Phone
			if (responseObj.has("Phone")) {
				settings.edit().putString("Phone", responseObj.getString("Phone")).commit();
			}

			// Gender
			if (responseObj.has("Gender") && !responseObj.isNull("Gender")) {
				settings.edit().putString("Gender", responseObj.getString("Gender")).commit();
			} else {
				settings.edit().putString("Gender", "").commit();
			}

			// HomePostCode
			if (responseObj.has("HomePostCode")) {
				settings.edit().putString("HomePostCode", responseObj.getString("HomePostCode"))
						.commit();
			}

			// WorkPostCode
			if (responseObj.has("WorkPostCode")) {
				settings.edit().putString("WorkPostCode", responseObj.getString("WorkPostCode"))
						.commit();
			}

			// HomeAddress
			if (responseObj.has("HomeAddress")) {
				settings.edit().putString("HomeAddress", responseObj.getString("HomeAddress"))
						.commit();
			}

			// HomeLatitude
			if (responseObj.has("HomeLatitude")) {
				settings.edit().putString("HomeLatitude", responseObj.getString("HomeLatitude"))
						.commit();
			}

			// HomeLongitude
			if (responseObj.has("HomeLongitude")) {
				settings.edit().putString("HomeLongitude", responseObj.getString("HomeLongitude"))
						.commit();
			}

			// WorkAddress
			if (responseObj.has("WorkAddress")) {
				settings.edit().putString("WorkAddress", responseObj.getString("WorkAddress"))
						.commit();
			}

			// HomeLatitude
			if (responseObj.has("WorkLatitude")) {
				settings.edit().putString("WorkLatitude", responseObj.getString("WorkLatitude"))
						.commit();
			}

			// HomeLongitude
			if (responseObj.has("WorkLongitude")) {
				settings.edit().putString("WorkLongitude", responseObj.getString("WorkLongitude"))
						.commit();
			}

			// Age
			if (responseObj.has("Age")) {
				settings.edit().putInt("Age", responseObj.getInt("Age")).commit();
			}

			// Age
			if (responseObj.has("DOB")) {
				settings.edit().putString("DOB", responseObj.getString("DOB")).commit();
			}

			// FavoriteCuisineType
			if (responseObj.has("FavoriteCuisineType")) {
				settings.edit()
						.putString("FavoriteCuisineType",
								responseObj.getString("FavoriteCuisineType")).commit();
				settings.edit()
						.putInt("CuisineTypeID",
								responseObj.getJSONObject("FavoriteCuisineType").getInt(
										"CuisineTypeID")).commit();

			}

			// RelationshipStatus
			if (responseObj.has("RelationshipStatus")) {
				settings.edit()
						.putString("RelationshipStatus",
								responseObj.getString("RelationshipStatus")).commit();
				settings.edit()
						.putInt("RelationshipStatusID",
								responseObj.getJSONObject("RelationshipStatus").getInt(
										"RelationshipStatusID")).commit();
			}

			// LookingFor
			if (responseObj.has("LookingFor")) {
				settings.edit().putString("LookingFor", responseObj.getString("LookingFor"))
						.commit();
				settings.edit()
						.putInt("LookingForID",
								responseObj.getJSONObject("LookingFor").getInt("LookingForID"))
						.commit();
			}

			// Education
			if (responseObj.has("Education")) {
				settings.edit().putString("Education", responseObj.getString("Education")).commit();
				settings.edit()
						.putInt("EducationID",
								responseObj.getJSONObject("Education").getInt("EducationID"))
						.commit();
			}

			// Ethnicity
			if (responseObj.has("Ethnicity")) {
				settings.edit().putString("Ethnicity", responseObj.getString("Ethnicity")).commit();
				settings.edit()
						.putInt("EthnicityID",
								responseObj.getJSONObject("Ethnicity").getInt("EthnicityID"))
						.commit();
			}

			// Language
			if (responseObj.has("Language")) {
				settings.edit().putString("Language", responseObj.getString("Language")).commit();
				settings.edit()
						.putInt("LanguageID",
								responseObj.getJSONObject("Language").getInt("LanguageID"))
						.commit();
			}

			// AboutMe
			if (responseObj.has("AboutMe")) {
				settings.edit().putString("AboutMe", responseObj.getString("AboutMe")).commit();
			}

			// AboutMe
			if (responseObj.has("Images")) {
				settings.edit().putString("Images", responseObj.getString("Images")).commit();
				JSONArray imageArray = responseObj.getJSONArray("Images");
				if (imageArray.length() > 0) {
					JSONObject imageObj = imageArray.getJSONObject(0);
					settings.edit().putInt("ImageID", imageObj.getInt("ImageID")).commit();
					settings.edit().putString("ImageURL", imageObj.getString("ImageURL")).commit();
					settings.edit().putBoolean("IsPrimary", imageObj.getBoolean("IsPrimary")).commit();
				}
			}

			// Profile Complete Percentage
			if (responseObj.has("ProfileCompletionPercentage")) {
				settings.edit()
						.putFloat("ProfileCompletionPercentage",
								(float) responseObj.getDouble("ProfileCompletionPercentage"))
						.commit();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean wasFilledMandatoryInfo() {
		if (TextUtils.isEmpty(settings.getString("FirstName", ""))) {
			return false;
		}
		return true;
	}
	
	// Get Map Place Image Url
	public String getMapPlaceImageUrl(double lat, double lon) {
		// http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=720x360&markers=size:large%7Ccolor:green%7C51.499983,-0.125849&sensor=false
		// Other params
		int scale = 17;
		int width = 720;
		int height = 360;
		String mapPlaceImageUrl = String
				.format("http://maps.googleapis.com/maps/api/staticmap?zoom=%d&size=%dx%d&markers=size:large|color:red|%f,%f&sensor=false",
						scale, width, height, lat, lon);
		return mapPlaceImageUrl;
	}

	public void findDirection(double dLat, double dLon) {
		try {
			String urlString = String.format("http://maps.google.com/maps?daddr=%f,%f", dLat, dLon);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(urlString));
			if (isPackageExisted("com.google.android.apps.maps")) {
				intent.setPackage("com.google.android.apps.maps");
			}
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void findDirection(double sLat, double sLon, double dLat, double dLon) {
		try {
			String urlString = String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
					sLat, sLon, dLat, dLon);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(urlString));
			if (isPackageExisted("com.google.android.apps.maps")) {
				intent.setPackage("com.google.android.apps.maps");
			}
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean isPackageExisted(String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;

		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage))
				return true;
		}
		return false;
	}

}
