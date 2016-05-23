package com.paulavasile.dineunite.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.internal.ChannelSendFileResponse;
import com.paulavasile.dineunite.DinnerEventsActivity;
import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.DatabaseHelper;
import com.paulavasile.dineunite.Global.GlobalHelper;
import com.paulavasile.dineunite.MainActivity;
import com.paulavasile.dineunite.ProfileActivity;
import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.data.FoundTableInfo;
import com.paulavasile.dineunite.data.TableBookingGuest;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;
import com.paulavasile.dineunite.views.CustomSpinner;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SocialTableFragment extends BaseFragment {

	List<FoundTableInfo> tableInfoList = new ArrayList<>();
	List<FoundTableInfo> filteredInfoList = new ArrayList<FoundTableInfo>();

	ListView tableLv;
	CustomListAdapter tableInfoAdapter;

	ViewGroup contentLayout;
	View nodataIv;
	LayoutInflater viewInflator;
	Typeface itemTitleFont;
	Typeface itemContentFont;

	// Sort Options
	CustomSpinner sortList;

	private static final int SORT_NEAREST = 0;
	private static final int SORT_EARLIST = 1;
	private static final int SORT_LATEST = 2;
	private static final int SORT_HIGHESTS_GUESTS = 3;
	int currentSortOption = SORT_NEAREST;

	public int mType = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		init();

		parentView = inflater.inflate(R.layout.fragment_social_table, container, false);

		progress = new ProgressDialog(mainActivity);
		progress.setCancelable(false);

		setViewTypeface(parentView.findViewById(R.id.queryTitleTv), mainActivity.getRegularTypeface());

		Bundle data = getArguments();
		mType = data.getInt("type");

		if( mType == 0 )
		{
			parentView.findViewById(R.id.privateTablesBtn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(mainActivity, "Private Tables Button Clicked.", Toast.LENGTH_LONG).show();
				}
			});
			parentView.findViewById(R.id.privateTablesBtn).setVisibility(View.VISIBLE);
		}
		else
		{
			parentView.findViewById(R.id.privateTablesBtn).setVisibility(View.GONE);
		}
		loadPendingData();
		// -----------------------------------

		itemTitleFont = mainActivity.getRegularTypeface();
		itemContentFont = mainActivity.getRegularTypeface();

		contentLayout = (ViewGroup) parentView.findViewById(R.id.contentLayout);
		nodataIv = parentView.findViewById(R.id.nodataIv);

		ArrayList<String> sortTitles = new ArrayList<>();
		sortTitles.add("Distance (nearest first)");
		sortTitles.add("Time (earliest first)");
		sortTitles.add("Time (latest first)");
		sortTitles.add("Number of guests (highest first)");

		sortList = (CustomSpinner) parentView.findViewById(R.id.sortList);
		CustomFontSpinnerAdapter sortOptionsAdapter = new CustomFontSpinnerAdapter(
				mainActivity, R.layout.spinner_item, sortTitles);
		sortOptionsAdapter.setDropDownViewResource(R.layout.spinner_list_item);
		sortList.setAdapter(sortOptionsAdapter);
		sortList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				sortTableList(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		tableLv = (ListView)parentView.findViewById(R.id.infoLv);
		tableLv.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastItem = firstVisibleItem + visibleItemCount;
				if(lastItem == totalItemCount){
					loadDateData();
				}
			}
		});
		tableInfoAdapter = new CustomListAdapter();
		tableLv.setAdapter(tableInfoAdapter);

		tableInfoList.clear();
		loadDateData();
		GlobalHelper.__gCurTable = this;


		return parentView;
	}

	Handler mHandler1 = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			loadPendingData();
			tableInfoAdapter.notifyDataSetChanged();
		}
	};

	public void refresh()
	{
		mHandler1.sendEmptyMessageDelayed(0, 100);
	}

	List<Integer> mPendingList = null;
	private void loadPendingData() {
		mPendingList = BackgroundProcessor.getBackgroundProcessor(mainActivity).getPendingPosition(mType);
	}

	Handler mHandler = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			DatabaseHelper helper = DatabaseHelper.getInstance(mainActivity);
			if( mType == 0 )
			{
				System.gc();
				List<FoundTableInfo> newTableInfoLists = helper.getSocialTable();
				if( newTableInfoLists.size() != tableInfoList.size() )
				{
					tableInfoList = newTableInfoLists;
					sortTableList(currentSortOption);
				}
				else if( BackgroundProcessor.getBackgroundProcessor(mainActivity).isRemainingTask() && GlobalHelper.isConnectingToInternet(mainActivity))
				{
					mHandler.sendEmptyMessageDelayed(0, 1000);
					return;
				}
			}
			else
			{
				System.gc();
				List<FoundTableInfo> newTableInfoLists = helper.getRestaurantTable();
				if( newTableInfoLists.size() != tableInfoList.size() )
				{
					tableInfoList = newTableInfoLists;
					sortTableList(currentSortOption);
				}
				else if( BackgroundProcessor.getBackgroundProcessor(mainActivity).isRemainingTask() )
				{
					mHandler.sendEmptyMessageDelayed(0, 1000);
					return;
				}
			}
			progress.hide();
			bCalling = false;
		}
	};

	boolean bCalling = false;
	private void loadDateData() {
		if(bCalling == true) return;
		bCalling = true;
		progress.show();
		progress.setContentView(R.layout.dialog_loading);
		mHandler.sendEmptyMessage(0);
	}

	private final static Comparator<FoundTableInfo> distanceComparator = new Comparator<FoundTableInfo>() {

		@Override
		public int compare(FoundTableInfo object1, FoundTableInfo object2) {
			if (object1.RestaurantDistance == object2.RestaurantDistance) {
				return 0;
			} else if (object1.RestaurantDistance > object2.RestaurantDistance) {
				return 1;
			} else {
				return -1;
			}
		}
	};

	private final static Comparator<FoundTableInfo> earliertimeComparator = new Comparator<FoundTableInfo>() {

		@Override
		public int compare(FoundTableInfo object1, FoundTableInfo object2) {
			return object1.compare(object2);
		}
	};

	private final static Comparator<FoundTableInfo> latertimeComparator = new Comparator<FoundTableInfo>() {

		@Override
		public int compare(FoundTableInfo object1, FoundTableInfo object2) {
			return -1 * object1.compare(object2);
		}
	};

	private final static Comparator<FoundTableInfo> higherGuestsComparator = new Comparator<FoundTableInfo>() {

		@Override
		public int compare(FoundTableInfo object1, FoundTableInfo object2) {
			int guests1 = object1.getGuestNumber();
			int guests2 = object2.getGuestNumber();
			if (guests1 > guests2) {
				return -1;
			} else if (guests1 < guests2) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private void sortTableList(int position) {
		currentSortOption = position;
		if( tableInfoList == null )
		{
			filteredInfoList.clear();
			return;
		}

		if (position == SORT_NEAREST) {
			filteredInfoList.clear();
			for (FoundTableInfo info : tableInfoList)
				filteredInfoList.add(info);
			Collections.sort(filteredInfoList, distanceComparator);
		} else if (position == SORT_EARLIST) {
			filteredInfoList.clear();
			for (FoundTableInfo info : tableInfoList)
				filteredInfoList.add(info);
			Collections.sort(filteredInfoList, earliertimeComparator);
		} else if (position == SORT_LATEST) {
			filteredInfoList.clear();
			for (FoundTableInfo info : tableInfoList)
				filteredInfoList.add(info);
			Collections.sort(filteredInfoList, latertimeComparator);
		} else if (position == SORT_HIGHESTS_GUESTS) {
			filteredInfoList.clear();
			for (FoundTableInfo info : tableInfoList)
				filteredInfoList.add(info);
			Collections.sort(filteredInfoList, higherGuestsComparator);
		}

		if( filteredInfoList.size() > 0 )
		{
			nodataIv.setVisibility(View.GONE);
		}
		else
		{
			nodataIv.setVisibility(View.VISIBLE);
		}
		tableInfoAdapter.notifyDataSetChanged();
//		showTableOptions();
	}

	private class CustomListAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			if( filteredInfoList == null ) return 0;
			return filteredInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			if( filteredInfoList == null ) return null;
			return filteredInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View child, ViewGroup parent) {
			if (viewInflator == null)
				viewInflator = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if( child == null )
			{
				child = viewInflator.inflate(
						R.layout.item_table_options, null);
			}
			FoundTableInfo tableInfo = (FoundTableInfo)getItem(position);
			if( tableInfo == null ) return child;

			TextView daysInfoTv, daysInfoTitleTv, hoursInfoTv, hoursInfoTitleTv, minsInfoTv, minsInfoTitleTv;
			daysInfoTv = (TextView) child.findViewById(R.id.daysInfoTv);
			daysInfoTitleTv = (TextView) child
					.findViewById(R.id.daysInfoTitleTv);
			hoursInfoTv = (TextView) child.findViewById(R.id.hoursInfoTv);
			hoursInfoTitleTv = (TextView) child
					.findViewById(R.id.hoursInfoTitleTv);
			minsInfoTv = (TextView) child.findViewById(R.id.minsInfoTv);
			minsInfoTitleTv = (TextView) child
					.findViewById(R.id.minsInfoTitleTv);

			daysInfoTv.setTypeface(itemTitleFont);
			daysInfoTitleTv.setTypeface(itemTitleFont);
			hoursInfoTv.setTypeface(itemTitleFont);
			hoursInfoTitleTv.setTypeface(itemTitleFont);
			minsInfoTv.setTypeface(itemTitleFont);
			minsInfoTitleTv.setTypeface(itemTitleFont);

			daysInfoTv.setText(tableInfo.RemaininDays + "");
			hoursInfoTv.setText(tableInfo.RemainingHours + "");
			minsInfoTv.setText(tableInfo.RemainingMinutes + "");

			// Table Status
			TextView tableStatusTv = (TextView) child
					.findViewById(R.id.tableStatusTv);
			tableStatusTv.setTypeface(itemTitleFont);
			tableStatusTv.setText(tableInfo.TableStatusName);

			// Second Row
			TextView dateTv = (TextView) child.findViewById(R.id.dateTv);
			TextView startTimeTv = (TextView) child
					.findViewById(R.id.startTimeTv);
			dateTv.setTypeface(itemContentFont);
			startTimeTv.setTypeface(itemContentFont);
			dateTv.setText(tableInfo.SearchDate);
			startTimeTv.setText(tableInfo.FromTime);

			// Restaurant Details Information
			TextView restaurantDetailsTitle = (TextView) child
					.findViewById(R.id.restaurantDetailsTitle);
			View restaurantDetails = child.findViewById(R.id.restaurantDetails);
			restaurantDetailsTitle.setTypeface(itemContentFont);
			restaurantDetailsTitle.setTag(restaurantDetails);
			restaurantDetailsTitle
					.setOnClickListener(detailsTitleClickListener);
			restaurantDetails.setVisibility(View.GONE);

			restaurantDetailsTitle.setText(String.format("%s (%.1f miles)",
					tableInfo.RestaurantName, tableInfo.RestaurantDistance));

			// Show map position
			ImageView restaurantIv = (ImageView) child
					.findViewById(R.id.restaurantIv);
			imageLoader.displayImage(
					mainActivity.getMapPlaceImageUrl(tableInfo.RestaurantLatitude,
							tableInfo.RestaurantLongitude), restaurantIv,
					options);

			// map direction
			ImageView directionBtn = (ImageView) child
					.findViewById(R.id.directionBtn);
			directionBtn.setTag(tableInfo);
			directionBtn.setOnClickListener(viewDirectionClickListener);

			TextView durationTv = (TextView) child
					.findViewById(R.id.durationTv);
			TextView address1Tv = (TextView) child
					.findViewById(R.id.address1Tv);
			TextView address2Tv = (TextView) child
					.findViewById(R.id.address2Tv);

			durationTv.setTypeface(itemContentFont);
			address1Tv.setTypeface(itemContentFont);
			address2Tv.setTypeface(itemContentFont);

			durationTv.setText(String.format("%s - %s", tableInfo.FromTime,
					tableInfo.ToTime));
			address1Tv.setText(tableInfo.RestaurantAddress);
			address2Tv.setText(tableInfo.PostCode);

			// Your Host Block
			View yourHostPanel = child.findViewById(R.id.yourHostPanel);
			TextView yourHostTitle = (TextView) child
					.findViewById(R.id.yourHostTitle);
			ViewGroup yourHostDetails = (ViewGroup) child
					.findViewById(R.id.yourHostDetails);
			ViewGroup yourHostList = (ViewGroup) child
					.findViewById(R.id.yourHostList);
			yourHostTitle.setTypeface(itemContentFont);
			yourHostTitle.setTag(yourHostDetails);
			yourHostTitle.setOnClickListener(detailsTitleClickListener);
			yourHostDetails.setVisibility(View.GONE);
			if (/* dinnerTableBooking.AmIHost || */tableInfo.Host == null
					|| tableInfo.Host.UserID == 0) {
				yourHostPanel.setVisibility(View.GONE);
			} else {
				// Show Host Information
				ImageView hostPictureIv = (ImageView) yourHostList
						.findViewById(R.id.hostPictureIv);
				TextView hostNameTv = (TextView) yourHostList
						.findViewById(R.id.hostNameTv);
				TextView hostAgeTv = (TextView) yourHostList
						.findViewById(R.id.hostAgeTv);
				TextView relationshipStatusNameTv = (TextView) yourHostList
						.findViewById(R.id.relationshipStatusNameTv);
				TextView lookingForNameTv = (TextView) yourHostList
						.findViewById(R.id.lookingForNameTv);

				hostNameTv.setTypeface(itemContentFont);
				hostAgeTv.setTypeface(itemContentFont);
				relationshipStatusNameTv.setTypeface(itemContentFont);
				lookingForNameTv.setTypeface(itemContentFont);

				if (!TextUtils.isEmpty(tableInfo.Host.ProfilePicture)) {
					imageLoader.displayImage(tableInfo.Host.ProfilePicture,
							hostPictureIv, options);
				}
				if (TextUtils.isEmpty(tableInfo.Host.LastName)) {
					hostNameTv.setText(String.format("%s",
							tableInfo.Host.FirstName));
				} else {
					hostNameTv.setText(String.format("%s %s",
							tableInfo.Host.FirstName,
							tableInfo.Host.LastName.substring(0, 1)));
				}

				hostAgeTv.setText(String.format("%d years old",
						tableInfo.Host.Age));
				relationshipStatusNameTv
						.setText(tableInfo.Host.RelationshipStatusName);
				lookingForNameTv.setText(tableInfo.Host.LookingForName);
			}

			// Guest Block
			ViewGroup guestsPanel = (ViewGroup) child
					.findViewById(R.id.guestsPanel);
			if (tableInfo.Guests == null || tableInfo.Guests.size() == 0) {
				guestsPanel.setVisibility(View.GONE);
			} else {
				TextView guestsTitle = (TextView) guestsPanel
						.findViewById(R.id.guestsTitle);
				ViewGroup guestsDetails = (ViewGroup) guestsPanel
						.findViewById(R.id.guestsDetails);
				ViewGroup guestsListContent = (ViewGroup) guestsPanel
						.findViewById(R.id.guestsListContent);
				guestsTitle.setTypeface(itemContentFont);
				guestsTitle.setTag(guestsDetails);
				guestsTitle.setOnClickListener(detailsTitleClickListener);
				guestsDetails.setVisibility(View.GONE);
				guestsListContent.removeAllViews();

				for (TableBookingGuest guest : tableInfo.Guests) {
					LinearLayout guestChild = (LinearLayout) viewInflator
							.inflate(R.layout.item_dinner_event_guest, null);
					guestsListContent.addView(guestChild);
					ImageView guestPictureIv = (ImageView) guestChild
							.findViewById(R.id.guestPictureIv);
					TextView guestNameTv = (TextView) guestChild
							.findViewById(R.id.guestNameTv);
					TextView guestAgeTv = (TextView) guestChild
							.findViewById(R.id.guestAgeTv);
					TextView relationshipStatusNameTv = (TextView) guestChild
							.findViewById(R.id.relationshipStatusNameTv);
					TextView lookingForNameTv = (TextView) guestChild
							.findViewById(R.id.lookingForNameTv);

					guestNameTv.setTypeface(itemContentFont);
					guestAgeTv.setTypeface(itemContentFont);
					relationshipStatusNameTv.setTypeface(itemContentFont);
					lookingForNameTv.setTypeface(itemContentFont);

					if (!TextUtils.isEmpty(guest.ProfilePicture)) {
						imageLoader.displayImage(guest.ProfilePicture,
								guestPictureIv, options);
					}

					if (TextUtils.isEmpty(guest.LastName)) {
						guestNameTv.setText(String
								.format("%s", guest.FirstName));
					} else {
						guestNameTv
								.setText(String.format("%s %s",
										guest.FirstName,
										guest.LastName.substring(0, 1)));
					}

					guestAgeTv
							.setText(String.format("%d years old", guest.Age));
					relationshipStatusNameTv
							.setText(guest.RelationshipStatusName);
					lookingForNameTv.setText(guest.LookingForName);
				}
			}

			View actionBtns = child.findViewById(R.id.actionBtns);
			Button actionBtn = (Button) actionBtns.findViewById(R.id.actionBtn);
			// Button Actions
			if(isPending(position))
			{
				actionBtn.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
				actionBtn.setText("Pending");
			}
			else
			{
				actionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_invite_guests));
				tableInfo.nTmpPosition = position;
				actionBtn.setTag(tableInfo);
				actionBtn.setOnClickListener(actionClickListener);
				if (FoundTableInfo.STATE_NAME_PRIVATE_TABLE
						.equals(tableInfo.TableStatusName)) {
					// PRIVATE_TABLE
					actionBtn.setText(R.string.book_table);
					if( tableInfo.TotalSeatsCount > 1 )
						((TextView)child.findViewById(R.id.seats_status)).setText(tableInfo.TotalSeatsCount + " seats");
					else
						((TextView)child.findViewById(R.id.seats_status)).setText(tableInfo.TotalSeatsCount + " seat");
				} else if (FoundTableInfo.STATE_NAME_OPEN_TABLE
						.equals(tableInfo.TableStatusName)) {

					// OPEN_BOOKING
					actionBtn.setText(R.string.join_table);
					if( tableInfo.RemainingSeatsCount > 1 )
						((TextView)child.findViewById(R.id.seats_status)).setText(tableInfo.RemainingSeatsCount + " seats left!");
					else
						((TextView)child.findViewById(R.id.seats_status)).setText(tableInfo.RemainingSeatsCount + " seat left!");
					((TextView)child.findViewById(R.id.seats_status)).setTextColor(getResources().getColor(R.color.item_joined_status));
				} else if (FoundTableInfo.STATE_NAME_APPLY_TO_JOIN
						.equals(tableInfo.TableStatusName)) {

					// INVITATION_ONLY_BOOKING
					actionBtn.setText(R.string.contact_host_title);

				} else {
					Log.e("*Error*", "No Such case : " + tableInfo.TableStatusName);
				}
			}


			View shareBtn = actionBtns.findViewById(R.id.shareBtn);
			shareBtn.setTag(tableInfo);
			shareBtn.setOnClickListener(shareClickListener);

			return child;
		}
	}

	private boolean isPending(int position) {
		for(Integer pos : mPendingList)
		{
			if( pos == position )
				return true;
		}
		return false;
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
			FoundTableInfo tableInfo = (FoundTableInfo) view.getTag();
			mainActivity.findDirection(tableInfo.RestaurantLatitude,
					tableInfo.RestaurantLongitude);
		}
	};

	// Action Click Listener
	View.OnClickListener actionClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			FoundTableInfo tableInfo = (FoundTableInfo) view.getTag();
			if (tableInfo != null) {
				if (FoundTableInfo.STATE_NAME_PRIVATE_TABLE
						.equals(tableInfo.TableStatusName)) {
					// PRIVATE_TABLE
					bookTable(tableInfo);
				} else if (FoundTableInfo.STATE_NAME_OPEN_TABLE
						.equals(tableInfo.TableStatusName)) {

					// OPEN_BOOKING
					joinOtherGuest(tableInfo);
				} else if (FoundTableInfo.STATE_NAME_APPLY_TO_JOIN
						.equals(tableInfo.TableStatusName)) {

					// INVITATION_ONLY_BOOKING
					contactHost(tableInfo);
				}
			}
		}
	};

	// Action Click Listener
	View.OnClickListener shareClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			FoundTableInfo tableInfo = (FoundTableInfo) view.getTag();
			if (tableInfo != null) {
				String curUser = settings.getString("FirstName", "");
				if (TextUtils.isEmpty(curUser)) {
					curUser = String.format("Your friend (%s)",
							settings.getString("Phone", ""));
				}
				String shareMsg = String.format(
						getString(R.string.table_share_body_format), curUser,
						tableInfo.FromTime, tableInfo.RestaurantName);
				mainActivity.shareContent(shareMsg);
			}
		}
	};

	protected ProgressDialog progress = null;
	private static final int BOOKINGTYPE_ANYONECANJOIN = 0;
	private static final int BOOKINGTYPE_GUESTSAPPLYTOJOIN = 1;
	private static final int BOOKINGTYPE_REMINDMELATER = 2;

	private void bookTable(FoundTableInfo tableInfo) {

		if( tableInfo == null ) return;
		mCurSelInfo = tableInfo;
		if (!mainActivity.wasFilledMandatoryInfo()) {
			Intent intent = new Intent(mainActivity, ProfileActivity.class);
			startActivityForResult(intent, GlobalHelper.SOCIAL_TABLE_JOIN_PROFILE);
			return;
		}

		final String items[] = { "Anyone can join", "Guests apply to join",
				"Skip, remind me later" };

		AlertDialog.Builder ab = new AlertDialog.Builder(mainActivity);
		ab.setTitle("FANCY SOME COMPANY?");

		/*
		 * ArrayList<String> itemsArray = new ArrayList<>(); for (String item :
		 * items) itemsArray.add(item); ArrayAdapter<String> adapter = new
		 * ArrayAdapter<String>(cntx, android.R.layout.simple_list_item_1,
		 * itemsArray); ab.setAdapter(adapter, new OnClickListener() {
		 */
		ab.setSingleChoiceItems(items, -1, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (whichButton == 0) {
					TaskBook(BOOKINGTYPE_ANYONECANJOIN);
				} else if (whichButton == 1) {
					TaskBook(BOOKINGTYPE_GUESTSAPPLYTOJOIN);
				} else if (whichButton == 2) {
					TaskBook(BOOKINGTYPE_REMINDMELATER);
				}
				dialog.dismiss();
			}
		});

		// Chnage Title color and devider color
		Dialog mPopupDlg = ab.show();
		int textViewId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/alertTitle", null, null);
		TextView titleTv = (TextView) mPopupDlg.findViewById(textViewId);
		if (titleTv != null) {
			titleTv.setTextColor(getResources().getColor(R.color.item_title));
		}

		int dividerId = mPopupDlg.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		View divider = mPopupDlg.findViewById(dividerId);
		if (divider != null) {
			divider.setBackgroundColor(getResources().getColor(
					R.color.item_title));
		}
	}

	private void TaskBook(int bookingType)
	{
		if( mCurSelInfo == null ) return;
		FoundTableInfo tableInfo = mCurSelInfo;

		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.BOOK_RESTAURANT_TABLE);
		String soapAction = Utility.NAMESPACE + Utility.BOOK_RESTAURANT_TABLE;

		request.addProperty("UserID", settings.getInt("UserID", 0));
		request.addProperty("RestaurantID", tableInfo.RestaurantID);
		request.addProperty("TableID", tableInfo.TableID);
		request.addProperty("BookingDate", tableInfo.BookingDate);
		request.addProperty("FromTime", tableInfo.FromTime);
		request.addProperty("ToTime", tableInfo.ToTime);
		request.addProperty("SeatsOption", tableInfo.SeatsOption);
		request.addProperty("DayTimeID", tableInfo.DateTimeID);
		// Add new Param(BookingPrivacyTypeID)
		if (bookingType == BOOKINGTYPE_ANYONECANJOIN) {
			request.addProperty("BookingPrivacyTypeID", 2);
		} else if (bookingType == BOOKINGTYPE_GUESTSAPPLYTOJOIN) {
			request.addProperty("BookingPrivacyTypeID", 3);
		} else {
			request.addProperty("BookingPrivacyTypeID", 0);
		}

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
									response = (SoapPrimitive) envelope
											.getResponse();
									String responseString = response.toString();
									JSONObject respObj = new JSONObject(
											responseString);
									if (respObj.has("BookingID")) {
										String BookingId = respObj
												.getString("BookingID");

										showMessage(R.string.success_book_table);

										/******************************************************************************
										 * Add module to process the result
										 ******************************************************************************/
										gotoDinnerEventsScreen();
									} else {
										String message = getString(R.string.failed_book_table);
										if (respObj.has("Message"))
											message = respObj.getString("Message");
										showMessage(message);
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
		} else{
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
						}
					}, false);
			BackgroundProcessor.getBackgroundProcessor(mainActivity).setPerformTask(getNetworkTask);
			BackgroundProcessor.getBackgroundProcessor(mainActivity).addPendingTask(mType, tableInfo.nTmpPosition);
			gotoDinnerEventsScreen();
		}


	}
	private void TaskJoinOtherGuest()
	{
		if( mCurSelInfo == null ) return;
		FoundTableInfo tableInfo = mCurSelInfo;

		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.JOIN_OPEN_TABLE);
		String soapAction = Utility.NAMESPACE + Utility.JOIN_OPEN_TABLE;

		request.addProperty("UserID", settings.getInt("UserID", 0));
		request.addProperty("RestaurantID", tableInfo.RestaurantID);
		request.addProperty("TableID", tableInfo.TableID);
		request.addProperty("SeatsOption", "Two of us");
		request.addProperty("BookingID", tableInfo.BookingID);
		request.addProperty("Date", tableInfo.BookingDate);
		request.addProperty("FromTime", tableInfo.FromTime);
		request.addProperty("ToTime", tableInfo.ToTime);
		request.addProperty("DayTimeID", tableInfo.DateTimeID);

		if(GlobalHelper.isConnectingToInternet(mainActivity))
		{
			progress.show();
			progress.setContentView(R.layout.dialog_loading);
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope
											.getResponse();
									String responseString = response.toString();
									JSONObject respObj = new JSONObject(
											responseString);
									if (respObj.has("BookingID")) {
										String BookingId = respObj
												.getString("BookingID");

										showMessage(R.string.success_join_table);
										/******************************************************************************
										 * Add module to process the result
										 ******************************************************************************/

										gotoDinnerEventsScreen();
									} else {
										String message = getString(R.string.failed_join_table);
										if (respObj.has("Message"))
											message = respObj.getString("Message");
										showMessage(message);
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
							progress.hide();
						}
					}, false);
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
			BackgroundProcessor.getBackgroundProcessor(mainActivity).addPendingTask(mType, tableInfo.nTmpPosition);
			gotoDinnerEventsScreen();
		}

	}

	private FoundTableInfo mCurSelInfo = null;
	private void joinOtherGuest(FoundTableInfo tableInfo) {

		if( tableInfo == null ) return;
		mCurSelInfo = tableInfo;
		if (!mainActivity.wasFilledMandatoryInfo()) {
			Intent intent = new Intent(mainActivity, ProfileActivity.class);
			startActivityForResult(intent, GlobalHelper.SOCIAL_TABLE_JOIN_PROFILE);
			return;
		}

		TaskJoinOtherGuest();
	}

	private void contactHost(FoundTableInfo tableInfo) {
		if( tableInfo == null ) return;
		mCurSelInfo = tableInfo;
		if (!mainActivity.wasFilledMandatoryInfo()) {
			Intent intent = new Intent(mainActivity, ProfileActivity.class);
			startActivityForResult(intent, GlobalHelper.SOCIAL_TABLE_CONTACT_PROFILE);
			return;
		}

		SoapObject request = new SoapObject(Utility.NAMESPACE,
				Utility.REQUEST_JOIN_TABLE_BY_GUEST);
		String soapAction = Utility.NAMESPACE
				+ Utility.REQUEST_JOIN_TABLE_BY_GUEST;

		request.addProperty("UserID", settings.getInt("UserID", 0));
		request.addProperty("RestaurantID", tableInfo.RestaurantID);
		request.addProperty("TableID", tableInfo.TableID);
		request.addProperty("BookingID", tableInfo.BookingID);

		if(GlobalHelper.isConnectingToInternet(mainActivity))
		{
			progress.show();
			progress.setContentView(R.layout.dialog_loading);
			AnyTask getNetworkTask = new AnyTask(mainActivity, soapAction, request,
					new AnyTaskCallback() {

						@Override
						public void onResult(boolean success, Object result) {
							if (success) {
								SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

								SoapPrimitive response;
								try {
									response = (SoapPrimitive) envelope
											.getResponse();
									String responseString = response.toString();
									JSONObject respObj = new JSONObject(
											responseString);

									if (respObj.has("BookingID")) {

										showMessage(R.string.success_contact_host);
										/******************************************************************************
										 * Add module to process the result
										 ******************************************************************************/
										gotoDinnerEventsScreen();
									} else {
										String message = getString(R.string.failed_contact_host);
										if (respObj.has("Message"))
											message = respObj.getString("Message");
										showMessage(message);
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
							progress.hide();
						}
					}, false);
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
			BackgroundProcessor.getBackgroundProcessor(mainActivity).addPendingTask(mType, tableInfo.nTmpPosition);
			gotoDinnerEventsScreen();
		}

	}

	private void gotoDinnerEventsScreen() {
//		Intent intent = new Intent(mainActivity, DinnerEventsActivity.class);
//		startActivity(intent);
		((MainActivity)mainActivity).setTopTitle(R.string.menu_dinner_events);
		((MainActivity)mainActivity).showDinnerEventsFragment();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK)
		{
			if( requestCode == GlobalHelper.SOCIAL_TABLE_JOIN_PROFILE)
			{
				joinOtherGuest(mCurSelInfo);
			}
			else if( requestCode == GlobalHelper.SOCIAL_TABLE_CONTACT_PROFILE)
			{
				contactHost(mCurSelInfo);
			}
			else if( requestCode == GlobalHelper.SOCIAL_TABLE_BOOK_PROFILE)
			{
				bookTable(mCurSelInfo);
			}
		}
	}
}
