package com.student.attendance.app.models;

import java.util.Objects;

public class QRLecture {
    private String lectureId;
    private String courseId;
    private String location;

    public QRLecture() {
    }

    public QRLecture(String lectureId, String courseId, String location) {
        this.lectureId = lectureId;
        this.courseId = courseId;
        this.location = location;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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
        QRLecture qrLecture = (QRLecture) o;
        return lectureId.equals(qrLecture.lectureId) && courseId.equals(qrLecture.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lectureId, courseId);
    }

    @Override
    public String toString() {
        return "QRLecture{" +
                "lectureId='" + lectureId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
