package com.student.attendance.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.attendance.app.R;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.BitmapHelper;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.UIHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private CircleImageView profileImage;
    private ProgressBar progress;
    private ImageView qr;
    private TextView lblWelcome;
    private TextView username;
    private TextView grade;
    private TextView userType;
    private User user;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);
        profileImage = view.findViewById(R.id.profile_image);
        lblWelcome = view.findViewById(R.id.lbl_welcome);
        qr = view.findViewById(R.id.qr);
        username = view.findViewById(R.id.username);
        grade = view.findViewById(R.id.grade);
        userType = view.findViewById(R.id.user_type);
        progress = view.findViewById(R.id.progress);
        qr.setOnClickListener(this);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                lblWelcome.setText(getString(R.string.str_header_welcome, user.getFullName()));
                username.setText(user.getUsername());
                grade.setText(UIHelper.parseGrade(HomeFragment.this.getContext(), user.getGradeId()));
                grade.setVisibility(user.getUserType() == Constants.USER_TYPE_STUDENT ? View.VISIBLE : View.GONE);
                userType.setText(UIHelper.parseUserType(getContext(), user.getUserType()));
                progress.setVisibility(View.GONE);
                try {
                    qr.setImageBitmap(BitmapHelper.generateQRCode(user.getId()));
                } catch (Exception ex) {
                    Toast.makeText(HomeFragment.this.getContext(), "General Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                Glide.with(HomeFragment.this.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userReference != null && valueEventListenerUser != null) {
            userReference.removeEventListener(valueEventListenerUser);
        }
        valueEventListenerUser = null;
        userReference = null;
        database = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qr:
                break;
        }
    }
}
