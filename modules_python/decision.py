import tkinter as tk


QUESTIONS = [
    (
        "Problem Decomposition",
        "Can the problem be divided into smaller sub-tasks?",
        2
    ),
    (
        "Problem Decomposition",
        "Are there repeated operations on large data?",
        2
    ),
    (
        "Task Independence",
        "Can sub-tasks execute independently?",
        3
    ),
    (
        "Task Independence",
        "Do tasks avoid frequent communication?",
        2
    ),
    (
        "Synchronization",
        "Do tasks require minimal synchronization?",
        2
    ),
    (
        "Granularity",
        "Are tasks large enough to justify threading overhead?",
        2
    ),
    (
        "Resources",
        "Does the system have multiple CPU cores?",
        1
    ),
    (
        "Performance",
        "Will parallel execution significantly reduce runtime?",
        3
    )
]


class DecisionFrame(tk.Frame):

    def __init__(self, parent, app, go_home):
        super().__init__(parent)

        self.app = app
        self.answers = []

        # ===== HEADER =====
        tk.Label(
            self,
            text="Decision Module",
            font=("Arial", 16, "bold")
        ).pack(pady=(18, 4))

        tk.Label(
            self,
            text="Evaluate whether parallel programming is appropriate.",
            font=("Arial", 10)
        ).pack(pady=(0, 12))

        # ===== MAIN CONTENT =====
        main = tk.Frame(self)
        main.pack(fill="both", expand=True, padx=12)

        # LEFT SIDE (QUESTIONS)
        left = tk.Frame(main)
        left.pack(side="left", fill="both", expand=True)

        # RIGHT SIDE (RESULTS)
        right = tk.Frame(main)
        right.pack(side="right", fill="both", expand=True, padx=(10, 0))

        # ===== QUESTIONS =====
        self.vars = []

        current_section = None

        for section, question, weight in QUESTIONS:

            if section != current_section:
                tk.Label(
                    left,
                    text=section,
                    font=("Arial", 11, "bold"),
                    anchor="w"
                ).pack(fill="x", pady=(10, 2))

                current_section = section

            var = tk.BooleanVar()

            cb = tk.Checkbutton(
                left,
                text=question,
                variable=var,
                anchor="w",
                justify="left",
                wraplength=420
            )

            cb.pack(fill="x", anchor="w")

            self.vars.append((var, weight))

        # ===== RESULT AREA =====
        tk.Label(
            right,
            text="Analysis",
            font=("Arial", 11, "bold")
        ).pack(anchor="w")

        self.result = tk.Text(
            right,
            width=40,
            height=22,
            wrap="word",
            state="disabled",
            font=("Courier", 10)
        )

        self.result.pack(fill="both", expand=True)

        # ===== BUTTONS =====
        bottom = tk.Frame(self)
        bottom.pack(pady=12)

        tk.Button(
            bottom,
            text="Analyze",
            command=self._analyze
        ).pack(side="left", padx=5)

        tk.Button(
            bottom,
            text="Clear",
            command=self._clear
        ).pack(side="left", padx=5)

        tk.Button(
            bottom,
            text="Back to Menu",
            command=go_home
        ).pack(side="left", padx=5)

    # ===== DECISION LOGIC =====

    def _analyze(self):

        score = 0

        for var, weight in self.vars:
            if var.get():
                score += weight

        max_score = sum(weight for _, weight in self.vars)

        ratio = score / max_score

        if ratio >= 0.75:
            level = "STRONGLY RECOMMENDED"

            explanation = (
                "The workload appears highly suitable for parallel "
                "execution.\n\n"

                "The problem is divisible into independent tasks "
                "with relatively low synchronization and "
                "communication overhead.\n\n"

                "This suggests that parallel programming may "
                "provide significant performance improvement."
            )

        elif ratio >= 0.45:
            level = "POSSIBLY BENEFICIAL"

            explanation = (
                "Parallel programming may improve performance, "
                "but overhead costs should be considered carefully.\n\n"

                "Synchronization, communication, or task size "
                "may reduce achievable speedup."
            )

        else:
            level = "NOT RECOMMENDED"

            explanation = (
                "Sequential execution is likely more efficient.\n\n"

                "The workload may contain excessive dependency, "
                "communication overhead, or insufficient task size "
                "to justify parallel execution."
            )

        theory = (
            "\n\nTHEORY CONNECTIONS\n"
            "────────────────────────────\n"
            "• Task decomposition\n"
            "• Synchronization overhead\n"
            "• Task granularity\n"
            "• Data dependency\n"
            "• Parallel overhead\n"
            "• Resource utilization\n"
        )

        text = (
            f"PARALLELISM ANALYSIS\n"
            f"────────────────────────────\n"
            f"Score: {score}/{max_score}\n\n"
            f"Recommendation:\n{level}\n\n"
            f"{explanation}"
            f"{theory}"
        )

        self.result.config(state="normal")
        self.result.delete("1.0", "end")
        self.result.insert("end", text)
        self.result.config(state="disabled")

    def _clear(self):

        for var, _ in self.vars:
            var.set(False)

        self.result.config(state="normal")
        self.result.delete("1.0", "end")
        self.result.config(state="disabled")
