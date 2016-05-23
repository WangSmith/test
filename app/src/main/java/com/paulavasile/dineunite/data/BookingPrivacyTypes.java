package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class BookingPrivacyTypes implements Parcelable {

	public int BookingPrivacyTypeID;
	public String BookingPrivacyTypeName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(BookingPrivacyTypeID);
		dest.writeString(BookingPrivacyTypeName);
	}

	public BookingPrivacyTypes() {
	}

	public BookingPrivacyTypes(Parcel source) {
		BookingPrivacyTypeID = source.readInt();
		BookingPrivacyTypeName = source.readString();
	}

	public static final Parcelable.Creator<BookingPrivacyTypes> CREATOR = new Parcelable.Creator<BookingPrivacyTypes>() {
		@Override
		public BookingPrivacyTypes createFromParcel(Parcel source) {
			return new BookingPrivacyTypes(source);
		}

		@Override
		public BookingPrivacyTypes[] newArray(int size) {
			return new BookingPrivacyTypes[size];
		}
	};

	public static BookingPrivacyTypes parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {

			JSONObject bookingTypesObj = new JSONObject(objectString);
			BookingPrivacyTypes bookingTypes = new BookingPrivacyTypes();
			if (bookingTypesObj.has("BookingPrivacyTypeID")
					&& !bookingTypesObj.isNull("BookingPrivacyTypeID")) {
				bookingTypes.BookingPrivacyTypeID = bookingTypesObj.getInt("BookingPrivacyTypeID");
			}

			if (bookingTypesObj.has("BookingPrivacyTypeName")
					&& !bookingTypesObj.isNull("BookingPrivacyTypeName")) {
				bookingTypes.BookingPrivacyTypeName = bookingTypesObj
						.getString("BookingPrivacyTypeName");
			}

			return bookingTypes;
		} catch (JSONException e) {
			return null;
		}
	}
}
