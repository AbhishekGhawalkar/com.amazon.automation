package testcases;

import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends LoginPage {

  @Test
  public void verifyLoginFuctionality() {
    // Get user and pass
    String email = getProperty("username");
    String password = getProperty("password");
    // Login to the page
    loginFlipkart(email, password);
  }
}
