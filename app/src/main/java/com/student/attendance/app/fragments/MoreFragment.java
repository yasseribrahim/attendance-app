package com.student.attendance.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.student.attendance.app.CustomApplication;
import com.student.attendance.app.R;
import com.student.attendance.app.activities.SplashActivity;
import com.student.attendance.app.activities.users.admin.UserActivity;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.Constants;
import com.student.attendance.app.utils.StorageHelper;
import com.student.attendance.app.utils.UIHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private CircleImageView profileImage;
    private TextView logout;
    private TextView username;
    private TextView userType;
    private TextView changeImageProfile;
    private TextView changeLanguage;
    private TextView btnEdit;
    private User user;

    public static MoreFragment newInstance() {
        Bundle args = new Bundle();

        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + StorageHelper.getCurrentUser().getId();
        userReference = database.getReference(userPath);
        profileImage = view.findViewById(R.id.profile_image);
        changeImageProfile = view.findViewById(R.id.change_image_profile);
        changeLanguage = view.findViewById(R.id.change_language);
        changeImageProfile.setOnClickListener(this);
        changeLanguage.setOnClickListener(this);
        btnEdit = view.findViewById(R.id.edit);
        btnEdit.setOnClickListener(this);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                StorageHelper.clearCurrentUser();
                startActivity(new Intent(MoreFragment.this.getContext(), SplashActivity.class));
                MoreFragment.this.getActivity().finishAffinity();
            }
        });
        username = view.findViewById(R.id.username);
        userType = view.findViewById(R.id.user_type);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                userType.setText(UIHelper.parseUserType(getContext(), user.getUserType()));

                Glide.with(MoreFragment.this.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = result.getUri();
                upload(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
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
            case R.id.change_image_profile:
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
                break;
            case R.id.change_language:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CustomApplication.getApplication());
                try {
                    String language = preferences.getString("language", Locale.getDefault().getLanguage());
                    if ("en".equalsIgnoreCase(language)) {
                        language = "ar";
                    } else {
                        language = "en";
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("language", language);
                    editor.apply();
                    startActivity(new Intent(getContext(), SplashActivity.class));
                    getActivity().finishAffinity();
                } catch (Exception ex) {
                }
                break;
            case R.id.edit:
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("object", user);
                intent.putExtra(Constants.ARG_ID, Constants.USER_TYPE_STUDENT);
                startActivity(intent);
        }
    }

    private void upload(Uri uri) {
        ProgressDialogFragment.show(getChildFragmentManager());
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(Constants.NODE_NAME_IMAGES + "/" + StorageHelper.getCurrentUser().getId());
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MoreFragment.this.getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;

                        user.setImageProfile(downloadUrl.toString());
                        userReference.setValue(user);
                        ProgressDialogFragment.hide(getChildFragmentManager());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MoreFragment.this.getContext(), "Upload Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressDialogFragment.hide(getChildFragmentManager());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.i("MoreFragment", "Uploaded  " + (int) progress + "%");
            }
        });
    }
}
