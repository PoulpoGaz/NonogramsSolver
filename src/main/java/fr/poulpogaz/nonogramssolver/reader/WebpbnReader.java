package fr.poulpogaz.nonogramssolver.reader;

import fr.poulpogaz.nonogramssolver.Nonogram;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.*;

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

        return parseDocument(document);
    }

    private Nonogram parseDocument(Document document) {
        Nonogram.Builder builder = new Nonogram.Builder();
        builder.setBackground(Color.WHITE);

        List<NamedColor> colors = getColors(document);
        List<Node> clues = document.selectNodes("//clues");

        for (int i = 0; i < 2; i++) {
            Element element = (Element) clues.get(i);
            boolean row = element.attribute("type").getText().equals("rows");

            List<Element> children = element.elements();

            if (row) {
                builder.setHeight(children.size());
            } else {
                builder.setWidth(children.size());
            }

            for (int j = 0; j < children.size(); j++) {
                Element line = children.get(j);
                newLine(colors, builder, line, j, row);
            }
        }

        return builder.build();
    }

    private List<NamedColor> getColors(Document document) {
        List<Node> colorNodes = document.selectNodes("//color");

        List<NamedColor> colors = new ArrayList<>();
        for (Node n : colorNodes) {
            Element e = (Element) n;

            String colorName = e.attributeValue("color");
            Color color = new Color(Integer.parseInt(e.getText(), 16));

            colors.add(new NamedColor(colorName, color));
        }

        return colors;
    }

    private void newLine(List<NamedColor> colors, Nonogram.Builder builder, Element line, int i, boolean row) {
        List<Clue> clues = new ArrayList<>();

        for (int j = 0; j < line.nodeCount(); j++) {
            Element clue = (Element) line.node(j);
            int length = Integer.parseInt(clue.getText());

            if (length != 0) {
                String col = clue.attributeValue("color");

                if (col == null) {
                    clues.add(new Clue(length, colors.get(1).color()));
                } else {
                    clues.add(new Clue(length, getColor(colors, col)));
                }
            }
        }

        builder.setNumberOfClue(i, row, clues.size());
        for (Clue c : clues) {
            builder.addClue(i, row, c.length(), c.color());
        }
    }

    private Color getColor(List<NamedColor> colors, String col) {
        for (NamedColor c : colors) {
            if (c.name().equals(col)) {
                return c.color();
            }
        }

        throw new IllegalStateException();
    }

    private record NamedColor(String name, Color color) {}

    private record Clue(int length, Color color) {}
}
