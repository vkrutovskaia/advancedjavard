package taskProducerConsumer;

import static java.util.concurrent.ThreadLocalRandom.current;

import java.util.Date;
import java.util.Random;

public class Hotel {

  private int roomId = new Random().nextInt(15);
  private Date date = new Date(current().nextInt() * 1000L);

  private String hotel = "Boris Burda Hotel Deluxe";

  public Hotel() {
  }

  public Hotel(int roomId, Date date, String hotel) {
    this.roomId = roomId;
    this.date = date;
    this.hotel = hotel;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setRoomId(int roomId) {
    this.roomId = roomId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getHotel() {
    return hotel;
  }

  public void setHotel(String hotel) {
    this.hotel = hotel;
  }
}
