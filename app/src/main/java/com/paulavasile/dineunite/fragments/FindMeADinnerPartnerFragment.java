package com.paulavasile.dineunite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.paulavasile.dineunite.R;

public class FindMeADinnerPartnerFragment extends BaseFragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		init();
		parentView = inflater.inflate(R.layout.fragment_find_me_a_dinner_partner, container, false);
			
		setViewTypeface(parentView.findViewById(R.id.inviteGuestChk), itemContentFont);
		setViewTypeface(parentView.findViewById(R.id.findTableChk), itemContentFont);
		
		Button btnContinie = (Button) parentView.findViewById(R.id.btnContinue);
		setViewTypeface(btnContinie, itemContentFont);
		btnContinie.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		return parentView;
	}
}
