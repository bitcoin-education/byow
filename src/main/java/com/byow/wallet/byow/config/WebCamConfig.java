package com.byow.wallet.byow.config;

import com.byow.wallet.byow.gui.services.WebCamService;
import com.github.sarxos.webcam.Webcam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebCamConfig {
    @Bean
    public WebCamService webCamService() {
        Webcam cam = Webcam.getWebcams().get(0);
        return new WebCamService(cam);
    }
}
