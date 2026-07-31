package com.dka.cloud.storage.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class LiquidProgressBar extends JComponent {

    private int percentage = 0;
    private double wavePhase = 0;
    private Color waveColor = new Color(76, 175, 80);
    private Timer animationTimer;

    public LiquidProgressBar() {
        setPreferredSize(new Dimension(150, 150));
        setOpaque(false);

        animationTimer = new Timer(30, e -> {
            wavePhase += 0.1;
            repaint();
        });
        animationTimer.start();
    }

    public void setPercentage(int percentage) {
        this.percentage = Math.max(0, Math.min(100, percentage));
        updateColor();
        repaint();
    }

    private void updateColor() {
        if (percentage >= 90) waveColor = new Color(244, 67, 54);
        else if (percentage >= 70) waveColor = new Color(255, 152, 0);
        else waveColor = new Color(76, 175, 80);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 10;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        Ellipse2D circle = new Ellipse2D.Double(x, y, size, size);

        // Clip jadi lingkaran penuh
        g2.setClip(circle);

        // Background lingkaran (area kosong)
        g2.setColor(new Color(235, 235, 235));
        g2.fill(new Rectangle(x, y, size, size));

        int liquidHeight = (int) (size * (percentage / 100.0));
        int liquidTop = y + size - liquidHeight;

        // Gambar 3 lapis gelombang: belakang (paling transparan/jauh) -> depan (paling solid)
        drawWaveLayer(g2, x, y, size, liquidTop, waveOpacity(0.35f), 10, 50, wavePhase * 0.7, 6);
        drawWaveLayer(g2, x, y, size, liquidTop, waveOpacity(0.55f), 8, 45, wavePhase * 1.0 + 1.5, 3);
        drawWaveLayer(g2, x, y, size, liquidTop, waveColor, 6, 40, wavePhase * 1.3 + 3.0, 0);

        // Border lingkaran wadah
        g2.setClip(null);
        g2.setColor(new Color(190, 190, 190));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(circle);

        // Teks persentase di tengah
        g2.setColor(percentage > 50 ? Color.WHITE : new Color(60, 60, 60));
        g2.setFont(getFont().deriveFont(Font.BOLD, size / 5f));
        String text = percentage + "%";
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + size / 2 + fm.getAscent() / 2 - 4;
        g2.drawString(text, textX, textY);

        g2.dispose();
    }

    // Gambar satu lapisan gelombang
    private void drawWaveLayer(Graphics2D g2, int x, int y, int size, int liquidTop,
                                 Color color, int amplitude, int waveLength, double phase, int yOffset) {
        Path2D wave = new Path2D.Double();
        int top = liquidTop + yOffset;

        wave.moveTo(x, top);
        for (int i = 0; i <= size; i++) {
            double wy = top + amplitude * Math.sin((i / (double) waveLength) + phase);
            wave.lineTo(x + i, wy);
        }
        wave.lineTo(x + size, y + size);
        wave.lineTo(x, y + size);
        wave.closePath();

        g2.setColor(color);
        g2.fill(wave);
    }

    // Bikin warna waveColor jadi transparan (buat layer belakang)
    private Color waveOpacity(float alpha) {
        return new Color(waveColor.getRed(), waveColor.getGreen(), waveColor.getBlue(), (int) (alpha * 255));
    }

    public void stopAnimation() {
        animationTimer.stop();
    }
}