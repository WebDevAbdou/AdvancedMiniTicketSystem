import javax.swing.*;
import java.awt.*;

public class TestSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Swing Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            
            JLabel label = new JLabel("If you can see this, Swing is working correctly!");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(label);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("Test window should be visible now");
        });
    }
}
