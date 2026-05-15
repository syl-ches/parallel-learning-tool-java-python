import tkinter as tk
from tkinter import font


TOPICS = [
    "Overview", "Variables", "Scope", "Lifetime",
    "Type/Value Binding", "Control Structures",
    "Threads vs Processes", "Java vs Python"
]

CONTENT = {
    "Overview": """OVERVIEW
──────────────────────────────────────────────────────────

Parallel programming is a computing paradigm where multiple tasks execute simultaneously across multiple processors or threads. It offers significant performance benefits but introduces behaviors that differ fundamentally from sequential programs.

WHY THEORY MATTERS
Understanding parallel programs requires grounding in programming language theory: how variables, scope, lifetime, type and value binding, and control structures operate in a concurrent environment.
""",

    "Variables": """VARIABLES
──────────────────────────────────────────────────────────

A variable is a named storage location that holds a value during program execution. In sequential programming, only one thread accesses a variable at a time. In parallel programming, multiple threads may read and write shared variables concurrently, introducing data races and synchronization challenges.

CLASSIFICATION BY SHARING

  Shared Variable
    Visible and accessible to all threads. Requires 
    synchronization to avoid race conditions.

  Private Variable
    Belongs exclusively to one thread. No synchronization
     needed. Safe by design.

  Reduction Variable
    Each thread keeps a local copy combined into one 
    aggregate result at the end of parallel execution.

LANGUAGE NOTES
  Java    — volatile ensures a shared variable is always
            read from main memory. synchronized blocks 
            protect mutable shared state.

  Python  — Variables in threaded code share process 
            memory. The GIL provides limited safety for 
            threads. multiprocessing assigns separate 
            memory spaces.
""",

    "Scope": """SCOPE
──────────────────────────────────────────────────────────

Scope refers to the region of a program where a variable is visible and accessible. Scope determines which variables are shared across threads and which are private.

PRIMARY SCOPING RULES

  Static (Lexical) Scope
    Determined at compile time based on source code. 
    Both Java and Python use static scoping.

  Dynamic Scope
    Determined at runtime.

LANGUAGE NOTES

  Java    — Shared instance fields require synchronized
            access or atomic types.

  Python  — Use threading.local() for thread-local
            storage. Module-level variables are shared.
""",

    "Lifetime": """LIFETIME
──────────────────────────────────────────────────────────

The lifetime of a variable is the duration during which it occupies memory, from creation to destruction.

CORE RULE
  A thread must not outlive the variable it references.

  Static Lifetime
    Exists for the entire program. Shared across all
    threads.

  Stack (Local) Lifetime
    Created on function call and destroyed on return.

  Heap Lifetime
    Persists while references remain. Shared mutable
    objects need synchronization.

Both Java and Python manage heap lifetimes through garbage collection.
""",

    "Type/Value Binding": """TYPE AND VALUE BINDING
──────────────────────────────────────────────────────────

TYPE BINDING

  Static (Java)   — Binds type at compile time.
  Dynamic (Python) — Resolves type at runtime.

VALUE BINDING

  Race conditions are conflicts over value binding:
  two threads write the same variable simultaneously.

  Immutable Binding
    Value cannot change after assignment. Thread-safe.

  Mutable Binding
    Value can change after assignment. Requires 
    synchronization.

Prefer immutable bindings whenever possible.
""",

    "Control Structures": """CONTROL STRUCTURES
──────────────────────────────────────────────────────────

In sequential programs, execution follows a single deterministic path. In parallel programs, multiple threads execute concurrently.

PARALLEL CONSTRUCTS

  Fork / Spawn
    Creates a new thread or process to execute a task 
    concurrently with the caller.

  Join
    Blocks the calling thread until a specified thread 
    completes.

  Mutual Exclusion  
    Ensures only one thread executes a critical section 
    at a time.

  Atomic Operation 
    An indivisible read-modify-write operation that
    cannot be interrupted.

  Parallel Loop
    Distributes loop iterations across multiple threads
    or processes.

PERFORMANCE AND LIMITS

  Three tasks of 1 second each take 3 seconds sequentially but about 1 second in parallel. Speedup is bounded by Amdahl's Law.
""",

    "Threads vs Processes": """THREADS VS PROCESSES
──────────────────────────────────────────────────────────

  Aspect           Thread                  Process
  ─────────────────────────────────────────────────────────
  Memory           Shares heap and         Own isolated
                   static data             address space

  Communication    Direct shared           IPC via queues
                   variables               or pipes

  Creation Cost    Lightweight and fast    Heavyweight

  Crash Isolation  Can affect process      Usually 
                                           isolated

  Best For         I/O-bound tasks         CPU-bound tasks
""",

    "Java vs Python": """JAVA VS PYTHON
──────────────────────────────────────────────────────────

Aspect            Java                   Python
  ─────────────────────────────────────────────────────────
Type Binding      Static, compile-time   Dynamic,
                  checked                runtime-
                                         resolved

Parallel Model    Thread,                threading /
                  ExecutorService,       multiprocessing
                  ForkJoinPool

True CPU          Yes                    Limited for
Parallelism                              threads (GIL)

Synchronization   synchronized,          threading.Lock,
                  volatile               queue.Queue

Memory Model      Java Memory Model      GIL / separate
                                         processes

KEY TAKEAWAY

  Java provides true multi-threaded CPU parallelism.
  Python achieves CPU parallelism through 
  multiprocessing.
"""
}


class TheoryFrame(tk.Frame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent)
        self.app = app

        header = tk.Label(self, text="Theory Module",
                          font=("Arial", 16, "bold"))
        header.pack(pady=(16, 8))

        main = tk.Frame(self)
        main.pack(fill="both", expand=True, padx=12, pady=(0, 8))

        # Sidebar
        sidebar = tk.Frame(main, width=160, relief="groove", bd=1)
        sidebar.pack(side="left", fill="y", padx=(0, 8))
        sidebar.pack_propagate(False)

        tk.Label(sidebar, text="Topics", font=("Arial", 11, "bold"),
                 anchor="w").pack(fill="x", padx=8, pady=(8, 4))

        self._topic_buttons = []
        for topic in TOPICS:
            btn = tk.Button(sidebar, text=topic, anchor="w",
                            relief="flat", cursor="hand2",
                            font=("Arial", 10),
                            command=lambda t=topic: self._show(t))
            btn.pack(fill="x", padx=4, pady=1)
            self._topic_buttons.append((topic, btn))

        # Content area
        self._text = tk.Text(main, wrap="word", state="disabled",
                             font=("Courier", 10),
                             relief="groove", bd=1,
                             padx=10, pady=10)
        scrollbar = tk.Scrollbar(main, command=self._text.yview)
        self._text.config(yscrollcommand=scrollbar.set)
        scrollbar.pack(side="right", fill="y")
        self._text.pack(side="left", fill="both", expand=True)

        tk.Button(self, text="Back to Menu",
                  command=go_home).pack(pady=(0, 12))

        # Show first topic by default
        self._show(TOPICS[0])

    def _show(self, topic):
        # Highlight selected button
        for t, btn in self._topic_buttons:
            btn.config(relief="sunken" if t == topic else "flat",
                       font=("Arial", 10, "bold") if t == topic
                       else ("Arial", 10))

        # Update content
        self._text.config(state="normal")
        self._text.delete("1.0", "end")
        self._text.insert("end", CONTENT[topic])
        self._text.config(state="disabled")
        self._text.yview_moveto(0)
