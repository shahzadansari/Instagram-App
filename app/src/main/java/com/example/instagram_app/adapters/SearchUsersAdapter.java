package com.example.instagram_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.model.UserSettings;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersAdapter extends ListAdapter<UserSettings, SearchUsersAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClickListener listener;

    private static final DiffUtil.ItemCallback<UserSettings> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserSettings>() {
        @Override
        public boolean areItemsTheSame(UserSettings oldItem, UserSettings newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(UserSettings oldItem, @NonNull UserSettings newItem) {
            return oldItem.getUser().getUser_id().equals(newItem.getUser().getUser_id());
        }
    };

    public SearchUsersAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_search_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserSettings userSettings = getItem(position);
        User user = userSettings.getUser();
        UserAccountSettings userAccountSettings = userSettings.getSettings();

        Glide.with(mContext)
                .load(userAccountSettings.getProfile_photo())
                .into(holder.imageViewProfilePhoto);

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewDisplayName.setText(userAccountSettings.getDisplay_name());
    }

    // TODO: add a field "commentId" in Comment model, to delete comments with.
    public UserSettings getUserSettingsAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageViewProfilePhoto;
        TextView textViewUsername;
        TextView textViewDisplayName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfilePhoto = itemView.findViewById(R.id.search_result_image);
            textViewUsername = itemView.findViewById(R.id.search_result_username);
            textViewDisplayName = itemView.findViewById(R.id.search_result_display_name);

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
