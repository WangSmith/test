package com.paulavasile.dineunite;

import java.io.IOException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.DatabaseHelper;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.fragments.DinnerEventsFragment;
import com.paulavasile.dineunite.fragments.FindMeADinnerPartnerFragment;
import com.paulavasile.dineunite.fragments.NavigationDrawerFragment;
import com.paulavasile.dineunite.fragments.NewsInfoFragment;
import com.paulavasile.dineunite.fragments.SocialTableFragment;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {

	DrawerLayout drawer;
	NavigationDrawerFragment mNavigationDrawerFragment;
	int drawerGravity = Gravity.LEFT;

	View menu_right;
	ListView menuList;
	MenuAdapter menuAdpter;

	View menuTail;
	int[] menuTitleIds = { R.string.menu_my_profile, R.string.menu_dinner_events, R.string.menu_find_me_a_dinner_partner,
			R.string.menu_restaurant_tables, R.string.menu_invite_my_friends,
			R.string.menu_my_dineunite_friends, R.string.menu_credits, R.string.menu_account_settings, R.string.menu_about_dineunite,
			R.string.menu_exit};

	int[] menuImageIds = { R.drawable.ic_menu_myprofile, R.drawable.ic_menu_dinnerevents, R.drawable.ic_menu_findmeadinnerpartner,
	R.drawable.ic_menu_restaurant_tables, R.drawable.ic_menu_invitemyfriends, R.drawable.ic_menu_dineunite_friends,
			R.drawable.ic_menu_credits,	R.drawable.ic_menu_setting, R.drawable.ic_menu_aboutdineunite, R.drawable.ic_menu_exit};

	// Please don't change this value
	private static final int MENU_NONE = -1;
	private static final int MENU_MY_ROFILE = 0;
	private static final int MENU_DINNER_EVENTS = 1;
	private static final int MENU_FIND_A_DINNER_PARTNER = 2;
	private static final int MENU_RESTAURANT_TABLES = 3;
	private static final int MENU_INVITE_FRIENDS = 4;
	private static final int MENU_MY_DINEUNITE_FRIENDS = 5;
	private static final int MENU_CREDITS = 6;
	private static final int MENU_ACCOUNTS_SETTINGS = 7;
	private static final int MENU_ABOUT_DINEUNITE = 8;
	private static final int MENU_EXIT = 9;

	int currentMenu = MENU_NONE;

	Fragment currentFragment;
	NewsInfoFragment newsInfoFragment;
	FindMeADinnerPartnerFragment findMeADinnerPartnerFragment;
	DinnerEventsFragment dinnerEventsFragment;
	SocialTableFragment socialTableFragment;


	// GCM
	String appVersionName;
	int appVersionCode;
	GoogleCloudMessaging gcm;

	String SENDER_ID = "922039224657"; // Server Key :
										// AIzaSyAPJINwWfbANrgfFV3bBncHOimLzrEAYsY
	String regid;

	private static final String Tag = "DineUnite";
	private static boolean mRunning = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalHelper.regiserMe(this);
		setContentView(R.layout.activity_main);
//		tryLoginWithPhone();
		mRunning = true;
		init();

		setTopTitle(R.string.menu_social_tables);
		showSocialTablesFragment();

		new Thread(){
			@Override
			public void run()
			{
				boolean bExists;
				int nCount = DatabaseHelper.getInstance(MainActivity.this).getPendingCount();
				if( nCount > 0 )
				{
					bExists = true;
				}
				else
				{
					bExists = false;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while(true)
				{
					if( mRunning == false ) return;
					boolean bNewExists;
					nCount = DatabaseHelper.getInstance(MainActivity.this).getPendingCount();

					if( nCount > 0 )
					{
						bNewExists = true;
					}
					else
					{
						bNewExists = false;
					}

					if( bNewExists != bExists )
					{
						refreshFragment();
					}
					bExists = bNewExists;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void init()
	{
		initControls();

		// SignalR
		initialize();
		prepareGetMessage();
		connectToServer();
	}

	@Override
	protected boolean isHomeScreen() {
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initControls() {
		drawer = (DrawerLayout) findViewById(R.id.drawer);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_drawer);

		// Others
		menu_right = findViewById(R.id.left_button);
		menu_right.setOnClickListener(this);

		// Menu List
		menuList = (ListView) findViewById(R.id.menuList);
		menuAdpter = new MenuAdapter(cntx);
		menuList.setAdapter(menuAdpter);
		menuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				showItem(position);
			}
		});

		menuTail = findViewById(R.id.menu_tail);
		menuTail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				menuList.smoothScrollToPosition(menuAdpter.getCount());
			}
		});

		menuList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				if (totalItemCount == 0)
					return;

				final boolean reachBottom = firstVisibleItem + visibleItemCount == totalItemCount;

				if (reachBottom) {
					menuTail.setVisibility(View.GONE);
				} else {
					menuTail.setVisibility(View.VISIBLE);
				}
			}
		});

//		showItem(MENU_SOCIAL_TABLES);

		// Register Device Token
		// Check Google Clouding Message

		// We always register every lunch
		settings.edit().putString("registration_id", "").commit();
		settings.edit().putInt("app_version", 0).commit();

/*		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(cntx);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(Tag, "No valid Google Play Services APK found.");
		}*/

	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(cntx);
					}

					// Get ID from server
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.i(Tag, msg);

					// Register Server
					SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.REGISTER_TOKEN);
					String soapAction = Utility.NAMESPACE + Utility.REGISTER_TOKEN;
					request.addProperty("UserID", settings.getInt("UserID", 0));
					request.addProperty("type", "android");
					request.addProperty("DeviceToken", regid);

					AnyTask getNetworkTask = new AnyTask(cntx, soapAction, request,
							new AnyTaskCallback() {

								@Override
								public void onResult(boolean success, Object result) {
									if (success) {
										SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

										SoapPrimitive response;
										try {
											response = (SoapPrimitive) envelope.getResponse();
											String responseString = response.toString();
											JSONObject respObj = new JSONObject(responseString);

											if (respObj.has("IsError")
													&& !respObj.getBoolean("IsError")) {
												settings.edit().putString("registration_id", regid)
														.commit();
												settings.edit()
														.putInt("app_version", appVersionCode)
														.commit();
											}
										} catch (SoapFault e) {
											e.printStackTrace();
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										String errorMsg = (String) result;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
										Log.e(Tag, errorMsg);
									}
								}
							}, false);
					BackgroundProcessor.getBackgroundProcessor(MainActivity.this).setPerformTask(getNetworkTask);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}

		}.execute(null, null, null);
	}

	private String getRegistrationId(Context context) {
		String registrationId = settings.getString("registration_id", "");
		if (registrationId.isEmpty()) {
			Log.i("APP_GCM", "Registration not found.");
			return "";
		}

		// Check the Update status, If app was updated, remove original
		// registered id.
		// Because, there is no ensure that old id works on the new version.
		int registeredVersion = settings.getInt("app_version", Integer.MIN_VALUE);
		if (registeredVersion != appVersionCode) {
			Log.i(Tag, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	protected boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("ICELANCER", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void showItem(int itemId) {

//		if( itemId == currentMenu ) return;
		currentMenu = itemId;
		menuAdpter.notifyDataSetChanged();

		boolean closeMenu = false;

		if (currentMenu == MENU_MY_ROFILE) {
			startActivity(new Intent(cntx, ProfileActivity.class));
		} else if(currentMenu == MENU_DINNER_EVENTS) {
			setTopTitle(R.string.menu_dinner_events);
			showDinnerEventsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_FIND_A_DINNER_PARTNER) {
			setTopTitle(R.string.menu_find_me_a_dinner_partner);
			showFindMeADinnerPartnerFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_RESTAURANT_TABLES) {
			setTopTitle(R.string.menu_restaurant_tables);
			showRestaurantTablesFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_INVITE_FRIENDS) {
			setTopTitle(R.string.menu_invite_my_friends);
			showNewsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_MY_DINEUNITE_FRIENDS) {
			setTopTitle(R.string.menu_my_dineunite_friends);
			showNewsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_CREDITS) {
			setTopTitle(R.string.menu_credits);
			showNewsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_ACCOUNTS_SETTINGS) {
			setTopTitle(R.string.menu_account_settings);
			showNewsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_ABOUT_DINEUNITE) {
			setTopTitle(R.string.menu_account_settings);
			showNewsFragment();
			closeMenu = true;
		} else if(currentMenu == MENU_EXIT) {
			finish();
		}

		if (closeMenu) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					closeDrawerMenu();
				}
			}, 200);
		}
	}

	private void showSocialTablesFragment() {
		socialTableFragment = new SocialTableFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", 0);
		socialTableFragment.setArguments(bundle);
		changeFragment(socialTableFragment, false);
	}

	private void showRestaurantTablesFragment() {
		socialTableFragment = new SocialTableFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", 1);
		socialTableFragment.setArguments(bundle);
		changeFragment(socialTableFragment, false);
	}

	private void showNewsFragment() {
		newsInfoFragment = new NewsInfoFragment();
		changeFragment(newsInfoFragment, false);
	}

	private void showFindMeADinnerPartnerFragment() {
		findMeADinnerPartnerFragment = new FindMeADinnerPartnerFragment();
		changeFragment(findMeADinnerPartnerFragment, false);
	}

	public void showDinnerEventsFragment() {
		dinnerEventsFragment = DinnerEventsFragment.createInstance(0);
		changeFragment(dinnerEventsFragment, false);
	}

	public void changeFragment(Fragment targetFragment, boolean animation) {
		GlobalHelper.__gCurTable = null;
		currentFragment = targetFragment;

		if (animation) {
			// With Animation
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();

			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
					R.anim.push_right_in, R.anim.push_right_out);
			transaction.replace(R.id.main_content, targetFragment, "fragment");
			transaction.commit();
		} else {
			// Without Animation
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.main_content, targetFragment, "fragment")
					.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
		}
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.left_button) {
			openDrawerMenu();
		} else if (viewId == R.id.left_button) {

		}
	}

	private void openDrawerMenu() {
		if (!drawer.isDrawerOpen(drawerGravity)) {
			drawer.openDrawer(drawerGravity);
		}
	}

	private void closeDrawerMenu() {
		if (drawer.isDrawerOpen(drawerGravity)) {
			drawer.closeDrawer(drawerGravity);
		}
	}

	boolean isFinish = false;

	public void refreshFragment() {
		if( currentFragment instanceof DinnerEventsFragment )
		{
			((DinnerEventsFragment)currentFragment).refresh();
		}
		else if( currentFragment instanceof SocialTableFragment )
		{
			((SocialTableFragment)currentFragment).refresh();
		}
	}

	class FinishTimer extends CountDownTimer {
		public FinishTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			isFinish = true;
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			isFinish = false;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			if (drawer.isDrawerOpen(drawerGravity)) {
				drawer.closeDrawer(drawerGravity);
				return false;
			}

			if (!isFinish) {
				showMessage(R.string.finish_message);
				FinishTimer timer = new FinishTimer(2000, 1);
				timer.start();
			} else {
				finish();
				return true;
			}
		}
		return false;
	}

	private class MenuAdapter extends BaseAdapter {

		Context mContext;

		public MenuAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return menuTitleIds.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, null);
			}

			View select_mark = convertView.findViewById(R.id.select_mark);
			ImageView menuIv = (ImageView) convertView.findViewById(R.id.menuIv);
			TextView menuTitleTv = (TextView) convertView.findViewById(R.id.menuTitleTv);
			if (position == currentMenu) {
				select_mark.setVisibility(View.VISIBLE);
			} else {
				select_mark.setVisibility(View.GONE);
			}

			menuIv.setImageResource(menuImageIds[position]);
			menuTitleTv.setText(menuTitleIds[position]);
			menuTitleTv.setTypeface(spinnerItemsFont);

			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (currentFragment != null) {
			currentFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	// SignalR
	HubConnection mConnection;
	HubProxy mHub;

	private void initialize() {
		String serverUrl = "http://server.url";
		String hubName = "hubName";

		Platform.loadPlatformComponent(new AndroidPlatformComponent());
		mConnection = new HubConnection(serverUrl);
		mHub = mConnection.createHubProxy(hubName);
	}

	private void prepareGetMessage() {
		mHub.on("hello", new SubscriptionHandler1<String>() {
			@Override
			public void run(final String msg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showMessage(msg);
					}
				});
			}
		}, String.class);
	}

	private void connectToServer() {
		try {
			SignalRFuture<Void> awaitConnection = mConnection.start();
			awaitConnection.get();
			showMessage("Connected");
		} catch (Exception e) {
			Log.e("SignalR", "Failed to connect to server");
		}
	}

	@Override
	protected void onDestroy() {
		mConnection.stop();
		mRunning = false;
		super.onDestroy();
	}
}
