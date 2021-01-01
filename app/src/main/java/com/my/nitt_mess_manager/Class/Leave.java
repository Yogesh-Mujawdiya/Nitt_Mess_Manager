package com.my.nitt_mess_manager.Class;

public class Leave {
    String Id, From, To, Reason, RollNo, MessId, Status;

    public Leave(String from, String to, String reason, String rollNo, String messId, String status) {
        From = from;
        To = to;
        Reason = reason;
        RollNo = rollNo;
        MessId = messId;
        Status = status;
    }

    public Leave(String id, String from, String to, String reason, String rollNo, String messId, String status) {
        Id = id;
        From = from;
        To = to;
        Reason = reason;
        RollNo = rollNo;
        MessId = messId;
        Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }

    public String getMessId() {
        return MessId;
    }

    public void setMessId(String messId) {
        MessId = messId;
    }
}
