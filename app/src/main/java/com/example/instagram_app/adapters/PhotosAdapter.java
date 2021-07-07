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
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotosAdapter extends ListAdapter<Photo, PhotosAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Photo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(Photo oldItem, @NonNull Photo newItem) {
            return oldItem.getDate_created().equals(newItem.getDate_created());
        }
    };

    private final Context mContext;
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mFirebaseDatabase;
    private final DatabaseReference myRef;
    private Photo currentPhoto;
    private Boolean isLiked = false;
    private UserAccountSettings currentUserData;
    private OnItemClickListener listener;

    public PhotosAdapter(Context context) {
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
                .inflate(R.layout.layout_mainfeed_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentPhoto = getItem(position);
        String userId = currentPhoto.getUser_id();

        Glide.with(mContext)
                .load(currentPhoto.getImage_path())
                .into(holder.postImage);

        holder.textViewCaption.setText(currentPhoto.getCaption());

        String dateToTimeFormat = Utils.DateToTimeFormat(currentPhoto.getDate_created());
        holder.textViewTimePosted.setText(dateToTimeFormat);

        myRef.child("user_account_settings")
                .child(userId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    UserAccountSettings userAccountSettings = dataSnapshot
                            .getValue(UserAccountSettings.class);

                    String profilePhotoUrl = userAccountSettings
                            .getProfile_photo();

                    String username = userAccountSettings
                            .getUsername();

                    holder.authorUsername = username;
                    holder.authorProfilePictureUrl = profilePhotoUrl;

                    holder.textViewUsername.setText(username);

                    Glide.with(mContext)
                            .load(profilePhotoUrl)
                            .into(holder.profilePhoto);
                });

        holder.imageViewComments.setOnClickListener(v -> {
            listener.onItemClick(currentPhoto, v,
                    holder.authorUsername,
                    holder.authorProfilePictureUrl);
        });

        holder.textViewCommentsLink.setOnClickListener(v -> {
            listener.onItemClick(currentPhoto, v,
                    holder.authorUsername,
                    holder.authorProfilePictureUrl);
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getCurrentUserData(snapshot);
                getComments(snapshot, holder);
                getLikes(snapshot, holder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.imageViewLike.setOnClickListener(v -> {
            if (isLiked) {

                holder.imageViewLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_unchecked));
                myRef.child("photos")
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();

                myRef.child("user_photos")
                        .child(currentPhoto.getUser_id())
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
                isLiked = false;

            } else {

                holder.imageViewLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_checked));

                myRef.child("photos")
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mAuth.getUid());

                myRef.child("user_photos")
                        .child(currentPhoto.getUser_id())
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mAuth.getUid());
                isLiked = true;

            }
        });
    }

    public Photo getPhotoAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Photo photo,
                         View v,
                         String authorUsername,
                         String authorProfilePhotoUrl);
    }

    private void getComments(DataSnapshot snapshot, ViewHolder holder) {

        long totalComments = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("comments")
                .getChildrenCount();

        if (totalComments > 1) {
            holder.textViewCommentsLink.setText("View all " + totalComments + " comments");
        } else {
            holder.textViewCommentsLink.setText("View all comments");
        }
    }

    private void getLikes(DataSnapshot snapshot, ViewHolder holder) {

        StringBuilder mUsers = new StringBuilder();

        long totalLikes = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("likes")
                .getChildrenCount();

        if (totalLikes > 0) {
            for (DataSnapshot userIdsSnapshot : snapshot
                    .child("photos")
                    .child(currentPhoto.getPhoto_id())
                    .child("likes")
                    .getChildren()) {

                String user_id = (String) userIdsSnapshot.getValue();

                User user = snapshot
                        .child("users") // users node
                        .child(user_id) // user_id
                        .getValue(User.class); // data from that node

                mUsers.append(user.getUsername());
                mUsers.append(",");
                String likesString = createLikesString(mUsers);
                holder.textViewLikes.setText(likesString);

                /** fix "match found" bug e.g. shahzad & shahzadansari06 etc */
                isLiked = mUsers.toString()
                        .contains(currentUserData.getUsername() + "");

                if (isLiked) {
                    holder.imageViewLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_checked));
                } else {
                    holder.imageViewLike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_unchecked));
                }
            }
        } else {
            String likesString = createLikesString(null);
            holder.textViewLikes.setText(likesString);
        }
    }

    private String createLikesString(StringBuilder usersStringBuilder) {
        if (usersStringBuilder == null)
            return "No likes yet";

        String likesString;

        String[] splitUsers = usersStringBuilder.toString().split(",");
        int length = splitUsers.length;

        if (length == 1) {
            likesString = "Liked by " + splitUsers[0];
        } else if (length == 2) {
            likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];
        } else if (length == 3) {
            likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + splitUsers[2];
        } else if (length >= 4) {
            likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + (length - 2) + " others.";
        } else {
            likesString = "No likes yet";
        }

        return likesString;
    }

    public void getCurrentUserData(DataSnapshot snapshot) {
        currentUserData = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(mAuth.getUid()) // user_id
                .getValue(UserAccountSettings.class); // data from that node
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private String authorUsername;
        private String authorProfilePictureUrl;

        private CircleImageView profilePhoto;
        private ImageView postImage, imageViewEllipses,
                imageViewLike, imageViewComments;
        private TextView textViewUsername, textViewLikes, textViewCaption,
                textViewCommentsLink, textViewTimePosted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            imageViewEllipses = itemView.findViewById(R.id.image_view_ellipses);
            postImage = itemView.findViewById(R.id.post_image);
            imageViewLike = itemView.findViewById(R.id.image_view_heart_unchecked);
            imageViewComments = itemView.findViewById(R.id.image_view_comments);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            textViewLikes = itemView.findViewById(R.id.text_view_likes);
            textViewCaption = itemView.findViewById(R.id.text_view_image_caption);
            textViewCommentsLink = itemView.findViewById(R.id.text_view_image_comments_link);
            textViewTimePosted = itemView.findViewById(R.id.text_view_time_post_created);

//            textViewCommentsLink.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                Photo photo = getItem(position);
//
//                if (listener != null && position != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(photo, v);
//                }
//            });
//
//            imageViewComments.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                Photo photo = getItem(position);
//
//                if (listener != null && position != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(photo, v);
//                }
//            });
        }
    }
}
