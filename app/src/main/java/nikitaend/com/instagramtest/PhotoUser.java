package nikitaend.com.instagramtest;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;

/**
 * @author Endaltsev Nikita
 *         start at 21.09.15.
 */
public class PhotoUser implements ParentListItem {
    @Override
    public ArrayList<?> getChildItemList() {
        ArrayList<ArrayList<Like>> likes2Array = new ArrayList<>();
        likes2Array.add(likes.likes);
        return likes2Array;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }


    public class Likes {
        int likesCount;
        ArrayList<Like> likes;

        public Likes(int likesCount, ArrayList<Like> likes) {
            this.likesCount = likesCount;
            this.likes = likes;
        }
    }

    public class Like {
        String username;
        String profile_picture;
        String id;

        public Like(String username, String profile_picture, String id) {
            this.username = username;
            this.profile_picture = profile_picture;
            this.id = id;
        }
    }

    public class Comments {
        int commentsCount;
        ArrayList<Comment> comments;

        public Comments(int commentsCount, ArrayList<Comment> comments) {
            this.commentsCount = commentsCount;
            this.comments = comments;
        }
    }

    public class Comment {
        String nameComment;
        String timeCreated;
        String textComment;
        String profilePicture;

        public Comment(String nameComment, String profilePicture, String timeCreated, String textComment) {
            this.nameComment = nameComment;
            this.textComment = textComment;
            this.timeCreated = timeCreated;
            this.profilePicture = profilePicture;

        }
    }

    public class Image {
        String lowResolutionImage;
        String thumbnail;
        String standardResolution;

        public Image(String lowResolutionImage, String thumbnail, String standardResolution) {
            this.lowResolutionImage = lowResolutionImage;
            this.thumbnail = thumbnail;
            this.standardResolution = standardResolution;
        }
    }

    public class User {
        String name;
        String id;
        String text;
        String profilePicture;
        String createdTime;

        public User(String name, String profilePicture, String id, String createdTime, String text) {
            this.name = name;
            this.id = id;
            this.profilePicture = profilePicture;
            this.createdTime = createdTime;
            this.text = text;

        }
    }


    public boolean isLiked = false;
    public Comments comments;
    public Likes likes;
    public Image image;
    public User user;

    public PhotoUser(User user, Likes likes, Comments comments, Image image) {
        this.user = user;
        this.likes = likes;
        this.comments = comments;
        this.image = image;
    }

    public PhotoUser() {}
}
