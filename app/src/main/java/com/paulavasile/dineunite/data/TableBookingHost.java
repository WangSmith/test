package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class TableBookingHost implements Parcelable {

	public int UserID = 0;
	public String ProfilePicture = "";
	public String Title = "";
	public String FirstName = "";
	public String LastName = "";
	public int Age = 0;
	public String Gender = "";
	public String RelationshipStatusName = "";
	public String LookingForName = "";
	public String LanguageName = "";
	public String EthnicityName = "";

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(UserID);
		dest.writeString(ProfilePicture);
		dest.writeString(Title);
		dest.writeString(FirstName);
		dest.writeString(LastName);
		dest.writeInt(Age);
		dest.writeString(Gender);
		dest.writeString(RelationshipStatusName);
		dest.writeString(LookingForName);
		dest.writeString(LanguageName);
		dest.writeString(EthnicityName);
	}

	public TableBookingHost() {
	}

	private TableBookingHost(Parcel source) {
		UserID = source.readInt();
		ProfilePicture = source.readString();
		Title = source.readString();
		FirstName = source.readString();
		LastName = source.readString();
		Age = source.readInt();
		Gender = source.readString();
		RelationshipStatusName = source.readString();
		LookingForName = source.readString();
		LanguageName = source.readString();
		EthnicityName = source.readString();
	}

	public static final Parcelable.Creator<TableBookingHost> CREATOR = new Parcelable.Creator<TableBookingHost>() {
		@Override
		public TableBookingHost createFromParcel(Parcel source) {
			return new TableBookingHost(source);
		}

		@Override
		public TableBookingHost[] newArray(int size) {
			return new TableBookingHost[size];
		}
	};

	public static TableBookingHost parseObjectFromString(String listInformation) {
		if (TextUtils.isEmpty(listInformation))
			return null;
		try {
			JSONObject hostInfoObj = new JSONObject(listInformation);
			TableBookingHost hostInfo = new TableBookingHost();
			if (hostInfoObj.has("UserID") && !hostInfoObj.isNull("UserID")) {
				hostInfo.UserID = hostInfoObj.getInt("UserID");
			}

			if (hostInfoObj.has("ProfilePicture") && !hostInfoObj.isNull("ProfilePicture")) {
				hostInfo.ProfilePicture = hostInfoObj.getString("ProfilePicture");
			}

			if (hostInfoObj.has("Title") && !hostInfoObj.isNull("Title")) {
				hostInfo.Title = hostInfoObj.getString("Title");
			}

			if (hostInfoObj.has("FirstName") && !hostInfoObj.isNull("FirstName")) {
				hostInfo.FirstName = hostInfoObj.getString("FirstName");
			}

			if (hostInfoObj.has("LastName") && !hostInfoObj.isNull("LastName")) {
				hostInfo.LastName = hostInfoObj.getString("LastName");
			}
			
			if (hostInfoObj.has("Age") && !hostInfoObj.isNull("Age")) {
				hostInfo.Age = hostInfoObj.getInt("Age");
			}

			if (hostInfoObj.has("Gender") && !hostInfoObj.isNull("Gender")) {
				hostInfo.Gender = hostInfoObj.getString("Gender");
			}
			
			if (hostInfoObj.has("RelationshipStatusName")
					&& !hostInfoObj.isNull("RelationshipStatusName")) {
				hostInfo.RelationshipStatusName = hostInfoObj
						.getString("RelationshipStatusName");
			}

			if (hostInfoObj.has("LookingForName") && !hostInfoObj.isNull("LookingForName")) {
				hostInfo.LookingForName = hostInfoObj.getString("LookingForName");
			}

			if (hostInfoObj.has("LanguageName") && !hostInfoObj.isNull("LanguageName")) {
				hostInfo.LanguageName = hostInfoObj.getString("LanguageName");
			}

			if (hostInfoObj.has("EthnicityName") && !hostInfoObj.isNull("EthnicityName")) {
				hostInfo.EthnicityName = hostInfoObj.getString("EthnicityName");
			}
			return hostInfo;
		} catch (JSONException e) {
			return null;
		}
	}
}
