package com.student.attendance.app.presenters;

import com.student.attendance.app.models.User;

import java.util.List;

public interface UsersCallback extends BaseCallback {
    default void onGetUsersComplete(List<User> users) {
    }

    default void onGetSaveUserComplete() {
    }

    default void onGetDeleteUserComplete(int position) {
    }

    default void onGetSignupUserComplete() {
    }

    default void onGetSignupUserFail(String message) {
    }

    default void onGetUserComplete(User user) {
    }
}
