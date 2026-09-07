package com.example.oyo_price_monitor;

import com.microsoft.playwright.*;

public class OyoTest {

    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            Page page = browser.newPage();

            page.navigate("https://www.oyorooms.com/192673/");

            System.out.println("Page title: " + page.title());
            System.out.println("Page URL: " + page.url());

            page.waitForTimeout(15000);

            browser.close();
        }
    }
}