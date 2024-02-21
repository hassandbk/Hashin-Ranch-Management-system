// ActionHandlerFactory.java
package com.example.hashinfarm.controller.handlers;

import com.example.hashinfarm.controller.handlers.addItems.*;
import com.example.hashinfarm.controller.handlers.cattleDetailsMoreHandlers.*;
import com.example.hashinfarm.controller.handlers.imagesHandlers.*;
import com.example.hashinfarm.controller.interfaces.*;

public class ActionHandlerFactory {
    public static ActionHandler createActionHandler(String fxId) {
        return switch (fxId) {
            case "vaccinationHistory" -> new VaccinationHistoryHandler();
            case "updateVaccination" -> new UpdateVaccinationHandler();
            case "healthInfoHistory" -> new HealthInfoHistoryHandler();
            case "healthInfoUpdate" -> new HealthInfoUpdateHandler();
            case "viewCalvingHistory" -> new ViewCalvingHistoryHandler();
            case "saveCalvingChanges" -> new SaveCalvingChangesHandler();
            case "viewDewormingHistory" -> new ViewDewormingHistoryHandler();
            case "saveDewormingChanges" -> new SaveDewormingChangesHandler();
            case "viewMoreAdditionalInfo" -> new ViewMoreAdditionalInfoHandler();
            case "saveAdditionalInfo" -> new SaveAdditionalInfoHandler();

            case "viewImage" -> new ViewImageHandler();
            case "viewGallery" -> new ViewGalleryHandler();
            case "addBreed" -> new AddBreedHandler();
            case "viewBreed" -> new ViewBreedHandler();
            // Add more cases for other buttons
            default -> null;
        };
    }
}