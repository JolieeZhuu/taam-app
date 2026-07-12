package com.example.cscb07project;

public class AdminLogs {
    private String username;
    private String action;
    private String target;
    private String reason;
    private long time;

    public AdminLogs(){}

    AdminLogs(String username, String action, String target, String reason,long time){
        this.username=username;
        this.action=action;
        this.target=target;
        this.reason=reason;
        this.time=time;
    }

    String getUsername(){return username;}
    String getAction(){return action;}
    String getReason(){return reason;}
    long getTime(){return time;}
    String getTarget(){return target;}

}
