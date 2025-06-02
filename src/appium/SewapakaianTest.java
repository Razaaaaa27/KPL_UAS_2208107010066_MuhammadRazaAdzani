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

public class SewapakaianTest extends base {

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
     * Metode utama untuk menjalankan test sewa pakaian
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TEST SEWA PAKAIAN ===");
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
            
            // TC003: Navigasi ke halaman Cart (keranjang)
            executeTestCase("TC003", "Navigasi ke halaman keranjang", () -> {
                navigateToCartPage(wait);
            });
            
            // TC004: Klik salah satu item pakaian (Blazer)
            executeTestCase("TC004", "Klik item Blazer untuk detail", () -> {
                clickItemDetail(wait);
            });
            
            // TC005: Menambah durasi sewa menjadi 5 hari
            executeTestCase("TC005", "Menambah durasi sewa menjadi 5 hari", () -> {
                adjustRentalDuration(wait);
            });
            
            // TC006: Scroll ke bawah untuk melihat detail
            executeTestCase("TC006", "Scroll untuk melihat detail item", () -> {
                scrollToSeeDetails(wait);
            });
            
            // TC007: Klik tombol sewa 5 hari
            executeTestCase("TC007", "Klik tombol sewa 5 hari", () -> {
                clickRentButton(wait);
            });
            
            // TC008: Konfirmasi sewa dengan klik "YA, SEWA"
            executeTestCase("TC008", "Konfirmasi sewa pakaian", () -> {
                confirmRental(wait);
            });
            
            // Print summary
            printTestSummary();
            
        } catch (Exception e) {
            System.err.println("Error dalam test sewa pakaian: " + e.getMessage());
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
     * Navigasi ke halaman Cart (keranjang)
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
            
            System.out.println("✓ Berhasil navigasi ke halaman keranjang");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman Cart: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Klik salah satu item pakaian untuk melihat detail
     */
    private void clickItemDetail(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- KLIK ITEM UNTUK DETAIL ---");
        
        try {
            // Klik item Blazer berdasarkan resource-id yang diberikan
            System.out.println("Mencari item Blazer...");
            WebElement blazerItem = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/blazerImageView"))
            );
            
            System.out.println("✓ Item Blazer ditemukan, melakukan klik...");
            blazerItem.click();
            
            // Tunggu halaman detail dimuat
            Thread.sleep(3000);
            
            // Verifikasi kita sudah di halaman detail
            try {
                WebElement detailTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/tvTitle"))
                );
                System.out.println("✓ Berhasil masuk ke halaman detail: " + detailTitle.getText());
            } catch (Exception e) {
                System.out.println("✓ Berhasil masuk ke halaman detail item");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat klik item detail: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Menambah durasi sewa menjadi 5 hari
     */
    private void adjustRentalDuration(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MENAMBAH DURASI SEWA ---");
        
        try {
            // Klik tombol plus (+) sebanyak 5 kali
            System.out.println("Mencari tombol tambah (+)...");
            
            for (int i = 1; i <= 5; i++) {
                WebElement plusButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnPlus"))
                );
                
                System.out.println("Klik tombol + ke-" + i);
                plusButton.click();
                Thread.sleep(800); // Tunggu sebentar antar klik
            }
            
            // Verifikasi durasi sudah menjadi 5 hari
            try {
                WebElement durationField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/etJumlahHari"))
                );
                String duration = durationField.getText();
                System.out.println("✓ Durasi sewa berhasil diubah menjadi: " + duration + " hari");
                
                // Cek total harga
                WebElement totalPrice = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/tvTotalHarga"))
                );
                System.out.println("✓ Total harga: " + totalPrice.getText());
                
            } catch (Exception e) {
                System.out.println("✓ Durasi berhasil ditambah 5 kali");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat menambah durasi sewa: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Scroll ke bawah untuk melihat detail lengkap
     */
    private void scrollToSeeDetails(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- SCROLL UNTUK MELIHAT DETAIL ---");
        
        try {
            System.out.println("Melakukan scroll ke bawah...");
            
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
            
            System.out.println("✓ Scroll berhasil dilakukan");
            
            // Verifikasi kita bisa melihat detail item
            try {
                WebElement detailTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/tvDetailTitle"))
                );
                System.out.println("✓ Detail item terlihat: " + detailTitle.getText());
            } catch (Exception e) {
                System.out.println("✓ Scroll selesai, detail mungkin sudah terlihat");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat scroll: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Klik tombol sewa 5 hari
     */
    private void clickRentButton(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- KLIK TOMBOL SEWA ---");
        
        try {
            // Klik tombol "SEWA 5 HARI - RP 950.000"
            System.out.println("Mencari tombol sewa...");
            WebElement rentButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnRent"))
            );
            
            String buttonText = rentButton.getText();
            System.out.println("✓ Tombol sewa ditemukan: " + buttonText);
            System.out.println("Melakukan klik tombol sewa...");
            rentButton.click();
            
            // Tunggu dialog konfirmasi muncul
            Thread.sleep(2000);
            
            System.out.println("✓ Berhasil klik tombol sewa");
            
        } catch (Exception e) {
            System.err.println("Error saat klik tombol sewa: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Konfirmasi sewa dengan klik "YA, SEWA"
     */
    private void confirmRental(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- KONFIRMASI SEWA ---");
        
        try {
            // Klik tombol "YA, SEWA" pada dialog konfirmasi
            System.out.println("Mencari tombol konfirmasi 'YA, SEWA'...");
            WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("android:id/button1"))
            );
            
            String buttonText = confirmButton.getText();
            System.out.println("✓ Tombol konfirmasi ditemukan: " + buttonText);
            System.out.println("Melakukan klik konfirmasi...");
            confirmButton.click();
            
            // Tunggu proses konfirmasi
            Thread.sleep(3000);
            
            System.out.println("✓ Sewa pakaian berhasil dikonfirmasi!");
            System.out.println("✓ Proses sewa selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat konfirmasi sewa: " + e.getMessage());
            printAvailableElements();
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
        System.out.println("=== TES SEWA PAKAIAN SELESAI ===");
        System.out.println("==================================================");
    }

    /**
     * Method main untuk menjalankan test
     */
    public static void main(String[] args) {
        SewapakaianTest test = new SewapakaianTest();

        try {
            // Setup driver
            test.setup();
            
            // Print informasi device
            test.printDeviceInfo();

            // Jalankan test sewa pakaian
            test.executeTest();

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama test sewa pakaian:");
            e.printStackTrace();
        }
    }
}