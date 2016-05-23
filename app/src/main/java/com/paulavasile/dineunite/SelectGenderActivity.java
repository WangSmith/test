package com.paulavasile.dineunite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectGenderActivity extends BaseActivity implements OnClickListener {

	TextView maleTitleTv;
	CheckBox maleRadio;
	TextView femaleTitleTv;
	CheckBox femaleRadio;
	
	int normalColor = 0xff9a9a9a;
	int selectColor = 0xfff58220;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_gender);

		setTopTitle(R.string.hint_gender);

		Intent data = getIntent();
		String genderValue = data.getStringExtra("gender");

		findViewById(R.id.left_button).setOnClickListener(this);
		findViewById(R.id.right_button).setOnClickListener(this);

		findViewById(R.id.maleSelect).setOnClickListener(this);
		findViewById(R.id.femaleSelect).setOnClickListener(this);

		maleTitleTv = (TextView) findViewById(R.id.maleTitleTv);
		maleRadio = (CheckBox) findViewById(R.id.maleRadio);
		femaleTitleTv = (TextView) findViewById(R.id.femaleTitleTv);
		femaleRadio = (CheckBox) findViewById(R.id.femaleRadio);
		
		if (!TextUtils.isEmpty(genderValue)) {
			if (genderValue.equalsIgnoreCase("Male")) {
				selectGender(true);
			} else {
				selectGender(false);
			}
		}
	}

	private void selectGender(boolean male) {
		if (male) {
			maleRadio.setChecked(true);
			maleTitleTv.setTextColor(selectColor);
			femaleRadio.setChecked(false);
			femaleTitleTv.setTextColor(normalColor);
		} else {
			maleRadio.setChecked(false);
			maleTitleTv.setTextColor(normalColor);
			femaleRadio.setChecked(true);
			femaleTitleTv.setTextColor(selectColor);
		}
	}

	private void saveProfileField() {

		// Return data
		Intent intent = getIntent();
		intent.putExtra("gender", maleRadio.isChecked() ? "Male" : "Female");
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.left_button) {
			finish();
		} else if (viewId == R.id.right_button) {
			saveProfileField();
		} else if (viewId == R.id.maleSelect) {
			selectGender(true);
		} else if (viewId == R.id.femaleSelect) {
			selectGender(false);
		}
	}
}
