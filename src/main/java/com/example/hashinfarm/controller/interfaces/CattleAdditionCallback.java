package com.example.hashinfarm.controller.interfaces;

import java.sql.SQLException;

public interface CattleAdditionCallback {
    void onCattleAddedSuccessfully(boolean successFlag, String failureReason, int newCattleId) throws SQLException;
}
