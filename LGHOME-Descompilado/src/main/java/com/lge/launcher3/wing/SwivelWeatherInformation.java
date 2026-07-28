package com.lge.launcher3.wing;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelWeatherInformation {
    private final String mCity;
    private final int mCityIndex;
    private final String mCpName;
    private final long mCreatedTime;
    private final String mCurrentUrl;
    private final boolean mIsLocationUpdated;
    private final String mSunRise;
    private final String mSunSet;
    private final String mTemperature;
    private final String mUnitTemp;
    private final int mWeatherCpCode;
    private final int mWeatherIconCode;

    public SwivelWeatherInformation(String cpName, int cityIndex, String city, int iconCode, int cpCode, String temperature, String unitTemp, String sunSet, String sunRise, long createdTime, String locationUpdated, String currentUrl) {
        this.mCpName = cpName;
        this.mCityIndex = cityIndex;
        this.mCity = city;
        this.mWeatherIconCode = iconCode;
        this.mWeatherCpCode = cpCode;
        this.mTemperature = temperature;
        this.mUnitTemp = unitTemp;
        this.mSunRise = sunRise;
        this.mSunSet = sunSet;
        this.mCreatedTime = createdTime;
        this.mIsLocationUpdated = "true".equals(locationUpdated);
        this.mCurrentUrl = currentUrl;
    }

    public String getCpName() {
        return this.mCpName;
    }

    public int getCityIndex() {
        return this.mCityIndex;
    }

    public String getCity() {
        return this.mCity;
    }

    public int getWeatherIconCode() {
        return this.mWeatherIconCode;
    }

    public int getWeatherCPCode() {
        return this.mWeatherCpCode;
    }

    public String getTemperature() {
        return this.mTemperature;
    }

    public String getUnitTemp() {
        return this.mUnitTemp;
    }

    public String getSunSet() {
        return this.mSunSet;
    }

    public String getSunRise() {
        return this.mSunRise;
    }

    public long getCreatedTime() {
        return this.mCreatedTime;
    }

    public boolean isLocationUpdated() {
        return this.mIsLocationUpdated;
    }

    public String getWeatherUrl() {
        return this.mCurrentUrl;
    }
}
