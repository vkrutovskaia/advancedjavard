package taskConcurrencyIO.utilites;

import static java.text.MessageFormat.format;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    FileOutputStream fos = new FileOutputStream("src/main/resources/Accounts/accounts.bin");
    ObjectOutputStream oos = new ObjectOutputStream(fos);

    Random rnd = new Random();

    transferAbStart = System.currentTimeMillis();

    CountDownLatch startLatch = new CountDownLatch(1);

    CountDownLatch baLatch = new CountDownLatch(NUM_A_B_TRANSFERS);

    CyclicBarrier abBarrier = new CyclicBarrier(NUM_A_B_TRANSFERS,

        () -> transferAbEnd = System.currentTimeMillis());

    ExecutorService service = Executors.newFixedThreadPool(20);

    System.out.println(
        "Initial accounts balance: " + (accA.getBalance() + accB.getBalance() + accC.getBalance()
            + accD
            .getBalance() + accE.getBalance() + accF.getBalance() + accG.getBalance() + accH
            .getBalance() + accI.getBalance() + accJ.getBalance()) + "$");

    for (int k = 0; k < 111; k++) {
      service.submit(new Transfer(accA, accB, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accB, accC, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accC, accD, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accD, accE, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accF, accG, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accG, accH, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accH, accI, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accI, accJ, rnd.nextInt(2000), true,
          startLatch, baLatch, abBarrier));
      service.submit(new Transfer(accJ, accA, rnd.nextInt(10000), true,
          baLatch, baLatch, abBarrier));

      oos.writeObject(accA);
      oos.writeObject(accB);
      oos.writeObject(accC);
      oos.writeObject(accD);
      oos.writeObject(accE);
      oos.writeObject(accF);
      oos.writeObject(accG);
      oos.writeObject(accH);
      oos.writeObject(accI);
      oos.writeObject(accJ);
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
        (300), TimeUnit.SECONDS);

    if (!rezWait) {
      logger.warning("Not all tasks have completed");

      logger.info(
          "Interim accounts balance: " + (accA.getBalance() + accB.getBalance() + accC.getBalance()
              + accD
              .getBalance() + accE.getBalance() + accF.getBalance() + accG.getBalance() + accH
              .getBalance() + accI.getBalance() + accJ.getBalance()) + "$");

      logger.info("Failed accA transfers count: " + accA.getFailCount() + "\n"
          + "Failed accB transfers count: " + accB.getFailCount() + "\n"
          + "Failed accC transfers count: " + accC.getFailCount() + "\n"
          + "Failed accD transfers count: " + accD.getFailCount() + "\n"
          + "Failed accE transfers count: " + accE.getFailCount() + "\n"
          + "Failed accF transfers count: " + accF.getFailCount() + "\n"
          + "Failed accG transfers count: " + accG.getFailCount() + "\n"
          + "Failed accH transfers count: " + accH.getFailCount() + "\n"
          + "Failed accI transfers count: " + accI.getFailCount() + "\n"
          + "Failed accJ transfers count: " + accJ.getFailCount());

      logger.info("Interim accA balance: " + accA.getBalance()+ "$" + "\n"
          + "Interim accB balance: " + accB.getBalance()+ "$" + "\n"
          + "Interim accC balance: " + accC.getBalance()+ "$" + "\n"
          + "Interim accD balance: " + accD.getBalance()+ "$" + "\n"
          + "Interim accE balance: " + accE.getBalance()+ "$" + "\n"
          + "Interim accF balance: " + accF.getBalance()+ "$" + "\n"
          + "Interim accG balance: " + accG.getBalance()+ "$" + "\n"
          + "Interim accH balance: " + accH.getBalance()+ "$" + "\n"
          + "Interim accI balance: " + accI.getBalance()+ "$" + "\n"
          + "Interim accJ balance: " + accJ.getBalance()+ "$");
    }

    logger.info(
        format("Overall time for A->B transfers is: {0} ms", transferAbEnd - transferAbStart));
  }
}
