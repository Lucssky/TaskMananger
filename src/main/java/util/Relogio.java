package util;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Relogio extends Thread {
    private final Label label;

    public Relogio(Label label) {
        this.label = label;
    }

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        while (true) {
            Platform.runLater(() -> {
                String dataHora = LocalDateTime.now().format(formatter);
                label.setText(dataHora);
            });

            try {
                Thread.sleep(1000); // Atualiza a cada 1 segundo
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
