package GUI;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {
    private JPanel checkBoxPanel = new JPanel();
    private JTextArea textArea = new JTextArea("News:\n");
    public List<JCheckBox> checkBoxList = new ArrayList<>();

    public ClientGUI(){
        setLayout(new BorderLayout());

        add(checkBoxPanel, BorderLayout.NORTH);

        getContentPane().add(new JScrollPane(textArea));
        textArea.setEditable(false);
        add(textArea, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        setVisible(true);
    }

    public void updateCheckBoxes(String[] labels, ArrayList<String> subs) {
        checkBoxPanel.removeAll();

        for (String label : labels) {
            JCheckBox checkBox = new JCheckBox(label);
            checkBoxList.add(checkBox);
            if(subs.contains(label)){
                checkBox.setSelected(true);
            }
            checkBoxPanel.add(checkBox);
        }

        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    public void appendText( String text) {
        Document doc = this.textArea.getDocument();
        try {
            doc.insertString(doc.getLength(), text, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
