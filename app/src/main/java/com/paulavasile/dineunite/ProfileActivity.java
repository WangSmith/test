package com.paulavasile.dineunite;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.data.SearchItem;
import com.paulavasile.dineunite.data.UserProfile;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.DateUtil;
import com.paulavasile.dineunite.utils.UploadPhotoTask;
import com.paulavasile.dineunite.utils.Utility;
import com.paulavasile.dineunite.views.CustomSpinner;
import com.paulavasile.dineunite.views.OnSpinnerEventsListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

public class ProfileActivity extends BaseActivity implements OnClickListener,
		OnSpinnerEventsListener {

	// Profile Progress
	ProgressBar profileProgressBar;
	TextView profileProgressTv;

	// User Photo
	ImageView profilePhotoIv;
	String profileImageFile;
	boolean isLocalFile;
	String profileImageUrl;

	// Personal Info
	TextView personalInfoTitle;

	TextView firstNameValueTv;

	TextView lastNameValueTv;

	TextView genderValueTv;

	TextView ageValueTv;
	Calendar birthdayDate = Calendar.getInstance();
	String birthdayDateValue;

	TextView aboutYouValueTv;

	CustomSpinner relationshipStatusList;
	ArrayList<SearchItem> relationshipOptionItems = new ArrayList<SearchItem>();

	CustomSpinner lookingForList;
	ArrayList<SearchItem> lookingForOptionItems = new ArrayList<SearchItem>();

	CustomSpinner favouriteCuisineList;
	ArrayList<SearchItem> favouriteCuisineOptionItems = new ArrayList<SearchItem>();

	CustomSpinner ethnicityList;
	ArrayList<SearchItem> ethnicityOptionItems = new ArrayList<SearchItem>();

	CustomSpinner languageList;
	ArrayList<SearchItem> languageOptionItems = new ArrayList<SearchItem>();

	CustomSpinner educationList;
	ArrayList<SearchItem> educationOptionItems = new ArrayList<SearchItem>();

	// Contact Information
	TextView contactInfoTitle;

	TextView emailValueTv;

	TextView mobileValueTv;

	// Location Information
	TextView homeAddressValueTv;
	double homeLat, homeLng;

	TextView workAddressValueTv;
	double workLat, workLng;

	private static final int REQUEST_INPUT_FIELD = 100;
	private static final int REQUEST_SELECT_GENDER = 101;
	private static final int REQUEST_SELECT_HOME_LOCATION = 102;
	private static final int REQUEST_SELECT_WORK_LOCATION = 103;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		initTopbar();
		setTopTitle(R.string.menu_my_profile);
		hideShareBtn();

		Typeface boldFont = getRegularTypeface();
		Typeface titleFont = getRegularTypeface();
		setViewTypeface(findViewById(R.id.profileProgressTv), titleFont);

		setViewTypeface(findViewById(R.id.personalInfoTitle), boldFont);
		setViewTypeface(findViewById(R.id.firstNameTitle), titleFont);
		setViewTypeface(findViewById(R.id.lastNameTitle), titleFont);
		setViewTypeface(findViewById(R.id.genderTitle), titleFont);
		setViewTypeface(findViewById(R.id.ageTitle), titleFont);
		setViewTypeface(findViewById(R.id.aboutYouTitle), titleFont);
		setViewTypeface(findViewById(R.id.relationShipTitle), titleFont);
		setViewTypeface(findViewById(R.id.lookingForTitle), titleFont);
		setViewTypeface(findViewById(R.id.favouriteCuisineTitle), titleFont);
		setViewTypeface(findViewById(R.id.ethnicityTitle), titleFont);
		setViewTypeface(findViewById(R.id.languageTitle), titleFont);
		setViewTypeface(findViewById(R.id.educationTitle), titleFont);

		setViewTypeface(findViewById(R.id.contactInfoTitle), boldFont);
		setViewTypeface(findViewById(R.id.emailTitle), titleFont);
		setViewTypeface(findViewById(R.id.mobileTitle), titleFont);

		setViewTypeface(findViewById(R.id.locationInfoTitle), boldFont);
		setViewTypeface(findViewById(R.id.homeAddressTitle), titleFont);
		setViewTypeface(findViewById(R.id.workAddressTitle), titleFont);
		setViewTypeface(findViewById(R.id.btnSaveChanges), titleFont);

		profileProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
		profileProgressTv = (TextView) findViewById(R.id.profileProgressTv);

		profilePhotoIv = (ImageView) findViewById(R.id.profilePhotoIv);
		profilePhotoIv.setOnClickListener(this);

		firstNameValueTv = (TextView) findViewById(R.id.firstNameValueTv);
		firstNameValueTv.setTag(UserProfile.TYPE_FIRSTNAME);
		firstNameValueTv.setOnClickListener(fieldInputListener);

		lastNameValueTv = (TextView) findViewById(R.id.lastNameValueTv);
		lastNameValueTv.setTag(UserProfile.TYPE_LASTNAME);
		lastNameValueTv.setOnClickListener(fieldInputListener);

		genderValueTv = (TextView) findViewById(R.id.genderValueTv);
		genderValueTv.setTag(UserProfile.TYPE_GENDER);
		genderValueTv.setOnClickListener(this);

		ageValueTv = (TextView) findViewById(R.id.ageValueTv);
		ageValueTv.setTag(UserProfile.TYPE_BIRTHDAY);
		ageValueTv.setOnClickListener(this);

		aboutYouValueTv = (TextView) findViewById(R.id.aboutYouValueTv);
		aboutYouValueTv.setTag(UserProfile.TYPE_ABOUTYOU);
		aboutYouValueTv.setOnClickListener(fieldInputListener);

		relationshipStatusList = (CustomSpinner) findViewById(R.id.relationshipStatusList);
		relationshipStatusList.setSpinnerEventsListener(this);

		lookingForList = (CustomSpinner) findViewById(R.id.lookingForList);
		lookingForList.setSpinnerEventsListener(this);

		favouriteCuisineList = (CustomSpinner) findViewById(R.id.favouriteCuisineList);
		favouriteCuisineList.setSpinnerEventsListener(this);

		ethnicityList = (CustomSpinner) findViewById(R.id.ethnicityList);
		ethnicityList.setSpinnerEventsListener(this);

		languageList = (CustomSpinner) findViewById(R.id.languageList);
		languageList.setSpinnerEventsListener(this);

		educationList = (CustomSpinner) findViewById(R.id.educationList);
		educationList.setSpinnerEventsListener(this);

		emailValueTv = (TextView) findViewById(R.id.emailValueTv);
		emailValueTv.setTag(UserProfile.TYPE_EMAIL);
		emailValueTv.setOnClickListener(fieldInputListener);

		mobileValueTv = (TextView) findViewById(R.id.mobileValueTv);
		mobileValueTv.setTag(UserProfile.TYPE_MOBILE);
//		mobileValueTv.setOnClickListener(fieldInputListener);
		mobileValueTv.setEnabled(false);

		homeAddressValueTv = (TextView) findViewById(R.id.homeAddressValueTv);
		homeAddressValueTv.setTag(UserProfile.TYPE_HOMEADDRESS);
		homeAddressValueTv.setOnClickListener(this);

		workAddressValueTv = (TextView) findViewById(R.id.workAddressValueTv);
		workAddressValueTv.setTag(UserProfile.TYPE_WORKADDRESS);
		workAddressValueTv.setOnClickListener(this);

		findViewById(R.id.btnSaveChanges).setOnClickListener(this);
		findViewById(R.id.btnCancelBtn).setOnClickListener(this);
		// Show Current Profile
		showCurrentProfileInformation();

		// Show select options
		updateSearchQueries();

		// Load select options
		if(GlobalHelper.isConnectingToInternet(ProfileActivity.this))
		{
			loadSearchList();
		}
	}

	private boolean updateSearchQueries() {
		String searchQueriesString = settings.getString("profile_queries", "");
		if (TextUtils.isEmpty(searchQueriesString))
			return false;

		try {
			JSONObject responseObj = new JSONObject(searchQueriesString);

			// Relationship Status
			if (responseObj.has("RelationshipsStatus")) {
				JSONArray relationshipStatusArray = responseObj.getJSONArray("RelationshipsStatus");
				if (relationshipStatusArray.length() > 0) {
					relationshipOptionItems.clear();
					ArrayList<String> relationshipTitles = new ArrayList<String>();
					relationshipTitles.add(getString(R.string.hint_select));

					int selectItemPos = 0;
					int RelationshipStatusID = settings.getInt("RelationshipStatusID", 0);

					for (int i = 0; i < relationshipStatusArray.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = relationshipStatusArray.getJSONObject(i).getInt(
								"RelationshipStatusID");
						newItem.title = relationshipStatusArray.getJSONObject(i).getString(
								"RelationshipStatusName");
						relationshipOptionItems.add(newItem);
						relationshipTitles.add(newItem.title);

						if (newItem.id == RelationshipStatusID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, relationshipTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					relationshipStatusList.setAdapter(optionsAdapter);
					relationshipStatusList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}

			// User is Looking For
			if (responseObj.has("LookingForOptions")) {
				JSONArray lookingForArray = responseObj.getJSONArray("LookingForOptions");
				if (lookingForArray.length() > 0) {
					lookingForOptionItems.clear();
					ArrayList<String> lookingForTitles = new ArrayList<String>();
					lookingForTitles.add(getString(R.string.hint_select));

					int selectItemPos = 0;
					int LookingForID = settings.getInt("LookingForID", 0);

					for (int i = 0; i < lookingForArray.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = lookingForArray.getJSONObject(i).getInt("LookingForID");
						newItem.title = lookingForArray.getJSONObject(i)
								.getString("LookingForName");
						lookingForOptionItems.add(newItem);
						lookingForTitles.add(newItem.title);

						if (newItem.id == LookingForID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, lookingForTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					lookingForList.setAdapter(optionsAdapter);
					lookingForList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}

			// CuisineTypes
			if (responseObj.has("CuisineTypes")) {
				JSONArray educationLevelArry = responseObj.getJSONArray("CuisineTypes");
				if (educationLevelArry.length() > 0) {
					favouriteCuisineOptionItems.clear();
					ArrayList<String> cuisineTypesTitles = new ArrayList<String>();
					cuisineTypesTitles.add(getString(R.string.hint_idrathernotsay));

					int selectItemPos = 0;
					int CuisineTypeID = settings.getInt("CuisineTypeID", 0);

					for (int i = 0; i < educationLevelArry.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = educationLevelArry.getJSONObject(i).getInt("CuisineTypeID");
						newItem.title = educationLevelArry.getJSONObject(i).getString(
								"CuisineTypeName");
						favouriteCuisineOptionItems.add(newItem);
						cuisineTypesTitles.add(newItem.title);

						if (newItem.id == CuisineTypeID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, cuisineTypesTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					favouriteCuisineList.setAdapter(optionsAdapter);
					favouriteCuisineList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}

			// Ethnicities
			if (responseObj.has("Ethnicities")) {
				JSONArray ethnicityArray = responseObj.getJSONArray("Ethnicities");
				if (ethnicityArray.length() > 0) {
					ethnicityOptionItems.clear();
					ArrayList<String> ethnicityTitles = new ArrayList<String>();
					ethnicityTitles.add(getString(R.string.hint_idrathernotsay));

					int selectItemPos = 0;
					int EthnicityID = settings.getInt("EthnicityID", 0);

					for (int i = 0; i < ethnicityArray.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = ethnicityArray.getJSONObject(i).getInt("EthnicityID");
						newItem.title = ethnicityArray.getJSONObject(i).getString("EthnicityName");
						ethnicityOptionItems.add(newItem);
						ethnicityTitles.add(newItem.title);

						if (newItem.id == EthnicityID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, ethnicityTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					ethnicityList.setAdapter(optionsAdapter);
					ethnicityList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}

			// Language
			if (responseObj.has("Languages")) {
				JSONArray ethnicityArray = responseObj.getJSONArray("Languages");
				if (ethnicityArray.length() > 0) {
					languageOptionItems.clear();
					ArrayList<String> languageTitles = new ArrayList<String>();
					languageTitles.add(getString(R.string.hint_idrathernotsay));

					int selectItemPos = 0;
					int LanguageID = settings.getInt("LanguageID", 0);

					for (int i = 0; i < ethnicityArray.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = ethnicityArray.getJSONObject(i).getInt("LanguageID");
						newItem.title = ethnicityArray.getJSONObject(i).getString("LanguageName");
						languageOptionItems.add(newItem);
						languageTitles.add(newItem.title);

						if (newItem.id == LanguageID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, languageTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					languageList.setAdapter(optionsAdapter);
					languageList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}

			// EducationLevels
			if (responseObj.has("EducationLevels")) {
				JSONArray educationArray = responseObj.getJSONArray("EducationLevels");
				if (educationArray.length() > 0) {
					educationOptionItems.clear();
					ArrayList<String> educationTitles = new ArrayList<String>();
					educationTitles.add(getString(R.string.hint_idrathernotsay));
					
					int selectItemPos = 0;
					int EducationID = settings.getInt("EducationID", 0);

					for (int i = 0; i < educationArray.length(); i++) {
						SearchItem newItem = new SearchItem();
						newItem.id = educationArray.getJSONObject(i).getInt("EducationID");
						newItem.title = educationArray.getJSONObject(i).getString("EducationName");
						educationOptionItems.add(newItem);
						educationTitles.add(newItem.title);

						if (newItem.id == EducationID) {
							selectItemPos = i + 1;
						}
					}

					CustomFontSpinnerAdapter optionsAdapter = new CustomFontSpinnerAdapter(cntx,
							R.layout.spinner_item, educationTitles);
					optionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
					educationList.setAdapter(optionsAdapter);
					educationList.setSelection(selectItemPos);
					// favouriteCuisineList.setOnItemSelectedListener(optionsChangedListener);
				}
			}
		} catch (JSONException e) {
			showMessage(e.getMessage());
		}

		return true;
	}

	private void loadSearchList() {
		SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.GET_ALL_UTILS_FOR_PROFILE);
		String soapAction = Utility.NAMESPACE + Utility.GET_ALL_UTILS_FOR_PROFILE;
		request.addProperty("UserID", settings.getInt("UserID", 0));

		AnyTask getNetworkTask = new AnyTask(cntx, soapAction, request, new AnyTaskCallback() {

			@Override
			public void onResult(boolean success, Object result) {
				hideProgressDialog();
				if (success) {
					SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

					SoapPrimitive response;
					try {
						response = (SoapPrimitive) envelope.getResponse();
						String responseString = response.toString();
						settings.edit().putString("profile_queries", responseString).commit();
						updateSearchQueries();

						showCurrentProfileInformation();

					} catch (SoapFault e) {
						String errorMsg = (String) e.faultstring;
						if (TextUtils.isEmpty(errorMsg)) {
							errorMsg = getString(R.string.error_loading);
						}
						closeMsg(errorMsg);
					} catch (Exception e) {
						String errorMsg = (String) e.getMessage();
						if (TextUtils.isEmpty(errorMsg)) {
							errorMsg = getString(R.string.error_loading);
						}
						closeMsg(errorMsg);
					}
				} else {
					String errorMsg = (String) result;
					if (TextUtils.isEmpty(errorMsg)) {
						errorMsg = getString(R.string.error_loading);
					}
					closeMsg(errorMsg);
				}
			}
		}, false);
		showProgressDialog();
		BackgroundProcessor.getBackgroundProcessor(this).setPerformTask(getNetworkTask);
	}

	private void showCurrentProfileInformation() {

		// Show Profile Percentage
		float percentage = settings.getFloat("ProfileCompletionPercentage", 0);
		//profileProgressTv.setText(String.format("%.2f%% Completed", percentage));
		profileProgressTv.setText(String.format("%d%% Completed", (int) percentage));
		profileProgressBar.setProgress((int) percentage);

		showProfileImage();

		// Show General Information
		firstNameValueTv.setText(settings.getString("FirstName", ""));
		lastNameValueTv.setText(settings.getString("LastName", ""));

		// Show Contact Information
		emailValueTv.setText(settings.getString("Email", ""));
		mobileValueTv.setText(settings.getString("Phone", ""));

		// Show Gender
		String gender = settings.getString("Gender", "");
		if (!TextUtils.isEmpty(gender)) {
			genderValueTv.setText(gender.equalsIgnoreCase("M") ? "Male" : "Female");
		}

		// Show Address
		homeAddressValueTv.setText(settings.getString("HomeAddress", ""));
		workAddressValueTv.setText(settings.getString("WorkAddress", ""));

		// Show Day of Birthday
		String dob = settings.getString("DOB", "");
		if (!TextUtils.isEmpty(dob)) {
			birthdayDateValue = dob;
			Date birthDay = DateUtil.parseDataFromFormat12(birthdayDateValue);
			birthdayDate.setTime(birthDay);
			ageValueTv.setText(DateUtil.toStringFormat_11(birthDay));
		}

		// Show aboutme
		aboutYouValueTv.setText(settings.getString("AboutMe", ""));
	}

	private void showProfileImage() {
		profileImageUrl = settings.getString("ImageURL", "");
		if (!TextUtils.isEmpty(profileImageUrl)) {
			imageLoader.displayImage(profileImageUrl, profilePhotoIv, options);
		}
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.profilePhotoIv) {
			// Profile Image
			if(GlobalHelper.isConnectingToInternet(ProfileActivity.this))
			{
				showImageSourceDialog();
			}
			else
			{
				closeMsg("We can't exchange/create profile photo because your network is disconnected.\n");
			}
		} else if (viewId == R.id.genderValueTv) {
			//showGenderPickDialog();
			String currentGenderValue = genderValueTv.getText().toString();
			Intent intent = new Intent(cntx, SelectGenderActivity.class);
			intent.putExtra("gender", currentGenderValue);
			startActivityForResult(intent, REQUEST_SELECT_GENDER);
		} else if (viewId == R.id.ageValueTv) {
			// showDatePickDialog();
			showNormalDataPickDialog();
		} else if (viewId == R.id.homeAddressValueTv) {
			Intent intent = new Intent(cntx, AddressSelectActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_HOME_LOCATION);
		} else if (viewId == R.id.workAddressValueTv) {
			Intent intent = new Intent(cntx, AddressSelectActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_WORK_LOCATION);
		} else if (viewId == R.id.btnSaveChanges) {
			if(GlobalHelper.isConnectingToInternet(ProfileActivity.this))
			{
				saveMyProfile();
			}
			else
			{
				closeMsg("We can't save profile information to your server because your network is disconnected.\n");
			}
		} else if(viewId == R.id.btnCancelBtn) {
			finish();
		}
	}

	View.OnClickListener fieldInputListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			int typeField = (int) view.getTag();
			String initValue = "";
			if (typeField == UserProfile.TYPE_FIRSTNAME) {
				initValue = firstNameValueTv.getText().toString();
			} else if (typeField == UserProfile.TYPE_LASTNAME) {
				initValue = lastNameValueTv.getText().toString();
			} else if (typeField == UserProfile.TYPE_ABOUTYOU) {
				initValue = aboutYouValueTv.getText().toString();
			} else if (typeField == UserProfile.TYPE_EMAIL) {
				initValue = emailValueTv.getText().toString();
			} else if (typeField == UserProfile.TYPE_MOBILE) {
				initValue = mobileValueTv.getText().toString();
			}

			Intent intent = new Intent(cntx, ProfileFieldInputActivity.class);
			intent.putExtra("profile_type", typeField);
			intent.putExtra("profile_value", initValue);
			startActivityForResult(intent, REQUEST_INPUT_FIELD);
		}
	};

	private void showGenderPickDialog() {
		final String items[] = { "Male", "Female" };
		AlertDialog.Builder ab = new AlertDialog.Builder(cntx);
		ab.setTitle("Select gender");
		String currentGenderValue = genderValueTv.getText().toString();
		ab.setSingleChoiceItems(items, items[1].equalsIgnoreCase(currentGenderValue) ? 1 : 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						genderValueTv.setText(items[whichButton]);
						dialog.dismiss();
					}
				});

		// Chnage Title color and devider color
		Dialog mPopupDlg = ab.show();
		int textViewId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/alertTitle", null, null);
		TextView titleTv = (TextView) mPopupDlg.findViewById(textViewId);
		if (titleTv != null) {
			titleTv.setTextColor(getResources().getColor(R.color.item_title));
		}

		int dividerId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = mPopupDlg.findViewById(dividerId);
		if (divider != null) {
			divider.setBackgroundColor(getResources().getColor(R.color.item_title));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showDatePickDialog() {
		DatePickerDialog dpd = DatePickerDialog.newInstance(
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePickerDialog view, int year, int monthOfYear,
							int dayOfMonth) {
						birthdayDate.set(Calendar.YEAR, year);
						birthdayDate.set(Calendar.MONTH, monthOfYear);
						birthdayDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

						birthdayDateValue = DateUtil.toStringFormat_12(birthdayDate.getTime());
						String birthDate = DateUtil.toStringFormat_11(birthdayDate.getTime());
						ageValueTv.setText(birthDate);
					}

				}, birthdayDate.get(Calendar.YEAR), birthdayDate.get(Calendar.MONTH), birthdayDate
						.get(Calendar.DAY_OF_MONTH));

		dpd.setThemeDark(false);
		dpd.vibrate(false);
		dpd.dismissOnPause(true);
		dpd.showYearPickerFirst(true);
		dpd.setAccentColor(Color.parseColor("#f78413"));
		dpd.show(getFragmentManager(), "Datepickerdialog");
	}

	private void showNormalDataPickDialog() {
		android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(cntx,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						birthdayDate.set(Calendar.YEAR, year);
						birthdayDate.set(Calendar.MONTH, monthOfYear);
						birthdayDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

						birthdayDateValue = DateUtil.toStringFormat_12(birthdayDate.getTime());
						String birthDate = DateUtil.toStringFormat_11(birthdayDate.getTime());
						ageValueTv.setText(birthDate);
					}
				}, birthdayDate.get(Calendar.YEAR), birthdayDate.get(Calendar.MONTH), birthdayDate
						.get(Calendar.DAY_OF_MONTH));

		dialog.setTitle("Birthday");
		dialog.show();
	}

	// ---------------------- User Profile Photo ------------------------
	String strCameraOutputFilePath;
	String strCropedFilePath;
	private static final int FROM_CAMERA_REQCODE = 1000;
	private static final int FROM_ALBUM_REQCODE = 1100;
	private static final int CROP_REQCODE = 1200;

	// image crop parameter
	private static final String TYPE_IMAGE = "image/*";
	private static final int PROFILE_IMAGE_ASPECT_X = 5;
	private static final int PROFILE_IMAGE_ASPECT_Y = 5;
	private static final int PROFILE_IMAGE_OUTPUT_X = 300;
	private static final int PROFILE_IMAGE_OUTPUT_Y = 300;

	private void showImageSourceDialog() {

		final String items[] = { "Choose from gallery", "Take photo", "Clear profile picture" };

		AlertDialog.Builder ab = new AlertDialog.Builder(cntx);
		ab.setTitle("Change profile picture");
		
		/*ArrayList<String> itemsArray = new ArrayList<>();
		for (String item : items)
			itemsArray.add(item);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(cntx,
				android.R.layout.simple_list_item_1, itemsArray);
		ab.setAdapter(adapter, new DialogInterface.OnClickListener() {*/
		
		ab.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (whichButton == 0) {
					Intent galleryIntent = new Intent(Intent.ACTION_PICK,
							Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(galleryIntent, FROM_ALBUM_REQCODE);
				} else if (whichButton == 1) {
					/* start Camera Intent */
					File cameraOutputFile = Utility.makeCameraOutputFile();
					if (cameraOutputFile == null) {
						return;
					}

					strCameraOutputFilePath = cameraOutputFile.getAbsolutePath();

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraOutputFile));
					startActivityForResult(intent, FROM_CAMERA_REQCODE);
				} else if (whichButton == 2) {
					// Clear profile picture
					// -------------- Testing --------------------

					int imageID = settings.getInt("ImageID", 0);
					if (imageID > 0) {
						SoapObject request = new SoapObject(Utility.NAMESPACE,
								Utility.DELETE_PHOTO);
						String soapAction = Utility.NAMESPACE + Utility.DELETE_PHOTO;
						request.addProperty("UserID", settings.getInt("UserID", 0));
						request.addProperty("ImageID", imageID);
						// --------------------------------------------

						AnyTask getNetworkTask = new AnyTask(cntx, soapAction, request,
								new AnyTaskCallback() {

									@Override
									public void onResult(boolean success, Object result) {
										hideProgressDialog();
										if (success) {
											SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

											SoapPrimitive response;
											try {
												response = (SoapPrimitive) envelope.getResponse();
												String responseString = response.toString();
												JSONObject respObj = new JSONObject(responseString);
												boolean error = false;
												String message = getString(R.string.failed_clear_profile_photo);

												if (respObj.has("IsError")
														&& !respObj.isNull("IsError")) {
													error = respObj.getBoolean("IsError");
													if (respObj.has("Message"))
														message = respObj.getString("Message");
												}

												if (error) {
													showMessage(message);
												} else {
													profileImageUrl = "";
													profileImageFile = "";
													settings.edit().putInt("ImageID", 0).commit();
													settings.edit().putString("ImageURL", "")
															.commit();
													profilePhotoIv
															.setImageResource(R.drawable.profile_placeholder);
												}
											} catch (SoapFault e) {
												String errorMsg = (String) e.faultstring;
												if (TextUtils.isEmpty(errorMsg)) {
													errorMsg = getString(R.string.error_loading);
												}
												showMessage(errorMsg);
											} catch (Exception e) {
												String errorMsg = (String) e.getMessage();
												if (TextUtils.isEmpty(errorMsg)) {
													errorMsg = getString(R.string.error_loading);
												}
												showMessage(errorMsg);
											}
										} else {
											String errorMsg = (String) result;
											if (TextUtils.isEmpty(errorMsg)) {
												errorMsg = getString(R.string.error_loading);
											}
											showMessage(errorMsg);
										}
									}
								}, false);
						showProgressDialog();
						BackgroundProcessor.getBackgroundProcessor(ProfileActivity.this).setPerformTask(getNetworkTask);
					} else {
						profilePhotoIv.setImageResource(R.drawable.profile_placeholder);
						profileImageUrl = "";
						profileImageFile = "";
					}
				}
				dialog.dismiss();
			}
		});

		// Chnage Title color and devider color
		Dialog mPopupDlg = ab.show();
		int textViewId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/alertTitle", null, null);
		TextView titleTv = (TextView) mPopupDlg.findViewById(textViewId);
		if (titleTv != null) {
			titleTv.setTextColor(getResources().getColor(R.color.item_title));
		}

		int dividerId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = mPopupDlg.findViewById(dividerId);
		if (divider != null) {
			divider.setBackgroundColor(getResources().getColor(R.color.item_title));
		}
	}

	// Mandatory Information
	String firstname;
	String gender;
	String age;
	String email;
	String mobile;
	String homeAddress;

	private void saveMyProfile() {
		// Check mandatory information
		// Check first name
		firstname = firstNameValueTv.getText().toString();
		if (TextUtils.isEmpty(firstname)) {
			showMessage(R.string.error_add_firstname);
			return;
		}

		// Check gender
		gender = genderValueTv.getText().toString();
		if (TextUtils.isEmpty(gender)) {
			showMessage(R.string.error_select_gender);
			return;
		}

		// Check age
		age = ageValueTv.getText().toString();
		if (TextUtils.isEmpty(age)) {
			showMessage(R.string.error_add_age);
			return;
		}

		// Check email
		email = emailValueTv.getText().toString();
		if (TextUtils.isEmpty(email)) {
			showMessage(R.string.error_add_email);
			return;
		}

		// Check mobile
		mobile = mobileValueTv.getText().toString();
		if (TextUtils.isEmpty(mobile)) {
			showMessage(R.string.error_add_mobile);
			return;
		}
		
		homeAddress = homeAddressValueTv.getText().toString().trim();
		
		if (TextUtils.isEmpty(homeAddress)) {
			showMessage(R.string.error_add_home_address);
			return;
		}
		
		// Optional Information
		String lastname = lastNameValueTv.getText().toString();
		String aboutyou = aboutYouValueTv.getText().toString();

		int relationshipStatusPos = relationshipStatusList.getSelectedItemPosition();
		int lookingforPos = lookingForList.getSelectedItemPosition();
		int favouriteCuisinePos = favouriteCuisineList.getSelectedItemPosition();
		int ethnicityPos = ethnicityList.getSelectedItemPosition();
		int languagesPos = languageList.getSelectedItemPosition();
		int educationPos = educationList.getSelectedItemPosition();

		String workAddress = workAddressValueTv.getText().toString();

		SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.SAVE_DINNER_PROFILE);
		String soapAction = Utility.NAMESPACE + Utility.SAVE_DINNER_PROFILE;

		request.addProperty("UserID", settings.getInt("UserID", 0));
		request.addProperty("FirstName", firstname);
		request.addProperty("LastName", lastname);
		request.addProperty("Email", email);
		request.addProperty("Mobile", mobile);

		request.addProperty("Gender", gender.equalsIgnoreCase("Male") ? "M" : "F");
		request.addProperty("DOB", birthdayDateValue);
		request.addProperty("HomePostCode", "");
		request.addProperty("WorkPostCode", "");
		request.addProperty("Address1", homeAddress);
		request.addProperty("Address2", workAddress);
		if (favouriteCuisinePos> 0 && favouriteCuisineOptionItems.size() > 0) {
			request.addProperty("FavouriteCuisineTypeID",
					favouriteCuisineOptionItems.get(favouriteCuisinePos - 1).id);
		} else {
			request.addProperty("FavouriteCuisineTypeID", 0);
		}
		
		if (relationshipStatusPos > 0 && relationshipOptionItems.size() > 0) {
			request.addProperty("RelationshipStatusID",
					relationshipOptionItems.get(relationshipStatusPos - 1).id);
			Log.e("Relationship", relationshipOptionItems.get(relationshipStatusPos - 1).title);
		} else {
			request.addProperty("RelationshipStatusID", 0);
		}
		
		if (lookingforPos > 0 && lookingForOptionItems.size() > 0) {
			request.addProperty("LookingForID", lookingForOptionItems.get(lookingforPos - 1).id);
			Log.e("LookingForID", lookingForOptionItems.get(lookingforPos - 1).title);
		} else {
			request.addProperty("LookingForID", 0);
		}
		
		if (educationPos > 0 && educationOptionItems.size() > 0) {
			request.addProperty("EducationID", educationOptionItems.get(educationPos - 1).id);
		} else {
			request.addProperty("EducationID", 0);
		}
		
		if (ethnicityPos > 0 && ethnicityOptionItems.size() > 0) {
			request.addProperty("EthnicityID", ethnicityOptionItems.get(ethnicityPos - 1).id);
		} else {
			request.addProperty("EthnicityID", 0);
		}

		if (languagesPos > 0 && languageOptionItems.size() > 0) {
			request.addProperty("LanguageID", languageOptionItems.get(languagesPos - 1).id);
		} else {
			request.addProperty("LanguageID", 0);
		}
		request.addProperty("AboutMe", aboutyou);

		AnyTask getNetworkTask = new AnyTask(cntx, soapAction, request, new AnyTaskCallback() {

			@Override
			public void onResult(boolean success, Object result) {
				hideProgressDialog();
				if (success) {
					SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

					SoapPrimitive response;
					try {
						response = (SoapPrimitive) envelope.getResponse();
						String responseString = response.toString();
						JSONObject responseObj = new JSONObject(responseString);
						if (responseObj.has("IsError") && responseObj.getBoolean("IsError")) {
							String message = responseObj.getString("Message");
							showMessage(message);
						} else {
							saveUserInformation(responseString);
							showMessage(R.string.save_changes_successfully);
							showCurrentProfileInformation();
							setResult(Activity.RESULT_OK);
							finish();
						}
					} catch (SoapFault e) {
						String errorMsg = (String) e.faultstring;
						if (TextUtils.isEmpty(errorMsg)) {
							errorMsg = getString(R.string.error_loading);
						}
						showMessage(errorMsg);
					} catch (Exception e) {
						String errorMsg = (String) e.getMessage();
						if (TextUtils.isEmpty(errorMsg)) {
							errorMsg = getString(R.string.error_loading);
						}
						showMessage(errorMsg);
					}
				} else {
					String errorMsg = (String) result;
					if (TextUtils.isEmpty(errorMsg)) {
						errorMsg = getString(R.string.error_loading);
					}
					showMessage(errorMsg);
				}
			}
		}, false);
		showProgressDialog();
		BackgroundProcessor.getBackgroundProcessor(this).setPerformTask(getNetworkTask);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == FROM_CAMERA_REQCODE || requestCode == FROM_ALBUM_REQCODE
					|| requestCode == CROP_REQCODE) {
				if (requestCode == FROM_CAMERA_REQCODE || requestCode == FROM_ALBUM_REQCODE) {
					String mSelectedFile = "";
					if (requestCode == FROM_CAMERA_REQCODE) {
						if (!TextUtils.isEmpty(strCameraOutputFilePath))
							mSelectedFile = strCameraOutputFilePath;
					} else if (data != null && data.getData() != null) {
						mSelectedFile = Utility.getRealPathFromURI(cntx, data.getData());
					}

					if (!TextUtils.isEmpty(mSelectedFile)) {
						Uri justTakenPictureUri = Uri.fromFile(new File(mSelectedFile));

						File outputFile = Utility.makeTempJPGFile();
						if (outputFile == null)
							return;

						strCropedFilePath = outputFile.getAbsolutePath();
						Uri mResultImageUri = Uri.fromFile(outputFile);

						Intent intent = new Intent("com.android.camera.action.CROP");
						intent.setDataAndType(justTakenPictureUri, TYPE_IMAGE);
						intent.putExtra("scale", true);
						intent.putExtra("scaleUpIfNeeded", true);
						intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
						intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
						intent.putExtra("outputX", PROFILE_IMAGE_OUTPUT_X);
						intent.putExtra("outputY", PROFILE_IMAGE_OUTPUT_Y);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, mResultImageUri);
						startActivityForResult(intent, CROP_REQCODE);
					}
				} else if (requestCode == CROP_REQCODE) {
					File imgFile = new File(strCropedFilePath);
					if (imgFile.exists()) {
						profileImageFile = strCropedFilePath;
						Bitmap photoBmp = BitmapFactory.decodeFile(profileImageFile);
						profilePhotoIv.setImageBitmap(photoBmp);

						UploadPhotoTask uploadTask = new UploadPhotoTask(cntx,
								new AnyTaskCallback() {

									@Override
									public void onResult(boolean success, Object result) {
										if (success) {
											profileImageUrl = (String) result;
											settings.edit().putString("ImageURL", profileImageUrl)
													.commit();
											profileImageFile = "";
											showProfileImage();
										} else {
											String errorMsg = (String) result;
											if (TextUtils.isEmpty(errorMsg)) {
												showMessage(R.string.error_picture_upload);
											} else {
												showMessage(errorMsg);
											}
										}
									}
								}, profileImageFile, settings.getInt("UserID", 0) + "", true);
						uploadTask.execute("");
					}
					strCropedFilePath = "";
				}
			} else if (requestCode == REQUEST_SELECT_GENDER && data != null) {
				String gender = data.getStringExtra("gender");
				if (!TextUtils.isEmpty(gender)) {
					genderValueTv.setText(gender);
				}
			} else if (requestCode == REQUEST_INPUT_FIELD && data != null) {
				int typeField = data.getIntExtra("profile_type", UserProfile.TYPE_FIRSTNAME);
				String newValue = data.getStringExtra("profile_value");
				if (typeField == UserProfile.TYPE_FIRSTNAME) {
					firstNameValueTv.setText(newValue);
				} else if (typeField == UserProfile.TYPE_LASTNAME) {
					lastNameValueTv.setText(newValue);
				} else if (typeField == UserProfile.TYPE_ABOUTYOU) {
					aboutYouValueTv.setText(newValue);
				} else if (typeField == UserProfile.TYPE_EMAIL) {
					emailValueTv.setText(newValue);
				} else if (typeField == UserProfile.TYPE_MOBILE) {
					mobileValueTv.setText(newValue);
				}
			} else if (requestCode == REQUEST_SELECT_HOME_LOCATION && data != null) {
				String address = data.getStringExtra("new_location");
				homeLat = data.getDoubleExtra("lat", 0);
				homeLng = data.getDoubleExtra("lng", 0);
				homeAddressValueTv.setText(address);
			} else if (requestCode == REQUEST_SELECT_WORK_LOCATION && data != null) {
				String address = data.getStringExtra("new_location");
				workLat = data.getDoubleExtra("lat", 0);
				workLng = data.getDoubleExtra("lng", 0);
				workAddressValueTv.setText(address);
			}
		}
	}

	@Override
	public void onSpinnerOpened() {
	}

	@Override
	public void onSpinnerClosed() {
	}
}
