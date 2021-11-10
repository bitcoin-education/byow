package com.byow.wallet.byow;

import javafx.application.Application;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import java.security.Security;

@SpringBootApplication
@EnableAsync
@EnableRetry
public class ByowApplication {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Application.launch(GuiApplication.class, args);
	}

}
