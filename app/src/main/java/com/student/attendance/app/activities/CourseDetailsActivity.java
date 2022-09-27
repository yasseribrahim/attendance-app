package com.student.attendance.app.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.material.tabs.TabLayout;
import com.student.attendance.app.R;
import com.student.attendance.app.fragments.ProgressDialogFragment;
import com.student.attendance.app.fragments.users.LecturesFragment;
import com.student.attendance.app.fragments.users.StudentsFragment;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.UIHelper;
import com.student.attendance.app.utils.ValueCallback;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsActivity extends BaseActivity implements Listener {
    private Toolbar toolbar;
    private TextView name;
    private ImageView image;
    private TextView grade;
    private TextView lecturer;
    private TextView degreePerLecture;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private EasyWayLocation easyWayLocation;
    private Course course;
    private StringBuilder currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        easyWayLocation = new EasyWayLocation(this, false, false, this);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_course_viewer_title));

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        lecturer = findViewById(R.id.lecturer);
        grade = findViewById(R.id.grade);
        degreePerLecture = findViewById(R.id.degree_per_lecture);
        grade.setEnabled(false);

        course = getIntent().getParcelableExtra("object");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(StudentsFragment.newInstance(course), getString(R.string.str_students));
        adapter.addFragment(LecturesFragment.newInstance(course), getString(R.string.str_lectures));
        viewPager.setAdapter(adapter);
        bindCourse();

        Toast.makeText(this, "Fetch Current Location Start", Toast.LENGTH_SHORT).show();
        easyWayLocation.startLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EasyWayLocation.LOCATION_SETTING_REQUEST_CODE:
                easyWayLocation.onActivityResult(resultCode);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        easyWayLocation.endUpdates();

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

    private void bindCourse() {
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }
        name.setText(course.getName());
        if (course.getGradeId() > 0) {
            grade.setText(UIHelper.parseGrade(this, course.getGradeId()));
        } else {
            grade.setText("");
        }
        lecturer.setText(course.getLectureName());
        degreePerLecture.setText(getString(R.string.str_degree_per_lecture_summary, course.getDegreePerLecture() + ""));
        Glide.with(this).load(course.getImage()).placeholder(R.drawable.ic_default_image_circle).into(image);
    }

    @Override
    public void locationOn() {
        Toast.makeText(this, "Location ON", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentLocation(Location location) {
        currentLocation = new StringBuilder();
        currentLocation.append(location.getLatitude());
        currentLocation.append(",");
        currentLocation.append(location.getLongitude());
        Toast.makeText(this, "Fetch Current Location Done", Toast.LENGTH_SHORT).show();
        easyWayLocation.endUpdates();

        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof ValueCallback) {
                    ((ValueCallback) fragment).onValueCallback(currentLocation.toString());
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void locationCancelled() {
        Toast.makeText(this, "Location Cancelled", Toast.LENGTH_SHORT).show();
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