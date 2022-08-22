package com.byow.wallet.byow.gui.services;

import com.byow.wallet.byow.gui.annotations.ActivateProgressBar;
import com.byow.wallet.byow.observables.AsyncProgress;
import javafx.application.Platform;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProgressAspect {
    private final AsyncProgress asyncProgress;

    public ProgressAspect(AsyncProgress asyncProgress) {
        this.asyncProgress = asyncProgress;
    }

    @Around("@annotation(activateProgressBar)")
    public Object activateProgressBar(ProceedingJoinPoint proceedingJoinPoint, ActivateProgressBar activateProgressBar) throws Throwable {
        Platform.runLater(() -> {
            asyncProgress.setTaskDescription(activateProgressBar.value());
            asyncProgress.start();
        });
        Object result = proceedingJoinPoint.proceed();
        Platform.runLater(() -> {
            asyncProgress.setTaskDescription("");
            asyncProgress.stop();
        });
        return result;
    }
}