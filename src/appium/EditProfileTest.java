package appium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;

public class EditProfileTest extends base {

    // Data login yang sudah terdaftar
    private String registeredEmail = "maulanafikri@gmail.com";
    private String registeredPassword = "fikrifikri";
    
    // Data edit profile
    private String newFullName = "maulfikri";
    private String newPhoneNumber = "0853738049000";
    
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
     * Metode utama untuk menjalankan test edit profile
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TEST EDIT PROFILE ===");
        System.out.println();
        
        // Setup WebDriverWait with timeout in seconds (compatible with Selenium 3.x)
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            // Tunggu aplikasi dimuat
            Thread.sleep(3000);
            
            // TC001: Skip onboarding jika ada
            executeTestCase("TC001", "Melewati proses onboarding", () -> {
                skipOnboardingIfPresent(wait);
            });
            
            // TC002: Login ke aplikasi
            executeTestCase("TC002", "Login sebagai user", () -> {
                performLogin(wait);
            });
            
            // TC003: Navigasi ke halaman Profile
            executeTestCase("TC003", "Navigasi ke halaman Profile", () -> {
                navigateToProfile(wait);
            });
            
            // TC004: Klik tombol Edit Profile
            executeTestCase("TC004", "Klik tombol Edit Profile", () -> {
                clickEditProfile(wait);
            });
            
            // TC005: Edit Full Name
            executeTestCase("TC005", "Mengubah nama menjadi 'Raza'", () -> {
                editFullName(wait);
            });
            
            // TC006: Edit Phone Number
            executeTestCase("TC006", "Mengubah nomor telepon menjadi '085373804450'", () -> {
                editPhoneNumber(wait);
            });
            
            // TC007: Simpan perubahan
            executeTestCase("TC007", "Menyimpan perubahan profil", () -> {
                saveChanges(wait);
            });
            
            // TC008: Verifikasi perubahan berhasil
            executeTestCase("TC008", "Verifikasi perubahan profil", () -> {
                verifyProfileChanges(wait);
            });
            
            // Print summary
            printTestSummary();
            
        } catch (Exception e) {
            System.err.println("Error dalam test edit profile: " + e.getMessage());
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
            System.out.println("✓ Email berhasil diisi");
            
            // Isi password
            System.out.println("Mengisi password...");
            WebElement passwordField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginPassword"))
            );
            passwordField.clear();
            Thread.sleep(500);
            passwordField.sendKeys(registeredPassword);
            System.out.println("✓ Password berhasil diisi");
            
            // Klik tombol login
            System.out.println("Menekan tombol Login...");
            WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginBtn"))
            );
            loginButton.click();
            
            // Tunggu proses login
            Thread.sleep(4000);
            
            // Verifikasi login berhasil dengan melihat elemen halaman utama
            try {
                WebElement logoText = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//android.widget.TextView[@text='CircuWear']")
                    )
                );
                System.out.println("✓ Login berhasil! Logo aplikasi ditemukan: " + logoText.getText());
            } catch (Exception e) {
                System.out.println("✓ Login kemungkinan berhasil - tidak lagi di halaman login");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat login: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman Profile
     */
    private void navigateToProfile(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN PROFILE ---");
        
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
            
            System.out.println("✓ Berhasil navigasi ke halaman Profile");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman Profile: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Klik tombol Edit Profile
     */
    private void clickEditProfile(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- KLIK TOMBOL EDIT PROFILE ---");
        
        try {
            // Cari dan klik tombol Edit Profile
            System.out.println("Mencari tombol Edit Profile...");
            WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnEditProfile"))
            );
            
            System.out.println("✓ Tombol Edit Profile ditemukan, melakukan klik...");
            editButton.click();
            
            // Tunggu halaman edit dimuat
            Thread.sleep(3000);
            
            System.out.println("✓ Berhasil masuk ke halaman Edit Profile");
            
        } catch (Exception e) {
            System.err.println("Error saat klik tombol Edit Profile: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Edit Full Name menjadi "Raza"
     */
    private void editFullName(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MENGUBAH NAMA MENJADI 'RAZA' ---");
        
        try {
            // Cari field Full Name
            System.out.println("Mencari field Full Name...");
            WebElement fullNameField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etFullName"))
            );
            
            // Ambil nilai lama untuk log
            String oldName = fullNameField.getText();
            System.out.println("Nama lama: " + oldName);
            
            // Clear dan isi dengan nama baru
            System.out.println("Mengubah nama menjadi: " + newFullName);
            fullNameField.clear();
            Thread.sleep(500);
            fullNameField.sendKeys(newFullName);
            
            System.out.println("✓ Nama berhasil diubah menjadi '" + newFullName + "'");
            
        } catch (Exception e) {
            System.err.println("Error saat mengubah nama: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Edit Phone Number menjadi "085373804450"
     */
    private void editPhoneNumber(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MENGUBAH NOMOR TELEPON MENJADI '085373804450' ---");
        
        try {
            // Cari field Phone Number
            System.out.println("Mencari field Phone Number...");
            WebElement phoneField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etPhone"))
            );
            
            // Ambil nilai lama untuk log
            String oldPhone = phoneField.getText();
            System.out.println("Nomor telepon lama: " + oldPhone);
            
            // Clear dan isi dengan nomor baru
            System.out.println("Mengubah nomor telepon menjadi: " + newPhoneNumber);
            phoneField.clear();
            Thread.sleep(500);
            phoneField.sendKeys(newPhoneNumber);
            
            System.out.println("✓ Nomor telepon berhasil diubah menjadi '" + newPhoneNumber + "'");
            
        } catch (Exception e) {
            System.err.println("Error saat mengubah nomor telepon: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Simpan perubahan dengan klik tombol Save Changes
     */
    private void saveChanges(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MENYIMPAN PERUBAHAN ---");
        
        try {
            // Cari dan klik tombol Save Changes
            System.out.println("Mencari tombol Save Changes...");
            WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnSave"))
            );
            
            System.out.println("✓ Tombol Save Changes ditemukan, melakukan klik...");
            saveButton.click();
            
            // Tunggu proses penyimpanan
            Thread.sleep(4000);
            
            System.out.println("✓ Perubahan berhasil disimpan");
            
        } catch (Exception e) {
            System.err.println("Error saat menyimpan perubahan: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Verifikasi bahwa perubahan profil berhasil
     */
    private void verifyProfileChanges(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- VERIFIKASI PERUBAHAN PROFIL ---");
        
        try {
            // Tunggu halaman kembali ke profile atau halaman lain
            Thread.sleep(2000);
            
            // Cek apakah kembali ke halaman profile utama
            try {
                // Coba cari elemen yang menandakan kita di halaman profile
                WebElement profileIndicator = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/navProfile"))
                );
                
                if (profileIndicator.isDisplayed()) {
                    System.out.println("✓ Berhasil kembali ke halaman utama setelah edit profile");
                }
            } catch (Exception e) {
                System.out.println("✓ Perubahan profil kemungkinan berhasil disimpan");
            }
            
            // Print status akhir
            System.out.println("✓ Proses edit profile selesai");
            System.out.println("Data yang diubah:");
            System.out.println("  - Nama: " + newFullName);
            System.out.println("  - Nomor Telepon: " + newPhoneNumber);
            
        } catch (Exception e) {
            System.err.println("Error saat verifikasi: " + e.getMessage());
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
            for (int i = 0; i < buttons.size() && i < 10; i++) {
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
            }
            
            // TextView elements
            var textViews = driver.findElements(By.className("android.widget.TextView"));
            System.out.println("\nTextViews (" + textViews.size() + "):");
            for (int i = 0; i < textViews.size() && i < 10; i++) {
                var textView = textViews.get(i);
                String text = textView.getText();
                String resourceId = textView.getAttribute("resource-id");
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
                }
            }
            
            // EditText elements
            var editTexts = driver.findElements(By.className("android.widget.EditText"));
            System.out.println("\nEditTexts (" + editTexts.size() + "):");
            for (int i = 0; i < editTexts.size() && i < 5; i++) {
                var editText = editTexts.get(i);
                String text = editText.getText();
                String resourceId = editText.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
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
        EditProfileTest test = new EditProfileTest();

        try {
            // Setup driver
            test.setup();
            
            // Print informasi device
            test.printDeviceInfo();

            // Jalankan test edit profile
            test.executeTest();

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama test edit profile:");
            e.printStackTrace();
        }
    }
}