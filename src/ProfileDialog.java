import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileDialog extends JDialog {

    private static Font poppinsFont;
    private static List<NeuronNode> neurons;
    private static Random random = new Random();
    private static final Color BACKGROUND_COLOR = new Color(0, 20, 40);
    private static final Color NEURON_COLOR = new Color(0, 100, 150);
    private static final Color CONNECTION_COLOR = new Color(0, 150, 255);
    private static final Color TEXT_COLOR = new Color(0, 255, 255);
    private final String username;
    private JTextField nameField;
    private JComboBox<String> genderBox;
    private JTextField ageField;
    private JTextField emailField;
    private JTextField instagramField;
    private JTextField linkedinField;
    private JProgressBar completionBar;
    private boolean editMode = false;

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

    public ProfileDialog(JFrame parent, String username) {
        super(parent, "User Profile", true);
        this.username = username;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loadPoppinsFont(); // Load font
        initializeComponents();
        setupUI();
        loadProfileData();

        neurons = new ArrayList<>();
        int numNeurons = 50;
        for (int i = 0; i < numNeurons; i++) {
            neurons.add(new NeuronNode(random.nextDouble() * 450, random.nextDouble() * 700)); // Adjusted size
        }

        for (NeuronNode n1 : neurons) {
            for (NeuronNode n2 : neurons) {
                if (n1 != n2) {
                    double dist = Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
                    if (dist < 150) {
                        n1.connections.add(n2);
                    }
                }
            }
        }
    }

    private void initializeComponents() {
        // Initialize all components first
        nameField = new CustomTextField();
        genderBox = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
        styleComboBox(genderBox);  
        genderBox.setBackground(new Color(0, 30, 60));
        genderBox.setForeground(Color.CYAN);
        ageField = new CustomTextField();
        emailField = new CustomTextField();
        instagramField = new CustomTextField();
        linkedinField = new CustomTextField();
        completionBar = new JProgressBar(0, 100);
    }

    private void setupProgressBar(GridBagConstraints topGbc, JPanel topPanel) {
        // Progress Bar Panel with padding
        JPanel progressPanel = new JPanel(new BorderLayout(10, 5));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        progressPanel.setOpaque(false);
    
        // Label for completion text
        JLabel completionLabel = new JLabel("Profile Completion");
        completionLabel.setForeground(Color.CYAN);
        completionLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        progressPanel.add(completionLabel, BorderLayout.NORTH);
    
        // Custom Progress Bar
        completionBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
                // Background
                g2.setColor(new Color(0, 30, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
    
                // Progress
                int width = (int) (getWidth() * (getValue() / 100.0));
                g2.setColor(new Color(0, 200, 255));
                g2.fillRoundRect(0, 0, width, getHeight(), 20, 20);
    
                // Border
                g2.setColor(Color.CYAN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
    
                // Text
                String text = getValue() + "%";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2.setColor(Color.WHITE);
                g2.drawString(text, 
                    (getWidth() - textWidth) / 2,
                    (getHeight() + textHeight) / 2 - 2);
            }
        };
        
        completionBar.setPreferredSize(new Dimension(0, 35));
        completionBar.setStringPainted(false);
        completionBar.setBorderPainted(false);
        completionBar.setOpaque(false);
        
        progressPanel.add(completionBar, BorderLayout.CENTER);
        
        // Add to top panel
        topGbc.gridy = 2;
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(10, 5, 10, 5);
        topPanel.add(progressPanel, topGbc);
    }
    
    private void loadPoppinsFont() {
        try {
            File fontFile = new File("fonts/Poppins-Regular.ttf");
            if (fontFile.exists()) {
                poppinsFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(poppinsFont);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading Poppins font: " + e.getMessage());
            poppinsFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                for (NeuronNode neuron : neurons) {
                    if (neuron.activeState > 0.1) {
                        for (NeuronNode connection : neuron.connections) {
                            if (connection.activeState > 0.1) {
                                double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;
                                int alpha = (int)(pulse * 150 * neuron.activeState * connection.activeState);

                                g2d.setColor(new Color(CONNECTION_COLOR.getRed(), CONNECTION_COLOR.getGreen(), CONNECTION_COLOR.getBlue(), alpha));
                                g2d.setStroke(new BasicStroke(1.5f));
                                g2d.draw(new Line2D.Double(neuron.x, neuron.y, connection.x, connection.y));
                            }
                        }
                    }
                }

                for (NeuronNode neuron : neurons) {
                    double pulse = (Math.sin(neuron.pulsePhase) + 1) * 0.5;

                    if (neuron.activeState > 0.1) {
                        for (int i = 6; i > 0; i--) {
                            int alpha = (int)(pulse * 50 * neuron.activeState);
                            g2d.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), TEXT_COLOR.getBlue(), alpha));
                            int size = (int)(i * 4 * neuron.activeState);
                            g2d.fillOval((int)neuron.x - size/2, (int)neuron.y - size/2, size, size);
                        }
                        int alpha = (int)(200 * neuron.activeState);
                        g2d.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), TEXT_COLOR.getBlue(), alpha));
                        g2d.fillOval((int)neuron.x - 3, (int)neuron.y - 3, 6, 6);
                    }

                    int baseAlpha = (int)(100 * (1 - neuron.activeState) + 150 * neuron.activeState);
                    g2d.setColor(new Color(NEURON_COLOR.getRed(), NEURON_COLOR.getGreen(), NEURON_COLOR.getBlue(), baseAlpha));
                    g2d.fillOval((int)neuron.x - 2, (int)neuron.y - 2, 4, 4);
                }
                repaint();
            }
        };

        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
    
        // Top Panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.insets = new Insets(5, 5, 5, 5);
        topGbc.fill = GridBagConstraints.HORIZONTAL;
    
        // Profile Picture
        JLabel profilePic = createCircularAvatar(100);
        topGbc.gridx = 0;
        topGbc.gridy = 0;
        topGbc.gridwidth = 2;
        topPanel.add(profilePic, topGbc);
    
        // Username
        JLabel usernameLabel = new JLabel(username.toUpperCase());
        usernameLabel.setFont(poppinsFont.deriveFont(24f)); // Use poppinsFont
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topGbc.gridy = 1;
        topPanel.add(usernameLabel, topGbc);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
    
        setupProgressBar(topGbc, topPanel);
    
        // Fields Panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Add fields
        int gridY = 0;
        addFieldWithLabel(fieldsPanel, "NAME:", nameField, gridY++, gbc);
        addFieldWithLabel(fieldsPanel, "GENDER:", genderBox, gridY++, gbc);
        addFieldWithLabel(fieldsPanel, "AGE:", ageField, gridY++, gbc);
        addFieldWithLabel(fieldsPanel, "EMAIL:", emailField, gridY++, gbc);
        addFieldWithLabel(fieldsPanel, "INSTAGRAM:", instagramField, gridY++, gbc);
        addFieldWithLabel(fieldsPanel, "LINKEDIN:", linkedinField, gridY++, gbc);

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

            // Add panels to content
        contentPanel.add(fieldsPanel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);

        // Create Scroll Pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Custom ScrollBar UI
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 100, 200);
                this.trackColor = new Color(0, 30, 60);
            }
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        //JButton closeButton = new CustomButton("CLOSE");
        //closeButton.addActionListener(e -> dispose());  // Change to dispose
        //buttonPanel.add(closeButton);
        
        JButton editButton = new CustomButton("EDIT PROFILE");
        editButton.setFont(poppinsFont.deriveFont(14f));
        JButton saveButton = new CustomButton("SAVE");
        editButton.addActionListener(e -> toggleEditMode());
        saveButton.addActionListener(e -> saveProfile());
        
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
    
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        setContentPane(mainPanel);
        setSize(450, 700);
        setLocationRelativeTo(getParent());
        setFieldsEditable(false);
    }
    
    private void addFieldWithLabel(JPanel panel, String labelText, JComponent field, 
            int gridy, GridBagConstraints gbc) {
        // Label
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setFont(poppinsFont.deriveFont(14f));
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 0.3;
        panel.add(label, gbc);
    
        // Field
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(250, 30));
        panel.add(field, gbc);
    }

    private void loadProfileData() {
        try {
            ProfileData data = DatabaseManager.getProfileData(username);
            if (data != null) {
                nameField.setText(data.getName());
                genderBox.setSelectedItem(data.getGender());
                ageField.setText(data.getAge() > 0 ? String.valueOf(data.getAge()) : "");
                emailField.setText(data.getEmail());
                instagramField.setText(data.getInstagram());
                linkedinField.setText(data.getLinkedin());
                updateProfileCompletion();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading profile data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createCircularAvatar(int size) {
        BufferedImage output = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        // Background circle
        g2.setColor(new Color(0, 100, 200));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));
    
        // User icon
        g2.setColor(Color.WHITE);
        int centerX = size/2;
        int centerY = size/2;
        g2.fillOval(centerX - size/6, centerY - size/6, size/3, size/3);
        g2.fillArc(centerX - size/4, centerY + size/12, size/2, size/2, 0, 180);
    
        g2.dispose();
        return new JLabel(new ImageIcon(output));
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addStat(panel, "Trips Planned", "0");
        addStat(panel, "Places Visited", "0");
        addStat(panel, "Reviews", "0");
        addStat(panel, "Badges", "0");

        return panel;
    }

    private void addStat(JPanel panel, String title, String value) {
        JLabel stat = new JLabel("<html><body><center>" + title + "<br><font size='+1'><b>" + 
            value + "</b></font></center></body></html>");
        stat.setHorizontalAlignment(SwingConstants.CENTER);
        stat.setFont(new Font("Consolas", Font.PLAIN, 14));
        stat.setForeground(Color.CYAN);
        stat.setBorder(new RoundedCornerBorder());
        stat.setOpaque(true);
        stat.setBackground(new Color(0, 30, 60));
        panel.add(stat);
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setEnabled(false); // Initially disabled
    
        // Create custom ComboBox UI
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.CYAN.darker());
                        int width = getWidth();
                        int height = getHeight();
                        int[] xPoints = {width/4, width/2, 3*width/4};
                        int[] yPoints = {height/3, 2*height/3, height/3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                    }
                };
                button.setBackground(new Color(0, 30, 60));
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }
    
            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                ListCellRenderer<Object> renderer = comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
                
                if (c instanceof JComponent) {
                    ((JComponent)c).setOpaque(false);
                }
                
                // Paint background
                g.setColor(new Color(0, 30, 60));
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                
                // Paint the component on top
                boolean shouldValidate = c.getParent() != currentValuePane;
                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);
            }
        });
    
        // Custom renderer for both enabled and disabled states
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setForeground(Color.CYAN);
                label.setBackground(isSelected ? new Color(0, 100, 200) : new Color(0, 30, 60));
                label.setFont(new Font("Consolas", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
    
        box.setBackground(new Color(0, 30, 60));
        box.setForeground(Color.CYAN);
        box.setFont(new Font("Consolas", Font.PLAIN, 14));
        box.setBorder(new RoundedCornerBorder());
        box.setOpaque(false);        
    // Ensure proper popup menu width
    box.addPopupMenuListener(new PopupMenuListener() {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            JComboBox<?> combo = (JComboBox<?>) e.getSource();
            Object comp = combo.getUI().getAccessibleChild(combo, 0);
            if (comp instanceof JPopupMenu) {
                JPopupMenu popup = (JPopupMenu) comp;
                popup.setPreferredSize(new Dimension(combo.getWidth(), popup.getPreferredSize().height));
            }
        }
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {}
    });
}

    private void updateProfileCompletion() {
        int fields = 6;
        int filled = 0;
        
        if (!nameField.getText().isEmpty()) filled++;
        if (!genderBox.getSelectedItem().toString().isEmpty()) filled++;
        if (!ageField.getText().isEmpty()) filled++;
        if (!emailField.getText().isEmpty()) filled++;
        if (!instagramField.getText().isEmpty()) filled++;
        if (!linkedinField.getText().isEmpty()) filled++;
        
        int completion = (filled * 100) / fields;
        completionBar.setValue(completion);
        completionBar.setString("Profile Completion: " + completion + "%");
    }


    private void toggleEditMode() {
        editMode = !editMode;
        setFieldsEditable(editMode);
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        genderBox.setEnabled(editable);
        ageField.setEditable(editable);
        emailField.setEditable(editable);
        instagramField.setEditable(editable);
        linkedinField.setEditable(editable);
    }

    private void saveProfile() {
        try {
            int age = ageField.getText().isEmpty() ? 0 : Integer.parseInt(ageField.getText());
            DatabaseManager.updateProfile(
                username,
                nameField.getText(),
                (String) genderBox.getSelectedItem(),
                age,
                emailField.getText(),
                instagramField.getText(),
                linkedinField.getText()
            );
            updateProfileCompletion();
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            setFieldsEditable(false);
            editMode = false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid age", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class CustomTextField extends JTextField {
        public CustomTextField() {
            setOpaque(false);
            setBorder(new RoundedCornerBorder());
            setForeground(TEXT_COLOR);
            setCaretColor(TEXT_COLOR);
            setFont(poppinsFont.deriveFont(14f)); // Use poppinsFont
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

    static class CustomButton extends JButton {
        public CustomButton(String text) {
            super(text);
            setForeground(TEXT_COLOR);
            setFont(poppinsFont.deriveFont(Font.BOLD, 14f));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 35));
        }
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