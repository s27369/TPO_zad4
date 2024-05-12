package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class AdminGUI extends JFrame {
    public JTextField textField;
    public JButton submitButton, addTopicButton, removeTopicButton;
    public ButtonGroup radioGroup;
    public JPanel radioPanel, buttonPanel, inputPanel;
    public AdminGUI() {
        this.setTitle("Admin");
        this.setSize(400, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        initializeRadioPanel();
        initializeInputPanel();
        initializeButtonPanel();
        setVisible(true);
    }
    public String getSelectedTopic() {
        Enumeration<AbstractButton> buttons = radioGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
    public void initializeInputPanel(){
        this.inputPanel = new JPanel();
        this.inputPanel.setLayout(new BorderLayout());
        this.textField = new JTextField();
        this.inputPanel.add(this.textField, BorderLayout.SOUTH);
        this.add(inputPanel);
    }

    public void initializeButtonPanel(){
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(3, 1));

        this.submitButton = new JButton("Submit");
        this.buttonPanel.add(this.submitButton);

        this.addTopicButton = new JButton("Add Topic");
        this.buttonPanel.add(this.addTopicButton);

        this.removeTopicButton = new JButton("Remove Topic");
        this.buttonPanel.add(this.removeTopicButton);

        this.add(this.buttonPanel, BorderLayout.EAST);
    }

    public void initializeRadioPanel(){
        this.radioPanel = new JPanel(new FlowLayout());
        this.radioGroup = new ButtonGroup();
        this.radioPanel.setBackground(Color.blue);
        this.add(radioPanel, BorderLayout.NORTH);

    }

    public void updateTopics(String[] topicArray) {
        for(String s: topicArray) System.out.println(s);
        radioPanel.removeAll();
        radioGroup = new ButtonGroup();

        for (String topic : topicArray) {
            JRadioButton newRadioButton = new JRadioButton(topic);
            radioGroup.add(newRadioButton);
            radioPanel.add(newRadioButton);
        }
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }
}
