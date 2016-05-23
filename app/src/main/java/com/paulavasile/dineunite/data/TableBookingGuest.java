package com.paulavasile.dineunite.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class TableBookingGuest implements Parcelable {

	public int UserID;
	public String ProfilePicture;
	public String Title;
	public String FirstName;
	public String LastName;
	public int Age;
	public String Gender;
	public String RelationshipStatusName;
	public String LookingForName;
	public String LanguageName;
	public String EthnicityName;

	public int RequestStatus_RequestID;
	public int RequestStatus_RequestStatusID;
	public String RequestStatus_RequestStatusName;
	
	public String InvitationMessage;
	
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
		
		dest.writeInt(RequestStatus_RequestID);
		dest.writeInt(RequestStatus_RequestStatusID);
		dest.writeString(RequestStatus_RequestStatusName);
		
		dest.writeString(InvitationMessage);
	}

	public TableBookingGuest() {
	}

	private TableBookingGuest(Parcel source) {
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
		
		RequestStatus_RequestID = source.readInt();
		RequestStatus_RequestStatusID = source.readInt();
		RequestStatus_RequestStatusName = source.readString();
		
		InvitationMessage = source.readString();
	}

	public static final Parcelable.Creator<TableBookingGuest> CREATOR = new Parcelable.Creator<TableBookingGuest>() {
		@Override
		public TableBookingGuest createFromParcel(Parcel source) {
			return new TableBookingGuest(source);
		}

		@Override
		public TableBookingGuest[] newArray(int size) {
			return new TableBookingGuest[size];
		}
	};

	public static TableBookingGuest parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {

			JSONObject guestInfoObj = new JSONObject(objectString);
			TableBookingGuest guestInfo = new TableBookingGuest();
			if (guestInfoObj.has("UserID") && !guestInfoObj.isNull("UserID")) {
				guestInfo.UserID = guestInfoObj.getInt("UserID");
			}

			if (guestInfoObj.has("ProfilePicture") && !guestInfoObj.isNull("ProfilePicture")) {
				guestInfo.ProfilePicture = guestInfoObj.getString("ProfilePicture");
			}

			if (guestInfoObj.has("Title") && !guestInfoObj.isNull("Title")) {
				guestInfo.Title = guestInfoObj.getString("Title");
			}

			if (guestInfoObj.has("FirstName") && !guestInfoObj.isNull("FirstName")) {
				guestInfo.FirstName = guestInfoObj.getString("FirstName");
			}

			if (guestInfoObj.has("LastName") && !guestInfoObj.isNull("LastName")) {
				guestInfo.LastName = guestInfoObj.getString("LastName");
			}
			
			if (guestInfoObj.has("Age") && !guestInfoObj.isNull("Age")) {
				guestInfo.Age = guestInfoObj.getInt("Age");
			}

			if (guestInfoObj.has("Gender") && !guestInfoObj.isNull("Gender")) {
				guestInfo.Gender = guestInfoObj.getString("Gender");
			}
			
			if (guestInfoObj.has("RelationshipStatusName")
					&& !guestInfoObj.isNull("RelationshipStatusName")) {
				guestInfo.RelationshipStatusName = guestInfoObj
						.getString("RelationshipStatusName");
			}

			if (guestInfoObj.has("LookingForName") && !guestInfoObj.isNull("LookingForName")) {
				guestInfo.LookingForName = guestInfoObj.getString("LookingForName");
			}

			if (guestInfoObj.has("LanguageName") && !guestInfoObj.isNull("LanguageName")) {
				guestInfo.LanguageName = guestInfoObj.getString("LanguageName");
			}

			if (guestInfoObj.has("EthnicityName") && !guestInfoObj.isNull("EthnicityName")) {
				guestInfo.EthnicityName = guestInfoObj.getString("EthnicityName");
			}
			
			if (guestInfoObj.has("RequestStatus")) {
				JSONObject requestStatusObj = guestInfoObj.getJSONObject("RequestStatus");
				
				if (requestStatusObj.has("RequestID") && !requestStatusObj.isNull("RequestID")) {
					guestInfo.RequestStatus_RequestID = requestStatusObj.getInt("RequestID");
				}
				if (requestStatusObj.has("RequestStatusID") && !requestStatusObj.isNull("RequestStatusID")) {
					guestInfo.RequestStatus_RequestStatusID = requestStatusObj.getInt("RequestStatusID");
				}
				if (requestStatusObj.has("RequestStatusName") && !requestStatusObj.isNull("RequestStatusName")) {
					guestInfo.RequestStatus_RequestStatusName = requestStatusObj.getString("RequestStatusName");
				}
			}
			
			if (guestInfoObj.has("InvitationMessage") && !guestInfoObj.isNull("InvitationMessage")) {
				guestInfo.InvitationMessage = guestInfoObj.getString("InvitationMessage");
			}
			
			
			return guestInfo;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static ArrayList<TableBookingGuest> parseListFromString(String listString) {
		if (TextUtils.isEmpty(listString))
			return null;

		ArrayList<TableBookingGuest> result = new ArrayList<TableBookingGuest>();
		try {
			JSONArray tableInfoArray = new JSONArray(listString);
			if (tableInfoArray.length() > 0) {
				for (int i = 0; i < tableInfoArray.length(); i++) {
					JSONObject guestInfoObj = tableInfoArray.getJSONObject(i);
					TableBookingGuest newTableInfo = parseObjectFromString(guestInfoObj.toString());
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
