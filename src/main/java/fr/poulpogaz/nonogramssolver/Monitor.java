package fr.poulpogaz.nonogramssolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import fr.poulpogaz.nonogramssolver.solver.SolverListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Monitor extends JPanel {

    private final Nonogram nonogram;

    private JFrame frame;
    private Timer timer;

    private double offsetX;
    private double offsetY;
    private double scale = 1;

    private final NonogramRenderer renderer = NonogramRenderer.DEFAULT;

    public Monitor(Nonogram nonogram) {
        this.nonogram = nonogram;

        MouseAdapter mouseListener = createMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    public SolverListener runAsynchronously(SolverListener listener) {
        SwingUtilities.invokeLater(this::init);
        return wrap(listener);
    }

    private void init() {
        frame = new JFrame("Nonogram Solver");

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.addWindowListener(createWindowListener());

        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        timer = new Timer(1000 / 30, e -> repaint());
        timer.setRepeats(true);
        timer.start();
    }

    public void shutdown() {
        frame.dispose();
    }

    private SolverListener wrap(SolverListener listener) {
        return new SolverListener() {
            @Override
            public void onLineSolved(Nonogram n, Description d, int mode) {
                listener.onLineSolved(n, d, mode);
            }

            @Override
            public void onPassFinished(Nonogram n, int mode) {
                listener.onPassFinished(n, mode);
            }

            @Override
            public void onContradiction(Nonogram n, boolean found) {
                listener.onContradiction(n, found);
            }

            @Override
            public void onSuccess(Nonogram n) {
                timer.stop();
                repaint();

                listener.onSuccess(n);
            }

            @Override
            public void onFail(Nonogram n) {
                timer.stop();
                repaint();

                listener.onSuccess(n);
            }
        };
    }



    private WindowListener createWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                }
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

                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateMouseLocation(e);

                if (SwingUtilities.isMiddleMouseButton(e)) {
                    offsetX += (mouseX - lastMouseX);
                    offsetY += (mouseY - lastMouseY);

                    repaint();
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.translate(this.offsetX, this.offsetY);
        renderer.drawNonogram(nonogram, g2d, (int) (getWidth() * this.scale), (int) (getHeight() * this.scale));
        g2d.translate(-this.offsetX, -this.offsetY);

        g2d.setColor(Color.BLACK);
        g2d.drawString("scale: " + this.scale + "; offset: (" + this.offsetX + "; " + this.offsetY + ")",
                0, g2d.getFontMetrics().getHeight());
    }
}
