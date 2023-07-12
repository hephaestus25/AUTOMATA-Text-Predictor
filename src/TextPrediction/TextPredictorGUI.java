package TextPrediction;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;


public class TextPredictorGUI extends JFrame {
	  private TextPredictor predictor;
	  private JTextField trainingLibraryPathTextField;
	  private JTextField sentenceInputTextField;
	  private JTextField numWordsToPredictTextField;
	  private JLabel predictedWordsLabel;
	  private TextArea textArea;
	 

	  public TextPredictorGUI() {
	  	setResizable(false);
	    predictor = new TextPredictor();
	    
	    setSize(529, 555);
	    setTitle("Text Predictor");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    getContentPane().add(mainPanel);
	    
	    ImageIcon icon = new ImageIcon("");
	    JLabel background = new JLabel(icon);
	    background.setFont(new Font("Tahoma", Font.BOLD, 12));
	    background.setForeground(new Color(0, 20, 89));
	    mainPanel.add(background);
	    background.setLayout(new BorderLayout());

	    JPanel inputPanel = new JPanel(new GridLayout(7, 2));
	    background.add(inputPanel, BorderLayout.NORTH);

	    JLabel label = new JLabel("     Training Library Path:");
	    label.setForeground(new Color(0, 20, 89));
	    label.setFont(new Font("Tahoma", Font.BOLD, 12));
	    inputPanel.add(label);
	    trainingLibraryPathTextField = new JTextField(20);
	    trainingLibraryPathTextField.setBackground(new Color(228, 234, 250));
	    inputPanel.add(trainingLibraryPathTextField);
	    
	    JButton chooseLibraryButton = new JButton("Choose");
	    chooseLibraryButton.setForeground(new Color(0, 20, 89));
	    chooseLibraryButton.setBackground(new Color(186, 210, 248));
	    chooseLibraryButton.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	        chooseLibrary();
	      }
	    });
	    inputPanel.add(chooseLibraryButton);

	    JLabel label_1 = new JLabel("     Word/s:");
	    label_1.setForeground(new Color(0, 20, 89));
	    label_1.setBackground(new Color(192, 192, 192));
	    label_1.setFont(new Font("Tahoma", Font.BOLD, 12));
	    inputPanel.add(label_1);
	    sentenceInputTextField = new JTextField(20);
	    sentenceInputTextField.setBackground(new Color(228, 234, 250));
	    inputPanel.add(sentenceInputTextField);

	    JLabel label_2 = new JLabel("      Number of Words to Predict:");
	    label_2.setForeground(new Color(0, 20, 89));
	    label_2.setBackground(new Color(228, 217, 205));
	    label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
	    inputPanel.add(label_2);
	    numWordsToPredictTextField = new JTextField(20);
	    numWordsToPredictTextField.setBackground(new Color(228, 234, 250));
	    inputPanel.setBounds(130,100,100, 40);
	    inputPanel.add(numWordsToPredictTextField);
	    

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setBackground(new Color(228, 234, 250));
	    buttonPanel.setLayout(null);
	    background.add(buttonPanel, BorderLayout.CENTER);

	    textArea = new TextArea();
	    textArea.setBackground(new Color(220, 226, 248));
	    textArea.setBounds(24, 76, 462, 213);
	    buttonPanel.add(textArea);
	  
	    
	    JButton addButton = new JButton("Add the words");
	    addButton.setForeground(new Color(0, 20, 89));
	    addButton.setBackground(new Color(186, 210, 248));
	    addButton.setBounds(194, 10, 129, 25);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendWords();
            }

			private void sendWords() {
				textArea.append(" " + predictedWordsLabel.getText());
				
			}
        });
        buttonPanel.add(addButton);
	    
        JButton clearButton = new JButton("Clear");
        clearButton.setForeground(new Color(0, 20, 89));
        clearButton.setBackground(new Color(186, 210, 248));
	    clearButton.setBounds(344, 10, 116, 25);
	    clearButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    clearFields();
	    }
	    });
	    buttonPanel.add(clearButton);
	    
	    
	    JButton openButton = new JButton("Open");
	    openButton.setForeground(new Color(0, 20, 89));
	    openButton.setBackground(new Color(186, 210, 248));
        openButton.setBounds(380, 295, 80, 25);
        openButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openFile();
          }
        });
        buttonPanel.add(openButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setForeground(new Color(0, 20, 89));
        saveButton.setBackground(new Color(186, 210, 248));
        saveButton.setBounds(290, 295, 80, 25);
        saveButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            saveFile();
          }
        });
        buttonPanel.add(saveButton);
	    
	    	    JPanel outputPanel = new JPanel();
	    	    outputPanel.setBorder(new LineBorder(new Color(192, 192, 192)));
	    	    outputPanel.setBackground(new Color(228, 234, 250));
	    	    outputPanel.setBounds(24, 46, 462, 24);
	    	    buttonPanel.add(outputPanel);
	    	    
	    	    predictedWordsLabel = new JLabel(" ");
	    	    predictedWordsLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
	    	    predictedWordsLabel.setForeground(new Color(0, 20, 89));
	    	    outputPanel.add(predictedWordsLabel);
	    	    
	    	    	    JButton predictButton = new JButton("Predict");
	    	    	    predictButton.setForeground(new Color(0, 20, 89));
	    	    	    predictButton.setBackground(new Color(186, 210, 248));
	    	    	    predictButton.setBounds(58, 10, 116, 25);
	    	    	    buttonPanel.add(predictButton);
	    	    	    predictButton.addActionListener(new ActionListener() {
	    	    	      @Override
	    	    	      public void actionPerformed(ActionEvent e) {
	    	    	        predictWords();
	    	    	      }
	    	    	    });
	  }  

    private void chooseLibrary() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(TextPredictorGUI.this);
    if (result == JFileChooser.APPROVE_OPTION) {
    File selectedFile = fileChooser.getSelectedFile();
    trainingLibraryPathTextField.setText(selectedFile.getAbsolutePath());
    }
    }
    
    private void openFile() {
    	  JFileChooser fileChooser = new JFileChooser();
    	  int result = fileChooser.showOpenDialog(TextPredictorGUI.this);
    	  if (result == JFileChooser.APPROVE_OPTION) {
    	    File selectedFile = fileChooser.getSelectedFile();
    	    try {
    	      String content = readFile(selectedFile);
    	      textArea.setText(content);
    	    } catch (IOException ex) {
    	      JOptionPane.showMessageDialog(TextPredictorGUI.this,
    	          "Error reading file: " + ex.getMessage(), "Error",
    	          JOptionPane.ERROR_MESSAGE);
    	    }
    	  }
    	}

    	private String readFile(File file) throws IOException {
    	  StringBuilder sb = new StringBuilder();
    	  try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	      sb.append(line).append("\n");
    	    }
    	  }
    	  return sb.toString();
    	}
    	
    	private void saveFile() {
    		  JFileChooser fileChooser = new JFileChooser();
    		  int result = fileChooser.showSaveDialog(TextPredictorGUI.this);
    		  if (result == JFileChooser.APPROVE_OPTION) {
    		    File selectedFile = fileChooser.getSelectedFile();
    		    try {
    		      writeFile(selectedFile, textArea.getText());
    		    } catch (IOException ex) {
    		      JOptionPane.showMessageDialog(TextPredictorGUI.this,
    		          "Error writing file: " + ex.getMessage(), "Error",
    		          JOptionPane.ERROR_MESSAGE);
    		    }
    		  }
    		  }

    		  private void writeFile(File selectedFile, String text) throws IOException {
    		  try (FileWriter fileWriter = new FileWriter(selectedFile)) {
    		  fileWriter.write(text);
    		  }
    		  }
    
    private void predictWords() {
           String libraryPath = trainingLibraryPathTextField.getText();
           predictor.train(libraryPath);
           String sentence = sentenceInputTextField.getText();
           int numWords = Integer.parseInt(numWordsToPredictTextField.getText());

           String[] predictedWords = predictor.predict(sentence, numWords, numWords);
           predictedWordsLabel.setText(String.join(" ", predictedWords));
    
    
    }
    
    private void clearFields() {
    	  textArea.setText("");
    	  predictedWordsLabel.setText("");
    	  
    	  }

    public static void main(String[] args) {
     TextPredictorGUI gui = new TextPredictorGUI();
      gui.setVisible(true);
  
    
    }
    }