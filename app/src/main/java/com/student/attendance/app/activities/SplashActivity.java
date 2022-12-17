package com.student.attendance.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.student.attendance.app.R;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.DataManager;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.StorageHelper;

public class SplashActivity extends BaseActivity {
    private static final int SPLASH_DELAY_MILLIS = 2000;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isOpenHome()) {
                openHome();
            } else {
                openLogin();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        openApp();
    }

    private boolean isOpenHome() {
        User user = StorageHelper.getCurrentUser();
        return user != null;
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void openApp() {
        handler.postDelayed(runnable, SPLASH_DELAY_MILLIS);
    }

    private void open() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}