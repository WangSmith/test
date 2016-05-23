package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class TablePromotions implements Parcelable {
	public int PromotionID;
	public String PromotionName;
	public String PromotionType;
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(PromotionID);
		dest.writeString(PromotionName);
		dest.writeString(PromotionType);
	}
	
	public TablePromotions(Parcel source) {
		PromotionID = source.readInt();
		PromotionName = source.readString();
		PromotionType = source.readString();
	}
	
	public TablePromotions() {
	}

	public static final Parcelable.Creator<TablePromotions> CREATOR = new Parcelable.Creator<TablePromotions>() {
		@Override
		public TablePromotions createFromParcel(Parcel source) {
			return new TablePromotions(source);
		}

		@Override
		public TablePromotions[] newArray(int size) {
			return new TablePromotions[size];
		}
	};
	
	public static TablePromotions parseObjectFromString(String objectString) {
		if (TextUtils.isEmpty(objectString))
			return null;
		try {

			JSONObject tablePromotionObj = new JSONObject(objectString);
			TablePromotions promotionInfo = new TablePromotions();
			if (tablePromotionObj.has("PromotionID") && !tablePromotionObj.isNull("PromotionID")) {
				promotionInfo.PromotionID = tablePromotionObj.getInt("PromotionID");
			}

			if (tablePromotionObj.has("PromotionName") && !tablePromotionObj.isNull("PromotionName")) {
				promotionInfo.PromotionName = tablePromotionObj.getString("PromotionName");
			}

			if (tablePromotionObj.has("PromotionType") && !tablePromotionObj.isNull("PromotionType")) {
				promotionInfo.PromotionType = tablePromotionObj.getString("PromotionType");
			}

			return promotionInfo;
		} catch (JSONException e) {
			return null;
		}
	}
}	
