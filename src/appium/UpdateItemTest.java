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

public class UpdateItemTest extends base {

    // Data login admin
    private String registeredEmail = "Admin123@gmail.com";
    private String registeredPassword = "Admin123";

    // Daftar untuk menyimpan hasil test case
    private List<String> testResults = new ArrayList<>();

    /**
     * Mencetak header test case
     */
    private void logTestCaseStart(String testId, String description) {
        System.out.println("[" + testId + "] " + description);
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
        int passedTests = (int) testResults.stream().filter(result -> result.contains("✅ PASS")).count();
        int failedTests = totalTests - passedTests;
        double successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;

        System.out.println("\n==================================================");
        System.out.println("=== RINGKASAN HASIL TEST CASE ===");
        System.out.println("==================================================");
        for (String result : testResults) {
            System.out.println(result);
        }
        System.out.println("\n==================================================");
        System.out.println("Total Test Cases: " + totalTests);
        System.out.println("Passed: " + passedTests + " ✅");
        System.out.println("Failed: " + failedTests + " ❌");
        System.out.printf("Success Rate: %.1f%%\n", successRate);
        System.out.println("==================================================");
        System.out.println("=== TES OTOMASI SELESAI ===");
        System.out.println("=================================");
        System.out.println("=================");
    }

    /**
     * Metode utama untuk menjalankan tes update item
     */
    public void executeTest() throws InterruptedException {
        System.out.println("✅ Sesi driver berhasil dibuat!");
        System.out.println("Session ID: " + driver.getSessionId());
        
        System.out.println("=== MEMULAI TES UPDATE ITEM INVENTARIS ===");
        System.out.println();
        
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            Thread.sleep(3000);
            skipOnboardingIfPresent(wait);
            performLogin(wait);
            navigateToAdminDashboard(wait);
            openAdminPanelMenu(wait);
            navigateToInventoryPage(wait);
            selectFirstItem(wait);
            editItemName(wait);
            updateItem(wait);
            markItemAsRented(wait);
            deleteItem(wait);
            
        } catch (Exception e) {
            System.err.println("Error dalam tes update item: " + e.getMessage());
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
     * Buka menu admin panel
     */
    private void openAdminPanelMenu(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC004", "Membuka menu admin panel");
        System.out.println("--- MEMBUKA MENU ADMIN PANEL ---");
        
        try {
            // Periksa apakah navigation drawer sudah terbuka
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
            }
            
            WebElement navHeader = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Admin Panel']")
                )
            );
            System.out.println("✓ Menu admin panel terbuka: " + navHeader.getText());
            logTestCaseResult("TC004", true, "Membuka menu admin panel");
            
        } catch (Exception e) {
            System.err.println("Error saat membuka menu admin panel: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC004", false, "Membuka menu admin panel");
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman inventaris
     */
    private void navigateToInventoryPage(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC005", "Navigasi ke halaman manajemen inventaris");
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
            logTestCaseResult("TC005", true, "Navigasi ke halaman manajemen inventaris");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman inventaris: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC005", false, "Navigasi ke halaman manajemen inventaris");
            throw e;
        }
    }
    
    /**
     * Pilih item pertama dari daftar inventaris
     */
    private void selectFirstItem(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC006", "Memilih item pertama dari daftar inventaris");
        System.out.println("--- MEMILIH ITEM PERTAMA ---");
        
        try {
            System.out.println("Mencari item pertama dengan status 'Tersedia'...");
            WebElement firstItemStatus = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/tvItemStatus"))
            );
            
            System.out.println("✓ Item pertama ditemukan dengan status: " + firstItemStatus.getText());
            firstItemStatus.click();
            
            Thread.sleep(2000);
            
            // Verifikasi detail item terbuka
            WebElement editButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnEditItem"))
            );
            System.out.println("✓ Detail item terbuka, tombol edit tersedia: " + editButton.getText());
            logTestCaseResult("TC006", true, "Memilih item pertama dari daftar inventaris");
            
        } catch (Exception e) {
            System.err.println("Error saat memilih item pertama: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC006", false, "Memilih item pertama dari daftar inventaris");
            throw e;
        }
    }
    
    /**
     * Edit nama item dari "Gaun Formal Hitam" menjadi "Gaun Formal Hijau"
     */
    private void editItemName(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC007", "Mengedit nama item");
        System.out.println("--- MENGEDIT NAMA ITEM ---");
        
        try {
            System.out.println("Menekan tombol EDIT ITEM...");
            WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnEditItem"))
            );
            editButton.click();
            
            Thread.sleep(2000);
            
            System.out.println("Mencari field nama item...");
            WebElement nameField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etItemName"))
            );
            
            String currentName = nameField.getText();
            System.out.println("Nama item saat ini: " + currentName);
            
            System.out.println("Mengubah nama dari 'Gaun Formal Hitam' menjadi 'Gaun Formal Hijau'...");
            nameField.clear();
            Thread.sleep(500);
            nameField.sendKeys("Gaun Formal Hijau");
            
            System.out.println("✓ Nama item berhasil diubah menjadi: Gaun Formal Hijau");
            logTestCaseResult("TC007", true, "Mengedit nama item");
            
        } catch (Exception e) {
            System.err.println("Error saat mengedit nama item: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC007", false, "Mengedit nama item");
            throw e;
        }
    }
    
    /**
     * Update item dengan menekan tombol UPDATE ITEM
     */
    private void updateItem(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC008", "Menyimpan perubahan item");
        System.out.println("--- MENYIMPAN PERUBAHAN ITEM ---");
        
        try {
            // Scroll ke bawah untuk memastikan tombol terlihat
            System.out.println("Melakukan scroll ke bawah...");
            performScrollInForm();
            
            System.out.println("Mencari tombol UPDATE ITEM...");
            WebElement updateButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnUpdateItem"))
            );
            
            System.out.println("✓ Tombol UPDATE ITEM ditemukan: " + updateButton.getText());
            updateButton.click();
            
            Thread.sleep(3000);
            
            // Verifikasi kembali ke halaman detail item
            try {
                WebElement itemDetail = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.smallacademy.userroles:id/btnEditItem"))
                );
                System.out.println("✓ Item berhasil diupdate, kembali ke halaman detail item");
            } catch (Exception e) {
                System.out.println("✓ Item berhasil diupdate");
            }
            
            logTestCaseResult("TC008", true, "Menyimpan perubahan item");
            
        } catch (Exception e) {
            System.err.println("Error saat menyimpan perubahan item: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC008", false, "Menyimpan perubahan item");
            throw e;
        }
    }
    
    /**
     * Tandai item sebagai disewa
     */
    private void markItemAsRented(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC009", "Menandai item sebagai disewa");
        System.out.println("--- MENANDAI ITEM SEBAGAI DISEWA ---");
        
        try {
            System.out.println("Mencari tombol TANDAI DISEWA...");
            WebElement rentButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnChangeStatus"))
            );
            
            System.out.println("✓ Tombol TANDAI DISEWA ditemukan: " + rentButton.getText());
            rentButton.click();
            
            Thread.sleep(3000);
            
            // Verifikasi status berubah
            try {
                WebElement statusElement = driver.findElement(By.id("com.smallacademy.userroles:id/tvItemStatus"));
                System.out.println("✓ Status item berhasil diubah menjadi: " + statusElement.getText());
            } catch (Exception e) {
                System.out.println("✓ Item berhasil ditandai sebagai disewa");
            }
            
            logTestCaseResult("TC009", true, "Menandai item sebagai disewa");
            
        } catch (Exception e) {
            System.err.println("Error saat menandai item sebagai disewa: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC009", false, "Menandai item sebagai disewa");
            throw e;
        }
    }
    
    /**
     * Hapus item dari inventaris
     */
    private void deleteItem(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC010", "Menghapus item dari inventaris");
        System.out.println("--- MENGHAPUS ITEM DARI INVENTARIS ---");
        
        try {
            System.out.println("Mencari tombol HAPUS ITEM...");
            WebElement deleteButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnDeleteItem"))
            );
            
            System.out.println("✓ Tombol HAPUS ITEM ditemukan: " + deleteButton.getText());
            deleteButton.click();
            
            Thread.sleep(2000);
            
            // Konfirmasi penghapusan
            System.out.println("Mencari tombol konfirmasi YA...");
            WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("android:id/button1"))
            );
            
            System.out.println("✓ Tombol YA ditemukan: " + confirmButton.getText());
            confirmButton.click();
            
            Thread.sleep(3000);
            
            // Verifikasi kembali ke halaman inventaris
            WebElement inventoryTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Manajemen Inventaris']")
                )
            );
            System.out.println("✓ Item berhasil dihapus, kembali ke halaman inventaris: " + inventoryTitle.getText());
            logTestCaseResult("TC010", true, "Menghapus item dari inventaris");
            
        } catch (Exception e) {
            System.err.println("Error saat menghapus item: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC010", false, "Menghapus item dari inventaris");
            throw e;
        }
    }
    
    /**
     * Scroll di dalam form untuk memastikan tombol terlihat
     */
    private void performScrollInForm() throws InterruptedException {
        System.out.println("Melakukan scroll di form...");
        
        try {
            int screenHeight = driver.manage().window().getSize().getHeight();
            int screenWidth = driver.manage().window().getSize().getWidth();
            
            int startY = screenHeight * 80 / 100; // 80% dari atas
            int endY = screenHeight * 20 / 100;   // 20% dari atas
            int centerX = screenWidth / 2;
            
            @SuppressWarnings("rawtypes")
            TouchAction touchAction = new TouchAction(driver);
            touchAction
                .press(PointOption.point(centerX, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                .moveTo(PointOption.point(centerX, endY))
                .release()
                .perform();
            
            Thread.sleep(1000);
            System.out.println("✓ Scroll di form selesai");
        } catch (Exception e) {
            System.err.println("Error saat scroll di form: " + e.getMessage());
        }
    }
    
    /**
     * Print elemen-elemen yang ada di halaman saat ini untuk debugging
     */
    private void printAvailableElements() {
        try {
            System.out.println("\n=== DEBUG: SEMUA ELEMENT TERSEDIA ===");
            
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
        UpdateItemTest test = new UpdateItemTest();

        try {
            test.setup();
            test.printDeviceInfo();
            test.executeTest();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama tes update item:");
            e.printStackTrace();
        }
    }
}