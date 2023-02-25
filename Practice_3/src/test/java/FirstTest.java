import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)

public class FirstTest {
    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://sandbox.cardpay.com/MI/cardpayment2.html?orderXml=PE9SREVSIFdBTExFVF9JRD0nODI5OScgT1JERVJfTlVNQkVSPSc0NTgyMTEnIEFNT1VOVD0nMjkxLjg2JyBDVVJSRU5DWT0nRVVSJyAgRU1BSUw9J2N1c3RvbWVyQGV4YW1wbGUuY29tJz4KPEFERFJFU1MgQ09VTlRSWT0nVVNBJyBTVEFURT0nTlknIFpJUD0nMTAwMDEnIENJVFk9J05ZJyBTVFJFRVQ9JzY3NyBTVFJFRVQnIFBIT05FPSc4NzY5OTA5MCcgVFlQRT0nQklMTElORycvPgo8L09SREVSPg==&sha512=998150a2b27484b776a1628bfe7505a9cb430f276dfa35b14315c1c8f03381a90490f6608f0dcff789273e05926cd782e1bb941418a9673f43c47595aa7b8b0d";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
    }

    @DataProvider   // Ввод кучи данных
    public static Object[][] data() {
        return new Object[][] {
                {"4000000000000002", "CONFIRMED"},
                {"5555555555554444", "DECLINED BY ISSUING BANK"}
        };
    }

    @Test
    @UseDataProvider("data")
    public void confirmedPayment(String cardNum, String paymentRes) {
        driver.get(baseUrl);
        String Order_number = (String) driver.findElement(By.id("order-number")).getText();
        String Unlimit_Payment_Page = (String) driver.getTitle();
        String Total = (String) driver.findElement(By.id("total-amount")).getText();
        String currency = (String) driver.findElement(By.id("currency")).getText();
        driver.findElement(By.id("input-card-number")).click();
        driver.findElement(By.id("input-card-number")).clear();
        driver.findElement(By.id("input-card-number")).sendKeys(cardNum);
        driver.findElement(By.id("input-card-holder")).click();
        driver.findElement(By.id("input-card-holder")).clear();
        driver.findElement(By.id("input-card-holder")).sendKeys("BORIS");
        driver.findElement(By.id("card-expires-month")).click();
        new Select(driver.findElement(By.id("card-expires-month"))).selectByVisibleText("01");
        driver.findElement(By.id("card-expires-year")).click();
        new Select(driver.findElement(By.id("card-expires-year"))).selectByVisibleText("2025");
        driver.findElement(By.id("input-card-cvc")).click();
        driver.findElement(By.id("input-card-cvc")).clear();
        driver.findElement(By.id("input-card-cvc")).sendKeys("123");
        driver.findElement(By.id("action-submit")).click();
        driver.findElement(By.id("success")).click();
        assertEquals(Order_number, driver.findElement(By.xpath("//*[@id=\"payment-item-ordernumber\"]/div[2]")).getText());  // Сравнение номера заказа
        assertEquals(paymentRes, driver.findElement(By.xpath("//*[@id=\"payment-item-status\"]/div[2]")).getText().toUpperCase()); // Сравнение статуса заказа
    }

    @Test  // Снятие скриншота со знака вопроса
    public void screenshotCVC() throws IOException {
        driver.get(baseUrl);
        // Сдвиг курсора на конкретный элемент
        Actions action = new Actions(driver);

        WebElement cvchint = driver.findElement(By.xpath("//*[@id=\"cvc-hint-toggle\"]")); // xpath брали из ф12

        action.moveToElement(cvchint).click().build().perform();

        // Снятие скриншота
        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);

        ImageIO.write(screenshot.getImage(), "jpg", new File("target/1.jpg"));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
