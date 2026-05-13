package modules_java;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class TheoryModule {

    private static final String[] TOPICS = {
        "Overview","Variables","Scope","Lifetime",
        "Type/Value Binding","Control Structures",
        "Threads vs Processes","Java vs Python"
    };

    private static String card(String bg,String border,String title,String body){
        return "<div style='background:"+bg+
        ";border-left:5px solid "+border+
        ";padding:10px 14px;margin:8px 0;border-radius:16px;'>"+
        "<b style='color:"+border+"'>"+title+"</b><br><br>"+
        "<span style='color:#333;font-size:12px'>"+body+"</span></div>";
    }

    private static String box(String col,String title,String body){
        return "<div style='background:#FFF;border:1.5px solid "+col+
        ";padding:12px 14px;margin:8px 0;border-radius:18px;'>"+
        "<b style='color:"+col+"'>"+title+"</b><br><br>"+
        "<span style='color:#333;font-size:12px'>"+body+"</span></div>";
    }

    private static String row2(String a,String b){
        return "<tr>"+
        "<td style='padding:8px 12px;background:#D6E8F8;color:#1E3A6E;width:28%;border-radius:12px 0 0 12px'><b>"+a+"</b></td>"+
        "<td style='padding:8px 12px;color:#333;background:#F4F8FE;border-radius:0 12px 12px 0'>"+b+"</td></tr>"+
        "<tr><td colspan='2' style='padding:2px'></td></tr>";
    }

    private static String row3(String a,String b,String c){
        return "<tr>"+
        "<td style='padding:8px 12px;background:#D6E8F8;color:#1E3A6E;width:24%;border-radius:12px 0 0 12px'><b>"+a+"</b></td>"+
        "<td style='padding:8px 12px;color:#333;background:#F4F8FE;width:38%'>"+b+"</td>"+
        "<td style='padding:8px 12px;color:#333;background:#EEF4FD;width:38%;border-radius:0 12px 12px 0'>"+c+"</td></tr>"+
        "<tr><td colspan='3' style='padding:2px'></td></tr>";
    }

    private static String tableWrap(String inner){
        return "<div style='border-radius:18px;overflow:hidden;border:1.5px solid #C0D4EF;margin:8px 0'>"+
        "<table width='100%' cellspacing='0' cellpadding='0'>"+inner+"</table></div>";
    }

    private static String thead2(String a,String b){
        return "<tr style='background:#1E3A6E;color:white'>"+
        "<td style='padding:9px 12px;border-radius:16px 0 0 0'><b>"+a+"</b></td>"+
        "<td style='padding:9px 12px;border-radius:0 16px 0 0'><b>"+b+"</b></td></tr>";
    }

    private static String thead3(String a,String b,String c){
        return "<tr style='background:#1E3A6E;color:white'>"+
        "<td style='padding:9px 12px;border-radius:16px 0 0 0'><b>"+a+"</b></td>"+
        "<td style='padding:9px 12px'><b>"+b+"</b></td>"+
        "<td style='padding:9px 12px;border-radius:0 16px 0 0'><b>"+c+"</b></td></tr>";
    }

    private static String wrap(String body){
        return "<html><body style='font-family:Arial;font-size:12px;color:#222;padding:10px;margin:0'>"+
        body+"</body></html>";
    }

    private static final String[] CONTENT = {

    wrap("<h1 style='color:#1E3A6E'>Overview</h1>"+
    "<p>Parallel programming is a computing paradigm where multiple tasks execute simultaneously across multiple processors or threads. It offers significant performance benefits but introduces behaviors that differ fundamentally from sequential programs.</p>"+
    box("#3478DC","Why Theory Matters",
    "Understanding parallel programs requires grounding in programming language theory: how variables, scope, lifetime, type and value binding, and control structures operate in a concurrent environment.")),

    wrap("<h1 style='color:#1E3A6E'>Variables</h1>"+
    "<p>A variable is a named storage location that holds a value during program execution. In sequential programming, only one thread accesses a variable at a time. In parallel programming, multiple threads may read and write shared variables concurrently, introducing data races and synchronization challenges.</p>"+
    "<b style='color:#1E3A6E'>Classification by sharing:</b><br><br>"+
    card("#E8F0FE","#3478DC","Shared Variable","Visible and accessible to all threads. Requires synchronization to avoid race conditions.")+
    card("#E6F5EE","#5bb588","Private Variable","Belongs exclusively to one thread. No synchronization needed. Safe by design.")+
    card("#EAF0FF","#2563EB","Reduction Variable","Each thread keeps a local copy combined into one aggregate result at the end of parallel execution.")+
    tableWrap(thead2("Language","Notes")+
    row2("Java","The volatile keyword ensures a shared variable is always read from main memory, not a thread-local cache. synchronized blocks protect mutable shared state.")+
    row2("Python","Variables in threaded code share process memory. The GIL provides limited safety for threads. multiprocessing assigns separate memory spaces to each process."))),

    wrap("<h1 style='color:#1E3A6E'>Scope</h1>"+
    "<p>Scope refers to the region of a program where a variable is visible and accessible. Scope determines which variables are shared across threads and which are private.</p>"+
    box("#1E3A6E","Primary Scoping Rules",
    "<b>Static (Lexical) Scope</b> is determined at compile time based on source code. Both Java and Python use static scoping.<br><br><b>Dynamic Scope</b> is determined at runtime.")+
    tableWrap(thead2("Language","Notes")+
    row2("Java","Shared instance fields require synchronized access or atomic types.")+
    row2("Python","Use threading.local() for thread-local storage. Module-level variables are shared."))),

    wrap("<h1 style='color:#1E3A6E'>Lifetime</h1>"+
    "<p>The lifetime of a variable is the duration during which it occupies memory, from creation to destruction.</p>"+
    box("#2563EB","Core Rule","A thread must not outlive the variable it references.")+
    card("#E8F0FE","#3478DC","Static Lifetime","Exists for the entire program. Shared across all threads.")+
    card("#E6F5EE","#65ac89","Stack (Local) Lifetime","Created on function call and destroyed on return.")+
    card("#EAF0FF","#2563EB","Heap Lifetime","Persists while references remain. Shared mutable objects need synchronization.")+
    "<p style='color:#555'>Both Java and Python manage heap lifetimes through garbage collection.</p>"),

    wrap("<h1 style='color:#1E3A6E'>Type and Value Binding</h1>"+
    box("#1E3A6E","Type Binding",
    "<b>Static (Java)</b> binds type at compile time.<br><br><b>Dynamic (Python)</b> resolves type at runtime.")+
    box("#65ac89","Value Binding",
    "Race conditions are conflicts over value binding: two threads write the same variable simultaneously.")+
    card("#E8F0FE","#3478DC","Immutable Binding","Value cannot change after assignment. Thread-safe.")+
    card("#EAF0FF","#2563EB","Mutable Binding","Value can change after assignment. Requires synchronization.")+
    "<p style='color:#555'>Prefer immutable bindings whenever possible.</p>"),

    wrap("<h1 style='color:#1E3A6E'>Control Structures</h1>"+
    "<p>In sequential programs, execution follows a single deterministic path. In parallel programs, multiple threads execute concurrently.</p>"+
    tableWrap(thead2("Construct","Description")+
    row2("Fork / Spawn","Creates a new thread or process to execute a task concurrently with the caller.")+
    row2("Join","Blocks the calling thread until a specified thread completes.")+
    row2("Mutual Exclusion","Ensures only one thread executes a critical section at a time.")+
    row2("Atomic Operation","An indivisible read-modify-write operation that cannot be interrupted.")+
    row2("Parallel Loop","Distributes loop iterations across multiple threads or processes."))+
    box("#2563EB","Performance and Limits",
    "Three tasks of 1 second each take 3 seconds sequentially but about 1 second in parallel. Speedup is bounded by Amdahl's Law.")),

    wrap("<h1 style='color:#1E3A6E'>Threads vs Processes</h1>"+
    tableWrap(thead3("Aspect","Thread","Process")+
    row3("Memory","Shares heap and static data","Own isolated address space")+
    row3("Communication","Direct shared variables","IPC via queues or pipes")+
    row3("Creation Cost","Lightweight and fast","Heavyweight")+
    row3("Crash Isolation","Can affect process","Usually isolated")+
    row3("Best For","I/O-bound tasks","CPU-bound tasks"))),

    wrap("<h1 style='color:#1E3A6E'>Java vs Python</h1>"+
    tableWrap(thead3("Aspect","Java","Python")+
    row3("Type Binding","Static, compile-time checked","Dynamic, runtime resolved")+
    row3("Parallel Model","Thread, ExecutorService, ForkJoinPool","threading / multiprocessing")+
    row3("True CPU Parallelism","Yes","Limited for threads due to the GIL")+
    row3("Synchronization","synchronized, volatile","threading.Lock, queue.Queue")+
    row3("Memory Model","Java Memory Model","GIL / separate processes"))+
    box("#3478DC","Key Takeaway",
    "Java provides true multi-threaded CPU parallelism. Python achieves CPU parallelism through multiprocessing."))
    };

    public static String show(){ return "=== THEORY MODULE ==="; }

    public static JPanel buildPanel(Runnable onBack){
        Color BG=new Color(245,247,250),SIDE=new Color(30,40,60),ACCENT=new Color(52,120,220);

        JPanel root=new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(8,8,8,8));

        JPanel header=new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new CompoundBorder(new LineBorder(new Color(52,120,220),1,true),new EmptyBorder(12,18,12,18)));
        JLabel htitle=new JLabel("=== THEORY MODULE ===",SwingConstants.CENTER);
        htitle.setFont(new Font("Arial",Font.BOLD,15));
        htitle.setForeground(Color.WHITE);
        header.add(htitle);
        root.add(header,BorderLayout.NORTH);

        JEditorPane area=new JEditorPane("text/html",CONTENT[0]);
        area.setEditable(false);
        area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,true);
        area.setBackground(BG);
        area.setBorder(new EmptyBorder(10,14,10,14));

        JScrollPane scroll=new JScrollPane(area);
        scroll.setBorder(new CompoundBorder(new LineBorder(new Color(220,225,235),1,true),new EmptyBorder(2,2,2,2)));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel sidebar=new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDE);
        sidebar.setPreferredSize(new Dimension(190,0));
        sidebar.setBorder(new CompoundBorder(new LineBorder(new Color(45,55,75),1,true),new EmptyBorder(8,6,8,6)));

        JButton[] btns=new JButton[TOPICS.length];
        for(int i=0;i<TOPICS.length;i++){
            final int idx=i;
            JButton b=new JButton(TOPICS[i]);
            b.setFont(new Font("Arial",Font.PLAIN,13));
            b.setForeground(new Color(170,190,220)); b.setBackground(SIDE);
            b.setBorderPainted(false); b.setFocusPainted(false);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setBorder(new EmptyBorder(10,16,10,16));
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addActionListener(e->{
                for(JButton x:btns){ x.setBackground(SIDE); x.setForeground(new Color(170,190,220)); x.setFont(new Font("Arial",Font.PLAIN,13)); }
                b.setBackground(ACCENT); b.setForeground(Color.WHITE); b.setFont(new Font("Arial",Font.BOLD,13));
                area.setText(CONTENT[idx]); area.setCaretPosition(0);
            });
            btns[i]=b; sidebar.add(b);
        }
        btns[0].setBackground(ACCENT); btns[0].setForeground(Color.WHITE); btns[0].setFont(new Font("Arial",Font.BOLD,13));

        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sidebar,scroll);
        split.setDividerSize(2); split.setDividerLocation(190); split.setEnabled(false); split.setBorder(null);
        root.add(split,BorderLayout.CENTER);

        JPanel footer=new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(BG); footer.setBorder(new EmptyBorder(6,12,6,12));
        JButton back=new JButton("Back to Menu");
        back.setFont(new Font("Arial",Font.PLAIN,13));
        back.setBackground(ACCENT); back.setForeground(Color.WHITE); back.setFocusPainted(false);
        back.setBorder(new CompoundBorder(new LineBorder(new Color(52,120,220),1,true),new EmptyBorder(8,16,8,16)));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e->{ if(onBack!=null) onBack.run(); });
        footer.add(back);
        root.add(footer,BorderLayout.SOUTH);
        return root;
    }
}