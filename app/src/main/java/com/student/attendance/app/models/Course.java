package com.student.attendance.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class Course implements Parcelable {
    private String id;
    private String name;
    private int gradeId;
    private float degreePerLecture;
    private String lectureId;
    private String lectureName;
    private String image;
    private List<String> students;

    public Course() {
        degreePerLecture = 0.5f;
    }

    public Course(String id, String name, int gradeId) {
        this.id = id;
        this.name = name;
        this.gradeId = gradeId;
        degreePerLecture = 0.5f;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public float getDegreePerLecture() {
        return degreePerLecture;
    }

    public void setDegreePerLecture(float degreePerLecture) {
        this.degreePerLecture = degreePerLecture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id.equals(course.id);
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
        dest.writeString(this.name);
        dest.writeInt(this.gradeId);
        dest.writeFloat(this.degreePerLecture);
        dest.writeString(this.lectureId);
        dest.writeString(this.lectureName);
        dest.writeString(this.image);
        dest.writeStringList(this.students);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.gradeId = source.readInt();
        this.degreePerLecture = source.readFloat();
        this.lectureId = source.readString();
        this.lectureName = source.readString();
        this.image = source.readString();
        this.students = source.createStringArrayList();
    }

    protected Course(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.gradeId = in.readInt();
        this.degreePerLecture = in.readFloat();
        this.lectureId = in.readString();
        this.lectureName = in.readString();
        this.image = in.readString();
        this.students = in.createStringArrayList();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
