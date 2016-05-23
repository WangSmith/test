package com.paulavasile.dineunite.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class CustomSpinner extends Spinner {

	private OnSpinnerEventsListener mListener;
	private boolean mOpenInitiated = false;

	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomSpinner(Context context) {
		super(context);
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean performClick() {
		// register that the Spinner was opened so we have a status
		// indicator for the activity(which may lose focus for some other
		// reasons)
		mOpenInitiated = true;
		if (mListener != null) {
			mListener.onSpinnerOpened();
		}
		return super.performClick();
	}

	public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
		mListener = onSpinnerEventsListener;
	}

	/**
	 * Propagate the closed Spinner event to the listener from outside.
	 */
	public void performClosedEvent() {
		mOpenInitiated = false;
		if (mListener != null) {
			mListener.onSpinnerClosed();
		}
	}

	/**
	 * A boolean flag indicating that the Spinner triggered an open event.
	 * 
	 * @return true for opened Spinner
	 */
	public boolean hasBeenOpened() {
		return mOpenInitiated;
	}

}
