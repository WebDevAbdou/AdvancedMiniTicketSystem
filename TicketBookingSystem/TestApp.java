import javax.swing.*;
import java.awt.*;

public class TestApp {
    public static void main(String[] args) {
        // Create and show a simple window
        JFrame frame = new JFrame("Test Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JLabel label = new JLabel("Hello, World!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(label);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        System.out.println("Test application window should be visible now.");
    }
}
