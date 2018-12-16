package pl.tomaszbuga.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class FacebookLikesCounter {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> likes = new LinkedList<>();

    void crawl(String url, String websiteName) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();

            if (connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }

            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return;
            }

            Elements likesCounterDiv = htmlDocument.select("div._3xom");

            int counter = 0;
            for (Element like : likesCounterDiv) {
                if (counter == 0) {
                    this.likes.add(websiteName + " - Polubienia;" + like.text());
                    counter++;
                } else {
                    this.likes.add(websiteName + " - Obserwujący;" + like.text());
                    counter++;
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    List<String> getLikes() {
        return likes;
    }
}
