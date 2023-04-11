package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.gui.services.WebCamService;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class QRCodeCaptureController {
    @FXML
    private DialogPane dialogPane;

    @FXML
    private ImageView imageView;

    private final WebCamService webCamService;

    private String decodedQRCode = "";

    public QRCodeCaptureController(WebCamService webCamService) {
        this.webCamService = webCamService;
    }

    public void initialize() {
        decodedQRCode = "";
        webCamService.stateProperty().addListener((observableValue, oldState, newState) -> {
            switch (newState) {
                case RUNNING -> {
                    imageView.imageProperty().unbind();
                    imageView.imageProperty().bind(webCamService.valueProperty());
                }
                case CANCELLED -> {
                    imageView.imageProperty().unbind();
                    imageView.setImage(null);
                }
                case FAILED -> {
                    imageView.imageProperty().unbind();
                    webCamService.getException().printStackTrace();
                }
                case SUCCEEDED -> imageView.imageProperty().unbind();
            }
        });
        imageView.imageProperty().addListener((observableValue, oldImage, newImage) -> {
            if (newImage != null) {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(newImage, null);
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    decodedQRCode = result.getText();
                    destroy();
                    dialogPane.getScene().getWindow().hide();
                } catch (NotFoundException ignore) {
                }
            }
        });
        webCamService.restart();
    }

    public void destroy() {
        if (webCamService.isRunning()) {
            webCamService.cancel();
        }
    }

    public String getDecodedQRCode() {
        return decodedQRCode;
    }
}
