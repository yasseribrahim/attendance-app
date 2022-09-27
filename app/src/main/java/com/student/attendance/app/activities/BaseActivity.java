package com.student.attendance.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.student.attendance.app.CustomApplication;
import com.student.attendance.app.activities.users.admin.HomeAdminActivity;
import com.student.attendance.app.activities.users.teacher.HomeTeacherActivity;
import com.student.attendance.app.activities.users.student.HomeStudentActivity;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.StorageHelper;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    protected SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Locale getCurrentLanguage() {
        try {
            if (preferences == null) {
                preferences = PreferenceManager.getDefaultSharedPreferences(CustomApplication.getApplication());
            }
            return new Locale(preferences.getString("language", Locale.getDefault().getLanguage()));
        } catch (Exception ex) {
        }
        return Locale.getDefault();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.onAttach(context, getCurrentLanguage().getLanguage()));
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void openHome() {
        User user = StorageHelper.getCurrentUser();
        Intent intent = new Intent(this, LoginActivity.class);
        if (user != null) {
            switch (user.getUserType()) {
                case Constants.USER_TYPE_ADMIN:
                    intent = new Intent(this, HomeAdminActivity.class);
                    break;
                case Constants.USER_TYPE_STUDENT:
                    intent = new Intent(this, HomeStudentActivity.class);
                    break;
                case Constants.USER_TYPE_LECTURER:
                    intent = new Intent(this, HomeTeacherActivity.class);
                    break;
                default:
                    Toast.makeText(this, "This user not support from app, Please contact with admin", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        startActivity(intent);
        finishAffinity();
    }
}
