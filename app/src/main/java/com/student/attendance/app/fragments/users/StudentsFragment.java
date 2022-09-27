package com.student.attendance.app.fragments.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.student.attendance.app.R;
import com.student.attendance.app.adapters.UsersSelectorAdapter;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class StudentsFragment extends Fragment implements UsersCallback, UsersSelectorAdapter.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout;
    private TextView students;
    private TextView message;

    private UsersPresenter presenter;
    private RecyclerView recyclerView;
    private UsersSelectorAdapter adapter;
    private List<User> users;
    private Course course;

    public static StudentsFragment newInstance(Course course) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT, course);
        StudentsFragment fragment = new StudentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_students, container, false);

        course = getArguments().getParcelable(Constants.ARG_OBJECT);
        presenter = new UsersPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        students = rootView.findViewById(R.id.students);
        message = rootView.findViewById(R.id.message);

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        users = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsersSelectorAdapter(users, new ArrayList<>(), false, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void load() {
        presenter.getUsersByGrade(course.getGradeId());
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
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        for (User user : users) {
            if (course.getStudents().contains(user.getId())) {
                this.users.add(user);
            }
        }

        students.setText(getString(R.string.str_students_counter, this.users.size()));
        message.setVisibility(this.users.isEmpty() ? View.VISIBLE : View.GONE);

        adapter.notifyDataSetChanged();
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

    @Override
    public void onItemViewListener(int position) {

    }
}