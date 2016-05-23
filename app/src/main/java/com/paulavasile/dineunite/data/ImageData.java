package com.paulavasile.dineunite.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ImageData implements Parcelable {

	public boolean IsPrimary;
	public int ImageID;
	public String ImageURL;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(IsPrimary ? 1 : 0);
		dest.writeInt(ImageID);
		dest.writeString(ImageURL);
	}

	public ImageData() {
	}

	private ImageData(Parcel source) {
		IsPrimary = source.readInt() > 0;
		ImageID = source.readInt();
		ImageURL = source.readString();
	}

	public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
		@Override
		public ImageData createFromParcel(Parcel source) {
			return new ImageData(source);
		}

		@Override
		public ImageData[] newArray(int size) {
			return new ImageData[size];
		}
	};

	public static ImageData parseObjectFromString(String listInformation) {
		if (TextUtils.isEmpty(listInformation))
			return null;
		try {
			JSONObject hostInfoObj = new JSONObject(listInformation);
			ImageData imageInfo = new ImageData();
			if (hostInfoObj.has("IsPrimary") && !hostInfoObj.isNull("IsPrimary")) {
				imageInfo.IsPrimary = hostInfoObj.getBoolean("IsPrimary");
			}
			
			if (hostInfoObj.has("ImageID") && !hostInfoObj.isNull("ImageID")) {
				imageInfo.ImageID = hostInfoObj.getInt("ImageID");
			}

			if (hostInfoObj.has("ImageURL") && !hostInfoObj.isNull("ImageURL")) {
				imageInfo.ImageURL = hostInfoObj.getString("ImageURL");
			}
			return imageInfo;
		} catch (JSONException e) {
			return null;
		}
	}
}
