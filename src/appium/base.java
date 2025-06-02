package appium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class base {

    protected AndroidDriver<AndroidElement> driver;

    protected void setup() throws MalformedURLException {
        System.out.println("=== Memulai Setup Sesi Driver ===");

        File appDir = new File("src/appium");
        File app = new File(appDir, "CircuWear-debug.apk");

        // Pastikan file APK ada
        if (!app.exists()) {
            throw new RuntimeException("File APK tidak ditemukan: " + app.getAbsolutePath());
        }
        
        System.out.println("File APK ditemukan: " + app.getAbsolutePath());

        DesiredCapabilities cap = new DesiredCapabilities();
        
        // Basic capabilities
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Device");
        cap.setCapability(MobileCapabilityType.UDID, "e7a68173");
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());

        // SOLUSI UTAMA - Mencegah Appium melakukan operasi clear data
        cap.setCapability("noReset", true);        // PENTING: Jangan reset aplikasi
        cap.setCapability("fullReset", false);     // PENTING: Jangan full reset
        cap.setCapability("skipUnlock", true);     // Skip unlock device
        cap.setCapability("skipLogcatCapture", true); // Skip logcat untuk performa
        
        // Capabilities untuk mengatasi permission issues
        cap.setCapability("autoGrantPermissions", true);
        cap.setCapability("ignoreHiddenApiPolicyError", true);
        cap.setCapability("disableHiddenApiPolicyCheck", true);
        
        // Timeout settings yang lebih panjang
        cap.setCapability("newCommandTimeout", 300);
        cap.setCapability("androidInstallTimeout", 120000);
        cap.setCapability("appWaitDuration", 30000);
        
        // PENTING: Jangan spesifikasi package/activity, biarkan APK yang menentukan
        // Ini mencegah Appium mencoba mengakses package yang salah
        cap.setCapability("appWaitActivity", "*");
        
        // Capabilities tambahan untuk stabilitas
        cap.setCapability("uiautomator2ServerInstallTimeout", 60000);
        cap.setCapability("uiautomator2ServerLaunchTimeout", 60000);
        cap.setCapability("adbExecTimeout", 20000);
        
        // Nonaktifkan beberapa operasi otomatis yang bisa menyebabkan masalah
        cap.setCapability("skipServerInstallation", false);
        cap.setCapability("skipDeviceInitialization", false);
        cap.setCapability("suppressKillServer", true);

        URL url = new URL("http://127.0.0.1:4723/wd/hub");
        System.out.println("Menghubungkan ke Appium Server di: " + url);
        System.out.println("Menggunakan capabilities:");
        System.out.println("- noReset: " + cap.getCapability("noReset"));
        System.out.println("- fullReset: " + cap.getCapability("fullReset"));
        System.out.println("- APP: " + cap.getCapability("app"));

        try {
            this.driver = new AndroidDriver<>(url, cap);
            this.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            System.out.println("✅ Sesi driver berhasil dibuat!");
            
            // Tampilkan informasi session
            System.out.println("Session ID: " + this.driver.getSessionId());
            
        } catch (Exception e) {
            System.err.println("❌ Gagal membuat sesi driver: " + e.getMessage());
            System.err.println("Kemungkinan penyebab:");
            System.err.println("1. Aplikasi belum terinstall di device");
            System.err.println("2. Package name tidak cocok");
            System.err.println("3. Device tidak terhubung dengan benar");
            throw e;
        }
    }

    protected void tearDown() {
        if (this.driver != null) {
            System.out.println("=== Menutup Sesi Driver ===");
            try {
                this.driver.quit();
                System.out.println("✅ Driver berhasil ditutup.");
            } catch (Exception e) {
                System.err.println("⚠️ Error saat menutup driver: " + e.getMessage());
            }
        }
    }
    
    // Method helper untuk debugging
    protected void printDeviceInfo() {
        if (this.driver != null) {
            try {
                System.out.println("=== Informasi Device ===");
                System.out.println("Platform Name: " + this.driver.getCapabilities().getCapability("platformName"));
                System.out.println("Platform Version: " + this.driver.getCapabilities().getCapability("platformVersion"));
                System.out.println("Device Name: " + this.driver.getCapabilities().getCapability("deviceName"));
                System.out.println("UDID: " + this.driver.getCapabilities().getCapability("udid"));
            } catch (Exception e) {
                System.err.println("Tidak dapat mengambil informasi device: " + e.getMessage());
            }
        }
    }
}