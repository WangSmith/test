package com.paulavasile.dineunite.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

public class DinnerTableBooking implements Parcelable {

	public long BookingID;

	public String BookingDate;
	public String TimeFrom;
	public String TimeTo;

	public String FirstName;
	public String LastName;
	public String Email;
	public String Phone;
	public int TitleOptionID;

	public long TableID;
	public RestaurantTable Table;

	public TablePromotions Promotion;

	public BookingTypes BookingType;

	public BookingPrivacyTypes BookingPrivacyType;

	public int JoinedSeatsCount;
	public int BookedSeatsCount;

	public int RemainingSeatsCount;
	
	public boolean IsConfirmed;
	public boolean IsCancelled;
	public boolean IsExpired;

	public boolean AmIHost;
	public boolean AmIGuest;

	public int RemaininDays;
	public int RemainingHours;
	public int RemainingMinutes;

	public String BookingRef;

	public TableBookingHost Host;

	public ArrayList<TableBookingGuest> Guests;

	public long RestaurantID;
	public String RestaurantName;
	public String RestaurantImage;
	public String PostCode;
	public String RestaurantAddress;

	public double RestaurantDistance;
	public double RestaurantLatitude;
	public double RestaurantLongitude;

	
	
	public DinnerTableBooking() {
		Guests = new ArrayList<TableBookingGuest>();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(BookingID);

		dest.writeString(BookingDate);
		dest.writeString(TimeFrom);
		dest.writeString(TimeTo);

		dest.writeString(FirstName);
		dest.writeString(LastName);
		dest.writeString(Email);
		dest.writeString(Phone);
		dest.writeInt(TitleOptionID);

		dest.writeLong(TableID);
		dest.writeParcelable(Table, 0);
		dest.writeParcelable(Promotion, 0);
		dest.writeParcelable(BookingType, 0);
		dest.writeParcelable(BookingPrivacyType, 0);

		dest.writeInt(JoinedSeatsCount);
		dest.writeInt(BookedSeatsCount);
		dest.writeInt(RemainingSeatsCount);
		
		dest.writeInt(IsConfirmed ? 1 : 0);
		dest.writeInt(IsCancelled ? 1 : 0);
		dest.writeInt(IsExpired ? 1 : 0);
		dest.writeInt(AmIHost ? 1 : 0);
		dest.writeInt(AmIGuest ? 1 : 0);

		dest.writeInt(RemaininDays);
		dest.writeInt(RemainingHours);
		dest.writeInt(RemainingMinutes);
		dest.writeString(BookingRef);
		dest.writeParcelable(Host, 0);
		dest.writeTypedList(Guests);

		dest.writeLong(RestaurantID);
		dest.writeString(RestaurantName);
		dest.writeString(RestaurantImage);
		dest.writeString(PostCode);
		dest.writeString(RestaurantAddress);
	}

	public DinnerTableBooking(Parcel source) {
		BookingID = source.readLong();

		BookingDate = source.readString();
		TimeFrom = source.readString();
		TimeTo = source.readString();

		FirstName = source.readString();
		LastName = source.readString();
		Email = source.readString();
		Phone = source.readString();
		TitleOptionID = source.readInt();

		TableID = source.readLong();
		Table = source.readParcelable(RestaurantTable.class.getClassLoader());
		Promotion = source.readParcelable(TablePromotions.class.getClassLoader());
		BookingType = source.readParcelable(BookingTypes.class.getClassLoader());
		BookingPrivacyType = source.readParcelable(BookingPrivacyTypes.class.getClassLoader());

		JoinedSeatsCount = source.readInt();
		BookedSeatsCount = source.readInt();
		RemainingSeatsCount = source.readInt();
		
		IsConfirmed = source.readInt() > 0;
		IsCancelled = source.readInt() > 0;
		IsExpired = source.readInt() > 0;
		AmIHost = source.readInt() > 0;
		AmIGuest = source.readInt() > 0;

		RemaininDays = source.readInt();
		RemainingHours = source.readInt();
		RemainingMinutes = source.readInt();
		BookingRef = source.readString();

		Host = source.readParcelable(TableBookingHost.class.getClassLoader());

		Guests = new ArrayList<TableBookingGuest>();
		source.readTypedList(Guests, TableBookingGuest.CREATOR);

		RestaurantID = source.readLong();
		RestaurantName = source.readString();
		RestaurantImage = source.readString();
		PostCode = source.readString();
		RestaurantAddress = source.readString();
	}

	public static final Parcelable.Creator<DinnerTableBooking> CREATOR = new Parcelable.Creator<DinnerTableBooking>() {
		@Override
		public DinnerTableBooking createFromParcel(Parcel source) {
			return new DinnerTableBooking(source);
		}

		@Override
		public DinnerTableBooking[] newArray(int size) {
			return new DinnerTableBooking[size];
		}
	};

	public static DinnerTableBooking parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {
			JSONObject tableInfoObj = new JSONObject(objectString);
			DinnerTableBooking newTableInfo = new DinnerTableBooking();

			if (tableInfoObj.has("BookingID") && !tableInfoObj.isNull("BookingID")) {
				newTableInfo.BookingID = tableInfoObj.getLong("BookingID");
			}

			if (tableInfoObj.has("BookingDate") && !tableInfoObj.isNull("BookingDate")) {
				newTableInfo.BookingDate = tableInfoObj.getString("BookingDate");
			}

			if (tableInfoObj.has("TimeFrom") && !tableInfoObj.isNull("TimeFrom")) {
				newTableInfo.TimeFrom = tableInfoObj.getString("TimeFrom");
			}

			if (tableInfoObj.has("TimeTo") && !tableInfoObj.isNull("TimeTo")) {
				newTableInfo.TimeTo = tableInfoObj.getString("TimeTo");
			}

			if (tableInfoObj.has("FirstName") && !tableInfoObj.isNull("FirstName")) {
				newTableInfo.FirstName = tableInfoObj.getString("FirstName");
			}

			if (tableInfoObj.has("LastName") && !tableInfoObj.isNull("LastName")) {
				newTableInfo.LastName = tableInfoObj.getString("LastName");
			}

			if (tableInfoObj.has("Phone") && !tableInfoObj.isNull("Phone")) {
				newTableInfo.Phone = tableInfoObj.getString("Phone");
			}

			if (tableInfoObj.has("TitleOptionID") && !tableInfoObj.isNull("TitleOptionID")) {
				newTableInfo.TitleOptionID = tableInfoObj.getInt("TitleOptionID");
			}

			if (tableInfoObj.has("TableID") && !tableInfoObj.isNull("TableID")) {
				newTableInfo.TableID = tableInfoObj.getLong("TableID");
			}

			if (tableInfoObj.has("Table") && !tableInfoObj.isNull("Table")) {
				newTableInfo.Table = RestaurantTable.parseObjectFromString(tableInfoObj
						.getString("Table"));
			}

			if (tableInfoObj.has("Promotion") && !tableInfoObj.isNull("Promotion")) {
				newTableInfo.Promotion = TablePromotions.parseObjectFromString(tableInfoObj
						.getString("Promotion"));
			}

			if (tableInfoObj.has("BookingType") && !tableInfoObj.isNull("BookingType")) {
				newTableInfo.BookingType = BookingTypes.parseObjectFromString(tableInfoObj
						.getString("BookingType"));
			}

			if (tableInfoObj.has("BookingPrivacyType")
					&& !tableInfoObj.isNull("BookingPrivacyType")) {
				newTableInfo.BookingPrivacyType = BookingPrivacyTypes
						.parseObjectFromString(tableInfoObj.getString("BookingPrivacyType"));
			}

			if (tableInfoObj.has("JoinedSeatsCount") && !tableInfoObj.isNull("JoinedSeatsCount")) {
				newTableInfo.JoinedSeatsCount = tableInfoObj.getInt("JoinedSeatsCount");
			}

			if (tableInfoObj.has("BookedSeatsCount") && !tableInfoObj.isNull("BookedSeatsCount")) {
				newTableInfo.BookedSeatsCount = tableInfoObj.getInt("BookedSeatsCount");
			}

			if (tableInfoObj.has("RemainingSeatsCount") && !tableInfoObj.isNull("RemainingSeatsCount")) {
				newTableInfo.RemainingSeatsCount = tableInfoObj.getInt("RemainingSeatsCount");
			}
			
			if (tableInfoObj.has("IsConfirmed") && !tableInfoObj.isNull("IsConfirmed")) {
				newTableInfo.IsConfirmed = tableInfoObj.getBoolean("IsConfirmed");
			}

			if (tableInfoObj.has("IsCancelled") && !tableInfoObj.isNull("IsCancelled")) {
				newTableInfo.IsCancelled = tableInfoObj.getBoolean("IsCancelled");
			}

			if (tableInfoObj.has("IsExpired") && !tableInfoObj.isNull("IsExpired")) {
				newTableInfo.IsExpired = tableInfoObj.getBoolean("IsExpired");
			}

			if (tableInfoObj.has("AmIHost") && !tableInfoObj.isNull("AmIHost")) {
				newTableInfo.AmIHost = tableInfoObj.getBoolean("AmIHost");
			}

			if (tableInfoObj.has("AmIGuest") && !tableInfoObj.isNull("AmIGuest")) {
				newTableInfo.AmIGuest = tableInfoObj.getBoolean("AmIGuest");
			}

			if (tableInfoObj.has("RemaininDays") && !tableInfoObj.isNull("RemaininDays")) {
				newTableInfo.RemaininDays = tableInfoObj.getInt("RemaininDays");
			}

			if (tableInfoObj.has("RemainingHours") && !tableInfoObj.isNull("RemainingHours")) {
				newTableInfo.RemainingHours = tableInfoObj.getInt("RemainingHours");
			}

			if (tableInfoObj.has("RemainingMinutes") && !tableInfoObj.isNull("RemainingMinutes")) {
				newTableInfo.RemainingMinutes = tableInfoObj.getInt("RemainingMinutes");
			}

			if (tableInfoObj.has("BookingRef") && !tableInfoObj.isNull("BookingRef")) {
				newTableInfo.BookingRef = tableInfoObj.getString("BookingRef");
			}

			if (tableInfoObj.has("Host") && !tableInfoObj.isNull("Host")) {
				newTableInfo.Host = TableBookingHost.parseObjectFromString(tableInfoObj
						.getString("Host"));
			}

			if (tableInfoObj.has("Guests") && !tableInfoObj.isNull("Guests")) {
				newTableInfo.Guests = TableBookingGuest.parseListFromString(tableInfoObj
						.getString("Guests"));
			}

			if (tableInfoObj.has("RestaurantID") && !tableInfoObj.isNull("RestaurantID")) {
				newTableInfo.RestaurantID = tableInfoObj.getLong("RestaurantID");
			}

			if (tableInfoObj.has("RestaurantName") && !tableInfoObj.isNull("RestaurantName")) {
				newTableInfo.RestaurantName = tableInfoObj.getString("RestaurantName");
			}

			if (tableInfoObj.has("RestaurantImage") && !tableInfoObj.isNull("RestaurantImage")) {
				newTableInfo.RestaurantImage = tableInfoObj.getString("RestaurantImage");
			}

			if (tableInfoObj.has("PostCode") && !tableInfoObj.isNull("PostCode")) {
				newTableInfo.PostCode = tableInfoObj.getString("PostCode");
			}

			if (tableInfoObj.has("RestaurantAddress") && !tableInfoObj.isNull("RestaurantAddress")) {
				newTableInfo.RestaurantAddress = tableInfoObj.getString("RestaurantAddress");
			}

			if (tableInfoObj.has("RestaurantDistance")
					&& !tableInfoObj.isNull("RestaurantDistance")) {
				newTableInfo.RestaurantDistance = tableInfoObj.getDouble("RestaurantDistance");
			}

			if (tableInfoObj.has("RestaurantLatitude")
					&& !tableInfoObj.isNull("RestaurantLatitude")) {
				newTableInfo.RestaurantLatitude = tableInfoObj.getDouble("RestaurantLatitude");
			}

			if (tableInfoObj.has("RestaurantLongitude")
					&& !tableInfoObj.isNull("RestaurantLongitude")) {
				newTableInfo.RestaurantLongitude = tableInfoObj.getDouble("RestaurantLongitude");
			}

			return newTableInfo;
		} catch (JSONException e) {
			return null;
		}
	}

	public static ArrayList<DinnerTableBooking> parseListFromString(String listString) {
		if (TextUtils.isEmpty(listString))
			return null;

		ArrayList<DinnerTableBooking> result = new ArrayList<DinnerTableBooking>();
		try {
			JSONArray tableInfoArray = new JSONArray(listString);
			if (tableInfoArray.length() > 0) {
				for (int i = 0; i < tableInfoArray.length(); i++) {
					JSONObject tableInfoObj = tableInfoArray.getJSONObject(i);
					Log.e("DinnerTableBooking", tableInfoObj.toString());
					DinnerTableBooking newTableInfo = parseObjectFromString(tableInfoObj.toString());
					if (newTableInfo != null) {
						result.add(newTableInfo);
					}
				}
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}
}
