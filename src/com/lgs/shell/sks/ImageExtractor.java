
package com.lgs.shell.sks;

import com.lgs.workers.ExtractionWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author ShivanshuJS
 */
public class ImageExtractor extends JDialog{

    // Swing UI components which are required to access throughout the class.
    private final JFileChooser fileSelector = new JFileChooser();
    private final JPanel mainPanel = new JPanel();
    private final JButton fileInvokeButton = new JButton("Select File");
    private final JButton convertButton = new JButton("Extract");
    private final JTextField usernameTF = new JTextField();
    private final JPasswordField passwordTF = new JPasswordField();
    private final JTextArea infoTextArea = new JTextArea();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    
    // Constructor to initialize Swing UI Components.
    public ImageExtractor(){
        this.initComponents();
    }
    
    // Initialize the Swing UI Components and create the dialog.
    private void initComponents(){
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(ImageExtractor.class.getResource("/com/lgs/resources/shell.png")));
        this.setTitle("Image Extractor");
        this.setBounds(100, 100, 380, 275);
        this.getContentPane().setLayout(new BorderLayout());
        this.setResizable(false);
        this.mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.mainPanel.setLayout(new BorderLayout());
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileLabel = new JLabel("Select a Microsoft Excel Document: ");
        filePanel.add((Component)fileLabel);
        this.fileInvokeButton.addActionListener((e) -> {
            this.progressBar.setValue(0);
            if(this.showFileSelectorDialog() == JFileChooser.APPROVE_OPTION){
                this.infoTextArea.setText("Selected File: " + this.fileSelector.getSelectedFile().getAbsolutePath());
                this.convertButton.setEnabled(true);
            } else {
                this.infoTextArea.setText("");
                JOptionPane.showMessageDialog(null, "You have not selected any file.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
        });
        filePanel.add((Component)this.fileInvokeButton);
        this.convertButton.setEnabled(false);
        this.convertButton.addActionListener((e) -> {
            this.fileInvokeButton.setEnabled(false);
            this.convertButton.setEnabled(false);
            if(this.usernameTF.getText() != null && this.usernameTF.getText().length() > 5 && this.passwordTF.getPassword() != null && this.passwordTF.getPassword().length > 5){
                ExtractionWorker conversionWorker = new ExtractionWorker(this.fileSelector.getSelectedFile().getAbsolutePath(), this.usernameTF.getText(), new String(this.passwordTF.getPassword()), this.infoTextArea, this.fileInvokeButton);
                conversionWorker.addPropertyChangeListener((propertyChangeEvent) -> {
                    if("progress".equals(propertyChangeEvent.getPropertyName())){
                        this.progressBar.setValue((Integer)propertyChangeEvent.getNewValue());
                    }
                });
                conversionWorker.execute();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Length of Username or Password", "Alert", JOptionPane.ERROR_MESSAGE);
                this.convertButton.setEnabled(true);
            }
        });
        filePanel.add((Component)this.convertButton);
        
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Enter Username: ");
        this.usernameTF.setPreferredSize(new Dimension(240, 25));
        usernamePanel.add((Component)usernameLabel);
        usernamePanel.add((Component)this.usernameTF);
        
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passwordLabel = new JLabel("Enter Password:  ");
        
        this.passwordTF.setPreferredSize(new Dimension(240, 25));
        passwordPanel.add((Component)passwordLabel);
        passwordPanel.add((Component)this.passwordTF);
        
        JPanel pageStartPanel = new JPanel();
        pageStartPanel.setLayout(new BoxLayout(pageStartPanel, BoxLayout.Y_AXIS));
        pageStartPanel.add((Component)filePanel);
        pageStartPanel.add((Component)usernamePanel);
        pageStartPanel.add((Component)passwordPanel);
        this.mainPanel.add((Component)pageStartPanel, BorderLayout.PAGE_START);
        
        JPanel selectedFilePanel = new JPanel(new BorderLayout());
        this.infoTextArea.setEditable(false);
        this.infoTextArea.setLineWrap(true);
        this.infoTextArea.setWrapStyleWord(true);
        this.infoTextArea.setFont(this.infoTextArea.getFont().deriveFont(11f));
        JScrollPane infoScroller = new JScrollPane(this.infoTextArea);
        infoScroller.setPreferredSize(new Dimension(340, 25));
        selectedFilePanel.add((Component)infoScroller, "Center");
        this.mainPanel.add((Component)selectedFilePanel, BorderLayout.CENTER);
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.progressBar.setPreferredSize(new Dimension(345, 25));
        this.progressBar.setStringPainted(true);
        progressPanel.add((Component)this.progressBar);
        this.mainPanel.add((Component)progressPanel, BorderLayout.PAGE_END);
        this.getContentPane().add((Component)this.mainPanel, "Center");
    }
    
    // Configure the JFileChooser and show it to the user to select the file.
    private int showFileSelectorDialog(){
        this.fileSelector.setCurrentDirectory(new File(System.getProperty("user.home")));
        this.fileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.fileSelector.setAcceptAllFileFilterUsed(false);
        this.fileSelector.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Excel Documents", "xlsx"));
        int isFileSelected = this.fileSelector.showOpenDialog(null);
        return isFileSelected;
    }
    
    // The main method. Program execution starts from here.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ImageExtractor wordToHtmlDialog = new ImageExtractor();
            wordToHtmlDialog.setDefaultCloseOperation(2);
            wordToHtmlDialog.setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ImageExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
