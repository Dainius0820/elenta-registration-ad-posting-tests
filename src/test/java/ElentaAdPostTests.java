import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;
import java.time.Duration;

public class ElentaAdPostTests {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public String generateRndLetters(int length) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String text = "";
        for (int i = 0; i < length; i++) {
            text += symbols.charAt((int) (Math.random() * symbols.length()));
        }
        return text;
    }

    public String generateRndNumbs(int length) {
        String symbols = "0123456789";
        String text = "";
        for (int i = 0; i < length; i++) {
            text += symbols.charAt((int) (Math.random() * symbols.length()));
        }
        return text;
    }

    public String generateRndSpecialChars(int length) {
        String symbols = "\"#$%&'()*:;<=>?[\\]^_`{|}~";
        String text = "";
        for (int i = 0; i < length; i++) {
            text += symbols.charAt((int) (Math.random()*symbols.length()));
        }
        return text;
    }

    public void acceptCookies() {
        driver.get("https://elenta.lt");
        WebElement acceptBtn = driver.findElement(By.xpath
                ("/html/body/div[3]/div[2]/div[2]/div[2]/div[2]/button[1]/p"));
        acceptBtn.click();
    }

    public void submitAdForm() {
        driver.findElement(By.id("title")).sendKeys("Audi RS3");
        driver.findElement(By.id("text")).sendKeys("Visa info telefonu");
        driver.findElement(By.id("price")).sendKeys("25000");
        driver.findElement(By.id("location-search-box")).sendKeys("Vilnius");
        driver.findElement(By.id("phone")).sendKeys("+37061111111");
        driver.findElement(By.id("email")).sendKeys("standard@gmail.com");
        driver.findElement(By.id("submit-button")).click();
    }

    public void fillAdForm(String title, String description, String price, String location, String phone, String email) {
        driver.findElement(By.id("title")).sendKeys(title);
        driver.findElement(By.id("text")).sendKeys(description);
        driver.findElement(By.id("price")).sendKeys(price);
        driver.findElement(By.id("location-search-box")).sendKeys(location);
        driver.findElement(By.id("phone")).sendKeys(phone);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("submit-button")).click();
    }

    public void uploadImages(WebDriver driver, String... imageNames) {
        StringBuilder filePaths = new StringBuilder();
        for (int i = 0; i < imageNames.length; i++) {
            File imageFile = new File("src/test/resources/images/" + imageNames[i]);
            filePaths.append(imageFile.getAbsolutePath());
            if (i < imageNames.length - 1) {
                filePaths.append("\n");
            }
        }

        WebElement fileInput = driver.findElement(By.id("inputfile"));
        fileInput.sendKeys(filePaths.toString());

        try {
            WebElement progressBar = driver.findElement(By.id("fileupload-progress"));
            wait.until(ExpectedConditions.attributeToBe(progressBar, "value", progressBar.getDomAttribute("max")));
        } catch (Exception e) {
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("fileupload-progress")));
            }catch (Exception ex){}
        }
    }

    public void removeImages(int numbOfImages) {
        for (int i = 1; i <= numbOfImages; i++) {
            By removeLocator = By.id("remove-photo-" + i);
            WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(removeLocator));
            removeBtn.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(removeLocator));
        }
    }

    public void completeAdAndDelete() {
        driver.findElement(By.id("forward-button")).click();
        driver.findElement(By.id("forward-button")).click();
        driver.findElement(By.cssSelector(".action")).click();
        driver.findElement(By.className("delete")).click();
        driver.switchTo().alert().accept();
    }

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        acceptCookies();
    }

    @BeforeMethod
    public void beforeMethod() {
        driver.get("https://elenta.lt/patalpinti/ivesti-informacija?categoryId=AutoMoto_Automobiliai&actionId=Siulo&returnurl=%2F");
        driver.findElement(By.id("title")).clear();
        driver.findElement(By.id("text")).clear();
        driver.findElement(By.id("price")).clear();
        driver.findElement(By.id("location-search-box")).clear();
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("email")).clear();
    }

    @Test
    public void positiveSubmitAdFormTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void noTitleTest() {
        fillAdForm("", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Įveskite skelbimo pavadinimą\" error to be shown");
    }

    @Test
    public void noDescriptionTest() {
        fillAdForm("Audi RS3", "", "25000", "Vilnius",
                "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("txte"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Įveskite skelbimo aprašymą\" error to be shown");
    }

    @Test
    public void noPriceTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void noCityTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void noPhoneNumbTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("ce"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Įveskite telefono numerį\" error to be shown");
    }

    @Test
    public void noEmailTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void title2CharsTest() {
        fillAdForm("" + generateRndLetters(2), "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Per trumpas skelbimo pavadinimas\" error to be shown");
    }

    @Test
    public void title3CharsTest() {
        fillAdForm("" + generateRndLetters(3), "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void title150CharsTest() {
        fillAdForm("" + generateRndLetters(150), "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void title151CharsTest() {
        fillAdForm("" + generateRndLetters(151), "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Per ilgas skelbimo pavadinimas\" error to be shown");
    }

    @Test
    public void titleContainingSpecialCharTest () {
        fillAdForm("Audi RS3" + generateRndSpecialChars(1), "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Skelbimo pavadinimas nėra tinkamas\" error to be shown");
    }

    @Test
    public void titleRepetitiveTextTest() {
        fillAdForm("AudiAudiAudi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Skelbimo pavadinimas nėra tinkamas\" error to be shown");
    }

    @Test
    public void titleStartsWithPeriodTest() {
        fillAdForm(".Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("te"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Skelbimo pavadinimas nėra tinkamas\" error to be shown");
    }

    @Test
    public void description5CharsTest() {
        fillAdForm("Audi RS3", "" + generateRndLetters(5), "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("txte"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Per trumpas skelbimo aprašymas\" error to be shown");
    }

    @Test
    public void description6CharsTest() {
        fillAdForm("Audi RS3", "" + generateRndLetters(6), "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void description65_535CharsTest() {
        fillAdForm("Audi RS3", "" + generateRndLetters(65535), "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void description65_536CharsTest() {
        fillAdForm("Audi RS3", "" + generateRndLetters(65536), "25000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("txte"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Per ilgas skelbimo aprašymas\" error to be shown");
    }

    @Test
    public void priceSetTo99Test() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "99",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("txte"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Įveskite kainą nuo 100€ iki 999999€\" error to be shown");
    }

    @Test
    public void priceSetTo100Test() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "100",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void priceSetTo999_999Test() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "999999",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void priceSetTo1_000_000Test() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "1000000",
                "Vilnius", "+37061111111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("txte"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Įveskite kainą nuo 100€ iki 999999€\" error to be shown");
    }

    @Test
    public void priceDecimalPointNumberTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000.25",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[3]/span[1]")).getDomAttribute("title");
        Assert.assertEquals(actual.replaceAll("[A-Za-z\\s€]+", ""), "25000");
    }

    @Test
    public void priceNegativeTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "-100",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[3]/span[1]")).getDomAttribute("title");
        Assert.assertEquals(actual.replaceAll("[A-Za-z\\s€]+", ""), "100");
    }

    @Test
    public void priceContainingLettersDigitsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "150k",
                "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[3]/span[1]")).getDomAttribute("title");
        Assert.assertEquals(actual.replaceAll("[A-Za-z\\s€]+", ""), "150");
    }

    @Test
    public void priceContainingSpecialCharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise",
                "15" + generateRndSpecialChars(1) + "00", "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[3]/span[1]")).getDomAttribute("title");
        Assert.assertEquals(actual.replaceAll("[A-Za-z\\s€]+", ""), "1500");
    }

    @Test
    public void priceWrittenInWordsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise",
                "over twenty grand", "Vilnius", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("price")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void city3CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vil", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void city4CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Seda", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "Seda");
    }

    @Test
    public void city19CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Kudirkos Naumiestis", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void city20CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Naujoji Nauja Akmenė", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void cityNumbsOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "" + generateRndNumbs(4), "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void cityContainingSpecialCharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                generateRndSpecialChars(1) + "Vil" + generateRndSpecialChars(1) + "nius" + generateRndSpecialChars(1),
                "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void citySpacesOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "   ", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void cityContainingSlashTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius/name", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void cityContainingAccentedCharTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilniũs", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void cityNameFakeTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Mordor", "+37061111111", "standard@gmail.com");
        String actual = driver.findElement(By.id("location-search-box")).getDomAttribute("value");
        Assert.assertEquals(actual, "");
    }

    @Test
    public void phoneNumb7DigitsAndPrefixTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+370" + generateRndNumbs(7), "standard@gmail.com");
        try {
            Thread.sleep(200);
            driver.switchTo().alert().accept();
            Assert.fail("An alert box popped up");
        }catch (Exception ignored){
            String notExpected = "display:none";
            String actual = driver.findElement(By.id("pe")).getDomAttribute("style");
            Assert.assertNotEquals(actual, notExpected);
        }
    }

    @Test
    public void phoneNumb8DigitsAndPrefixTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+370" + generateRndNumbs(8), "standard@gmail.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void phoneNumb9DigitsAndPrefixTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+370" + generateRndNumbs(9), "standard@gmail.com");
        try {
            Thread.sleep(200);
            driver.switchTo().alert().accept();
            Assert.fail("An alert box popped up");
        }catch (Exception ignored){
            String notExpected = "display:none";
            String actual = driver.findElement(By.id("pe")).getDomAttribute("style");
            Assert.assertNotEquals(actual, notExpected);
        }
    }

    @Test
    public void phoneNumbContainingLettersTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+370611111" + generateRndLetters(2).toLowerCase(), "standard@gmail.com");
        WebElement error = driver.findElement(By.id("pe"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas tel. numeris\" error to be shown");
    }

    @Test
    public void phoneNumbContainingSpecialCharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+3706111111" + generateRndSpecialChars(1), "standard@gmail.com");
        WebElement error = driver.findElement(By.id("pe"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas tel. numeris\" error to be shown");
    }

    @Test
    public void phoneNumbZeroesOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "000000000000", "standard@gmail.com");
        try {
            Thread.sleep(200);
            driver.switchTo().alert().accept();
            Assert.fail("An alert box popped up");
        }catch (Exception ignored){
            String notExpected = "display:none";
            String actual = driver.findElement(By.id("pe")).getDomAttribute("style");
            Assert.assertNotEquals(actual, notExpected);
        }
    }

    @Test
    public void phoneNumbWrongPrefixTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+38061111111", "standard@gmail.com");
        try {
            Thread.sleep(200);
            driver.switchTo().alert().accept();
            Assert.fail("An alert box popped up");
        }catch (Exception ignored){
            String notExpected = "display:none";
            String actual = driver.findElement(By.id("pe")).getDomAttribute("style");
            Assert.assertNotEquals(actual, notExpected);
        }
    }

    @Test
    public void phoneNumbContainingSpaceTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111 111", "standard@gmail.com");
        WebElement error = driver.findElement(By.id("pe"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas tel. numeris\" error to be shown");
    }

    @Test
    public void email5CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", generateRndLetters(1).toLowerCase() + "@b.c");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void email6CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", generateRndLetters(1).toLowerCase() + "@b.co");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void email254CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", generateRndLetters(118).toLowerCase() + generateRndNumbs(118) + "@exampledomain.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void email255CharsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", generateRndLetters(118).toLowerCase() + generateRndNumbs(119) + "@exampledomain.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailNumbsOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "" + generateRndNumbs(6));
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailLettersOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "" + generateRndLetters(6).toLowerCase());
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailAtSymbolOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "@");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailMultipleAtSymbolsTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@@gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailLocalAtSymbolOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailDomainAtSymbolOnlyTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "@gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailDomainWithSubdomainTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@mail.example.com");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void emailIPAddressDomainTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@123.456.789.123");
        String actual = driver.findElement(By.xpath("//*[@id=\"fileinput-label\"]/span")).getText();
        Assert.assertEquals(actual, "įkelkite nuotraukas");
    }

    @Test
    public void emailStartsWithPeriodTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", ".standard@gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailEndsWithPeriodTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail.com.");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailDomainContainingCommaTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail,com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void email2ConsecutivePeriodTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@gmail..com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailLocalSpecialCharTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard" + generateRndSpecialChars(1) + "@gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailSpecialCharAfterAtSymbolTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "standard@" + generateRndSpecialChars(1) + "gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailQuotedLocalTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "\"standard\"@gmail.com");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void emailInternationalizedTest() {
        fillAdForm("Audi RS3", "Selling this beauty, because my neighbors can’t handle the noise", "25000",
                "Vilnius", "+37061111111", "ユーザー@例え.テスト");
        WebElement error = driver.findElement(By.id("ee"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Blogas el. pašto adresas\" error to be shown");
    }

    @Test
    public void photoUploadPositiveTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.jpg");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-1\"]/div/img")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photoFormatPDFTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.pdf");
        WebElement error = driver.findElement(By.id("fileupload-message"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Netinkamas nuotraukos formatas\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photoFormatTIFFTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.tiff");
        WebElement error = driver.findElement(By.id("fileupload-message"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Netinkamas nuotraukos formatas\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photoFormatGIFTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.gif");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-1\"]/div/img")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photoFormatPNGTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.png");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-1\"]/div/img")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photoFormatBMPTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.bmp");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-1\"]/div/img")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photoFormatMP4Test() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.mp4");
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileupload-message")));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Netinkamas nuotraukos formatas\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photoFormatDOCXTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.docx");
        WebElement error = driver.findElement(By.id("fileupload-message"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Netinkamas nuotraukos formatas\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photoUploadRemoveTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.jpg");
        removeImages(1);
        driver.findElement(By.id("forward-button")).click();
        String actual = driver.findElement(By.xpath("/html/body/div[1]/ul/li/a/img")).getDomAttribute("src");
        Assert.assertEquals(actual, "https://s.elenta.lt/items/na-2.jpg");
        driver.findElement(By.id("forward-button")).click();
        driver.findElement(By.cssSelector(".action")).click();
        driver.findElement(By.className("delete")).click();
        driver.switchTo().alert().accept();
    }

    @Test
    public void photo5MBTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3_5mb.jpg");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-1\"]/div/img")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photo6MBTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3_6mb.jpg");
        WebElement error = driver.findElement(By.id("fileupload-message"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Netinkamas nuotraukos dydis\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photos8Test() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.jpg", "audi_rs3_5mb.jpg", "audi_rs3.gif", "audi_rs3.png",
                "audi_rs3.bmp", "audi_rs3_1mb.jpg", "audi_rs3_2mb.jpg", "audi_rs3_3mb.jpg");
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"photo-8\"]")));
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.fail();
        }
        completeAdAndDelete();
    }

    @Test
    public void photos9Test() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.jpg", "audi_rs3_5mb.jpg", "audi_rs3.gif", "audi_rs3.png",
                "audi_rs3.bmp", "audi_rs3_1mb.jpg", "audi_rs3_2mb.jpg", "audi_rs3_3mb.jpg","audi_rs3_4mb.jpg");
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileupload-message")));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Negalima įkelti daugiau nei 8 nuotraukas\" error to be shown");
        completeAdAndDelete();
    }

    @Test
    public void photoDuplicatesTest() {
        submitAdForm();
        uploadImages(driver, "audi_rs3.jpg", "audi_rs3.jpg");
        WebElement error = driver.findElement(By.id("fileupload-message"));
        Assert.assertTrue(error.isDisplayed(), "Expected the \"Negalima įkelti daugiau nei 1 tos pačios nuotraukos kopiją\" error to be shown");
        completeAdAndDelete();
    }
}