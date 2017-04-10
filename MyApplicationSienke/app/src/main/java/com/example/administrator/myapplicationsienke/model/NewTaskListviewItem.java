package com.example.administrator.myapplicationsienke.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/4/5.
 */
public class NewTaskListviewItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(number);
        parcel.writeString(phoneNumber);
        parcel.writeString(userId);
        parcel.writeString(adress);
    }
    public static final Parcelable.Creator<NewTaskListviewItem>CREATOR = new Parcelable.Creator<NewTaskListviewItem>(){
        @Override
        public NewTaskListviewItem createFromParcel(Parcel parcel) {
            NewTaskListviewItem item = new NewTaskListviewItem();
            item.userName = parcel.readString();
            item.number = parcel.readString();
            item.phoneNumber = parcel.readString();
            item.userId = parcel.readString();
            item.adress = parcel.readString();
            return item;
        }

        @Override
        public NewTaskListviewItem[] newArray(int i) {
            return new NewTaskListviewItem[i];
        }
    };
}

