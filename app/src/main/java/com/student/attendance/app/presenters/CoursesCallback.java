package com.student.attendance.app.presenters;

import com.student.attendance.app.models.Course;

import java.util.List;

public interface CoursesCallback extends BaseCallback {
    default void onGetCoursesComplete(List<Course> courses) {
    }
}
