package com.example.oyo_price_monitor.scraper;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                            .setHeadless(true)
            );

            Page page = browser.newPage();

            System.out.println("Opening hotel: " + hotelUrl);
            System.out.println("Check-in: " + checkIn);
            System.out.println("Check-out: " + checkOut);
            System.out.println("Adults: " + adults);
            System.out.println("Rooms: " + rooms);

            // Open OYO hotel pageee
            page.navigate(hotelUrl);

            // Wait for JavaScript-rendered contentt
            page.waitForTimeout(5000);

            System.out.println("Page title: " + page.title());
            System.out.println("Current URL: " + page.url());

            // Get complete visible page text
            String text = page.locator("body").innerText();

            System.out.println("\n========== SEARCHING PRICE ==========");

            /*
             * Expected OYO text:
             *
             * Total price
             * ₹1502
             *
             * Regex allows spaces/newlines between
             * "Total price" and the rupee amount.
             */

            Pattern totalPricePattern = Pattern.compile(
                    "Total\\s+price\\s*[\\r\\n\\s]*₹\\s*([0-9,]+)",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = totalPricePattern.matcher(text);

            if (matcher.find()) {
                String priceText = matcher.group(1);
                double totalPrice = Double.parseDouble(
                        priceText.replace(",", "")
                );

                System.out.println(
                        "Total price found: ₹" + totalPrice
                );

                browser.close();

                return totalPrice;
            }

            // Fallback: try to find room priceee
            Pattern roomPricePattern = Pattern.compile(
                    "Classic\\s*[\\r\\n\\s]*₹\\s*([0-9,]+)",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher roomMatcher = roomPricePattern.matcher(text);

            if (roomMatcher.find()) {

                String priceText = roomMatcher.group(1);

                double roomPrice = Double.parseDouble(
                        priceText.replace(",", "")
                );

                System.out.println(
                        "Room price found: ₹" + roomPrice
                );

                browser.close();

                return roomPrice;
            }

            // Price couldn't be found
            System.out.println("PRICE NOT FOUND");

            browser.close();

            throw new RuntimeException(
                    "Unable to extract OYO price from page"
            );
        }
    }
}