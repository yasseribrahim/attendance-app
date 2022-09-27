package com.student.attendance.app.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.student.attendance.app.R;
import com.student.attendance.app.models.User;

import java.util.List;

public class UsersSelectorAdapter extends RecyclerView.Adapter<UsersSelectorAdapter.ViewHolder> {
    private List<User> users;
    private List<User> selectedUsers;
    private OnItemClickListener listener;
    private final boolean canEdit;

    // data is passed into the constructor
    public UsersSelectorAdapter(List<User> users, List<User> selectedUsers, boolean canEdit, OnItemClickListener listener) {
        this.users = users;
        this.selectedUsers = selectedUsers;
        this.listener = listener;
        this.canEdit = canEdit;
    }

    // inflates the row layout from xml when needed
    @Override
    public UsersSelectorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_selector, parent, false);
        return new UsersSelectorAdapter.ViewHolder(view);
    }

    private boolean isSelected(User user) {
        return selectedUsers.contains(user);
    }

    private void handleSelected(User user) {
        if (isSelected(user)) {
            selectedUsers.remove(user);
        } else {
            selectedUsers.add(user);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(UsersSelectorAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        holder.user = user;
        holder.name.setText(user.getFullName());
        holder.username.setText(user.getUsername());
        holder.prepare();
        Glide.with(holder.itemView.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(holder.image);
    }

    private int getSize(String id) {
        return users.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return users.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView username;
        ImageView check;

        User user;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            check = view.findViewById(R.id.check);
            name = view.findViewById(R.id.name);
            username = view.findViewById(R.id.username);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
        }

        public void prepare() {
            if (canEdit) {
                check.setVisibility(View.VISIBLE);
                if (isSelected(user)) {
                    name.setTypeface(name.getTypeface(), Typeface.BOLD);
                    username.setTypeface(username.getTypeface(), Typeface.BOLD);
                    check.setImageDrawable(ResourcesCompat.getDrawable(check.getResources(), R.drawable.ic_check_box_active, null));
                } else {
                    name.setTypeface(name.getTypeface(), Typeface.NORMAL);
                    username.setTypeface(username.getTypeface(), Typeface.NORMAL);
                    check.setImageDrawable(ResourcesCompat.getDrawable(check.getResources(), R.drawable.ic_check_box_inactive, null));
                }
            } else {
                check.setVisibility(View.GONE);
                name.setTypeface(name.getTypeface(), Typeface.BOLD);
                username.setTypeface(username.getTypeface(), Typeface.BOLD);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSelected(user);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);
    }
}