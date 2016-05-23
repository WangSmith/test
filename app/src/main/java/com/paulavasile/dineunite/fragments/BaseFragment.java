package com.paulavasile.dineunite.fragments;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.paulavasile.dineunite.BaseActivity;
import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.utils.Utility;

public class BaseFragment extends Fragment {

	protected BaseActivity mainActivity;
	protected SharedPreferences settings;

	protected Utility util;

	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;
	
	public View parentView;

	Typeface itemTitleFont;
	Typeface itemContentFont;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.activity_splash, container, false);
		return parentView;
	}

	protected void init() {
		mainActivity = (BaseActivity) getActivity();
		settings = mainActivity.getSharedPreferences("puntatrackingrefs", 0);

		util = Utility.getInstance(mainActivity);
		
		imageLoader = mainActivity.getImageLoader();
		options = mainActivity.getImageOption();

		itemTitleFont = mainActivity.getRegularTypeface();
		itemContentFont = mainActivity.getRegularTypeface();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
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

	// Show mesage dialog
	protected void msg(String msg) {
		mainActivity.msg(msg);
	}

	protected void msg(int msgId) {
		mainActivity.msg(msgId);
	}

	// Show message with Toast
	protected void showMessage(String msg) {
		mainActivity.showMessage(msg);
	}

	protected void showMessage(int msgId) {
		mainActivity.showMessage(msgId);
	}
	
	public class CustomFontSpinnerAdapter extends ArrayAdapter<String> {

		// (In reality I used a manager which caches the Typeface objects)
		public CustomFontSpinnerAdapter(Context context, int resource, List<String> items) {
			super(context, resource, items);
		}

		// Affects default (closed) state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView, parent);
			view.setTypeface(itemTitleFont);
			return view;
		}

		// Affects opened state of the spinner
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getDropDownView(position, convertView, parent);
			view.setTypeface(itemContentFont);
			return view;
		}
	}
}
