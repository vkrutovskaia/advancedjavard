package taskConcurrencyIO.model;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int id;

  private long balance;

  private final LongAdder failCounter = new LongAdder();

  private final Lock lock = new ReentrantLock();


  public Account(int accountId, long initialBalance) {
    this.id = accountId;
    this.balance = initialBalance;
  }

  public int getId() {

    return id;
  }

  public long getBalance() {
    return balance;
  }

  public void deposit(final long amount) {
    balance += amount;
  }

  public void withdraw(final long amount) {
    balance -= amount;
  }

  public void incFailedTransferCount() {
    failCounter.increment();
  }

  public long getFailCount() {
    return failCounter.sum();
  }

  public Lock getLock() {
    return lock;
  }
}
