package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class RestaurantTable implements Parcelable {
	public int TableID;
	public int TableNumber;
	public int TotalSeatsCount;
	public int RestaurantID;
	public boolean IsBooked;
	public boolean IsAdvertised;
	public boolean IsOpenTable;
	public boolean IsActive;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(TableID);
		dest.writeInt(TableNumber);
		dest.writeInt(TotalSeatsCount);
		dest.writeInt(RestaurantID);
		dest.writeInt(IsBooked ? 1 : 0);
		dest.writeInt(IsAdvertised ? 1 : 0);
		dest.writeInt(IsOpenTable ? 1 : 0);
		dest.writeInt(IsActive ? 1 : 0);
	}
	
	private RestaurantTable(Parcel source) {
		TableID = source.readInt();
		TableNumber = source.readInt();
		TotalSeatsCount = source.readInt();
		RestaurantID = source.readInt();
		IsBooked = source.readInt() > 0;
		IsAdvertised = source.readInt() > 0;
		IsOpenTable = source.readInt() > 0;
		IsActive = source.readInt() > 0;
	}
	
	public RestaurantTable() {
	}

	public static final Parcelable.Creator<RestaurantTable> CREATOR = new Parcelable.Creator<RestaurantTable>() {
		@Override
		public RestaurantTable createFromParcel(Parcel source) {
			return new RestaurantTable(source);
		}

		@Override
		public RestaurantTable[] newArray(int size) {
			return new RestaurantTable[size];
		}
	};

	public static RestaurantTable parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		
		try {

			JSONObject restaurantTableObj = new JSONObject(objectString);
			RestaurantTable restaurantTable = new RestaurantTable();
			
			if (restaurantTableObj.has("TableID") && !restaurantTableObj.isNull("TableID")) {
				restaurantTable.TableID = restaurantTableObj.getInt("TableID");
			}
			if (restaurantTableObj.has("TableNumber") && !restaurantTableObj.isNull("TableNumber")) {
				restaurantTable.TableNumber = restaurantTableObj.getInt("TableNumber");
			}
			if (restaurantTableObj.has("TotalSeatsCount") && !restaurantTableObj.isNull("TotalSeatsCount")) {
				restaurantTable.TotalSeatsCount = restaurantTableObj.getInt("TotalSeatsCount");
			}
			if (restaurantTableObj.has("RestaurantID") && !restaurantTableObj.isNull("RestaurantID")) {
				restaurantTable.RestaurantID = restaurantTableObj.getInt("RestaurantID");
			}
			if (restaurantTableObj.has("IsBooked") && !restaurantTableObj.isNull("IsBooked")) {
				restaurantTable.IsBooked = restaurantTableObj.getBoolean("IsBooked");
			}
			if (restaurantTableObj.has("IsAdvertised") && !restaurantTableObj.isNull("IsAdvertised")) {
				restaurantTable.IsAdvertised = restaurantTableObj.getBoolean("IsAdvertised");
			}
			if (restaurantTableObj.has("IsOpenTable") && !restaurantTableObj.isNull("IsOpenTable")) {
				restaurantTable.IsOpenTable = restaurantTableObj.getBoolean("IsOpenTable");
			}
			if (restaurantTableObj.has("IsActive") && !restaurantTableObj.isNull("IsActive")) {
				restaurantTable.IsActive = restaurantTableObj.getBoolean("IsActive");
			}
			
			return restaurantTable;
		} catch (JSONException e) {
			return null;
		}
	}
}
