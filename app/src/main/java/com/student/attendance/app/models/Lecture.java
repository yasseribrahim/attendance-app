package com.student.attendance.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Lecture implements Parcelable {
    private String id;
    private String courseId;
    private String name;
    private Date date;
    private String location;
    private List<String> students;

    public Lecture() {
        Calendar calendar = Calendar.getInstance();
        this.id = calendar.getTimeInMillis() + "";
        location = "0,0";
        this.name = "";
        this.date = new Date(calendar);
        this.students = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecture lecture = (Lecture) o;
        return id.equals(lecture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.courseId);
        dest.writeString(this.name);
        dest.writeParcelable(this.date, flags);
        dest.writeString(this.location);
        dest.writeStringList(this.students);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.courseId = source.readString();
        this.name = source.readString();
        this.date = source.readParcelable(Date.class.getClassLoader());
        this.location = source.readString();
        this.students = source.createStringArrayList();
    }

    protected Lecture(Parcel in) {
        this.id = in.readString();
        this.courseId = in.readString();
        this.name = in.readString();
        this.date = in.readParcelable(Date.class.getClassLoader());
        this.location = in.readString();
        this.students = in.createStringArrayList();
    }

    public static final Creator<Lecture> CREATOR = new Creator<Lecture>() {
        @Override
        public Lecture createFromParcel(Parcel source) {
            return new Lecture(source);
        }

        @Override
        public Lecture[] newArray(int size) {
            return new Lecture[size];
        }
    };
}
