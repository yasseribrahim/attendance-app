package com.student.attendance.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.android.material.tabs.TabLayout;
import com.student.attendance.app.R;
import com.student.attendance.app.fragments.ProgressDialogFragment;
import com.student.attendance.app.fragments.users.LecturesFragment;
import com.student.attendance.app.fragments.users.StudentsFragment;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.utils.LocaleHelper;
import com.student.attendance.app.utils.LocationService;
import com.student.attendance.app.utils.UIHelper;
import com.student.attendance.app.utils.ValueCallback;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsActivity extends BaseActivity implements LocationService.LocationServiceCallback {
    private Toolbar toolbar;
    private TextView name;
    private ImageView image;
    private TextView grade;
    private TextView lecturer;
    private TextView degreePerLecture;
    private ViewPager viewPager;

    private LocationService locationService;
    private Course course;
    private StringBuilder currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        locationService = new LocationService();
        locationService.init(this);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle(getString(R.string.str_course_viewer_title));

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEnableGps();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // locationService.endUpdates();
    }

    private void checkEnableGps() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gpsEnabled && !networkEnabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "Fetch Current Location Start", Toast.LENGTH_SHORT).show();
            locationService.getLocation(this, this);
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
    public void onSuccess(Location location) {
        currentLocation = new StringBuilder();
        currentLocation.append(location.getLatitude());
        currentLocation.append(",");
        currentLocation.append(location.getLongitude());
        Toast.makeText(this, "Fetch Current Location Done", Toast.LENGTH_SHORT).show();

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
    public void onError() {

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