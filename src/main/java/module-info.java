module producer.consumer.task17 {
    requires javafx.controls;
    requires javafx.fxml;


    opens producer.consumer.task17 to javafx.fxml;
    exports producer.consumer.task17;
}