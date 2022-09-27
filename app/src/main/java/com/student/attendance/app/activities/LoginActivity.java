package com.student.attendance.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.attendance.app.R;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.StorageHelper;


public class LoginActivity extends BaseActivity {
    private TextView forgetPassword;
    private ProgressBar progressBar;
    private EditText email, password;
    private FirebaseAuth auth;
    private TextView login;
    private CheckBox rememberMe;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rememberMe = findViewById(R.id.remember_me);
        login = findViewById(R.id.login);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        rememberMe.setChecked(getRememberMe());
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                setRememberMe(checked);
            }
        });

        database = FirebaseDatabase.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = LoginActivity.this.email.getText().toString().trim();
                String password = LoginActivity.this.password.getText().toString().trim();

                if (!isValidEmailAndPassword(email, password)) {
                    Toast.makeText(getApplicationContext(), R.string.str_invalid_username_or_password, Toast.LENGTH_LONG).show();
                    return;
                }

                showProgressBar();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                            userReference = database.getReference(userPath);
                            valueEventListenerUser = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    StorageHelper.setCurrentUser(user);
                                    Toast.makeText(getApplicationContext(), R.string.str_login_successful, Toast.LENGTH_LONG).show();
                                    openHome();
                                    hideProgressBar();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                    hideProgressBar();
                                }
                            };
                            userReference.addValueEventListener(valueEventListenerUser);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.str_invalid_username_or_password) + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userReference != null && valueEventListenerUser != null) {
            userReference.removeEventListener(valueEventListenerUser);
        }
        valueEventListenerUser = null;
        userReference = null;
        database = null;
    }

    private void setRememberMe(boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember_me", value);
        editor.apply();
    }

    private boolean getRememberMe() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("remember_me", false);
    }

    private boolean isValidEmailAndPassword(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
