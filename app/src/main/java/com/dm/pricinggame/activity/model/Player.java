package com.dm.pricinggame.activity.model;

/**
 * Created by linuxraju on 3/29/17.
 */
public class Player {

    /*
          "player": {
        "id": 1,
        "username": null,
        "email": "mainalipuk@wtf.com",
        "role": null
      }
     */

    private String pId;
    private String pUserName;
    private String pEmail;

    public Player(String pId, String pUserName, String pEmail) {
        this.pId = pId;
        this.pUserName = pUserName;
        this.pEmail = pEmail;
    }

    public String getpId() {
        return pId;
    }

    public String getpUserName() {
        return pUserName;
    }

    public String getpEmail() {
        return pEmail;
    }
}
