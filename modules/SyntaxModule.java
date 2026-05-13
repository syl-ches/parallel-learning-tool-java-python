package modules;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class SyntaxModule {

    private static final String[] TOPICS = {
        "Overview",
        "BNF Basics",
        "EBNF & Extensions",
        "Production Rules Deep Dive",
        "Java Thread Syntax",
        "Java Parallel Loop",
        "Java Synchronization",
        "Python Thread Syntax",
        "Python Process Syntax",
        "Python with Locks",
        "Syntax Diagrams",
        "Reading a Syntax Diagram",
        "How Syntax Relates to Parallel Behavior",
        "Common Syntax Errors",
        "Java vs Python",
        "Resources & Further Reading"
    };

    // =========================
    // UI HELPERS
    // =========================

    private static String card(String bg, String border, String title, String body) {
        return "<div style='background:" + bg +
                ";border-left:5px solid " + border +
                ";padding:10px 14px;margin:8px 0;border-radius:16px;'>" +
                "<b style='color:" + border + "'>" + title + "</b><br><br>" +
                "<span style='color:#333;font-size:12px'>" + body + "</span></div>";
    }

    private static String code(String body) {
        return "<div style='background:#F4F8FE;" +
                "padding:12px;" +
                "border-radius:14px;" +
                "font-family:Consolas,monospace;" +
                "font-size:12px;" +
                "border-left:5px solid #3478DC;" +
                "margin:10px 0;" +
                "white-space:pre-wrap;'>" +
                body +
                "</div>";
    }

    private static String tip(String body) {
        return "<div style='background:#FFFBE6;" +
                "border-left:5px solid #F59E0B;" +
                "padding:10px 14px;margin:8px 0;border-radius:16px;'>" +
                "<b style='color:#B45309'>&#128161; Tip: </b>" +
                "<span style='color:#333;font-size:12px'>" + body + "</span></div>";
    }

    private static String warn(String body) {
        return "<div style='background:#FEF2F2;" +
                "border-left:5px solid #EF4444;" +
                "padding:10px 14px;margin:8px 0;border-radius:16px;'>" +
                "<b style='color:#B91C1C'>&#9888; Watch Out: </b>" +
                "<span style='color:#333;font-size:12px'>" + body + "</span></div>";
    }

    private static String link(String url, String label) {
        return "<a href='" + url + "' style='color:#2563EB;font-weight:bold'>" + label + "</a>";
    }

    private static String wrap(String body) {
        return "<html><body style='font-family:Arial;font-size:12px;color:#222;padding:10px;margin:0'>" +
                body + "</body></html>";
    }

    // =========================
    // CONTENT
    // =========================

    private static final String[] CONTENT = {

// ================= OVERVIEW =================
wrap(
"<h1 style='color:#1E3A6E'>Overview</h1>" +

"<p>The <b>Syntax Module</b> teaches the formal structure of parallel programming constructs using " +
"<b>Backus-Naur Form (BNF)</b> notation and <b>syntax diagrams</b> (also called railroad diagrams).</p>" +

"<p>Programming languages follow strict, unambiguous rules called <b>grammar</b>. Understanding grammar " +
"rules helps you:</p>" +
"<ul style='font-size:12px;color:#333'>" +
"<li>Understand <b>why</b> the compiler rejects certain code</li>" +
"<li>Read and write language specifications</li>" +
"<li>Reason precisely about thread, process, and synchronization constructs</li>" +
"<li>Compare how different languages express the same parallel concepts</li>" +
"</ul>" +

card("#E8F0FE", "#3478DC", "What This Module Covers",
"BNF notation rules &bull; EBNF extensions &bull; Production rules &bull; " +
"Syntax diagrams &bull; Java and Python parallel constructs in formal notation &bull; " +
"Common mistakes and their grammar roots") +

card("#E6F5EE", "#5bb588", "Why It Matters for Parallel Programming",
"Parallel constructs like threads, locks, and process pools are <b>language features</b> with formal syntax. " +
"Knowing the grammar means you know exactly what is and is not allowed by the language — " +
"which directly affects how your program behaves at runtime.") +

tip("Even if you never write a language specification yourself, reading BNF makes API documentation and " +
"language manuals far easier to understand.")
),

// ================= BNF BASICS =================
wrap(
"<h1 style='color:#1E3A6E'>BNF Basics</h1>" +

"<p><b>Backus-Naur Form (BNF)</b> was invented in the late 1950s by John Backus and Peter Naur to " +
"formally define the syntax of the ALGOL programming language. It is now the universal standard for " +
"describing programming language grammars.</p>" +

"<h3 style='color:#2563EB'>The Three Building Blocks</h3>" +

card("#E6F5EE", "#5bb588", "1. Terminal Symbols",
"Terminals are the <b>actual text</b> that appears in source code. They cannot be broken down further.<br><br>" +
"Examples: <code>Thread</code>, <code>new</code>, <code>=</code>, <code>start()</code>, <code>(</code>, <code>)</code>, <code>;</code><br><br>" +
"In BNF, terminals are usually shown in <b>quotes</b> or <b>bold</b>.") +

card("#EAF0FF", "#2563EB", "2. Nonterminal Symbols",
"Nonterminals are <b>abstract placeholders</b> that represent categories of syntax. " +
"They are always enclosed in angle brackets.<br><br>" +
"Examples: <code>&lt;thread_creation&gt;</code>, <code>&lt;identifier&gt;</code>, <code>&lt;expression&gt;</code><br><br>" +
"Nonterminals are defined by production rules and do not appear in final source code.") +

card("#FFF4E6", "#F59E0B", "3. Production Rules",
"A production rule defines how a nonterminal is constructed. " +
"The symbol <b>::=</b> means \"is defined as\" or \"can be replaced by\".<br><br>" +
"The symbol <b>|</b> separates alternatives (OR choices).") +

"<h3 style='color:#2563EB'>Anatomy of a BNF Rule</h3>" +
code(
"&lt;rule_name&gt; ::= alternative_one | alternative_two\n\n" +
"Breaking it down:\n" +
"  &lt;rule_name&gt;    — the nonterminal being defined\n" +
"  ::=             — \"is defined as\"\n" +
"  alternative_one — one possible form\n" +
"  |               — OR\n" +
"  alternative_two — another possible form"
) +

"<h3 style='color:#2563EB'>A Simple Grammar Example</h3>" +
code(
"&lt;statement&gt;   ::= &lt;assignment&gt; | &lt;loop&gt; | &lt;parallel_construct&gt;\n" +
"&lt;assignment&gt;  ::= &lt;identifier&gt; \"=\" &lt;expression&gt;\n" +
"&lt;identifier&gt;  ::= letter { letter | digit }\n" +
"&lt;expression&gt;  ::= &lt;identifier&gt; | &lt;literal&gt; | &lt;expression&gt; &lt;op&gt; &lt;expression&gt;\n" +
"&lt;op&gt;          ::= \"+\" | \"-\" | \"*\" | \"/\""
) +

"<p>This tells us: a statement is either an assignment, a loop, or a parallel construct. " +
"An assignment is an identifier followed by <code>=</code> followed by an expression. And so on.</p>" +

tip("BNF rules can be recursive. Notice how &lt;expression&gt; can contain &lt;expression&gt; — " +
"this is how grammars describe things like <code>a + b + c</code> naturally.")
),

// ================= EBNF =================
wrap(
"<h1 style='color:#1E3A6E'>EBNF &amp; Extensions</h1>" +

"<p><b>Extended BNF (EBNF)</b> adds extra notation to make grammars more readable and concise. " +
"Most modern language specifications use EBNF or a variant of it.</p>" +

"<h3 style='color:#2563EB'>EBNF Notation Table</h3>" +
"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'>" +
"<th>Symbol</th><th>Meaning</th><th>Example</th><th>Equivalent BNF idea</th></tr>" +
"<tr style='background:#F4F8FE'><td><code>{ ... }</code></td><td>Zero or more repetitions</td>" +
"<td><code>{ digit }</code></td><td>digit repeated any number of times</td></tr>" +
"<tr><td><code>[ ... ]</code></td><td>Optional (zero or one)</td>" +
"<td><code>[ \"-\" ]</code></td><td>optional minus sign</td></tr>" +
"<tr style='background:#F4F8FE'><td><code>( ... )</code></td><td>Grouping</td>" +
"<td><code>( \"a\" | \"b\" )</code></td><td>either a or b</td></tr>" +
"<tr><td><code>\" ... \"</code></td><td>Terminal literal</td>" +
"<td><code>\"Thread\"</code></td><td>exact keyword Thread</td></tr>" +
"<tr style='background:#F4F8FE'><td><code>|</code></td><td>Alternative (OR)</td>" +
"<td><code>\"int\" | \"double\"</code></td><td>int or double</td></tr>" +
"</table><br>" +

"<h3 style='color:#2563EB'>EBNF in Action: Java Variable Declaration</h3>" +
code(
"&lt;var_decl&gt;  ::= &lt;type&gt; &lt;identifier&gt; [ \"=\" &lt;expression&gt; ] \";\"\n\n" +
"&lt;type&gt;      ::= \"int\" | \"double\" | \"String\" | \"Thread\" | \"boolean\"\n\n" +
"&lt;identifier&gt; ::= letter { letter | digit | \"_\" }\n\n" +
"Examples this grammar accepts:\n" +
"  int x;            — valid (no initializer, optional part omitted)\n" +
"  int x = 5;        — valid (initializer present)\n" +
"  Thread t = new Thread(task);  — valid"
) +

"<h3 style='color:#2563EB'>EBNF for a Simple Parallel Block (Conceptual)</h3>" +
code(
"&lt;parallel_block&gt; ::= \"parallel\" \"{\" { &lt;task_statement&gt; } \"}\"\n\n" +
"&lt;task_statement&gt; ::= &lt;thread_creation&gt; | &lt;thread_start&gt; | &lt;thread_join&gt;\n\n" +
"&lt;thread_join&gt;    ::= &lt;identifier&gt; \".join()\" \";\""
) +

tip("EBNF curly braces <code>{ }</code> do NOT mean a code block like in Java. " +
"In EBNF they mean zero or more repetitions. Context always matters.")
),

// ================= PRODUCTION RULES DEEP DIVE =================
wrap(
"<h1 style='color:#1E3A6E'>Production Rules Deep Dive</h1>" +

"<p>Production rules are the heart of any formal grammar. " +
"Understanding them deeply helps you trace exactly why code is valid or invalid.</p>" +

"<h3 style='color:#2563EB'>Recursive Rules</h3>" +
"<p>A rule is <b>recursive</b> when it refers to itself. This is how grammars handle " +
"arbitrarily long constructs like nested expressions or method chains.</p>" +
code(
"-- Left recursion (expression chains like a + b + c):\n" +
"&lt;expr&gt; ::= &lt;expr&gt; \"+\" &lt;term&gt; | &lt;term&gt;\n\n" +
"-- Right recursion:\n" +
"&lt;list&gt; ::= &lt;item&gt; | &lt;item&gt; \",\" &lt;list&gt;\n\n" +
"-- Example list this matches: item, item, item"
) +

"<h3 style='color:#2563EB'>Derivation: Expanding a Rule Step by Step</h3>" +
"<p>A <b>derivation</b> is the step-by-step process of expanding nonterminals " +
"until only terminals remain. This mirrors what the compiler does during parsing.</p>" +
code(
"Grammar:\n" +
"  &lt;stmt&gt;   ::= &lt;type&gt; &lt;id&gt; \"=\" \"new\" \"Thread\" \"(\" &lt;runnable&gt; \")\" \";\"\n" +
"  &lt;type&gt;   ::= \"Thread\"\n" +
"  &lt;id&gt;     ::= \"t1\"\n" +
"  &lt;runnable&gt; ::= \"myTask\"\n\n" +
"Derivation:\n" +
"  &lt;stmt&gt;\n" +
"  => &lt;type&gt; &lt;id&gt; \"=\" \"new\" \"Thread\" \"(\" &lt;runnable&gt; \")\" \";\"\n" +
"  => \"Thread\" &lt;id&gt; \"=\" \"new\" \"Thread\" \"(\" &lt;runnable&gt; \")\" \";\"\n" +
"  => \"Thread\" \"t1\" \"=\" \"new\" \"Thread\" \"(\" &lt;runnable&gt; \")\" \";\"\n" +
"  => \"Thread\" \"t1\" \"=\" \"new\" \"Thread\" \"(\" \"myTask\" \")\" \";\"\n\n" +
"Final derived string:\n" +
"  Thread t1 = new Thread(myTask);"
) +

"<h3 style='color:#2563EB'>Parse Trees</h3>" +
"<p>A <b>parse tree</b> shows the hierarchical structure of a derivation visually. " +
"Each internal node is a nonterminal; leaves are terminals.</p>" +
code(
"               &lt;stmt&gt;\n" +
"            /    |     \\\n" +
"         &lt;type&gt;  &lt;id&gt;  \"=\" ...\n" +
"           |      |\n" +
"        \"Thread\" \"t1\""
) +

card("#EAF0FF", "#2563EB", "Why Parse Trees Matter",
"Compilers build parse trees internally to understand your code's structure. " +
"A syntax error means the compiler could NOT build a valid parse tree for your input. " +
"This is exactly what error messages like 'unexpected token' mean.") +

warn("Ambiguous grammars produce multiple valid parse trees for the same input. " +
"Ambiguity causes unpredictable compiler behavior and is considered a grammar defect.")
),

// ================= JAVA THREAD SYNTAX =================
wrap(
"<h1 style='color:#1E3A6E'>Java Thread Syntax</h1>" +

"<p>Java supports multithreading natively through the <code>java.lang.Thread</code> class " +
"and the <code>Runnable</code> interface. Let us define these constructs formally.</p>" +

"<h3 style='color:#2563EB'>BNF: Thread Creation</h3>" +
code(
"&lt;thread_decl&gt; ::= \"Thread\" &lt;identifier&gt; \"=\" &lt;thread_init&gt; \";\"\n\n" +
"&lt;thread_init&gt; ::= \"new\" \"Thread\" \"(\" &lt;runnable_expr&gt; \")\"\n\n" +
"&lt;runnable_expr&gt; ::= &lt;identifier&gt;\n" +
"                 | \"new\" &lt;identifier&gt; \"(\" [ &lt;arg_list&gt; ] \")\"\n" +
"                 | &lt;lambda_expr&gt;\n\n" +
"&lt;lambda_expr&gt; ::= \"()\" \"->\" \"{\" { &lt;statement&gt; } \"}\"\n" +
"               | \"()\" \"->\" &lt;statement&gt;"
) +

"<h3 style='color:#2563EB'>Examples this grammar accepts</h3>" +
code(
"// Named Runnable class:\n" +
"Thread t1 = new Thread(myTask);\n\n" +
"// Anonymous Runnable:\n" +
"Thread t2 = new Thread(new Runnable() {\n" +
"    public void run() { /* ... */ }\n" +
"});\n\n" +
"// Lambda (Java 8+):\n" +
"Thread t3 = new Thread(() -> {\n" +
"    System.out.println(\"Running in thread\");\n" +
"});"
) +

"<h3 style='color:#2563EB'>BNF: Thread Lifecycle Methods</h3>" +
code(
"&lt;thread_start&gt; ::= &lt;identifier&gt; \".start()\" \";\"\n\n" +
"&lt;thread_join&gt;  ::= &lt;identifier&gt; \".join()\" \";\"\n" +
"               | &lt;identifier&gt; \".join(\" &lt;long_literal&gt; \")\" \";\"\n\n" +
"&lt;thread_sleep&gt; ::= \"Thread\" \".sleep(\" &lt;long_literal&gt; \")\" \";\""
) +

card("#E6F5EE", "#5bb588", "start() vs run()",
"Calling <code>start()</code> tells the JVM to create a new execution thread and run the task concurrently. " +
"Calling <code>run()</code> directly just executes the method on the current thread — no parallelism occurs. " +
"The BNF makes this distinction concrete: only &lt;thread_start&gt; triggers parallel behavior.") +

"<h3 style='color:#2563EB'>Complete Java Thread Pattern</h3>" +
code(
"// 1. Define task\n" +
"Runnable myTask = () -> {\n" +
"    System.out.println(\"Task running in: \" + Thread.currentThread().getName());\n" +
"};\n\n" +
"// 2. Create thread  (matches &lt;thread_decl&gt;)\n" +
"Thread t = new Thread(myTask);\n\n" +
"// 3. Start thread   (matches &lt;thread_start&gt;)\n" +
"t.start();\n\n" +
"// 4. Wait for completion (matches &lt;thread_join&gt;)\n" +
"t.join();"
) +

tip("You must call <code>join()</code> on worker threads if your main thread needs their results. " +
"Without join(), the main thread may finish and exit before the workers are done.")
),

// ================= JAVA PARALLEL LOOP =================
wrap(
"<h1 style='color:#1E3A6E'>Java Parallel Loop</h1>" +

"<p>Java 8 introduced the <b>Stream API</b> with parallel stream support, " +
"allowing data collections to be processed concurrently without manually managing threads.</p>" +

"<h3 style='color:#2563EB'>BNF: Parallel Stream Pipeline</h3>" +
code(
"&lt;parallel_stream&gt; ::= &lt;collection&gt; \".parallelStream()\" { &lt;stream_op&gt; }\n\n" +
"&lt;stream_op&gt;      ::= \".filter(\" &lt;predicate&gt; \")\"\n" +
"                  | \".map(\" &lt;function&gt; \")\"\n" +
"                  | \".forEach(\" &lt;consumer&gt; \")\"\n" +
"                  | \".reduce(\" &lt;identity&gt; \",\" &lt;accumulator&gt; \")\"\n" +
"                  | \".collect(\" &lt;collector&gt; \")\"\n\n" +
"&lt;predicate&gt;  ::= &lt;lambda_expr&gt; | &lt;method_reference&gt;\n" +
"&lt;function&gt;   ::= &lt;lambda_expr&gt; | &lt;method_reference&gt;\n" +
"&lt;consumer&gt;   ::= &lt;lambda_expr&gt; | &lt;method_reference&gt;"
) +

"<h3 style='color:#2563EB'>Examples</h3>" +
code(
"List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);\n\n" +
"// Basic parallel forEach\n" +
"numbers.parallelStream().forEach(n -> System.out.println(n));\n\n" +
"// Parallel filter + map + collect\n" +
"List<Integer> evens = numbers.parallelStream()\n" +
"    .filter(n -> n % 2 == 0)\n" +
"    .map(n -> n * n)\n" +
"    .collect(Collectors.toList());\n\n" +
"// Parallel reduce (sum)\n" +
"int sum = numbers.parallelStream()\n" +
"    .reduce(0, Integer::sum);"
) +

"<h3 style='color:#2563EB'>BNF: Traditional Parallel Loop with Threads</h3>" +
code(
"&lt;manual_parallel_loop&gt; ::=\n" +
"    \"for\" \"(\" &lt;loop_init&gt; \";\" &lt;condition&gt; \";\" &lt;update&gt; \")\" \"{\"\n" +
"        &lt;thread_decl&gt;\n" +
"        &lt;thread_start&gt;\n" +
"    \"}\"\n" +
"    \"for\" \"(\" &lt;loop_init&gt; \";\" &lt;condition&gt; \";\" &lt;update&gt; \")\" \"{\"\n" +
"        &lt;thread_join&gt;\n" +
"    \"}\""
) +

card("#FFF4E6", "#F59E0B", "When to Use Parallel Streams",
"Parallel streams shine when: the collection is large (thousands+ elements), " +
"each operation is expensive (CPU-bound), and order of output does not matter. " +
"For small datasets or I/O-bound work, sequential streams are often faster.") +

warn("Do NOT use parallel streams with shared mutable state. " +
"Operations like <code>list.add()</code> inside <code>parallelStream().forEach()</code> " +
"will cause race conditions and corrupted data.")
),

// ================= JAVA SYNCHRONIZATION =================
wrap(
"<h1 style='color:#1E3A6E'>Java Synchronization</h1>" +

"<p>When multiple threads share data, we need <b>synchronization</b> to prevent " +
"<b>race conditions</b> — situations where the final result depends on the unpredictable " +
"order threads execute in.</p>" +

"<h3 style='color:#2563EB'>BNF: synchronized Block</h3>" +
code(
"&lt;synchronized_block&gt; ::=\n" +
"    \"synchronized\" \"(\" &lt;lock_object&gt; \")\" \"{\"\n" +
"        { &lt;statement&gt; }\n" +
"    \"}\"\n\n" +
"&lt;lock_object&gt; ::= &lt;identifier&gt; | \"this\""
) +

"<h3 style='color:#2563EB'>BNF: synchronized Method</h3>" +
code(
"&lt;sync_method&gt; ::=\n" +
"    { &lt;modifier&gt; } \"synchronized\" &lt;return_type&gt; &lt;identifier&gt;\n" +
"    \"(\" [ &lt;param_list&gt; ] \")\" \"{\"\n" +
"        { &lt;statement&gt; }\n" +
"    \"}\"\n\n" +
"&lt;modifier&gt; ::= \"public\" | \"private\" | \"protected\" | \"static\""
) +

"<h3 style='color:#2563EB'>BNF: ReentrantLock (java.util.concurrent)</h3>" +
code(
"&lt;lock_decl&gt;    ::= \"ReentrantLock\" &lt;identifier&gt; \"=\" \"new\" \"ReentrantLock()\" \";\"\n\n" +
"&lt;lock_acquire&gt; ::= &lt;identifier&gt; \".lock()\" \";\"\n\n" +
"&lt;lock_release&gt; ::= &lt;identifier&gt; \".unlock()\" \";\"\n\n" +
"&lt;lock_block&gt;   ::= &lt;lock_acquire&gt;\n" +
"               \"try\" \"{\" { &lt;statement&gt; } \"}\"\n" +
"               \"finally\" \"{\" &lt;lock_release&gt; \"}\""
) +

"<h3 style='color:#2563EB'>Example: Counter with Lock</h3>" +
code(
"ReentrantLock lock = new ReentrantLock();\n" +
"int counter = 0;\n\n" +
"Runnable task = () -> {\n" +
"    lock.lock();          // matches &lt;lock_acquire&gt;\n" +
"    try {\n" +
"        counter++;        // critical section\n" +
"    } finally {\n" +
"        lock.unlock();    // matches &lt;lock_release&gt;\n" +
"    }\n" +
"};"
) +

card("#EAF0FF", "#2563EB", "Monitor Model",
"The synchronized keyword uses Java's built-in <b>monitor</b>: every object has one, " +
"and only one thread can hold it at a time. ReentrantLock gives more control " +
"(timed waits, fairness, try-lock).") +

warn("Always release locks in a <code>finally</code> block. " +
"If an exception occurs before <code>unlock()</code>, the lock stays held forever — " +
"causing all other threads to block indefinitely (deadlock).")
),

// ================= PYTHON THREAD SYNTAX =================
wrap(
"<h1 style='color:#1E3A6E'>Python Thread Syntax</h1>" +

"<p>Python's <code>threading</code> module provides a thread class similar to Java's. " +
"However, Python threads are affected by the <b>Global Interpreter Lock (GIL)</b>, " +
"which prevents true CPU parallelism for CPU-bound code.</p>" +

"<h3 style='color:#2563EB'>BNF: Python Thread Creation</h3>" +
code(
"&lt;import_stmt&gt;   ::= \"from\" \"threading\" \"import\" \"Thread\"\n" +
"                | \"import\" \"threading\"\n\n" +
"&lt;thread_decl&gt;   ::= &lt;identifier&gt; \"=\" \"Thread\" \"(\" &lt;thread_args&gt; \")\"\n\n" +
"&lt;thread_args&gt;   ::= \"target\" \"=\" &lt;callable&gt;\n" +
"                | \"target\" \"=\" &lt;callable&gt; \",\" \"args\" \"=\" &lt;tuple&gt;\n" +
"                | \"target\" \"=\" &lt;callable&gt; \",\" \"kwargs\" \"=\" &lt;dict&gt;\n\n" +
"&lt;callable&gt;      ::= &lt;identifier&gt; | &lt;lambda_expr&gt;\n\n" +
"&lt;thread_start&gt;  ::= &lt;identifier&gt; \".start()\"\n\n" +
"&lt;thread_join&gt;   ::= &lt;identifier&gt; \".join()\"\n" +
"                | &lt;identifier&gt; \".join(\" &lt;float_literal&gt; \")\""
) +

"<h3 style='color:#2563EB'>Examples</h3>" +
code(
"from threading import Thread\n\n" +
"# Basic thread with target function\n" +
"def my_task():\n" +
"    print('Running in thread')\n\n" +
"t = Thread(target=my_task)    # matches &lt;thread_decl&gt;\n" +
"t.start()                     # matches &lt;thread_start&gt;\n" +
"t.join()                      # matches &lt;thread_join&gt;\n\n" +
"# Thread with arguments\n" +
"def worker(num):\n" +
"    print(f'Worker {num}')\n\n" +
"t2 = Thread(target=worker, args=(42,))  # args is a tuple\n" +
"t2.start()\n" +
"t2.join()"
) +

"<h3 style='color:#2563EB'>BNF: ThreadPoolExecutor (Modern Python)</h3>" +
code(
"&lt;pool_import&gt; ::= \"from\" \"concurrent.futures\" \"import\" \"ThreadPoolExecutor\"\n\n" +
"&lt;pool_block&gt;  ::= \"with\" \"ThreadPoolExecutor\" \"(\" \"max_workers\" \"=\" &lt;int_literal&gt; \")\"\n" +
"               \"as\" &lt;identifier&gt; \":\" &lt;indented_block&gt;\n\n" +
"&lt;submit_call&gt; ::= &lt;identifier&gt; \".submit(\" &lt;callable&gt; [ \",\" &lt;arg_list&gt; ] \")\"\n\n" +
"&lt;map_call&gt;    ::= &lt;identifier&gt; \".map(\" &lt;callable&gt; \",\" &lt;iterable&gt; \")\""
) +

code(
"from concurrent.futures import ThreadPoolExecutor\n\n" +
"def process_item(item):\n" +
"    return item * 2\n\n" +
"with ThreadPoolExecutor(max_workers=4) as executor:\n" +
"    results = list(executor.map(process_item, range(100)))"
) +

card("#FFF4E6", "#F59E0B", "When Are Python Threads Useful?",
"Python threads excel at <b>I/O-bound</b> tasks: file reading, web requests, database queries. " +
"While one thread waits for I/O, other threads can run. " +
"For CPU-heavy computation, use multiprocessing instead.")
),

// ================= PYTHON PROCESS SYNTAX =================
wrap(
"<h1 style='color:#1E3A6E'>Python Process Syntax</h1>" +

"<p>The <code>multiprocessing</code> module creates separate Python interpreter processes, " +
"each with its own memory and GIL. This is how Python achieves <b>true CPU parallelism</b>.</p>" +

"<h3 style='color:#2563EB'>BNF: Process Creation</h3>" +
code(
"&lt;import_stmt&gt;    ::= \"from\" \"multiprocessing\" \"import\" \"Process\"\n" +
"                 | \"import\" \"multiprocessing\"\n\n" +
"&lt;process_decl&gt;   ::= &lt;identifier&gt; \"=\" \"Process\" \"(\" &lt;process_args&gt; \")\"\n\n" +
"&lt;process_args&gt;   ::= \"target\" \"=\" &lt;callable&gt;\n" +
"                 | \"target\" \"=\" &lt;callable&gt; \",\" \"args\" \"=\" &lt;tuple&gt;\n\n" +
"&lt;process_start&gt;  ::= &lt;identifier&gt; \".start()\"\n\n" +
"&lt;process_join&gt;   ::= &lt;identifier&gt; \".join()\"\n\n" +
"&lt;process_kill&gt;   ::= &lt;identifier&gt; \".terminate()\""
) +

"<h3 style='color:#2563EB'>Example: Multiple Processes</h3>" +
code(
"from multiprocessing import Process\n\n" +
"def compute(n):\n" +
"    result = sum(i * i for i in range(n))\n" +
"    print(f'Result: {result}')\n\n" +
"# Create processes\n" +
"p1 = Process(target=compute, args=(1000000,))\n" +
"p2 = Process(target=compute, args=(2000000,))\n\n" +
"# Start both\n" +
"p1.start()\n" +
"p2.start()\n\n" +
"# Wait for both to finish\n" +
"p1.join()\n" +
"p2.join()\n" +
"print('All done')"
) +

"<h3 style='color:#2563EB'>BNF: ProcessPoolExecutor</h3>" +
code(
"&lt;pool_import&gt; ::= \"from\" \"concurrent.futures\" \"import\" \"ProcessPoolExecutor\"\n\n" +
"&lt;pool_block&gt;  ::= \"with\" \"ProcessPoolExecutor\" \"(\" \"max_workers\" \"=\" &lt;int_literal&gt; \")\"\n" +
"               \"as\" &lt;identifier&gt; \":\" &lt;indented_block&gt;"
) +

code(
"from concurrent.futures import ProcessPoolExecutor\n\n" +
"def heavy_compute(n):\n" +
"    return sum(i ** 2 for i in range(n))\n\n" +
"with ProcessPoolExecutor(max_workers=4) as executor:\n" +
"    futures = [executor.submit(heavy_compute, 10**6) for _ in range(4)]\n" +
"    results = [f.result() for f in futures]"
) +

card("#EAF0FF", "#2563EB", "Process vs Thread: Memory Model",
"Threads share memory — one thread can read/write the same variable as another. " +
"Processes have <b>separate</b> memory — data must be explicitly shared using " +
"<code>multiprocessing.Queue</code>, <code>Pipe</code>, or <code>Manager</code>.") +

warn("On Windows, multiprocessing code must be inside <code>if __name__ == '__main__':</code> " +
"to prevent recursive process spawning. This is a common beginner mistake.")
),

// ================= PYTHON LOCKS =================
wrap(
"<h1 style='color:#1E3A6E'>Python with Locks</h1>" +

"<p>Python's <code>threading</code> module provides locking primitives " +
"that mirror Java's synchronization mechanisms, but with Python-style syntax.</p>" +

"<h3 style='color:#2563EB'>BNF: Lock Usage</h3>" +
code(
"&lt;lock_import&gt;   ::= \"from\" \"threading\" \"import\" \"Lock\"\n\n" +
"&lt;lock_decl&gt;     ::= &lt;identifier&gt; \"=\" \"Lock()\"\n\n" +
"-- Explicit acquire/release:\n" +
"&lt;lock_acquire&gt;  ::= &lt;identifier&gt; \".acquire()\"\n" +
"&lt;lock_release&gt;  ::= &lt;identifier&gt; \".release()\"\n\n" +
"-- Context manager (preferred):\n" +
"&lt;lock_with&gt;     ::= \"with\" &lt;identifier&gt; \":\" &lt;indented_block&gt;"
) +

"<h3 style='color:#2563EB'>Example: Thread-Safe Counter</h3>" +
code(
"from threading import Thread, Lock\n\n" +
"counter = 0\n" +
"lock = Lock()     # matches &lt;lock_decl&gt;\n\n" +
"def increment():\n" +
"    global counter\n" +
"    with lock:    # matches &lt;lock_with&gt; — acquires then auto-releases\n" +
"        counter += 1\n\n" +
"threads = [Thread(target=increment) for _ in range(1000)]\n" +
"for t in threads: t.start()\n" +
"for t in threads: t.join()\n" +
"print(counter)  # Always prints 1000"
) +

"<h3 style='color:#2563EB'>Other Synchronization Primitives</h3>" +
"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'><th>Primitive</th><th>Purpose</th><th>BNF Declaration</th></tr>" +
"<tr style='background:#F4F8FE'><td><code>Lock</code></td><td>Mutual exclusion</td>" +
"<td><code>&lt;id&gt; = Lock()</code></td></tr>" +
"<tr><td><code>RLock</code></td><td>Reentrant lock (same thread can lock twice)</td>" +
"<td><code>&lt;id&gt; = RLock()</code></td></tr>" +
"<tr style='background:#F4F8FE'><td><code>Semaphore</code></td><td>Limit number of simultaneous accessors</td>" +
"<td><code>&lt;id&gt; = Semaphore(&lt;n&gt;)</code></td></tr>" +
"<tr><td><code>Event</code></td><td>Signal between threads</td>" +
"<td><code>&lt;id&gt; = Event()</code></td></tr>" +
"<tr style='background:#F4F8FE'><td><code>Barrier</code></td><td>All threads wait at a point</td>" +
"<td><code>&lt;id&gt; = Barrier(&lt;n&gt;)</code></td></tr>" +
"</table><br>" +

tip("Prefer the <code>with lock:</code> context manager over explicit <code>acquire()</code>/<code>release()</code>. " +
"The <code>with</code> statement guarantees the lock is released even if an exception is raised.")
),

// ================= SYNTAX DIAGRAMS =================
wrap(
"<h1 style='color:#1E3A6E'>Syntax Diagrams</h1>" +

"<p><b>Syntax diagrams</b> (also called <b>railroad diagrams</b>) are a visual alternative to BNF. " +
"They represent the same grammar rules as a path through a directed graph. " +
"You follow the arrows; wherever the path branches, you have a choice.</p>" +

"<h3 style='color:#2563EB'>Reading Conventions</h3>" +
"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'><th>Shape</th><th>Meaning</th></tr>" +
"<tr style='background:#F4F8FE'><td>Rounded rectangle</td><td>Terminal symbol (literal token)</td></tr>" +
"<tr><td>Rectangle</td><td>Nonterminal (reference to another rule)</td></tr>" +
"<tr style='background:#F4F8FE'><td>Arrow</td><td>Required sequence (one direction)</td></tr>" +
"<tr><td>Branch/fork</td><td>Alternative choices (like BNF |)</td></tr>" +
"<tr style='background:#F4F8FE'><td>Loop-back arrow</td><td>Repetition (like EBNF { })</td></tr>" +
"</table><br>" +

"<h3 style='color:#2563EB'>Diagram: Java Thread Creation</h3>" +
code(
"START\n" +
"  |\n" +
"  v\n" +
"[Thread]---->[identifier]---->[=]---->[new]---->[Thread]---->(---->[runnable]-----)---->[;]\n" +
"                                                                                     |\n" +
"                                                                                     v\n" +
"                                                                                    END"
) +

"<h3 style='color:#2563EB'>Diagram: Thread Lifecycle</h3>" +
code(
"START\n" +
"  |\n" +
"  v\n" +
"[Thread decl]---->[.start()]---->[... runs concurrently ...]---->[.join()]---->[END]\n" +
"                                          |\n" +
"                              (other threads also running here)"
) +

"<h3 style='color:#2563EB'>Diagram: Python Thread with Args</h3>" +
code(
"START\n" +
"  |\n" +
"  v\n" +
"[identifier]---->[=]---->[Thread(]---->[target=]---->[callable]\n" +
"                                              |\n" +
"                                    optional branch:\n" +
"                                       [, args=]---->[tuple]\n" +
"                                              |\n" +
"                                            [)]\n" +
"                                              |\n" +
"                                             END"
) +

"<h3 style='color:#2563EB'>Diagram: synchronized Block</h3>" +
code(
"START\n" +
"  |\n" +
"  v\n" +
"[synchronized]---->(---->[lock_object]-----)---->[{]---->[statements]---->[}]---->[END]"
) +

tip("Railroad diagrams are used extensively in the official Java Language Specification and in many " +
"database query language docs (like SQL). Recognizing them will help you read official documentation.")
),

// ================= READING A SYNTAX DIAGRAM =================
wrap(
"<h1 style='color:#1E3A6E'>Reading a Syntax Diagram</h1>" +

"<p>Let us practice reading syntax diagrams step-by-step with a complete worked example.</p>" +

"<h3 style='color:#2563EB'>Example: Java parallelStream() Pipeline</h3>" +
code(
"START\n" +
"  |\n" +
"  v\n" +
"[collection]---->[.parallelStream()]---->[stream operation loop:]\n" +
"                                              |\n" +
"                          .------------------<------------------.\n" +
"                          |                                     |\n" +
"                          v                                     ^\n" +
"               choose one of:                                   |\n" +
"                [.filter(predicate)]----------------------------'\n" +
"                [.map(function)]--------------------------------'\n" +
"                [.forEach(consumer)]----------------------------' (terminal: ends pipeline)\n" +
"                [.reduce(identity, accumulator)]----------------' (terminal: ends pipeline)\n" +
"                [.collect(collector)]---------------------------' (terminal: ends pipeline)"
) +

"<h3 style='color:#2563EB'>Trace: numbers.parallelStream().filter(x -> x > 0).map(x -> x*2).collect(...)</h3>" +
code(
"Step 1: [collection]         => numbers\n" +
"Step 2: [.parallelStream()]  => enters parallel stream\n" +
"Step 3: loop iteration 1:\n" +
"         choose .filter()    => .filter(x -> x > 0)\n" +
"Step 4: loop iteration 2:\n" +
"         choose .map()       => .map(x -> x * 2)\n" +
"Step 5: loop iteration 3:\n" +
"         choose .collect()   => terminal operation, exits loop\n" +
"Result: valid parallel stream pipeline"
) +

"<h3 style='color:#2563EB'>Trace: What Would Be Invalid?</h3>" +
code(
"numbers.parallelStream().collect(...).filter(x -> x > 0)\n\n" +
"Why invalid:\n" +
"  .collect() is a TERMINAL operation — the diagram has no arrow\n" +
"  going back to another operation after a terminal.\n" +
"  The grammar does not permit chaining after .collect()."
) +

card("#EAF0FF", "#2563EB", "Key Insight",
"Syntax diagrams make it immediately obvious which operations can be chained and in what order. " +
"This is exactly what a compiler checks during parsing — before any code runs.") +

tip("When you encounter a confusing API, try sketching a syntax diagram for it. " +
"It forces you to identify what is required, what is optional, and what the valid orderings are.")
),

// ================= HOW SYNTAX RELATES =================
wrap(
"<h1 style='color:#1E3A6E'>How Syntax Relates to Parallel Behavior</h1>" +

"<p>Programming language syntax is not just about formatting — it directly determines " +
"how programs execute, communicate, and share resources at runtime.</p>" +

"<h3 style='color:#2563EB'>Syntax &rarr; Semantics &rarr; Behavior</h3>" +
code(
"Syntax    — What the code looks like (is it grammatically valid?)\n" +
"Semantics — What the code means (what will it do when executed?)\n" +
"Behavior  — What actually happens at runtime\n\n" +
"Example:\n" +
"  t.start();   // syntax: valid &lt;thread_start&gt;\n" +
"               // semantics: JVM spawns a new OS-level thread\n" +
"               // behavior: concurrent execution begins\n\n" +
"  t.run();     // syntax: valid method call (compiles!)\n" +
"               // semantics: calls run() on the CURRENT thread\n" +
"               // behavior: sequential — NO parallelism"
) +

card("#E6F5EE", "#5bb588", "Thread Creation Syntax", 
"The choice of &lt;runnable_expr&gt; — named class, anonymous class, or lambda — " +
"does not change concurrency behavior but affects readability and how state is captured.") +

card("#EAF0FF", "#2563EB", "Synchronization Syntax", 
"The scope of a <code>synchronized</code> block defines precisely which statements " +
"are protected. Placing the braces incorrectly can leave a critical section unprotected " +
"even though the code compiles and runs without error.") +

card("#FFF4E6", "#F59E0B", "Join Syntax", 
"Whether you call <code>join()</code> or omit it changes whether the main thread waits " +
"for results. This is a semantic distinction expressed purely through syntax choice.") +

"<h3 style='color:#2563EB'>Race Condition from Missing Synchronization</h3>" +
code(
"// BROKEN: no synchronization\n" +
"int counter = 0;\n" +
"Runnable r = () -> { counter++; };\n" +
"Thread t1 = new Thread(r);\n" +
"Thread t2 = new Thread(r);\n" +
"t1.start(); t2.start();\n" +
"t1.join();  t2.join();\n" +
"// counter may be 1 instead of 2 — race condition!\n\n" +
"// FIXED: synchronized block\n" +
"Object lock = new Object();\n" +
"Runnable r = () -> { synchronized(lock) { counter++; } };"
) +

warn("Race conditions are runtime bugs caused by missing synchronization syntax. " +
"The broken version above compiles and usually runs — it just produces wrong answers inconsistently. " +
"This makes them among the hardest bugs to detect and fix.")
),

// ================= COMMON SYNTAX ERRORS =================
wrap(
"<h1 style='color:#1E3A6E'>Common Syntax Errors in Parallel Code</h1>" +

"<p>Understanding BNF grammar helps you diagnose and fix common mistakes in parallel code.</p>" +

"<h3 style='color:#2563EB'>Java Errors</h3>" +

"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'><th>Mistake</th><th>Wrong Code</th><th>Correct Code</th><th>Why</th></tr>" +
"<tr style='background:#F4F8FE'><td>Calling run() instead of start()</td>" +
"<td><code>t.run();</code></td><td><code>t.start();</code></td>" +
"<td>run() executes on current thread — no new thread is spawned</td></tr>" +
"<tr><td>Forgetting join()</td>" +
"<td>(missing join)</td><td><code>t.join();</code></td>" +
"<td>Main thread exits before worker finishes</td></tr>" +
"<tr style='background:#F4F8FE'><td>Modifying shared state without lock</td>" +
"<td><code>count++</code></td><td><code>synchronized(lock){count++;}</code></td>" +
"<td>Non-atomic read-modify-write causes race condition</td></tr>" +
"<tr><td>Lock not released on exception</td>" +
"<td><code>lock.lock(); ...; lock.unlock();</code></td>" +
"<td><code>lock.lock(); try{...}finally{lock.unlock();}</code></td>" +
"<td>Exception skips unlock, causing deadlock</td></tr>" +
"</table><br>" +

"<h3 style='color:#2563EB'>Python Errors</h3>" +

"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'><th>Mistake</th><th>Wrong</th><th>Correct</th><th>Why</th></tr>" +
"<tr style='background:#F4F8FE'><td>Passing result of calling function</td>" +
"<td><code>target=my_task()</code></td><td><code>target=my_task</code></td>" +
"<td>my_task() calls the function immediately; omit () to pass the function itself</td></tr>" +
"<tr><td>Single-element tuple</td>" +
"<td><code>args=(42)</code></td><td><code>args=(42,)</code></td>" +
"<td>(42) is just parentheses around int; (42,) is a tuple</td></tr>" +
"<tr style='background:#F4F8FE'><td>Missing __main__ guard</td>" +
"<td>(no guard on Windows)</td><td><code>if __name__ == '__main__':</code></td>" +
"<td>multiprocessing spawns new processes that re-import module, causing infinite spawning</td></tr>" +
"<tr><td>Using threads for CPU-bound work</td>" +
"<td><code>Thread(target=heavy_math)</code></td>" +
"<td><code>Process(target=heavy_math)</code></td>" +
"<td>GIL prevents threads from running Python bytecode in parallel</td></tr>" +
"</table><br>" +

tip("The BNF rule for Python's Thread args: <code>\"args\" \"=\" &lt;tuple&gt;</code> — " +
"the type must be a tuple. This is why <code>args=(42)</code> fails: <code>(42)</code> " +
"is an int, not a tuple. The trailing comma creates a tuple: <code>(42,)</code>.")
),

// ================= JAVA VS PYTHON =================
wrap(
"<h1 style='color:#1E3A6E'>Java vs Python: Syntax &amp; Parallel Model Comparison</h1>" +

"<p>Java and Python express parallel programming differently at every level — " +
"from syntax to runtime behavior.</p>" +

"<h3 style='color:#2563EB'>Side-by-Side BNF Comparison</h3>" +
"<table border='1' cellpadding='7' cellspacing='0' width='100%' " +
"style='border-collapse:collapse;font-size:12px;border-color:#C7D2E8'>" +
"<tr style='background:#3478DC;color:white'><th>Construct</th><th>Java BNF</th><th>Python BNF</th></tr>" +
"<tr style='background:#F4F8FE'><td>Thread creation</td>" +
"<td><code>\"Thread\" id \"=\" \"new\" \"Thread\" \"(\" runnable \")\"</code></td>" +
"<td><code>id \"=\" \"Thread\" \"(\" \"target=\" callable \")\"</code></td></tr>" +
"<tr><td>Thread start</td>" +
"<td><code>id \".start()\"</code></td>" +
"<td><code>id \".start()\"</code></td></tr>" +
"<tr style='background:#F4F8FE'><td>Thread join</td>" +
"<td><code>id \".join()\"</code></td>" +
"<td><code>id \".join()\"</code></td></tr>" +
"<tr><td>Mutual exclusion</td>" +
"<td><code>\"synchronized\" \"(\" lock \")\" block</code></td>" +
"<td><code>\"with\" lock \":\" block</code></td></tr>" +
"<tr style='background:#F4F8FE'><td>CPU parallelism</td>" +
"<td><code>Thread (native OS thread)</code></td>" +
"<td><code>Process (separate interpreter)</code></td></tr>" +
"<tr><td>Parallel loop</td>" +
"<td><code>collection \".parallelStream()\" ops</code></td>" +
"<td><code>Pool.map(func, iterable)</code></td></tr>" +
"</table><br>" +

"<h3 style='color:#2563EB'>Runtime Model Comparison</h3>" +
code(
"Java:\n" +
"  Thread  ---[OS thread]-->  CPU core 1\n" +
"  Thread  ---[OS thread]-->  CPU core 2\n" +
"  Thread  ---[OS thread]-->  CPU core 3\n" +
"  (True parallel execution on multiple cores)\n\n" +
"Python Threads:\n" +
"  Thread  --[GIL blocks]-->  CPU core 1 only\n" +
"  Thread  --[GIL blocks]--> (waiting)\n" +
"  (Only one thread runs Python bytecode at a time)\n\n" +
"Python Processes:\n" +
"  Process ---[own interpreter]--> CPU core 1\n" +
"  Process ---[own interpreter]--> CPU core 2\n" +
"  (True parallel execution, but separate memory)"
) +

card("#3478DC", "#3478DC", "Java Strengths",
"True multithreading with shared memory. Fine-grained control with synchronized, " +
"ReentrantLock, atomic variables. Excellent for CPU-bound and I/O-bound concurrent work.") +

card("#5bb588", "#5bb588", "Python Strengths",
"Simple threading syntax for I/O-bound work. multiprocessing for CPU parallelism. " +
"concurrent.futures provides a clean high-level abstraction for both.") +

card("#FFF4E6", "#F59E0B", "Key Difference Summary",
"Java: one process, many threads, shared memory, true CPU parallelism.<br>" +
"Python threads: one process, many threads, shared memory, NO true CPU parallelism (GIL).<br>" +
"Python multiprocessing: many processes, separate memory, true CPU parallelism.")
),

// ================= RESOURCES =================
wrap(
"<h1 style='color:#1E3A6E'>Resources &amp; Further Reading</h1>" +

"<p>These resources will deepen your understanding of BNF, syntax diagrams, and parallel programming.</p>" +

"<h3 style='color:#2563EB'>BNF &amp; Formal Grammars</h3>" +

card("#E8F0FE", "#3478DC", "Wikipedia: Backus–Naur Form",
"Comprehensive reference for BNF notation, history, and EBNF extensions.<br>" +
"<b>URL:</b> https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form") +

card("#E8F0FE", "#3478DC", "Railroad Diagram Generator",
"Online tool to convert BNF/EBNF into interactive railroad diagrams. Paste any grammar and see it visualized.<br>" +
"<b>URL:</b> https://rr.databricks.com") +

card("#E8F0FE", "#3478DC", "Online BNF Playground",
"Browser-based tool to write and test BNF grammars interactively.<br>" +
"<b>URL:</b> https://bnfplayground.pauliankline.com") +

"<h3 style='color:#2563EB'>Java Parallel Programming</h3>" +

card("#E6F5EE", "#5bb588", "Oracle: Java Concurrency Tutorial (Official)",
"The definitive beginner guide to Java threads, synchronization, and concurrent collections.<br>" +
"<b>URL:</b> https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html") +

card("#E6F5EE", "#5bb588", "Java Language Specification (JLS) — Threads",
"The formal BNF-style grammar specification for Java, including thread and synchronization syntax.<br>" +
"<b>URL:</b> https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html") +

card("#E6F5EE", "#5bb588", "Baeldung: Java Concurrency",
"Beginner-friendly tutorials on threads, locks, executors, and parallel streams with examples.<br>" +
"<b>URL:</b> https://www.baeldung.com/java-concurrency") +

"<h3 style='color:#2563EB'>Python Parallel Programming</h3>" +

card("#FFF4E6", "#F59E0B", "Python Docs: threading module (Official)",
"Complete reference for Python's threading API with examples.<br>" +
"<b>URL:</b> https://docs.python.org/3/library/threading.html") +

card("#FFF4E6", "#F59E0B", "Python Docs: multiprocessing module (Official)",
"Complete reference for Python's multiprocessing API with examples.<br>" +
"<b>URL:</b> https://docs.python.org/3/library/multiprocessing.html") +

card("#FFF4E6", "#F59E0B", "Real Python: Python GIL Explained",
"Clear explanation of the Global Interpreter Lock and why it matters for parallel code.<br>" +
"<b>URL:</b> https://realpython.com/python-gil/") +

"<h3 style='color:#2563EB'>Programming Language Theory</h3>" +

card("#EAF0FF", "#2563EB", "Crafting Interpreters (Free Online Book)",
"A full introduction to language grammar, parsing, and interpretation. Covers BNF and syntax diagrams extensively.<br>" +
"<b>URL:</b> https://craftinginterpreters.com") +

card("#EAF0FF", "#2563EB", "Stanford: Compilers Course (Free on Coursera)",
"University-level course covering formal grammars, BNF, parse trees, and language implementation.<br>" +
"<b>URL:</b> https://www.coursera.org/learn/compilers") +

tip("Start with the Oracle Java Concurrency Tutorial and Python's official threading docs — " +
"they are written for beginners and use real code examples throughout.")
)
    };

    public static String show() {
        return "=== SYNTAX MODULE ===";
    }

    // =========================
    // MAIN PANEL
    // =========================

    public static JPanel buildPanel(Runnable onBack) {

        Color BG = new Color(245, 247, 250);
        Color SIDE = new Color(30, 40, 60);
        Color ACCENT = new Color(52, 120, 220);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new CompoundBorder(
                new LineBorder(new Color(52, 120, 220), 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));
        JLabel htitle = new JLabel("=== SYNTAX MODULE ===", SwingConstants.CENTER);
        htitle.setFont(new Font("Arial", Font.BOLD, 15));
        htitle.setForeground(Color.WHITE);
        header.add(htitle);
        root.add(header, BorderLayout.NORTH);

        // CONTENT AREA
        JEditorPane area = new JEditorPane("text/html", CONTENT[0]);
        area.setEditable(false);
        area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        area.setBackground(BG);
        area.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Enable hyperlink navigation (opens browser)
        area.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (Exception ex) {
                    // silently ignore if browser unavailable
                }
            }
        });

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 225, 235), 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 55, 75), 1, true),
                new EmptyBorder(8, 6, 8, 6)
        ));

        JButton[] btns = new JButton[TOPICS.length];

        for (int i = 0; i < TOPICS.length; i++) {
            final int idx = i;
            JButton b = new JButton(TOPICS[i]);
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setForeground(new Color(170, 190, 220));
            b.setBackground(SIDE);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setBorder(new EmptyBorder(9, 16, 9, 16));
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!b.getBackground().equals(ACCENT))
                        b.setBackground(new Color(45, 60, 85));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!b.getBackground().equals(ACCENT))
                        b.setBackground(SIDE);
                }
            });

            b.addActionListener(e -> {
                for (JButton x : btns) {
                    x.setBackground(SIDE);
                    x.setForeground(new Color(170, 190, 220));
                    x.setFont(new Font("Arial", Font.PLAIN, 12));
                }
                b.setBackground(ACCENT);
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Arial", Font.BOLD, 12));
                area.setText(CONTENT[idx]);
                area.setCaretPosition(0);
            });

            btns[i] = b;
            sidebar.add(b);

            // Add divider after logical groups
            if (i == 0 || i == 3 || i == 7 || i == 10 || i == 12) {
                JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
                sep.setForeground(new Color(55, 70, 95));
                sep.setBackground(new Color(55, 70, 95));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                sidebar.add(sep);
            }
        }

        btns[0].setBackground(ACCENT);
        btns[0].setForeground(Color.WHITE);
        btns[0].setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane sideScroll = new JScrollPane(sidebar);
        sideScroll.setBorder(null);
        sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sideScroll.getVerticalScrollBar().setUnitIncrement(10);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideScroll, scroll);
        split.setDividerSize(2);
        split.setDividerLocation(240);
        split.setEnabled(false);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(6, 12, 6, 12));

        JButton back = new JButton("Back to Menu");
        back.setFont(new Font("Arial", Font.PLAIN, 13));
        back.setBackground(ACCENT);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorder(new CompoundBorder(
                new LineBorder(new Color(52, 120, 220), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> { if (onBack != null) onBack.run(); });
        footer.add(back);

        // Topic count label
        JLabel topicCount = new JLabel(TOPICS.length + " topics");
        topicCount.setFont(new Font("Arial", Font.ITALIC, 11));
        topicCount.setForeground(new Color(130, 140, 160));
        footer.add(topicCount);

        root.add(footer, BorderLayout.SOUTH);
        return root;
    }
}