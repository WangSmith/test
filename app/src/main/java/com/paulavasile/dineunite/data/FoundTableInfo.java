package com.paulavasile.dineunite.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

public class FoundTableInfo implements Parcelable {

	// Constants(Pleae don't change this value)
	public static final int STATE_PRIVATE = 1;
	public static final int STATE_OPEN_INVITATION = 2;
	public static final int STATE_INVITATION_ONLY = 3;
	
	public static final String STATE_NAME_PRIVATE_TABLE = "PRIVATE TABLE";
	public static final String STATE_NAME_OPEN_TABLE = "OPEN TABLE";
	public static final String STATE_NAME_APPLY_TO_JOIN = "APPLY TO JOIN";
	
	public long RestaurantID;
	public String RestaurantName;
	public String RestaurantImage;
	public double RestaurantDistance;
	public String PostCode;
	public String RestaurantAddress;
	public double RestaurantLatitude;
	public double RestaurantLongitude;
	public int TableID;
	public int TableNumber;
	public int JoinedSeatsCount;
	public int TotalSeatsCount;
	public String SearchDate;
	public int RemainingSeatsCount;
	
	public String FromTime;
	public String ToTime;
	public boolean IsOpenTable;
	public boolean IsBookedTable;
	public int TableStatusID;
	public String TableStatusName;
	public int BookingID;

	public int RemaininDays;
	public int RemainingHours;
	public int RemainingMinutes;

	public String BookingDate;
	public int DateTimeID;
	public String SeatsOption = "";
	
	public TableBookingHost Host;
	public ArrayList<TableBookingGuest> Guests;

	public int nTmpPosition = 0;
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(RestaurantID);
		dest.writeString(RestaurantName);
		dest.writeString(RestaurantImage);
		dest.writeDouble(RestaurantDistance);
		dest.writeString(PostCode);
		dest.writeString(RestaurantAddress);
		dest.writeDouble(RestaurantLatitude);
		dest.writeDouble(RestaurantLongitude);
		dest.writeInt(TableID);
		dest.writeInt(TableNumber);
		dest.writeInt(JoinedSeatsCount);
		dest.writeInt(TotalSeatsCount);
		dest.writeString(SearchDate);
		dest.writeInt(RemainingSeatsCount);
		dest.writeString(FromTime);
		dest.writeString(ToTime);
		dest.writeInt(IsOpenTable ? 1 : 0);
		dest.writeInt(IsBookedTable ? 1 : 0);
		dest.writeInt(TableStatusID);
		dest.writeString(TableStatusName);
		dest.writeInt(BookingID);

		dest.writeInt(RemaininDays);
		dest.writeInt(RemainingHours);
		dest.writeInt(RemainingMinutes);
		dest.writeString(BookingDate);
		dest.writeInt(DateTimeID);
		dest.writeString(SeatsOption);

		dest.writeParcelable(Host, 0);
		dest.writeTypedList(Guests);

	}

	public FoundTableInfo() {
	}

	private FoundTableInfo(Parcel source) {
		RestaurantID = source.readLong();
		RestaurantName = source.readString();
		RestaurantImage = source.readString();
		RestaurantDistance = source.readDouble();
		PostCode = source.readString();
		RestaurantAddress = source.readString();
		RestaurantLatitude = source.readDouble();
		RestaurantLongitude = source.readDouble();
		TableID = source.readInt();
		TableNumber = source.readInt();
		JoinedSeatsCount = source.readInt();
		TotalSeatsCount = source.readInt();
		SearchDate = source.readString();
		RemainingSeatsCount = source.readInt();
		FromTime = source.readString();
		ToTime = source.readString();
		IsOpenTable = source.readInt() > 0;
		IsBookedTable = source.readInt() > 0;
		TableStatusID = source.readInt();
		TableStatusName = source.readString();
		BookingID = source.readInt();
		
		RemaininDays = source.readInt();
		RemainingHours = source.readInt();
		RemainingMinutes = source.readInt();

		BookingDate = source.readString();
		DateTimeID = source.readInt();
		SeatsOption = source.readString();

		Host = source.readParcelable(TableBookingHost.class.getClassLoader());
		
		Guests = new ArrayList<TableBookingGuest>();
		source.readTypedList(Guests, TableBookingGuest.CREATOR);
	}

	private long getSecValueOfFromTime() {
		if (TextUtils.isEmpty(FromTime))
			return 0;
		String[] timeFields = FromTime.split(":");
		
		long secValue = 0;
		for (int i = 0; i < timeFields.length; i++) {
			try{
				secValue += Integer.parseInt(timeFields[i]) * Math.pow(60, 2-i);
			} catch(NumberFormatException e) {}
		}
		return secValue;
	}
	
	public int getGuestNumber() {
		if (Guests == null)
			return 0;
		return Guests.size();
	}
	
	public int compare(FoundTableInfo otherTableInfo) {
		if(otherTableInfo == null)
			return -1;
		
		long secValue1 = getSecValueOfFromTime();
		long secValue2 = otherTableInfo.getSecValueOfFromTime();
		if (secValue1 < secValue2) {
			return -1;
		} else if (secValue1 > secValue2) {
			return 1;
		} else {
			return 0;
		}		
	}
	
	public static final Parcelable.Creator<FoundTableInfo> CREATOR = new Parcelable.Creator<FoundTableInfo>() {
		@Override
		public FoundTableInfo createFromParcel(Parcel source) {
			return new FoundTableInfo(source);
		}

		@Override
		public FoundTableInfo[] newArray(int size) {
			return new FoundTableInfo[size];
		}
	};
	
	public static FoundTableInfo parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {
			JSONObject tableInfoObj = new JSONObject(objectString);
			FoundTableInfo newTableInfo = new FoundTableInfo();
			if (tableInfoObj.has("RestaurantID") && !tableInfoObj.isNull("RestaurantID")) {
				newTableInfo.RestaurantID = tableInfoObj.getLong("RestaurantID");
			}

			if (tableInfoObj.has("RestaurantName") && !tableInfoObj.isNull("RestaurantName")) {
				newTableInfo.RestaurantName = tableInfoObj.getString("RestaurantName");
			}

			if (tableInfoObj.has("RestaurantImage") && !tableInfoObj.isNull("RestaurantImage")) {
				newTableInfo.RestaurantImage = tableInfoObj.getString("RestaurantImage");
			}

			if (tableInfoObj.has("RestaurantDistance")
					&& !tableInfoObj.isNull("RestaurantDistance")) {
				newTableInfo.RestaurantDistance = tableInfoObj.getDouble("RestaurantDistance");
			}

			if (tableInfoObj.has("PostCode") && !tableInfoObj.isNull("PostCode")) {
				newTableInfo.PostCode = tableInfoObj.getString("PostCode");
			}

			if (tableInfoObj.has("RestaurantAddress") && !tableInfoObj.isNull("RestaurantAddress")) {
				newTableInfo.RestaurantAddress = tableInfoObj.getString("RestaurantAddress");
			}

			if (tableInfoObj.has("RestaurantLatitude") && !tableInfoObj.isNull("RestaurantLatitude")) {
				newTableInfo.RestaurantLatitude = tableInfoObj.getDouble("RestaurantLatitude");
			}
			
			if (tableInfoObj.has("RestaurantLongitude") && !tableInfoObj.isNull("RestaurantLongitude")) {
				newTableInfo.RestaurantLongitude = tableInfoObj.getDouble("RestaurantLongitude");
			}
			
			if (tableInfoObj.has("TableID") && !tableInfoObj.isNull("TableID")) {
				newTableInfo.TableID = tableInfoObj.getInt("TableID");
			}

			if (tableInfoObj.has("TableNumber") && !tableInfoObj.isNull("TableNumber")) {
				newTableInfo.TableNumber = tableInfoObj.getInt("TableNumber");
			}

			if (tableInfoObj.has("JoinedSeatsCount") && !tableInfoObj.isNull("JoinedSeatsCount")) {
				newTableInfo.JoinedSeatsCount = tableInfoObj.getInt("JoinedSeatsCount");
			}

			if (tableInfoObj.has("TotalSeatsCount") && !tableInfoObj.isNull("TotalSeatsCount")) {
				newTableInfo.TotalSeatsCount = tableInfoObj.getInt("TotalSeatsCount");
			}

			if (tableInfoObj.has("SearchDate") && !tableInfoObj.isNull("SearchDate")) {
				newTableInfo.SearchDate = tableInfoObj.getString("SearchDate");
			}

			if (tableInfoObj.has("RemainingSeatsCount") && !tableInfoObj.isNull("RemainingSeatsCount")) {
				newTableInfo.RemainingSeatsCount = tableInfoObj.getInt("RemainingSeatsCount");
			}
			
			if (tableInfoObj.has("FromTime") && !tableInfoObj.isNull("FromTime")) {
				newTableInfo.FromTime = tableInfoObj.getString("FromTime");
			}

			if (tableInfoObj.has("ToTime") && !tableInfoObj.isNull("ToTime")) {
				newTableInfo.ToTime = tableInfoObj.getString("ToTime");
			}

			if (tableInfoObj.has("IsOpenTable") && !tableInfoObj.isNull("IsOpenTable")) {
				newTableInfo.IsOpenTable = tableInfoObj.getBoolean("IsOpenTable");
			}

			if (tableInfoObj.has("IsBookedTable") && !tableInfoObj.isNull("IsBookedTable")) {
				newTableInfo.IsBookedTable = tableInfoObj.getBoolean("IsBookedTable");
			}

			if (tableInfoObj.has("TableStatusID") && !tableInfoObj.isNull("TableStatusID")) {
				newTableInfo.TableStatusID = tableInfoObj.getInt("TableStatusID");
			}

			if (tableInfoObj.has("TableStatusName") && !tableInfoObj.isNull("TableStatusName")) {
				newTableInfo.TableStatusName = tableInfoObj.getString("TableStatusName");
			}

			if (tableInfoObj.has("BookingID") && !tableInfoObj.isNull("BookingID")) {
				newTableInfo.BookingID = tableInfoObj.getInt("BookingID");
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

			if(tableInfoObj.has("BookingDate") && !tableInfoObj.isNull("BookingDate")){
				newTableInfo.BookingDate = tableInfoObj.getString("BookingDate");
			}

			if(tableInfoObj.has("DateTimeID") && !tableInfoObj.isNull("DateTimeID")){
				newTableInfo.DateTimeID = tableInfoObj.getInt("DateTimeID");
			}

			if(tableInfoObj.has("SeatsOption") && !tableInfoObj.isNull("SeatsOption")){
				newTableInfo.SeatsOption = tableInfoObj.getString("SeatsOption");
			}

			if (tableInfoObj.has("Host") && !tableInfoObj.isNull("Host")) {
				newTableInfo.Host = TableBookingHost.parseObjectFromString(tableInfoObj.getString("Host"));
			}
			
			if (tableInfoObj.has("Guests") && !tableInfoObj.isNull("Guests")) {
				newTableInfo.Guests = TableBookingGuest.parseListFromString(tableInfoObj.getString("Guests"));
			}


			
			return newTableInfo;
		} catch (JSONException e) {
			return null;
		}
	}

	public static ArrayList<FoundTableInfo> parseListFromString(String listString) {
		if (TextUtils.isEmpty(listString))
			return null;

		ArrayList<FoundTableInfo> result = new ArrayList<FoundTableInfo>();
		try {
			JSONArray tableInfoArray = new JSONArray(listString);
			if (tableInfoArray.length() > 0) {
				for (int i = 0; i < tableInfoArray.length(); i++) {
					JSONObject tableInfoObj = tableInfoArray.getJSONObject(i);
					//Log.e("FoundTable", tableInfoObj.toString());
					FoundTableInfo newTableInfo = parseObjectFromString(tableInfoObj.toString());
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
