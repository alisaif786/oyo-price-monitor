
        package com.example.oyo_price_monitor.scraper;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class OyoPriceScraper {

    public double getPrice(
            String hotelUrl,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int rooms) {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            Page page = browser.newPage();

            System.out.println("Opening hotel: " + hotelUrl);
            System.out.println("Check-in: " + checkIn);
            System.out.println("Check-out: " + checkOut);
            System.out.println("Adults: " + adults);
            System.out.println("Rooms: " + rooms);

            page.navigate(hotelUrl);

            page.waitForTimeout(5000);

            System.out.println("Page title: " + page.title());
            System.out.println("Current URL: " + page.url());


            // ============================
            // PRINT BUTTONS
            // ============================

            System.out.println("\n========== BUTTONS ==========");

            Locator buttons = page.locator("button");

            int buttonCount = buttons.count();

            System.out.println("Total buttons: " + buttonCount);

            for (int i = 0; i < buttonCount; i++) {

                String text = buttons.nth(i).innerText().trim();

                if (!text.isEmpty()) {
                    System.out.println(
                            "Button [" + i + "] = " + text
                    );
                }
            }


            // ============================
            // PRINT INPUTS
            // ============================

            System.out.println("\n========== INPUTS ==========");

            Locator inputs = page.locator("input");

            int inputCount = inputs.count();

            System.out.println("Total inputs: " + inputCount);

            for (int i = 0; i < inputCount; i++) {

                Locator input = inputs.nth(i);

                System.out.println(
                        "Input [" + i + "]"
                                + " type=" + input.getAttribute("type")
                                + " placeholder=" + input.getAttribute("placeholder")
                );
            }


            // ============================
            // PAGE TEXT
            // ============================

            System.out.println("\n========== PAGE TEXT ==========");

            String text = page.locator("body").innerText();

            System.out.println(
                    text.substring(
                            0,
                            Math.min(text.length(), 5000)
                    )
            );


            // Screenshot
            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(Paths.get("oyo-page.png"))
                            .setFullPage(true)
            );


            // Keep browser open
            page.waitForTimeout(30000);

            browser.close();

            return 0;
        }
    }
}

