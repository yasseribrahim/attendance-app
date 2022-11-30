package com.student.attendance.app.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.student.attendance.app.R;
import com.student.attendance.app.adapters.CourseSummaryAdapter;
import com.student.attendance.app.fragments.ProgressDialogFragment;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.models.Lecture;
import com.student.attendance.app.models.Summary;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.LecturesCallback;
import com.student.attendance.app.presenters.LecturesPresenter;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.StorageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSummaryActivity extends BaseActivity implements LecturesCallback, UsersCallback {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView name;
    private TextView degreePerLecture;
    private TextView message;
    private ViewGroup containerTotal;
    private TextView total;
    private View separator;

    private UsersPresenter usersPresenter;
    private LecturesPresenter presenter;
    private CourseSummaryAdapter adapter;
    private List<Lecture> lectures;
    private List<Summary> summaries;
    private List<User> users;
    private Course course;
    private boolean isLecturerAccount;
    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_summary);

        toolbar = findViewById(R.id.toolbar);
        containerTotal = findViewById(R.id.container_total);
        total = findViewById(R.id.total);
        separator = findViewById(R.id.separator);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_course_summary_title));

        name = findViewById(R.id.name);
        degreePerLecture = findViewById(R.id.degree_per_lecture);

        course = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        presenter = new LecturesPresenter(this);
        usersPresenter = new UsersPresenter(this);
        message = findViewById(R.id.message);
        User user = StorageHelper.getCurrentUser();
        if (user != null && user.getUserType() == Constants.USER_TYPE_STUDENT) {
            userId = user.getId();
            isLecturerAccount = false;
        } else {
            isLecturerAccount = true;
        }
        summaries = new ArrayList<>();
        lectures = new ArrayList<>();
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseSummaryAdapter(summaries, !isLecturerAccount);
        recyclerView.setAdapter(adapter);
        bindCourse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        presenter.getLecturesByCourseId(course.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
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

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void bindCourse() {
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }
        name.setText(course.getName());
        degreePerLecture.setText(getString(R.string.str_degree_per_lecture_summary, course.getDegreePerLecture() + ""));
    }

    @Override
    public void onGetLecturesComplete(List<Lecture> lectures) {
        this.lectures.clear();
        this.lectures.addAll(lectures);
        summaries.clear();
        Map<String, Integer> map = new HashMap<>();

        float total = 0;
        for (Lecture lecture : lectures) {
            if (lecture.getStudents() == null) {
                lecture.setStudents(new ArrayList<>());
            }
            Summary summary = new Summary(lecture.getId(), lecture.getName(), null);
            if (isLecturerAccount) {
                for (String studentId : lecture.getStudents()) {
                    Integer number = map.get(studentId);
                    if (number == null) {
                        number = 0;
                    }
                    number++;
                    map.put(studentId, number);
                }
            } else {
                if (lecture.getStudents().contains(userId)) {
                    summary.setDegree(course.getDegreePerLecture());
                    total += course.getDegreePerLecture();
                }
                summaries.add(summary);
            }
        }

        if (isLecturerAccount) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                summaries.add(new Summary("", "", entry.getValue() * course.getDegreePerLecture(), entry.getValue(), entry.getKey()));
            }
        }

        if (!isLecturerAccount) {
            this.containerTotal.setVisibility(View.VISIBLE);
            this.separator.setVisibility(View.VISIBLE);
            this.total.setText(total + "");
        } else {
            this.containerTotal.setVisibility(View.INVISIBLE);
            this.separator.setVisibility(View.INVISIBLE);
        }

        message.setVisibility(this.lectures.isEmpty() ? View.VISIBLE : View.GONE);
        summaries.add(0, new Summary("", "", ""));
        if (isLecturerAccount) {
            usersPresenter.getUsersByGrade(course.getGradeId());
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        for (Summary summary : summaries) {
            int index = users.indexOf(new User(summary.getStudentName()));
            if (index != -1) {
                summary.setStudentName(users.get(index).getFullName());
            }
        }
        adapter.notifyDataSetChanged();
    }
}