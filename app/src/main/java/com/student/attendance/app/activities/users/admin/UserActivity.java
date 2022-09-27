package com.student.attendance.app.activities.users.admin;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.barisatalay.filterdialog.FilterDialog;
import com.barisatalay.filterdialog.model.DialogListener;
import com.barisatalay.filterdialog.model.FilterItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.student.attendance.app.R;
import com.student.attendance.app.activities.BaseActivity;
import com.student.attendance.app.models.Grade;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.DataManager;
import com.student.attendance.app.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements UsersCallback {
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText fullName;
    private TextView lblGrade, grade, lblPassword, lblConfirmPassword;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText phone;
    private EditText address;
    private TextView btnSave;
    private TextView header;
    private FrameLayout gradeContainer, passwordContainer, passwordConfirmContainer;
    private int selectedTypeUser;

    private UsersPresenter presenter;
    private User user;
    private int selectedGradeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_create_new_user));

        presenter = new UsersPresenter(this);
        fullName = (EditText) findViewById(R.id.full_name);
        lblGrade = findViewById(R.id.lbl_grade);
        grade = findViewById(R.id.grade);
        gradeContainer = findViewById(R.id.grade_container);
        passwordContainer = findViewById(R.id.container_password);
        passwordConfirmContainer = findViewById(R.id.container_confirm_password);
        lblPassword = findViewById(R.id.lbl_password);
        lblConfirmPassword = findViewById(R.id.lbl_confirm_password);
        header = findViewById(R.id.header);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.re_password);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        btnSave = findViewById(R.id.btn_save);

        selectedTypeUser = getIntent().getIntExtra(Constants.ARG_ID, 1);
        user = getIntent().getParcelableExtra("object");
        bindUser();

        grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGradesDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = UserActivity.this.password.getText().toString().trim();
                String confirmPassword = UserActivity.this.confirmPassword.getText().toString().trim();
                String username = UserActivity.this.username.getText().toString().trim();
                String phone = UserActivity.this.phone.getText().toString().trim();
                String address = UserActivity.this.address.getText().toString().trim();
                String fullName = UserActivity.this.fullName.getText().toString().trim();

                if (username.isEmpty()) {
                    UserActivity.this.username.setError(getString(R.string.str_username_invalid));
                    UserActivity.this.username.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    UserActivity.this.username.setError(getString(R.string.str_username_invalid));
                    UserActivity.this.username.requestFocus();
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    UserActivity.this.password.setError(getString(R.string.str_password_length_invalid));
                    UserActivity.this.password.requestFocus();
                    return;
                }
                if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                    UserActivity.this.password.setError(getString(R.string.str_password_confirm_invalid));
                    UserActivity.this.password.requestFocus();
                    return;
                }
                if (fullName.isEmpty()) {
                    UserActivity.this.phone.setError(getString(R.string.str_full_name_invalid));
                    UserActivity.this.phone.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    UserActivity.this.fullName.setError(getString(R.string.str_phone_invalid));
                    UserActivity.this.fullName.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    UserActivity.this.address.setError(getString(R.string.str_address_invalid));
                    UserActivity.this.address.requestFocus();
                    return;
                }

                if (selectedTypeUser == Constants.USER_TYPE_STUDENT && selectedGradeId <= 0) {
                    Toast.makeText(UserActivity.this, R.string.str_grade_alert, Toast.LENGTH_LONG).show();
                    return;
                }

                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);
                user.setGradeId(selectedGradeId);
                user.setUserType(selectedTypeUser);

                if (user.getId() == null || user.getId().isEmpty()) {
                    presenter.signup(user);
                } else {
                    presenter.save(user);
                }
            }
        });
    }

    @Override
    public void onGetSignupUserComplete() {
        Toast.makeText(UserActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGetSaveUserComplete() {
        Toast.makeText(UserActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGetSignupUserFail(String message) {
        Toast.makeText(UserActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(UserActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHideLoading() {
        hideProgressBar();
    }

    @Override
    public void onShowLoading() {
        showProgressBar();
    }

    private void save() {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
        node.child(user.getId()).setValue(user);
        Toast.makeText(UserActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showGradesDialog() {
        List<FilterItem> items = new ArrayList<>();

        List<Grade> grades = DataManager.getGrades(this);
        for (Grade grade : grades) {
            items.add(new FilterItem.Builder().code(grade.getId() + "").name(grade.getName()).build());
        }

        final FilterDialog filterDialog = new FilterDialog(this);
        filterDialog.setToolbarTitle(getString(R.string.str_grade_hint));
        filterDialog.setSearchBoxHint(getString(R.string.str_search_hint));
        filterDialog.setList(items);

        /*
         * nameField : model's is the part that will appear on the screen.
         * idField : id section in the model.
         * dialogListener : when any row item selected, selected item will be return from interface
         */
        filterDialog.show("code", "name", new DialogListener.Single() {
            @Override
            public void onResult(FilterItem selectedItem) {
                try {
                    selectedGradeId = Integer.parseInt(selectedItem.getCode());
                    grade.setText(selectedItem.getName());
                    filterDialog.dispose();
                } catch (Exception ex) {
                    Toast.makeText(UserActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindUser() {
        username.setEnabled(false);
        passwordContainer.setVisibility(View.GONE);
        passwordConfirmContainer.setVisibility(View.GONE);
        lblPassword.setVisibility(View.GONE);
        lblConfirmPassword.setVisibility(View.GONE);
        if (user == null) {
            user = new User();
            username.setEnabled(true);

            passwordContainer.setVisibility(View.VISIBLE);
            passwordConfirmContainer.setVisibility(View.VISIBLE);
            lblPassword.setVisibility(View.VISIBLE);
            lblConfirmPassword.setVisibility(View.VISIBLE);
        }

        fullName.setText(user.getFullName());
        username.setText(user.getUsername());
        password.setText(user.getPassword());
        confirmPassword.setText(user.getPassword());
        phone.setText(user.getPhone());
        address.setText(user.getAddress());
        selectedGradeId = user.getGradeId();

        lblGrade.setVisibility(View.GONE);
        gradeContainer.setVisibility(View.GONE);
        switch (selectedTypeUser) {
            case Constants.USER_TYPE_ADMIN:
                header.setText(R.string.str_user_type_admin_title);
                break;
            case Constants.USER_TYPE_STUDENT:
                header.setText(R.string.str_user_type_student_title);
                lblGrade.setVisibility(View.VISIBLE);
                gradeContainer.setVisibility(View.VISIBLE);
                break;
            case Constants.USER_TYPE_LECTURER:
                header.setText(R.string.str_user_type_lecturer_title);
                break;
        }
    }
}