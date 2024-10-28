// ActionHandlerFactory.java
package com.example.hashinfarm.businessLogic.services.handlers;

import com.example.hashinfarm.businessLogic.services.handlers.addItems.*;
import com.example.hashinfarm.businessLogic.services.handlers.cattleDetailsMoreHandlers.*;

import com.example.hashinfarm.businessLogic.services.interfaces.ActionHandler;

public class ActionHandlerFactory {
    public static ActionHandler createActionHandler(String fxId) {
        return switch (fxId) {
            case "vaccinationHistory" -> new VaccinationHistoryHandler();
            case "updateVaccination" -> new UpdateVaccinationHandler();
            case "healthInfoHistory" -> new HealthInfoHistoryHandler();
            case "healthInfoUpdate" -> new HealthInfoUpdateHandler();
            case "viewCalvingHistory" -> new ViewCalvingHistoryHandler();
            case "saveCalvingChanges" -> new SaveCalvingChangesHandler();
            case "viewInjuriesHistory" -> new ViewInjuriesHistoryHandler();
            case "viewSurgicalHistory" -> new ViewSurgicalHistoryHandler();
            case "saveDewormingChanges" -> new SaveDewormingChangesHandler();
            case "viewMoreAdditionalInfo" -> new ViewMoreAdditionalInfoHandler();
            case "saveAdditionalInfo" -> new SaveAdditionalInfoHandler();
            case "modifyCattle" -> new ModifyCattleHandler();
            case "addBreed" -> new AddBreedHandler();

            // Add more cases for other buttons
            default -> null;
        };
    }
}