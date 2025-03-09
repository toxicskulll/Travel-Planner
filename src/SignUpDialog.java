import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;

public class SignUpDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean signupSuccessful = false;

   private static final Color BACKGROUND_COLOR = new Color(0, 20, 40); // Dark blue
    private static final Color TEXT_COLOR = new Color(0, 255, 255); // Cyan
    private static final Color BUTTON_COLOR = new Color(0, 50, 100);
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 100, 200);

    public SignUpDialog(JFrame parent) {
        super(parent, "Sign Up", true);
        setupUI();
    }

    private void setupUI() {
        // Main panel with dark background and subtle gradient
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Subtle gradient
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR.darker(), 0, getHeight(), BACKGROUND_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
         gbc.gridx = 0;
        gbc.gridy++;
        JLabel userLabel = createStyledLabel("USERNAME:");
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new CustomTextField();
        styleTextField(usernameField);
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = createStyledLabel("PASSWORD:");
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new CustomPasswordField();
        styleTextField(passwordField);
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel confirmLabel = createStyledLabel("CONFIRM:");
        mainPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new CustomPasswordField();
        styleTextField(confirmPasswordField);
        mainPanel.add(confirmPasswordField, gbc);


        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)); // Added vertical gap
        buttonPanel.setOpaque(false);

        JButton signupButton = new CustomButton("SIGN UP");
        JButton cancelButton = new CustomButton("CANCEL");

        signupButton.addActionListener(e -> handleSignup());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(getParent());
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DatabaseManager.addUser(username, password)) {
            signupSuccessful = true;
            JOptionPane.showMessageDialog(this, 
                "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Consolas", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBackground(BACKGROUND_COLOR.darker()); // Darker background for text fields
    }

    // Custom text field with rounded corners
    static class CustomTextField extends JTextField {
        public CustomTextField() {
            setOpaque(false);
            setBorder(new RoundedCornerBorder());
            setForeground(TEXT_COLOR);
            setCaretColor(TEXT_COLOR);
            setBackground(BACKGROUND_COLOR.darker());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BACKGROUND_COLOR.darker()); // Consistent background
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            super.paintComponent(g);
        }
    }

    // Custom password field with rounded corners
    static class CustomPasswordField extends JPasswordField {
        public CustomPasswordField() {
            setOpaque(false);
            setBorder(new RoundedCornerBorder());
            setForeground(TEXT_COLOR);
            setCaretColor(TEXT_COLOR);
            setBackground(BACKGROUND_COLOR.darker());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BACKGROUND_COLOR.darker()); // Consistent background
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            super.paintComponent(g);
        }
    }

    // Custom button with glowing effect
    static class CustomButton extends JButton {
        public CustomButton(String text) {
            super(text);
            setForeground(TEXT_COLOR);
            setFont(new Font("Consolas", Font.BOLD, 14));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 35));
            setBackground(BUTTON_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2.setColor(BUTTON_HOVER_COLOR);
            } else {
                g2.setColor(BUTTON_COLOR);
            }
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            super.paintComponent(g);
        }
    }

    static class RoundedCornerBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.CYAN);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, height, height));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }

    public boolean isSignupSuccessful() {
        return signupSuccessful;
    }
}