package modules_java;

public class DecisionModule {

    public static String run() {

        StringBuilder sb = new StringBuilder();

        sb.append("=== DECISION MODULE ===\n\n");

        sb.append("This module helps determine whether parallel programming\n");
        sb.append("is appropriate for a computing problem.\n\n");

        sb.append("Guide questions:\n\n");

        // Problem Decomposition
        sb.append("[1] Problem Decomposition\n");
        sb.append("- Can the problem be divided into smaller sub-tasks?\n");
        sb.append("- Are there repeated operations on large data?\n");
        sb.append("- Will decomposition improve performance?\n\n");

        // Task Independence
        sb.append("[2] Task Independence\n");
        sb.append("- Can each task execute independently?\n");
        sb.append("- Do tasks frequently depend on each other?\n");
        sb.append("- Can communication between tasks be minimized?\n\n");

        // Data Dependency
        sb.append("[3] Data Dependency and Communication\n");
        sb.append("- Is shared data necessary during execution?\n");
        sb.append("- Will tasks need frequent synchronization?\n");
        sb.append("- Can communication delays be reduced?\n\n");

        // Synchronization
        sb.append("[4] Synchronization Requirements\n");
        sb.append("- Do tasks need to wait for one another?\n");
        sb.append("- Are there synchronization points?\n");
        sb.append("- Will one delayed task affect the others?\n\n");

        // Granularity
        sb.append("[5] Task Granularity\n");
        sb.append("- Are tasks large enough for parallel execution?\n");
        sb.append("- Is computation time larger than coordination time?\n");
        sb.append("- Will thread management overhead be acceptable?\n\n");

        // Resources
        sb.append("[6] Resource Availability\n");
        sb.append("- Does the system support multiple cores/threads?\n");
        sb.append("- Is memory sufficient for concurrent execution?\n");
        sb.append("- Will tasks compete heavily for resources?\n\n");

        // Performance
        sb.append("[7] Expected Performance Gain\n");
        sb.append("- Will runtime improve significantly?\n");
        sb.append("- Is the parallel portion of the program large enough?\n");
        sb.append("- Is the added complexity justified?\n\n");

        sb.append("=== INTERPRETATION GUIDE ===\n\n");
        sb.append("If most answers suggest:\n");
        sb.append("- independent tasks,\n");
        sb.append("- minimal synchronization,\n");
        sb.append("- large workloads,\n");
        sb.append("- and sufficient hardware resources,\n\n");
        sb.append("then parallel programming is likely appropriate.\n\n");

        sb.append("If most answers suggest:\n");
        sb.append("- strong task dependency,\n");
        sb.append("- frequent communication,\n");
        sb.append("- small workloads,\n");
        sb.append("- or excessive coordination overhead,\n\n");
        sb.append("then sequential programming may be more suitable.\n\n");

        sb.append("=== THEORY CONNECTIONS ===\n");
        sb.append("- Shared data relates to variable scope and lifetime.\n");
        sb.append("- Synchronization affects control structures.\n");
        sb.append("- Task decomposition relates to abstraction.\n");
        sb.append("- Dependencies affect execution semantics.\n");
        sb.append("- Granularity affects runtime efficiency.\n");

        return sb.toString();
    }
}
