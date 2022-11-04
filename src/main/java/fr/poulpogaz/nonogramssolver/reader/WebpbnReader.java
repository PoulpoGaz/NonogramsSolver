package fr.poulpogaz.nonogramssolver.reader;

import fr.poulpogaz.nonogramssolver.Nonogram;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO: color support
public class WebpbnReader {

    private static final SAXReader reader = new SAXReader();
    private static final URI uri = URI.create("https://webpbn.com/XMLpuz.cgi");

    public Nonogram read(String id) throws IOException, InterruptedException {
        String post = "id=%s&version=0&restore=undefined&sid=undefined".formatted(id);

        HttpRequest req = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(post))
                .build();

        HttpResponse<InputStream> rep = HttpUtils.CLIENT.send(req, HttpResponse.BodyHandlers.ofInputStream());

        if (rep.statusCode() != HttpURLConnection.HTTP_OK) {
            rep.body().close();

            throw new IOException("Bad status: " + rep.statusCode());
        }

        Document document;
        try {
            document = reader.read(rep.body());
        } catch (DocumentException e) {
            throw new IOException(e);
        } finally {
            rep.body().close();
        }

        return null;// parseDocument(document);
    }

    /*private Nonogram parseDocument(Document document) {
        List<Node> clues = document.selectNodes("//clues");

        int[][] rows = null;
        int[][] cols = null;

        for (int i = 0; i < 2; i++) {
            Element n = (Element) clues.get(i);

            if (n.attribute("type").getText().equals("rows")) {
                rows = createArray(n);
            } else {
                cols = createArray(n);
            }
        }

        Objects.requireNonNull(rows);
        Objects.requireNonNull(cols);
        return new Nonogram(rows, cols);
    }

    private int[][] createArray(Element clues) {
        List<Element> elements = clues.elements();
        int[][] array = new int[elements.size()][];

        for (int i = 0; i < elements.size(); i++) {
            Element line = elements.get(i);

            List<Integer> ints = new ArrayList<>();

            for (int j = 0; j < line.nodeCount(); j++) {
                int v = Integer.parseInt(line.node(j).getText());

                if (v != 0) {
                    ints.add(v);
                }
            }

            array[i] = Utils.toArray(ints);
        }

        return array;
    }*/
}
