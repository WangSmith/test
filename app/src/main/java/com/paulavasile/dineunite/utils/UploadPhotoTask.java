package com.paulavasile.dineunite.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.task.AnyTaskCallback;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

//AsyncTask<Params, Progress, Result>
@SuppressWarnings("deprecation")
public class UploadPhotoTask extends AsyncTask<String, Integer, String> {

	private Context mContext;
	private AnyTaskCallback mCallback;
	private boolean mShowProgress;
	protected ProgressDialog progress;
	private boolean mSuccess;
	private String mErrorMsg;
	String mSrcPath;
	String mUserID;

	public UploadPhotoTask(Context context, AnyTaskCallback callback, String srcPath,
			String userID, boolean showProgress) {

		mContext = context;
		mCallback = callback;
		mSrcPath = srcPath;
		mUserID = userID;
		mShowProgress = showProgress;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mSuccess = true;

		if (mShowProgress) {
			progress = new ProgressDialog(mContext);
			progress.setCanceledOnTouchOutside(false);
			progress.setCancelable(true);

			progress.show();
			progress.setContentView(R.layout.dialog_loading);
		}
	}

	@Override
	protected String doInBackground(String... urls) {

		String imgPath = null;
		
		try {
			File mImageFile = new File(mSrcPath);
			int size = (int) mImageFile.length();
			byte[] bytes = new byte[size];
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(mImageFile));
			buf.read(bytes, 0, bytes.length);
			buf.close();

			SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.UPLOAD_PHOTO);
			String soapAction = Utility.NAMESPACE + Utility.UPLOAD_PHOTO;
			request.addProperty("UserID", mUserID);
			request.addProperty("Image", bytes);
			
			SoapSerializationEnvelope mEnvelop = Utility.getSoapFileService(soapAction, request);
			SoapPrimitive response= (SoapPrimitive) mEnvelop.getResponse();
			
			String responseString = response.toString();
			JSONObject responseObj = new JSONObject(responseString);
			
			if (responseObj.has("IsError") && responseObj.getBoolean("IsError")) {
				mSuccess = false;
				mErrorMsg = responseObj.getString("Message");
			} else {
				imgPath = responseObj.getString("ImageURL");
			}
		}catch (Exception e) {
			e.printStackTrace();
			mSuccess = false;
			mErrorMsg = mContext.getString(R.string.error_picture_upload);
		} 

		return imgPath;
	}

	@Override
	protected void onPostExecute(String imgPath) {

		if (progress != null && progress.isShowing())
			progress.dismiss();

		if (mCallback != null) {
			if (mSuccess) {
				mCallback.onResult(true, imgPath);
			} else {
				mCallback.onResult(false, mErrorMsg);
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
