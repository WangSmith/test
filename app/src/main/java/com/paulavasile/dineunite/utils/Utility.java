package com.paulavasile.dineunite.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.paulavasile.dineunite.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

@SuppressLint("WorldReadableFiles")
public class Utility {

	public static String CUR_PROTOCAL_VERSION = "1";
	private Context cntxt = null;
	protected IntentFilter filter = null;
	private static Utility util;

	public static Utility getInstance(Context context) {
		if (util == null) {
			util = new Utility(context);
		}

		return util;
	}

	public Utility(Context context) {
		cntxt = context;
		util = this;
	}

	public static String getPhoneNumber(Context context) {
		Context appContext = context.getApplicationContext();
		TelephonyManager tMgr = (TelephonyManager) appContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		return mPhoneNumber;
	}

	public static String getCountryCode(Context context) {
		Context appContext = context.getApplicationContext();
		TelephonyManager tm = (TelephonyManager) appContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String countryCode = tm.getSimCountryIso();
		return countryCode;
	}

	public static String getCountryZipCode(Context context) {
		Context appContext = context.getApplicationContext();
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) appContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = appContext.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	public static void log(String str) {
		// Log.d("NINTH_DEV", str);
	}


	public static double distanceInMiles2(double Lat1, double Long1, double Lat2, double Long2) {

		Location locationA = new Location("point A");

		locationA.setLatitude(Lat1);
		locationA.setLongitude(Long1);

		Location locationB = new Location("point B");

		locationB.setLatitude(Lat2);
		locationB.setLongitude(Long2);

		float distance = locationA.distanceTo(locationB);
		double distanceInMiles =distance * 0.000621371192;
		
		Log.e("Distance", String.format("Between : [%f %f]-[%f %f]", Lat1, Long1, Lat2, Long2));
		
		return distanceInMiles;
	}

	public static final String WEBSERVICE_URL = "http://dineunite.com/MobiServices/Diners/DinersServiceManager.asmx";
	public static final String NAMESPACE = "http://tempuri.org/";
	// public static final long USERID = 1615l;

	// -------------- Find Me a Table Relation ------------------
	public static final String LOGIN_WITH_PHONE = "LoginUsingPhone";
	public static final String REGISTER_TOKEN = "RegisterDeviceToken";
	public static final String UPLOAD_PHOTO = "UploadDinerNewImage";
	public static final String DELETE_PHOTO = "DeleteDinermage";
	public static final String SAVE_DINNER_PROFILE = "SaveDinerProfile";

	// -------------- Find Me a Table Relation ------------------
	public static final String GET_ALL_UTILS = "GetAllUtilsData";
	public static final String GET_ALL_UTILS_FOR_PROFILE = "GetAllUtilsDataForProfileScreen";
	public static final String GET_POST_CODES = "GetPostCodes";
	public static final String GET_SEARCH_TABLES = "SearchTables";

	// -------------- Show Social Tables ------------------
	public static final String GET_SOCIAL_TABLES = "ShowSocialTables";
	public static final String GET_RESTAURANT_TABLES = "ShowAllTables";

	// Book
	public static final String BOOK_RESTAURANT_TABLE = "BookRestaurantTable";
	public static final String JOIN_OPEN_TABLE = "JoinOpenTable";
	public static final String REQUEST_JOIN_TABLE_BY_GUEST = "RequestJoinTableByGuest";
	public static final String REQUEST_JOIN_TABLE_BY_HOST = "RequestJoinTableByHost";
	public static final String REQUEST_JOIN_TABLE_BY_HOST_WITH_MESSAGE = "RequestJoinTableByHostWithMessage";
	
	// -------------- Dinner Events Relation --------------------
	public static final String GET_DINNER_EVENTS = "GetDinerTableBookings";
	public static final String ACCEPT_JOIN_TABLE_REQUEST = "AcceptJoinTableRequest";
	public static final String DECLINE_JOIN_TABLE_REQUEST_BYHOST = "DeclineJoinTableRequestByHost";
	public static final String DECLINE_JOIN_TABLE_REQUEST_BYGUEST = "DeclineJoinTableRequestByGuest";
	public static final String CANCEL_JOINED_SEAT_BYGUEST = "CancelJoinedTableByGuest";
	
	public static final String CANCEL_RESTAURANT_TABLE_BOOKING = "CancelDinerTableBooking";
	public static final String SEARCH_DINNERS = "SearchDiners";
	
	
	// Update DinnerEvents item status
	public static final String UPDATE_DINNERTABLEBOOKING_ASPRIVATE = "UpdateDinerTableBookingAsPrivate";
	public static final String UPDATE_DINNERTABLEBOOKING_ASOPEN = "UpdateDinerTableBookingAsOpen";
	public static final String UPDATE_DINNERTABLEBOOKING_ASGUESTINVITATION = "UpdateDinerTableBookingAsGuestInvitation";

	public static String getServiceNamespace() {
		return NAMESPACE;
	}

	public static SoapSerializationEnvelope getSoapService(String soapAction, SoapObject params)
			throws Exception {
		HttpTransportSE androidHttpTransport = new HttpTransportSE(WEBSERVICE_URL);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(params);

		// Invole web service
		androidHttpTransport.call(soapAction, envelope);
		return envelope;
	}

	public static SoapSerializationEnvelope getSoapFileService(String soapAction, SoapObject params)
			throws Exception {
		HttpTransportSE androidHttpTransport = new HttpTransportSE(WEBSERVICE_URL);
		androidHttpTransport.debug = true;
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(params);
		
		MarshalBase64 marshal = new MarshalBase64();
		marshal.register(envelope);
		
		// Invole web service
		androidHttpTransport.call(soapAction, envelope);
		return envelope;
	}
	
	// Check Network State
	public static boolean hasConnection(Context c) {

		// These part was deprecated in API23, use this part in lower version
		/*
		 * ConnectivityManager cm = (ConnectivityManager)
		 * c.getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo
		 * wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI); if
		 * (wifiNetwork != null && wifiNetwork.isConnected()) { return true; }
		 * 
		 * NetworkInfo mobileNetwork =
		 * cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); if (mobileNetwork
		 * != null && mobileNetwork.isConnected()) { return true; }
		 * 
		 * NetworkInfo activeNetwork = cm.getActiveNetworkInfo(); if
		 * (activeNetwork != null && activeNetwork.isConnected()) { return true;
		 * }
		 */

		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				// connected to wifi
				// Toast.makeText(context, activeNetwork.getTypeName(),
				// Toast.LENGTH_SHORT).show();
			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				// connected to the mobile provider's data plan
				// Toast.makeText(context, activeNetwork.getTypeName(),
				// Toast.LENGTH_SHORT).show();
			}
			return true;
		} else {
			return false;
		}
	}

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new NTHSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 15000);
			HttpConnectionParams.setSocketBufferSize(params, 99999);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static String requestServerGet(String strUrl) throws Exception {
		Utility.log("Request : " + strUrl);

		InputStream inputStream = null;
		String result = "";

		HttpClient httpclient = Utility.getNewHttpClient();
		HttpGet httpGet = new HttpGet(strUrl);

		// httpGet.setHeader("Accept", "application/json");
		// httpPost.setHeader("Content-type", "application/json");
		HttpResponse httpResponse = httpclient.execute(httpGet);
		inputStream = httpResponse.getEntity().getContent();

		if (inputStream != null) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
		}

		if (!result.equals("") && result.startsWith("\ufeff")) {
			result = result.substring(1);
		}

		// Utility.log("URL : " + APP_URL);
		Utility.log("Response : " + result);

		return result;
	}

	// Android 6.0 functions
	/*
	 * static public String getFormattedPhoneNumber(String number) { if
	 * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { return
	 * PhoneNumberUtils.formatNumber(number, "KR"); } else { // noinspection
	 * deprecation return PhoneNumberUtils.formatNumber(number); }
	 * 
	 * public static final int getColor(Context context, int id) { final int
	 * version = Build.VERSION.SDK_INT; if (version >= 23) { return
	 * ContextCompatApi23.getColor(context, id); } else { return
	 * context.getResources().getColor(id); }
	 * 
	 * public static final Drawable getDrawable(Context context, int id) { final
	 * int version = Build.VERSION.SDK_INT; if (version >= 21) { return
	 * ContextCompatApi21.getDrawable(context, id); } else { return
	 * context.getResources().getDrawable(id); } }
	 */

	// ********* Temp File Manger **********
	public static final String extStoragePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private static final String folderName = "dineunite";
	public static final String appFolder = extStoragePath + File.separator + folderName;
	public static final String cameraFolder = appFolder + File.separator + "Camera";
	public static final String tmpPicFolder = appFolder + File.separator + "tmpPic";
	final static String IMG_FILE_PREFIX = "fg";
	final static String CAMERA_FILE_PREFIX = "CAMERA";
	final static String JPEG_FILE_SUFFIX = ".jpg";
	final static String PNG_FILE_SUFFIX = ".png";

	public static File makeTempFile(String suffix) {

		if (TextUtils.isEmpty(suffix))
			suffix = JPEG_FILE_SUFFIX;

		File mediaStorageDir = new File(tmpPicFolder);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Temp File Manager", "Required media storage does not exist");
				return null;
			}
		}

		String strFileName = String.format("%s%d", IMG_FILE_PREFIX, System.currentTimeMillis());

		File tempImage = null;
		try {
			tempImage = File.createTempFile(strFileName, // prefix
					suffix, // suffix
					mediaStorageDir // directory
					);

			tempImage.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempImage;
	}

	public static File makeTempJPGFile() {

		return makeTempFile(JPEG_FILE_SUFFIX);
	}

	/**
	 * Used to return the camera File output.
	 * 
	 * @return
	 */
	public static File makeCameraOutputFile() {

		File mediaStorageDir = new File(cameraFolder);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		String strFileName = String.format("%s%d", CAMERA_FILE_PREFIX, System.currentTimeMillis());

		File cameraImage = null;
		try {
			cameraImage = File.createTempFile(strFileName, // prefix
					JPEG_FILE_SUFFIX, // suffix
					mediaStorageDir // directory
					);
			cameraImage.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cameraImage;
	}

	private static void removeFolder(File folder) {
		if (folder.exists() && folder.isDirectory()) {
			String[] children = folder.list();
			for (int i = 0; i < children.length; i++) {
				new File(folder, children[i]).delete();
			}
		}
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, // Which
				// columns
				// to
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity activity) {
		Point size = new Point();
		Display d = activity.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			d.getSize(size);
		} else {
			size.x = d.getWidth();
			size.y = d.getHeight();
		}

		return size.x;
	}
	
}