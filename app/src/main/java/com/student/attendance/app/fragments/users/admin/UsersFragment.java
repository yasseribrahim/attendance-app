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
import com.student.attendance.app.activities.users.admin.UserActivity;
import com.student.attendance.app.adapters.UsersAdapter;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment implements UsersCallback, UsersAdapter.OnItemClickListener {
    private TextView btnNewUser;
    private SwipeRefreshLayout refreshLayout;
    private AutoCompleteTextView textSearch;
    private TextView message;

    private UsersPresenter presenter;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> users, searchedUsers;
    private int userType;

    public static UsersFragment newInstance(int userType) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_ID, userType);
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users, container, false);

        userType = getArguments().getInt(Constants.ARG_ID);
        presenter = new UsersPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        btnNewUser = rootView.findViewById(R.id.new_user);
        message = rootView.findViewById(R.id.message);
        textSearch = rootView.findViewById(R.id.text_search);

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra(Constants.ARG_ID, userType);
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

        users = new ArrayList<>();
        searchedUsers = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(searchedUsers, this);
        recyclerView.setAdapter(usersAdapter);

        return rootView;
    }

    private void load() {
        presenter.getUsersByType(userType);
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
        this.users.addAll(users);
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
        searchedUsers.clear();
        if (!searchedText.isEmpty()) {
            for (User user : users) {
                if (isMatched(user, searchedText)) {
                    searchedUsers.add(user);
                }
            }
        } else {
            searchedUsers.addAll(users);
        }

        refresh();
    }

    private boolean isMatched(User user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getFullName().toLowerCase().contains(searchedText) ||
                (user.getAddress() != null && user.getAddress().toLowerCase().contains(searchedText)) ||
                (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchedText)) ||
                (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        message.setVisibility(View.GONE);
        if (searchedUsers.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        }

        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemViewListener(int position) {
        User user = searchedUsers.get(position);
        Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra(Constants.ARG_ID, userType);
        intent.putExtra("object", user);
        startActivity(intent);
    }

    @Override
    public void onDeleteItemViewListener(int position) {
        if(position >= 0 && position < searchedUsers.size()) {
            User user = searchedUsers.get(position);
            int index = users.indexOf(user);
            searchedUsers.remove(position);
            if(index >= 0 && index < users.size()) {
                users.remove(position);
            }
            presenter.delete(user, position);
        }
    }

    @Override
    public void onGetDeleteUserComplete(int position) {
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        usersAdapter.notifyItemRemoved(position);
    }
}