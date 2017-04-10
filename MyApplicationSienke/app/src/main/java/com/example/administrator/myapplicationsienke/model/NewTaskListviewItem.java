package com.example.administrator.myapplicationsienke.model;

/**
 * Created by Administrator on 2017/4/5.
 */
public class NewTaskListviewItem {
    private String userName;             //姓名
    private String number;                   //表编号
    private String phoneNumber;             //电话号码
    private String userId;                     //时间
    private String adress;                //地址

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

