package com.summer.demo.selectmorepic;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ParcelCreator")
public class CommonFile implements Parcelable {
	public String filename;
	public List<ImageInfo> filecontent=new ArrayList<ImageInfo>();

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filename);
		dest.writeList(filecontent);
	}

	public static final Creator<CommonFile> CREATOR=new Creator<CommonFile>() {

		@Override
		public CommonFile[] newArray(int size) {
			return null;
		}

		@Override
		public CommonFile createFromParcel(Parcel source) {
			CommonFile ft=new CommonFile();
			ft.filename= source.readString();
			ft.filecontent= source.readArrayList(CommonFile.class.getClassLoader());

			return ft;
		}


	};
}
