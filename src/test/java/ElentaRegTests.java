import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class ElentaRegTests {

    public static WebDriver driver;
    public static String pass64Chars = generateRndLetters(32).toLowerCase() + generateRndNumbs(32);
    public static String pass65Chars = generateRndLetters(32).toLowerCase() + generateRndNumbs(33);
    public static String specialChar = generateRndSpecialChars(1);
    public static String passNumbsOnly = generateRndNumbs(6);
    public static String passLettersOnly = generateRndLetters(6).toLowerCase();
    public static String rndLetter = generateRndLetters(1);

    public static String generateRndLetters(int length) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String text = "";
        for (int i = 0; i < length; i++) {
            text += symbols.charAt((int) (Math.random()*symbols.length()));
        }
        return text;
    }

    public static String generateRndNumbs(int length) {
        String symbols = "0123456789";
        String text = "";
        for (int i = 0; i < length; i++) {
            text += symbols.charAt((int) (Math.random()*symbols.length()));
        }
        return text;
    }

    public static String generateRndSpecialChars(int length) {
        String symbols = "!\"#$%&'()*+,-.:;<=>?[\\]^_`{|}~";
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

    public void fillRegForm(String userName, String email, String password, String password2){
        driver.findElement(By.id("UserName")).sendKeys(userName);
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("Password2")).sendKeys(password2);
        driver.findElement(By.xpath("//*[@id=\"main-container\"]/form/fieldset/table/tbody/tr[11]/td[2]/input")).click();
    }

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        acceptCookies();
    }

    @BeforeMethod
    public void beforeMethod() {
        driver.get("https://elenta.lt/registracija");
        driver.findElement(By.id("UserName")).clear();
        driver.findElement(By.id("Email")).clear();
        driver.findElement(By.id("Password")).clear();
        driver.findElement(By.id("Password2")).clear();
    }

    @Test
    public void positiveRegistrationFormTest() {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void noUsernameTest() {
        fillRegForm("", "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Įveskite vartotojo vardą.");
    }

    @Test
    public void noEmailTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Įveskite el. pašto adresą.");
    }

    @Test
    public void noPasswordTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Įveskite slaptažodį.");
    }

    @Test
    public void noPassword2Test () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Pakartotinai neįvedėte slaptažodžio.");
    }

    @Test
    public void username2CharsTest () {
        fillRegForm("" + generateRndLetters(2).toLowerCase(),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Per trumpas vartotojo vardas");
    }

    @Test
    public void username3CharsTest () {
        fillRegForm("" + generateRndLetters(3).toLowerCase(), "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void username64CharsTest () {
        fillRegForm("" + generateRndLetters(32).toLowerCase() + generateRndNumbs(32),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void username65CharsTest () {
        fillRegForm("" + generateRndLetters(32).toLowerCase() + generateRndNumbs(33),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Per ilgas vartotojo vardas");
    }

    @Test
    public void usernameUpperCaseLetterTest () {
        fillRegForm(generateRndLetters(1) + "tandard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Vartotojo varde negali būti didžiųjų raidžių");
    }

    @Test
    public void usernameContainsSpecialCharTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)) + generateRndSpecialChars(1),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Vartotojo varde negali būti specialiųjų simbolių");
    }

    @Test
    public void usernameContainsSpaceTest () {
        fillRegForm("stan" + " dard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Vartotojo varde negali būti tarpų");
    }

    @Test
    public void password5CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secr1", "secr1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Įvestas slaptažodis per trumpas.");
    }

    @Test
    public void password6CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secre1", "secre1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void password64CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", pass64Chars, pass64Chars);
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void password65CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", pass65Chars, pass65Chars);
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Įvestas slaptažodis per ilgas.");
    }

    @Test
    public void passwordContainsSpecialCharTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "secret1" + specialChar, "secret1" + specialChar);
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Slaptažodyje negali būti specialiųjų simbolių.");
    }

    @Test
    public void passwordContainsSpaceTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", "sec" + " ret1", "sec" + " ret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Slaptažodyje negali būti tarpų.");
    }

    @Test
    public void passwordLettersOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", passLettersOnly, passLettersOnly);
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Slaptažodį turi sudaryti raidės (a-z) ir skaičiai (0-9).");
    }

    @Test
    public void passwordNumbsOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", passNumbsOnly, passNumbsOnly);
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Slaptažodį turi sudaryti raidės (a-z) ir skaičiai (0-9).");
    }

    @Test
    public void passContainsUpperCaseLetterTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "standard" + (100 + (int)(Math.random() * 9900)) + "@gmail.com", rndLetter + "ecret1", rndLetter + "ecret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Slaptažodyje negali būti didžiųjų raidžių.");
    }

    @Test
    public void email5CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(1).toLowerCase() + "@b.c", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");

    }

    @Test
    public void email6CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(2).toLowerCase() + "@b.c", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void email254CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(118) + generateRndNumbs(118) + "@exampledomain.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void email255CharsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(118) + generateRndNumbs(119) + "@exampledomain.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailNumbsOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "" + generateRndNumbs(8), "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailLettersOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "" + generateRndLetters(8).toLowerCase(), "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailAtSymbolOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "@", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailMultipleAtSymbolsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailLocalAtSymbolOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailDomainAtSymbolOnlyTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailDomainWithSubdomainTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@mail.example.com", "secret1", "secret1");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void emailIPAddressDomainTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@123.456.789.123", "secret1", "secret1");
        String actual = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[2]/h1/b")).getText();
        Assert.assertEquals(actual, "Jūs sėkmingai prisiregistravote!");
    }

    @Test
    public void emailStartsWithPeriodTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "." + generateRndLetters(8).toLowerCase() + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailEndsWithPeriodTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@gmail.com.", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailDomainContainingCommaTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@gmail,com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void email2ConsecutivePeriodsTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@gmail..com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailLocalSpecialCharTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + generateRndSpecialChars(1) + "@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailSpecialCharAfterAtSymbolTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                generateRndLetters(8).toLowerCase() + "@" + generateRndSpecialChars(1) + "gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailQuotedLocalTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)),
                "\"" + generateRndLetters(8).toLowerCase() + "\"@gmail.com", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }

    @Test
    public void emailInternationalizedTest () {
        fillRegForm("standard" + (100 + (int)(Math.random() * 9900)), "ユーザー@例え.テスト", "secret1", "secret1");
        String actual = "";
        try{
            actual = driver.findElement(By.className("field-validation-error")).getText();
        }catch (Exception e){}
        Assert.assertEquals(actual, "El. pašto adresas nėra tinkamas.");
    }
}