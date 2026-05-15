import tkinter as tk


QUESTIONS = [
    ("Problem Decomposition", "Can the problem be divided into sub-tasks?",             2),
    ("Problem Decomposition", "Are there repeated operations on large data?",           2),
    ("Task Independence",     "Can tasks execute independently?",                       3),
    ("Task Independence",     "Do tasks avoid frequent communication?",                 2),
    ("Synchronization",       "Do tasks require minimal synchronization?",              2),
    ("Granularity",           "Are tasks large enough to justify overhead?",            2),
    ("Resources",             "Does the system have multiple CPU cores?",               1),
    ("Performance",           "Will parallel execution significantly reduce runtime?",  3),
]


class DecisionFrame(tk.Frame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent)
        self.go_home = go_home

        # Header
        tk.Label(self, text="Decision Module",
                 font=("Arial", 16, "bold")).pack(pady=(16, 2))
        tk.Label(self, text="Evaluate whether parallel programming is appropriate.",
                 font=("Arial", 10)).pack(pady=(0, 8))

        # Main area
        main = tk.Frame(self)
        main.pack(fill="both", expand=True, padx=16, pady=(0, 8))

        # Questions
        left_outer = tk.Frame(main, relief="groove", bd=1)
        left_outer.pack(side="left", fill="both", expand=True, padx=(0, 6))

        left_canvas = tk.Canvas(left_outer, highlightthickness=0)
        left_scroll = tk.Scrollbar(left_outer, orient="vertical",
                                   command=left_canvas.yview)
        left_canvas.configure(yscrollcommand=left_scroll.set)
        left_scroll.pack(side="right", fill="y")
        left_canvas.pack(side="left", fill="both", expand=True)

        left = tk.Frame(left_canvas)
        left_canvas.create_window((0, 0), window=left, anchor="nw")
        left.bind("<Configure>",
                  lambda e: left_canvas.configure(
                      scrollregion=left_canvas.bbox("all")))

        self._vars = []
        current_category = ""
        for category, text, weight in QUESTIONS:
            if category != current_category:
                tk.Label(left, text=category,
                         font=("Arial", 11, "bold"),
                         anchor="w").pack(fill="x", padx=8, pady=(10, 2))
                current_category = category
            var = tk.BooleanVar()
            tk.Checkbutton(left, text=text, variable=var,
                           anchor="w", font=("Arial", 10)).pack(
                               fill="x", padx=16, pady=1)
            self._vars.append((var, weight))

        # Output panel
        right = tk.Frame(main, relief="groove", bd=1)
        right.pack(side="left", fill="both", expand=True, padx=(6, 0))

        self._output = tk.Text(right, font=("Courier", 10),
                               state="disabled", wrap="word",
                               padx=8, pady=8)
        r_scroll = tk.Scrollbar(right, command=self._output.yview)
        self._output.configure(yscrollcommand=r_scroll.set)
        r_scroll.pack(side="right", fill="y")
        self._output.pack(fill="both", expand=True)

        # Buttons
        btn_frame = tk.Frame(self)
        btn_frame.pack(pady=(0, 12))

        tk.Button(btn_frame, text="Analyze",
                  command=self._analyze).pack(side="left", padx=4)
        tk.Button(btn_frame, text="Clear",
                  command=self._clear).pack(side="left", padx=4)
        tk.Button(btn_frame, text="Back to Menu",
                  command=go_home).pack(side="left", padx=4)

    # Logic

    def _analyze(self):
        score = 0
        max_score = sum(w for _, w in self._vars)
        for var, weight in self._vars:
            if var.get():
                score += weight

        ratio = score / max_score

        if ratio >= 0.75:
            level = "STRONGLY RECOMMENDED"
            explanation = (
                "The workload appears highly suitable for parallel execution.\n\n"
                "The problem is divisible into independent tasks with relatively "
                "low synchronization and communication overhead.\n\n"
                "Parallel programming may provide significant performance improvement."
            )
        elif ratio >= 0.45:
            level = "POSSIBLY BENEFICIAL"
            explanation = (
                "Parallel programming may improve performance, but overhead costs "
                "should be considered.\n\n"
                "Synchronization, communication, or task size may reduce achievable speedup."
            )
        else:
            level = "NOT RECOMMENDED"
            explanation = (
                "Sequential execution is likely more efficient.\n\n"
                "The workload may contain excessive dependency, communication overhead, "
                "or insufficient task size to justify parallel execution."
            )

        text = (
            f"PARALLELISM ANALYSIS\n"
            f"{'─' * 28}\n"
            f"Score: {score}/{max_score}\n\n"
            f"Recommendation:\n"
            f"{level}\n\n"
            f"{explanation}\n\n"
            f"THEORY CONNECTIONS\n"
            f"{'─' * 28}\n"
            f"• Task decomposition\n"
            f"• Synchronization overhead\n"
            f"• Task granularity\n"
            f"• Data dependency\n"
            f"• Parallel overhead\n"
            f"• Resource utilization\n"
        )

        self._output.config(state="normal")
        self._output.delete("1.0", "end")
        self._output.insert("end", text)
        self._output.config(state="disabled")

    def _clear(self):
        for var, _ in self._vars:
            var.set(False)
        self._output.config(state="normal")
        self._output.delete("1.0", "end")
        self._output.config(state="disabled")
