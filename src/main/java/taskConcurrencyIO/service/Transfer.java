package taskConcurrencyIO.service;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import taskConcurrencyIO.exception.InsufficientFundsException;
import taskConcurrencyIO.model.Account;

public class Transfer implements Callable<Boolean> {

  private static final AtomicInteger idGenerator = new AtomicInteger();

  private static final int LOCK_WAIT_SEC = 5;

  private final int id;

  private final Account accFrom;

  private final Account accTo;

  private final long amount;

  private final Random waitRandom = new Random();

  private boolean retryFail;
  private CountDownLatch startLatch;
  private CountDownLatch endLatch;
  private CyclicBarrier endBarrier;

  public Transfer(Account accFrom, Account accTo, long amount) {
    this.id = idGenerator.getAndIncrement();

    this.accFrom = accFrom;
    this.accTo = accTo;
    this.amount = amount;
  }

  public Transfer(Account accFrom, Account accTo, long amount,
      boolean retryFail, CountDownLatch startLatch,
      CountDownLatch endLatch, CyclicBarrier endBarrier) {
    this(accFrom, accTo, amount);
    this.retryFail = retryFail;
    this.startLatch = startLatch;
    this.endLatch = endLatch;
    this.endBarrier = endBarrier;
  }

  @Override
  public Boolean call() throws Exception {

    if (startLatch != null) {
      System.out.println("[" + id + "] " + "Waiting to start...");
      startLatch.await();
    }

    for (;;) {
      if (accFrom.getLock().tryLock(LOCK_WAIT_SEC, TimeUnit.SECONDS)) {
        try {
          if (accTo.getLock()
              .tryLock(LOCK_WAIT_SEC, TimeUnit.SECONDS)) {

            try {
              if (accFrom.getBalance() < amount) {
                throw new InsufficientFundsException("[" + id
                    + "] " + "Failed to transfer " + amount
                    + " from Account " + accFrom.getId()
                    + " (Balance is "
                    + accFrom.getBalance() + ")");
              }

              accFrom.withdraw(amount);
              accTo.deposit(amount);

              Thread.sleep(waitRandom.nextInt(1000));

              System.out.println("[" + id + "] " + "Transfer "
                  + amount + "$" + " done from " + "Account " + accFrom.getId()
                  + " to Account " + accTo.getId());

              return true;

            } finally {
              accFrom.getLock().unlock();
              accTo.getLock().unlock();

              if (endLatch != null) {
                endLatch.countDown();
              }

              if (endBarrier != null) {
                endBarrier.await();
              }
            }

          } else {
            accTo.incFailedTransferCount();
            if (!retryFail) {
              return false;
            }
          }
        } finally {
          accFrom.getLock().unlock();
        }
      } else {
        accFrom.incFailedTransferCount();
        if (!retryFail) {
          return false;
        }
      }
    }
  }
}
