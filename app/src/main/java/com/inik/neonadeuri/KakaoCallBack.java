package com.inik.neonadeuri;

public interface KakaoCallBack {
    public void getKakaoNickName(String nickName);
    public void getKakaoProfileImageUrl(String url);
    public void getKakaoBirthday(String birthDay);
    public void getKakaoEmail(String email);
    public void getKakaoPhonNumber(String phoneNum);
    public void kakaoLoginComplete();
}
