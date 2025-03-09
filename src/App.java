import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.io.File;
import java.io.IOException;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    private static JFrame loginFrame;
    private static Font poppinsFont;
    private static List<NeuronNode> neurons;
    private static Random random = new Random();

    // Neuron node class to represent each point in the network
    static class NeuronNode {
        double x, y;
        double dx, dy;
        List<NeuronNode> connections;
        double pulsePhase;
        double activeState; // Using double for smooth transition
        double targetActiveState;
        
        NeuronNode(double x, double y) {
            this.x = x;
            this.y = y;
            this.dx = random.nextDouble() * 1.2 - 0.6; // Reduced speed range
            this.dy = random.nextDouble() * 1.2 - 0.6;
            this.connections = new ArrayList<>();
            this.pulsePhase = random.nextDouble() * Math.PI * 2;
            this.activeState = random.nextBoolean() ? 1.0 : 0.0;
            this.targetActiveState = this.activeState;
        }

        void update() {
            // Smooth movement with damping
            x += dx * 0.3; // Slower movement
            y += dy * 0.3;

            // Smooth bounce off edges with gradual speed change
            if (x < 50) {
                dx = Math.abs(dx) * 0.8;
                x = 50;
            } else if (x > 1230) {
                dx = -Math.abs(dx) * 0.8;
                x = 1230;
            }
            if (y < 50) {
                dy = Math.abs(dy) * 0.8;
                y = 50;
            } else if (y > 750) {
                dy = -Math.abs(dy) * 0.8;
                y = 750;
            }

            // Add slight randomness to movement
            dx += (random.nextDouble() - 0.5) * 0.1;
            dy += (random.nextDouble() - 0.5) * 0.1;

            // Limit maximum speed
            double speed = Math.sqrt(dx * dx + dy * dy);
            if (speed > 1.5) {
                dx = (dx / speed) * 1.5;
                dy = (dy / speed) * 1.5;
            }

            // Smooth pulse phase update
            pulsePhase += 0.03; // Slower pulse
            if (pulsePhase > Math.PI * 2) {
                pulsePhase = 0;
                targetActiveState = random.nextDouble() < 0.2 ? 1.0 : 0.0; // 20% chance to change state
            }

            // Smooth transition between active states
            double diff = targetActiveState - activeState;
            activeState += diff * 0.1; // Gradual transition
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load Poppins font
        try {
            File fontFile = new File("fonts/Poppins-Regular.ttf"); // Or Poppins-Bold.ttf, etc.
            if (!fontFile.exists()) {
                System.err.println("Font file not found: " + fontFile.getAbsolutePath());
            } else {
                poppinsFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(poppinsFont);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading Poppins font: " + e.getMessage());
            poppinsFont = new Font("Arial", Font.PLAIN, 12); // Fallback font
        }

        loginFrame = new JFrame("System Access Portal");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(1280, 800);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        // Main panel with geeky background
        /* JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 50), 0, getHeight(), new Color(0, 30, 60));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Tech circles
                g2d.setColor(new Color(0, 100, 255, 50));
                for (int i = 0; i < 5; i++) {
                    int size = 40 + i * 20;
                    g2d.drawOval(getWidth()/2 - size/2, getHeight()/2 - size/2, size, size);
                }

                // Data streams
                g2d.setColor(new Color(0, 255, 0, 100));
                for (int i = 0; i < 20; i++) {
                    int x1 = (int)(Math.random() * getWidth());
                    int y1 = (int)(Math.random() * getHeight());
                    int x2 = (int)(Math.random() * getWidth());
                    int y2 = (int)(Math.random() * getHeight());
                    g2d.drawLine(x1, y1, x2, y2);
                }
            } */
            // Initialize neurons
        neurons = new ArrayList<>();
        int numNeurons = 50; // Increased number of neurons
        for (int i = 0; i < numNeurons; i++) {
            neurons.add(new NeuronNode(random.nextDouble() * 1280, random.nextDouble() * 800));
        }

        // Connect nearby neurons
        for (NeuronNode n1 : neurons) {
            for (NeuronNode n2 : neurons) {
                if (n1 != n2) {
                    double dist = Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
                    if (dist < 200) { // Maximum connection distance
                        n1.connections.add(n2);
                    }
                }
            }
        }

        // Main panel with neuron network background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark background gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 30),
                        0, getHeight(), new Color(0, 20, 40));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw neural connections
                for (NeuronNode neuron : neurons) {
                    if (neuron.activeState > 0.1) {
                        for (NeuronNode connection : neuron.connections) {
                            if (connection.activeState > 0.1) {
                                // Calculate pulse effect with smooth transition
                                double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;
                                int alpha = (int)(pulse * 150 * neuron.activeState * connection.activeState); // Smooth transparency
                                
                                // Draw connection line with gradient
                                GradientPaint connectionGradient = new GradientPaint(
                                    (float)neuron.x, (float)neuron.y, new Color(0, 150, 255, alpha),
                                    (float)connection.x, (float)connection.y, new Color(0, 255, 255, alpha)
                                );
                                g2d.setPaint(connectionGradient);
                                g2d.setStroke(new BasicStroke(1.5f));
                                g2d.draw(new Line2D.Double(neuron.x, neuron.y, connection.x, connection.y));
                            }
                        }
                    }
                }

                // Draw neurons with smooth transitions
                for (NeuronNode neuron : neurons) {
                    // Interpolated glowing effect
                    double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;
                    
                    if (neuron.activeState > 0.1) {
                        // Smooth glowing effect based on activeState
                        for (int i = 6; i > 0; i--) {
                            int alpha = (int)(pulse * 50 * neuron.activeState);
                            g2d.setColor(new Color(0, 255, 255, alpha));
                            int size = (int)(i * 4 * neuron.activeState);
                            g2d.fillOval((int)neuron.x - size/2, (int)neuron.y - size/2, size, size);
                        }
                        // Core of the neuron with smooth transition
                        int alpha = (int)(200 * neuron.activeState);
                        g2d.setColor(new Color(0, 255, 255, alpha));
                        g2d.fillOval((int)neuron.x - 3, (int)neuron.y - 3, 6, 6);
                    }
                    
                    // Base neuron (always visible)
                    int baseAlpha = (int)(100 * (1 - neuron.activeState) + 150 * neuron.activeState);
                    g2d.setColor(new Color(0, 100 + (int)(155 * neuron.activeState), 150 + (int)(105 * neuron.activeState), baseAlpha));
                    g2d.fillOval((int)neuron.x - 2, (int)neuron.y - 2, 4, 4);
                }
            }
        };


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setForeground(Color.CYAN);
        userLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        JTextField userField = new CustomTextField();
        styleTextField(userField);

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setForeground(Color.CYAN);
        passLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        JPasswordField passField = new JPasswordField(20);
        styleTextField(passField);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        // Login Button
        JButton loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(new Color(0, 100, 200));
                } else {
                    g.setColor(new Color(0, 50, 100));
                }
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        styleButton(loginButton);

        // Signup Button
        JButton signupButton = new JButton("SIGN UP") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(new Color(0, 100, 200));
                } else {
                    g.setColor(new Color(0, 50, 100));
                }
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
            }
        };
        styleButton(signupButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        // Action Listeners
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
        
            if (DatabaseManager.validateUser(username, password)) {
                SwingUtilities.invokeLater(() -> {
                    TravelPlanner travelPlanner = new TravelPlanner(username);
                    travelPlanner.setVisible(true);
                    loginFrame.dispose();  // Close login window after new window is visible
                });
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Access Denied", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            SignUpDialog dialog = new SignUpDialog(loginFrame);
            dialog.setLocationRelativeTo(loginFrame);  // Center dialog relative to login window
            dialog.setModal(true);  // Make dialog modal so it must be closed before continuing
            dialog.setVisible(true);
        });

        loginFrame.add(mainPanel);
        loginFrame.setVisible(true);
        // Initialize animation timer
        // Update animation timer
        Timer animationTimer = new Timer(30, e -> {
            // Update all neurons
            for (NeuronNode neuron : neurons) {
                neuron.update();
            }
            mainPanel.repaint();
        });
        animationTimer.start();
    }

    private static void updateBackground(JPanel mainPanel) {
        mainPanel.repaint();  // Trigger repaint to redraw the background
    }

    // Custom text field with rounded corners
    static class CustomTextField extends JTextField {
        public CustomTextField() {
            setOpaque(false);
            setBorder(new RoundedCornerBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 30, 60));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 
                getHeight(), getHeight()));
            super.paintComponent(g);
            // Dynamic Data Streams
            /* for (int i = 0; i < 20; i++) {
                int x1 = (int)(Math.random() * getWidth());
                int y1 = (int)(Math.random() * getHeight());
                // Modify x2 and y2 to create animation
                int x2 = (int)(x1 + Math.random() * 10 - 5); // Random change in x
                int y2 = (int)(y1 + Math.random() * 10 - 5); // Random change in y
                g2d.drawLine(x1, y1, x2, y2);
            } */
        }
    }

    private static Font usePoppinsFont(float size, int style) {
        if (poppinsFont != null) {
            return poppinsFont.deriveFont(style, size);
        } else {
            return new Font("Arial", style, (int)size); // Fallback
        }
    }

    private static void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setForeground(Color.CYAN);
        field.setCaretColor(Color.CYAN);
        field.setFont(usePoppinsFont(14, Font.PLAIN)); // Use Poppins
        if (!(field instanceof CustomTextField)) {
            field.setOpaque(false);
            field.setBorder(new RoundedCornerBorder());
        }
    }

    private static void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setForeground(Color.CYAN);
        button.setFont(usePoppinsFont(14, Font.BOLD)); // Use Poppins
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
}