package com.student.attendance.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.student.attendance.app.R;
import com.student.attendance.app.models.Summary;

import java.util.List;

public class CourseSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_DETAIL = 2;
    private List<Summary> summaries;
    private boolean forStudent;

    public CourseSummaryAdapter(List<Summary> summaries, boolean forStudent) {
        this.summaries = summaries;
        this.forStudent = forStudent;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        }
        return ITEM_TYPE_DETAIL;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                if (forStudent) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_summary_header_student, parent, false);
                    return new CourseSummaryAdapter.HeaderViewHolder(view);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_summary_header_teacher, parent, false);
                    return new CourseSummaryAdapter.HeaderViewHolder(view);
                }
            case ITEM_TYPE_DETAIL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_summary, parent, false);
                return new CourseSummaryAdapter.DetailViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_summary, parent, false);
        return new CourseSummaryAdapter.DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_DETAIL:
                Summary summary = summaries.get(position);
                DetailViewHolder holder = (DetailViewHolder) viewHolder;
                holder.key.setText(summary.getLectureName());
                if (forStudent) {
                    holder.value.setText(summary.getDegree() + "");
                } else {
                    holder.value.setText(summary.getStudentNumber() + "");
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return summaries.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View view) {
            super(view);
        }
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView key;
        TextView value;

        DetailViewHolder(View view) {
            super(view);
            key = view.findViewById(R.id.key);
            value = view.findViewById(R.id.value);
        }
    }
}