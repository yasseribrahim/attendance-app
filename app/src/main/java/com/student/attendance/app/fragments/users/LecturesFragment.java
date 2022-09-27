package com.student.attendance.app.fragments.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.student.attendance.app.R;
import com.student.attendance.app.activities.CourseSummaryActivity;
import com.student.attendance.app.activities.QRCodeScannerActivity;
import com.student.attendance.app.activities.users.teacher.LectureActivity;
import com.student.attendance.app.adapters.LecturesAdapter;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.models.Lecture;
import com.student.attendance.app.models.QRLecture;
import com.student.attendance.app.models.User;
import com.student.attendance.app.presenters.LecturesCallback;
import com.student.attendance.app.presenters.LecturesPresenter;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.StorageHelper;
import com.student.attendance.app.utils.ValueCallback;

import java.util.ArrayList;
import java.util.List;

public class LecturesFragment extends Fragment implements ValueCallback, LecturesCallback, LecturesAdapter.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout;
    private TextView lblLectures;
    private TextView btnNewLecture;
    private TextView takeAttendance;
    private TextView btnSummary;
    private TextView message;
    private TextView loading;

    private LecturesPresenter presenter;
    private RecyclerView recyclerView;
    private LecturesAdapter adapter;
    private List<Lecture> lectures;
    private Course course;
    private boolean isLecturerAccount;
    private String currentLocation;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(LecturesFragment.this.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    takeAttendance(result.getContents());
                }
            });

    public static LecturesFragment newInstance(Course course) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT, course);
        LecturesFragment fragment = new LecturesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lectures, container, false);

        course = getArguments().getParcelable(Constants.ARG_OBJECT);
        presenter = new LecturesPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        lblLectures = rootView.findViewById(R.id.lectures);
        btnNewLecture = rootView.findViewById(R.id.new_lecture);
        btnSummary = rootView.findViewById(R.id.summary);
        takeAttendance = rootView.findViewById(R.id.take_attendance);
        loading = rootView.findViewById(R.id.loading);
        message = rootView.findViewById(R.id.message);

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        String userId = null;
        User user = StorageHelper.getCurrentUser();
        if (user != null && user.getUserType() == Constants.USER_TYPE_STUDENT) {
            userId = user.getId();
        }
        isLecturerAccount = userId == null;
        btnNewLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LecturesFragment.this.getContext(), LectureActivity.class);
                intent.putExtra(Constants.ARG_COURSE_ID, course.getId());
                intent.putExtra(Constants.ARG_GRADE_ID, course.getGradeId());
                intent.putExtra(Constants.ARG_OBJECT, currentLocation);
                startActivity(intent);
            }
        });
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LecturesFragment.this.getContext(), CourseSummaryActivity.class);
                intent.putExtra(Constants.ARG_OBJECT, course);
                startActivity(intent);
            }
        });

        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Scan");
                options.setOrientationLocked(true);
                options.setCameraId(0);  // Use a specific camera of the device
                options.setBeepEnabled(false);
                options.setCaptureActivity(QRCodeScannerActivity.class);
                options.setBarcodeImageEnabled(true);
                barcodeLauncher.launch(options);
            }
        });

        lectures = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LecturesAdapter(lectures, userId, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onValueCallback(String value) {
        this.currentLocation = value;
        loading.setVisibility(View.GONE);
        btnNewLecture.setVisibility(isLecturerAccount ? View.VISIBLE : View.GONE);
        takeAttendance.setVisibility(!isLecturerAccount ? View.VISIBLE : View.GONE);
    }

    private void load() {
        presenter.getLecturesByCourseId(course.getId());
    }

    private void takeAttendance(String content) {
        try {
            QRLecture qr = new Gson().fromJson(content, QRLecture.class);
            if (course.getId().equals(qr.getCourseId())) {
                FirebaseDatabase.getInstance().getReference(Constants.NODE_NAME_LECTURES).child(qr.getCourseId()).child(qr.getLectureId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Lecture lecture = snapshot.getValue(Lecture.class);
                            try {
                                String[] values1 = lecture.getLocation().split(",");
                                double latitude1 = Double.parseDouble(values1[0]);
                                double longitude1 = Double.parseDouble(values1[1]);
                                String[] values2 = currentLocation.split(",");
                                double latitude2 = Double.parseDouble(values2[0]);
                                double longitude2 = Double.parseDouble(values2[1]);
                                double distance = distance(latitude1, longitude1, latitude2, longitude2, 'M');

                                if (distance < 20) {
                                    if (lecture.getStudents() == null) {
                                        lecture.setStudents(new ArrayList<>());
                                    }

                                    User user = StorageHelper.getCurrentUser();
                                    if (!lecture.getStudents().contains(user.getId())) {
                                        lecture.getStudents().add(user.getId());
                                        snapshot.getRef().setValue(lecture);
                                        load();
                                        Toast.makeText(getContext(), R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Attendance already taked", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Your device is too far", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception ex) {
                                Toast.makeText(getContext(), "General error while extract QR", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Sorry: This Lecture Not Found.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(getContext(), "Error: QR Code not matched with current Course", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error: " + ex, Toast.LENGTH_LONG).show();
        }
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
    public void onGetLecturesComplete(List<Lecture> lectures) {
        this.lectures.clear();
        this.lectures.addAll(lectures);
        lblLectures.setText(getString(R.string.str_lectures_counter, lectures.size()));
        message.setVisibility(this.lectures.isEmpty() ? View.VISIBLE : View.GONE);
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
        if (isLecturerAccount) {
            Intent intent = new Intent(LecturesFragment.this.getContext(), LectureActivity.class);
            intent.putExtra(Constants.ARG_COURSE_ID, course.getId());
            intent.putExtra(Constants.ARG_GRADE_ID, course.getGradeId());
            intent.putExtra(Constants.ARG_OBJECT, lectures.get(position));
            startActivity(intent);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}