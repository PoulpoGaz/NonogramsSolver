package fr.poulpogaz.nonogramssolver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class Monitor extends Canvas {

    private static final Logger LOGGER = LogManager.getLogger(Monitor.class);

    private static final int TPS = 60;

    private final Nonogram nonogram;

    private boolean running;
    private JFrame frame;
    private Thread thread;

    private double offsetX;
    private double offsetY;
    private double scale = 1;

    public Monitor(Nonogram nonogram) {
        this.nonogram = nonogram;

        setIgnoreRepaint(true);

        MouseAdapter mouseListener = createMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    public void runAsynchronously() {
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        frame = new JFrame("Nonogram Solver");

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.addWindowListener(createWindowListener());

        frame.setSize(1024, 576);
        frame.setVisible(true);

        thread = new Thread(this::run);
        thread.setName("Monitor thread");
        thread.start();
    }

    private WindowListener createWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        };
    }

    private MouseAdapter createMouseListener() {
        return new MouseAdapter() {

            private int lastMouseX;
            private int lastMouseY;
            private int mouseX;
            private int mouseY;


            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateMouseLocation(e);

                int v = (int) (Math.log(scale) / Math.log(2)) - e.getWheelRotation();

                v = Math.max(v, 0);

                if (v > 5) {
                    return;
                }

                double zoom1 = Monitor.this.scale;
                double zoom2 = (float) Math.pow(2, v);

                double newCameraX = mouseX - (mouseX - offsetX) * zoom2 / zoom1;
                double newCameraY = mouseY - (mouseY - offsetY) * zoom2 / zoom1;

                Monitor.this.scale = zoom2;
                Monitor.this.offsetX = newCameraX;
                Monitor.this.offsetY = newCameraY;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateMouseLocation(e);

                if (SwingUtilities.isMiddleMouseButton(e)) {
                    offsetX += (mouseX - lastMouseX);
                    offsetY += (mouseY - lastMouseY);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMouseLocation(e);
            }

            private void updateMouseLocation(MouseEvent e) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;

                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
    }

    private void run() {
        running = true;

        long lastTime = System.nanoTime();
        float ns = 1000000000.0f / TPS;
        float delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int ticks = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                ticks++;
                update(delta);

                delta--;
            }

            if (running) {
                render();
            }

            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                LOGGER.info("FPS: {}, TPS: {}", frames, ticks);

                frames = 0;
                ticks = 0;
            }
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }

        Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
        render(g2d);
        g2d.dispose();

        if (running) {
            bs.show();
        }
    }

    private void render(Graphics2D g2d) {
        double offsetX = this.offsetX;
        double offsetY = this.offsetY;
        double scale = this.scale;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.translate(offsetX, offsetY);
        nonogram.drawNonogram(g2d, (int) (getWidth() * scale), (int) (getHeight() * scale));
        g2d.translate(-offsetX, -offsetY);

        g2d.setColor(Color.BLACK);
        g2d.drawString("scale: " + scale + "; offset: (" + offsetX + "; " + offsetY + ")", 0, g2d.getFontMetrics().getHeight());
    }

    private void update(float delta) {

    }
}
