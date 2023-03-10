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

public class FirstTest {
    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://sandbox.cardpay.com/MI/cardpayment2.html?orderXml=PE9SREVSIFdBTExFVF9JRD0nODI5OScgT1JERVJfTlVNQkVSPSc0NTgyMTEnIEFNT1VOVD0nMjkxLjg2JyBDVVJSRU5DWT0nRVVSJyAgRU1BSUw9J2N1c3RvbWVyQGV4YW1wbGUuY29tJz4KPEFERFJFU1MgQ09VTlRSWT0nVVNBJyBTVEFURT0nTlknIFpJUD0nMTAwMDEnIENJVFk9J05ZJyBTVFJFRVQ9JzY3NyBTVFJFRVQnIFBIT05FPSc4NzY5OTA5MCcgVFlQRT0nQklMTElORycvPgo8L09SREVSPg==&sha512=998150a2b27484b776a1628bfe7505a9cb430f276dfa35b14315c1c8f03381a90490f6608f0dcff789273e05926cd782e1bb941418a9673f43c47595aa7b8b0d";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @DataProvider
    public static Object[][] data() {  // Valid data
        return new Object[][] {
                {"4000000000000002", "John Doe", "02", "2030", "123"},
                {"5555555555554444", "James Smith", "10", "2025", "323"},
                {"4000000000000044", "Emma Watson", "12", "2038", "888"},
        };
    }

    @DataProvider // Card number check. Invalid data.
    public static Object[][] data1() {
        return new Object[][] {
                {"40000000000000020", "John Doe", "02", "2030", "123"},
                {"0000000000000000",  "John Doe", "02", "2030", "123"},
                {"4000",  "John Doe", "02", "2030", "123"},
                {"djdjljlwkwejlfkl",  "John Doe", "02", "2030", "123"},
                {"@@!#@#@!#$@$!@#@",  "John Doe", "02", "2030", "123"}
        };
    }

    @DataProvider // Card holder name check. Invalid data.
    public static Object[][] data2() {
        return new Object[][] {
                {"4000000000000002",  "John", "02", "2030", "123"},
                {"4000000000000002",  "JohnDoe", "02", "2030", "123"},
                {"4000000000000002",  "123445", "02", "2030", "888"},
                {"4000000000000002",  "@#$!", "02", "2030", "123"},
                {"4000000000000002",  "Иван Петров", "02", "2030", "123"}
        };
    }

    @DataProvider // Month and year check. Invalid data.
    public static Object[][] data3() {
        return new Object[][] {
                {"4000000000000002",  "John Doe", "02", "2022", "123"},
                {"4000000000000002",  "John Doe", "month", "year", "123"},
                {"4000000000000002",  "John Doe", "@@", "!@#$", "123"},
                {"4000000000000002",  "John Doe", "05", "2044", "123"},
                {"4000000000000002",  "John Doe", "7", "2044", "123"},
                {"4000000000000002",  "John Doe", "13", "2030", "123"}
        };
    }

    @DataProvider // CVC check. Invalid data.
    public static Object[][] data4() {
        return new Object[][] {
                {"4000000000000002",  "John Doe", "02", "2030", "1"},
                {"4000000000000002",  "John Doe", "02", "2030", "1234"},
                {"4000000000000002",  "John Doe", "02", "2030", "000"},
                {"4000000000000002",  "John Doe", "02", "2030", "cvccode"},
                {"4000000000000002",  "John Doe", "02", "2030", "!@#"}
        };
    }

    @Test
    @UseDataProvider("data")  // Valid data
    public void correctDataTesting(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc) {
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

        // Сравнение номера заказа
        assertEquals(Order_number, driver.findElement(By.xpath("//*[@id=\"payment-item-ordernumber\"]/div[2]")).getText());

        // Сравнение держателя карты
        assertEquals(cardHolder.toUpperCase(), driver.findElement(By.xpath("//*[@id=\"payment-item-cardholder\"]/div[2]")).getText());

        // Сравнение валюты
        assertEquals(Currency + "   " + Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total\"]/div[2]")).getText());

        // Сравнение суммы
        assertEquals(Total, driver.findElement(By.xpath("//*[@id=\"payment-item-total-amount\"]")).getText());
    }

    @Test
    @UseDataProvider("data1")  // Invalid card numbers
    public void cardNumberTest(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc) {
        driver.get(baseUrl);
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

        if ((cardNum == "djdjljlwkwejlfkl") || (cardNum == "@@!#@#@!#$@$!@#@")) {
            assertEquals("Card number is required", driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText());
        }
        else {
            assertEquals("Card number is not valid", driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText());
        }
    }

    @Test
    @UseDataProvider("data2")  // Invalid cardholder names
    public void cardHolderNameTest(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc) {
        driver.get(baseUrl);
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

        assertEquals("Cardholder name is not valid", driver.findElement(By.xpath("//*[@id=\"card-holder-field\"]/div/label")).getText());
    }

    @Test
    @UseDataProvider("data3")  // Invalid month and year
    public void monthYearTest(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc) {
        driver.get(baseUrl);
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

        assertEquals("Expiration Date is not valid", driver.findElement(By.xpath("//*[@id=\"card-expires-field\"]/div/label")).getText());
    }

    @Test
    @UseDataProvider("data4")  // Invalid cvc code
    public void cvcTest(String cardNum, String cardHolder, String cardMonth, String cardYear, String cardCvc) {
        driver.get(baseUrl);
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

        if ((cardCvc == "cvccode") || (cardCvc == "!@#")) {
            assertEquals("CVV2/CVC2/CAV2 is required", driver.findElement(By.xpath("//*[@id=\"card-cvc-field\"]/div/label")).getText());
        }
        else {
            assertEquals("CVV2/CVC2/CAV2 is not valid", driver.findElement(By.xpath("//*[@id=\"card-cvc-field\"]/div/label")).getText());
        }
    }

    @Test // Empty form
    public void emptyFormTest() {
        driver.get(baseUrl);
        driver.findElement(By.id("action-submit")).click();

        assertEquals("Card number is required", driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText());

        assertEquals("Cardholder name is required", driver.findElement(By.xpath("//*[@id=\"card-holder-field\"]/div/label")).getText());

        assertEquals("Expiration Date is required", driver.findElement(By.xpath("//*[@id=\"card-expires-field\"]/div/label")).getText());

        assertEquals("CVV2/CVC2/CAV2 is required", driver.findElement(By.xpath("//*[@id=\"card-cvc-field\"]/div/label")).getText());
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
