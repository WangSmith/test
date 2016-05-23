package com.paulavasile.dineunite.fragments;

import java.util.ArrayList;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.DatabaseHelper;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.data.DinnerTableBooking;
import com.paulavasile.dineunite.data.TableBookingGuest;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;

public class DinnerEventsFragment extends BaseFragment {

	long remarkedBookingID;
	View remarkedBookPanel;

	ArrayList<DinnerTableBooking> tableInfoList = new ArrayList<DinnerTableBooking>();
	int mUserID;

	ScrollView infoLv;
	ViewGroup contentLayout;
	View nodataIv;

	LayoutInflater viewInflator;

	int mPendingCount = 0;
	public static DinnerEventsFragment createInstance(long BookingID)
	{
		DinnerEventsFragment dinnerEventsFragment = new DinnerEventsFragment();
		Bundle params = new Bundle();
		params.putLong("BookingID", BookingID);
		dinnerEventsFragment.setArguments(params);
		return dinnerEventsFragment;
	}
	public DinnerEventsFragment()
	{

	}

	public void updateBookingInfo(long BookingID) {
		remarkedBookingID = BookingID;
		updateData();
	}

	public void updateBookingInfo(DinnerTableBooking newTableInfo) {
		if (newTableInfo != null && tableInfoList != null) {
			remarkedBookingID = newTableInfo.BookingID;
			for (int i = 0; i < tableInfoList.size(); i++) {
				if (tableInfoList.get(i).BookingID == newTableInfo.BookingID) {
					tableInfoList.set(i, newTableInfo);
					showInformation();
					return;
				}
			}
		}
		
		remarkedBookingID = -1;
		updateData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle arguments = getArguments();
		if( arguments != null )
		{
			remarkedBookingID = arguments.getLong("BookingID");
		}
		init();
		parentView = inflater.inflate(R.layout.fragment_dinnerevents, container, false);

		infoLv = (ScrollView) parentView.findViewById(R.id.infoLv);
		contentLayout = (ViewGroup) parentView.findViewById(R.id.contentLayout);
		nodataIv = parentView.findViewById(R.id.nodataIv);
		viewInflator = (LayoutInflater) mainActivity.getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mUserID = settings.getInt("UserID", 0);
		showInformation();
		updateData();
	}

	private void updateData() {
		SoapObject request = new SoapObject(Utility.NAMESPACE, Utility.GET_DINNER_EVENTS);
		String soapAction = Utility.NAMESPACE + Utility.GET_DINNER_EVENTS;
		request.addProperty("UserID", mUserID);

		if(GlobalHelper.isConnectingToInternet(mainActivity))
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							tableInfoList.clear();
							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope.getResponse();
									String responseString = response.toString();
									JSONObject responseObj = new JSONObject(responseString);
									mPendingCount = 0;
									if (responseObj.has("Bookings")
											&& responseObj.getJSONArray("Bookings").length() > 0) {
										tableInfoList = DinnerTableBooking
												.parseListFromString(responseObj.getString("Bookings"));
										DatabaseHelper.getInstance(mainActivity).registerBookingList(responseObj.getString("Bookings"));
									}
								} catch (SoapFault e) {
									String errorMsg = (String) e.faultstring;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								} catch (Exception e) {
									String errorMsg = (String) e.getMessage();
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								}
							} else {
								String errorMsg = (String) result;
								if (TextUtils.isEmpty(errorMsg)) {
									errorMsg = getString(R.string.error_loading);
								}
								msg(errorMsg);
							}
							showInformation();
							mainActivity.hideProgressDialog();
						}
					}, false);
			mainActivity.showProgressDialog();
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}
		else
		{
			String strText = DatabaseHelper.getInstance(mainActivity).getBookingList();
			if( strText != null && strText.length() > 0)
			{
				tableInfoList = DinnerTableBooking
						.parseListFromString(strText);
			}
			mPendingCount = BackgroundProcessor.getBackgroundProcessor(mainActivity).getPendingTaskListCount();
			showInformation();
		}
	}

	Handler mHandler = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			updateData();
		}
	};
	public void refresh()
	{
		mHandler.sendEmptyMessageDelayed(0, 100);
	}

	// Action Click Listener
	View.OnClickListener shareClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			DinnerTableBooking tableInfo = (DinnerTableBooking) view.getTag();
			if (tableInfo != null) {
				String curUser = settings.getString("FirstName", "");
				if (TextUtils.isEmpty(curUser)) {
					curUser = String.format("Your friend (%s)",
							settings.getString("Phone", ""));
				}
				String shareMsg = String.format(
						getString(R.string.table_share_body_format), curUser,
						tableInfo.TimeFrom, tableInfo.RestaurantName);
				mainActivity.shareContent(shareMsg);
			}
		}
	};

	private void showInformation() {
		contentLayout.removeAllViews();
		remarkedBookPanel = null;

		if (tableInfoList.size() == 0 && mPendingCount == 0) {
			nodataIv.setVisibility(View.VISIBLE);
			return;
		}

		// Show Dinner Event Data
		nodataIv.setVisibility(View.GONE);
		for (int i = 0; i < tableInfoList.size(); i++) {
			DinnerTableBooking dinnerTableBooking = tableInfoList.get(i);

			LinearLayout child = (LinearLayout) viewInflator.inflate(
					R.layout.item_dinner_event_open, null);
			contentLayout.addView(child);

			View shareBtn = child.findViewById(R.id.shareBtn);
			shareBtn.setVisibility(View.VISIBLE);
			shareBtn.setTag(dinnerTableBooking);
			shareBtn.setOnClickListener(shareClickListener);

			if (dinnerTableBooking.BookingID == remarkedBookingID) {
				remarkedBookPanel = child;
			}

			EventInfoPlaceHolder actionPlaceHolder = new EventInfoPlaceHolder();
			actionPlaceHolder.bookingIdx = i;
			actionPlaceHolder.tableBookingInfo = dinnerTableBooking;
			actionPlaceHolder.tableInfoViewPlaceHolder = child;

			// Header Status
			TextView expiredStatusTv = (TextView) child.findViewById(R.id.expiredStatusTv);
			View remainingDays = child.findViewById(R.id.remainingDays);
			TextView daysInfoTv, daysInfoTitleTv, hoursInfoTv, hoursInfoTitleTv, minsInfoTv, minsInfoTitleTv;
			daysInfoTv = (TextView) child.findViewById(R.id.daysInfoTv);
			daysInfoTitleTv = (TextView) child.findViewById(R.id.daysInfoTitleTv);
			hoursInfoTv = (TextView) child.findViewById(R.id.hoursInfoTv);
			hoursInfoTitleTv = (TextView) child.findViewById(R.id.hoursInfoTitleTv);
			minsInfoTv = (TextView) child.findViewById(R.id.minsInfoTv);
			minsInfoTitleTv = (TextView) child.findViewById(R.id.minsInfoTitleTv);
			TextView iAmHostingTv = (TextView) child.findViewById(R.id.iAmHostingTv);
			TextView openTableTv = (TextView) child.findViewById(R.id.openTableTv);
			View hostInfo = child.findViewById(R.id.hostInfo);
			TextView hostTitleTv, hostValueTv;
			hostTitleTv = (TextView) child.findViewById(R.id.hostTitleTv);
			hostValueTv = (TextView) child.findViewById(R.id.hostValueTv);

			daysInfoTv.setTypeface(itemTitleFont);
			daysInfoTitleTv.setTypeface(itemTitleFont);
			hoursInfoTv.setTypeface(itemTitleFont);
			hoursInfoTitleTv.setTypeface(itemTitleFont);
			minsInfoTv.setTypeface(itemTitleFont);
			minsInfoTitleTv.setTypeface(itemTitleFont);
			iAmHostingTv.setTypeface(itemTitleFont);
			openTableTv.setTypeface(itemTitleFont);
			hostTitleTv.setTypeface(itemContentFont);
			hostValueTv.setTypeface(itemContentFont);

			if (dinnerTableBooking.IsExpired) {
				remainingDays.setVisibility(View.GONE);
			} else {
				expiredStatusTv.setVisibility(View.GONE);
				daysInfoTv.setText(dinnerTableBooking.RemaininDays + "");
				hoursInfoTv.setText(dinnerTableBooking.RemainingHours + "");
				minsInfoTv.setText(dinnerTableBooking.RemainingMinutes + "");
			}

			if (dinnerTableBooking.Host == null) {
				hostTitleTv.setVisibility(View.GONE);
			}

			if (dinnerTableBooking.AmIHost) {
				openTableTv.setVisibility(View.GONE);
				hostInfo.setVisibility(View.GONE);
			} else if (dinnerTableBooking.AmIGuest) {
				iAmHostingTv.setVisibility(View.GONE);
				String name = String.format("%s %s", dinnerTableBooking.FirstName,
						dinnerTableBooking.LastName);
				if ("OPEN TABLE".equalsIgnoreCase(name)) {
					hostInfo.setVisibility(View.GONE);
				} else {
					openTableTv.setVisibility(View.GONE);
					String hostValue = "";

					if (TextUtils.isEmpty(dinnerTableBooking.LastName)) {
						hostValue = String.format("%s", dinnerTableBooking.FirstName);
					} else {
						hostValue = String.format("%s %s", dinnerTableBooking.FirstName,
								dinnerTableBooking.LastName.substring(0, 1));
					}

					if (hostValue.contains("JOINED AS GUEST")) {
						hostTitleTv.setVisibility(View.GONE);
					}
					hostValueTv.setText(hostValue);
				}
			}

			// Second Row
			TextView dateTv = (TextView) child.findViewById(R.id.dateTv);
			TextView startTimeTv = (TextView) child.findViewById(R.id.startTimeTv);
			dateTv.setTypeface(itemContentFont);
			startTimeTv.setTypeface(itemContentFont);
			dateTv.setText(dinnerTableBooking.BookingDate);
			startTimeTv.setText(dinnerTableBooking.TimeFrom);

			// Restaurant Details Information
			TextView restaurantDetailsTitle = (TextView) child
					.findViewById(R.id.restaurantDetailsTitle);
			View restaurantDetails = child.findViewById(R.id.restaurantDetails);
			restaurantDetailsTitle.setTypeface(itemContentFont);
			restaurantDetailsTitle.setTag(restaurantDetails);
			restaurantDetailsTitle.setOnClickListener(detailsTitleClickListener);
			restaurantDetails.setVisibility(View.GONE);

			restaurantDetailsTitle.setText(String.format("%s (%.1f miles)",
					dinnerTableBooking.RestaurantName, dinnerTableBooking.RestaurantDistance));

			// Show map position
			ImageView restaurantIv = (ImageView) child.findViewById(R.id.restaurantIv);
			imageLoader.displayImage(mainActivity.getMapPlaceImageUrl(
					dinnerTableBooking.RestaurantLatitude, dinnerTableBooking.RestaurantLongitude),
					restaurantIv, options);

			// map direction
			ImageView directionBtn = (ImageView) child.findViewById(R.id.directionBtn);
			directionBtn.setTag(dinnerTableBooking);
			directionBtn.setOnClickListener(viewDirectionClickListener);

			TextView durationTv = (TextView) child.findViewById(R.id.durationTv);
			TextView address1Tv = (TextView) child.findViewById(R.id.address1Tv);
			TextView address2Tv = (TextView) child.findViewById(R.id.address2Tv);

			durationTv.setTypeface(itemContentFont);
			address1Tv.setTypeface(itemContentFont);
			address2Tv.setTypeface(itemContentFont);

			durationTv.setText(String.format("%s - %s", dinnerTableBooking.TimeFrom,
					dinnerTableBooking.TimeTo));
			address1Tv.setText(dinnerTableBooking.RestaurantAddress);
			address2Tv.setText(dinnerTableBooking.PostCode);

			// Your Host Block
			View yourHostPanel = child.findViewById(R.id.yourHostPanel);
			TextView yourHostTitle = (TextView) child.findViewById(R.id.yourHostTitle);
			TextView invitationIcon = (TextView) child.findViewById(R.id.invitationIcon);
			ViewGroup yourHostDetails = (ViewGroup) child.findViewById(R.id.yourHostDetails);
			ViewGroup yourHostList = (ViewGroup) child.findViewById(R.id.yourHostList);
			yourHostTitle.setTypeface(itemContentFont);
			yourHostTitle.setTag(yourHostDetails);
			yourHostTitle.setOnClickListener(detailsTitleClickListener);
			yourHostDetails.setVisibility(View.GONE);

			TextView invitationMsgTv = (TextView) yourHostList.findViewById(R.id.invitationMsgTv);
			invitationMsgTv.setTypeface(itemContentFont);

			if (/* dinnerTableBooking.AmIHost || */dinnerTableBooking.Host == null) {
				yourHostPanel.setVisibility(View.GONE);
			} else {
				// Show Host Information
				ImageView hostPictureIv = (ImageView) yourHostList.findViewById(R.id.hostPictureIv);
				TextView hostNameTv = (TextView) yourHostList.findViewById(R.id.hostNameTv);
				TextView hostAgeTv = (TextView) yourHostList.findViewById(R.id.hostAgeTv);
				TextView relationshipStatusNameTv = (TextView) yourHostList
						.findViewById(R.id.relationshipStatusNameTv);
				TextView lookingForNameTv = (TextView) yourHostList
						.findViewById(R.id.lookingForNameTv);

				hostNameTv.setTypeface(itemContentFont);
				hostAgeTv.setTypeface(itemContentFont);
				relationshipStatusNameTv.setTypeface(itemContentFont);
				lookingForNameTv.setTypeface(itemContentFont);

				if (!TextUtils.isEmpty(dinnerTableBooking.Host.ProfilePicture)) {
					imageLoader.displayImage(dinnerTableBooking.Host.ProfilePicture, hostPictureIv,
							options);
				}

				String hostNameValue = "";
				if (TextUtils.isEmpty(dinnerTableBooking.Host.LastName)) {
					hostNameValue = String.format("%s", dinnerTableBooking.Host.FirstName);
				} else {
					hostNameValue = String.format("%s %s", dinnerTableBooking.Host.FirstName,
							dinnerTableBooking.Host.LastName.substring(0, 1));
				}
				hostNameTv.setText(hostNameValue);

				hostAgeTv.setText(String.format("%d years old", dinnerTableBooking.Host.Age));
				relationshipStatusNameTv.setText(dinnerTableBooking.Host.RelationshipStatusName);
				lookingForNameTv.setText(dinnerTableBooking.Host.LookingForName);

			}

			// Guest Block
			ViewGroup guestsPanel = (ViewGroup) child.findViewById(R.id.guestsPanel);
			if (dinnerTableBooking.Guests == null || dinnerTableBooking.Guests.size() == 0) {
				guestsPanel.setVisibility(View.GONE);
			} else {
				TextView guestsTitle = (TextView) guestsPanel.findViewById(R.id.guestsTitle);
				ViewGroup guestsDetails = (ViewGroup) guestsPanel.findViewById(R.id.guestsDetails);
				ViewGroup guestsListContent = (ViewGroup) guestsPanel
						.findViewById(R.id.guestsListContent);
				guestsTitle.setTypeface(itemContentFont);
				guestsTitle.setTag(guestsDetails);
				guestsTitle.setText(String.format("See guests (%d)",
						dinnerTableBooking.Guests.size()));
				guestsTitle.setOnClickListener(detailsTitleClickListener);
				guestsDetails.setVisibility(View.GONE);
				guestsListContent.removeAllViews();

				for (TableBookingGuest guest : dinnerTableBooking.Guests) {
					LinearLayout guestChildView = (LinearLayout) viewInflator.inflate(
							R.layout.item_dinner_event_guest, null);
					guestsListContent.addView(guestChildView);
					guestChildView.setTag(guest);

					ImageView guestPictureIv = (ImageView) guestChildView
							.findViewById(R.id.guestPictureIv);
					TextView guestNameTv = (TextView) guestChildView.findViewById(R.id.guestNameTv);
					TextView guestAgeTv = (TextView) guestChildView.findViewById(R.id.guestAgeTv);
					TextView relationshipStatusNameTv = (TextView) guestChildView
							.findViewById(R.id.relationshipStatusNameTv);
					TextView lookingForNameTv = (TextView) guestChildView
							.findViewById(R.id.lookingForNameTv);

					TextView statusTv = (TextView) guestChildView.findViewById(R.id.statusTv);

					View buttonActions = guestChildView.findViewById(R.id.buttonActions);
					Button acceptBtn = (Button) guestChildView.findViewById(R.id.acceptBtn);
					Button declineBtn = (Button) guestChildView.findViewById(R.id.declineBtn);

					guestNameTv.setTypeface(itemContentFont);
					guestAgeTv.setTypeface(itemContentFont);
					relationshipStatusNameTv.setTypeface(itemContentFont);
					lookingForNameTv.setTypeface(itemContentFont);
					statusTv.setTypeface(itemContentFont);
					acceptBtn.setTypeface(itemContentFont);
					declineBtn.setTypeface(itemContentFont);

					if (!TextUtils.isEmpty(guest.ProfilePicture)) {
						imageLoader.displayImage(guest.ProfilePicture, guestPictureIv, options);
					}

					if (TextUtils.isEmpty(guest.LastName)) {
						guestNameTv.setText(String.format("%s", guest.FirstName));
					} else {
						guestNameTv.setText(String.format("%s %s", guest.FirstName,
								guest.LastName.subSequence(0, 1)));
					}

					guestAgeTv.setText(String.format("%d years old", guest.Age));
					if (TextUtils.isEmpty(guest.RelationshipStatusName)
							|| guest.RelationshipStatusName.equalsIgnoreCase("Any")) {
						relationshipStatusNameTv.setVisibility(View.INVISIBLE);
					} else {
						relationshipStatusNameTv.setText(guest.RelationshipStatusName);
					}

					if (TextUtils.isEmpty(guest.LookingForName)
							|| guest.LookingForName.equalsIgnoreCase("Any")) {
						lookingForNameTv.setVisibility(View.INVISIBLE);
					} else {
						lookingForNameTv.setText(guest.LookingForName);
					}

					// Check Canceled or Expired status
					if (dinnerTableBooking.IsCancelled || dinnerTableBooking.IsExpired) {
						// There is no need to add actions
						continue;
					}

					if (dinnerTableBooking.AmIHost) {
						// Current user is host and add actions here as Host
						// permission
						if ("APPLIED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {

							EventInfoPlaceHolder guestInfoPlaceHolder = new EventInfoPlaceHolder();
							guestInfoPlaceHolder.bookingIdx = i;
							guestInfoPlaceHolder.tableBookingInfo = dinnerTableBooking;
							guestInfoPlaceHolder.tableInfoViewPlaceHolder = child;
							guestInfoPlaceHolder.guestInfo = guest;
							
							// User can accept or decline it.
							buttonActions.setVisibility(View.VISIBLE);

							// Host accept the Guest join :
							// AcceptJoinTableRequest
							acceptBtn.setTag(guestInfoPlaceHolder);
							acceptBtn.setOnClickListener(hostAcceptBtnClickListener);

							// Host decline the Guest join :
							// DeclineJoinTableRequestByHost
							declineBtn.setTag(guestInfoPlaceHolder);
							declineBtn.setOnClickListener(hostDeclineBtnClickListener);

							// may be exist Cancel button
							// statusTv.setVisibility(View.VISIBLE);
							// statusTv.setText("Pending");
						} else if ("JOINED AS GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// may be exist Cancel button
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Accepted");
						} else if ("INVITED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// Guest was invited by host(me)
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Invited");
						} else if ("DECLINED BY HOST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("DECLINED BY GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("CANCELLED"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Cancelled");
						}
					} else if (guest.UserID == mUserID) {
						// Current user is Guest and Host invited me, add
						// actions here as Guest permission

						actionPlaceHolder.guestInfo = guest;
						actionPlaceHolder.guestInfoViewPlaceHolder = guestChildView;

						// Show Invitation Message
						if (!TextUtils.isEmpty(guest.InvitationMessage)) {
							invitationMsgTv.setVisibility(View.VISIBLE);
							invitationIcon.setVisibility(View.VISIBLE);
							invitationMsgTv.setText(guest.InvitationMessage);
						}

						if ("APPLIED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// User can accept or decline it.
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Pending");
						} else if ("INVITED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// User can accept or decline it.
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Accepted");
						} else if ("JOINED AS GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// User can cancel.
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Joined");
						} else if ("DECLINED BY HOST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("DECLINED BY GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("CANCELLED"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Cancelled");
						}
					} else {
						// Other guest
						if ("APPLIED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							// User can accept or decline it.
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Pending");
						} else if ("INVITED TO JOIN"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {

							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Invited");
						} else if ("JOINED AS GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Joined");
						} else if ("DECLINED BY HOST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("DECLINED BY GUEST"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Declined");
						} else if ("CANCELLED"
								.equalsIgnoreCase(guest.RequestStatus_RequestStatusName)) {
							statusTv.setVisibility(View.VISIBLE);
							statusTv.setText("Cancelled");
						}
					}
				}
			}

			// Guests Joined Area
			TextView guestsJoinedValue = (TextView) child.findViewById(R.id.guestsJoinedValue);
			guestsJoinedValue.setTypeface(itemContentFont);
			if (dinnerTableBooking.JoinedSeatsCount > 0) {
				guestsJoinedValue.setText(String.format("Guests Joined (%d)",
						dinnerTableBooking.JoinedSeatsCount));
			} else {
				guestsJoinedValue.setVisibility(View.GONE);
			}
			guestsJoinedValue.setVisibility(View.GONE);

			// Cancelled Status Area
			TextView cancelTitle = (TextView) child.findViewById(R.id.cancelTitle);
			cancelTitle.setTypeface(itemContentFont);

			if (!dinnerTableBooking.IsCancelled) {
				cancelTitle.setVisibility(View.GONE);
			}

			// Show Options
			View optionsSelector = child.findViewById(R.id.optionsSelector);
			optionsSelector.setTag(dinnerTableBooking);

			if (dinnerTableBooking.AmIHost && !dinnerTableBooking.IsExpired
					&& !dinnerTableBooking.IsCancelled) {
				optionsSelector.setVisibility(View.VISIBLE);

				View anyoneCanJoinSelect = child.findViewById(R.id.anyoneCanJoinSelect);
				TextView anyoneCanJoinTitleTv = (TextView) child
						.findViewById(R.id.anyoneCanJoinTitleTv);
				anyoneCanJoinTitleTv.setTypeface(itemContentFont);
				RadioButton anyoneCanJoinRadio = (RadioButton) child
						.findViewById(R.id.anyoneCanJoinRadio);

				View guestsApplyToJoinSelect = child.findViewById(R.id.guestsApplyToJoinSelect);
				TextView guestsApplyToJoinTv = (TextView) child
						.findViewById(R.id.guestsApplyToJoinTitleTv);
				guestsApplyToJoinTv.setTypeface(itemContentFont);
				RadioButton guestsApplyToJoinRadio = (RadioButton) child
						.findViewById(R.id.guestsApplyToJoinRadio);

				anyoneCanJoinSelect.setTag(optionsSelector);
				anyoneCanJoinSelect.setOnClickListener(anyoneCanJoinClickListener);

				guestsApplyToJoinSelect.setTag(optionsSelector);
				guestsApplyToJoinSelect.setOnClickListener(guestsApplyToJoinClickListener);

				if ("Private"
						.equalsIgnoreCase(dinnerTableBooking.BookingPrivacyType.BookingPrivacyTypeName)) {
					anyoneCanJoinRadio.setChecked(false);
					guestsApplyToJoinRadio.setChecked(false);
				} else if ("Anyone can join"
						.equalsIgnoreCase(dinnerTableBooking.BookingPrivacyType.BookingPrivacyTypeName)) {
					anyoneCanJoinRadio.setChecked(true);
					guestsApplyToJoinRadio.setChecked(false);
				} else if ("Guests apply to join"
						.equalsIgnoreCase(dinnerTableBooking.BookingPrivacyType.BookingPrivacyTypeName)) {
					anyoneCanJoinRadio.setChecked(false);
					guestsApplyToJoinRadio.setChecked(true);
				}
			} else {
				optionsSelector.setVisibility(View.GONE);
			}

			// Host Action Buttons
			View actionBtnsPanel = child.findViewById(R.id.actionBtnsPanel);
			View hostActionBtns = child.findViewById(R.id.hostActionBtns);
			Button cancelEventBtn = (Button) hostActionBtns.findViewById(R.id.cancelEventBtn);
			Button inviteGuestsBtn = (Button) hostActionBtns.findViewById(R.id.inviteGuestsBtn);
			cancelEventBtn.setTypeface(itemContentFont);
			inviteGuestsBtn.setTypeface(itemContentFont);

			// Guest Action Buttons
			View guestActionBtns = child.findViewById(R.id.guestActionBtns);
			Button declineBtn = (Button) guestActionBtns.findViewById(R.id.declineBtn);
			Button acceptBtn = (Button) guestActionBtns.findViewById(R.id.acceptBtn);

			if (dinnerTableBooking.IsExpired || dinnerTableBooking.IsCancelled) {
				actionBtnsPanel.setVisibility(View.GONE);
			} else {
				if (dinnerTableBooking.AmIHost) {
					guestActionBtns.setVisibility(View.GONE);

					cancelEventBtn.setTag(dinnerTableBooking);
					cancelEventBtn.setOnClickListener(cancelEventButtonListener);

					inviteGuestsBtn.setTag(dinnerTableBooking);
					inviteGuestsBtn.setOnClickListener(shareClickListener);
				} else if (actionPlaceHolder.guestInfo != null
						&& mUserID == actionPlaceHolder.guestInfo.UserID) {
					hostActionBtns.setVisibility(View.GONE);

					if ("INVITED TO JOIN"
							.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)) {
						// User can accept or decline it.
						// User decline the join request :
						// DeclineJoinTableRequestByGuest

						declineBtn.setTag(actionPlaceHolder);
						declineBtn.setOnClickListener(guestDeclineBtnClickListener);

						// User accept the join request :
						// AcceptJoinTableRequest
						acceptBtn.setTag(actionPlaceHolder);
						acceptBtn.setOnClickListener(guestAcceptBtnClickListener);
					} else if ("JOINED AS GUEST"
							.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)
							|| "APPLIED TO JOIN"
									.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)) {

						if ("JOINED AS GUEST"
								.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)) {
							cancelTitle.setVisibility(View.VISIBLE);
							cancelTitle.setText("Accepted");
						}

						acceptBtn.setVisibility(View.INVISIBLE);

						// Host Cancel the JoinedSeat
						declineBtn.setText("CANCEL EVENT");
						declineBtn.setTag(actionPlaceHolder);
						declineBtn.setOnClickListener(guestCancelJoinedSeatBtnClickListener);
					} else if ("CANCELLED"
							.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)) {
						cancelTitle.setVisibility(View.VISIBLE);
						cancelTitle.setText("Cancelled");
						
						guestActionBtns.setVisibility(View.GONE);
					} else if ("DECLINED BY GUEST"
							.equalsIgnoreCase(actionPlaceHolder.guestInfo.RequestStatus_RequestStatusName)) {
						cancelTitle.setVisibility(View.VISIBLE);
						cancelTitle.setText("Declined");
						guestActionBtns.setVisibility(View.GONE);
					} else {
						guestActionBtns.setVisibility(View.GONE);
					}
				} else {
					// There is no case like this, but process exception :)
					actionBtnsPanel.setVisibility(View.GONE);
				}
			}
		}

		for(int i = 0; i < mPendingCount; i++)
		{
			LinearLayout child = (LinearLayout) viewInflator.inflate(
					R.layout.item_dinner_pending, null);
			contentLayout.addView(child);
		}

		// Go to remarked Event information
		if (remarkedBookPanel != null) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					initScrollView(remarkedBookPanel);
				}
			}, 100);
		}
	}

	private void initScrollView(View selectedInfoView) {
		if (selectedInfoView == null) {
			// Nothing to scroll.
			return;
		}

		LinearLayout linearLayout = (LinearLayout) contentLayout;

		Rect rect = new Rect();
		Rect linearLayoutRect = new Rect();
		Rect scrollViewRect = new Rect();
		selectedInfoView.getHitRect(rect);
		linearLayout.getHitRect(linearLayoutRect);
		infoLv.getDrawingRect(scrollViewRect);

		// Get coordinate relative to linear layout. See the note below.
		int correct_expected_bottom_y = linearLayoutRect.top + rect.bottom;

		int dy = correct_expected_bottom_y - scrollViewRect.bottom;
		if (dy > 0) {
			infoLv.scrollBy(0, dy);
		}
	}

	View.OnClickListener detailsTitleClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			TextView titleView = (TextView) view;
			View detailsView = (View) view.getTag();

			if (titleView == null || detailsView == null)
				return;

			if (detailsView.getVisibility() == View.VISIBLE) {
				titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.ic_spinner_tail_yellow, 0);
				detailsView.setVisibility(View.GONE);
			} else {
				titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.ic_spinner_tail_up_yellow, 0);
				detailsView.setVisibility(View.GONE);
				detailsView.setVisibility(View.VISIBLE);
			}
		}
	};

	// Go to the google map and find Direction
	View.OnClickListener viewDirectionClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			DinnerTableBooking tableInfo = (DinnerTableBooking) view.getTag();
			if (tableInfo != null) {
				mainActivity.findDirection(tableInfo.RestaurantLatitude,
						tableInfo.RestaurantLongitude);
			}
		}
	};

	// Accept Button Click Listener

	// Host Accept Button click listener
	View.OnClickListener hostAcceptBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			EventInfoPlaceHolder placeHolder = (EventInfoPlaceHolder) view.getTag();
			if (placeHolder == null)
				return;

			DinnerTableBooking dinnerTableBooking = placeHolder.tableBookingInfo;
			TableBookingGuest guestInfo = placeHolder.guestInfo;
			final View guestInfoView = placeHolder.guestInfoViewPlaceHolder;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.ACCEPT_JOIN_TABLE_REQUEST);
			String soapAction = Utility.NAMESPACE + Utility.ACCEPT_JOIN_TABLE_REQUEST;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);
			request.addProperty("RequestID", guestInfo.RequestStatus_RequestID);
			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_accept_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											// Update UI
											TextView statusTv = (TextView) guestInfoView
													.findViewById(R.id.statusTv);
											statusTv.setVisibility(View.VISIBLE);
											statusTv.setText("Accepted");

											View buttonActions = guestInfoView
													.findViewById(R.id.buttonActions);
											buttonActions.setVisibility(View.GONE);
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}
			else
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};

	// Host Decline Button Click Listener
	View.OnClickListener hostDeclineBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			EventInfoPlaceHolder placeHolder = (EventInfoPlaceHolder) view.getTag();
			if (placeHolder == null)
				return;

			DinnerTableBooking dinnerTableBooking = placeHolder.tableBookingInfo;
			TableBookingGuest guestInfo = placeHolder.guestInfo;
			final View guestInfoView = placeHolder.guestInfoViewPlaceHolder;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.DECLINE_JOIN_TABLE_REQUEST_BYHOST);
			String soapAction = Utility.NAMESPACE + Utility.DECLINE_JOIN_TABLE_REQUEST_BYHOST;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);
			request.addProperty("RequestID", guestInfo.RequestStatus_RequestID);

			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_accept_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											// Update UI
											TextView statusTv = (TextView) guestInfoView
													.findViewById(R.id.statusTv);
											statusTv.setVisibility(View.VISIBLE);
											statusTv.setText("Declined");

											View buttonActions = guestInfoView
													.findViewById(R.id.buttonActions);
											buttonActions.setVisibility(View.GONE);
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}else{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};

	// Accept Button Click Listener
	View.OnClickListener guestAcceptBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			final EventInfoPlaceHolder placeHolder = (EventInfoPlaceHolder) view.getTag();
			if (placeHolder == null)
				return;

			DinnerTableBooking dinnerTableBooking = placeHolder.tableBookingInfo;
			final TableBookingGuest guestInfo = placeHolder.guestInfo;
			final View guestInfoView = placeHolder.guestInfoViewPlaceHolder;
			final View tableInfoView = placeHolder.tableInfoViewPlaceHolder;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.ACCEPT_JOIN_TABLE_REQUEST);
			String soapAction = Utility.NAMESPACE + Utility.ACCEPT_JOIN_TABLE_REQUEST;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);
			request.addProperty("RequestID", guestInfo.RequestStatus_RequestID);

			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_accept_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											guestInfo.RequestStatus_RequestStatusName = "JOINED AS GUEST";

											// Show Main Panel
											TextView cancelTitle = (TextView) tableInfoView
													.findViewById(R.id.cancelTitle);
											cancelTitle.setVisibility(View.VISIBLE);
											cancelTitle.setText("Accepted");

											// Show Guest Panel
											TextView statusTv = (TextView) guestInfoView
													.findViewById(R.id.statusTv);
											statusTv.setVisibility(View.VISIBLE);
											statusTv.setText("Accepted");

											// I already accepted, so I can cancel
											View guestActionBtns = tableInfoView
													.findViewById(R.id.guestActionBtns);
											Button declineBtn = (Button) guestActionBtns
													.findViewById(R.id.declineBtn);
											Button acceptBtn = (Button) guestActionBtns
													.findViewById(R.id.acceptBtn);

											acceptBtn.setVisibility(View.INVISIBLE);

											// Host Cancel the JoinedSeat
											declineBtn.setText("CANCEL EVENT");
											declineBtn.setTag(placeHolder);
											declineBtn
													.setOnClickListener(guestCancelJoinedSeatBtnClickListener);
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}else
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};

	// Guest Dcline Button Click Listener
	View.OnClickListener guestDeclineBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			EventInfoPlaceHolder placeHolder = (EventInfoPlaceHolder) view.getTag();
			if (placeHolder == null)
				return;

			DinnerTableBooking dinnerTableBooking = placeHolder.tableBookingInfo;
			final TableBookingGuest guestInfo = placeHolder.guestInfo;

			final View tableInfoView = placeHolder.tableInfoViewPlaceHolder;
			final View guestInfoView = placeHolder.guestInfoViewPlaceHolder;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.DECLINE_JOIN_TABLE_REQUEST_BYGUEST);
			String soapAction = Utility.NAMESPACE + Utility.DECLINE_JOIN_TABLE_REQUEST_BYGUEST;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);
			request.addProperty("RequestID", guestInfo.RequestStatus_RequestID);

			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_accept_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											guestInfo.RequestStatus_RequestStatusName = "DECLINED BY GUEST";

											// Show Main Panel
											TextView cancelTitle = (TextView) tableInfoView
													.findViewById(R.id.cancelTitle);
											cancelTitle.setVisibility(View.VISIBLE);
											cancelTitle.setText("Declined");

											// Show Guest Panel
											TextView statusTv = (TextView) guestInfoView
													.findViewById(R.id.statusTv);
											statusTv.setVisibility(View.VISIBLE);
											statusTv.setText("Declined");

											View guestActionBtns = tableInfoView
													.findViewById(R.id.guestActionBtns);
											guestActionBtns.setVisibility(View.GONE);
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}else{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};

	// Guest Dcline Button Click Listener
	View.OnClickListener guestCancelJoinedSeatBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			EventInfoPlaceHolder placeHolder = (EventInfoPlaceHolder) view.getTag();
			if (placeHolder == null)
				return;

			DinnerTableBooking dinnerTableBooking = placeHolder.tableBookingInfo;
			final TableBookingGuest guestInfo = placeHolder.guestInfo;
			final View guestInfoView = placeHolder.guestInfoViewPlaceHolder;
			final View tableInfoView = placeHolder.tableInfoViewPlaceHolder;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.CANCEL_JOINED_SEAT_BYGUEST);
			String soapAction = Utility.NAMESPACE + Utility.CANCEL_JOINED_SEAT_BYGUEST;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);
			request.addProperty("RequestID", guestInfo.RequestStatus_RequestID);

			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_accept_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											guestInfo.RequestStatus_RequestStatusName = "CANCELLED";

											// Show Main Panel
											TextView cancelTitle = (TextView) tableInfoView
													.findViewById(R.id.cancelTitle);
											cancelTitle.setVisibility(View.VISIBLE);
											cancelTitle.setText("Cancelled");

											// Show Guest Panel
											TextView statusTv = (TextView) guestInfoView
													.findViewById(R.id.statusTv);
											statusTv.setVisibility(View.VISIBLE);
											statusTv.setText("Cancelled");

											View guestActionBtns = tableInfoView
													.findViewById(R.id.guestActionBtns);
											guestActionBtns.setVisibility(View.GONE);
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			} else
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};

	// Host Cancel the Event
	View.OnClickListener cancelEventButtonListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			DinnerTableBooking dinnerTableBooking = (DinnerTableBooking) view.getTag();

			if (dinnerTableBooking == null)
				return;

			SoapObject request = new SoapObject(Utility.NAMESPACE,
					Utility.CANCEL_RESTAURANT_TABLE_BOOKING);
			String soapAction = Utility.NAMESPACE + Utility.CANCEL_RESTAURANT_TABLE_BOOKING;

			request.addProperty("UserID", mUserID);
			request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
			request.addProperty("TableID", dinnerTableBooking.TableID);
			request.addProperty("BookingID", dinnerTableBooking.BookingID);

			if(GlobalHelper.isConnectingToInternet(mainActivity))
			{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
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
										boolean error = false;
										String message = getString(R.string.failed_cancel_event);

										if (respObj.has("IsError") && !respObj.isNull("IsError")) {
											error = respObj.getBoolean("IsError");
											if (respObj.has("Message"))
												message = respObj.getString("Message");
										}

										if (error) {
											showMessage(message);
										} else {
											updateData();
										}
									} catch (SoapFault e) {
										String errorMsg = (String) e.faultstring;
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									} catch (Exception e) {
										String errorMsg = (String) e.getMessage();
										if (TextUtils.isEmpty(errorMsg)) {
											errorMsg = getString(R.string.error_loading);
										}
										showMessage(errorMsg);
									}
								} else {
									String errorMsg = (String) result;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									showMessage(errorMsg);
								}
								mainActivity.hideProgressDialog();
							}
						}, false);
				mainActivity.showProgressDialog();
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}else{
				AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
						new AnyTaskCallback() {

							@Override
							public void onResult(boolean success, Object result) {

							}
						}, false);
				BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			}

		}
	};


	View.OnClickListener anyoneCanJoinClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			View optionsSelector = (View) view.getTag();
			if (optionsSelector == null)
				return;

			DinnerTableBooking dinnerTableBooking = (DinnerTableBooking) optionsSelector.getTag();
			if (dinnerTableBooking == null)
				return;

			RadioButton anyoneCanJoinRadio = (RadioButton) optionsSelector
					.findViewById(R.id.anyoneCanJoinRadio);
			RadioButton guestsApplyToJoinRadio = (RadioButton) optionsSelector
					.findViewById(R.id.guestsApplyToJoinRadio);

			if (anyoneCanJoinRadio.isChecked()) {
				anyoneCanJoinRadio.setChecked(false);

				// Check Private status
				if (!guestsApplyToJoinRadio.isChecked()) {
					updateDinnerTableBookingAsPrivate(dinnerTableBooking);
				}
			} else {
				anyoneCanJoinRadio.setChecked(true);
				guestsApplyToJoinRadio.setChecked(false);

				updateDinnerTableBookingAsOpen(dinnerTableBooking);
			}
		}
	};

	View.OnClickListener guestsApplyToJoinClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			View optionsSelector = (View) view.getTag();
			if (optionsSelector == null)
				return;

			DinnerTableBooking dinnerTableBooking = (DinnerTableBooking) optionsSelector.getTag();
			if (dinnerTableBooking == null)
				return;

			RadioButton anyoneCanJoinRadio = (RadioButton) optionsSelector
					.findViewById(R.id.anyoneCanJoinRadio);
			RadioButton guestsApplyToJoinRadio = (RadioButton) optionsSelector
					.findViewById(R.id.guestsApplyToJoinRadio);

			if (guestsApplyToJoinRadio.isChecked()) {
				guestsApplyToJoinRadio.setChecked(false);

				// Check Private status
				if (!anyoneCanJoinRadio.isChecked()) {
					updateDinnerTableBookingAsPrivate(dinnerTableBooking);
				}
			} else {
				guestsApplyToJoinRadio.setChecked(true);
				anyoneCanJoinRadio.setChecked(false);

				updateDinnerTableBookingAsGuestInvitation(dinnerTableBooking);
			}
		}
	};

	private void updateDinnerTableBookingAsPrivate(DinnerTableBooking dinnerTableBooking) {
		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.UPDATE_DINNERTABLEBOOKING_ASPRIVATE);
		String soapAction = Utility.NAMESPACE + Utility.UPDATE_DINNERTABLEBOOKING_ASPRIVATE;
		request.addProperty("UserID", mUserID);
		request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
		request.addProperty("TableID", dinnerTableBooking.TableID);
		request.addProperty("BookingID", dinnerTableBooking.BookingID);

		if(GlobalHelper.isConnectingToInternet(mainActivity))
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							tableInfoList.clear();
							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope.getResponse();
									String responseString = response.toString();
									JSONObject responseObj = new JSONObject(responseString);

									updateData();
								} catch (SoapFault e) {
									String errorMsg = (String) e.faultstring;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								} catch (Exception e) {
									String errorMsg = (String) e.getMessage();
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								}
							} else {
								String errorMsg = (String) result;
								if (TextUtils.isEmpty(errorMsg)) {
									errorMsg = getString(R.string.error_loading);
								}
								msg(errorMsg);
							}
							mainActivity.hideProgressDialog();
						}
					}, false);
			mainActivity.showProgressDialog();
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}else{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {

						}
					}, false);
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}

	}

	private void updateDinnerTableBookingAsOpen(DinnerTableBooking dinnerTableBooking) {
		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.UPDATE_DINNERTABLEBOOKING_ASOPEN);
		String soapAction = Utility.NAMESPACE + Utility.UPDATE_DINNERTABLEBOOKING_ASOPEN;
		request.addProperty("UserID", mUserID);
		request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
		request.addProperty("TableID", dinnerTableBooking.TableID);
		request.addProperty("BookingID", dinnerTableBooking.BookingID);

		if(GlobalHelper.isConnectingToInternet(mainActivity))
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							tableInfoList.clear();
							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope.getResponse();
									String responseString = response.toString();
									JSONObject responseObj = new JSONObject(responseString);

									updateData();
								} catch (SoapFault e) {
									String errorMsg = (String) e.faultstring;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								} catch (Exception e) {
									String errorMsg = (String) e.getMessage();
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								}
							} else {
								String errorMsg = (String) result;
								if (TextUtils.isEmpty(errorMsg)) {
									errorMsg = getString(R.string.error_loading);
								}
								msg(errorMsg);
							}
							mainActivity.hideProgressDialog();
						}
					}, false);
			mainActivity.showProgressDialog();
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}else
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {

						}
					}, false);
			mainActivity.showProgressDialog();
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}

	}

	private void updateDinnerTableBookingAsGuestInvitation(DinnerTableBooking dinnerTableBooking) {
		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.UPDATE_DINNERTABLEBOOKING_ASGUESTINVITATION);
		String soapAction = Utility.NAMESPACE + Utility.UPDATE_DINNERTABLEBOOKING_ASGUESTINVITATION;
		request.addProperty("UserID", mUserID);
		request.addProperty("RestaurantID", dinnerTableBooking.RestaurantID);
		request.addProperty("TableID", dinnerTableBooking.TableID);
		request.addProperty("BookingID", dinnerTableBooking.BookingID);

		if( GlobalHelper.isConnectingToInternet(mainActivity))
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							tableInfoList.clear();

							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope.getResponse();
									String responseString = response.toString();
									JSONObject responseObj = new JSONObject(responseString);

									updateData();
								} catch (SoapFault e) {
									String errorMsg = (String) e.faultstring;
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								} catch (Exception e) {
									String errorMsg = (String) e.getMessage();
									if (TextUtils.isEmpty(errorMsg)) {
										errorMsg = getString(R.string.error_loading);
									}
									msg(errorMsg);
								}
							} else {
								String errorMsg = (String) result;
								if (TextUtils.isEmpty(errorMsg)) {
									errorMsg = getString(R.string.error_loading);
								}
								msg(errorMsg);
							}
							mainActivity.hideProgressDialog();
						}
					}, false);
			mainActivity.showProgressDialog();
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}
		else
		{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {

						}
					}, false);
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
		}

	}

	private class EventInfoPlaceHolder {
		int bookingIdx;
		DinnerTableBooking tableBookingInfo;
		TableBookingGuest guestInfo;
		View tableInfoViewPlaceHolder;
		View guestInfoViewPlaceHolder;
	}

	/*
	 * {"Bookings":[{"BookingID":10835,"BookingDate":"02-03-2016","TimeFrom":"18:00"
	 * ,"TimeTo":"20:00","FirstName":"Diner 1","LastName":"Diner 1","Email":
	 * "Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"TableID":10272,"Promotion"
	 * :{"PromotionID":0
	 * ,"PromotionName":null,"PromotionType":null},"BookingType"
	 * :{"BookingTypeID"
	 * :4,"BookingTypeName":"DineUnite","SlotType":{"SlotTypeID"
	 * :3,"SlotTypeName"
	 * :"Dinner"}},"BookingPrivacyType":{"BookingPrivacyTypeID":
	 * 2,"BookingPrivacyTypeName"
	 * :"Open invitation, anyone can join"},"JoinedSeatsCount"
	 * :2,"BookedSeatsCount"
	 * :7,"IsConfirmed":false,"IsCancelled":false,"IsExpired"
	 * :false,"AmIHost":true
	 * ,"AmIGuest":false,"RemaininDays":0,"RemainingHours":6
	 * ,"RemainingMinutes":15
	 * ,"BookingRef":null,"Host":{"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs"
	 * ,"FirstName":"Diner 1","LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :
	 * "Separated","LookingForName":"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests":[{"RequestStatus":{"RequestStatusID":2,
	 * "RequestStatusName"
	 * :"Accepted"},"UserID":1615,"ProfilePicture":null,"Title"
	 * :"Mrs","FirstName"
	 * :"Diner 1","LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated"
	 * ,"LookingForName":"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName"
	 * :"Any"}],"RestaurantID":4,"RestaurantName":"Test123","RestaurantImage"
	 * :"http://www.dineunite.com/Data/5/Restaurants/4/Images/ZeEBlfV.png"
	 * ,"PostCode"
	 * :"W2 5LE","RestaurantAddress":"127 Ledbury LONDON"},{"BookingID"
	 * :10835,"BookingDate"
	 * :"02-03-2016","TimeFrom":"18:00","TimeTo":"20:00","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Email":"Diner1@gmail.com","Phone":"","TitleOptionID"
	 * :1,"TableID"
	 * :10272,"Promotion":{"PromotionID":0,"PromotionName":null,"PromotionType"
	 * :null
	 * },"BookingType":{"BookingTypeID":4,"BookingTypeName":"DineUnite","SlotType"
	 * :{"SlotTypeID":3,"SlotTypeName":"Dinner"}},"BookingPrivacyType":{
	 * "BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"
	 * },"JoinedSeatsCount"
	 * :2,"BookedSeatsCount":7,"IsConfirmed":false,"IsCancelled"
	 * :false,"IsExpired"
	 * :false,"AmIHost":true,"AmIGuest":false,"RemaininDays":0,
	 * "RemainingHours":6
	 * ,"RemainingMinutes":15,"BookingRef":null,"Host":{"UserID"
	 * :1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName":
	 * "Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests"
	 * :[{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}],"RestaurantID":4,"RestaurantName":"Test123","RestaurantImage":
	 * "http://www.dineunite.com/Data/5/Restaurants/4/Images/ZeEBlfV.png"
	 * ,"PostCode":"W2 5LE","RestaurantAddress":"127 Ledbury LONDON"}]}
	 */

	/*
	 * <string xmlns="http://tempuri.org/">
	 * {"Bookings":[{"BookingID":10834,"BookingDate"
	 * :"22-05-2016","TimeFrom":"18:00"
	 * ,"TimeTo":"20:00","FirstName":"Diner 1","LastName"
	 * :"Diner 1","Email":"Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"Table"
	 * :{"TableID":10270,"TableNumber":2,"TotalSeatsCount"
	 * :4,"RestaurantID":4,"IsBooked"
	 * :true,"IsAdvertised":true,"IsOpenTable":true
	 * ,"IsActive":true},"Promotion":
	 * {"PromotionID":0,"PromotionName":null,"PromotionType"
	 * :null},"BookingType":
	 * {"BookingTypeID":4,"BookingTypeName":"DineUnite","SlotType"
	 * :{"SlotTypeID":
	 * 3,"SlotTypeName":"Dinner"}},"BookingPrivacyType":{"BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"},
	 * "JoinedSeatsCount"
	 * :4,"BookedSeatsCount":4,"IsConfirmed":true,"IsCancelled"
	 * :false,"IsExpired"
	 * :false,"AmIHost":true,"AmIGuest":false,"RemaininDays":81
	 * ,"RemainingHours":
	 * 14,"RemainingMinutes":42,"BookingRef":"4210834","Host":{
	 * "UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName":
	 * "Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests"
	 * :[{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"},{"RequestStatus"
	 * :{"RequestStatusID":2,"RequestStatusName":"Accepted"
	 * },"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1",
	 * "LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName"
	 * :"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},{
	 * "RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}]},
	 * {"BookingID":10834,"BookingDate":"22-05-2016","TimeFrom":"18:00"
	 * ,"TimeTo":
	 * "20:00","FirstName":"Diner 1","LastName":"Diner 1","Email":"Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"Table":{"TableID":10270,"TableNumber":2,
	 * "TotalSeatsCount"
	 * :4,"RestaurantID":4,"IsBooked":true,"IsAdvertised":true,"IsOpenTable"
	 * :true,"IsActive":true},"Promotion":{"PromotionID":0,"PromotionName":null,
	 * "PromotionType"
	 * :null},"BookingType":{"BookingTypeID":4,"BookingTypeName":"DineUnite"
	 * ,"SlotType"
	 * :{"SlotTypeID":3,"SlotTypeName":"Dinner"}},"BookingPrivacyType"
	 * :{"BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"
	 * },"JoinedSeatsCount"
	 * :4,"BookedSeatsCount":4,"IsConfirmed":true,"IsCancelled"
	 * :false,"IsExpired"
	 * :false,"AmIHost":true,"AmIGuest":false,"RemaininDays":81
	 * ,"RemainingHours":
	 * 14,"RemainingMinutes":42,"BookingRef":"4210834","Host":{
	 * "UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName":
	 * "Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests"
	 * :[{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"},{"RequestStatus"
	 * :{"RequestStatusID":2,"RequestStatusName":"Accepted"
	 * },"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1",
	 * "LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName"
	 * :"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},{
	 * "RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}]},
	 * {"BookingID":10834,"BookingDate":"22-05-2016","TimeFrom":"18:00"
	 * ,"TimeTo":
	 * "20:00","FirstName":"Diner 1","LastName":"Diner 1","Email":"Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"Table":{"TableID":10270,"TableNumber":2,
	 * "TotalSeatsCount"
	 * :4,"RestaurantID":4,"IsBooked":true,"IsAdvertised":true,"IsOpenTable"
	 * :true,"IsActive":true},"Promotion":{"PromotionID":0,"PromotionName":null,
	 * "PromotionType"
	 * :null},"BookingType":{"BookingTypeID":4,"BookingTypeName":"DineUnite"
	 * ,"SlotType"
	 * :{"SlotTypeID":3,"SlotTypeName":"Dinner"}},"BookingPrivacyType"
	 * :{"BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"
	 * },"JoinedSeatsCount"
	 * :4,"BookedSeatsCount":4,"IsConfirmed":true,"IsCancelled"
	 * :false,"IsExpired"
	 * :false,"AmIHost":true,"AmIGuest":false,"RemaininDays":81
	 * ,"RemainingHours":
	 * 14,"RemainingMinutes":42,"BookingRef":"4210834","Host":{
	 * "UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName":
	 * "Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests"
	 * :[{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"},{"RequestStatus"
	 * :{"RequestStatusID":2,"RequestStatusName":"Accepted"
	 * },"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1",
	 * "LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated","LookingForName"
	 * :"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName":"Any"},{
	 * "RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID"
	 * :1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName"
	 * :"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName":
	 * "Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}]},
	 * {"BookingID":10833,"BookingDate":"24-02-2016","TimeFrom":"18:00"
	 * ,"TimeTo":
	 * "20:00","FirstName":"Diner 1","LastName":"Diner 1","Email":"Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"Table":{"TableID":10270,"TableNumber":2,
	 * "TotalSeatsCount"
	 * :4,"RestaurantID":4,"IsBooked":true,"IsAdvertised":true,"IsOpenTable"
	 * :true
	 * ,"IsActive":false},"Promotion":{"PromotionID":0,"PromotionName":null,
	 * "PromotionType"
	 * :null},"BookingType":{"BookingTypeID":4,"BookingTypeName":"DineUnite"
	 * ,"SlotType"
	 * :{"SlotTypeID":3,"SlotTypeName":"Dinner"}},"BookingPrivacyType"
	 * :{"BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"
	 * },"JoinedSeatsCount"
	 * :2,"BookedSeatsCount":4,"IsConfirmed":true,"IsCancelled"
	 * :true,"IsExpired":
	 * true,"AmIHost":true,"AmIGuest":false,"RemaininDays":0,"RemainingHours"
	 * :0,"RemainingMinutes"
	 * :0,"BookingRef":"4210833","Host":{"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName":"Diner 1","Age":19,
	 * "RelationshipStatusName"
	 * :"Separated","LookingForName":"Romantic Relationship"
	 * ,"LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests":[{"RequestStatus"
	 * :{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated"
	 * ,"LookingForName":"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName"
	 * :"Any"},{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"
	 * },"UserID":1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1",
	 * "LastName":"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName"
	 * :"Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}]}
	 * ,{"BookingID":10833,"BookingDate":"24-02-2016","TimeFrom":"18:00"
	 * ,"TimeTo":"20:00","FirstName":"Diner 1","LastName":"Diner 1","Email":
	 * "Diner1@gmail.com"
	 * ,"Phone":"","TitleOptionID":1,"Table":{"TableID":10270,"TableNumber"
	 * :2,"TotalSeatsCount"
	 * :4,"RestaurantID":4,"IsBooked":true,"IsAdvertised":true
	 * ,"IsOpenTable":true
	 * ,"IsActive":false},"Promotion":{"PromotionID":0,"PromotionName"
	 * :null,"PromotionType"
	 * :null},"BookingType":{"BookingTypeID":4,"BookingTypeName"
	 * :"DineUnite","SlotType"
	 * :{"SlotTypeID":3,"SlotTypeName":"Dinner"}},"BookingPrivacyType"
	 * :{"BookingPrivacyTypeID"
	 * :2,"BookingPrivacyTypeName":"Open invitation, anyone can join"
	 * },"JoinedSeatsCount"
	 * :2,"BookedSeatsCount":4,"IsConfirmed":true,"IsCancelled"
	 * :true,"IsExpired":
	 * true,"AmIHost":true,"AmIGuest":false,"RemaininDays":0,"RemainingHours"
	 * :0,"RemainingMinutes"
	 * :0,"BookingRef":"4210833","Host":{"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName":"Diner 1","LastName":"Diner 1","Age":19,
	 * "RelationshipStatusName"
	 * :"Separated","LookingForName":"Romantic Relationship"
	 * ,"LanguageName":"English"
	 * ,"EthnicityName":"Any"},"Guests":[{"RequestStatus"
	 * :{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"},"UserID":1615,"ProfilePicture"
	 * :null,"Title":"Mrs","FirstName"
	 * :"Diner 1","LastName":"Diner 1","Age":19,"RelationshipStatusName"
	 * :"Separated"
	 * ,"LookingForName":"Romantic Relationship","LanguageName":"English"
	 * ,"EthnicityName"
	 * :"Any"},{"RequestStatus":{"RequestStatusID":2,"RequestStatusName"
	 * :"Accepted"
	 * },"UserID":1615,"ProfilePicture":null,"Title":"Mrs","FirstName"
	 * :"Diner 1",
	 * "LastName":"Diner 1","Age":19,"RelationshipStatusName":"Separated"
	 * ,"LookingForName"
	 * :"Romantic Relationship","LanguageName":"English","EthnicityName"
	 * :"Any"}]}]} </string>
	 */
}
