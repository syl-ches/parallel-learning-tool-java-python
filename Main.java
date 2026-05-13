import modules_java.*;

import javax.swing.*;
import java.awt.*;

public class Main {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public Main() {
        frame = new JFrame("Parallelism in Java & Python");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create screens
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(TheoryModule.buildPanel(() -> cardLayout.show(mainPanel, "menu")), "theory");
        mainPanel.add(DecisionModule.buildPanel(() -> cardLayout.show(mainPanel, "menu")), "decision");
        mainPanel.add(DemoModule.run(cardLayout, mainPanel), "demo");
        mainPanel.add(
            SyntaxModule.buildPanel(
                () -> cardLayout.show(mainPanel, "menu")
            ),
            "syntax"
        );

        // Visualization gets its own live panel — NOT a plain string panel
        // so the simulation never auto-starts and results show inside the GUI
        mainPanel.add(createVisualizationScreen(), "visual");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // ===== MAIN MENU =====
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Parallelism in Java & Python", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(5, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        JButton theoryBtn   = new JButton("Theory");
        JButton decisionBtn = new JButton("Decision");
        JButton demoBtn     = new JButton("Execution / Demo");
        JButton visualBtn   = new JButton("Visualization");
        JButton syntaxBtn   = new JButton("Syntax");

        center.add(theoryBtn);
        center.add(decisionBtn);
        center.add(demoBtn);
        center.add(visualBtn);
        center.add(syntaxBtn);

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton exitBtn = new JButton("Exit");
        bottom.add(exitBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        theoryBtn.addActionListener(e   -> cardLayout.show(mainPanel, "theory"));
        decisionBtn.addActionListener(e -> cardLayout.show(mainPanel, "decision"));
        demoBtn.addActionListener(e     -> cardLayout.show(mainPanel, "demo"));
        visualBtn.addActionListener(e   -> cardLayout.show(mainPanel, "visual"));
        syntaxBtn.addActionListener(e   -> cardLayout.show(mainPanel, "syntax"));
        exitBtn.addActionListener(e     -> System.exit(0));

        return panel;
    }

    // ===== GENERIC MODULE SCREEN (String content) =====
    private JPanel createModulePanel(String titleText, String content) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton backBtn = new JButton("Back to Menu");
        bottom.add(backBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        return panel;
    }

    // ===== VISUALIZATION SCREEN (live panel, no auto-start) =====
    private JPanel createVisualizationScreen() {
        // Wrap the live visualization panel with a title and Back button
        JPanel wrapper = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Visualization Module", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        wrapper.add(title, BorderLayout.NORTH);

        // The actual live panel from VisualizationModule
        wrapper.add(VisualizationModule.createPanel(), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton backBtn = new JButton("Back to Menu");
        bottom.add(backBtn);
        wrapper.add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        return wrapper;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
