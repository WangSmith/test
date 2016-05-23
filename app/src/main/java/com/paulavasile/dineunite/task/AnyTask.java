package com.paulavasile.dineunite.task;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.utils.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;

//AsyncTask<Params, Progress, Result>
public class AnyTask extends AsyncTask<String, Void, String> {

	private Context mContext;
	private String mAction;
	private SoapObject mParams;
	private AnyTaskCallback mCallback;
	private boolean mShowProgress;

	protected ProgressDialog progress;
	private boolean mSuccess;
	private String mErrorMsg;

	SoapSerializationEnvelope mEnvelop;

	private String Tag = "AnyTask";

	public AnyTask(Context context, String function, SoapObject params, AnyTaskCallback callback,
			boolean showProgress) {
		mContext = context;
		mAction = function;
		mParams = params;
		mCallback = callback;
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
			progress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});

			progress.show();
			progress.setContentView(R.layout.dialog_loading);
		}
	}

	@Override
	protected String doInBackground(String... urls) {
		String mResult = "";
		try {
			mEnvelop = Utility.getSoapService(mAction, mParams);
		} catch (Exception e) {
			mSuccess = false;
			mErrorMsg = e.getMessage();
		}
		return mResult;
	}

	@Override
	protected void onPostExecute(String result) {

		if(latch != null && latch.getCount() > 0 )
		{
			latch.countDown();
		}
		// Hide ProgressBar
		if (mShowProgress && progress != null && progress.isShowing()) {
			progress.dismiss();
		}

		if (mCallback != null) {
			if (mSuccess) {
				mCallback.onResult(mSuccess, mEnvelop);
			} else {
				mCallback.onResult(mSuccess, mErrorMsg);
			}
		}

	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	private CountDownLatch latch = null;
	public void setProcessFlag(boolean b) {
		if( b )
		{
			latch = new CountDownLatch(1);
		}
		else
		{
			while(latch.getCount() > 0)
			{
				latch.countDown();
			}
			latch = null;
		}
	}

	public CountDownLatch getProcessFlag() {
		return latch;
	}
}
