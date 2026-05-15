import tkinter as tk
from tkinter import ttk
import threading
import queue
import random
import time


class VisualizationFrame(tk.Frame):

    MAX_THREADS = 6
    MAX_TASKS = 16

    ROW_HEIGHT = 50
    BAR_HEIGHT = 26
    TIME_SCALE = 0.12  # pixels per ms

    COLORS = [
        "#5DA5DA",
        "#60BD68",
        "#F17CB0",
        "#B2912F",
        "#B276B2",
        "#DECF3F",
        "#F15854",
        "#4D4D4D",
    ]

    def __init__(self, parent, app, go_home):
        super().__init__(parent)

        self.window_size = (1200, 650)
        self.go_home = go_home

        self.lock = threading.Lock()
        self.ui_queue = queue.Queue()

        self.worker_threads = []
        self.sim_running = False

        self.start_time = 0

        self._build_ui()

        self.after(50, self.process_ui_queue)

    # === UI SETUP ===

    def _build_ui(self):

        title = tk.Label(
            self,
            text="Thread Execution Visualization",
            font=("Arial", 16, "bold")
        )
        title.pack(pady=10)

        main_frame = tk.Frame(self)
        main_frame.pack(fill="both", expand=True, padx=10, pady=5)

        # Gant Chart
        chart_frame = tk.LabelFrame(main_frame, text="Gantt Chart")
        chart_frame.pack(side="left", fill="both", expand=True)

        self.canvas = tk.Canvas(
            chart_frame,
            bg="white",
            width=800,
            height=400
        )
        self.canvas.pack(fill="both", expand=True)

        # Log
        log_frame = tk.LabelFrame(main_frame, text="Event Log")
        log_frame.pack(side="right", fill="y", padx=(10, 0))

        self.log_text = tk.Text(
            log_frame,
            width=42,
            height=25,
            state="disabled"
        )
        self.log_text.pack(fill="both", expand=True)

        controls = tk.Frame(self)
        controls.pack(fill="x", padx=10, pady=10)

        self.run_button = tk.Button(
            controls,
            text="Run Simulation",
            command=self.run_simulation,
            width=16
        )
        self.run_button.pack(side="left", padx=5)

        # Threads slider
        thread_frame = tk.Frame(controls)
        thread_frame.pack(side="left", padx=15)

        tk.Label(thread_frame, text="Threads").pack()

        self.thread_var = tk.IntVar(value=3)

        self.thread_slider = tk.Scale(
            thread_frame,
            from_=1,
            to=self.MAX_THREADS,
            orient="horizontal",
            variable=self.thread_var
        )
        self.thread_slider.pack()

        # Tasks slider
        task_frame = tk.Frame(controls)
        task_frame.pack(side="left", padx=15)

        tk.Label(task_frame, text="Tasks").pack()

        self.task_var = tk.IntVar(value=6)

        self.task_slider = tk.Scale(
            task_frame,
            from_=1,
            to=self.MAX_TASKS,
            orient="horizontal",
            variable=self.task_var
        )
        self.task_slider.pack()

        back_button = tk.Button(
            self,
            text="Back to Menu",
            command=self.go_home,
            width=18
        )
        back_button.pack(pady=(0, 10))

    # === Simulation Control ===

    def run_simulation(self):

        if self.sim_running:
            return

        self.sim_running = True

        self.run_button.config(state="disabled")

        self.clear_visuals()

        thread_count = self.thread_var.get()
        task_count = self.task_var.get()

        self.start_time = time.time()

        self.draw_thread_labels(thread_count)

        # Round-robin task assignment
        assignments = {i: [] for i in range(thread_count)}

        for task_id in range(task_count):
            thread_id = task_id % thread_count
            assignments[thread_id].append(task_id)

        self.worker_threads.clear()

        for thread_id in range(thread_count):

            t = threading.Thread(
                target=self.thread_worker,
                args=(thread_id, assignments[thread_id]),
                daemon=True
            )

            self.worker_threads.append(t)
            t.start()

        watcher = threading.Thread(
            target=self.wait_for_completion,
            daemon=True
        )
        watcher.start()

    def wait_for_completion(self):

        for t in self.worker_threads:
            t.join()

        self.ui_queue.put(("simulation_complete",))

    def thread_worker(self, thread_id, tasks):

        for task_id in tasks:

            duration_ms = random.randint(400, 1400)

            start_ms = int((time.time() - self.start_time) * 1000)

            self.ui_queue.put((
                "log",
                f"Thread-{thread_id} started Task-{task_id}"
            ))

            self.ui_queue.put((
                "log",
                f"Thread-{thread_id} waiting for lock"
            ))

            with self.lock:

                self.ui_queue.put((
                    "log",
                    f"Thread-{thread_id} acquired lock"
                ))

                lock_sleep = random.uniform(0.08, 0.22)
                time.sleep(lock_sleep)

                self.ui_queue.put((
                    "log",
                    f"Thread-{thread_id} released lock"
                ))

            remaining = max(0.1, (duration_ms / 1000) - lock_sleep)

            time.sleep(remaining)

            self.ui_queue.put((
                "task_complete",
                thread_id,
                task_id,
                start_ms,
                duration_ms
            ))

            self.ui_queue.put((
                "log",
                f"Thread-{thread_id} completed "
                f"Task-{task_id} ({duration_ms} ms)"
            ))

        self.ui_queue.put((
            "log",
            f"Thread-{thread_id} finished execution"
        ))

    def process_ui_queue(self):

        try:
            while True:

                item = self.ui_queue.get_nowait()

                event = item[0]

                if event == "log":

                    self.add_log(item[1])

                elif event == "task_complete":

                    _, thread_id, task_id, start_ms, duration_ms = item

                    self.draw_task_bar(
                        thread_id,
                        task_id,
                        start_ms,
                        duration_ms
                    )

                elif event == "simulation_complete":

                    self.sim_running = False
                    self.run_button.config(state="normal")

                    self.add_log("Simulation completed.")

        except queue.Empty:
            pass

        self.after(50, self.process_ui_queue)

    # === Drawing ===

    def clear_visuals(self):

        self.canvas.delete("all")

        self.log_text.config(state="normal")
        self.log_text.delete("1.0", tk.END)
        self.log_text.config(state="disabled")

    def draw_thread_labels(self, thread_count):

        for i in range(thread_count):

            y = 40 + i * self.ROW_HEIGHT

            self.canvas.create_text(
                70,
                y,
                text=f"Thread-{i}",
                anchor="e",
                font=("Arial", 10, "bold")
            )

            self.canvas.create_line(
                80,
                y,
                2000,
                y,
                fill="#DDDDDD"
            )

    def draw_task_bar(
        self,
        thread_id,
        task_id,
        start_ms,
        duration_ms
    ):

        y = 40 + thread_id * self.ROW_HEIGHT

        x1 = 100 + (start_ms * self.TIME_SCALE)
        x2 = x1 + (duration_ms * self.TIME_SCALE)

        color = self.COLORS[task_id % len(self.COLORS)]

        self.canvas.create_rectangle(
            x1,
            y - self.BAR_HEIGHT // 2,
            x2,
            y + self.BAR_HEIGHT // 2,
            fill=color,
            outline="black"
        )

        self.canvas.create_text(
            (x1 + x2) / 2,
            y,
            text=f"T{task_id}",
            font=("Arial", 9, "bold")
        )

    # === Logging ===

    def add_log(self, message):

        timestamp = time.strftime("%H:%M:%S")

        self.log_text.config(state="normal")

        self.log_text.insert(
            tk.END,
            f"[{timestamp}] {message}\n"
        )

        self.log_text.see(tk.END)

        self.log_text.config(state="disabled")