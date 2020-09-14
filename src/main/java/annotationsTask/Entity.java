package annotationsTask;

public class Entity {

  public Entity() {
  }

  @Secured(number = 1)
  private void sum(int a, int b) {
    a = 10;
    b = 5;
    System.out.println("Print sum: " + a+b);
  }

  public void division(int v, int n) {
    n = 100;
    v = 20;
    System.out.println("Print division: " + n / v);
  }

  @Secured(number = 2, name = "Boris Burda")
  public String printName(String name, int num) {
    num = 10;
    name = "Boris Burda";
    System.out.println("Just print the name and num params: " + name + num);
    return name;
  }

}
