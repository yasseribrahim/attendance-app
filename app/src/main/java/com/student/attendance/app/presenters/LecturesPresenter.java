package com.student.attendance.app.presenters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.attendance.app.models.Lecture;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class LecturesPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private LecturesCallback callback;

    public LecturesPresenter(LecturesCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_LECTURES).getRef();
        this.callback = callback;
    }

    public void getLecturesByCourseId(String courseId) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Lecture> lectures = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Lecture lecture = child.getValue(Lecture.class);
                    lecture.setId(child.getKey());
                    lecture.setCourseId(courseId);
                    lectures.add(lecture);
                }

                if (callback != null) {
                    callback.onGetLecturesComplete(lectures);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                callback.onHideLoading();
            }
        };

        reference.child(courseId).addListenerForSingleValueEvent(listener);
    }

    public void save(Lecture lecture, String courseId) {
        callback.onShowLoading();
        reference.child(courseId).setValue(lecture);
        if (callback != null) {
            callback.onSaveLectureComplete();
        }
        callback.onHideLoading();
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
