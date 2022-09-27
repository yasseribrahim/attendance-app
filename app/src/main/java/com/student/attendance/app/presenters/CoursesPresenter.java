package com.student.attendance.app.presenters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CoursesPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private CoursesCallback callback;

    public CoursesPresenter(CoursesCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_COURSES).getRef();
        this.callback = callback;
    }

    public void getCoursesByGradeId(int gradeId) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Course> courses = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Course course = child.getValue(Course.class);
                    if (course.getGradeId() == gradeId) {
                        course.setId(child.getKey());
                        courses.add(course);
                    }
                }

                if (callback != null) {
                    callback.onGetCoursesComplete(courses);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                callback.onHideLoading();
            }
        };
        reference.addListenerForSingleValueEvent(listener);
    }

    public void save(User user) {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
        node.child(user.getId()).setValue(user);
    }

    public void getCoursesByLecturerId(String lecturerId) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Course> courses = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Course course = child.getValue(Course.class);
                    if (course.getLectureId().equals(lecturerId)) {
                        course.setId(child.getKey());
                        courses.add(course);
                    }
                }

                if (callback != null) {
                    callback.onGetCoursesComplete(courses);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                callback.onHideLoading();
            }
        };
        reference.addListenerForSingleValueEvent(listener);
    }

    public void getCoursesByStudentId(String studentId) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Course> courses = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Course course = child.getValue(Course.class);
                    if (course.getStudents().contains(studentId)) {
                        course.setId(child.getKey());
                        courses.add(course);
                    }
                }

                if (callback != null) {
                    callback.onGetCoursesComplete(courses);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                callback.onHideLoading();
            }
        };
        reference.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
