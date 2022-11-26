package baseUtility;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CommonUtility {

	// Standard Waits
	protected static int WAIT_TIME = 10;

	private long domContentLoadedEventStart = 0L;

	private WebElement webElement;
	public static WebDriver driver;

	@BeforeSuite
	public void beforeSuite() {
		setUpBrowserAndLauch();

	}

	public void setUpBrowserAndLauch() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions o = new ChromeOptions();
		o.addExtensions(new File("./src/main/java/resources/extension_5_3_0_0.crx"));
		o.addExtensions(new File("./src/main/java/resources/IDMGCExt.crx"));
		o.setCapability(ChromeOptions.CAPABILITY, o);
		driver = new ChromeDriver(o);
		driver.manage().window().maximize();
		waitTime(10);
		transferControlToWindow(2, true);
		transferControlToWindow(1, false);
	}

	public String getProperty(String key) {
		String value = null;
		try {
			FileReader reader = new FileReader("./src/main/java/resources/Data.properties");
			Properties props = new Properties();
			props.load(reader);
			value = props.getProperty(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public void typeIn(String objectLocator, String inputValue) {
		try {
			scrollToElement(objectLocator);
			this.webElement = findWebElement(objectLocator);
			this.webElement.click();
			this.webElement.clear();
			this.webElement.sendKeys(new CharSequence[] { inputValue });
			testStepPassed("Type '" + inputValue + "' in : " + objectLocator);
		} catch (InvalidSelectorException e) {
			testStepFailed("Invalid Selector Exception occured for locator [" + objectLocator
					+ "]. Please make sure locator syntax is correct. Error:", e.getMessage());
		}
	}

	public void waitForPageToLoad() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			for (int i = 0; i < 20000; i++) {
				String status = js.executeScript("return document.readyState").toString();
				if (status.equals("complete")) {
					Thread.sleep(1000);
					break;
				}
			}
		} catch (Exception e) {
			testStepFailed("An exception occurred waitForPageToLoad() ", e.getMessage());
			e.printStackTrace();
		}
	}

	public void openNewTab() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.open()");
	}

	public void pasteIntoField(String webElement, String characters) {
		clickOn(webElement);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selectCharSet = new StringSelection(characters);
		clipboard.setContents(selectCharSet, null);
		Actions act = new Actions(driver);
		act.keyDown(Keys.CONTROL).sendKeys("v").build().perform();
		act.keyUp(Keys.CONTROL).build().perform();
	}

	public void pastePathAndAdd(String characters) throws FindFailed {
		waitTime(5);
		Screen currentScreen = new Screen(0);
		org.sikuli.script.Pattern textField = new org.sikuli.script.Pattern("./pattern/TextField.png");
		org.sikuli.script.Pattern openBtn = new org.sikuli.script.Pattern("./pattern/Open.png");

		currentScreen.wait(textField, 20);
		currentScreen.type(textField, characters);
		currentScreen.click(openBtn);
		waitForPageToLoad();
	}

	public void transferControlToWindow(int index, boolean closeAllWindow) {
		Set<String> AllHandles = driver.getWindowHandles();
		ArrayList<String> HandlesToList = new ArrayList<String>();
		HandlesToList.addAll(AllHandles);
		int windows = HandlesToList.size();
		if (windows >= 2) {
			if (index == 1 || index == 2) {
				for (int iterBrowWind = (HandlesToList.size() - 1); iterBrowWind >= (index - 1); iterBrowWind--) {
					driver.switchTo().window(HandlesToList.get(iterBrowWind));
				}
				driver.switchTo().window(HandlesToList.get(index - 1));
				if (closeAllWindow) {
					driver.close();
				}
			} else if (index <= HandlesToList.size()) {
				driver.switchTo().window(HandlesToList.get(index - 1));
				if (closeAllWindow) {
					driver.close();
				}
			}
		} else {
			driver.switchTo().window(HandlesToList.get(0));
			if (index >= 2) {
				testStepFailed("The given window \"" + index + "\" is failed to open");
			}
		}
	}

	public String getText(String objLocator) {
		waitForElement(objLocator, 20);
		this.webElement = findWebElement(objLocator);
		return webElement.getText();
	}

	public void represhPage() {
		driver.navigate().refresh();
		waitForPageToLoad();
	}

	public boolean isElementDisplayed(String objectLocator) {
		waitForElement(objectLocator, WAIT_TIME);
		try {
			this.webElement = findWebElement(objectLocator);
			if (this.webElement.isDisplayed())
				return true;
		} catch (Exception e) {
			testStepFailed("Unable to verify element displayed or not", objectLocator);
		}
		return false;
	}

	public File getVideoFileIndex(File[] fileList) {
		for (File file : fileList) {
			if (!file.isDirectory() && file.getName().contains(".mp4")) {
				return file;
			} else {

			}
		}
		return null;
	}

	public void clickOn(String objectLocator) {
		waitForElement(objectLocator, 30);
		this.webElement = findWebElement(objectLocator);
		if (findWebElement(objectLocator).isDisplayed()) {
			webElement.click();
			waitForPageToLoad();
		} else {
			scrollToElement(objectLocator);
			webElement.click();
			waitForPageToLoad();
		}
	}

	public void scrollToElement(String objectLocator) {
		WebElement element = findWebElement(objectLocator);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", element);
		waitForPageToLoad();
	}

	public void clickOnCss(String objectLocator) {
		waitForPageToLoad();
		this.webElement = findWebElementCss(objectLocator);
		Actions act = new Actions(driver);
		act.moveToElement(webElement);
		if (this.webElement.isDisplayed()) {
			webElement.click();
			waitForPageToLoad();
		}
	}

	public void clickOn(WebElement objectLocator) {
		waitForPageToLoad();
		Actions act = new Actions(driver);
		this.webElement = objectLocator;
		act.moveToElement(webElement);
		if (this.webElement.isDisplayed()) {
			webElement.click();
			waitForPageToLoad();
		}
	}

	public WebElement findWebElement(String objectLocator) {
		waitForPageToLoad();
		return driver.findElement(By.xpath(objectLocator));
	}

	public WebElement findWebElementCss(String objectLocator) {
		waitForPageToLoad();
		return driver.findElement(By.cssSelector(objectLocator));
	}

	public List<WebElement> findWebElements(String objectLocator) {
		waitForPageToLoad();
		waitForElement(objectLocator, 30);
		return driver.findElements(By.xpath(objectLocator));
	}

	public void waitForElement(String objectLocator, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(objectLocator)));
		} catch (Exception e) {
			testStepFailed("Element not found", objectLocator);
		}
	}

	public void waitForElementToBeDisplayed(String objectLocator, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOf((WebElement) By.xpath(objectLocator)));
		} catch (Exception e) {
			testStepFailed("Element not found", objectLocator);
		}
	}

	public static void writeToLogFile(String type, String message) {
		String t = type.toUpperCase();
		if (t.equalsIgnoreCase("DEBUG")) {
			System.out.println(message);
		} else if (t.equalsIgnoreCase("INFO")) {
			System.out.println(message);
		} else if (t.equalsIgnoreCase("WARN")) {
			System.err.println(message);
		} else if (t.equalsIgnoreCase("ERROR")) {
			System.err.println(message);
		} else if (t.equalsIgnoreCase("FATAL")) {
			System.err.println(message);
		} else {
			System.err.println("Invalid log Type :" + type + ". Unable to log the message.");
		}
	}

	public String getPageUrl() {
		try {
			return driver.getCurrentUrl();
		} catch (WebDriverException e) {
			writeToLogFile("ERROR", e.getMessage());
		}
		return null;
	}

	public void waitTime(long waittime) {
		writeToLogFile("INFO", "Waiting for " + waittime + " seconds...");
		try {
			Thread.sleep(waittime * 1000L);
		} catch (InterruptedException e) {
			writeToLogFile("ERROR", "Thread.sleep operation failed, during waitTime function call");
		}
	}

	public void testStepPassed(String errMessage) {
		writeToLogFile("Info", errMessage);
		System.out.println(errMessage);
	}

	public void testStepFailed(String userMessage, String execeptionMesage) {
		writeToLogFile("WARN", userMessage + ":-" + execeptionMesage);
		System.out.println(userMessage + ":-" + execeptionMesage);
		Assert.fail();
	}

	public void testStepFailed(String userMessage) {
		writeToLogFile("WARN", userMessage);
		System.out.println(userMessage);
		Assert.fail();
	}

	public void scrollPageUp() {
		try {
			waitTime(2);
			Actions act = new Actions(driver);

			act.sendKeys(Keys.PAGE_UP).build().perform();
			waitForPageToLoad();

		} catch (Exception e) {
			testStepFailed("Failed to scroll UP " + e.getClass());
		}
	}

	public void scrollPageDown() {
		waitForPageToLoad();
		try {
			waitTime(2);
			Actions act = new Actions(driver);

			act.sendKeys(Keys.PAGE_DOWN).build().perform();
			waitForPageToLoad();

		} catch (Exception e) {
			testStepFailed("Failed to scroll UP " + e.getClass());
		}
	}

	public boolean isElementPresent(String objectLocator) {
		try {
			waitForElement(objectLocator, WAIT_TIME);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			testStepFailed("Exception Error '" + e.toString() + "'");
			return false;
		}
	}

	public long pageLoadTime() {
		try {
			long domContentLoadedEventStart = getDomContentLoadedEventStart();
			long domComplete = getDomComplete();
			if (domContentLoadedEventStart != this.domContentLoadedEventStart
					&& domComplete > domContentLoadedEventStart) {
				this.domContentLoadedEventStart = domContentLoadedEventStart;
				writeToLogFile("INFO",
						"Total page load time: '" + (domComplete - domContentLoadedEventStart) + "' milli seconds");
				writeToLogFile("INFO",
						"Total page load time: '" + ((domComplete - domContentLoadedEventStart) / 1000L) + "' seconds");
				return domComplete - domContentLoadedEventStart;
			}
			return 0L;
		} catch (Exception e) {
			writeToLogFile("ERROR", "Error in getting page load wait time: " + e.getMessage());
			return 0L;
		}
	}

	private long getDomContentLoadedEventStart() {
		RemoteWebDriver remoteWebDriver = (RemoteWebDriver) this.driver;
		Object val = remoteWebDriver.executeScript("return window.performance.timing.domContentLoadedEventStart",
				new Object[0]);
		return Long.parseLong(val.toString());
	}

	private long getDomComplete() {
		RemoteWebDriver remoteWebDriver = (RemoteWebDriver) this.driver;
		Object val = remoteWebDriver.executeScript("return window.performance.timing.domComplete", new Object[0]);
		return Long.parseLong(val.toString());
	}

	public void navigateTo(String url) {
		try {
			writeToLogFile("INFO", "Navigating to URL : " + url);
			this.driver.get(url);
			waitForPageToLoad();
			writeToLogFile("INFO", "Navigation Successful : " + url);
			testStepPassed("Navigate to : " + url);
			System.out.println("Page Load Time:- " + this.pageLoadTime());
		} catch (TimeoutException e) {
			testStepFailed("Page fail to load within in " + this.pageLoadTime() + " seconds", e.toString());
		} catch (Exception e) {
			writeToLogFile("ERROR", "Browser: Open Failure/Navigation cancelled, please check the application window.");
			testStepFailed(
					"Browser: Open Failure/Navigation cancelled, please check the application window. URL: " + url,
					e.toString());
		}
	}

	@AfterSuite
	public void closeBrowser() {
		driver.manage().deleteAllCookies();
		driver.quit();
	}
}
