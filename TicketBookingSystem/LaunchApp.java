import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LaunchApp {
    public static void main(String[] args) {
        // Create and show a simple window
        JFrame frame = new JFrame("Ticket Booking System Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Ticket Booking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        JLabel statusLabel = new JLabel("Application Status: Not Running");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);
        
        JButton launchButton = new JButton("Launch Application");
        launchButton.addActionListener(e -> {
            statusLabel.setText("Application Status: Launching...");
            
            // Launch the application in a separate thread
            new Thread(() -> {
                try {
                    // Check if the build directory exists
                    File buildDir = new File("build");
                    if (!buildDir.exists() || !buildDir.isDirectory()) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Application Status: Error - Build directory not found");
                        });
                        return;
                    }
                    
                    // Check if the Main class exists
                    File mainClass = new File("build/com/ticketbooking/Main.class");
                    if (!mainClass.exists()) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Application Status: Error - Main class not found");
                        });
                        return;
                    }
                    
                    // Launch the application
                    ProcessBuilder pb = new ProcessBuilder("java", "-cp", "build:lib/*", "com.ticketbooking.Main");
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Application Status: Running");
                        launchButton.setEnabled(false);
                    });
                    
                    // Wait for the process to complete
                    int exitCode = process.waitFor();
                    
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Application Status: Exited with code " + exitCode);
                        launchButton.setEnabled(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Application Status: Error - " + ex.getMessage());
                        launchButton.setEnabled(true);
                    });
                }
            }).start();
        });
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(launchButton, gbc);
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(exitButton, gbc);
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        System.out.println("Launcher window is now visible.");
    }
}
