package modules;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * VisualizationModule (Console Version)
 *
 * Simulates parallel thread execution in real-time using the console.
 * 
 * Key Features:
 * - Multiple threads executing tasks concurrently
 * - Shared lock to demonstrate synchronization and contention
 * - Live progress updates using text-based progress bars
 * - Event logging and execution statistics
 *
 * Purpose:
 * Helps visualize how threads run, wait, and complete tasks
 * without using a GUI.
 */
public class VisualizationModule {

    /**
     * Starts the console-based visualization in a separate thread.
     * 
     * @return short message shown in the GUI text area
     */
    public static String show() {
        new Thread(ConsoleSimulation::start).start();

        return "Console Visualization started...\n" +
                "Watch the terminal for live execution.\n";
    }

    /**
     * Core simulation engine that mimics parallel execution.
     */
    static class ConsoleSimulation {

        static int numThreads = 3;
        static int numTasks = 6;

        static boolean running = false;
        static long startTime = 0;
        static long wallTime = 0;

        static final List<ThreadInfo> threads = new ArrayList<>();
        static final List<CompletedBar> bars = new ArrayList<>();
        static final List<String> events = new ArrayList<>();

        static final AtomicBoolean lockHeld = new AtomicBoolean(false);
        static final AtomicInteger lockHolder = new AtomicInteger(-1);

        /**
         * Entry point of the simulation loop.
         */
        static void start() {
            startSimulation();

            while (running) {
                printState();
                sleep(500);
            }

            printState();
        }

        /**
         * Initializes threads, tasks, and starts execution.
         */
        static void startSimulation() {
            running = true;
            startTime = System.currentTimeMillis();
            wallTime = 0;

            threads.clear();
            bars.clear();
            events.clear();
            lockHeld.set(false);
            lockHolder.set(-1);

            for (int i = 0; i < numThreads; i++) {
                threads.add(new ThreadInfo(i));
            }

            List<TaskSpec> tasks = new ArrayList<>();
            for (int t = 0; t < numTasks; t++) {
                tasks.add(new TaskSpec(
                        t,
                        t % numThreads,
                        (t % 3 == 2),
                        900 + (int) (Math.random() * 1300)));
            }

            log("▶ Started — " + numThreads + " threads, " + numTasks + " tasks");

            ExecutorService pool = Executors.newFixedThreadPool(numThreads);

            for (TaskSpec task : tasks) {
                pool.submit(() -> runTask(task));
            }

            pool.shutdown();

            new Thread(() -> {
                try {
                    pool.awaitTermination(60, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }

                onDone();
            }).start();
        }

        /**
         * Executes a single task, including lock handling and progress updates.
         */
        static void runTask(TaskSpec task) {
            ThreadInfo ti = threads.get(task.owner);
            long taskStart = elapsed();

            ti.progress = 0f;

            if (task.needLock) {
                ti.status = Status.WAITING;
                log("🟡 Thread-" + task.owner + " waiting for lock [Task-" + task.id + "]");
            } else {
                ti.status = Status.RUNNING;
                log("🔵 Thread-" + task.owner + " started Task-" + task.id);
            }

            if (task.needLock) {
                while (!lockHeld.compareAndSet(false, true)) {
                    sleep(25);
                }
                lockHolder.set(task.owner);
                ti.status = Status.LOCKED;
                log("🔒 Thread-" + task.owner + " acquired lock [Task-" + task.id + "]");
            }

            int chunks = 12;
            for (int i = 0; i < chunks; i++) {
                sleep(task.duration / chunks);
                ti.progress = (i + 1f) / chunks;
            }

            if (task.needLock) {
                lockHeld.set(false);
                lockHolder.set(-1);
                log("🔓 Thread-" + task.owner + " released lock [Task-" + task.id + "]");
            }

            long end = elapsed();

            bars.add(new CompletedBar(task.owner, task.id, taskStart, end, task.needLock));

            ti.tasksCompleted++;
            ti.progress = 0f;
            ti.status = Status.IDLE;

            log("✔ Thread-" + task.owner + " done Task-" + task.id +
                    " (" + (end - taskStart) + " ms)");
        }

        /**
         * Called when all tasks are completed.
         */
        static void onDone() {
            wallTime = elapsed();
            running = false;
            log("✅ All tasks complete — wall time: " + wallTime + " ms");
        }

        /**
         * Prints current simulation state (threads, stats, logs).
         */
        static void printState() {
            clearConsole();

            System.out.println("=== PARALLEL EXECUTION (LIVE) ===");
            System.out.println("Time: " + elapsed() + " ms\n");

            for (ThreadInfo ti : threads) {
                String bar = progressBar(ti.progress);

                System.out.printf("Thread-%d [%s] %s Tasks:%d\n",
                        ti.id,
                        ti.status,
                        bar,
                        ti.tasksCompleted);
            }

            int locks = (int) bars.stream().filter(b -> b.locked).count();

            System.out.println("\n--- STATS ---");
            System.out.println("Tasks Done: " + bars.size() + " / " + numTasks);
            System.out.println("Threads: " + numThreads);
            System.out.println("Lock Waits: " + locks);
            System.out.println("Wall Time: " + (running ? elapsed() : wallTime) + " ms");

            System.out.println("\n--- EVENT LOG ---");
            int start = Math.max(0, events.size() - 8);
            for (int i = start; i < events.size(); i++) {
                System.out.println(events.get(i));
            }
        }

        /**
         * Creates a simple text progress bar.
         */
        static String progressBar(float progress) {
            int width = 20;
            int filled = (int) (progress * width);

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < width; i++) {
                sb.append(i < filled ? "=" : " ");
            }
            sb.append("] ").append((int) (progress * 100)).append("%");

            return sb.toString();
        }

        /** Clears the console output (ANSI-based). */
        static void clearConsole() {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }

        /** Returns elapsed time since simulation start. */
        static long elapsed() {
            return System.currentTimeMillis() - startTime;
        }

        /** Adds a message to the event log. */
        static void log(String msg) {
            events.add(msg);
        }

        /** Utility sleep method. */
        static void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /** Thread states during execution. */
    enum Status {
        IDLE, RUNNING, WAITING, LOCKED
    }

    /** Represents a worker thread. */
    static class ThreadInfo {
        final int id;
        volatile Status status = Status.IDLE;
        volatile float progress = 0f;
        volatile int tasksCompleted = 0;

        ThreadInfo(int id) {
            this.id = id;
        }
    }

    /** Defines a task assigned to a thread. */
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

    /** Stores completed task execution data. */
    static class CompletedBar {
        final int thread, taskId;
        final long start, end;
        final boolean locked;

        CompletedBar(int thread, int taskId, long start, long end, boolean locked) {
            this.thread = thread;
            this.taskId = taskId;
            this.start = start;
            this.end = end;
            this.locked = locked;
        }
    }
}
