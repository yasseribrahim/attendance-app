package com.student.attendance.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.student.attendance.app.R;
import com.student.attendance.app.models.Lecture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.ViewHolder> {
    private List<Lecture> lectures;
    private OnItemClickListener listener;
    private String userId;
    private SimpleDateFormat format;

    // data is passed into the constructor
    public LecturesAdapter(List<Lecture> lectures, String userId, OnItemClickListener listener) {
        this.lectures = lectures;
        this.listener = listener;
        this.userId = userId;
        format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    }

    // inflates the row layout from xml when needed
    @Override
    public LecturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false);
        return new LecturesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LecturesAdapter.ViewHolder holder, int position) {
        Lecture lecture = lectures.get(position);
        holder.lecture = lecture;
        holder.prepare();
    }

    private int getSize(String id) {
        return lectures.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return lectures.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView students;
        TextView attendance;

        Lecture lecture;

        ViewHolder(View view) {
            super(view);
            students = view.findViewById(R.id.students);
            attendance = view.findViewById(R.id.attendance);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
        }

        public void prepare() {
            name.setText(lecture.getName());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lecture.getDate().getTime());
            date.setText(format.format(calendar.getTime()));
            if (userId != null) {
                students.setVisibility(View.GONE);
                attendance.setVisibility(View.VISIBLE);
                if (lecture.getStudents() != null && lecture.getStudents().contains(userId)) {
                    attendance.setText(R.string.str_attended);
                    attendance.setTextColor(ResourcesCompat.getColor(attendance.getResources(), R.color.green, null));
                } else {
                    attendance.setText(R.string.str_absent);
                    attendance.setTextColor(ResourcesCompat.getColor(attendance.getResources(), R.color.red, null));
                }
            } else {
                attendance.setVisibility(View.GONE);
                students.setVisibility(View.VISIBLE);
                int studentCounter = lecture.getStudents() != null ? lecture.getStudents().size() : 0;
                students.setText(students.getContext().getString(R.string.str_students_counter, studentCounter));
                if (studentCounter > 0) {
                    students.setTextColor(ResourcesCompat.getColor(students.getResources(), R.color.green, null));
                } else {
                    students.setTextColor(ResourcesCompat.getColor(students.getResources(), R.color.red, null));
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemViewListener(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);
    }
}