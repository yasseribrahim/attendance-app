package com.student.attendance.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.student.attendance.app.R;
import com.student.attendance.app.models.User;
import com.student.attendance.app.utils.StorageHelper;
import com.student.attendance.app.utils.UIHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> users;
    private OnItemClickListener listener;

    // data is passed into the constructor
    public UsersAdapter(List<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        holder.name.setText(user.getFullName());
        holder.username.setText(user.getUsername());
        holder.userType.setText(UIHelper.parseUserType(holder.userType.getContext(), user.getUserType()));
        holder.phone.setText(user.getPhone());
        holder.btnDelete.setVisibility(StorageHelper.getCurrentUser() != null ? StorageHelper.getCurrentUser().getId().equalsIgnoreCase(user.getId()) ? View.GONE : View.VISIBLE : View.GONE);
        if (user.getAddress() != null) {
            holder.address.setText(user.getAddress());
        } else {
            holder.address.setText("---");
        }
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
        CircleImageView image;
        TextView name;
        TextView username;
        TextView userType;
        TextView phone;
        TextView address;
        TextView btnDelete;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            username = view.findViewById(R.id.username);
            userType = view.findViewById(R.id.user_type);
            phone = view.findViewById(R.id.phone);
            address = view.findViewById(R.id.address);
            btnDelete  = view.findViewById(R.id.btn_delete);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemViewListener(getAdapterPosition());
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onDeleteItemViewListener(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemViewListener(int position);

        void onDeleteItemViewListener(int position);
    }
}