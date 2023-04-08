package main;

import java.util.ArrayList;
import java.util.List;
import org.testng.TestNG;

public class MainRunner {
  static TestNG testng;

  public static void main(String[] args) {
    testng = new TestNG();
    List<String> xmlList = new ArrayList<>();
    xmlList.add("user.dir" + "/Suite.xml");
    testng.setTestSuites(xmlList);
    testng.run();
  }
}
