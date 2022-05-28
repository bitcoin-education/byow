package com.byow.wallet.byow.gui.services;

import org.springframework.stereotype.Service;
import javafx.scene.control.Alert;
import com.byow.wallet.byow.domains.node.Error;

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

    public void alertError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.show();
    }
}
