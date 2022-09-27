package com.student.attendance.app.activities.users.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barisatalay.filterdialog.FilterDialog;
import com.barisatalay.filterdialog.model.DialogListener;
import com.barisatalay.filterdialog.model.FilterItem;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.student.attendance.app.R;
import com.student.attendance.app.activities.BaseUploadActivity;
import com.student.attendance.app.adapters.UsersSelectorAdapter;
import com.student.attendance.app.fragments.ProgressDialogFragment;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.models.Grade;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.DataManager;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends BaseUploadActivity implements UsersCallback, UsersSelectorAdapter.OnItemClickListener {
    private Toolbar toolbar;
    private EditText name;
    private EditText degreePerLecture;
    private TextView grade;
    private TextView lecturer;
    private TextView btnSave;
    private RecyclerView recyclerView;

    private UsersPresenter presenter;
    private UsersSelectorAdapter adapter;
    private List<User> students, lecturers, filteredStudents, selectedStudents;
    private Course course;
    private int selectedGradeId, selectionUserTypeId;
    private String selectedLecturerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_create_new_course));

        presenter = new UsersPresenter(this);
        students = new ArrayList<>();
        lecturers = new ArrayList<>();
        filteredStudents = new ArrayList<>();
        selectedStudents = new ArrayList<>();

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        lecturer = findViewById(R.id.lecturer);
        grade = findViewById(R.id.grade);
        grade.setEnabled(false);
        recyclerView = findViewById(R.id.recyclerView);
        btnSave = findViewById(R.id.btn_save);
        degreePerLecture = findViewById(R.id.degree_per_lecture);

        setImageClickListener();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersSelectorAdapter(filteredStudents, selectedStudents, true, this);
        recyclerView.setAdapter(adapter);

        selectedGradeId = getIntent().getIntExtra(Constants.ARG_ID, 1);
        course = getIntent().getParcelableExtra("object");
        bindCourse();
        lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLecturersDialog();
            }
        });
        grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGradesDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = CourseActivity.this.name.getText().toString().trim();
                String lecturer = CourseActivity.this.lecturer.getText().toString().trim();
                String degreePerLectureString = CourseActivity.this.degreePerLecture.getText().toString().trim();
                float degreePerLecture = 0;
                try {
                    degreePerLecture = Float.parseFloat(degreePerLectureString);
                } catch (Exception ex) {
                }

                if (name.isEmpty()) {
                    CourseActivity.this.name.setError(getString(R.string.str_course_name_alert));
                    CourseActivity.this.name.requestFocus();
                    return;
                }
                if (lecturer.isEmpty()) {
                    Toast.makeText(CourseActivity.this, R.string.str_course_lecturer_alert, Toast.LENGTH_LONG).show();
                    return;
                }
                if (selectedGradeId <= 0) {
                    Toast.makeText(CourseActivity.this, R.string.str_course_grade_alert, Toast.LENGTH_LONG).show();
                    return;
                }
                if (selectedStudents.isEmpty()) {
                    Toast.makeText(CourseActivity.this, R.string.str_course_students_alert, Toast.LENGTH_LONG).show();
                    return;
                }
                if (lecturer.isEmpty()) {
                    Toast.makeText(CourseActivity.this, R.string.str_course_lecturer_alert, Toast.LENGTH_LONG).show();
                    return;
                }
                if (degreePerLecture <= 0) {
                    Toast.makeText(CourseActivity.this, R.string.str_course_degree_per_lecture_alert, Toast.LENGTH_LONG).show();
                    return;
                }

                List<String> ids = new ArrayList<>();
                for (User user : selectedStudents) {
                    ids.add(user.getId());
                }

                course.setName(name);
                course.setGradeId(selectedGradeId);
                course.setLectureId(selectedLecturerId);
                course.setLectureName(lecturer);
                course.setStudents(ids);

                showProgressBar();
                save();
            }
        });
    }

    @Override
    protected void onUploadImageCallback(String url) {
        course.setImage(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectionUserTypeId = Constants.USER_TYPE_STUDENT;
        presenter.getUsersByType(Constants.USER_TYPE_STUDENT);
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
        if (selectionUserTypeId == Constants.USER_TYPE_STUDENT) {
            this.students.clear();
            this.students.addAll(users);
            filterUsers();
            selectionUserTypeId = Constants.USER_TYPE_LECTURER;
            presenter.getUsersByType(Constants.USER_TYPE_LECTURER);
        } else {
            this.lecturers.clear();
            this.lecturers.addAll(users);
        }
    }

    @Override
    public void onItemViewListener(int position) {

    }

    private void filterUsers() {
        this.filteredStudents.clear();
        this.selectedStudents.clear();

        for (User user : students) {
            if (user.getGradeId() == selectedGradeId) {
                filteredStudents.add(user);
            }
        }
        for (String id : course.getStudents()) {
            this.selectedStudents.add(new User(id));
        }

        adapter.notifyDataSetChanged();
    }

    private void save() {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_COURSES);
        if (course.getId() != null && !course.getId().isEmpty()) {
            node.child(course.getId()).setValue(course);
        } else {
            node.push().setValue(course);
        }
        Toast.makeText(CourseActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
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

    public void hideProgressBar() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    public void showProgressBar() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    private void showGradesDialog() {
        List<FilterItem> items = new ArrayList<>();

        List<Grade> grades = DataManager.getGrades(this);
        for (Grade grade : grades) {
            items.add(new FilterItem.Builder().code(grade.getId() + "").name(grade.getName()).build());
        }

        final FilterDialog filterDialog = new FilterDialog(this);
        filterDialog.setToolbarTitle(getString(R.string.str_course_grade_hint));
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

                    filterUsers();
                    filterDialog.dispose();
                } catch (Exception ex) {
                    Toast.makeText(CourseActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLecturersDialog() {
        List<FilterItem> items = new ArrayList<>();

        for (User user : lecturers) {
            items.add(new FilterItem.Builder().code(user.getId()).name(user.getFullName()).build());
        }

        final FilterDialog filterDialog = new FilterDialog(this);
        filterDialog.setToolbarTitle(getString(R.string.str_course_lecturer_hint));
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
                    selectedLecturerId = selectedItem.getCode();
                    lecturer.setText(selectedItem.getName());
                    filterDialog.dispose();
                } catch (Exception ex) {
                    Toast.makeText(CourseActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindCourse() {
        if (course == null) {
            course = new Course();
            course.setGradeId(selectedGradeId);
        }
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }

        name.setText(course.getName());
        degreePerLecture.setText(course.getDegreePerLecture() + "");
        if (course.getGradeId() > 0) {
            grade.setText(UIHelper.parseGrade(this, course.getGradeId()));
        } else {
            grade.setText("");
        }
        lecturer.setText(course.getLectureName());

        selectedLecturerId = course.getLectureId();
        selectedGradeId = course.getGradeId();
        Glide.with(this).load(course.getImage()).placeholder(R.drawable.ic_profile).into(image);
        filterUsers();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }
}