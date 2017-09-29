package com.srain.utils.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SRain on 2016/10/1 0001.
 * 用途：
 */

public class AppInfo implements Parcelable {
    private String name;
    private Drawable icon = null;
    private String packageName;
    private String packagePath;
    private String versionName;
    private int versionCode;
    private boolean isSystem;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable((Parcelable) this.icon, flags);
        dest.writeString(this.packageName);
        dest.writeString(this.packagePath);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeByte(this.isSystem ? (byte) 1 : (byte) 0);
    }

    public AppInfo() {
    }

    public AppInfo(String name, Drawable icon, String packageName, String packagePath, String versionName, int versionCode, boolean isSystem) {
        this.setName(name);
        this.setIcon(icon);
        this.setPackageName(packageName);
        this.setPackagePath(packagePath);
        this.setVersionName(versionName);
        this.setVersionCode(versionCode);
        this.setSystem(isSystem);
    }

    protected AppInfo(Parcel in) {
        this.name = in.readString();
        this.icon = in.readParcelable(Drawable.class.getClassLoader());
        this.packageName = in.readString();
        this.packagePath = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.isSystem = in.readByte() != 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    @Override
    public String toString() {
        return getName() + "\n"
                + getIcon() + "\n"
                + getPackageName() + "\n"
                + getPackagePath() + "\n"
                + getVersionName() + "\n"
                + getVersionCode() + "\n"
                + isSystem() + "\n";
    }
}
