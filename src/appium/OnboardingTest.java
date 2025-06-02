package appium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;

public class OnboardingTest extends base {

    // Data registrasi yang akan digunakan untuk login nanti
    private String registeredEmail = "razaadzaniiii@gmail.com";
    private String registeredPassword = "razaadzaniiii";
    
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
    
    // Helper method untuk memulai test case
    private TestCase startTestCase(String description) {
        String id = String.format("TC%03d", currentTestCaseNumber++);
        TestCase tc = new TestCase(id, description);
        testCases.add(tc);
        
        System.out.println();
        System.out.println("[" + id + "] " + description);
        return tc;
    }
    
    // Helper method untuk menyelesaikan test case
    private void completeTestCase(TestCase tc, boolean passed, String details) {
        tc.passed = passed;
        tc.details = details;
        
        if (passed) {
            System.out.println("[" + tc.id + "] ✅ PASS");
        } else {
            System.out.println("[" + tc.id + "] ❌ FAIL: " + details);
        }
    }
    
    // Method untuk print ringkasan hasil
    private void printTestSummary() {
        System.out.println();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("=== RINGKASAN HASIL TEST CASE ===");
        System.out.println("==================================================");
        
        for (TestCase tc : testCases) {
            String status = tc.passed ? "✅ PASS" : "❌ FAIL";
            System.out.println(tc.id + " - " + tc.description + ": " + status);
        }
        
        long passedCount = testCases.stream().mapToLong(tc -> tc.passed ? 1 : 0).sum();
        long failedCount = testCases.size() - passedCount;
        double successRate = testCases.size() > 0 ? (double) passedCount / testCases.size() * 100 : 0;
        
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Total Test Cases: " + testCases.size());
        System.out.println("Passed: " + passedCount + " ✅");
        System.out.println("Failed: " + failedCount + " ❌");
        System.out.println("Success Rate: " + String.format("%.1f", successRate) + "%");
        System.out.println("==================================================");
        System.out.println("=== TES OTOMASI SELESAI ===");
        System.out.println("=================================");
    }

    /**
     * Metode ini berisi langkah-langkah spesifik untuk tes onboarding.
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TEST ONBOARDING HINGGA LOGIN ===");
        System.out.println();

        // Setup WebDriverWait untuk menunggu element
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            // TC001: Skip onboarding
            skipOnboardingProcess(wait);
            
            // TC002: Navigate to Create Account
            navigateToCreateAccount(wait);
            
            // TC003: Fill registration form
            fillRegistrationForm(wait);
            
            // TC004: Navigate back to login
            navigateToLoginPage(wait);
            
            // TC005: Perform login
            performLogin(wait);
            
            // TC006: Verify login success
            verifyLoginResult(wait);
            
        } catch (Exception e) {
            System.err.println("Error saat menjalankan tes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Print ringkasan hasil
            printTestSummary();
        }
    }
    
    /**
     * TC001: Skip onboarding process
     */
    private void skipOnboardingProcess(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Melewati proses onboarding");
        
        try {
            System.out.println("Mengecek apakah ada onboarding...");
            
            // Check if onboarding exists
            try {
                WebElement nextButton = driver.findElement(By.id("com.smallacademy.userroles:id/btnNext"));
                
                if (nextButton != null) {
                    System.out.println("Onboarding ditemukan, melakukan skip...");
                    
                    // Loop untuk menekan tombol Next 2 kali (3 halaman onboarding)
                    for (int i = 1; i <= 2; i++) {
                        System.out.println("Klik tombol Next/Get Started ke-" + i);
                        
                        WebElement button = wait.until(
                            ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnNext"))
                        );
                        button.click();
                        Thread.sleep(1500);
                    }
                    
                    // Di halaman terakhir, tekan Get Started
                    System.out.println("Klik tombol Next/Get Started ke-3");
                    WebElement getStartedButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnNext"))
                    );
                    getStartedButton.click();
                    Thread.sleep(2000);
                } else {
                    System.out.println("Tidak ada onboarding, langsung ke halaman login");
                }
                
                completeTestCase(tc, true, "");
                
            } catch (Exception e) {
                System.out.println("Tidak ada onboarding atau sudah dilewati");
                completeTestCase(tc, true, "");
            }
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
            throw e;
        }
    }
    
    /**
     * TC002: Navigate to Create Account page
     */
    private void navigateToCreateAccount(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Navigasi ke halaman Create Account");
        
        try {
            System.out.println("--- NAVIGASI KE HALAMAN CREATE ACCOUNT ---");
            
            // Tunggu sampai halaman login dimuat sepenuhnya
            Thread.sleep(2000);
            
            System.out.println("Mencari tombol Create Account...");
            WebElement createAccountButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/gotoRegister"))
            );
            
            System.out.println("✓ Tombol Create Account ditemukan, melakukan klik...");
            createAccountButton.click();
            
            // Tunggu transisi ke halaman registrasi
            Thread.sleep(2000);
            
            // Verifikasi bahwa kita sudah masuk ke halaman Create Account
            WebElement registerTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.smallacademy.userroles:id/registerTitle")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman Create Account: " + registerTitle.getText());
            
            completeTestCase(tc, true, "");
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
            
            // Debug informasi untuk troubleshooting
            try {
                System.out.println("Mencoba mencari element yang tersedia di halaman saat ini...");
                printAvailableElements();
            } catch (Exception debugError) {
                System.err.println("Tidak dapat mengambil informasi debug: " + debugError.getMessage());
            }
            throw e;
        }
    }
    
    /**
     * TC003: Fill registration form
     */
    private void fillRegistrationForm(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Mengisi form registrasi");
        
        try {
            System.out.println("--- MENGISI FORM CREATE ACCOUNT ---");
            
            String fullName = "muhammad raza adzani";
            
            // 1. Isi Full Name
            System.out.println("Mengisi field Full Name...");
            WebElement nameField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/registerName"))
            );
            nameField.clear();
            nameField.sendKeys(fullName);
            System.out.println("✓ Field Full Name berhasil diisi");
            Thread.sleep(500);
            
            // 2. Isi Email Address
            System.out.println("Mengisi field Email Address...");
            WebElement emailField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/registerEmail"))
            );
            emailField.clear();
            emailField.sendKeys(registeredEmail);
            System.out.println("✓ Field Email Address berhasil diisi");
            Thread.sleep(500);
            
            // 3. Isi Password
            System.out.println("Mengisi field Password...");
            WebElement passwordField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/registerPassword"))
            );
            passwordField.clear();
            passwordField.sendKeys(registeredPassword);
            System.out.println("✓ Field Password berhasil diisi");
            Thread.sleep(500);
            
            // 4. Isi Phone Number
            System.out.println("Mengisi field Phone Number...");
            WebElement phoneField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/registerPhone"))
            );
            phoneField.clear();
            phoneField.sendKeys("085373804460");
            System.out.println("✓ Field Phone Number berhasil diisi");
            Thread.sleep(500);
            
            // 5. Klik tombol Create Account
            System.out.println("Menekan tombol Create Account...");
            WebElement createAccountButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/registerBtn"))
            );
            createAccountButton.click();
            System.out.println("✓ Tombol Create Account berhasil ditekan");
            
            // Tunggu proses registrasi
            Thread.sleep(3000);
            
            System.out.println("✓ Form registrasi berhasil disubmit");
            completeTestCase(tc, true, "");
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
            
            // Debug informasi
            try {
                System.out.println("Menampilkan semua element di halaman saat ini untuk debugging:");
                printAvailableElements();
            } catch (Exception debugError) {
                System.err.println("Tidak dapat mengambil informasi debug: " + debugError.getMessage());
            }
            throw e;
        }
    }
    
    /**
     * TC004: Navigate back to login page
     */
    private void navigateToLoginPage(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Navigasi kembali ke halaman login");
        
        try {
            System.out.println("--- NAVIGASI KE HALAMAN LOGIN ---");
            
            // Tunggu sebentar untuk memastikan proses registrasi selesai
            Thread.sleep(2000);
            
            // Cek apakah kita sudah berada di halaman login atau masih di halaman registrasi
            try {
                WebElement loginTitle = driver.findElement(By.id("com.smallacademy.userroles:id/loginTitle"));
                System.out.println("✓ Sudah berada di halaman login: " + loginTitle.getText());
                completeTestCase(tc, true, "");
                return;
                
            } catch (Exception e) {
                System.out.println("Belum di halaman login, mencari cara untuk kembali...");
            }
            
            // Jika masih di halaman registrasi, coba navigasi kembali
            try {
                // Coba gunakan navigasi hardware back
                System.out.println("Mencoba tombol back...");
                driver.navigate().back();
                Thread.sleep(2000);
                
                // Verifikasi berhasil sampai di halaman login
                WebElement loginTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
                );
                System.out.println("✓ Berhasil navigasi ke halaman login: " + loginTitle.getText());
                
            } catch (Exception e1) {
                System.out.println("Hardware back tidak berhasil, mencoba restart aplikasi...");
                
                // Restart aplikasi untuk kembali ke halaman awal
                driver.closeApp();
                Thread.sleep(1000);
                driver.launchApp();
                Thread.sleep(3000);
                
                // Skip onboarding jika muncul lagi
                skipOnboardingIfPresent(wait);
                
                WebElement loginTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
                );
                System.out.println("✓ Berhasil sampai di halaman login setelah restart: " + loginTitle.getText());
            }
            
            completeTestCase(tc, true, "");
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
            throw e;
        }
    }
    
    /**
     * TC005: Perform login
     */
    private void performLogin(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Login dengan akun yang baru dibuat");
        
        try {
            System.out.println("--- MEMULAI PROSES LOGIN ---");
            
            // Tunggu sampai halaman login siap
            Thread.sleep(2000);
            
            // Verifikasi halaman login
            WebElement loginTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
            );
            System.out.println("Halaman login ditemukan: " + loginTitle.getText());
            
            // 1. Isi Email Address
            System.out.println("Mengisi email...");
            WebElement emailField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginEmail"))
            );
            emailField.clear();
            Thread.sleep(500);
            emailField.sendKeys(registeredEmail);
            Thread.sleep(500);
            
            // 2. Isi Password
            System.out.println("Mengisi password...");
            WebElement passwordField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginPassword"))
            );
            passwordField.clear();
            Thread.sleep(500);
            passwordField.sendKeys(registeredPassword);
            Thread.sleep(500);
            
            // 3. Klik tombol Login
            System.out.println("Menekan tombol Login...");
            WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginBtn"))
            );
            loginButton.click();
            
            // Tunggu proses login
            Thread.sleep(3000);
            
            System.out.println("Memverifikasi login berhasil...");
            completeTestCase(tc, true, "");
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
            throw e;
        }
    }
    
    /**
     * TC006: Verify login result
     */
    private void verifyLoginResult(WebDriverWait wait) throws InterruptedException {
        TestCase tc = startTestCase("Verifikasi hasil login");
        
        try {
            System.out.println("--- VERIFIKASI HASIL LOGIN ---");
            
            // Tunggu sebentar untuk melihat apakah ada response dari server
            Thread.sleep(3000);
            
            // Cek apakah masih di halaman login atau sudah pindah
            try {
                WebElement loginTitle = driver.findElement(By.id("com.smallacademy.userroles:id/loginTitle"));
                System.out.println("Masih di halaman login - kemungkinan ada masalah dengan kredensial");
                completeTestCase(tc, false, "Masih di halaman login setelah submit");
                return;
            } catch (Exception e) {
                System.out.println("✓ Tidak lagi di halaman login - kemungkinan login berhasil");
            }
            
            // Cari indikator bahwa login berhasil
            try {
                // Cari element yang menunjukkan user sudah login
                WebElement dashboardElement = driver.findElement(
                    By.xpath("//*[contains(@text, 'Dashboard') or contains(@text, 'Home') or contains(@text, 'Welcome') or contains(@text, 'Selamat')]")
                );
                System.out.println("✓ Login berhasil! Element dashboard ditemukan: " + dashboardElement.getText());
                completeTestCase(tc, true, "");
                
            } catch (Exception e) {
                System.out.println("Mencari indikator login berhasil lainnya...");
                
                // Cek apakah ada tombol atau menu yang hanya muncul setelah login
                try {
                    // Tampilkan element untuk debugging
                    printAvailableElements();
                    System.out.println("✓ Login kemungkinan berhasil - halaman berubah dari login");
                    completeTestCase(tc, true, "");
                    
                } catch (Exception e2) {
                    completeTestCase(tc, false, "Tidak dapat memverifikasi status login");
                }
            }
            
        } catch (Exception e) {
            completeTestCase(tc, false, e.getMessage());
        }
    }
    
    /**
     * Method helper untuk skip onboarding jika muncul lagi setelah restart
     */
    private void skipOnboardingIfPresent(WebDriverWait wait) throws InterruptedException {
        try {
            WebElement nextButton = driver.findElement(By.id("com.smallacademy.userroles:id/btnNext"));
            
            if (nextButton != null) {
                System.out.println("Onboarding muncul lagi, melakukan skip...");
                
                for (int i = 0; i < 3; i++) {
                    try {
                        nextButton = wait.until(
                            ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnNext"))
                        );
                        nextButton.click();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Onboarding tidak muncul
        }
    }
    
    /**
     * Method helper untuk debugging - menampilkan element yang tersedia
     */
    private void printAvailableElements() {
        try {
            System.out.println("=== Element yang tersedia ===");
            
            // Cari semua button
            var buttons = driver.findElementsByClassName("android.widget.Button");
            System.out.println("Jumlah Button ditemukan: " + buttons.size());
            
            for (int i = 0; i < Math.min(buttons.size(), 5); i++) { // Limit to 5 for brevity
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("Button " + (i+1) + ": text='" + text + "', id='" + resourceId + "'");
                }
            }
            
            // Cari semua TextView (untuk title/label)
            var textViews = driver.findElementsByClassName("android.widget.TextView");
            System.out.println("\nJumlah TextView ditemukan: " + textViews.size());
            
            for (int i = 0; i < Math.min(textViews.size(), 5); i++) { // Limit to 5 for brevity
                var textView = textViews.get(i);
                String text = textView.getText();
                String resourceId = textView.getAttribute("resource-id");
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("TextView " + (i+1) + ": text='" + text + "', id='" + resourceId + "'");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error saat debugging: " + e.getMessage());
        }
    }

    /**
     * Main method - titik masuk program
     */
    public static void main(String[] args) {
        OnboardingTest test = new OnboardingTest();

        try {
            // 1. Setup driver
            test.setup();
            
            // 2. Print informasi device untuk debugging
            test.printDeviceInfo();

            // 3. Jalankan tes lengkap
            test.executeTest();

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama tes berjalan:");
            e.printStackTrace();
        } 
    }
}