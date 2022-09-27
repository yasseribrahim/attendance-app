package com.student.attendance.app.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.attendance.app.CustomApplication;
import com.student.attendance.app.R;
import com.student.attendance.app.models.About;
import com.student.attendance.app.utils.Constants;

import java.util.Locale;

public class AboutFragment extends Fragment {
    private TextView content;
    private TextView conditions;
    private TextView objectives;

    private DatabaseReference reference;
    private ValueEventListener valueEventListenerUser;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        content = rootView.findViewById(R.id.content);
        conditions = rootView.findViewById(R.id.conditions);
        objectives = rootView.findViewById(R.id.objectives);

        reference = FirebaseDatabase.getInstance().getReference(Constants.NODE_NAME_ABOUT + "/" + PreferenceManager.getDefaultSharedPreferences(CustomApplication.getApplication()).getString("language", Locale.getDefault().getLanguage()));
        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                About about = snapshot.getValue(About.class);
                objectives.setText(about.getObjectives());
                conditions.setText(about.getConditions());
                content.setText(about.getContent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(valueEventListenerUser);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reference != null && valueEventListenerUser != null) {
            reference.removeEventListener(valueEventListenerUser);
        }
        valueEventListenerUser = null;
        reference = null;
    }
}
