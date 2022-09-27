package com.student.attendance.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class Date implements Parcelable {
    @SerializedName("date")
    private int date;

    @SerializedName("hours")
    private int hours;

    @SerializedName("seconds")
    private int seconds;

    @SerializedName("month")
    private int month;

    @SerializedName("timezoneOffset")
    private int timezoneOffset;

    @SerializedName("year")
    private int year;

    @SerializedName("minutes")
    private int minutes;

    @SerializedName("time")
    private long time;

    @SerializedName("day")
    private int day;

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getHours() {
        return hours;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    @Override
    public String toString() {
        return
                "Date{" +
                        "date = '" + date + '\'' +
                        ",hours = '" + hours + '\'' +
                        ",seconds = '" + seconds + '\'' +
                        ",month = '" + month + '\'' +
                        ",timezoneOffset = '" + timezoneOffset + '\'' +
                        ",year = '" + year + '\'' +
                        ",minutes = '" + minutes + '\'' +
                        ",time = '" + time + '\'' +
                        ",day = '" + day + '\'' +
                        "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.date);
        dest.writeInt(this.hours);
        dest.writeInt(this.seconds);
        dest.writeInt(this.month);
        dest.writeInt(this.timezoneOffset);
        dest.writeInt(this.year);
        dest.writeInt(this.minutes);
        dest.writeLong(this.time);
        dest.writeInt(this.day);
    }

    public void readFromParcel(Parcel source) {
        this.date = source.readInt();
        this.hours = source.readInt();
        this.seconds = source.readInt();
        this.month = source.readInt();
        this.timezoneOffset = source.readInt();
        this.year = source.readInt();
        this.minutes = source.readInt();
        this.time = source.readLong();
        this.day = source.readInt();
    }

    public Date(Calendar calendar) {
        this.date = calendar.get(Calendar.DATE);
        this.hours = calendar.get(Calendar.HOUR);
        this.seconds = calendar.get(Calendar.SECOND);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
        this.minutes = calendar.get(Calendar.MINUTE);
        this.time = calendar.getTimeInMillis();
        this.day = calendar.get(Calendar.DAY_OF_WEEK);
    }

    public Date() {
        this(Calendar.getInstance());
    }

    protected Date(Parcel in) {
        this.date = in.readInt();
        this.hours = in.readInt();
        this.seconds = in.readInt();
        this.month = in.readInt();
        this.timezoneOffset = in.readInt();
        this.year = in.readInt();
        this.minutes = in.readInt();
        this.time = in.readLong();
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<Date> CREATOR = new Parcelable.Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel source) {
            return new Date(source);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
}