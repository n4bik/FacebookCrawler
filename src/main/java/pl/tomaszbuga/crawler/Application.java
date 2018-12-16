package pl.tomaszbuga.crawler;

import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Application {
    public static void main(String[] args) throws IOException {
        FacebookLikesCounter facebookLikesCounter = new FacebookLikesCounter();
        ExportToExcel exportToExcel = new ExportToExcel(facebookLikesCounter);
        ArrayList<String> listOfWebsite = readListOfWebsite();

        for (int i = 0; i < listOfWebsite.size(); i++) {
            if (i == 0 || i % 2 == 0) {
                facebookLikesCounter.crawl(listOfWebsite.get(i), listOfWebsite.get(i + 1));
            }
        }

        exportToExcel.exportToXls("Likes.xlsx");

        try {
            HtmlEmailSender mailer = new HtmlEmailSender(HtmlEmailSender.GMAIL_CONFIG, ImmutablePair.of("sender@mail.com", "mailPassword"));
            mailer.sendHtmlEmailWithAttachment("recepient@mail.com", "Mail subject",
                    "Mail message",
                    "Likes.xlsx", "Filename.xlsx");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> readListOfWebsite() throws IOException {
        File file = new File("list-of-websites-to-crawl.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

        ArrayList<String> searchInfo = new ArrayList<>();
        String textLine;
        while ((textLine = br.readLine()) != null) {
            if (textLine.substring(0, 4).equals("url:")) {
                searchInfo.add(textLine.substring(4));
            } else if (textLine.substring(1, 5).equals("url:")) {
                searchInfo.add(textLine.substring(5));
            } else if (textLine.substring(0, 13).equals("website-name:")) {
                searchInfo.add(textLine.substring(13));
            }
        }
        br.close();
        return searchInfo;
    }
}
