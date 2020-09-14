package taskConcurrencyIO.utilites;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import taskConcurrencyIO.model.Account;

public class AccountManager {

  public static void main(String[] args) throws IOException {

    Account accA = new Account(1, 10000L);
    Account accB = new Account(2, 15000L);
    Account accC = new Account(3, 15505L);
    Account accD = new Account(4, 20000L);
    Account accE = new Account(5, 12000L);
    Account accF = new Account(6, 12070L);
    Account accG = new Account(7, 14302L);
    Account accH = new Account(8, 14000L);
    Account accI = new Account(9, 10030L);
    Account accJ = new Account(10, 18000L);

    FileOutputStream fos = new FileOutputStream("src/main/resources/Accounts/accounts.bin");
    ObjectOutputStream oos = new ObjectOutputStream(fos);

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

    oos.close();
  }

  public static void writeToFile(Account account) throws IOException {
    FileOutputStream fos = new FileOutputStream("src/main/resources/Accounts/accounts.bin");
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(account);
  }

}
