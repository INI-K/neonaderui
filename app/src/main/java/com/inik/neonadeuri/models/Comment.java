package com.inik.neonadeuri.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comment  implements Serializable {

    private String idx;
    private String comment;

    private Date reportingDate;

    private User writer;
    private ArrayList<User> likedPeoples;

    public Comment() {
        this.reportingDate = new Date();

        likedPeoples = new ArrayList<>();
    }

    public Comment(String idx) {
        this.idx = idx;
        this.reportingDate = new Date();

        likedPeoples = new ArrayList<>();
    }

    public Comment(User writer) {
        this.writer = writer;
        this.reportingDate = new Date();

        likedPeoples = new ArrayList<>();
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String commentId) {
        this.idx = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(Date reportingDate) {
        this.reportingDate = reportingDate;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public void addLike(User user) {
        likedPeoples.add(user);
    }

    public ArrayList<User> getLikedPeoples() {
        return likedPeoples;
    }

    public void setLikedPeoples(ArrayList<User> likedPeoples) {
        this.likedPeoples = likedPeoples;
    }

    public String getLikedString() {
        int numOflikedPeople = likedPeoples.size();

        if (numOflikedPeople == 0) {
            return "좋아요 없음";
        } else {
            return Integer.toString(numOflikedPeople) + " 명이 좋아함";
        }

    }

    public boolean checkUserInLikePeople(User user) {
        for (int i = 0 ; i < likedPeoples.size(); i++) {
            if(likedPeoples.get(i).getIdx().equals(user.getIdx())) {
                System.out.println("라이크 아이디 확인 : " + likedPeoples.get(i).getIdx());
                return true;
            }
        }

        return false;
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
