package nikitaend.com.instagramtest;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import nikitaend.com.instagramtest.helpers.StringHelper;
import nikitaend.com.instagramtest.requests.VolleyInstaFeedRequest;
import nikitaend.com.instagramtest.requests.VolleyInstaPostRequests;

/**
 * @author Endaltsev Nikita
 *         start at 19.09.15.
 */
public class FeedAdapter extends ExpandableRecyclerAdapter<FeedAdapter.ParentHolder, FeedAdapter.ChildHolder>  {

    Context mContext;
    ArrayList<PhotoUser> photoUsers;
    LayoutInflater mInflater;
    InstagramAuth mApp;
    HorizontalAdapter horizontalAdapter;

    private boolean likersPressed = false;

    public FeedAdapter(Context context, ArrayList<PhotoUser> photoUsers, InstagramAuth app) {
        super(photoUsers);


        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.photoUsers = photoUsers;
        this.mApp = app;


    }

    @Override
    public ParentHolder onCreateParentViewHolder(ViewGroup parent) {
        View v = mInflater.inflate(R.layout.view_card, parent, false);
        return new ParentHolder(v);
    }

    @Override
    public ChildHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View list = mInflater.inflate(R.layout.view_child_info_card, viewGroup, false);
        return new ChildHolder(list);
    }



    @Override
    public void onBindParentViewHolder(final ParentHolder holder,
                                       int position, ParentListItem parentListItem) {
//        PhotoUser photoUser = photoUsers.get(position);
        final PhotoUser photoUser = (PhotoUser) parentListItem;


        if (photoUser.user != null) {
            holder.userName.setText(photoUser.user.name);

            if (photoUser.user.createdTime != "") {
                holder.whenPhoto.setText(StringHelper.getTimeAgo(photoUser.user.createdTime));
            }

            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();


            Picasso.with(mContext).load(photoUser.user.profilePicture)
                    .fit()
                    .transform(transformation)
                    .error(R.drawable.empty_grey)
                    .into(holder.userPhoto);

        }

        if (photoUser.image != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            Picasso.with(mContext).load(photoUser.image.standardResolution)
                    .resize(display.getWidth(), display.getWidth())
                    .into(holder.feedPhoto);

        }

        if (photoUser.likes != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(photoUser.likes.likesCount);
//            holder.likesCounter.setCurrentText(stringBuilder.toString());
            holder.likersButton.setText(mContext.getString(R.string.likeBtn) + " " + photoUser.likes.likesCount);
        }

        if (photoUser.comments != null) {
            if (photoUser.comments.commentsCount != 0) {
                holder.commentButton
                        .setText(mContext.getString(R.string.commentBtn) + " " + photoUser.comments.commentsCount);
            } else {
                holder.commentButton
                        .setText(mContext.getString(R.string.commentBtn));
            }
        }


//        if (holder.likeBtn.getDrawable().equals(mContext.getDrawable(R.drawable.ic_heart_red))) {
//            VolleyInstaPostRequests.configureLikePostRequest(mContext, photoUser.user.id, mApp.getAccessToken());
//        }

        if (photoUser.isLiked) {
            holder.likeBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_heart_red));
        } else {
            holder.likeBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_heart_outline_grey));
        }
//                VolleyInstaFeedRequest.configureLikesId(mContext, photoUser, photoUser.user.id);
//                horizontalAdapter = new HorizontalAdapter(mContext, R.layout.view_horizontal_item, photoUser.likes.likes);



        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (holder.likeBtn.getDrawable().equals(mContext.getResources().getDrawable(R.drawable.ic_heart_outline_grey))) {
                if (!photoUser.isLiked) {
                    holder.likeBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_heart_red));
                    photoUser.isLiked = true;
                    VolleyInstaPostRequests.configureLikePostRequest(mContext, photoUser.user.id, mApp.getAccessToken());
                } else {
                    photoUser.isLiked = false;
                    holder.likeBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_heart_outline_grey));
                }
            }
        });

//        holder.likersButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.isExpanded()) {
//                    VolleyInstaFeedRequest.configureLikesId(mContext, holder, photoUser, photoUser.user.id);
//                } else {
//                }
//            }
//        });

    }

    @Override
    public void onBindChildViewHolder(ChildHolder childHolder, int i, Object childListItem) {
        ArrayList<PhotoUser.Like> likers = (ArrayList<PhotoUser.Like>) childListItem;

//        if (horizontalAdapter == null ) {
            horizontalAdapter = new HorizontalAdapter(mContext, R.layout.view_horizontal_item, likers);
//        }
        childHolder.horizontalListView.setAdapter(horizontalAdapter);
    }

    class ParentHolder extends ParentViewHolder implements VolleyInstaFeedRequest.InstaFeedLikeRequestListener{
        ImageView userPhoto;
        TextView userName;
        TextSwitcher likesCounter;
        TextView commentsText;
        ImageView feedPhoto;
        Button likersButton;
        Button commentButton;
        RelativeLayout imageCoordLayout;
        LinearLayout downMenuLayout;
        ImageButton likeBtn;
        TextView whenPhoto;


        public ParentHolder(View itemView) {
            super(itemView);

            this.userPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
            this.userName = (TextView) itemView.findViewById(R.id.user_name);
//            this.likesCounter = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
            this.likeBtn = (ImageButton) itemView.findViewById(R.id.button_like);

            this.commentsText = (TextView) itemView.findViewById(R.id.viewComments);
            this.feedPhoto = (ImageView) itemView.findViewById(R.id.ivFeedCenter);
            this.imageCoordLayout = (RelativeLayout) itemView.findViewById(R.id.insta_image);
            this.whenPhoto = (TextView) itemView.findViewById(R.id.when_was_photo_done);
            this.likersButton = (Button) itemView.findViewById(R.id.likersButton);
            this.downMenuLayout = (LinearLayout) itemView.findViewById(R.id.downMenuLayout);
            this.commentButton = (Button) itemView.findViewById(R.id.commentBtn);
            this.likersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded()) {
                        collapseView();
                        likersPressed = false;
                        downMenuLayout.setBackground(mContext.getDrawable(R.drawable.border_shadow_bottom));
                        downMenuLayout.setBottom(4);
                    } else {
                        expandView();
                        likersPressed = true;
                        downMenuLayout.setBackground(mContext.getDrawable(R.drawable.border_lr));
                        downMenuLayout.setBottom(0);
                    }
                }
            });

        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }

        @Override
        public void onSuccess(PhotoUser.Likes likes) {

        }
    }

    class ChildHolder extends ChildViewHolder {

        HorizontalListView horizontalListView;
//        ImageView imageView;
        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this ViewHolder
         */
        public ChildHolder(View itemView) {
            super(itemView);

            this.horizontalListView = (HorizontalListView) itemView.findViewById(R.id.horizontalListView);
        }

    }

    public class HorizontalAdapter extends ArrayAdapter<PhotoUser.Like> {
        private ArrayList<PhotoUser.Like> likers;
        private Context mContext;

        public HorizontalAdapter(Context context, int resource,
                              ArrayList<PhotoUser.Like> objects) {
            super(context, R.layout.view_horizontal_item, objects);

            this.likers = objects;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.view_horizontal_item, parent, false);

            PhotoUser.Like liker = getItem(position);
            ImageView itemImage = (ImageView) convertView.findViewById(R.id.itemImageView);

            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();


            Picasso.with(mContext).load(liker.profile_picture)
                    .transform(transformation)
                    .error(R.drawable.empty_grey)
                    .into(itemImage);

            TextView username = (TextView) convertView.findViewById(R.id.itemUsername);
            username.setText(liker.username);
//
            return convertView;
        }
    }


}
