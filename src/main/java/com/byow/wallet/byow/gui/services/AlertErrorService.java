package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.domains.node.Error;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AlertErrorService {
    public void handleError(Future<Error> result) {
        Error error;
        try {
            error = result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (error != null) {
            alertError(error.message());
        }
    }

    public void alertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        ok.setId("alertOk");
        alert.show();
    }
}
