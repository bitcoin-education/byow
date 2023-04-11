package com.byow.wallet.byow.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.springframework.stereotype.Component;

@Component
public class QRCodeViewController {
    @FXML
    private DialogPane dialogPane;
    @FXML
    private ImageView imageView;

    public void setImage(WritableImage image) {
        imageView.setImage(image);
    }
}
