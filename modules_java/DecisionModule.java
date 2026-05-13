package modules_java;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DecisionModule {

    static class Question {
        String category;
        String text;
        int weight;
        JCheckBox box;

        Question(String category, String text, int weight) {
            this.category = category;
            this.text = text;
            this.weight = weight;
            this.box = new JCheckBox(text);
        }
    }

    private static final List<Question> QUESTIONS = new ArrayList<>();

    static {

        QUESTIONS.add(new Question(
            "Problem Decomposition",
            "Can the problem be divided into sub-tasks?",
            2
        ));

        QUESTIONS.add(new Question(
            "Problem Decomposition",
            "Are there repeated operations on large data?",
            2
        ));

        QUESTIONS.add(new Question(
            "Task Independence",
            "Can tasks execute independently?",
            3
        ));

        QUESTIONS.add(new Question(
            "Task Independence",
            "Do tasks avoid frequent communication?",
            2
        ));

        QUESTIONS.add(new Question(
            "Synchronization",
            "Do tasks require minimal synchronization?",
            2
        ));

        QUESTIONS.add(new Question(
            "Granularity",
            "Are tasks large enough to justify overhead?",
            2
        ));

        QUESTIONS.add(new Question(
            "Resources",
            "Does the system have multiple CPU cores?",
            1
        ));

        QUESTIONS.add(new Question(
            "Performance",
            "Will parallel execution significantly reduce runtime?",
            3
        ));
    }

    public static JPanel buildPanel(Runnable onBack) {

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== HEADER =====

        JLabel title = new JLabel("Decision Module");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel subtitle = new JLabel(
            "Evaluate whether parallel programming is appropriate."
        );

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        // ===== MAIN AREA =====

        JPanel main = new JPanel(new GridLayout(1, 2, 12, 0));

        // LEFT SIDE
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        String currentCategory = "";

        for (Question q : QUESTIONS) {

            if (!q.category.equals(currentCategory)) {

                JLabel section = new JLabel(q.category);

                section.setFont(new Font(
                    "Arial",
                    Font.BOLD,
                    13
                ));

                section.setBorder(
                    new EmptyBorder(10, 0, 4, 0)
                );

                left.add(section);

                currentCategory = q.category;
            }

            q.box.setFocusPainted(false);

            left.add(q.box);
        }

        JScrollPane leftScroll = new JScrollPane(left);

        // RIGHT SIDE

        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane rightScroll = new JScrollPane(output);

        main.add(leftScroll);
        main.add(rightScroll);

        root.add(main, BorderLayout.CENTER);

        // ===== BUTTONS =====

        JPanel bottom = new JPanel();

        JButton analyze = new JButton("Analyze");
        JButton clear = new JButton("Clear");
        JButton back = new JButton("Back to Menu");

        bottom.add(analyze);
        bottom.add(clear);
        bottom.add(back);

        root.add(bottom, BorderLayout.SOUTH);

        // ===== ANALYZE =====

        analyze.addActionListener(e -> {

            int score = 0;
            int max = 0;

            for (Question q : QUESTIONS) {

                max += q.weight;

                if (q.box.isSelected()) {
                    score += q.weight;
                }
            }

            double ratio = (double) score / max;

            String level;
            String explanation;

            if (ratio >= 0.75) {

                level = "STRONGLY RECOMMENDED";

                explanation =
                    "The workload appears highly suitable for " +
                    "parallel execution.\n\n" +

                    "The problem is divisible into independent " +
                    "tasks with relatively low synchronization " +
                    "and communication overhead.\n\n" +

                    "Parallel programming may provide significant " +
                    "performance improvement.";

            }
            else if (ratio >= 0.45) {

                level = "POSSIBLY BENEFICIAL";

                explanation =
                    "Parallel programming may improve performance, " +
                    "but overhead costs should be considered.\n\n" +

                    "Synchronization, communication, or task size " +
                    "may reduce achievable speedup.";

            }
            else {

                level = "NOT RECOMMENDED";

                explanation =
                    "Sequential execution is likely more efficient.\n\n" +

                    "The workload may contain excessive dependency, " +
                    "communication overhead, or insufficient task " +
                    "size to justify parallel execution.";
            }

            String text =
                "PARALLELISM ANALYSIS\n" +
                "────────────────────────────\n" +
                "Score: " + score + "/" + max + "\n\n" +

                "Recommendation:\n" +
                level + "\n\n" +

                explanation +

                "\n\nTHEORY CONNECTIONS\n" +
                "────────────────────────────\n" +
                "• Task decomposition\n" +
                "• Synchronization overhead\n" +
                "• Task granularity\n" +
                "• Data dependency\n" +
                "• Parallel overhead\n" +
                "• Resource utilization\n";

            output.setText(text);
        });

        // ===== CLEAR =====

        clear.addActionListener(e -> {

            for (Question q : QUESTIONS) {
                q.box.setSelected(false);
            }

            output.setText("");
        });

        // ===== BACK =====

        back.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        return root;
    }
}
