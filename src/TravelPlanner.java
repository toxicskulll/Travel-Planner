import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

public class TravelPlanner extends JFrame {
    private final String[] INTERESTS = {"Culture", "Food", "Shopping", "Nature", "Adventure"};
    private JTextField originField;
    private JTextField destinationField;
    private JTextField startDate;
    private JTextField endDate;
    private JSlider budget;
    private JPanel interestsPanel;
    private String currentUser;
    private Font poppinsFont;
    private List<NeuronNode> neurons;
    private Timer animationTimer;
    private Random random = new Random();
    
    // Neuron node class for background animation
    static class NeuronNode {
        double x, y;
        double dx, dy;
        List<NeuronNode> connections;
        double pulsePhase;
        double activeState;
        double targetActiveState;
        
        NeuronNode(double x, double y) {
            this.x = x;
            this.y = y;
            this.dx = new Random().nextDouble() * 1.2 - 0.6;
            this.dy = new Random().nextDouble() * 1.2 - 0.6;
            this.connections = new ArrayList<>();
            this.pulsePhase = new Random().nextDouble() * Math.PI * 2;
            this.activeState = new Random().nextBoolean() ? 1.0 : 0.0;
            this.targetActiveState = this.activeState;
        }

        void update() {
            x += dx * 0.3;
            y += dy * 0.3;

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

            dx += (new Random().nextDouble() - 0.5) * 0.1;
            dy += (new Random().nextDouble() - 0.5) * 0.1;

            double speed = Math.sqrt(dx * dx + dy * dy);
            if (speed > 1.5) {
                dx = (dx / speed) * 1.5;
                dy = (dy / speed) * 1.5;
            }

            pulsePhase += 0.03;
            if (pulsePhase > Math.PI * 2) {
                pulsePhase = 0;
                targetActiveState = new Random().nextDouble() < 0.2 ? 1.0 : 0.0;
            }

            double diff = targetActiveState - activeState;
            activeState += diff * 0.1;
        }
    }
    
    public TravelPlanner(String username) {
        // Initialize neurons
        neurons = new ArrayList<>();
        int numNeurons = 50;
        for (int i = 0; i < numNeurons; i++) {
            neurons.add(new NeuronNode(random.nextDouble() * 1280, random.nextDouble() * 800));
        }

        // Connect nearby neurons
        for (NeuronNode n1 : neurons) {
            for (NeuronNode n2 : neurons) {
                if (n1 != n2) {
                    double dist = Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
                    if (dist < 200) {
                        n1.connections.add(n2);
                    }
                }
            }
        }

        // Start animation timer
        animationTimer = new Timer(30, e -> {
            for (NeuronNode neuron : neurons) {
                neuron.update();
            }
            repaint();
        });
        animationTimer.start();

        // Font initialization and rest of the constructor code...
        try {
            File fontFile = new File("fonts/Poppins-Regular.ttf");
            if (!fontFile.exists()) {
                System.err.println("Font file not found: " + fontFile.getAbsolutePath());
            } else {
                poppinsFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(poppinsFont);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading Poppins font: " + e.getMessage());
            poppinsFont = new Font("Arial", Font.PLAIN, 12);
        }

        this.currentUser = username;
        setTitle("\tTravel Planner");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel with neural network background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
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
                                double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;
                                int alpha = (int)(pulse * 150 * neuron.activeState * connection.activeState);
                                
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

                // Draw neurons
                for (NeuronNode neuron : neurons) {
                    double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;
                    
                    if (neuron.activeState > 0.1) {
                        for (int i = 6; i > 0; i--) {
                            int alpha = (int)(pulse * 50 * neuron.activeState);
                            g2d.setColor(new Color(0, 255, 255, alpha));
                            int size = (int)(i * 4 * neuron.activeState);
                            g2d.fillOval((int)neuron.x - size/2, (int)neuron.y - size/2, size, size);
                        }
                        
                        int alpha = (int)(200 * neuron.activeState);
                        g2d.setColor(new Color(0, 255, 255, alpha));
                        g2d.fillOval((int)neuron.x - 3, (int)neuron.y - 3, 6, 6);
                    }
                    
                    int baseAlpha = (int)(100 * (1 - neuron.activeState) + 150 * neuron.activeState);
                    g2d.setColor(new Color(0, 100 + (int)(155 * neuron.activeState), 
                        150 + (int)(105 * neuron.activeState), baseAlpha));
                    g2d.fillOval((int)neuron.x - 2, (int)neuron.y - 2, 4, 4);
                }
            }
        };

       // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(450, 60));
        JLabel titleLabel = new JLabel("\t  TRAVEL PLANNER");
        titleLabel.setFont(poppinsFont.deriveFont(Font.BOLD, 24f));
        titleLabel.setForeground(Color.CYAN);
        headerPanel.add(titleLabel);

        // Profile Button
        JButton profileButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 100, 200));
                } else {
                    g2d.setColor(new Color(0, 50, 100));
                }
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2d.setColor(Color.CYAN);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
                g2d.fillArc(centerX - 12, centerY + 4, 24, 24, 0, 180);
                
                g2d.dispose();
            }
        };
        
        profileButton.setPreferredSize(new Dimension(40, 40));
        profileButton.setBorderPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileButton.addActionListener(e -> showProfile());

        profileButton.setToolTipText("View Profile");

        profileButton.setVisible(true);
        profileButton.setEnabled(true);

        System.out.println("Profile button initialized. Is visible: " + 
        profileButton.isVisible() + ", Is enabled: " + profileButton.isEnabled());
        
        profileButton.addActionListener(e -> {
            System.out.println("Opening profile for user: " + currentUser); // Debug line
            showProfile();
        });

        // Add to header with right alignment
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(profileButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Initialize components with custom style
        originField = new CustomTextField();
        destinationField = new CustomTextField();
        startDate = new CustomTextField();
        endDate = new CustomTextField();
        budget = createStyledSlider();
        interestsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        interestsPanel.setOpaque(false);

        // Add form fields
        addFormField(formPanel, "ORIGIN:", originField, gbc, 0);
        addFormField(formPanel, "DESTINATION:", destinationField, gbc, 1);
        addFormField(formPanel, "START DATE:", startDate, gbc, 2);
        addFormField(formPanel, "END DATE:", endDate, gbc, 3);

        // Budget Slider
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createStyledLabel("BUDGET ($):"), gbc);
        gbc.gridx = 1;
        formPanel.add(budget, gbc);

        // Interests
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createStyledLabel("INTERESTS:"), gbc);
        for (String interest : INTERESTS) {
            JCheckBox cb = new JCheckBox(interest);
            styleCheckBox(cb);
            interestsPanel.add(cb);
        }
        gbc.gridx = 1;
        formPanel.add(interestsPanel, gbc);

        // Generate Button
        JButton generateButton = new JButton("GENERATE ITINERARY") {
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
        styleButton(generateButton);
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(generateButton, gbc);

        generateButton.addActionListener(e -> {
            if (originField.getText().trim().isEmpty() || destinationField.getText().trim().isEmpty() || 
                startDate.getText().trim().isEmpty() || 
                endDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill all required fields", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String start = startDate.getText().trim();
            String end = endDate.getText().trim();
            int budgetValue = budget.getValue();
            
            List<String> selectedInterests = new ArrayList<>();
            for (Component c : interestsPanel.getComponents()) {
                if (c instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) c;
                    if (cb.isSelected()) {
                        selectedInterests.add(cb.getText());
                    }
                }
            }

            String prompt = String.format(
                "Create a travel itinerary from %s place to %s place from %s to %s with a budget of $%d. " +
                "Interests include: %s", 
                origin, destination, start, end, budgetValue, 
                String.join(", ", selectedInterests)
            );

            JDialog loadingDialog = new JDialog(this, "Processing", true);
            JLabel loadingLabel = new JLabel("Generating itinerary...");
            loadingLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
            loadingLabel.setForeground(new Color(173, 181, 189));
            loadingDialog.add(loadingLabel);
            loadingDialog.pack();
            loadingDialog.setLocationRelativeTo(this);

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return PythonConnector.getAIResponse(prompt);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        String itinerary = get();
                        
                        // Create custom styled HTML content
                        String styledContent = String.format("""
                            <html>
                            <body style='width: 500px; font-family: Consolas; background-color: rgb(0,30,60); color: rgb(0,255,255);'>
                                <div style='padding: 20px;'>
                                    %s
                                </div>
                            </body>
                            </html>""",
                            itinerary.replace("**", "</span>")
                                    .replaceFirst("TRAVEL ITINERARY FOR", 
                                        "<span style='font-size: 28px; color: rgb(0,255,255); display: block; text-align: center; margin-bottom: 30px;'>TRAVEL ITINERARY FOR")
                                    .replace("Day", "<span style='font-size: 22px; color: rgb(0,200,255); display: block; margin-top: 25px; margin-bottom: 15px;'>Day")
                                    .replace("Duration:", "<span style='font-size: 18px; color: rgb(0,255,200); display: block; margin-top: 15px;'>⏱ Duration:")
                                    .replace("Budget:", "<span style='font-size: 18px; color: rgb(0,255,200); display: block; margin-top: 10px;'>💰 Budget:")
                                    .replace("Recommended Hotels:", "<span style='font-size: 20px; color: rgb(0,200,255); display: block; margin-top: 30px;'>🏨 Recommended Hotels:")
                                    .replace("Tips:", "<span style='font-size: 20px; color: rgb(0,200,255); display: block; margin-top: 30px;'>💡 Tips:")
                                    .replace("* Morning:", "<div style='margin: 25px 0 15px 0;'><span style='font-size: 20px; color: rgb(255,200,0); font-weight: bold;'>🌅 Morning:</span></div>")
                                    .replace("* Afternoon:", "<div style='margin: 25px 0 15px 0;'><span style='font-size: 20px; color: rgb(255,200,0); font-weight: bold;'>☀️ Afternoon:</span></div>")
                                    .replace("* Evening:", "<div style='margin: 25px 0 15px 0;'><span style='font-size: 20px; color: rgb(255,200,0); font-weight: bold;'>🌙 Evening:</span></div>")
                                    .replaceAll("\\n\\* ([^\\n]+)", "<div style='margin: 12px 0 12px 20px; font-size: 15px;'>• $1</div>")
                                    .replace("\n", "<br>"));
                
                        JEditorPane editorPane = new JEditorPane();
                        editorPane.setContentType("text/html");
                        editorPane.setText(styledContent);
                        editorPane.setEditable(false);
                        editorPane.setBackground(new Color(0, 30, 60));
                
                        JScrollPane scrollPane = new JScrollPane(editorPane);
                        scrollPane.setPreferredSize(new Dimension(550, 500));
                        scrollPane.setBorder(BorderFactory.createEmptyBorder());
                
                        // Custom dialog
                        JDialog resultDialog = new JDialog(TravelPlanner.this, "Your AI Generated Itinerary", true);
                        resultDialog.setLayout(new BorderLayout());
                        resultDialog.add(scrollPane, BorderLayout.CENTER);
                        
                        // Add close button at bottom
                        JButton closeButton = new JButton("CLOSE");
                        styleButton(closeButton);
                        closeButton.addActionListener(e -> resultDialog.dispose());
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        buttonPanel.setBackground(new Color(0, 30, 60));
                        buttonPanel.add(closeButton);
                        resultDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                        resultDialog.setSize(1280, 800);
                        resultDialog.setLocationRelativeTo(TravelPlanner.this);
                        resultDialog.setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(TravelPlanner.this,
                            "Error generating itinerary: " + ex.getMessage(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
            loadingDialog.setVisible(true);
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    // Custom text field with rounded corners
    static class CustomTextField extends JTextField {
        public CustomTextField() {
            setOpaque(false);
            setBorder(new RoundedCornerBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 30, 60));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 
                getHeight(), getHeight()));
            super.paintComponent(g);
        }
    }

    private JSlider createStyledSlider() {
        JSlider slider = new JSlider(500, 5000, 1000);
        slider.setOpaque(false);
        slider.setForeground(Color.CYAN);
        slider.setMajorTickSpacing(1000);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, 
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createStyledLabel(labelText), gbc);
        gbc.gridx = 1;
        styleTextField(field);
        panel.add(field, gbc);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(poppinsFont.deriveFont(Font.BOLD)); // Use Poppins Bold
        label.setForeground(Color.CYAN);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setForeground(Color.CYAN);
        field.setCaretColor(Color.CYAN);
        field.setFont(poppinsFont.deriveFont(14f)); // Use Poppins Regular, size 14
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(poppinsFont); // Use Poppins Regular, default size
        checkBox.setForeground(Color.CYAN);
        checkBox.setBackground(new Color(0, 30, 60));
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setOpaque(true);
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(200, 35));
        button.setForeground(Color.CYAN);
        button.setFont(poppinsFont.deriveFont(Font.BOLD, 14f)); // Use Poppins Bold, size 14
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
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

    private void showProfile() {
        try {
            System.out.println("Opening profile for user: " + currentUser); // Debug line
            ProfileDialog dialog = new ProfileDialog(this, currentUser);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening profile: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }
}