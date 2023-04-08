package testcases;

import org.testng.annotations.Test;
import pages.PageClass;

public class TestClass extends PageClass {

  @Test
  public void test_1() {
    // Get user,email and pass
    String email = getProperty("email");
    String password = getProperty("password");
    String username = getProperty("username");
    // Test Data
    String searchWord = "amazon";
    String expUrl = "https://www.amazon.in";
    String productCategory = "Electronics";
    String productKeyword = "dell computers";
    // Search keyword on google
    googleSearch(searchWord, expUrl);
    // Select and open expected url from results
    loginToAmazonPortal(email, password, username);
    // Select product category on amazon home page
    selectCategory(productCategory);
    // Search product name in searchbar
    seachProduct(productKeyword);
  }
}
