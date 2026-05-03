package modules;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DemoModule {

    private static final int ARRAY_SIZE = 100_000_000;

    public static JPanel run(CardLayout cardLayout, JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Description
        JLabel desc = new JLabel(
            "<html>For this demo, the program will <b>sum " + String.format("%,d", ARRAY_SIZE) +
            " random doubles</b> in two ways: sequentially and in parallel. Both approaches will produce the same result but they will differ in terms of the <b>elapsed real time</b>.</html>"
        );
        desc.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(desc, BorderLayout.NORTH);

        // Chart panel (custom painted)
        BarChartPanel chartPanel = new BarChartPanel();
        panel.add(chartPanel, BorderLayout.CENTER);

        // Run button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton runBtn = new JButton("Run Demo");
        bottom.add(runBtn);

        // Back button
        JButton backBtn = new JButton("Back to Menu");
        bottom.add(backBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        runBtn.addActionListener(e -> {
            runBtn.setEnabled(false);
            runBtn.setText("Running...");
            chartPanel.reset();

            SwingWorker<long[], Void> worker = new SwingWorker<>() {
                @Override
                protected long[] doInBackground() {
                    double[] data = generateData();

                    // Sequential
                    long seqStart = System.nanoTime();
                    double seqResult = sumSequential(data);
                    long seqTime = System.nanoTime() - seqStart;

                    // Parallel
                    long parStart = System.nanoTime();
                    double parResult = sumParallel(data);
                    long parTime = System.nanoTime() - parStart;

                    System.out.printf(
                        "Sequential: %.2f | Parallel: %.2f%n",
                        seqResult, parResult
                    );

                    return new long[]{ seqTime, parTime };
                }

                @Override
                protected void done() {
                    try {
                        long[] times = get();
                        chartPanel.setResults(times[0], times[1]);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    runBtn.setEnabled(true);
                    runBtn.setText("Run Again");
                }
            };

            worker.execute();
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        return panel;
    }

    // ===== BENCHMARK LOGIC =====

    private static double[] generateData() {
        double[] data = new double[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) data[i] = Math.random();
        return data;
    }

    private static double sumSequential(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum;
    }

    private static double sumParallel(double[] data) {
        int cores = Runtime.getRuntime().availableProcessors();
        int chunkSize = data.length / cores;
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        AtomicLong bits = new AtomicLong(0);

        Future<?>[] futures = new Future[cores];
        for (int c = 0; c < cores; c++) {
            final int start = c * chunkSize;
            final int end = (c == cores - 1) ? data.length : start + chunkSize;
            futures[c] = pool.submit(() -> {
                double localSum = 0;
                for (int i = start; i < end; i++) localSum += data[i];
                // Accumulate atomically using long bits
                long prev, next;
                do {
                    prev = bits.get();
                    next = Double.doubleToLongBits(
                        Double.longBitsToDouble(prev) + localSum
                    );
                } while (!bits.compareAndSet(prev, next));
            });
        }

        for (Future<?> f : futures) {
            try { f.get(); } catch (Exception e) { e.printStackTrace(); }
        }
        pool.shutdown();
        return Double.longBitsToDouble(bits.get());
    }

    // ===== CUSTOM BAR CHART =====

    static class BarChartPanel extends JPanel {
        private long seqNs = 0, parNs = 0;
        private boolean hasResults = false;

        BarChartPanel() {
            setPreferredSize(new Dimension(600, 300));
            setBackground(Color.WHITE);
        }

        void reset() {
            hasResults = false;
            repaint();
        }

        void setResults(long seqNs, long parNs) {
            this.seqNs = seqNs;
            this.parNs = parNs;
            this.hasResults = true;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int marginLeft = 80, marginBottom = 50, marginTop = 40, marginRight = 40;
            int chartW = w - marginLeft - marginRight;
            int chartH = h - marginBottom - marginTop;

            // Axes
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartH);
            g2.drawLine(marginLeft, marginTop + chartH,
                        marginLeft + chartW, marginTop + chartH);

            if (!hasResults) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                String msg = "Press \"Run Demo\" to see results";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg,
                    marginLeft + (chartW - fm.stringWidth(msg)) / 2,
                    marginTop + chartH / 2);
                return;
            }

            long maxNs = Math.max(seqNs, parNs);

            // Y axis labels
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= 4; i++) {
                long val = maxNs * i / 4;
                int y = marginTop + chartH - (int)((double) val / maxNs * chartH);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(marginLeft, y, marginLeft + chartW, y);
                g2.setColor(Color.DARK_GRAY);
                String label = String.format("%,d ms", val / 1_000_000);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, marginLeft - fm.stringWidth(label) - 6, y + 4);
            }

            // Bars
            int barWidth = chartW / 5;
            int gap = barWidth / 2;

            int seqX = marginLeft + gap;
            int parX = marginLeft + gap * 2 + barWidth;

            drawBar(g2, seqX, barWidth, seqNs, maxNs, marginTop, chartH,
                    new Color(66, 133, 244), "Sequential");
            drawBar(g2, parX, barWidth, parNs, maxNs, marginTop, chartH,
                    new Color(52, 168, 83), "Parallel");

            // Speedup ratio
            double speedup = (double) seqNs / parNs;
            String ratioText = String.format("Speedup: %.2fx faster", speedup);
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(new Color(30, 30, 30));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(ratioText,
                marginLeft + chartW - fm.stringWidth(ratioText),
                marginTop - 12);
        }

        private void drawBar(Graphics2D g2, int x, int barW,
                             long ns, long maxNs,
                             int marginTop, int chartH,
                             Color color, String label) {
            int barH = (int)((double) ns / maxNs * chartH);
            int y = marginTop + chartH - barH;

            g2.setColor(color);
            g2.fillRoundRect(x, y, barW, barH, 6, 6);

            // Time label above bar
            String timeLabel = String.format("%,d ms", ns / 1_000_000);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(Color.DARK_GRAY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(timeLabel,
                x + (barW - fm.stringWidth(timeLabel)) / 2,
                y - 6);

            // X axis label
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Color.DARK_GRAY);
            fm = g2.getFontMetrics();
            int labelH = marginTop + chartH + 20;
            g2.drawString(label,
                x + (barW - fm.stringWidth(label)) / 2,
                labelH);
        }
    }
}