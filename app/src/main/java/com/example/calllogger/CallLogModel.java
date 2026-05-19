package com.example.calllogger;

public class CallLogModel {
    public int id;
    public String contactName;
    public String phoneNumber;
    public String callType;
    public String note;
    public long timestamp;

    public CallLogModel(int id, String contactName, String phoneNumber, String callType, String note, long timestamp) {
        this.id = id;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.note = note;
        this.timestamp = timestamp;
    }
}
