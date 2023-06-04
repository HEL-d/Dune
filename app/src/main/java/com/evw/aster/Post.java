package com.evw.aster;

public class Post {
    public String username;
    public String lastmessage;
    public String roomid;

    public Post(){}

    public Post(String username,String lastmessage,String roomid) {
      this.username = username;
      this.lastmessage = lastmessage;
      this.roomid = roomid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
}
