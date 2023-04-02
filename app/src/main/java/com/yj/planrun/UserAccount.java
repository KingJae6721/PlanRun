package com.yj.planrun;

/*
    사용자 계정 정보 모델 클레스
 */
public class UserAccount {
    
    private  String idToken;    //Firebase Uid 고유토큰정보
    private String emailId;     //email아이디
    private String password;    //비밀번호  //

    public UserAccount(){}//파이어베이스는 빈 생성자를 지정안하면 데이터베이스에서 오류남

    public String getEmailId(){return emailId;}
    public void setEmailId(String emailId){this.emailId = emailId;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    public String getIdToken(){return idToken;}
    public void setIdToken(String idToken){this.idToken = idToken;}


}
