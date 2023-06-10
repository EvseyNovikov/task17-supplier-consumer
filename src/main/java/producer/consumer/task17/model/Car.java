package producer.consumer.task17.model;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Car implements Runnable {
    private int carNumber;
    private BlockingQueue<ParkingLot> parkingQueue; // Общий ресурс (парковочные места)
    private Label Carlabel; // Label объекта Car, который добавляется в Vbox через цикл
    private int parkingTimeout; // Время ожидания свободного места

    private Parking parking;

    public Car(int carNumber, BlockingQueue<ParkingLot> parkingQueue, int parkingTimeout, Parking parking) {
        this.carNumber = carNumber;
        this.parkingQueue = parkingQueue;

        this.Carlabel = new Label("Машина " + carNumber + ": вне парковки");
        this.Carlabel.setUserData(this);

        this.parkingTimeout = parkingTimeout;
        this.parking = parking;
    }

    public Label getCarLabel() {
        return Carlabel;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ParkingLot parkingLot = parkingQueue.poll(parkingTimeout, TimeUnit.MILLISECONDS);

                if (parkingLot != null) {
                    park(parkingLot);
                    leave(parkingLot);
                } else {
                    updateLabel("Машина " + carNumber + ": нет свободных мест. Уезжает с парковки");
                    Thread.sleep(getRandomDelay()); // Немного подождать перед тем, как приехать обратно на парковку
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //Завершение потока при ощибке
        }
    }

    private void park(ParkingLot parkingLot) throws InterruptedException {
        updateLabel("Машина " + carNumber + ": занимает парковочное место №" + parkingLot.getLotNumber());
        Thread.sleep(getRandomDelay());

        // Имитация того, что машина занимает парковочное место
        updateLabel("Машина " + carNumber + ": припаркована на парковочном месте №" + parkingLot.getLotNumber());
        Thread.sleep(getRandomDelay());
    }

    private void leave(ParkingLot parkingLot) throws InterruptedException {
        updateLabel("Машина " + carNumber + ": покидает парковочное место №" + parkingLot.getLotNumber());
        Thread.sleep(getRandomDelay());

        parking.setNewLotNeeded(true);

        updateLabel("Машина " + carNumber + ": вне парковки");
        Thread.sleep(getRandomDelay());
    }

    private void updateLabel(String text) {
        Platform.runLater(() -> Carlabel.setText(text)); // Обновление объекта Label из потока поставщика
    }

    private long getRandomDelay() {
        return ThreadLocalRandom.current().nextInt(2000, 6000);
    }
}