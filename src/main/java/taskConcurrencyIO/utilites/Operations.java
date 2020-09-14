package taskConcurrencyIO.utilites;

import static java.text.MessageFormat.format;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import taskConcurrencyIO.model.Account;
import taskConcurrencyIO.service.Transfer;

public class Operations {

  private static final Logger logger = Logger.getLogger(Operations.class.getSimpleName());

  private static final int NUM_A_B_TRANSFERS = 1;

  static volatile long transferAbStart;
  static volatile long transferAbEnd;

  public static void main(String[] args)
      throws InterruptedException, IOException, ClassNotFoundException {

    FileInputStream fis = new FileInputStream("src/main/resources/Accounts/accounts.bin");
    ObjectInputStream ois = new ObjectInputStream(fis);

    Account accA = (Account) ois.readObject();
    Account accB = (Account) ois.readObject();
    Account accC = (Account) ois.readObject();
    Account accD = (Account) ois.readObject();
    Account accE = (Account) ois.readObject();
    Account accF = (Account) ois.readObject();
    Account accG = (Account) ois.readObject();
    Account accH = (Account) ois.readObject();
    Account accI = (Account) ois.readObject();
    Account accJ = (Account) ois.readObject();
    ois.close();

    Random rnd = new Random();

    transferAbStart = System.currentTimeMillis();

    CountDownLatch startLatch = new CountDownLatch(1);

    CountDownLatch baLatch = new CountDownLatch(NUM_A_B_TRANSFERS);

    CyclicBarrier abBarrier = new CyclicBarrier(NUM_A_B_TRANSFERS,

        new Runnable() {
          @Override
          public void run() {
            transferAbEnd = System.currentTimeMillis();
          }
        });

    ExecutorService service = Executors.newFixedThreadPool(20);

    System.out.println(
        "Initial accounts balance: " + (accA.getBalance() + accB.getBalance() + accC.getBalance()
            + accD
            .getBalance() + accE.getBalance() + accF.getBalance() + accG.getBalance() + accH
            .getBalance() + accI.getBalance() + accJ.getBalance()) + "$");

    for (int k = 0; k < 125; k++) {
      service.submit(new Transfer(accA, accB, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accB, accC, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accC, accD, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accD, accE, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accF, accG, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accG, accH, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accH, accI, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accI, accJ, rnd.nextInt(50), true,
          startLatch, baLatch, abBarrier));
    }
    for (int l = 0; l < 125; l++) {
      service.submit(new Transfer(accB, accA, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accC, accB, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accD, accC, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accE, accD, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accG, accF, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accH, accG, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accI, accH, rnd.nextInt(50), true,
          baLatch, null, null));
      service.submit(new Transfer(accJ, accI, rnd.nextInt(50), true,
          baLatch, null, null));
    }

    service.shutdown();

    startLatch.countDown();

    System.out.println(
        "Summary accounts balance: " + (accA.getBalance() + accB.getBalance() + accC.getBalance()
            + accD
            .getBalance() + accE.getBalance() + accF.getBalance() + accG.getBalance() + accH
            .getBalance() + accI.getBalance() + accJ.getBalance()) + "$");

    // Waiting for all tasks to complete
    boolean rezWait = service.awaitTermination(
        (100 + 100), TimeUnit.SECONDS);

    if (!rezWait) {
      logger.warning("Not all tasks have completed");

      logger.info(
          "Interim accounts balance: " + (accA.getBalance() + accB.getBalance() + accC.getBalance()
              + accD
              .getBalance() + accE.getBalance() + accF.getBalance() + accG.getBalance() + accH
              .getBalance() + accI.getBalance() + accJ.getBalance()) + "$");
    }

    logger.info(format("Overall time for A->B transfers is: {0} ms", transferAbEnd - transferAbStart));
  }
}
