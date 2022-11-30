package com.student.attendance.app.models;

import java.util.Objects;

public class Summary {
    private String lectureId;
    private String lectureName;
    private float degree;
    private int studentNumberLectures;
    private String studentName;

    public Summary(String lectureId, String lectureName, String studentName) {
        this(lectureId, lectureName, 0, 0, studentName);
    }

    public Summary(String lectureId, String lectureName, float degree, int studentNumberLectures, String studentName) {
        this.lectureId = lectureId;
        this.lectureName = lectureName;
        this.degree = degree;
        this.studentNumberLectures = studentNumberLectures;
        this.studentName = studentName;
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

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public int getStudentNumberLectures() {
        return studentNumberLectures;
    }

    public void setStudentNumberLectures(int studentNumberLectures) {
        this.studentNumberLectures = studentNumberLectures;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary summary = (Summary) o;
        return lectureId.equals(summary.lectureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lectureId);
    }
}
