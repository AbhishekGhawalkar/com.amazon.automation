package pages;

import baseUtility.CommonUtility;

public class LoginPage extends CommonUtility {

  // Login Page xpaths
  String loginForm = "//div/form[@autocomplete='on']";
  String emailFld =
      "//label[normalize-space()='Enter Email/Mobile number']/preceding-sibling::input";
  String passwordFld = "//label[normalize-space()='Enter Password']/preceding-sibling::input";
  String loginBtn = "//button/span[text()='Login']";
  String myAccountTxt = "//div[text()='My Account']";
  // #######################################################################

  public void loginFlipkart(String email, String password) {
    navigateTo("https://www.flipkart.com/");
    if (isElementDisplayed(loginForm)) {
      typeIn(emailFld, email);
      typeIn(passwordFld, password);
      clickOn(loginBtn);
    }

    if (isElementDisplayed(myAccountTxt)) {
      System.out.println("User logged in");
    } else {
      testStepFailed("User login failed");
    }
  }
}
