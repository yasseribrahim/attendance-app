package com.student.attendance.app.fragments.users.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.student.attendance.app.R;
import com.student.attendance.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UsersTypesFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;

    public static UsersTypesFragment newInstance() {
        Bundle args = new Bundle();

        UsersTypesFragment fragment = new UsersTypesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_types, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(UsersFragment.newInstance(Constants.USER_TYPE_STUDENT), getString(R.string.str_user_type_student));
        adapter.addFragment(UsersFragment.newInstance(Constants.USER_TYPE_LECTURER), getString(R.string.str_user_type_teacher));
        adapter.addFragment(UsersFragment.newInstance(Constants.USER_TYPE_ADMIN), getString(R.string.str_user_type_admin));
        viewPager.setAdapter(adapter);

        return view;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        // this is a secondary constructor of ViewPagerAdapter class.
        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        // returns which item is selected from arraylist of fragments.
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        // returns which item is selected from arraylist of titles.
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        // returns the number of items present in arraylist.
        @Override
        public int getCount() {
            return fragments.size();
        }

        // this function adds the fragment and title in 2 separate  arraylist.
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
    }
}