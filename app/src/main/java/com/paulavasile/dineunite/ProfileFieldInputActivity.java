package com.paulavasile.dineunite;

import com.paulavasile.dineunite.data.UserProfile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ProfileFieldInputActivity extends BaseActivity implements
		OnClickListener {

	int typeField;
	String currentValue = "";

	EditText profileEdt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_input);

		Intent data = getIntent();
		typeField = data
				.getIntExtra("profile_type", UserProfile.TYPE_FIRSTNAME);
		currentValue = data.getStringExtra("profile_value");

		findViewById(R.id.left_button).setOnClickListener(this);
		findViewById(R.id.right_button).setOnClickListener(this);

		profileEdt = (EditText) findViewById(R.id.profileEdt);

		String topTitle = "";
		if (typeField == UserProfile.TYPE_FIRSTNAME) {
			topTitle = getString(R.string.hint_firstname);
			profileEdt.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (typeField == UserProfile.TYPE_LASTNAME) {
			topTitle = getString(R.string.hint_lastname);
			profileEdt.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (typeField == UserProfile.TYPE_ABOUTYOU) {
			topTitle = getString(R.string.hint_aboutyou);
			profileEdt
					.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_NULL
							| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		} else if (typeField == UserProfile.TYPE_EMAIL) {
			topTitle = getString(R.string.hint_email);
			profileEdt
					.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		} else if (typeField == UserProfile.TYPE_MOBILE) {
			topTitle = getString(R.string.hint_mobile);
			profileEdt.setInputType(InputType.TYPE_CLASS_PHONE);
		}

		setTopTitle(topTitle);
		profileEdt.setText(currentValue);
	}

	private void saveProfileField() {
		hideKeyboard(profileEdt);

		String fieldValue = profileEdt.getText().toString().trim();

		if (TextUtils.isEmpty(fieldValue)) {
			return;
		}

		if (typeField == UserProfile.TYPE_EMAIL && !isEmailValid(fieldValue)) {
			showMessage(R.string.error_invalid_email);
			return;
		}

		// Return data
		Intent intent = getIntent();
		intent.putExtra("profile_type", typeField);
		intent.putExtra("profile_value", fieldValue);
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
		}
	}
}
