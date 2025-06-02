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

public class AdminExplorationTest extends base {

    // Data login admin
    private String registeredEmail = "Admin123@gmail.com";
    private String registeredPassword = "Admin123";
    
    // Test case tracking
    private List<TestCase> testCases = new ArrayList<>();
    private int currentTestCaseNumber = 1;

    // Inner class untuk tracking test case
    private static class TestCase {
        String id;
        String description;
        boolean passed;
        String errorMessage;
        
        TestCase(String id, String description) {
            this.id = id;
            this.description = description;
            this.passed = false;
            this.errorMessage = "";
        }
    }

    /**
     * Metode utama untuk menjalankan test eksplorasi halaman admin
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TEST EKSPLORASI HALAMAN ADMIN ===\n");
        
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            Thread.sleep(3000);
            
            // Execute all test cases
            executeTestCase("TC001", "Melewati onboarding jika ada", () -> {
                skipOnboardingIfPresent(wait);
                return null;
            });
            
            executeTestCase("TC002", "Login sebagai admin", () -> {
                performLogin(wait);
                return null;
            });
            
            executeTestCase("TC003", "Navigasi ke dashboard admin", () -> {
                navigateToAdminDashboard(wait);
                return null;
            });
            
            executeTestCase("TC004", "Scroll dashboard utama", () -> {
                scrollMainDashboard(wait);
                return null;
            });
            
            executeTestCase("TC005", "Navigasi ke halaman inventaris", () -> {
                openAdminPanelMenu(wait);
                navigateToInventoryPage(wait);
                return null;
            });
            
            executeTestCase("TC006", "Scroll halaman inventaris", () -> {
                scrollInventoryPage(wait);
                return null;
            });
            
            executeTestCase("TC007", "Navigasi ke halaman pesanan", () -> {
                goBackToDashboard(wait);
                openAdminPanelMenu(wait);
                navigateToOrdersPage(wait);
                return null;
            });
            
            executeTestCase("TC008", "Navigasi ke halaman loyalitas", () -> {
                goBackToDashboard(wait);
                openAdminPanelMenu(wait);
                navigateToLoyaltyPage(wait);
                return null;
            });
            
            executeTestCase("TC009", "Navigasi ke halaman laporan", () -> {
                goBackToDashboard(wait);
                openAdminPanelMenu(wait);
                navigateToReportsPage(wait);
                return null;
            });
            
            executeTestCase("TC010", "Navigasi ke halaman pengguna dan scroll", () -> {
                goBackToDashboard(wait);
                openAdminPanelMenu(wait);
                navigateToUsersPageAndScroll(wait);
                return null;
            });
            
            executeTestCase("TC011", "Kembali ke dashboard utama", () -> {
                goBackToDashboard(wait);
                return null;
            });
            
            // Print final summary
            printTestSummary();
            
        } catch (Exception e) {
            System.err.println("Error dalam test eksplorasi admin: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    /**
     * Execute a test case with proper logging and error handling
     */
    private void executeTestCase(String testId, String description, TestCaseExecutor executor) {
        TestCase testCase = new TestCase(testId, description);
        testCases.add(testCase);
        
        System.out.println("[" + testId + "] " + description);
        
        try {
            executor.execute();
            testCase.passed = true;
            System.out.println("[" + testId + "] ✅ PASS\n");
            
        } catch (Exception e) {
            testCase.passed = false;
            testCase.errorMessage = e.getMessage();
            System.out.println("[" + testId + "] ❌ FAIL - " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Functional interface untuk test case execution
     */
    @FunctionalInterface
    private interface TestCaseExecutor {
        Void execute() throws Exception;
    }
    
    /**
     * Print comprehensive test summary
     */
    private void printTestSummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== RINGKASAN HASIL TEST CASE ===");
        System.out.println("=".repeat(50));
        
        int passedCount = 0;
        int failedCount = 0;
        
        for (TestCase testCase : testCases) {
            String status = testCase.passed ? "✅ PASS" : "❌ FAIL";
            System.out.println(testCase.id + " - " + testCase.description + ": " + status);
            
            if (!testCase.passed && !testCase.errorMessage.isEmpty()) {
                System.out.println("    Error: " + testCase.errorMessage);
            }
            
            if (testCase.passed) {
                passedCount++;
            } else {
                failedCount++;
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Total Test Cases: " + testCases.size());
        System.out.println("Passed: " + passedCount + " ✅");
        System.out.println("Failed: " + failedCount + " ❌");
        System.out.println("Success Rate: " + String.format("%.1f", (passedCount * 100.0 / testCases.size())) + "%");
        System.out.println("=".repeat(50));
        System.out.println("=== TES OTOMASI SELESAI ===");
        System.out.println("=".repeat(50));
    }
    
    /**
     * Skip onboarding jika muncul
     */
    private void skipOnboardingIfPresent(WebDriverWait wait) throws InterruptedException {
        System.out.println("Mengecek apakah ada onboarding...");
        
        try {
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
            
        } catch (Exception e) {
            System.out.println("Tidak ada onboarding atau sudah selesai");
        }
    }
    
    /**
     * Melakukan login dengan kredensial admin
     */
    private void performLogin(WebDriverWait wait) throws InterruptedException {
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
            
        } catch (Exception e) {
            System.err.println("Error saat login: " + e.getMessage());
            printAvailableElements();
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
            System.err.println("Gagal verifikasi login: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke dashboard admin
     */
    private void navigateToAdminDashboard(WebDriverWait wait) throws InterruptedException {
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
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke dashboard admin: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Scroll dashboard utama sampai bawah
     */
    private void scrollMainDashboard(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- SCROLL DASHBOARD UTAMA ---");
        
        try {
            performScrollExploration();
            System.out.println("✓ Scroll dashboard utama selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat scroll dashboard utama: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Buka menu admin panel
     */
    private void openAdminPanelMenu(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- MEMBUKA MENU ADMIN PANEL ---");
        
        try {
            // Periksa apakah berada di halaman selain dashboard
            try {
                WebElement nonDashboardTitle = driver.findElement(By.xpath("//android.widget.TextView[@text='Manajemen Inventaris' or @text='Manajemen Pesanan' or @text='Manajemen Pengguna']"));
                if (nonDashboardTitle.isDisplayed()) {
                    System.out.println("Berada di halaman " + nonDashboardTitle.getText() + ", menekan tombol Kembali...");
                    WebElement backButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.ImageButton[@content-desc='Kembali ke atas']")
                        )
                    );
                    backButton.click();
                    Thread.sleep(3000);
                    
                    // Verifikasi kembali ke dashboard
                    WebElement dashboardTitle = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.TextView[@text='Dasbor Utama']")
                        )
                    );
                    System.out.println("✓ Berhasil kembali ke dashboard: " + dashboardTitle.getText());
                }
            } catch (Exception e) {
                System.out.println("Sudah berada di dashboard atau halaman lain, melanjutkan...");
            }
            
            // Periksa apakah navigation drawer sudah terbuka
            try {
                WebElement closeDrawerButton = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Close navigation drawer']"));
                if (closeDrawerButton.isDisplayed()) {
                    System.out.println("Navigation drawer sudah terbuka, melanjutkan...");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Navigation drawer belum terbuka, mencoba membuka...");
            }
            
            // Buka navigation drawer
            System.out.println("Mencari tombol navigasi drawer...");
            WebElement navDrawerButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")
                )
            );
            
            System.out.println("✓ Tombol navigasi drawer ditemukan, melakukan klik...");
            navDrawerButton.click();
            
            Thread.sleep(2000);
            
            // Verifikasi navigation drawer terbuka
            WebElement navHeader = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Admin Panel']")
                )
            );
            System.out.println("✓ Menu admin panel terbuka: " + navHeader.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat membuka menu admin panel: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Kembali ke dashboard dengan tombol kembali
     */
    private void goBackToDashboard(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- KEMBALI KE DASHBOARD ---");
        
        try {
            System.out.println("Mencari tombol Kembali...");
            WebElement backButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageButton[@content-desc='Kembali ke atas']")
                )
            );
            
            System.out.println("✓ Tombol Kembali ditemukan, melakukan klik...");
            backButton.click();
            
            Thread.sleep(3000);
            
            WebElement dashboardTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Dasbor Utama']")
                )
            );
            System.out.println("✓ Berhasil kembali ke dashboard: " + dashboardTitle.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat kembali ke dashboard: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman inventaris
     */
    private void navigateToInventoryPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN INVENTARIS ---");
        
        try {
            System.out.println("Mencari opsi inventaris...");
            WebElement inventoryOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_inventory"))
            );
            
            System.out.println("✓ Opsi inventaris ditemukan, melakukan klik...");
            inventoryOption.click();
            
            Thread.sleep(3000);
            
            WebElement inventoryTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Manajemen Inventaris']")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman inventaris: " + inventoryTitle.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman inventaris: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Scroll halaman inventaris sampai bawah
     */
    private void scrollInventoryPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- SCROLL HALAMAN INVENTARIS ---");
        
        try {
            performScrollExploration();
            System.out.println("✓ Scroll halaman inventaris selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat scroll halaman inventaris: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman pesanan
     */
    private void navigateToOrdersPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN PESANAN ---");
        
        try {
            System.out.println("Mencari opsi pesanan...");
            WebElement ordersOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_orders"))
            );
            
            System.out.println("✓ Opsi pesanan ditemukan, melakukan klik...");
            ordersOption.click();
            
            Thread.sleep(3000);
            
            WebElement ordersTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Manajemen Pesanan']")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman pesanan: " + ordersTitle.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman pesanan: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman loyalitas
     */
    private void navigateToLoyaltyPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN LOYALITAS ---");
        
        try {
            System.out.println("Mencari opsi loyalitas...");
            WebElement loyaltyOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_loyalty"))
            );
            
            System.out.println("✓ Opsi loyalitas ditemukan, melakukan klik...");
            loyaltyOption.click();
            
            Thread.sleep(5000); // Penundaan lebih lama untuk memastikan halaman dimuat
            
            // Verifikasi halaman loyalitas menggunakan elemen yang ada
            WebElement loyaltyElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.smallacademy.userroles:id/tvRewardTitle")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman loyalitas, elemen ditemukan: " + loyaltyElement.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman loyalitas: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman laporan
     */
    private void navigateToReportsPage(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN LAPORAN ---");
        
        try {
            System.out.println("Mencari opsi laporan...");
            WebElement reportsOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_reports"))
            );
            
            System.out.println("✓ Opsi laporan ditemukan, melakukan klik...");
            reportsOption.click();
            
            Thread.sleep(5000); // Penundaan lebih lama untuk memastikan halaman dimuat
            
            // Verifikasi halaman laporan menggunakan elemen yang ada
            WebElement reportsElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.id("com.smallacademy.userroles:id/tvRevenue3")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman laporan, elemen ditemukan: " + reportsElement.getText());
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman laporan: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman pengguna dan scroll ke bawah
     */
    private void navigateToUsersPageAndScroll(WebDriverWait wait) throws InterruptedException {
        System.out.println("--- NAVIGASI KE HALAMAN PENGGUNA ---");
        
        try {
            System.out.println("Mencari opsi pengguna...");
            WebElement usersOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/nav_users"))
            );
            
            System.out.println("✓ Opsi pengguna ditemukan, melakukan klik...");
            usersOption.click();
            
            Thread.sleep(3000);
            
            WebElement usersTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Manajemen Pengguna']")
                )
            );
            System.out.println("✓ Berhasil navigasi ke halaman pengguna: " + usersTitle.getText());
            
            System.out.println("--- SCROLL HALAMAN PENGGUNA ---");
            performScrollExploration();
            System.out.println("✓ Scroll halaman pengguna selesai");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman pengguna atau scroll: " + e.getMessage());
            printAvailableElements();
            throw e;
        }
    }
    
    /**
     * Melakukan scroll ke bawah untuk eksplorasi lebih lanjut
     */
    private void performScrollExploration() throws InterruptedException {
        System.out.println("\n-- Melakukan Scroll Eksplorasi --");
        
        try {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Scroll ke-" + i + "...");
                
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
                
                Thread.sleep(2000);
                
                System.out.println("✓ Scroll ke-" + i + " selesai");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat melakukan scroll: " + e.getMessage());
        }
    }
    
    /**
     * Print elemen-elemen yang ada di halaman saat ini untuk debugging
     */
    private void printAvailableElements() {
        try {
            System.out.println("\n=== DEBUG: SEMUA ELEMENT TERSEDIA ===");
            
            var buttons = driver.findElements(By.className("android.widget.Button"));
            System.out.println("Buttons (" + buttons.size() + "):");
            for (int i = 0; i < buttons.size() && i < 15; i++) {
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
            }
            
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
     * Method main untuk menjalankan test
     */
    public static void main(String[] args) {
        AdminExplorationTest test = new AdminExplorationTest();

        try {
            test.setup();
            test.printDeviceInfo();
            test.executeTest();

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama test eksplorasi admin:");
            e.printStackTrace();
        }
    }
}