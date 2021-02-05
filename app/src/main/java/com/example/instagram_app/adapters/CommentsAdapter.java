package com.example.instagram_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Comment;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends ListAdapter<Comment, CommentsAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Comment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Comment>() {
        @Override
        public boolean areItemsTheSame(Comment oldItem, Comment newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(Comment oldItem, @NonNull Comment newItem) {
            return oldItem.getDate_created().equals(newItem.getDate_created());
        }
    };
    private final Context mContext;
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mFirebaseDatabase;
    private final DatabaseReference myRef;

    public CommentsAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment currentComment = getItem(position);
        String userId = currentComment.getUser_id();

        if (position == 0) {
            holder.imageViewCommentHeart.setVisibility(View.INVISIBLE);
            holder.textViewCommentLikes.setVisibility(View.INVISIBLE);
            holder.textViewCommentReply.setVisibility(View.INVISIBLE);
        }

        myRef.child("user_account_settings")
                .child(userId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                    holder.textViewUsername.setText(userAccountSettings.getUsername());
                    Glide.with(mContext)
                            .load(userAccountSettings.getProfile_photo())
                            .into(holder.imageViewProfilePhoto);
                });

        holder.textViewComment.setText(currentComment.getComment());
        holder.textViewCommentTimePosted.setText(currentComment.getDate_created());

        String dateToTimeFormat = Utils.DateToTimeFormat(currentComment.getDate_created());
        holder.textViewCommentTimePosted.setText(dateToTimeFormat);
    }

    // TODO: add a field "commentId" in Comment model, to delete comments with.
    public Comment getCommentAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageViewProfilePhoto;
        ImageView imageViewCommentHeart;
        TextView textViewUsername, textViewComment,
                textViewCommentTimePosted, textViewCommentLikes, textViewCommentReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfilePhoto = itemView.findViewById(R.id.image_view_comment_profile_image);
            imageViewCommentHeart = itemView.findViewById(R.id.image_view_comment_heart);
            textViewUsername = itemView.findViewById(R.id.text_view_comment_username);
            textViewComment = itemView.findViewById(R.id.text_view_comment);
            textViewCommentTimePosted = itemView.findViewById(R.id.text_view_comment_time_posted);
            textViewCommentLikes = itemView.findViewById(R.id.text_view_comment_likes);
            textViewCommentReply = itemView.findViewById(R.id.text_view_comment_reply);
        }
    }
}
