package com.student.attendance.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.student.attendance.app.R;
import com.student.attendance.app.models.Course;
import com.student.attendance.app.utils.UIHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    private List<Course> courses;
    private OnItemClickListener listener;

    // data is passed into the constructor
    public CoursesAdapter(List<Course> courses, OnItemClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public CoursesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CoursesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CoursesAdapter.ViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.name.setText(course.getName());
        holder.lecturer.setText(course.getLectureName());
        holder.grade.setText(UIHelper.parseGrade(holder.grade.getContext(), course.getGradeId()));
        int students = 0;
        if (course.getStudents() != null) {
            students = course.getStudents().size();
        }
        holder.students.setText(holder.students.getContext().getString(R.string.str_students_counter, students));
        Glide.with(holder.itemView.getContext()).load(course.getImage()).placeholder(R.drawable.ic_default_image).into(holder.image);
    }

    private int getSize(String id) {
        return courses.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return courses.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView grade;
        TextView lecturer;
        TextView students;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            grade = view.findViewById(R.id.grade);
            lecturer = view.findViewById(R.id.lecturer);
            students = view.findViewById(R.id.students);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);
    }
}