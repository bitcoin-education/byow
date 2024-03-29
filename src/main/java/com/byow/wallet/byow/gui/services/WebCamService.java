package com.byow.wallet.byow.gui.services;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class WebCamService extends Service<Image> {
    private final Webcam cam;

    private final WebcamResolution resolution;

    public WebCamService(Webcam cam, WebcamResolution resolution) {
        this.cam = cam;
        this.resolution = resolution;
        cam.setCustomViewSizes(resolution.getSize());
        cam.setViewSize(resolution.getSize());
    }

    public WebCamService(Webcam cam) {
        this(cam, WebcamResolution.VGA);
    }

    @Override
    public Task<Image> createTask() {
        return new Task<>() {
            @Override
            protected Image call() throws Exception {

                try {
                    cam.open();
                    while (!isCancelled()) {
                        if (cam.isImageNew()) {
                            BufferedImage bimg = cam.getImage();
                            updateValue(SwingFXUtils.toFXImage(bimg, null));
                        }
                    }
                    cam.close();
                    return getValue();
                } finally {
                    cam.close();
                }
            }

        };
    }


    public int getCamWidth() {
        return resolution.getSize().width;
    }

    public int getCamHeight() {
        return resolution.getSize().height;
    }
}
