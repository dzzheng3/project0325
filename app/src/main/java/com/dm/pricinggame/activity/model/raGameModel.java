package com.dm.pricinggame.activity.model;

/**
 * Created by linuxraju on 3/29/17.
 */

public class raGameModel {

    /*
      "status": "success",
  "data": [
    {
      "id": 1,
      "game_id": 1,
      "user_id": 1,
      "current_level": 1,
      "status": "active",
      "player": {
        "id": 1,
        "username": null,
        "email": "mainalipuk@wtf.com",
        "role": null
      }
    }*/

    private String playerGameId;
    private String current_level;
    private String createdDate;
    private Player player;

    public raGameModel(String playerGameId, String current_level, Player player,String createdDate) {
        this.playerGameId = playerGameId;
        this.current_level = current_level;
        this.player = player;
        this.createdDate = createdDate;
    }

    public String getPlayerGameId() {
        return playerGameId;
    }

    public String getCurrent_level() {
        return current_level;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
