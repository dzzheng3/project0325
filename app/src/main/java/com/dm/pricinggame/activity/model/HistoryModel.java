package com.dm.pricinggame.activity.model;

/**
 * Created by linuxraju on 3/28/17.
 */

public class HistoryModel {

    /*    {
      "id": 1,
      "player_game_id": 1,
      "user_id": 1,
      "user_input": 5,
      "user_score": 5,
      "level": 1
    },*/

    private String hisId;
    private String hisPlayerGameId;
    private String hisUserInput;
    private String hisUserProfit;
    private String hisLevel;

    public HistoryModel(String hisId, String hisPlayerGameId, String hisUserInput, String hisUserProfit, String hisLevel) {
        this.hisId = hisId;
        this.hisPlayerGameId = hisPlayerGameId;
        this.hisUserInput = hisUserInput;
        this.hisUserProfit = hisUserProfit;
        this.hisLevel = hisLevel;
    }

    public String getHisId() {
        return hisId;
    }

    public String getHisPlayerGameId() {
        return hisPlayerGameId;
    }

    public String getHisUserInput() {
        return hisUserInput;
    }

    public String getHisUserProfit() {
        return hisUserProfit;
    }

    public String getHisLevel() {
        return hisLevel;
    }
}
