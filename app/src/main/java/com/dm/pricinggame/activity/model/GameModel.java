package com.dm.pricinggame.activity.model;

/**
 * Created by linuxraju on 3/27/17.
 */
public class GameModel {

    private int gameId;
    private String gameName;
    private String gameDes;
    private String gameType;

    public GameModel(int gameId, String gameName, String gameDes, String gameType) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameDes = gameDes;
        this.gameType = gameType;
    }

    public String getGameType() {
        return gameType;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameDes() {
        return gameDes;
    }

    public int getGameId() {
        return gameId;
    }
}
