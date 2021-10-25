package com.byow.wallet.byow;

import javafx.application.Application;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class ByowApplication {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Application.launch(GuiApplication.class, args);
	}

}
