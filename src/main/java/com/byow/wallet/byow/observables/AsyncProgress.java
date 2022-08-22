package com.byow.wallet.byow.observables;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

@Component
public class AsyncProgress {
    private final DoubleProperty progress = new SimpleDoubleProperty(0);

    private final StringProperty taskDescription = new SimpleStringProperty();

    public void start() {
        progress.set(INDETERMINATE_PROGRESS);
    }

    public void stop() {
        progress.set(0);
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public StringProperty taskDescriptionProperty() {
        return taskDescription;
    }

    public void setTaskDescription(String description) {
        taskDescription.set(description);
    }
}