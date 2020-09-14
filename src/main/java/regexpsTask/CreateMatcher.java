package regexpsTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;


public class CreateMatcher {

  public static void main(String[] args) throws FileNotFoundException {

    String findPhoneNumbers = "\\+[0-9]{1}\\([0-9]{3}\\)\\s[0-9]{3}\\s[0-9]{2}\\s[0-9]{2}";

    InputStream inStream = null;
    OutputStream outStream = null;
    String line = null;

    {
      try {
        inStream = new FileInputStream(
            "C:/Users/Viktoriia_Krutovskai/advancedjavard/src/main/resources/Notifications.txt");

        System.out.println("File Notification.txt is found successfully \n");

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    {
      try {
        line = IOUtils.toString(inStream, StandardCharsets.UTF_8.name());
        System.out.println("Original file is: " + line + "\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Pattern pattern = Pattern.compile(findPhoneNumbers);
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      System.out.println("Founded numbers: " + matcher.group());

      String changeFormat = matcher.group().replaceAll("\\D", "") + '\n';
      System.out.println("Formatted numbers: " + changeFormat);

      outStream = new FileOutputStream(
          "C:/Users/Viktoriia_Krutovskai/advancedjavard/src/main/resources/PhoneNumbers.txt", true);
      try {
        outStream.write(changeFormat.getBytes("UTF-8"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("Changed format numbers are successfully written in PhoneNumbers.txt \n");
    }
  }
}
