package com.inik.neonadeuri.models;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.inik.neonadeuri.HomeActivity;
import com.inik.neonadeuri.SplashActivity;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.VolleyManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Feed  implements Serializable {

    private String idx;

    private User writer;
    private Date reportingDate;

    private Photo photo;

    private String feedText;
    private String feedTag;

    private ArrayList<User> likedPeople;
    private ArrayList<Comment> comments;

    public void print() {
        String info = new String();

        info += "사용자 식별 고유 번호 : ";
        info += (idx != null ? idx : "NULL");
        info += "\n";

        info += "작성자               : ";
        info += (writer != null ? writer.getIdx() : "NULL");
        info += "\n";

        info += "작성자               : ";
        info += (writer != null ? writer.getEmail() : "NULL");
        info += "\n";

        System.out.println("----------------피드 정보 임시 출력--------------");
        System.out.println(info);
        System.out.println("-----------------------------------------------");
    }

    public Feed() {
        idx = new String();
        photo = new Photo();
        likedPeople = new ArrayList<>();
        comments = new ArrayList<>();

        this.reportingDate = new Date();
    }

    public Feed(String idx) {
        this.idx = idx;

        photo = new Photo();
        likedPeople = new ArrayList<>();
        comments = new ArrayList<>();

        this.reportingDate = new Date();
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public Date getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(Date reportingDate) {
        this.reportingDate = reportingDate;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getFeedText() {
        return feedText;
    }

    public void setFeedText(String feedText) {
        this.feedText = feedText;
    }

    public String getFeedTag() {
        return feedTag;
    }

    public void setFeedTag(String feedTag) {
        this.feedTag = feedTag;
    }

    public void addLike(User user) {
        likedPeople.add(user);
    }

    public void removeLike(User user) {
        likedPeople.remove(user);
    }

    public ArrayList<User> getLikedPeople() {
        return likedPeople;
    }

    public void setLikedPeople(ArrayList<User> likedPeople) {
        this.likedPeople = likedPeople;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setlikes(User user) {
        for (int i = 0; i < likedPeople.size(); i++) {
            if (likedPeople.get(i).getIdx().equals(user.getIdx())) {
                likedPeople.set(i, user);
            }
        }
    }

    public boolean checkUserInLikePeople(User user) {
        for (int i = 0 ; i < likedPeople.size(); i++) {
            if(likedPeople.get(i).getIdx().equals(user.getIdx())) {
                return true;
            }
        }

        return false;
    }

    public void setComment(Comment comment) {
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getIdx().equals(comment.getIdx())) {
                comments.set(i, comment);
            }
        }
    }

    public String getDateString() {
        long calculateDate = System.currentTimeMillis() - reportingDate.getTime();
        long calculateDateDays;

        if (calculateDate > 86400000) {
            calculateDateDays = calculateDate / (24 * 60 * 60 * 1000);
            return Long.toString(calculateDateDays) + " 일 전";
        } else if (calculateDate > 3600000) {
            calculateDateDays = calculateDate / (60 * 60 * 1000);
            return Long.toString(calculateDateDays) + " 시간 전";
        } else {
            calculateDateDays = calculateDate / (60 * 1000);
            if (calculateDateDays == 0) {
                return "방금 전";
            } else {
                return Long.toString(calculateDateDays) + " 분 전";
            }
        }
    }

}
