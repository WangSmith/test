package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class BookingTypes implements Parcelable{
	
	public int BookingTypeID;
	public String BookingTypeName;
	public String SlotType;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(BookingTypeID);
		dest.writeString(BookingTypeName);
		dest.writeString(SlotType);
	}
	
	public BookingTypes() {
	}
	
	public BookingTypes(Parcel source) {
		BookingTypeID = source.readInt();
		BookingTypeName = source.readString();
		SlotType = source.readString();
	}
	
	public static final Parcelable.Creator<BookingTypes> CREATOR = new Parcelable.Creator<BookingTypes>() {
		@Override
		public BookingTypes createFromParcel(Parcel source) {
			return new BookingTypes(source);
		}

		@Override
		public BookingTypes[] newArray(int size) {
			return new BookingTypes[size];
		}
	};
	
	public static BookingTypes parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {

			JSONObject bookingTypesObj = new JSONObject(objectString);
			BookingTypes bookingTypes = new BookingTypes();
			if (bookingTypesObj.has("BookingTypeID") && !bookingTypesObj.isNull("BookingTypeID")) {
				bookingTypes.BookingTypeID = bookingTypesObj.getInt("BookingTypeID");
			}

			if (bookingTypesObj.has("BookingTypeName") && !bookingTypesObj.isNull("BookingTypeName")) {
				bookingTypes.BookingTypeName = bookingTypesObj.getString("BookingTypeName");
			}

			if (bookingTypesObj.has("SlotType") && !bookingTypesObj.isNull("SlotType")) {
				bookingTypes.SlotType = bookingTypesObj.getString("SlotType");
			}

			return bookingTypes;
		} catch (JSONException e) {
			return null;
		}
	}
}
