import tkinter as tk
from tkinter import font

TOPICS = [
    "Overview", "BNF Basics", "EBNF & Extensions",
    "Production Rules", "Java Thread Syntax",
    "Java Parallel Loop", "Java Synchronization",
    "Python Thread Syntax", "Python Process Syntax",
    "Python with Locks", "Syntax Diagrams",
    "Reading Diagrams", "Parallel Behavior",
    "Common Errors", "Java vs Python"
]

CONTENT = {
    "Overview": """OVERVIEW
───────────────────────────────────────────────────────
The Syntax Module teaches formal structures using 
Backus-Naur Form (BNF) and syntax diagrams.

WHY IT MATTERS:
• Understand why the compiler rejects certain code.
• Read and write language specifications.
• Reason about thread and sync constructs precisely.
• Compare how languages express parallel concepts.

Parallel constructs like threads and locks follow strict
grammar rules. Knowing the grammar means knowing exactly
what is allowed by the language at runtime.
""",

    "BNF Basics": """BNF BASICS
───────────────────────────────────────────────────────
Backus-Naur Form (BNF) is the universal standard for
describing programming language grammars.

THE THREE BUILDING BLOCKS:

1. TERMINAL SYMBOLS
   The actual text in code (e.g., "Thread", "new", ";").
   Usually shown in quotes or bold.

2. NONTERMINAL SYMBOLS
   Abstract placeholders like <identifier>. Enclosed in 
   angle brackets. They define categories of syntax.

3. PRODUCTION RULES
   Defined by ::= (is defined as) and | (OR).

Example:
<assignment> ::= <id> "=" <expression>
""",

    "EBNF & Extensions": """EBNF & EXTENSIONS
───────────────────────────────────────────────────────
Extended BNF (EBNF) adds notation for conciseness:

{ ... } : Zero or more repetitions.
[ ... ] : Optional (zero or one).
( ... ) : Grouping.
" ... " : Terminal literal.

JAVA EXAMPLE:
<var_decl> ::= <type> <id> [ "=" <expr> ] ";"

Note: In EBNF, curly braces { } mean repetition, not 
a Java-style code block.
""",

    "Production Rules": """PRODUCTION RULES DEEP DIVE
───────────────────────────────────────────────────────
Production rules are the heart of formal grammar.

RECURSIVE RULES:
A rule referring to itself allows for infinite chains.
<list> ::= <item> | <item> "," <list>

DERIVATION:
Expanding nonterminals until only terminals remain. 
This mirrors how a compiler parses your code.

PARSE TREES:
Visual hierarchy of a derivation. If a compiler cannot
build a valid tree, it throws a "syntax error."
""",

    "Java Thread Syntax": """JAVA THREAD SYNTAX
───────────────────────────────────────────────────────
Java supports threads via the Thread class.

BNF: THREAD CREATION
<thread_init> ::= "new" "Thread" "(" <runnable> ")"

BNF: LIFECYCLE
<thread_start> ::= <id> ".start()" ";"
<thread_join>  ::= <id> ".join()" ";"

START VS RUN:
Only .start() triggers parallel behavior. Calling .run()
is just a sequential method call on the current thread.
""",

    "Java Parallel Loop": """JAVA PARALLEL LOOP
───────────────────────────────────────────────────────
Java 8 introduced parallel streams for collections.

BNF: PARALLEL PIPELINE
<p_stream> ::= <coll> ".parallelStream()" { <op> }
<op>       ::= ".filter(" <pred> ")" | ".map(" <func> ")"

WATCH OUT:
Do NOT use parallel streams with shared mutable state.
Operations like list.add() inside forEach() will cause
race conditions and corrupted data.
""",

    "Java Synchronization": """JAVA SYNCHRONIZATION
───────────────────────────────────────────────────────
Synchronization prevents race conditions.

BNF: SYNCHRONIZED BLOCK
"synchronized" "(" <lock_obj> ")" "{" { <stmt> } "}"

BNF: REENTRANT LOCK
<id> ".lock()" ";"
"try" "{" <stmts> "}" "finally" "{" <id> ".unlock()" "}"

MONITOR MODEL:
Every Java object has an internal lock (monitor). Only
one thread can hold it at a time.
""",

    "Python Thread Syntax": """PYTHON THREAD SYNTAX
───────────────────────────────────────────────────────
Python uses the 'threading' module.

BNF: THREAD CREATION
<id> "=" "Thread" "(" "target=" <callable> ["," "args="] ")"

Note: Python threads are limited by the Global 
Interpreter Lock (GIL), which prevents true CPU 
parallelism for CPU-bound code. They are best for 
I/O-bound tasks like web requests or file reading.
""",

    "Python Process Syntax": """PYTHON PROCESS SYNTAX
───────────────────────────────────────────────────────
The 'multiprocessing' module bypasses the GIL.

BNF: PROCESS CREATION
<id> "=" "Process" "(" "target=" <callable> ")"

Processes have separate memory spaces. Data must be
shared explicitly via Queues or Pipes. True CPU 
parallelism is achieved by spawning separate
interpreter instances for each process.
""",

    "Python with Locks": """PYTHON WITH LOCKS
───────────────────────────────────────────────────────
Python provides locking via threading.Lock().

BNF: LOCK USAGE
<id> "=" "Lock()"
"with" <id> ":" <indented_block>

The "with" statement (context manager) is preferred 
because it guarantees the lock is released even if 
an exception occurs, preventing deadlocks.
""",

    "Syntax Diagrams": """SYNTAX DIAGRAMS
───────────────────────────────────────────────────────
Also called Railroad Diagrams. Follow the paths!

SYMBOLS:
• Rounded Rect: Terminal (literal).
• Rectangle: Nonterminal (another rule).
• Arrow: Required sequence.
• Loop-back: Repetition.

Example (Java Sync):
[synchronized]-->(--><lock_obj>-->)-->{--><stmts>-->}
""",

    "Reading Diagrams": """READING A SYNTAX DIAGRAM
───────────────────────────────────────────────────────
Let's trace a Parallel Stream pipeline.

1. [collection] -> Start.
2. [.parallelStream()] -> Enter parallel mode.
3. [Operation Loop] -> 
   Choose .filter(), .map(), or .collect().
4. [.collect()] -> Terminal operation. Exit loop.

A diagram makes it obvious that you cannot chain more 
operations after a terminal one like .collect().
""",

    "Parallel Behavior": """PARALLEL BEHAVIOR
───────────────────────────────────────────────────────
Syntax directly determines runtime execution.

SYNTAX -> SEMANTICS -> BEHAVIOR

Example:
t.start()
Syntax: Valid <thread_start>.
Semantics: JVM spawns OS thread.
Behavior: Concurrent execution.

Placement of "synchronized" braces determines the scope
of protection. Incorrect syntax leads to race conditions.
""",

    "Common Errors": """COMMON SYNTAX ERRORS
───────────────────────────────────────────────────────
JAVA ERRORS:
• t.run() instead of t.start() (No parallelism).
• Missing t.join() (Main thread exits early).
• No finally block for unlock() (Deadlock risk).

PYTHON ERRORS:
• target=task() (Calls function instead of passing it).
• args=(42) (Missing comma; needs to be a tuple (42,)).
• Missing __main__ guard on Windows.
""",

    "Java vs Python": """JAVA VS PYTHON
───────────────────────────────────────────────────────
JAVA:
• One process, many threads.
• Shared memory.
• True CPU parallelism.

PYTHON:
• Threads: Shared memory, no CPU parallelism (GIL).
• Processes: Separate memory, true CPU parallelism.

Java is often faster for heavy CPU shared-state tasks,
while Python is easier for I/O and process-based work.
"""
}

class SyntaxFrame(tk.Frame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent)
        self.app = app

        # Header
        header = tk.Label(self, text="Syntax Module",
                          font=("Arial", 16, "bold"), fg="#1E3A6E")
        header.pack(pady=(16, 8))

        main = tk.Frame(self)
        main.pack(fill="both", expand=True, padx=12, pady=(0, 8))

        # Sidebar
        sidebar_container = tk.Frame(main, width=180, relief="groove", bd=1)
        sidebar_container.pack(side="left", fill="y", padx=(0, 8))
        sidebar_container.pack_propagate(False) # Keep fixed width

        canvas = tk.Canvas(sidebar_container, highlightthickness=0, bg="#F0F4F8")
        side_vbar = tk.Scrollbar(sidebar_container, orient="vertical", command=canvas.yview)
        
        self.scrollable_sidebar = tk.Frame(canvas, bg="#F0F4F8")

        self.scrollable_sidebar.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )

        canvas.create_window((0, 0), window=self.scrollable_sidebar, anchor="nw", width=175)
        canvas.configure(yscrollcommand=side_vbar.set)

        side_vbar.pack(side="right", fill="y")
        canvas.pack(side="left", fill="both", expand=True)
        
        canvas.bind_all("<MouseWheel>", lambda e: canvas.yview_scroll(int(-1*(e.delta/120)), "units"))



        tk.Label(self.scrollable_sidebar, text="Topics", font=("Arial", 11, "bold"),
                 bg="#F0F4F8", anchor="w").pack(fill="x", padx=8, pady=(8, 4))

        self._topic_buttons = []
        for topic in TOPICS:
            btn = tk.Button(self.scrollable_sidebar, text=topic, anchor="w",
                            relief="flat", cursor="hand2", font=("Arial", 10), 
                            bg="#F0F4F8", command=lambda t=topic: self._show(t))
            btn.pack(fill="x", padx=4, pady=1)
            self._topic_buttons.append((topic, btn))

        # Content Area
        self._text = tk.Text(main, wrap="word", state="disabled",
                             font=("Courier", 10),
                             relief="groove", bd=1,
                             padx=15, pady=15, bg="#FFFFFF")
        
        scrollbar = tk.Scrollbar(main, command=self._text.yview)
        self._text.config(yscrollcommand=scrollbar.set)
        
        scrollbar.pack(side="right", fill="y")
        self._text.pack(side="left", fill="both", expand=True)

        # Footer
        tk.Button(self, text="Back to Menu",
                  command=go_home).pack(pady=(0, 12))

        # Initial View
        self._show(TOPICS[0])

    def _show(self, topic):
        # Update Button Styles
        for t, btn in self._topic_buttons:
            if t == topic:
                btn.config(font=("Arial", 10, "bold"))
            else:
                btn.config(font=("Arial", 10))

        # Update Text Content
        self._text.config(state="normal")
        self._text.delete("1.0", "end")
        self._text.insert("end", CONTENT.get(topic, "Content not found."))
        self._text.config(state="disabled")
        self._text.yview_moveto(0)