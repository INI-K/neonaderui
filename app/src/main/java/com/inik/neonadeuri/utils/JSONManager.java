package com.inik.neonadeuri.utils;

import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSONManager {

    // User -> JSON 변환
    public static JSONObject userToJSONObject(User user) {
        JSONObject userJsonObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("idx", user.getIdx());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("name", user.getName());
            jsonObject.put("nickname", user.getNickname());
            jsonObject.put("phone", user.getPhone());
            jsonObject.put("photoIdx", user.getProfileImg().getIdx());
            jsonObject.put("webSite", user.getWebSite());
            jsonObject.put("introduction", user.getIntroduction());

            JSONArray followersJSONArray = new JSONArray();
            for (int i = 0; i < user.getFollowers().size(); i++)
                followersJSONArray.put(i, user.getFollowers().get(i).getIdx());
            jsonObject.put("followers", followersJSONArray);

            JSONArray followingJSONArray = new JSONArray();
            for (int i = 0; i < user.getFollowing().size(); i++)
                followingJSONArray.put(i, user.getFollowing().get(i).getIdx());
            jsonObject.put("following", followingJSONArray);

            JSONArray feedJSONArray = new JSONArray();
            for (int i = 0; i < user.getFeeds().size(); i++)
                feedJSONArray.put(i, user.getFeeds().get(i).getIdx());
            jsonObject.put("feeds", feedJSONArray);

            userJsonObject.put("User", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userJsonObject;
    }

    // Feed -> JSON 변환
    public static JSONObject feedToJSONObject(Feed feed) {
        JSONObject feedJsonObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("idx", feed.getIdx());
            jsonObject.put("writer", feed.getWriter().getIdx());
            jsonObject.put("reportingDate", new SimpleDateFormat("yyyyMMdd_HHmmss").format(feed.getReportingDate()));
            jsonObject.put("photoIdx", feed.getPhoto().getIdx());
            jsonObject.put("feedText", feed.getFeedText());
            jsonObject.put("feedTag", feed.getFeedTag());

            JSONArray likedPeoplesJSONArray = new JSONArray();
            for (int i = 0; i < feed.getLikedPeople().size(); i++)
                likedPeoplesJSONArray.put(i, feed.getLikedPeople().get(i).getIdx());
            jsonObject.put("likedPeoples", likedPeoplesJSONArray);

            JSONArray commentsJSONArray = new JSONArray();
            for (int i = 0; i < feed.getComments().size(); i++)
                commentsJSONArray.put(i, feed.getComments().get(i).getIdx());
            jsonObject.put("comments", commentsJSONArray);

            feedJsonObject.put("Feed", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("피드 제이슨 확인 :" + feedJsonObject);
        return feedJsonObject;
    }

    // Photo -> JSON 변환
    public static JSONObject photoToJSONObject(Photo photo) {
        JSONObject photoJsonObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("idx", photo.getIdx());
            jsonObject.put("applicationPath", photo.getApplicationPath());
            jsonObject.put("serverPath", photo.getServerPath());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(photoJsonObject);
        return photoJsonObject;
    }

    // Comment -> JSON 변환
    public static JSONObject commentToJSONObject(Comment comment) {
        JSONObject commentJsonObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("idx", comment.getIdx());
            jsonObject.put("writer", comment.getWriter().getIdx());
            jsonObject.put("reportingDate", new SimpleDateFormat("yyyyMMdd_HHmmss").format(comment.getReportingDate()));
            jsonObject.put("comment", comment.getComment());


            JSONArray likedPeoplesJSONArray = new JSONArray();
            for (int i = 0; i < comment.getLikedPeoples().size(); i++)
                likedPeoplesJSONArray.put(i, comment.getLikedPeoples().get(i).getIdx());
            jsonObject.put("likedPeoples", likedPeoplesJSONArray);

            commentJsonObject.put("Comment", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("코멘트 업로드 제이슨 확인 : " + commentJsonObject);
        return commentJsonObject;
    }

    // JSON -> User 변환
    public static User jsonObjectToUser(String json) {
        User user = new User();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("User")) {
                JSONObject userJsonObject = jsonObject.getJSONObject("User");

                if (userJsonObject.has("idx")) {
                    user.setIdx(userJsonObject.getString("idx"));
                } else {
                    user.setIdx("");
                }
                if (userJsonObject.has("email")) {
                    user.setEmail(userJsonObject.getString("email"));
                } else {
                    user.setEmail("");
                }
                if (userJsonObject.has("password")) {
                    user.setPassword(userJsonObject.getString("password"));
                } else {
                    user.setPassword("");
                }
                if (userJsonObject.has("name")) {
                    user.setName(userJsonObject.getString("name"));
                } else {
                    user.setName("");
                }
                if (userJsonObject.has("nickname")) {
                    user.setNickname(userJsonObject.getString("nickname"));
                } else {
                    user.setNickname("");
                }
                if (userJsonObject.has("phone")) {
                    user.setPhone(userJsonObject.getString("phone"));
                } else {
                    user.setPhone("");
                }
                if (userJsonObject.has("webSite")) {
                    user.setWebSite(userJsonObject.getString("webSite"));
                } else {
                    user.setWebSite("");
                }
                if (userJsonObject.has("introduction")) {
                    user.setIntroduction(userJsonObject.getString("introduction"));
                } else {
                    user.setIntroduction("");
                }

                if (userJsonObject.has("photoIdx")) {
                    Photo photo = new Photo(userJsonObject.getString("photoIdx"));
                    user.setProfileImg(photo);
                }

                if (userJsonObject.has("feeds")) {
                    JSONArray feedJSONArray = userJsonObject.getJSONArray("feeds");

                    for (int i = 0; i < feedJSONArray.length(); i++) {
                        String feedIdx = (String) feedJSONArray.get(i);

                        Feed feed = new Feed(feedIdx);
                        user.addFeed(feed);
                    }
                }

                if (userJsonObject.has("followers")) {
                    JSONArray followerJSONArray = userJsonObject.getJSONArray("followers");

                    for (int i = 0; i < followerJSONArray.length(); i++) {
                        String userIdx = (String) followerJSONArray.get(i);

                        User follower = new User(userIdx);
                        user.addFollower(follower);
                    }
                }

                if (userJsonObject.has("following")) {
                    JSONArray followingJSONArray = userJsonObject.getJSONArray("following");

                    for (int i = 0; i < followingJSONArray.length(); i++) {
                        String userIdx = (String) followingJSONArray.get(i);

                        User following = new User(userIdx);
                        user.addFollowing(following);
                    }
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    // JSON -> Feed 변환
    public static Feed jsonObjectToFeed(String json) {
        Feed feed = new Feed();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("Feed")) {
                JSONObject feedJsonObject = jsonObject.getJSONObject("Feed");

                if (feedJsonObject.has("idx")) {
                    feed.setIdx(feedJsonObject.getString("idx"));
                } else {
                    feed.setIdx("");
                }
                if (feedJsonObject.has("feedText")) {
                    feed.setFeedText(feedJsonObject.getString("feedText"));
                } else {
                    feed.setFeedText("");
                }
                if (feedJsonObject.has("feedTag")) {
                    feed.setFeedTag(feedJsonObject.getString("feedTag"));
                } else {
                    feed.setFeedTag("");
                }

                if (feedJsonObject.has("writer")) {
                    User user = new User(feedJsonObject.getString("writer"));
                    feed.setWriter(user);
                }

                if (feedJsonObject.has("reportingDate")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    try {
                        Date date = simpleDateFormat.parse(feedJsonObject.getString("reportingDate"));
                        feed.setReportingDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (feedJsonObject.has("photoIdx")) {
                    Photo photo = new Photo(feedJsonObject.getString("photoIdx"));
                    feed.setPhoto(photo);
                }

                if (feedJsonObject.has("likedPeoples")) {
                    JSONArray likedPeoplesJSONArray = feedJsonObject.getJSONArray("likedPeoples");

                    for (int i = 0; i < likedPeoplesJSONArray.length(); i++) {
                        String userIdx = (String) likedPeoplesJSONArray.get(i);

                        User likedPerson = new User(userIdx);
                        feed.addLike(likedPerson);
                    }
                }

                if (feedJsonObject.has("comments")) {
                    JSONArray commentJSONArray = feedJsonObject.getJSONArray("comments");

                    for (int i = 0; i < commentJSONArray.length(); i++) {
                        String commentIdx = (String) commentJSONArray.get(i);

                        Comment comment = new Comment(commentIdx);
                        feed.addComment(comment);
                    }
                }

            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feed;
    }

    // JSON -> Feed 변환
    public static Feed jsonObjectToFeed(JSONObject jsonObject) {
        Feed feed = new Feed();

        try {
            if (jsonObject.has("Feed")) {
                JSONObject feedJsonObject = jsonObject.getJSONObject("Feed");

                if (feedJsonObject.has("idx")) {
                    feed.setIdx(feedJsonObject.getString("idx"));
                } else {
                    feed.setIdx("");
                }
                if (feedJsonObject.has("feedText")) {
                    feed.setFeedText(feedJsonObject.getString("feedText"));
                } else {
                    feed.setFeedText("");
                }
                if (feedJsonObject.has("feedTag")) {
                    feed.setFeedTag(feedJsonObject.getString("feedTag"));
                } else {
                    feed.setFeedTag("");
                }

                if (feedJsonObject.has("writer")) {
                    User user = new User(feedJsonObject.getString("writer"));
                    feed.setWriter(user);
                }

                if (feedJsonObject.has("reportingDate")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    try {
                        Date date = simpleDateFormat.parse(feedJsonObject.getString("reportingDate"));
                        feed.setReportingDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (feedJsonObject.has("photoIdx")) {
                    Photo photo = new Photo(feedJsonObject.getString("photoIdx"));
                    feed.setPhoto(photo);
                }

                if (feedJsonObject.has("likedPeoples")) {
                    JSONArray likedPeoplesJSONArray = feedJsonObject.getJSONArray("likedPeoples");

                    for (int i = 0; i < likedPeoplesJSONArray.length(); i++) {
                        String userIdx = (String) likedPeoplesJSONArray.get(i);

                        User likedPerson = new User(userIdx);
                        feed.addLike(likedPerson);
                    }
                }

                if (feedJsonObject.has("comments")) {
                    JSONArray commentJSONArray = feedJsonObject.getJSONArray("comments");

                    for (int i = 0; i < commentJSONArray.length(); i++) {
                        String commentIdx = (String) commentJSONArray.get(i);

                        Comment comment = new Comment(commentIdx);
                        feed.addComment(comment);
                    }
                }

            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feed;
    }

    // JSON -> Photo 변환
    public static Photo jsonObjectToPhoto(String json) {
        Photo photo = new Photo();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("Photo")) {
                JSONObject photoJsonObject = jsonObject.getJSONObject("Photo");

                if (photoJsonObject.has("idx")) {
                    photo.setIdx(photoJsonObject.getString("idx"));
                } else {
                    photo.setIdx("");
                }

                if (photoJsonObject.has("applicationPath")) {
                    photo.setApplicationPath(photoJsonObject.getString("applicationPath"));
                } else {
                    photo.setApplicationPath("");
                }

                if (photoJsonObject.has("serverPath")) {
                    photo.setServerPath(photoJsonObject.getString("serverPath"));
                } else {
                    photo.setServerPath("");
                }

            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photo;
    }

    // JSON -> Comment 변환
    public static Comment jsonObjectToComment(String json) {
        Comment comment = new Comment();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("Comment")) {
                JSONObject commentJsonObject = jsonObject.getJSONObject("Comment");

                if (commentJsonObject.has("idx")) {
                    comment.setIdx(commentJsonObject.getString("idx"));
                } else {
                    comment.setIdx("");
                }
                if (commentJsonObject.has("comment")) {
                    comment.setComment(commentJsonObject.getString("comment"));
                } else {
                    comment.setComment("");
                }

                if (commentJsonObject.has("writer")) {
                    User user = new User(commentJsonObject.getString("writer"));
                    comment.setWriter(user);
                }

                if (commentJsonObject.has("reportingDate")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    try {
                        Date date = simpleDateFormat.parse(commentJsonObject.getString("reportingDate"));
                        comment.setReportingDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (commentJsonObject.has("likedPeoples")) {
                    JSONArray likedPeoplesJSONArray = commentJsonObject.getJSONArray("likedPeoples");

                    for (int i = 0; i < likedPeoplesJSONArray.length(); i++) {
                        String userIdx = (String) likedPeoplesJSONArray.get(i);

                        User likedPerson = new User(userIdx);
                        comment.addLike(likedPerson);
                    }
                }

            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comment;
    }
}
