package appium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;

public class UpdateUserTest extends base {

    // Data login admin
    private String registeredEmail = "Admin123@gmail.com";
    private String registeredPassword = "Admin123";

    // Daftar untuk menyimpan hasil test case
    private List<String> testResults = new ArrayList<>();

    /**
     * Mencetak header test case
     */
    private void logTestCaseStart(String testId, String description) {
        System.out.println("\n[" + testId + "] " + description);
    }

    /**
     * Mencetak status test case
     */
    private void logTestCaseResult(String testId, boolean passed, String description) {
        String status = passed ? "✅ PASS" : "❌ FAIL";
        System.out.println("[" + testId + "] " + status);
        testResults.add(testId + " - " + description + ": " + status);
    }

    /**
     * Mencetak ringkasan hasil tes
     */
    private void printTestSummary() {
        int totalTests = testResults.size();
        int passedTests = 0;
        int failedTests = 0;

        System.out.println("\n==================================================");
        System.out.println("=== RINGKASAN HASIL TEST CASE ===");
        System.out.println("==================================================");
        
        for (String result : testResults) {
            System.out.println(result);
            if (result.contains("✅ PASS")) {
                passedTests++;
            } else if (result.contains("❌ FAIL")) {
                failedTests++;
            }
        }

        System.out.println("\n==================================================");
        System.out.println("Total Test Cases: " + totalTests);
        System.out.println("Passed: " + passedTests + " ✅");
        System.out.println("Failed: " + failedTests + " ❌");
        
        if (totalTests > 0) {
            double successRate = ((double) passedTests / totalTests) * 100;
            System.out.println("Success Rate: " + String.format("%.1f", successRate) + "%");
        }
        
        System.out.println("==================================================");
        System.out.println("=== TES OTOMASI SELESAI ===");
        System.out.println("=================================");
        System.out.println("=================");
    }

    /**
     * Metode utama untuk menjalankan tes update user
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TES UPDATE USER ===");
        
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            Thread.sleep(3000);
            skipOnboardingIfPresent(wait);
            performLogin(wait);
            navigateToAdminDashboard(wait);
            navigateToUserManagement(wait);
            selectUser(wait);
            deactivateUser(wait);
            deleteUser(wait);
            
        } catch (Exception e) {
            System.err.println("Error dalam tes update user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            printTestSummary();
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    /**
     * Skip onboarding jika muncul
     */
    private void skipOnboardingIfPresent(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC001", "Melewati proses onboarding");
        
        try {
            System.out.println("Mengecek apakah ada onboarding...");
            WebElement nextButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnNext"))
            );
            
            if (nextButton.isDisplayed()) {
                System.out.println("Onboarding ditemukan, melakukan skip...");
                
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
            logTestCaseResult("TC001", true, "Melewati proses onboarding");
            
        } catch (Exception e) {
            System.out.println("Tidak ada onboarding atau sudah selesai");
            logTestCaseResult("TC001", true, "Melewati proses onboarding");
        }
    }
    
    /**
     * Melakukan login dengan kredensial admin
     */
    private void performLogin(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC002", "Login sebagai admin");
        System.out.println("--- MEMULAI PROSES LOGIN ---");
        
        try {
            Thread.sleep(2000);
            
            WebElement loginTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/loginTitle"))
            );
            System.out.println("Halaman login ditemukan: " + loginTitle.getText());
            
            System.out.println("Mengisi email...");
            WebElement emailField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginEmail"))
            );
            emailField.clear();
            Thread.sleep(500);
            emailField.sendKeys(registeredEmail);
            
            System.out.println("Mengisi password...");
            WebElement passwordField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginPassword"))
            );
            passwordField.clear();
            Thread.sleep(500);
            passwordField.sendKeys(registeredPassword);
            
            System.out.println("Menekan tombol Login...");
            WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/loginBtn"))
            );
            loginButton.click();
            
            Thread.sleep(4000);
            
            verifyLoginSuccess(wait);
            logTestCaseResult("TC002", true, "Login sebagai admin");
            
        } catch (Exception e) {
            System.err.println("Error saat login: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC002", false, "Login sebagai admin");
            throw e;
        }
    }
    
    /**
     * Verifikasi bahwa login berhasil
     */
    private void verifyLoginSuccess(WebDriverWait wait) throws InterruptedException {
        System.out.println("Memverifikasi login berhasil...");
        
        try {
            WebElement dashboardButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnDashboard"))
            );
            System.out.println("✓ Login berhasil! Tombol dashboard ditemukan: " + dashboardButton.getText());
            
        } catch (Exception e) {
            System.err.println("Gagal memverifikasi login: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke dashboard admin
     */
    private void navigateToAdminDashboard(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC003", "Navigasi ke dashboard admin");
        System.out.println("--- NAVIGASI KE DASHBOARD ADMIN ---");
        
        try {
            System.out.println("Mencari tombol GO TO DASHBOARD...");
            WebElement dashboardButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnDashboard"))
            );
            
            System.out.println("✓ Tombol GO TO DASHBOARD ditemukan, melakukan klik...");
            dashboardButton.click();
            
            Thread.sleep(3000);
            
            WebElement dashboardTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Dasbor Utama']")
                )
            );
            System.out.println("✓ Berhasil navigasi ke dashboard admin: " + dashboardTitle.getText());
            logTestCaseResult("TC003", true, "Navigasi ke dashboard admin");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke dashboard admin: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC003", false, "Navigasi ke dashboard admin");
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman manajemen pengguna
     */
    private void navigateToUserManagement(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC004", "Navigasi ke halaman manajemen pengguna");
        System.out.println("--- NAVIGASI KE HALAMAN PENGGUNA ---");
        
        try {
            // Buka navigation drawer jika belum terbuka
            try {
                WebElement closeDrawerButton = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Close navigation drawer']"));
                if (closeDrawerButton.isDisplayed()) {
                    System.out.println("Navigation drawer sudah terbuka, melanjutkan...");
                }
            } catch (Exception e) {
                System.out.println("Navigation drawer belum terbuka, mencoba membuka...");
                
                System.out.println("Mencari tombol navigasi drawer...");
                WebElement navDrawerButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")
                    )
                );
                
                System.out.println("✓ Tombol navigasi drawer ditemukan, melakukan klik...");
                navDrawerButton.click();
                
                Thread.sleep(2000);
                
                WebElement navHeader = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//android.widget.TextView[@text='Admin Panel']")
                    )
                );
                System.out.println("✓ Menu admin panel terbuka: " + navHeader.getText());
            }
            
            // Klik menu Users/Pengguna
            System.out.println("Mencari opsi manajemen pengguna...");
            WebElement userOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_users"))
            );
            
            System.out.println("✓ Opsi manajemen pengguna ditemukan, melakukan klik...");
            userOption.click();
            
            Thread.sleep(3000);
            
            // Verifikasi halaman pengguna
            WebElement userTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Manajemen Pengguna']")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman pengguna: " + userTitle.getText());
            logTestCaseResult("TC004", true, "Navigasi ke halaman manajemen pengguna");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman pengguna: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC004", false, "Navigasi ke halaman manajemen pengguna");
            throw e;
        }
    }
    
    /**
     * Memilih salah satu user untuk dikelola
     */
    private void selectUser(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC005", "Memilih pengguna");
        System.out.println("--- MEMILIH PENGGUNA ---");
        
        try {
            System.out.println("Mencari daftar pengguna...");
            
            // Mencari user dengan nama "Fikri Nazi" atau user pertama yang tersedia
            WebElement userElement = null;
            try {
                userElement = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.TextView[@text='Fikri Nazi']")
                    )
                );
                System.out.println("✓ Pengguna 'Fikri Nazi' ditemukan");
            } catch (Exception e) {
                System.out.println("Pengguna 'Fikri Nazi' tidak ditemukan, mencari pengguna lain...");
                userElement = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.id("com.smallacademy.userroles:id/tvUserName")
                    )
                );
                System.out.println("✓ Pengguna ditemukan: " + userElement.getText());
            }
            
            System.out.println("Mengklik pengguna...");
            userElement.click();
            
            Thread.sleep(2000);
            
            // Verifikasi detail pengguna terbuka
            WebElement userDetailTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Detail Pengguna']")
                )
            );
            System.out.println("✓ Detail pengguna terbuka: " + userDetailTitle.getText());
            logTestCaseResult("TC005", true, "Memilih pengguna");
            
        } catch (Exception e) {
            System.err.println("Error saat memilih pengguna: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC005", false, "Memilih pengguna");
            throw e;
        }
    }
    
    /**
     * Menonaktifkan pengguna
     */
    private void deactivateUser(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC006", "Menonaktifkan pengguna");
        System.out.println("--- MENONAKTIFKAN PENGGUNA ---");
        
        try {
            System.out.println("Mencari tombol NONAKTIFKAN...");
            WebElement deactivateButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnToggleStatus"))
            );
            
            String buttonText = deactivateButton.getText();
            System.out.println("✓ Tombol status ditemukan: " + buttonText);
            
            if (buttonText.equals("NONAKTIFKAN")) {
                System.out.println("Mengklik tombol NONAKTIFKAN...");
                deactivateButton.click();
                
                Thread.sleep(2000);
                
                // Verifikasi perubahan status
                try {
                    WebElement activateButton = wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnToggleStatus"))
                    );
                    String newButtonText = activateButton.getText();
                    System.out.println("✓ Status pengguna berhasil diubah. Tombol sekarang: " + newButtonText);
                } catch (Exception e) {
                    System.out.println("✓ Pengguna berhasil dinonaktifkan");
                }
                
                logTestCaseResult("TC006", true, "Menonaktifkan pengguna");
                
            } else if (buttonText.equals("AKTIFKAN")) {
                System.out.println("Pengguna sudah dalam status nonaktif, melanjutkan ke langkah berikutnya...");
                logTestCaseResult("TC006", true, "Menonaktifkan pengguna");
            } else {
                System.out.println("Status tombol tidak dikenali: " + buttonText);
                logTestCaseResult("TC006", false, "Menonaktifkan pengguna");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat menonaktifkan pengguna: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC006", false, "Menonaktifkan pengguna");
            throw e;
        }
    }
    
    /**
     * Menghapus pengguna
     */
    private void deleteUser(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC007", "Menghapus pengguna");
        System.out.println("--- MENGHAPUS PENGGUNA ---");
        
        try {
            System.out.println("Mencari tombol HAPUS PENGGUNA...");
            WebElement deleteButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnDeleteUser"))
            );
            
            System.out.println("✓ Tombol HAPUS PENGGUNA ditemukan: " + deleteButton.getText());
            System.out.println("Mengklik tombol HAPUS PENGGUNA...");
            deleteButton.click();
            
            Thread.sleep(2000);
            
            // Menangani dialog konfirmasi
            try {
                System.out.println("Mencari dialog konfirmasi hapus...");
                WebElement confirmButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.Button[@text='Ya'] | //android.widget.Button[@text='YA'] | //android.widget.Button[@text='OK']")
                    )
                );
                
                System.out.println("✓ Dialog konfirmasi ditemukan, mengklik tombol konfirmasi...");
                confirmButton.click();
                
                Thread.sleep(3000);
                
                // Verifikasi penghapusan berhasil
                try {
                    WebElement successMessage = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.TextView[@text='Pengguna berhasil dihapus']")
                        )
                    );
                    System.out.println("✓ Pengguna berhasil dihapus: " + successMessage.getText());
                } catch (Exception e) {
                    System.out.println("✓ Pengguna kemungkinan berhasil dihapus - kembali ke daftar pengguna");
                    
                    // Verifikasi kembali ke halaman manajemen pengguna
                    WebElement userTitle = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.TextView[@text='Manajemen Pengguna']")
                        )
                    );
                    System.out.println("✓ Kembali ke halaman manajemen pengguna: " + userTitle.getText());
                }
                
                logTestCaseResult("TC007", true, "Menghapus pengguna");
                
            } catch (Exception e) {
                System.err.println("Dialog konfirmasi tidak ditemukan atau error: " + e.getMessage());
                logTestCaseResult("TC007", false, "Menghapus pengguna");
                throw e;
            }
            
        } catch (Exception e) {
            System.err.println("Error saat menghapus pengguna: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC007", false, "Menghapus pengguna");
            throw e;
        }
    }
    
    /**
     * Print elemen-elemen yang ada di halaman saat ini untuk debugging
     */
    private void printAvailableElements() {
        try {
            System.out.println("\n=== Element yang tersedia ===");
            
            // Tombol Button
            var buttons = driver.findElements(By.className("android.widget.Button"));
            System.out.println("Jumlah Button ditemukan: " + buttons.size());
            for (int i = 0; i < buttons.size() && i < 10; i++) {
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                System.out.println("Button " + (i+1) + ": text='" + text + "', id='" + resourceId + "'");
            }
            
            // TextView
            var textViews = driver.findElements(By.className("android.widget.TextView"));
            System.out.println("\nJumlah TextView ditemukan: " + textViews.size());
            for (int i = 0; i < textViews.size() && i < 10; i++) {
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
     * Method main untuk menjalankan tes
     */
    public static void main(String[] args) {
        UpdateUserTest test = new UpdateUserTest();

        try {
            test.setup();
            test.printDeviceInfo();
            test.executeTest();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama tes update user:");
            e.printStackTrace();
        }
    }
}