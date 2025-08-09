package com.ticketbooking;

import com.ticketbooking.view.MainFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.Color;
import java.io.File;

/**
 * Main class for the Ticket Booking System application
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Main method to start the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting Ticket Booking System");

        // Create logs directory if it doesn't exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }

        // Set look and feel to Nimbus for a modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    // Customize Nimbus colors for a more professional look
                    UIManager.put("nimbusBase", new Color(45, 125, 154));
                    UIManager.put("nimbusBlueGrey", new Color(200, 221, 242));
                    UIManager.put("control", new Color(240, 240, 240));
                    UIManager.put("text", new Color(30, 30, 30));
                    UIManager.put("nimbusSelectionBackground", new Color(57, 105, 138));
                    UIManager.put("Table.alternateRowColor", new Color(240, 248, 255));
                    logger.info("Set Nimbus look and feel with custom colors");
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                logger.info("Fallback to system look and feel");
            } catch (Exception ex) {
                logger.error("Error setting look and feel", ex);
            }
        }

        // Start application
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                logger.info("Application started successfully");
            } catch (Exception e) {
                logger.error("Error starting application", e);
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
