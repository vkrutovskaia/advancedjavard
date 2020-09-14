package taskProducerConsumer;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class Booking {

  private static final Logger logger = Logger.getLogger(Booking.class.getSimpleName());

  public static void main(String[] args) {
    MyBlockingQueue myBlockingQueue = new MyBlockingQueue();
    final Semaphore semaphore = new Semaphore(6);
    Thread[] threads = new Thread[6];
    for (int i = 0; i < threads.length; i++) {
      String threadName = Integer.toString(i);
      threads[i] = new Thread(() -> {
        try {
          semaphore.acquire();
          try {
            while (true) {
              Runnable task = myBlockingQueue.get();
              task.run();
              logger.info(format("[Booker {0}]: received [Thread Id]:{1}", threadName,
                  Thread.currentThread().getId()));
              Thread.sleep(5000);
              logger.info(format("[Booker {0}]: processed [Thread Id]:{1}", threadName,
                  Thread.currentThread().getId()));
            }
          } finally {
            semaphore.release();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }, threadName);
    }
    stream(threads).forEach(Thread::start);

    final Semaphore semaphore2 = new Semaphore(1);
    Thread[] producers = new Thread[3];
    for (int i = 0; i < producers.length; i++) {
      producers[i] = new Thread(() -> {
        try {
          semaphore2.acquire();
          try {
            for (int j = 0; j < 3; j++) {
              Runnable produce = myBlockingQueue.put(bookingHotel());
              produce.run();
            }
          } finally {
            semaphore2.release();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
    stream(producers).forEach(Thread::start);
  }


  public static Runnable bookingHotel() {
    return () -> {
      logger.info(format("{0} [Thread Id]:{1}",
          format("[Producer {0}]: sent {1}", Thread.currentThread().getName(),
              Booking.bookingRequest()), Thread.currentThread().getId()));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };
  }

  static class MyBlockingQueue {

    private final int CAPACITY = 5;
    List<Runnable> bookings = new ArrayList<>(CAPACITY);

    public synchronized Runnable get() {
      while (bookings.isEmpty()) {
        try {
          wait();
          logger.warning("Queue is empty, waiting for put request");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      Runnable booking = bookings.get(0);
      bookings.remove(booking);
      return booking;
    }

    public synchronized Runnable put(Runnable booking) {
      if (bookings.size() == CAPACITY) {
        logger.warning("Max capacity, waiting for removing requests");
        try {
          wait();

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      bookings.add(booking);
      notify();
      return booking;
    }
  }

  public static String bookingRequest() {
    Hotel hotel = new Hotel();
    return String
        .format("[Room Id]: %s [Hotel name]: %s [Date of booking]: %s.", hotel.getRoomId(),
            hotel.getHotel(), hotel.getDate());
  }

}
