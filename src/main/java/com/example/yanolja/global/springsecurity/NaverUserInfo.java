package com.example.yanolja.global.springsecurity;

import java.util.Map;
/**
 * @author liyusang1
 * @implNote OAuth2 네이버 로그인 후 받아온 값에서 사용자 정보를 저장하기 위한 클래스 입니다.
 * 구글,카카오,페이스북 등 로그인하고자 하는 대상별로 제공하는 세부 값들이 다르기에 따로 클래스를 생성했습니다.
 */
public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getPhoneNumber() {
        return (String) attributes.get("mobile");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email")+"OAuth2";
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}
