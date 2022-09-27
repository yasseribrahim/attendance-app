package com.student.attendance.app.activities.users.teacher;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.student.attendance.app.R;
import com.student.attendance.app.activities.BaseActivity;
import com.student.attendance.app.adapters.UsersSelectorAdapter;
import com.student.attendance.app.models.Lecture;
import com.student.attendance.app.models.QRLecture;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.BitmapHelper;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.LocaleHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LectureActivity extends BaseActivity implements UsersCallback, UsersSelectorAdapter.OnItemClickListener {
    private DatabaseReference reference;
    private ValueEventListener listener;

    private Toolbar toolbar;
    private EditText name;
    private TextView date;
    private TextView btnSave;
    private RecyclerView recyclerView;
    private ViewGroup qrContainer;
    private View separator1, separator2;
    private TextView message;
    private TextView lblStudents;
    private ProgressBar progress;
    private ImageView qr;

    private UsersPresenter presenter;
    private UsersSelectorAdapter adapter;
    private List<User> students, allStudents;
    private Lecture lecture;
    private String courseId, currentLocation;
    private int gradeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        lecture = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_create_new_course));

        presenter = new UsersPresenter(this);
        students = new ArrayList<>();
        allStudents = new ArrayList<>();

        name = findViewById(R.id.name);
        lblStudents = findViewById(R.id.lbl_students);
        date = findViewById(R.id.date);
        qrContainer = findViewById(R.id.qr_container);
        separator1 = findViewById(R.id.separator1);
        separator2 = findViewById(R.id.separator2);
        message = findViewById(R.id.message);
        progress = findViewById(R.id.progress);
        qr = findViewById(R.id.qr);
        recyclerView = findViewById(R.id.recyclerView);
        btnSave = findViewById(R.id.btn_save);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersSelectorAdapter(students, new ArrayList<>(), false, this);
        recyclerView.setAdapter(adapter);

        courseId = getIntent().getExtras().getString(Constants.ARG_COURSE_ID);
        currentLocation = getIntent().getExtras().getString(Constants.ARG_OBJECT);
        gradeId = getIntent().getIntExtra(Constants.ARG_GRADE_ID, 1);
        bindLecture();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = LectureActivity.this.name.getText().toString().trim();

                if (name.isEmpty()) {
                    LectureActivity.this.name.setError(getString(R.string.str_lecture_name_alert));
                    LectureActivity.this.name.requestFocus();
                    return;
                }

                lecture.setName(name);
                lecture.setLocation(currentLocation);

                showProgressBar();
                hideKeyboard();
                save();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference(Constants.NODE_NAME_LECTURES).child(courseId).child(lecture.getId());
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Lecture lecture = snapshot.getValue(Lecture.class);
                    lecture.setId(snapshot.getKey());
                    LectureActivity.this.lecture = lecture;
                    bindLecture();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LectureActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getUsersByGrade(gradeId);

        if (reference != null && listener != null) {
            reference.addValueEventListener(listener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.allStudents.clear();
        this.allStudents.addAll(users);
        prepareStudents();
    }

    @Override
    public void onItemViewListener(int position) {

    }

    private void prepareStudents() {
        students.clear();
        for (User user : allStudents) {
            if (lecture.getStudents().contains(user.getId())) {
                students.add(user);
            }
        }
        lblStudents.setText(getString(R.string.str_students_counter, students.size()));
        message.setVisibility(students.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void save() {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_LECTURES);
        node.child(courseId).child(lecture.getDate().getTime() + "").setValue(lecture);
        Toast.makeText(LectureActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        hideProgressBar();
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

    public void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    private void bindLecture() {
        if (lecture == null) {
            lecture = new Lecture();

            qrContainer.setVisibility(View.GONE);
            separator1.setVisibility(View.GONE);
            separator2.setVisibility(View.GONE);
            lblStudents.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
        } else {
            qrContainer.setVisibility(View.VISIBLE);
            separator1.setVisibility(View.VISIBLE);
            separator2.setVisibility(View.VISIBLE);
            lblStudents.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            try {
                qr.setImageBitmap(BitmapHelper.generateQRCode(new Gson().toJson(new QRLecture(lecture.getId(), courseId, lecture.getLocation()))));
            } catch (Exception ex) {
                Toast.makeText(this, "General Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (lecture.getStudents() == null) {
            lecture.setStudents(new ArrayList<>());
        }
        name.setText(lecture.getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lecture.getDate().getTime());
        date.setText(getString(R.string.str_lecture_date, new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(calendar.getTime())));
        prepareStudents();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        showProgressBar();
    }

    @Override
    public void onHideLoading() {
        hideProgressBar();
    }
}