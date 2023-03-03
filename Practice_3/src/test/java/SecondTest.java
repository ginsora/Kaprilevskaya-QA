import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)

public class SecondTest {
    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://sandbox.cardpay.com/MI/cardpayment2.html?orderXml=PE9SREVSIFdBTExFVF9JRD0nODI5OScgT1JERVJfTlVNQkVSPSc0NTgyMTEnIEFNT1VOVD0nMjkxLjg2JyBDVVJSRU5DWT0nRVVSJyAgRU1BSUw9J2N1c3RvbWVyQGV4YW1wbGUuY29tJz4KPEFERFJFU1MgQ09VTlRSWT0nVVNBJyBTVEFURT0nTlknIFpJUD0nMTAwMDEnIENJVFk9J05ZJyBTVFJFRVQ9JzY3NyBTVFJFRVQnIFBIT05FPSc4NzY5OTA5MCcgVFlQRT0nQklMTElORycvPgo8L09SREVSPg==&sha512=998150a2b27484b776a1628bfe7505a9cb430f276dfa35b14315c1c8f03381a90490f6608f0dcff789273e05926cd782e1bb941418a9673f43c47595aa7b8b0d";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
    }

    @DataProvider   // 3-D secure version 1
    public static Object[][] data() {
        return new Object[][] {
                {"4000000000000002", "John Doe", "02", "2030", "123", "Success", "Confirmed", "VISA", "...0002"},
                {"5555555555554444", "James Smith", "10", "2025", "323", "Decline", "Declined by issuing bank", "MASTERCARD", "...4444"},
                {"4000000000000044", "Emma Watson", "12", "2038", "888", "Info", "Confirmed", "VISA", "...0044"},
                {"4000000000000085", "Victoria Cruse", "01", "2038", "756", "Info", "Confirmed", "VISA", "...0085"}
        };
    }

    @DataProvider   // 3-D secure version 2
    public static Object[][] data1() {
        return new Object[][] {
                {"4000000000000093", "Tom Hardy", "05", "2026", "492", "Success", "Confirmed", "VISA", "...0093"},
                {"4000000000000077", "Brad Pitt", "11", "2031", "574", "Success", "Confirmed", "VISA", "...0077"},
                {"5555555555554477", "Leo DiCaprio", "06", "2037", "025", "Decline", "Declined by issuing bank", "MASTERCARD", "...4477"},
                {"4000000000000051", "Quentin Tarantino", "08", "2029", "999", "Info", "Confirmed", "VISA", "...0051"}
        };
    }


    @Test
    @UseDataProvider("data") // 3-D secure version 1
    public void finalPageTesting(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc, String operationStatus, String paymentStatus, String cardType, String cardShortNum) {
        driver.get(baseUrl);
        String Order_number = (String) driver.findElement(By.id("order-number")).getText();
        String Total = (String) driver.findElement(By.id("total-amount")).getText();
        String Currency = (String) driver.findElement(By.id("currency")).getText();
        driver.findElement(By.id("input-card-number")).click();
        driver.findElement(By.id("input-card-number")).clear();
        driver.findElement(By.id("input-card-number")).sendKeys(cardNum);
        driver.findElement(By.id("input-card-holder")).click();
        driver.findElement(By.id("input-card-holder")).clear();
        driver.findElement(By.id("input-card-holder")).sendKeys(cardHolder.toUpperCase());
        driver.findElement(By.id("card-expires-month")).click();
        new Select(driver.findElement(By.id("card-expires-month"))).selectByVisibleText(cardMonth);
        driver.findElement(By.id("card-expires-year")).click();
        new Select(driver.findElement(By.id("card-expires-year"))).selectByVisibleText(cardYear);
        driver.findElement(By.id("input-card-cvc")).click();
        driver.findElement(By.id("input-card-cvc")).clear();
        driver.findElement(By.id("input-card-cvc")).sendKeys(cardCvc);
        driver.findElement(By.id("action-submit")).click();
        driver.findElement(By.id("success")).click();


        // Сравнение статуса операции
        assertEquals(operationStatus, driver.findElement(By.xpath("//*[@id=\"payment-status-title\"]/span")).getText());

        // Сравнение номера заказа
        assertEquals(Order_number, driver.findElement(By.xpath("//*[@id=\"payment-item-ordernumber\"]/div[2]")).getText());

        // Сравнение статуса платежа
        assertEquals(paymentStatus.toUpperCase(), driver.findElement(By.xpath("//*[@id=\"payment-item-status\"]/div[2]")).getText().toUpperCase());

        // Сравнение короткого номера карты
        assertEquals(cardShortNum, driver.findElement(By.xpath("//*[@id=\"payment-item-cardnumber\"]/div[2]")).getText());

        // Сравнение типа карты
        assertEquals(cardType, driver.findElement(By.xpath("//*[@id=\"payment-item-cardtype\"]/div[2]")).getText());

        // Сравнение держателя карты
        assertEquals(cardHolder.toUpperCase(), driver.findElement(By.xpath("//*[@id=\"payment-item-cardholder\"]/div[2]")).getText());

        // Сравнение валюты
        assertEquals(Currency + "   " + Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total\"]/div[2]")).getText());

        // Сравнение суммы
        assertEquals(Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total-amount\"]")).getText());
    }

    @Test
    @UseDataProvider("data1") // 3-D secure version 2
    public void finalPageTesting1(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc, String operationStatus, String paymentStatus, String cardType, String cardShortNum) {
        driver.get(baseUrl);
        String Order_number = (String) driver.findElement(By.id("order-number")).getText();
        String Total = (String) driver.findElement(By.id("total-amount")).getText();
        String Currency = (String) driver.findElement(By.id("currency")).getText();
        driver.findElement(By.id("input-card-number")).click();
        driver.findElement(By.id("input-card-number")).clear();
        driver.findElement(By.id("input-card-number")).sendKeys(cardNum);
        driver.findElement(By.id("input-card-holder")).click();
        driver.findElement(By.id("input-card-holder")).clear();
        driver.findElement(By.id("input-card-holder")).sendKeys(cardHolder.toUpperCase());
        driver.findElement(By.id("card-expires-month")).click();
        new Select(driver.findElement(By.id("card-expires-month"))).selectByVisibleText(cardMonth);
        driver.findElement(By.id("card-expires-year")).click();
        new Select(driver.findElement(By.id("card-expires-year"))).selectByVisibleText(cardYear);
        driver.findElement(By.id("input-card-cvc")).click();
        driver.findElement(By.id("input-card-cvc")).clear();
        driver.findElement(By.id("input-card-cvc")).sendKeys(cardCvc);
        driver.findElement(By.id("action-submit")).click();
        //driver.findElement(By.id("success")).click();


        // Сравнение статуса операции
        assertEquals(operationStatus, driver.findElement(By.xpath("//*[@id=\"payment-status-title\"]/span")).getText());

        // Сравнение номера заказа
        assertEquals(Order_number, driver.findElement(By.xpath("//*[@id=\"payment-item-ordernumber\"]/div[2]")).getText());

        // Сравнение статуса платежа
        assertEquals(paymentStatus.toUpperCase(), driver.findElement(By.xpath("//*[@id=\"payment-item-status\"]/div[2]")).getText().toUpperCase());

        // Сравнение короткого номера карты
        assertEquals(cardShortNum, driver.findElement(By.xpath("//*[@id=\"payment-item-cardnumber\"]/div[2]")).getText());

        // Сравнение типа карты
        assertEquals(cardType, driver.findElement(By.xpath("//*[@id=\"payment-item-cardtype\"]/div[2]")).getText());

        // Сравнение держателя карты
        assertEquals(cardHolder.toUpperCase(), driver.findElement(By.xpath("//*[@id=\"payment-item-cardholder\"]/div[2]")).getText());

        // Сравнение валюты
        assertEquals(Currency + "   " + Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total\"]/div[2]")).getText());

        // Сравнение суммы
        assertEquals(Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total-amount\"]")).getText());
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
