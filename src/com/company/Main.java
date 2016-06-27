package com.company;

import com.jaunt.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    // URLs are the exact url publicly available when one makes the classes respective google search

    private static String [] urlsToVisit = {
            // Class 0 - Windows Desktop
            "https://www.google.ca/search?q=desktop&safe=off&hl=en&authuser=0&biw=1129&bih=749&site=imghp&tbs=isz:ex,iszw:1024,iszh:768&tbm=isch&source=lnt",
            // Class 1 - Macintosh Desktop
            "https://www.google.ca/search?q=desktop&safe=off&hl=en&authuser=0&biw=1129&bih=749&site=imghp&tbs=isz:ex,iszw:1024,iszh:768&tbm=isch&source=lnt#safe=off&hl=en&authuser=0&tbs=isz:ex%2Ciszw:1024%2Ciszh:768&tbm=isch&q=mac+desktop",
            // Class 2 - Ubuntu (Linux) Desktop
            "https://www.google.ca/search?q=desktop&safe=off&hl=en&authuser=0&biw=1129&bih=749&site=imghp&tbs=isz:ex,iszw:1024,iszh:768&tbm=isch&source=lnt#safe=off&hl=en&authuser=0&tbs=isz:ex%2Ciszw:1024%2Ciszh:768&tbm=isch&q=ubuntu+desktop"
    };

    // Exact Image Width and Image Height required for the image to be a candidate test data point
    private static String imageWidth;
    private static String imageHeight;

    public static void main(String[] args) {
        System.out.println("A web scraper built using the jaunt api");

        // Criteria for validating image dimensions
        if (args.length > 0) {
            imageHeight = args[0];
            imageWidth = args[1];
        } else {
            imageHeight = "768";
            imageWidth = "1024";
        }

        // Set up UserAgent to visit sites
        UserAgent userAgent = new UserAgent();
        visitSites(userAgent);
    }

    private static void visitSites(UserAgent userAgent) {
        int classNumber = 0;
        // Visit each site and scrape images that meet the required dimensions
        for (String url: urlsToVisit
             ) {
            try {
                userAgent.visit(url);
                ArrayList<URL> urls = scrapeImages(userAgent.doc.findEach("<img>"));
                writeImagesToFile(urls, classNumber);
                classNumber++;
            } catch (JauntException e) {
                System.err.println(e);
            }

        }
    }

    private static ArrayList<URL> scrapeImages(Elements imageElements) {

        ArrayList<URL> urls = new ArrayList<URL>();
        URL url;
        for (Element image: imageElements
                ) {
                try {
                    if (
                            (!image.hasAttribute("height") || !image.hasAttribute("width"))
                                    || (image.getAt("height").equals(imageHeight) || image.getAt("width").equals(imageWidth))) {

                        System.out.println("Image was not an acceptable data point.");
                    } else {

                        url =  new URL(image.getAt("src"));
                        urls.add(url);

                    }

                } catch (JauntException jauntException) {
                    System.err.println("Jaunt Exception: " + jauntException);
                } catch (MalformedURLException malformedURLException) {
                    System.err.println("MalformedURL Exception: " + malformedURLException);
                } catch (NullPointerException nullPointerException) {
                    System.err.println("NullPointer Exception: " + nullPointerException);
                }
            }
        return urls;
        }

    private static void writeImagesToFile(ArrayList<URL> urls, int classNumber) {
        BufferedImage image;
        int i = 0;
        try {
            for (URL url : urls
                    ) {
                image = ImageIO.read(url);

                ImageIO.write(image, "jpg", new File("images/class-" + classNumber + "-scraped-image-" + String.valueOf(i) + ".jpg"));
                i++;
            }
        } catch (IOException ioException) {
            System.err.println("IO Exception: " + ioException);
        }
    }
}
