package com.paulavasile.dineunite;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class SplashActivity extends BaseActivity {

	private long showStartTime;
	private final static long DELAY_TIME = 3000;
	private boolean isRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		isRunning = true;	
		showStartTime = System.currentTimeMillis();

		if(GlobalHelper.isConnectingToInternet(this))
		{
			tryLoginWithPhone();
		}
		else
		{
			startSplash();
		}
	}

	private void tryLoginWithPhone() {
		String phone = Utility.getPhoneNumber(cntx);
		SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.LOGIN_WITH_PHONE);
		String soapAction = Utility.NAMESPACE + Utility.LOGIN_WITH_PHONE;
		int nLen = phone.length();
		if( phone == null || phone.length() == 0 )
			phone = "15860703733";
		request.addProperty("PhoneNumber", /*"15860703734"*/phone); // 07870550246

		AnyTask getNetworkTask = new AnyTask(cntx, soapAction, request, new AnyTaskCallback() {

			@Override
			public void onResult(boolean success, Object result) {
				// User cancel processing
				if (!isRunning) {
					return;
				}
				
				if (success) {
					SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

					SoapPrimitive response;
					try {
						response = (SoapPrimitive) envelope.getResponse();
						String responseString = response.toString();
						JSONObject responseObj = new JSONObject(responseString);
						if (responseObj.has("IsError") && responseObj.getBoolean("IsError")) {
							String message = responseObj.getString("Message");
							closeMsg(message);
						} else {
							saveUserInformation(responseString);
							startSplash();
						}
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
		BackgroundProcessor.getBackgroundProcessor(this).setPerformTask(getNetworkTask);
	}
	
	private void startSplash() {

		Thread background = new Thread() {
			public void run() {
				try {
					// Delay Time
					long currentTime = System.currentTimeMillis();
					if (currentTime - showStartTime < DELAY_TIME) {
						try {
							// Delay for DELAY_TIME
							Thread.sleep(showStartTime + DELAY_TIME - currentTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					return;
				} catch (Exception e) {
					return;
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							doFinish();
						}
					});
				}
			}
		};

		background.start();
	}

	private void doFinish() {
		if (isRunning) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (isRunning) {
			isRunning = false;
		}
		finish();
	}
}
