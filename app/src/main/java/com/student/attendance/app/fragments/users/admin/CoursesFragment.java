package com.student.attendance.app.fragments.users.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.student.attendance.app.R;
import com.student.attendance.app.activities.users.admin.CourseActivity;
import com.student.attendance.app.adapters.CoursesAdapter;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.presenters.CoursesCallback;
import com.student.attendance.app.presenters.CoursesPresenter;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment implements CoursesCallback, CoursesAdapter.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout;
    private AutoCompleteTextView textSearch;
    private TextView message;
    private TextView btnNewCourse;

    private CoursesPresenter presenter;
    private RecyclerView recyclerView;
    private CoursesAdapter adapter;
    private List<Course> courses, searchedCourses;
    private int gradeId;

    public static CoursesFragment newInstance(int gradeId) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_ID, gradeId);
        CoursesFragment fragment = new CoursesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);

        gradeId = getArguments().getInt(Constants.ARG_ID);
        presenter = new CoursesPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        btnNewCourse = rootView.findViewById(R.id.new_course);
        message = rootView.findViewById(R.id.message);
        textSearch = rootView.findViewById(R.id.text_search);

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        btnNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CourseActivity.class);
                intent.putExtra(Constants.ARG_ID, gradeId);
                startActivity(intent);
            }
        });

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        courses = new ArrayList<>();
        searchedCourses = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CoursesAdapter(searchedCourses, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void load() {
        presenter.getCoursesByGradeId(gradeId);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetCoursesComplete(List<Course> courses) {
        this.courses.clear();
        this.courses.addAll(courses);
        search(textSearch.getText().toString());
    }

    @Override
    public void onShowLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void search(String searchedText) {
        searchedCourses.clear();
        if (!searchedText.isEmpty()) {
            for (Course course : courses) {
                if (isMatched(course, searchedText)) {
                    searchedCourses.add(course);
                }
            }
        } else {
            searchedCourses.addAll(courses);
        }

        refresh();
    }

    private boolean isMatched(Course course, String text) {
        String searchedText = text.toLowerCase();
        boolean result = course.getName().toLowerCase().contains(searchedText);
        return result;
    }

    private void refresh() {
        message.setVisibility(View.GONE);
        if (searchedCourses.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemViewListener(int position) {
        Course course = searchedCourses.get(position);
        Intent intent = new Intent(getContext(), CourseActivity.class);
        intent.putExtra(Constants.ARG_ID, gradeId);
        intent.putExtra("object", course);
        startActivity(intent);
    }
}