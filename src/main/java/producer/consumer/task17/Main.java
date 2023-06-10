package producer.consumer.task17;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import producer.consumer.task17.model.Car;
import producer.consumer.task17.model.Parking;
import producer.consumer.task17.model.ParkingLot;
import java.util.concurrent.*;


public class Main extends Application {
    private static final int NUM_PARKING_SPOTS = 4;
    private static final int NUM_CARS = 10;
    private static final int PARKING_TIMEOUT = 5000;
    private ExecutorService executorService;
    private BlockingQueue<ParkingLot> parkingQueue;

    private VBox carBox;
    private Label parkingStatusLabel;
    private Button startButton, stopButton;

    @Override
    public void start(Stage primaryStage) {
        parkingQueue = new ArrayBlockingQueue<>(NUM_PARKING_SPOTS); // Общий ресурс (парковочные места)
        Parking parking = new Parking(parkingQueue, NUM_PARKING_SPOTS); // Создаём объект парковки (поставщик)

        carBox = new VBox(10);
        for (int i = 1; i <= NUM_CARS; i++) {
            Car car = new Car(i, parkingQueue, PARKING_TIMEOUT, parking);
            carBox.getChildren().add(car.getCarLabel());
        }
        VBox.setMargin(carBox, new Insets(0, 0, 30, 0)); // Нижний отступ для списка машин

        Thread parkingThread = new Thread(parking); // Запуск потока парковки (поставщика)
        parkingThread.start();

        parkingStatusLabel = new Label("Парковака пуста");
        VBox.setMargin(parkingStatusLabel, new Insets(10, 0, 10, 0));

        startButton = new Button("Начать работу");
        stopButton = new Button("Остановить работу");

        HBox buttonContainer = new HBox(startButton, stopButton);
        buttonContainer.setSpacing(30);

        startButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());

        VBox root = new VBox();
        root.setPadding(new Insets(15));

        Separator separator = new Separator();
        separator.setPrefHeight(4);

        root.getChildren().addAll(carBox, separator, parkingStatusLabel, buttonContainer);
        Scene scene = new Scene(root, 350, 420);

        primaryStage.setTitle("Task 17 Поставщик-Потребитель");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> stopSimulation());
        primaryStage.show();
    }

    private void startSimulation() {
        executorService = Executors.newFixedThreadPool(NUM_CARS); // Создание пулла потоков

        for (int i = 0; i < NUM_CARS; i++) {
            Car car = (Car) carBox.getChildren().get(i).getUserData();
            executorService.execute(car); // Добавление задачи в пулл потоков
        }

        parkingStatusLabel.setText("Парковка работает");

        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    private void stopSimulation() {
        if (executorService != null) {
            executorService.shutdownNow(); // Немедленное прерывание работы задач
            try {
                executorService.awaitTermination(1, TimeUnit.SECONDS); // Ждём завершение всех задач
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        parkingQueue.clear(); // Очистка очереди
        parkingStatusLabel.setText("Работа парковки остановлена");

        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}