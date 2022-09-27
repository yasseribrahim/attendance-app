package com.student.attendance.app.presenters;

import com.student.attendance.app.models.Lecture;

import java.util.List;

public interface LecturesCallback extends BaseCallback {
    default void onGetLecturesComplete(List<Lecture> lectures) {
    }

    default void onSaveLectureComplete() {
    }
}
