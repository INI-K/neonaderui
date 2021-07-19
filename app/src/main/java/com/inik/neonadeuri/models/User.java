package com.inik.neonadeuri.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String idx; // 사용자 식별 고유번호
    private String email; // 이메일
    private String password; // 비밀번호 (나중에 삭제할 것)

    private String name; // 이름
    private String nickname; // 사용자 이름
    private String phone; // 전화번호

    private Photo profileImg;
    private String webSite; // 웹사이트
    private String introduction; // 소개

    private ArrayList<User> followers; // 팔로워들의 사용자
    private ArrayList<User> following; // 팔로잉 사용자
    private ArrayList<Feed> feeds; // 작성한 피드


    public User() {
        profileImg = new Photo();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        feeds = new ArrayList<>();
    }

    public User(String idx) {
        this.idx = idx;

        profileImg = new Photo();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        feeds = new ArrayList<>();
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Photo getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(Photo profileImg) {
        this.profileImg = profileImg;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void addFollower(User user) {
        followers.add(user);
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }

    public void addFollowing(User user) {
        following.add(user);
    }

    public ArrayList<User> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
    }

    public void setFeed(Feed feed) {
        for (int i = 0; i < feeds.size(); i++) {
            if (feeds.get(i).getIdx().equals(feed.getIdx())) {
                feeds.set(i, feed);
            }
        }
    }



    public boolean checkUserInFollowing(User user) {
        for (int i = 0 ; i < following.size(); i++) {
            if(following.get(i).getIdx().equals(user.getIdx())) {
                return true;
            }
        }
        return false;
    }

    public void removeFollow(User user) {
        for (int i = 0 ; i < following.size(); i++) {
            if(following.get(i).getIdx().equals(user.getIdx())) {
                following.remove(i).getIdx();
            }
        }
    }

    public void addFeed(Feed feed) {
        feeds.add(0, feed);
    }

    public ArrayList<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(ArrayList<Feed> feeds) {
        this.feeds = feeds;
    }

    // 개발 편의를 위해 테스트 데이터 생성
    public void generateTestingData(int setNumber) {
        switch (setNumber) {
            case 0:
                idx = "0001";
                email = "test0001@email.com";
                password = "IU";
                name = "아이유";
                nickname = "IU (아이유)";
                phone = "010 0000 0000";
                profileImg = new Photo();
                webSite = "www.IU.com";
                introduction = "아이유 같은 여자친구 구합니다.";
                followers = new ArrayList<>();
                following = new ArrayList<>();
                feeds = new ArrayList<>();
                break;
            case 1:
                idx = "0002";
                email = "test0002@email.com";
                password = "INIK";
                name = "INIK";
                nickname = "INIK (이니게이)";
                phone = "010 1111 1111";
                profileImg = new Photo();
                webSite = "www.INIK.com";
                introduction = "연애 마렵다.";
                followers = new ArrayList<>();
                following = new ArrayList<>();
                feeds = new ArrayList<>();
                break;
            case 2:
                idx = "0003";
                email = "test0003@email.com";
                password = "킹수씨";
                name = "킹수씨";
                nickname = "KINGSU (킹수씨)";
                phone = "010 2222 2222";
                profileImg = new Photo();
                webSite = "www.KINGSU.com";
                introduction = "킹수씨가 최고야.";
                followers = new ArrayList<>();
                following = new ArrayList<>();
                feeds = new ArrayList<>();
                break;
            case 3:
                idx = "0004";
                email = "test0004@email.com";
                password = "근형쿤";
                name = "근형쿤";
                nickname = "MOssolADA (근형쿤)";
                phone = "010 3333 3333";
                profileImg = new Photo();
                webSite = "www.MOssolADA.com";
                introduction = "하림씨... 그립읍니다...";
                followers = new ArrayList<>();
                following = new ArrayList<>();
                feeds = new ArrayList<>();
                break;
        }
    }

    public void printInfo() {
        String info = new String();

        info += "사용자 식별 고유 번호 : ";
        info += (idx != null ? idx : "NULL");
        info += "\n";

        info += "이메일               : ";
        info += (email != null ? email : "NULL");
        info += "\n";

        info += "비밀번호             : ";
        info += (password != null ? password : "NULL");
        info += "\n";

        info += "이름                 : ";
        info += (name != null ? name : "NULL");
        info += "\n";

        info += "사용자 이름           : ";
        info += (nickname != null ? nickname : "NULL");
        info += "\n";

        info += "전화번호              : ";
        info += (phone != null ? phone : "NULL");
        info += "\n";

        info += "프로필 이미지 원본     : ";
        info += (profileImg != null ? "설정됨" : "설정 안됨");
        info += "\n";

        info += "프로필 이미지 주소     : ";
        info += (profileImg.getApplicationPath() != null ? profileImg.getApplicationPath() : "NULL");
        info += "\n";

        info += "웹사이트              : ";
        info += (webSite != null ? webSite : "NULL");
        info += "\n";

        info += "소개                  : ";
        info += (introduction != null ? introduction : "NULL");
        info += "\n";

        System.out.println("----------------사용자 정보 출력----------------");
        System.out.println(info);
        System.out.println("-----------------------------------------------");
    }
}

