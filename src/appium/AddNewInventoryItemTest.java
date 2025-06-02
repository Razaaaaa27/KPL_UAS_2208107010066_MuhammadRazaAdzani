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

public class AddNewInventoryItemTest extends base {

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
        System.out.println("\n=== RINGKASAN HASIL TEST CASE ===");
        for (String result : testResults) {
            System.out.println(result);
        }
        System.out.println("=== TES OTOMASI SELESAI ===");
    }

    /**
     * Metode utama untuk menjalankan tes tambah item baru
     */
    public void executeTest() throws InterruptedException {
        System.out.println("=== MEMULAI TES TAMBAH ITEM BARU ===");
        
        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            Thread.sleep(3000);
            skipOnboardingIfPresent(wait);
            performLogin(wait);
            navigateToAdminDashboard(wait);
            openAdminPanelMenu(wait);
            navigateToInventoryPage(wait);
            addNewItem(wait);
            
        } catch (Exception e) {
            System.err.println("Error dalam tes tambah item baru: " + e.getMessage());
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
        logTestCaseStart("TC001", "Melewati onboarding jika ada");
        
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
            logTestCaseResult("TC001", true, "Skip Onboarding");
            
        } catch (Exception e) {
            System.out.println("Tidak ada onboarding atau sudah selesai");
            logTestCaseResult("TC001", true, "Skip Onboarding");
        }
    }
    
    /**
     * Melakukan login dengan kredensial admin
     */
    private void performLogin(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC002", "Login sebagai admin");
        
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
            logTestCaseResult("TC002", true, "Login Admin");
            
        } catch (Exception e) {
            System.err.println("Error saat login: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC002", false, "Login Admin");
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
            logTestCaseResult("TC003", true, "Navigasi Dashboard");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke dashboard admin: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC003", false, "Navigasi Dashboard");
            throw e;
        }
    }
    
    /**
     * Buka menu admin panel
     */
    private void openAdminPanelMenu(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC004", "Navigasi ke halaman inventaris");
        
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
                
                WebElement navHeader = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//android.widget.TextView[@text='Admin Panel']")
                    )
                );
                System.out.println("✓ Menu admin panel terbuka: " + navHeader.getText());
            }
            
        } catch (Exception e) {
            System.err.println("Error saat membuka menu admin panel: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC004", false, "Buka Inventaris");
            throw e;
        }
    }
    
    /**
     * Navigasi ke halaman inventaris
     */
    private void navigateToInventoryPage(WebDriverWait wait) throws InterruptedException {
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
            logTestCaseResult("TC004", true, "Buka Inventaris");
            
        } catch (Exception e) {
            System.err.println("Error saat navigasi ke halaman inventaris: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC004", false, "Buka Inventaris");
            throw e;
        }
    }
    
    /**
     * Menambah item baru di halaman inventaris
     */
    private void addNewItem(WebDriverWait wait) throws InterruptedException {
        logTestCaseStart("TC005", "Menambahkan item baru ke inventaris");
        
        try {
            // Klik tombol Tambah Item
            System.out.println("Mencari tombol Tambah Item...");
            WebElement addItemButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnAddItem"))
            );
            System.out.println("✓ Tombol Tambah Item ditemukan, melakukan klik...");
            addItemButton.click();
            
            Thread.sleep(2000);
            
            // Verifikasi form Tambah Item Baru
            WebElement formTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.TextView[@text='Tambah Item Baru']")
                )
            );
            System.out.println("✓ Form Tambah Item Baru terbuka: " + formTitle.getText());
            
            // Isi form detail item
            System.out.println("Mengisi detail item...");
            
            // Nama item
            WebElement nameField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etItemName"))
            );
            nameField.clear();
            nameField.sendKeys("Gaun Pesta Elegan");
            System.out.println("✓ Nama item diisi: Gaun Pesta Elegan");
            
            // Harga
            WebElement priceField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etItemPrice"))
            );
            priceField.clear();
            priceField.sendKeys("250000");
            System.out.println("✓ Harga diisi: Rp 250.000");
            
            // Kode item
            WebElement codeField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/etItemCode"))
            );
            codeField.clear();
            codeField.sendKeys("GP-001");
            System.out.println("✓ Kode item diisi: GP-001");
            
            // Pilih kategori
            System.out.println("Memilih kategori...");
            WebElement categorySpinner = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/spinnerCategory"))
            );
            categorySpinner.click();
            Thread.sleep(3000); // Tunggu dropdown muncul
            
            // Cetak opsi untuk debugging
            printSpinnerOptions(categorySpinner);
            
            try {
                WebElement categoryOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.CheckedTextView[@text='Gaun Formal']")
                    )
                );
                categoryOption.click();
                System.out.println("✓ Kategori dipilih: Gaun Formal");
            } catch (Exception e) {
                System.err.println("Gagal memilih 'Gaun Formal', mencoba opsi pertama...");
                WebElement fallbackOption = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.CheckedTextView[1]"))
                );
                fallbackOption.click();
                System.out.println("✓ Kategori default dipilih");
            }
            
            // Pilih tipe
            System.out.println("Memilih tipe...");
            WebElement typeSpinner = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/spinnerType"))
            );
            typeSpinner.click();
            Thread.sleep(3000);
            
            try {
                WebElement typeOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.CheckedTextView[@text='formal']")
                    )
                );
                typeOption.click();
                System.out.println("✓ Tipe dipilih: formal");
            } catch (Exception e) {
                System.err.println("Gagal memilih 'formal', mencoba opsi pertama...");
                WebElement fallbackOption = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.CheckedTextView[1]"))
                );
                fallbackOption.click();
                System.out.println("✓ Tipe default dipilih");
            }
            
            // Pilih status
            System.out.println("Memilih status...");
            WebElement statusSpinner = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/spinnerStatus"))
            );
            statusSpinner.click();
            Thread.sleep(3000);
            
            try {
                WebElement statusOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//android.widget.CheckedTextView[@text='Tersedia']")
                    )
                );
                statusOption.click();
                System.out.println("✓ Status dipilih: Tersedia");
            } catch (Exception e) {
                System.err.println("Gagal memilih 'Tersedia', mencoba opsi pertama...");
                WebElement fallbackOption = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.CheckedTextView[1]"))
                );
                fallbackOption.click();
                System.out.println("✓ Status default dipilih");
            }
            
            // Scroll untuk memastikan tombol Simpan terlihat
            performScrollInForm();
            
            // Klik tombol Simpan Item
            System.out.println("Mencari tombol Simpan Item...");
            WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.smallacademy.userroles:id/btnSaveItem"))
            );
            System.out.println("✓ Tombol Simpan Item ditemukan, melakukan klik...");
            saveButton.click();
            
            Thread.sleep(3000);
            
            // Verifikasi item tersimpan
            try {
                WebElement successMessage = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//android.widget.TextView[@text='Item berhasil ditambahkan']")
                    )
                );
                System.out.println("✓ Item berhasil ditambahkan: " + successMessage.getText());
            } catch (Exception e) {
                System.out.println("Pesan sukses tidak ditemukan, memeriksa kembali halaman inventaris...");
                WebElement inventoryTitle = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//android.widget.TextView[@text='Manajemen Inventaris']")
                    )
                );
                System.out.println("✓ Kembali ke halaman inventaris: " + inventoryTitle.getText());
            }
            
            logTestCaseResult("TC005", true, "Tambah Item");
            
        } catch (Exception e) {
            System.err.println("Error saat menambah item baru: " + e.getMessage());
            printAvailableElements();
            logTestCaseResult("TC005", false, "Tambah Item");
            throw e;
        }
    }
    
    /**
     * Scroll di dalam form untuk memastikan tombol Simpan terlihat
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
     * Cetak opsi yang tersedia di dropdown spinner untuk debugging
     */
    private void printSpinnerOptions(WebElement spinner) {
        System.out.println("\n=== OPSI SPINNER TERSEDIA ===");
        
        try {
            List<? extends WebElement> options = driver.findElements(By.className("android.widget.CheckedTextView"));
            if (options.isEmpty()) {
                System.out.println("Tidak ada opsi ditemukan di dropdown");
            } else {
                for (int i = 0; i < options.size(); i++) {
                    String text = options.get(i).getText();
                    System.out.println("  " + (i+1) + ". Opsi: '" + text + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("Error saat mencetak opsi spinner: " + e.getMessage());
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
            System.out.println("Buttons (" + buttons.size() + "):");
            for (int i = 0; i < buttons.size() && i < 15; i++) {
                var button = buttons.get(i);
                String text = button.getText();
                String resourceId = button.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
            }
            
            // Tombol ImageButton
            var imageButtons = driver.findElements(By.className("android.widget.ImageButton"));
            System.out.println("\nImageButtons (" + imageButtons.size() + "):");
            for (int i = 0; i < imageButtons.size() && i < 15; i++) {
                var button = imageButtons.get(i);
                String contentDesc = button.getAttribute("content-desc");
                String resourceId = button.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Content-Desc: '" + contentDesc + "' | ID: '" + resourceId + "'");
            }
            
            // TextView
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
            
            // EditText
            var editTexts = driver.findElements(By.className("android.widget.EditText"));
            System.out.println("\nEditTexts (" + editTexts.size() + "):");
            for (int i = 0; i < editTexts.size() && i < 15; i++) {
                var editText = editTexts.get(i);
                String text = editText.getText();
                String resourceId = editText.getAttribute("resource-id");
                System.out.println("  " + (i+1) + ". Text: '" + text + "' | ID: '" + resourceId + "'");
            }
            
            // CheckedTextView untuk opsi dropdown
            var checkedTextViews = driver.findElements(By.className("android.widget.CheckedTextView"));
            System.out.println("\nCheckedTextViews (" + checkedTextViews.size() + "):");
            for (int i = 0; i < checkedTextViews.size() && i < 15; i++) {
                var checkedTextView = checkedTextViews.get(i);
                String text = checkedTextView.getText();
                System.out.println("  " + (i+1) + ". Text: '" + text + "'");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat debugging: " + e.getMessage());
        }
    }

    /**
     * Method main untuk menjalankan tes
     */
    public static void main(String[] args) {
        AddNewInventoryItemTest test = new AddNewInventoryItemTest();

        try {
            test.setup();
            test.printDeviceInfo();
            test.executeTest();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan selama tes tambah item baru:");
            e.printStackTrace();
        }
    }
}