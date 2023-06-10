package producer.consumer.task17.model;

import java.lang.ref.PhantomReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Parking implements Runnable {
    private BlockingQueue<ParkingLot> parkingQueue; // Общий ресурс (парковочные места)
    private int NUM_PARKING_SPOTS;

    private boolean newLotNeeded = false;
    private boolean parkingWasBorn = true;

    public Parking(BlockingQueue<ParkingLot> parkingQueue, int NUM_PARKING_SPOTS) {
        this.parkingQueue = parkingQueue;
        this.NUM_PARKING_SPOTS = NUM_PARKING_SPOTS;
    }

    public void setNewLotNeeded(boolean newLotNeeded) {
        this.newLotNeeded = newLotNeeded;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            if(parkingWasBorn){ // Первоначальное заполнение очереди парковочных мест
                for (int i = 1; i <= NUM_PARKING_SPOTS; i++) {
                    try {
                        parkingQueue.put(new ParkingLot(i));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Завершение потока при ошибке
                    }
                }
                parkingWasBorn = false;
            }

           if(newLotNeeded){ // Если с парковки уезжает машина, Поставщик добавляет новое парковочное место на парковку
               try {
                   parkingQueue.put(new ParkingLot(5));
                   newLotNeeded = false; // Новое парковочное место пока больше не нужно
               } catch (InterruptedException e) {
                   throw new RuntimeException(e); // Завершение потока при ошибке
               }
           }
        }
    }

}