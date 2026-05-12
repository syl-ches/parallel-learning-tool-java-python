package modules;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * VisualizationModule
 *
 * Shows HOW parallel execution happens in real-time inside the main GUI.
 *
 * Two public methods:
 * show() — kept so other code that still calls it won't break.
 * Returns a placeholder string. Does NOT start simulation.
 * createPanel() — called by Main.java to embed the live visualization
 * panel directly inside the app window.
 *
 * The simulation only starts when the user clicks [Run Simulation].
 */
public class VisualizationModule {

    /**
     * Legacy entry point — returns a plain String, does NOT start anything.
     * Kept so Main.java can be updated to call createPanel() instead.
     */
    public static String show() {
        return "Visualization Module\n\nClick 'Run Simulation' to begin.";
    }

    /**
     * Returns the live visualization JPanel to be embedded in Main.java.
     * Nothing runs until the user presses the Run button.
     */
    public static JPanel createPanel() {
        return new VisualizationPanel();
    }

    // =========================================================================
    // COLORS
    // =========================================================================
    static final Color C_BG = new Color(0x1E1E2E);
    static final Color C_PANEL = new Color(0x2A2A3E);
    static final Color C_BORDER = new Color(0x44475A);
    static final Color C_TEXT = new Color(0xCDD6F4);
    static final Color C_MUTED = new Color(0x6C7086);
    static final Color C_ACCENT = new Color(0x89B4FA);
    static final Color C_GREEN = new Color(0xA6E3A1);
    static final Color C_YELLOW = new Color(0xF9E2AF);
    static final Color C_RED = new Color(0xF38BA8);
    static final Color C_SURFACE = new Color(0x313244);

    static final Color[] THREAD_COLORS = {
            new Color(0x89B4FA), // blue
            new Color(0xA6E3A1), // green
            new Color(0xCBA6F7), // mauve
            new Color(0xFAB387), // peach
            new Color(0x94E2D5), // teal
            new Color(0xF5C2E7), // pink
    };

    // =========================================================================
    // DATA CLASSES
    // =========================================================================

    enum Status {
        IDLE, RUNNING, WAITING, LOCKED
    }

    static class ThreadInfo {
        final int id;
        final Color color;
        volatile Status status = Status.IDLE;
        volatile float progress = 0f;
        volatile int tasksCompleted = 0;

        ThreadInfo(int id, Color color) {
            this.id = id;
            this.color = color;
        }
    }

    static class TaskSpec {
        final int id, owner, duration;
        final boolean needLock;

        TaskSpec(int id, int owner, boolean needLock, int duration) {
            this.id = id;
            this.owner = owner;
            this.needLock = needLock;
            this.duration = duration;
        }
    }

    static class CompletedBar {
        final int thread, taskId;
        final long start, end;
        final boolean locked;
        final Color color;

        CompletedBar(int thread, int taskId,
                long start, long end,
                boolean locked, Color color) {
            this.thread = thread;
            this.taskId = taskId;
            this.start = start;
            this.end = end;
            this.locked = locked;
            this.color = color;
        }
    }

    // =========================================================================
    // MAIN PANEL — embedded inside Main.java's card layout
    // =========================================================================
    static class VisualizationPanel extends JPanel {

        // ── simulation state ──────────────────────────────────────────────
        int numThreads = 3;
        int numTasks = 6;
        boolean running = false;
        long startTime = 0;
        long wallTime = 0;

        final List<ThreadInfo> threads = new ArrayList<>();
        final List<CompletedBar> bars = new ArrayList<>();
        final List<String> events = new ArrayList<>();

        final AtomicBoolean lockHeld = new AtomicBoolean(false);
        final AtomicInteger lockHolder = new AtomicInteger(-1);

        // ── sub-panels ────────────────────────────────────────────────────
        GanttPanel gantt;
        LogPanel logPanel;
        StatsPanel stats;

        // ── controls ─────────────────────────────────────────────────────
        JButton runBtn;
        JSlider thrSlider, tskSlider;
        JLabel thrVal, tskVal;

        javax.swing.Timer repaintTimer;

        // =================================================================
        VisualizationPanel() {
            setLayout(new BorderLayout());
            setBackground(C_BG);

            add(buildHeader(), BorderLayout.NORTH);
            add(buildCenter(), BorderLayout.CENTER);
            add(buildControls(), BorderLayout.SOUTH);

            // 30 fps repaint while simulation is running
            repaintTimer = new javax.swing.Timer(33, e -> {
                if (running) {
                    gantt.repaint();
                    stats.repaint();
                }
            });
            repaintTimer.start();
        }

        // ── header ────────────────────────────────────────────────────────
        JPanel buildHeader() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(C_PANEL);
            p.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, C_BORDER),
                    new EmptyBorder(8, 14, 8, 14)));

            JLabel sub = new JLabel(
                    "Watch threads run, block on a shared lock, and complete tasks in real time");
            sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
            sub.setForeground(C_MUTED);
            p.add(sub, BorderLayout.CENTER);
            return p;
        }

        // ── center: gantt (left) + log (right) ───────────────────────────
        JPanel buildCenter() {
            gantt = new GanttPanel();
            stats = new StatsPanel();
            logPanel = new LogPanel();
            logPanel.setPreferredSize(new Dimension(195, 0));

            JPanel left = new JPanel(new BorderLayout());
            left.setBackground(C_BG);
            left.add(gantt, BorderLayout.CENTER);
            left.add(stats, BorderLayout.SOUTH);

            JSplitPane split = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT, left, logPanel);
            split.setDividerLocation(490);
            split.setDividerSize(1);
            split.setBackground(C_BG);
            split.setBorder(null);

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(C_BG);
            wrap.add(split, BorderLayout.CENTER);
            return wrap;
        }

        // ── controls bar ─────────────────────────────────────────────────
        JPanel buildControls() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 7));
            p.setBackground(C_PANEL);
            p.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDER));

            runBtn = new JButton("▶  Run Simulation");
            runBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
            runBtn.setBackground(C_ACCENT);
            runBtn.setForeground(Color.BLACK);
            runBtn.setFocusPainted(false);
            runBtn.setBorderPainted(false);
            runBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            runBtn.addActionListener(e -> startSimulation());
            p.add(runBtn);

            p.add(vsep());

            p.add(lbl("Threads:"));
            thrSlider = mkSlider(2, 6, numThreads);
            thrVal = valLbl(numThreads);
            thrSlider.addChangeListener(e -> {
                numThreads = thrSlider.getValue();
                thrVal.setText(String.valueOf(numThreads));
            });
            p.add(thrSlider);
            p.add(thrVal);

            p.add(vsep());

            p.add(lbl("Tasks:"));
            tskSlider = mkSlider(3, 16, numTasks);
            tskVal = valLbl(numTasks);
            tskSlider.addChangeListener(e -> {
                numTasks = tskSlider.getValue();
                tskVal.setText(String.valueOf(numTasks));
            });
            p.add(tskSlider);
            p.add(tskVal);

            p.add(vsep());

            // legend
            p.add(dot(C_ACCENT));
            p.add(lbl("Working"));
            p.add(dot(C_RED));
            p.add(lbl("Locked"));
            p.add(dot(C_YELLOW));
            p.add(lbl("Waiting"));

            return p;
        }

        // ── small UI helpers ──────────────────────────────────────────────
        JLabel lbl(String t) {
            JLabel l = new JLabel(t);
            l.setFont(new Font("Monospaced", Font.PLAIN, 11));
            l.setForeground(C_MUTED);
            return l;
        }

        JLabel valLbl(int v) {
            JLabel l = new JLabel(String.valueOf(v));
            l.setFont(new Font("Monospaced", Font.BOLD, 12));
            l.setForeground(C_ACCENT);
            l.setPreferredSize(new Dimension(18, 20));
            return l;
        }

        JSlider mkSlider(int min, int max, int val) {
            JSlider s = new JSlider(min, max, val);
            s.setBackground(C_PANEL);
            s.setPreferredSize(new Dimension(80, 24));
            return s;
        }

        JSeparator vsep() {
            JSeparator s = new JSeparator(JSeparator.VERTICAL);
            s.setPreferredSize(new Dimension(1, 20));
            s.setForeground(C_BORDER);
            return s;
        }

        JPanel dot(Color c) {
            return new JPanel() {
                {
                    setBackground(C_PANEL);
                    setPreferredSize(new Dimension(12, 18));
                }

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(c);
                    g.fillOval(1, 4, 10, 10);
                }
            };
        }

        // =================================================================
        // SIMULATION LOGIC
        // =================================================================

        void startSimulation() {
            if (running)
                return;

            // reset state
            running = true;
            startTime = System.currentTimeMillis();
            wallTime = 0;
            threads.clear();
            bars.clear();
            events.clear();
            lockHeld.set(false);
            lockHolder.set(-1);

            // lock controls while running
            runBtn.setEnabled(false);
            runBtn.setText("Running...");
            thrSlider.setEnabled(false);
            tskSlider.setEnabled(false);

            // build thread descriptors
            for (int i = 0; i < numThreads; i++)
                threads.add(new ThreadInfo(i, THREAD_COLORS[i % THREAD_COLORS.length]));

            // build tasks — round-robin; every 3rd task needs the shared lock
            List<TaskSpec> tasks = new ArrayList<>();
            for (int t = 0; t < numTasks; t++)
                tasks.add(new TaskSpec(
                        t,
                        t % numThreads,
                        (t % 3 == 2),
                        900 + (int) (Math.random() * 1300)));

            log("▶  Started — " + numThreads + " threads, " + numTasks + " tasks");

            ExecutorService pool = Executors.newFixedThreadPool(numThreads);
            for (TaskSpec task : tasks)
                pool.submit(() -> runTask(task));
            pool.shutdown();

            // watcher thread — fires onDone() when pool finishes
            new Thread(() -> {
                try {
                    pool.awaitTermination(60, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
                SwingUtilities.invokeLater(this::onDone);
            }).start();
        }

        // runs on a worker thread
        void runTask(TaskSpec task) {
            ThreadInfo ti = threads.get(task.owner);
            long taskStart = elapsed();

            SwingUtilities.invokeLater(() -> {
                ti.progress = 0f;
                if (task.needLock) {
                    ti.status = Status.WAITING;
                    log("🟡 Thread-" + task.owner
                            + " waiting for lock  [Task-" + task.id + "]");
                } else {
                    ti.status = Status.RUNNING;
                    log("🔵 Thread-" + task.owner + " started Task-" + task.id);
                }
            });

            // spin-wait for the shared lock
            if (task.needLock) {
                while (!lockHeld.compareAndSet(false, true)) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ignored) {
                    }
                }
                lockHolder.set(task.owner);
                SwingUtilities.invokeLater(() -> {
                    ti.status = Status.LOCKED;
                    log("🔒 Thread-" + task.owner
                            + " acquired lock  [Task-" + task.id + "]");
                });
            }

            // do work in small chunks so the progress bar animates
            int chunks = 12;
            for (int c = 0; c < chunks; c++) {
                try {
                    Thread.sleep(task.duration / chunks);
                } catch (InterruptedException ignored) {
                }
                final float pct = (c + 1f) / chunks;
                SwingUtilities.invokeLater(() -> ti.progress = pct);
            }

            // release lock
            if (task.needLock) {
                lockHeld.set(false);
                lockHolder.set(-1);
                SwingUtilities.invokeLater(() -> log("🔓 Thread-" + task.owner
                        + " released lock  [Task-" + task.id + "]"));
            }

            long end = elapsed();
            boolean wasLock = task.needLock;
            Color barColor = ti.color;

            SwingUtilities.invokeLater(() -> {
                bars.add(new CompletedBar(
                        task.owner, task.id,
                        taskStart, end,
                        wasLock, barColor));
                ti.tasksCompleted++;
                ti.progress = 0f;
                ti.status = Status.IDLE;
                log("✔  Thread-" + task.owner
                        + " done Task-" + task.id
                        + "  (" + (end - taskStart) + " ms)");
            });
        }

        // called on EDT when all tasks finish
        void onDone() {
            wallTime = elapsed();
            running = false;
            log("✅  All tasks complete — wall time: " + wallTime + " ms");
            gantt.repaint();
            stats.repaint();
            logPanel.repaint();
            runBtn.setEnabled(true);
            runBtn.setText("▶  Run Again");
            thrSlider.setEnabled(true);
            tskSlider.setEnabled(true);
        }

        long elapsed() {
            return System.currentTimeMillis() - startTime;
        }

        void log(String msg) {
            if (SwingUtilities.isEventDispatchThread()) {
                events.add(msg);
                logPanel.repaint();
            } else {
                SwingUtilities.invokeLater(() -> {
                    events.add(msg);
                    logPanel.repaint();
                });
            }
        }

        // =================================================================
        // GANTT PANEL
        // =================================================================
        class GanttPanel extends JPanel {

            static final int LABEL_W = 82;
            static final int PAD_TOP = 22;
            static final int PAD_R = 10;
            static final int ROW_H = 44;
            static final int BAR_PAD = 5;

            GanttPanel() {
                setBackground(C_BG);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                rh(g2);

                int W = getWidth(), H = getHeight();

                g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                g2.setColor(C_MUTED);
                g2.drawString("THREAD TIMELINE (GANTT CHART)", 8, 14);

                if (threads.isEmpty()) {
                    g2.setColor(C_MUTED);
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    g2.drawString("Press  ▶ Run Simulation  to begin.",
                            W / 2 - 140, H / 2);
                    return;
                }

                long nowMs = running ? elapsed() : (wallTime > 0 ? wallTime : 1);
                long maxT = Math.max(nowMs, 200);
                int chartX = LABEL_W;
                int chartW = W - chartX - PAD_R;
                int top = PAD_TOP;

                // grid
                int gridN = 5;
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND, 0, new float[] { 4, 4 }, 0));
                for (int gi = 0; gi <= gridN; gi++) {
                    int gx = chartX + gi * chartW / gridN;
                    g2.drawLine(gx, top, gx, top + threads.size() * ROW_H);
                }
                g2.setStroke(new BasicStroke(1));

                // rows
                for (int i = 0; i < threads.size(); i++) {
                    ThreadInfo ti = threads.get(i);
                    int rowY = top + i * ROW_H;

                    g2.setColor(i % 2 == 0 ? new Color(0x25253A) : C_PANEL);
                    g2.fillRect(chartX, rowY, chartW, ROW_H - 1);

                    // label
                    g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                    g2.setColor(ti.color);
                    g2.drawString("Thread-" + ti.id, 4, rowY + ROW_H / 2 - 2);

                    // status badge
                    Color sc = switch (ti.status) {
                        case RUNNING -> C_GREEN;
                        case WAITING -> C_YELLOW;
                        case LOCKED -> C_RED;
                        default -> C_MUTED;
                    };
                    g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2.setColor(sc);
                    g2.drawString(ti.status.name(), 4, rowY + ROW_H / 2 + 11);

                    // completed bars
                    for (CompletedBar bar : bars)
                        if (bar.thread == i)
                            paintDone(g2, bar, chartX, chartW, maxT, rowY);

                    // live bar
                    if (running && (ti.status == Status.RUNNING
                            || ti.status == Status.LOCKED))
                        paintLive(g2, ti, i, chartX, chartW, maxT, rowY);

                    // waiting bar
                    if (running && ti.status == Status.WAITING)
                        paintWait(g2, ti, i, chartX, chartW, maxT, rowY);
                }

                // time axis
                int axisY = top + threads.size() * ROW_H + 14;
                g2.setColor(C_BORDER);
                g2.drawLine(chartX, axisY - 6, chartX + chartW, axisY - 6);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2.setColor(C_MUTED);
                for (int gi = 0; gi <= gridN; gi++) {
                    int gx = chartX + gi * chartW / gridN;
                    long ms = gi * maxT / gridN;
                    g2.drawString(ms + "ms", gx - 14, axisY + 4);
                }

                // "now" cursor
                if (running) {
                    int nowX = chartX + (int) ((nowMs * (long) chartW) / maxT);
                    g2.setColor(C_ACCENT);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(nowX, top, nowX, top + threads.size() * ROW_H);
                    g2.fillOval(nowX - 4, top - 4, 8, 8);
                    g2.setStroke(new BasicStroke(1));
                }
            }

            void paintDone(Graphics2D g2, CompletedBar bar,
                    int cx, int cw, long maxT, int rowY) {
                int bx = cx + (int) ((bar.start * cw) / maxT);
                int bw = Math.max(3, (int) ((bar.end - bar.start) * cw / maxT));
                int by = rowY + BAR_PAD, bh = ROW_H - BAR_PAD * 2 - 1;

                g2.setColor(bar.locked ? alpha(C_RED, 160) : alpha(bar.color, 160));
                fillRR(g2, bx, by, bw, bh);
                g2.setColor(bar.locked ? C_RED : bar.color);
                g2.setStroke(new BasicStroke(0.8f));
                drawRR(g2, bx, by, bw, bh);
                g2.setStroke(new BasicStroke(1));

                if (bw > 32) {
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    g2.setColor(Color.WHITE);
                    g2.drawString("T" + bar.taskId + (bar.locked ? "[L]" : ""),
                            bx + 3, by + bh / 2 + 3);
                }
            }

            void paintLive(Graphics2D g2, ThreadInfo ti, int idx,
                    int cx, int cw, long maxT, int rowY) {
                long laneEnd = bars.stream().filter(b -> b.thread == idx)
                        .mapToLong(b -> b.end).max().orElse(0L);
                long nowMs = elapsed();
                int lx = cx + (int) ((laneEnd * cw) / maxT);
                int lw = Math.max(4, (int) ((nowMs - laneEnd) * cw / maxT));
                int ly = rowY + BAR_PAD, lh = ROW_H - BAR_PAD * 2 - 1;

                Color fill = (ti.status == Status.LOCKED) ? alpha(C_RED, 90) : alpha(ti.color, 90);
                Color edge = (ti.status == Status.LOCKED) ? C_RED : ti.color;

                g2.setColor(fill);
                fillRR(g2, lx, ly, lw, lh);
                g2.setColor(edge);
                g2.setStroke(new BasicStroke(0.8f));
                drawRR(g2, lx, ly, lw, lh);
                g2.setStroke(new BasicStroke(1));

                // progress stripe
                int sy = ly + lh - 3;
                g2.setColor(C_SURFACE);
                g2.fillRect(lx, sy, lw, 3);
                g2.setColor(edge);
                g2.fillRect(lx, sy, (int) (lw * ti.progress), 3);
            }

            void paintWait(Graphics2D g2, ThreadInfo ti, int idx,
                    int cx, int cw, long maxT, int rowY) {
                long laneEnd = bars.stream().filter(b -> b.thread == idx)
                        .mapToLong(b -> b.end).max().orElse(0L);
                long nowMs = elapsed();
                int lx = cx + (int) ((laneEnd * cw) / maxT);
                int lw = Math.max(4, (int) ((nowMs - laneEnd) * cw / maxT));
                int ly = rowY + BAR_PAD, lh = ROW_H - BAR_PAD * 2 - 1;

                g2.setColor(alpha(C_YELLOW, 55));
                fillRR(g2, lx, ly, lw, lh);
                float dash = (System.currentTimeMillis() / 60f) % 10f;
                g2.setColor(C_YELLOW);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND, 0, new float[] { 5, 5 }, dash));
                drawRR(g2, lx, ly, lw, lh);
                g2.setStroke(new BasicStroke(1));
                if (lw > 36) {
                    g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                    g2.setColor(C_YELLOW);
                    g2.drawString("WAIT", lx + 4, ly + lh / 2 + 3);
                }
            }

            void fillRR(Graphics2D g, int x, int y, int w, int h) {
                g.fillRoundRect(x, y, w, h, 5, 5);
            }

            void drawRR(Graphics2D g, int x, int y, int w, int h) {
                g.drawRoundRect(x, y, w, h, 5, 5);
            }

            Color alpha(Color c, int a) {
                return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
            }

            void rh(Graphics2D g) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
        }

        // =================================================================
        // STATS PANEL
        // =================================================================
        class StatsPanel extends JPanel {

            StatsPanel() {
                setBackground(C_PANEL);
                setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(1, 0, 0, 0, C_BORDER),
                        new EmptyBorder(7, 10, 7, 10)));
                setPreferredSize(new Dimension(0, 62));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                g2.setColor(C_MUTED);
                g2.drawString("STATS", 0, 11);

                long wall = running ? elapsed() : wallTime;
                int locks = (int) bars.stream().filter(b -> b.locked).count();

                String[] labels = { "Tasks Done", "Wall Time", "Threads", "Lock Waits" };
                String[] values = {
                        bars.size() + " / " + numTasks,
                        wall + " ms",
                        String.valueOf(numThreads),
                        locks + " tasks"
                };
                Color[] colors = { C_GREEN, C_TEXT, C_ACCENT, C_YELLOW };

                int colW = getWidth() / 4;
                for (int i = 0; i < 4; i++) {
                    int cx = i * colW;
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    g2.setColor(C_MUTED);
                    g2.drawString(labels[i], cx, 25);
                    g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                    g2.setColor(colors[i]);
                    g2.drawString(values[i], cx, 46);
                }
            }
        }

        // =================================================================
        // LOG PANEL
        // =================================================================
        class LogPanel extends JPanel {

            LogPanel() {
                setBackground(C_PANEL);
                setBorder(new MatteBorder(0, 1, 0, 0, C_BORDER));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setFont(new Font("Monospaced", Font.BOLD, 9));
                g2.setColor(C_MUTED);
                g2.drawString("EVENT LOG", 8, 14);

                int lineH = 14;
                int startY = 28;
                int maxLines = (getHeight() - startY) / lineH;

                List<String> visible = events.size() <= maxLines
                        ? events
                        : events.subList(events.size() - maxLines, events.size());

                g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
                for (int i = 0; i < visible.size(); i++) {
                    String line = visible.get(i);
                    g2.setColor(lineColor(line));
                    g2.drawString(fit(g2, line, getWidth() - 14),
                            8, startY + i * lineH);
                }
            }

            Color lineColor(String s) {
                if (s.startsWith("✅") || s.startsWith("✔"))
                    return C_GREEN;
                if (s.startsWith("🔒") || s.startsWith("🔓")
                        || s.startsWith("🟡"))
                    return C_YELLOW;
                if (s.startsWith("▶"))
                    return C_ACCENT;
                return C_TEXT;
            }

            String fit(Graphics2D g2, String s, int maxW) {
                FontMetrics fm = g2.getFontMetrics();
                if (fm.stringWidth(s) <= maxW)
                    return s;
                while (s.length() > 3 && fm.stringWidth(s + "…") > maxW)
                    s = s.substring(0, s.length() - 1);
                return s + "…";
            }
        }

    } // end VisualizationPanel

} // end VisualizationModule