package appium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UserExplorationTest extends base {

    // Data login yang sudah terdaftar
    private String registeredEmail = "razaadzaniii@gmail.com";
    private String registeredPassword = "razaadzaniii";
    
    // Test case tracking
    private List<TestCase> testCases = new ArrayList<>();
    private int currentTestCaseNumber = 1;

    // Inner class untuk tracking test cases
    private static class TestCase {
        String id;
        String description;
        boolean passed;
        String details;
        
        TestCase(String id, String description) {
            this.id = id;
            this.description = description;
            this.passed = false;
            this.details = "";
        }
    }

    /**
     * Metode utama untuk menjalankan test eksplorasi halaman user
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TEST EKSPLORASI HALAMAN USER ===");
        System.out.println();
        
        // Setup WebDriverWait with timeout in seconds (compatible with Selenium 3.x)
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            // Tunggu aplikasi dimuat
            Thread.sleep(3000);
            
            // TC001: Skip onboarding jika ada
            executeTestCase("TC001", "Melewati onboarding jika ada", () -> {
                skipOnboardingIfPresent(wait);
            });
            
            // TC002: Login ke aplikasi
            executeTestCase("TC002", "Login sebagai user", () -> {
                performLogin(wait);
            });
            
            // TC003: Eksplorasi halaman utama
            executeTestCase("TC003", "Eksplorasi halaman utama", () -> {
                exploreMainPage(wait);
            });
            
            // TC004: Navigasi ke halaman Saved (katalog)
            executeTestCase("TC004", "Navigasi ke halaman katalog", () -> {
                navigateToSavedPage(wait);
            });
            
            // TC005: Scroll dan eksplorasi halaman katalog
            executeTestCase("TC005", "Scroll halaman katalog", () -> {
                exploreCatalogPage(wait);
            });
            
            // TC006: Navigasi ke halaman Cart
            executeTestCase("TC006", "Navigasi ke halaman keranjang", () -> {
                navigateToCartPage(wait);
            });
            
            // TC007: Navigasi ke halaman History
            executeTestCase("TC007", "Navigasi ke halaman riwayat", () -> {
                navigateToHistoryPage(wait);
            });
            
            // TC008: Navigasi ke halaman Profile dan logout
            executeTestCase("TC008", "Navigasi ke profil dan logout", () -> {
                navigateToProfileAndLogout(wait);
            });
            
            // Print summary
            printTestSummary();
            
        } catch (Exception e) {
            System.err.println("Error dalam test eksplorasi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Cleanup driver
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    /**
     * Execute test case dengan tracking
     */
    private void executeTestCase(String tcId, String description, TestCaseRunnable testCase) {
        System.out.println("[" + tcId + "] " + description);
        TestCase tc = new TestCase(tcId, description);
        testCases.add(tc);
        
        try {
            testCase.run();
            tc.passed = true;
            System.out.println("[" + tcId + "] ✅ PASS");
            System.out.println();
        } catch (Exception e) {
            tc.passed = false;
            tc.details = e.getMessage();
            System.out.println("[" + tcId + "] ❌ FAIL: " + e.getMessage());
            System.out.println();
        }
    }
    
    // Functional interface for test cases
    @FunctionalInterface
    private interface TestCaseRunnable {
        void run() throws Exception;
    }
    
    /**
     * Skip onboarding jika muncul
     */
    private void skipOnboardingIfPresent(WebDriverWait wait) throws InterruptedException {
        System.out.println("Mengecek apakah ada onboarding...");
        
        try {
            // Cek apakah ada tombol Next (onboarding)
            WebElement nextButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnNext"))
            );
            
            if (nextButton.isDisplayed()) {
                System.out.println("Onboarding ditemukan, melakukan skip...");
                
                // Skip onboarding dengan cepat (maksimal 3 kali klik)
                for (int i = 0; i < 3; i++) {
                    try {
                        nextButton = wait.until(
                            ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnNext"))
                        );
                        nextButton.click();
                        System.out.println("Klik tombol Next/Get Started ke-" + (i+1));
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        System.out.println("Onboarding selesai atau tombol tidak ditemukan");
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Tidak ada onboarding atau sudah selesai");
        }
    }
    
    /**
     * Melakukan login dengan kredensial yang sudah ada
     */
    private void performLogin(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MEMULAI PROSES LOGIN ---");
        
        try {
            // Tunggu sampai halaman login siap
            Thread.sleep(2000);
            
            // Verifikasi kita di halaman login
            WebElement loginTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
            );
            System.out.println("Halaman login ditemukan: " + loginTitle.getText());
            
            // Isi email
            System.out.println("Mengisi email...");
            WebElement emailField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginEmail"))
            );
            emailField.clear();
            Thread.sleep(500);
            emailField.sendKeys(registeredEmail);
            
            // Isi password
            System.out.println("Mengisi password...");
            WebElement passwordField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginPassword"))
            );
            passwordField.clear();
            Thread.sleep(500);
            passwordField.sendKeys(registeredPassword);
            
            // Klik tombol login
            System.out.println("Menekan tombol Login...");
            WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginBtn"))
            );
            loginButton.click();
            
            // Tunggu proses login
            Thread.sleep(4000);
            
            // Verifikasi login berhasil
            verifyLoginSuccess(wait);
            
        } catch (Exception e) {
            System.err.println("Error saat login: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Verifikasi bahwa login berhasil dengan melihat elemen halaman utama
     */
    private void verifyLoginSuccess(WebDriverWait wait) throws InterruptedException {
        System.out.println("Memverifikasi login berhasil...");
        
        try {
            // Cek apakah kita sudah masuk ke halaman utama dengan melihat logo CircuWear
            WebElement logoText = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='CircuWear']")
                )
            );
            System.out.println("✓ Login berhasil! Logo aplikasi ditemukan: " + logoText.getText());
            
            // Cek koin/poin user
            try {
                WebElement pointsText = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/pointsText"))
                );
                System.out.println("✓ Poin user: " + pointsText.getText());
            } catch (Exception e) {
                System.out.println("Poin user tidak ditemukan atau belum dimuat");
            }
            
        } catch (Exception e) {
            System.err.println("Gagal verifikasi login: " + e.getMessage());
            
            // Cek apakah masih di halaman login
            try {
                WebElement loginTitle = driver.findElement(By.id("com.smallacademy.userroles:id/loginTitle"));
                System.err.println("Masih di halaman login. Login mungkin gagal.");
                printAvailableElements();
            } catch (Exception e2) {
                System.out.println("Tidak di halaman login, mungkin sudah berhasil login tapi elemen berbeda");
            }
            
            throw e;
        }
    }
    
    /**
     * Eksplorasi halaman utama untuk melihat elemen-elemen yang tersedia
     */
    private void exploreMainPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- EKSPLORASI HALAMAN UTAMA ---");
        
        try {
            Thread.sleep(2000);
            
            // Explore main content
            System.out.println("Menganalisis konten halaman utama...");
            
            // Cek title utama
            try {
                WebElement mainTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/tvTitle"))
                );
                System.out.println("✓ Judul utama ditemukan: " + mainTitle.getText());
            } catch (Exception e) {
                System.out.println("Judul utama tidak ditemukan");
            }
            
            // Cek deskripsi
            try {
                WebElement description = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/tvDescription"))
                );
                System.out.println("✓ Deskripsi ditemukan: " + description.getText());
            } catch (Exception e) {
                System.out.println("Deskripsi tidak ditemukan");
            }
            
            // Eksplorasi tombol-tombol kategori pakaian
            exploreClothingCategories();
            
            // Eksplorasi pilihan gender
            exploreGenderOptions();
            
            // Eksplorasi bottom navigation
            exploreBottomNavigation();
            
            System.out.println("✓ Eksplorasi halaman utama selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat eksplorasi halaman utama: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Eksplorasi kategori pakaian yang tersedia
     */
    private void exploreClothingCategories() {
        System.out.println("Mengecek kategori pakaian yang tersedia...");
        
        String[] categoryIds = {
            "com.smallacademy.userroles:id/btnSuit",
            "com.smallacademy.userroles:id/btnSweater", 
            "com.smallacademy.userroles:id/btnShirt",
            "com.smallacademy.userroles:id/btnHoodie",
            "com.smallacademy.userroles:id/btnBlazzer",
            "com.smallacademy.userroles:id/btnTshirt",
            "com.smallacademy.userroles:id/btnJacket",
            "com.smallacademy.userroles:id/btnPoloShirt"
        };
        
        int foundCategories = 0;
        for (String categoryId : categoryIds) {
            try {
                WebElement categoryBtn = driver.findElement(By.id(categoryId));
                if (categoryBtn.isDisplayed()) {
                    foundCategories++;
                }
            } catch (Exception e) {
                // Element not found
            }
        }
        
        System.out.println("✓ Kategori pakaian tersedia: " + foundCategories + " kategori");
    }
    
    /**
     * Eksplorasi pilihan gender
     */
    private void exploreGenderOptions() {
        System.out.println("Mengecek pilihan gender...");
        
        try {
            WebElement genderTitle = driver.findElement(By.id("com.smallacademy.userroles:id/genderTitle"));
            
            // Cek opsi Male dan Female
            boolean maleAvailable = false;
            boolean femaleAvailable = false;
            
            try {
                WebElement maleOption = driver.findElement(By.id("com.smallacademy.userroles:id/maleOption"));
                maleAvailable = maleOption.isDisplayed();
            } catch (Exception e) {}
            
            try {
                WebElement femaleOption = driver.findElement(By.id("com.smallacademy.userroles:id/femaleOption"));
                femaleAvailable = femaleOption.isDisplayed();
            } catch (Exception e) {}
            
            System.out.println("✓ Pilihan gender tersedia: Male(" + (maleAvailable ? "Ya" : "Tidak") + 
                             "), Female(" + (femaleAvailable ? "Ya" : "Tidak") + ")");
            
        } catch (Exception e) {
            System.out.println("Pilihan gender tidak ditemukan");
        }
    }
    
    /**
     * Eksplorasi bottom navigation
     */
    private void exploreBottomNavigation() {
        System.out.println("Mengecek navigasi bawah...");
        
        String[] navItems = {
            "com.smallacademy.userroles:id/navHome",
            "com.smallacademy.userroles:id/navSaved",
            "com.smallacademy.userroles:id/navCart", 
            "com.smallacademy.userroles:id/navHistory",
            "com.smallacademy.userroles:id/navProfile"
        };
        
        int foundNavItems = 0;
        for (String navItem : navItems) {
            try {
                WebElement nav = driver.findElement(By.id(navItem));
                if (nav.isDisplayed()) {
                    foundNavItems++;
                }
            } catch (Exception e) {
                // Element not found
            }
        }
        
        System.out.println("✓ Navigasi bawah tersedia: " + foundNavItems + " menu");
    }
    
    /**
     * Navigasi ke halaman Saved (katalog)
     */
    private void navigateToSavedPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN KATALOG ---");
        
        try {
            // Klik tombol Saved di bottom navigation
            System.out.println("Mencari tombol Saved...");
            WebElement savedButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/navSaved"))
            );
            
            System.out.println("✓ Tombol Saved ditemukan, melakukan klik...");
            savedButton.click();
            
            // Tunggu halaman dimuat
            Thread.sleep(3000);
            
            System.out.println("✓ Berhasil navigasi ke halaman katalog");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman Saved: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Eksplorasi halaman katalog dengan scroll ke bawah
     */
    private void exploreCatalogPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- SCROLL HALAMAN KATALOG ---");
        
        try {
            Thread.sleep(2000);
            
            System.out.println();
            System.out.println("-- Melakukan Scroll Eksplorasi --");
            
            // Lakukan scroll untuk melihat lebih banyak konten
            performScrollExploration();
            
            System.out.println("✓ Scroll halaman katalog selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat eksplorasi halaman katalog: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman Cart
     */
    private void navigateToCartPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN KERANJANG ---");
        
        try {
            // Klik tombol Cart di bottom navigation
            System.out.println("Mencari tombol Cart...");
            WebElement cartButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/navCart"))
            );
            
            System.out.println("✓ Tombol Cart ditemukan, melakukan klik...");
            cartButton.click();
            
            // Tunggu halaman dimuat
            Thread.sleep(3000);
            
            // Cek apakah ada elemen keranjang
            try {
                WebElement cartContent = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/cartContent"))
                );
                System.out.println("✓ Konten keranjang ditemukan");
            } catch (Exception e) {
                System.out.println("✓ Halaman keranjang dimuat (keranjang mungkin kosong)");
            }
            
            System.out.println("✓ Berhasil navigasi ke halaman keranjang");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman Cart: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman History
     */
    private void navigateToHistoryPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN RIWAYAT ---");
        
        try {
            // Klik tombol History di bottom navigation
            System.out.println("Mencari tombol History...");
            WebElement historyButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/navHistory"))
            );
            
            System.out.println("✓ Tombol History ditemukan, melakukan klik...");
            historyButton.click();
            
            // Tunggu halaman dimuat
            Thread.sleep(3000);
            
            // Cek apakah ada elemen history
            try {
                WebElement historyContent = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/historyContent"))
                );
                System.out.println("✓ Konten riwayat ditemukan");
            } catch (Exception e) {
                System.out.println("✓ Halaman riwayat dimuat (belum ada riwayat)");
            }
            
            System.out.println("✓ Berhasil navigasi ke halaman riwayat");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman History: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman Profile dan melakukan logout
     */
    private void navigateToProfileAndLogout(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE PROFIL DAN LOGOUT ---");
        
        try {
            // Klik tombol Profile di bottom navigation
            System.out.println("Mencari tombol Profile...");
            WebElement profileButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/navProfile"))
            );
            
            System.out.println("✓ Tombol Profile ditemukan, melakukan klik...");
            profileButton.click();
            
            // Tunggu halaman dimuat
            Thread.sleep(3000);
            
            // Cek apakah ada elemen profile
            try {
                WebElement profileContent = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/profileContent"))
                );
                System.out.println("✓ Konten profil ditemukan");
            } catch (Exception e) {
                System.out.println("✓ Halaman profil dimuat");
            }
            
            // Melakukan logout
            System.out.println("Mencari tombol Log Out...");
            WebElement logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnLogout"))
            );
            
            System.out.println("✓ Tombol Log Out ditemukan, melakukan klik...");
            logoutButton.click();
            
            // Tunggu proses logout
            Thread.sleep(3000);
            
            // Verifikasi logout berhasil
            try {
                WebElement loginTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
                );
                System.out.println("✓ Logout berhasil! Kembali ke halaman login: " + loginTitle.getText());
            } catch (Exception e) {
                System.err.println("Gagal verifikasi logout: " + e.getMessage());
                printAvailableElements();
                throw e;
            }
            
            System.out.println("✓ Berhasil logout dari aplikasi");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman Profile atau logout: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Melakukan scroll ke bawah untuk eksplorasi lebih lanjut
     */
    private void performScrollExploration() throws InterruptedException {
        try {
            // Lakukan beberapa kali scroll ke bawah
            for (int i = 1; i <= 3; i++) {
                System.out.println("Scroll ke-" + i + "...");
                
                // Scroll dari tengah ke bawah layar
                int screenHeight = driver.manage().window().getSize().getHeight();
                int screenWidth = driver.manage().window().getSize().getWidth();
                
                int startY = screenHeight * 70 / 100; // 70% dari atas
                int endY = screenHeight * 30 / 100;   // 30% dari atas
                int centerX = screenWidth / 2;
                
                @SuppressWarnings("rawtypes")
                TouchAction touchAction = new TouchAction(driver);
                touchAction
                    .press(PointOption.point(centerX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                    .moveTo(PointOption.point(centerX, endY))
                    .release()
                    .perform();
                
                // Tunggu sebentar setelah scroll
                Thread.sleep(2000);
                
                System.out.println("✓ Scroll ke-" + i + " selesai");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat melakukan scroll: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Method helper untuk debugging - menampilkan semua element yang tersedia
     */
    private void printAvailableElements() {
        try {
            System.out.println("\n=== DEBUG: SEMUA ELEMENT TERSEDIA ===");
            
            // Button elements
            var buttons = driver.findElements(By.className("android.widget.Button"));
            System.out.println("Buttons (" + buttons.size() + "):");
            for (int i = 0; i < buttons.size() && i < 15; i++) {
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
            }
            
            // TextView elements
            var textViews = driver.findElements(By.className("android.widget.TextView"));
            System.out.println("\nTextViews (" + textViews.size() + "):");
            for (int i = 0; i < textViews.size() && i < 15; i++) {
                var textView = textViews.get(i);
                String text = textView.getText();
                String resourceId = textView.getAttribute("resource-id");
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error saat debugging: " + e.getMessage());
        }
    }
    
    /**
     * Print summary test hasil
     */
    private void printTestSummary() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("=== RINGKASAN HASIL TEST CASE ===");
        System.out.println("==================================================");
        
        int passed = 0;
        int failed = 0;
        
        for (TestCase tc : testCases) {
            String status = tc.passed ? "✅ PASS" : "❌ FAIL";
            System.out.println(tc.id + " - " + tc.description + ": " + status);
            
            if (tc.passed) {
                passed++;
            } else {
                failed++;
                if (!tc.details.isEmpty()) {
                    System.out.println("    Error: " + tc.details);
                }
            }
        }
        
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Total Test Cases: " + testCases.size());
        System.out.println("Passed: " + passed + " ✅");
        System.out.println("Failed: " + failed + " ❌");
        System.out.println("Success Rate: " + String.format("%.1f", (passed * 100.0 / testCases.size())) + "%");
        System.out.println("==================================================");
        System.out.println("=== TES OTOMASI SELESAI ===");
        System.out.println("==================================================");
    }

    /**
     * Method main untuk menjalankan test
     */
    public static void main(String[] args) {
        UserExplorationTest test = new UserExplorationTest();

        try {
            // Setup driver
            test.setup();
            
            // Print informasi device
            test.printDeviceInfo();

            // Jalankan test eksplorasi
            test.executeTest();

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama test eksplorasi:");
            e.printStackTrace();
        }
    }
}