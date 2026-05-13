import tkinter as tk
import threading
import time
import requests

class DemoFrame(tk.Frame):
    links = [
        "https://python.org",
        "https://docs.python.org",
        "https://peps.python.org",
        "https://pypi.org/",
        "https://www.learnpython.org/",
        "https://en.wikipedia.org/wiki/Python_(programming_language)"
    ]

    def __init__(self, parent, app, go_home):
        super().__init__(parent)
        self.app = app

        tk.Label(self, text="Demo Module", font=("Arial", 16, "bold")).pack(pady=(20, 4))
        tk.Label(
            self,
            text="Fetch links with and without threading",
            font=("Arial", 10), justify="center"
        ).pack()

        # Canvas for bar chart
        self.canvas = tk.Canvas(self, width=620, height=260, bg="white",
                                highlightthickness=1, highlightbackground="#ccc")
        self.canvas.pack(pady=(12, 6), padx=40)
        self._draw_placeholder()

        # Controls
        ctrl = tk.Frame(self)
        ctrl.pack()

        self.run_btn = tk.Button(ctrl, text="Run Demo", command=self._run)
        self.run_btn.pack(side="left", padx=5)

        self.back_btn = tk.Button(ctrl, text="Back to Menu", command=go_home)
        self.back_btn.pack(side="right", padx=5)

    # ===== DEMO FUNCTIONALITY =====
    def _fetch(self, link):
        requests.get(link)

    def _fetch_sequential(self):
        for link in self.links:
            self._fetch(link)

    def _fetch_parallel(self):
        # Start threads for each link
        threads = []
        for link in self.links:
            # Using `args` to pass positional arguments and `kwargs` for keyword arguments
            t = threading.Thread(target=self._fetch, args=(link,))
            threads.append(t)

        # Start each thread
        for t in threads:
            t.start()

        # Wait for all threads to finish
        for t in threads:
            t.join()

    def _draw_placeholder(self, message='Press "Run Demo" to see results'):
        self.canvas.delete("all")
        self.canvas.create_text(
            310, 130, text=message,
            fill="gray", font=("Arial", 12)
        )

    # ===== CUSTOM BAR CHART =====
    def _draw_chart(self, seq_ms, par_ms):
        c = self.canvas
        c.delete("all")

        W, H = 620, 260
        ml, mr, mt, mb = 80, 30, 40, 50
        chart_w = W - ml - mr
        chart_h = H - mt - mb
        max_ms = max(seq_ms, par_ms)

        # Grid lines + Y labels
        for i in range(5):
            val = max_ms * i / 4
            y = mt + chart_h - int(val / max_ms * chart_h)
            c.create_line(ml, y, ml + chart_w, y, fill="#e0e0e0")
            c.create_text(ml - 6, y, text=f"{val:.0f} ms",
                          anchor="e", font=("Arial", 9), fill="#555")

        # Axes
        c.create_line(ml, mt, ml, mt + chart_h, fill="#aaa")
        c.create_line(ml, mt + chart_h, ml + chart_w, mt + chart_h, fill="#aaa")

        bar_w = chart_w // 5
        gap = bar_w // 2

        self._bar(c, ml + gap, bar_w, seq_ms, max_ms, mt, chart_h, "#4285F4", "Sequential")
        self._bar(c, ml + gap * 2 + bar_w, bar_w, par_ms, max_ms, mt, chart_h, "#34A853", "Parallel")

        # Speedup label
        speedup = seq_ms / par_ms if par_ms > 0 else 0
        c.create_text(
            ml + chart_w, mt - 14,
            text=f"Speedup: {speedup:.2f}x faster",
            anchor="e", font=("Arial", 12, "bold"), fill="#222"
        )

    def _bar(self, c, x, bw, ms, max_ms, mt, chart_h, color, label):
        bh = int(ms / max_ms * chart_h)
        y = mt + chart_h - bh
        c.create_rectangle(x, y, x + bw, mt + chart_h, fill=color, outline="")
        c.create_text(x + bw // 2, y - 10, text=f"{ms:.0f} ms",
                      font=("Arial", 10, "bold"), fill="#333")
        c.create_text(x + bw // 2, mt + chart_h + 16, text=label,
                      font=("Arial", 10), fill="#333")

    def _run(self):
        self.run_btn.config(state="disabled", text="Running...")
        self._draw_placeholder()
        threading.Thread(target=self._benchmark, daemon=True).start()

    def _benchmark(self):
        self.after(0, self._draw_placeholder, "Running sequential...")
        t0 = time.perf_counter()
        self._fetch_sequential()
        seq_ms = (time.perf_counter() - t0) * 1000

        self.after(0, self._draw_placeholder, "Running parallel...")
        t0 = time.perf_counter()
        self._fetch_parallel()
        par_ms = (time.perf_counter() - t0) * 1000

        self.after(0, self._show_results, seq_ms, par_ms)

    def _show_results(self, seq_ms, par_ms):
        self._draw_chart(seq_ms, par_ms)
        self.run_btn.config(state="normal", text="Run Again")