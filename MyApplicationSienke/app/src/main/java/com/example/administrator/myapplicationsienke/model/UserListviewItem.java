package com.example.administrator.myapplicationsienke.model;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UserListviewItem {
    private String userName;             //姓名
    private int number;                   //表编号
    private int phoneNumber;             //电话号码
    private String securityType;         //安检类型
    private String userId;                     //时间
    private String adress;                //地址

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
