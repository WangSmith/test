package com.paulavasile.dineunite.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

public class UserProfile implements Parcelable {

	// Manual Input Types
	public static final int TYPE_FIRSTNAME = 0;
	public static final int TYPE_LASTNAME = 1;
	public static final int TYPE_GENDER = 2;
	public static final int TYPE_BIRTHDAY = 3;
	public static final int TYPE_ABOUTYOU = 4;
	public static final int TYPE_EMAIL = 5;
	public static final int TYPE_MOBILE = 6;
	public static final int TYPE_HOMEADDRESS = 7;
	public static final int TYPE_WORKADDRESS = 8;

	public int UserID;
	public String TitleOptionID;
	public String Title;
	public String FirstName;
	public String LastName;

	public String Email;
	public String Phone;
	public String Gender;

	public String HomePostCode;
	public String WorkPostCode;
	public String HomeAddress;
	public String WorkAddress;
	public double HomeLatitude;
	public double HomeLongitude;
	public double WorkLatitude;
	public double WorkLongitude;

	public String DOB;
	public int Age;

	public String FavoriteCuisineType;
	public String RelationshipStatus;
	public String LookingFor;
	public String Education;
	public String Ethnicity;
	public String Language;

	public String AboutMe;
	public ImageData Image;
	public double ProfileCompletionPercentage;
	public boolean IsInvitedAlready;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(UserID);
		dest.writeString(TitleOptionID);
		dest.writeString(Title);
		dest.writeString(FirstName);
		dest.writeString(LastName);
		dest.writeString(Email);
		dest.writeString(Phone);
		dest.writeString(Gender);
		dest.writeString(HomePostCode);
		dest.writeString(WorkPostCode);
		dest.writeString(HomeAddress);
		dest.writeString(WorkAddress);
		dest.writeDouble(HomeLatitude);
		dest.writeDouble(HomeLongitude);
		dest.writeDouble(WorkLatitude);
		dest.writeDouble(WorkLongitude);
		dest.writeString(DOB);
		dest.writeInt(Age);
		dest.writeString(FavoriteCuisineType);
		dest.writeString(RelationshipStatus);
		dest.writeString(LookingFor);
		dest.writeString(Education);
		dest.writeString(Ethnicity);
		dest.writeString(Language);
		dest.writeString(AboutMe);
		
		dest.writeParcelable(Image, 0);
		
		dest.writeDouble(ProfileCompletionPercentage);
		dest.writeInt(IsInvitedAlready ? 1 : 0);
	}

	public UserProfile(Parcel source) {
		UserID = source.readInt();
		TitleOptionID = source.readString();
		Title = source.readString();
		FirstName = source.readString();
		LastName = source.readString();
		Email = source.readString();
		Phone = source.readString();
		Gender = source.readString();
		HomePostCode = source.readString();
		WorkPostCode = source.readString();
		HomeAddress = source.readString();
		WorkAddress = source.readString();
		HomeLatitude = source.readDouble();
		HomeLongitude = source.readDouble();
		WorkLatitude = source.readDouble();
		WorkLongitude = source.readDouble();
		DOB = source.readString();
		Age = source.readInt();
		FavoriteCuisineType = source.readString();
		RelationshipStatus = source.readString();
		LookingFor = source.readString();
		Education = source.readString();
		Ethnicity = source.readString();
		Language = source.readString();
		AboutMe = source.readString();

		Image = source.readParcelable(ImageData.class.getClassLoader());
		ProfileCompletionPercentage = source.readDouble();
		
		IsInvitedAlready = source.readInt() > 0;
	}

	public UserProfile() {
	}

	public String getCuisineTypeName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(FavoriteCuisineType);
			if (jsonObj.has("CuisineTypeName")) {
				value = jsonObj.getString("CuisineTypeName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getRelationShipStatusName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(RelationshipStatus);
			if (jsonObj.has("RelationshipStatusName")) {
				value = jsonObj.getString("RelationshipStatusName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getLookingForName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(LookingFor);
			if (jsonObj.has("LookingForName")) {
				value = jsonObj.getString("LookingForName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getEducationName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(Education);
			if (jsonObj.has("EducationName")) {
				value = jsonObj.getString("EducationName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getEthnicityName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(Ethnicity);
			if (jsonObj.has("EthnicityName")) {
				value = jsonObj.getString("EthnicityName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getLanguageName() {
		String value = "";
		try {
			JSONObject jsonObj = new JSONObject(Language);
			if (jsonObj.has("LanguageName")) {
				value = jsonObj.getString("LanguageName");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getAvatarImage() {
		if (Image == null) {
			return "";
		}
		return Image.ImageURL;
	}

	public static UserProfile parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {

			JSONObject bookingTypesObj = new JSONObject(objectString);
			UserProfile userInfo = new UserProfile();
			if (bookingTypesObj.has("UserID")
					&& !bookingTypesObj.isNull("UserID")) {
				userInfo.UserID = bookingTypesObj.getInt("UserID");
			}

			if (bookingTypesObj.has("TitleOptionID")
					&& !bookingTypesObj.isNull("TitleOptionID")) {
				userInfo.TitleOptionID = bookingTypesObj
						.getString("TitleOptionID");
			}

			if (bookingTypesObj.has("Title")
					&& !bookingTypesObj.isNull("Title")) {
				userInfo.Title = bookingTypesObj.getString("Title");
			}

			if (bookingTypesObj.has("FirstName")
					&& !bookingTypesObj.isNull("FirstName")) {
				userInfo.FirstName = bookingTypesObj.getString("FirstName");
			}

			if (bookingTypesObj.has("LastName")
					&& !bookingTypesObj.isNull("LastName")) {
				userInfo.LastName = bookingTypesObj.getString("LastName");
			}

			if (bookingTypesObj.has("Email")
					&& !bookingTypesObj.isNull("Email")) {
				userInfo.Email = bookingTypesObj.getString("Email");
			}

			if (bookingTypesObj.has("Phone")
					&& !bookingTypesObj.isNull("Phone")) {
				userInfo.Phone = bookingTypesObj.getString("Phone");
			}

			if (bookingTypesObj.has("Gender")
					&& !bookingTypesObj.isNull("Gender")) {
				userInfo.Gender = bookingTypesObj.getString("Gender");
			}

			if (bookingTypesObj.has("HomePostCode")
					&& !bookingTypesObj.isNull("HomePostCode")) {
				userInfo.HomePostCode = bookingTypesObj
						.getString("HomePostCode");
			}

			if (bookingTypesObj.has("WorkPostCode")
					&& !bookingTypesObj.isNull("WorkPostCode")) {
				userInfo.WorkPostCode = bookingTypesObj
						.getString("WorkPostCode");
			}

			if (bookingTypesObj.has("HomeAddress")
					&& !bookingTypesObj.isNull("HomeAddress")) {
				userInfo.HomeAddress = bookingTypesObj.getString("HomeAddress");
			}

			if (bookingTypesObj.has("WorkAddress")
					&& !bookingTypesObj.isNull("WorkAddress")) {
				userInfo.WorkAddress = bookingTypesObj.getString("WorkAddress");
			}

			if (bookingTypesObj.has("HomeLatitude")
					&& !bookingTypesObj.isNull("HomeLatitude")) {
				userInfo.HomeLatitude = bookingTypesObj
						.getDouble("HomeLatitude");
			}

			if (bookingTypesObj.has("HomeLongitude")
					&& !bookingTypesObj.isNull("HomeLongitude")) {
				userInfo.HomeLongitude = bookingTypesObj
						.getDouble("HomeLongitude");
			}

			if (bookingTypesObj.has("WorkLatitude")
					&& !bookingTypesObj.isNull("WorkLatitude")) {
				userInfo.WorkLatitude = bookingTypesObj
						.getDouble("WorkLatitude");
			}

			if (bookingTypesObj.has("WorkLongitude")
					&& !bookingTypesObj.isNull("WorkLongitude")) {
				userInfo.WorkLongitude = bookingTypesObj
						.getDouble("WorkLongitude");
			}

			if (bookingTypesObj.has("DOB") && !bookingTypesObj.isNull("DOB")) {
				userInfo.DOB = bookingTypesObj.getString("DOB");
			}

			if (bookingTypesObj.has("Age") && !bookingTypesObj.isNull("Age")) {
				userInfo.Age = bookingTypesObj.getInt("Age");
			}

			if (bookingTypesObj.has("FavoriteCuisineType")
					&& !bookingTypesObj.isNull("FavoriteCuisineType")) {
				userInfo.FavoriteCuisineType = bookingTypesObj
						.getString("FavoriteCuisineType");
			}

			if (bookingTypesObj.has("RelationshipStatus")
					&& !bookingTypesObj.isNull("RelationshipStatus")) {
				userInfo.RelationshipStatus = bookingTypesObj
						.getString("RelationshipStatus");
			}

			if (bookingTypesObj.has("LookingFor")
					&& !bookingTypesObj.isNull("LookingFor")) {
				userInfo.LookingFor = bookingTypesObj.getString("LookingFor");
			}

			if (bookingTypesObj.has("Education")
					&& !bookingTypesObj.isNull("Education")) {
				userInfo.Education = bookingTypesObj.getString("Education");
			}

			if (bookingTypesObj.has("Ethnicity")
					&& !bookingTypesObj.isNull("Ethnicity")) {
				userInfo.Ethnicity = bookingTypesObj.getString("Ethnicity");
			}

			if (bookingTypesObj.has("Language")
					&& !bookingTypesObj.isNull("Language")) {
				userInfo.Language = bookingTypesObj.getString("Language");
			}

			if (bookingTypesObj.has("AboutMe")
					&& !bookingTypesObj.isNull("AboutMe")) {
				userInfo.AboutMe = bookingTypesObj.getString("AboutMe");
			}

			if (bookingTypesObj.has("Image")
					&& !bookingTypesObj.isNull("Image")) {
				userInfo.Image = ImageData
						.parseObjectFromString(bookingTypesObj
								.getString("Image"));
			}

			if (bookingTypesObj.has("ProfileCompletionPercentage")
					&& !bookingTypesObj.isNull("ProfileCompletionPercentage")) {
				userInfo.ProfileCompletionPercentage = bookingTypesObj
						.getDouble("ProfileCompletionPercentage");
			}
			
			if (bookingTypesObj.has("IsInvitedAlready")
					&& !bookingTypesObj.isNull("IsInvitedAlready")) {
				userInfo.IsInvitedAlready = bookingTypesObj
						.getBoolean("IsInvitedAlready");
			}
			
			return userInfo;
		} catch (JSONException e) {
			return null;
		}
	}

	public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
		@Override
		public UserProfile createFromParcel(Parcel source) {
			return new UserProfile(source);
		}

		@Override
		public UserProfile[] newArray(int size) {
			return new UserProfile[size];
		}
	};

	public static ArrayList<UserProfile> parseListFromString(String listString) {
		if (TextUtils.isEmpty(listString))
			return null;

		ArrayList<UserProfile> result = new ArrayList<UserProfile>();
		try {
			JSONArray tableInfoArray = new JSONArray(listString);
			if (tableInfoArray.length() > 0) {
				for (int i = 0; i < tableInfoArray.length(); i++) {
					JSONObject guestInfoObj = tableInfoArray.getJSONObject(i);
					Log.e("friend", guestInfoObj.toString());
					UserProfile newTableInfo = parseObjectFromString(guestInfoObj
							.toString());
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
