
package com.lgs.workers;

import com.lgs.utils.JAMAUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author ShivanshuJS
 */
public class ExtractionWorker extends SwingWorker<Void, Void>{
    
    // Swing UI components which are required to access throughout the class.
    private final String selectedFilePath;
    private final String username;
    private final String password;
    private final JTextArea infoTextArea;
    private final JButton fileSelectorButton;
    
    // Constructor to initialize the global objects.
    public ExtractionWorker(String selectedFilePath, String username, String password, JTextArea infoTextArea, JButton fileSelectorButton){
        this.selectedFilePath = selectedFilePath;
        this.username = username;
        this.password = password;
        this.infoTextArea = infoTextArea;
        this.fileSelectorButton = fileSelectorButton;
        this.setProgress(10);
    }
    
    private Stack<Integer> getProgressStack(int peakValue){
        Stack<Integer> progressStack = new Stack<>();
        progressStack.push(peakValue);
        int decremenetValue = peakValue/5;
        for(int i=0; i<4; i++){
            peakValue = peakValue - decremenetValue;
            progressStack.push(peakValue);
        }
        return progressStack;
    }

    @Override
    protected Void doInBackground() throws Exception {
        InputStream inputStream = new FileInputStream(this.selectedFilePath);
        XSSFWorkbook excelWB = new XSSFWorkbook(inputStream);
        XSSFSheet sheet1 = excelWB.getSheetAt(0);
        Stack<Integer> progressStack = this.getProgressStack(sheet1.getLastRowNum());
        this.setProgress(20);
        for(int rowNumber = 1; rowNumber <= sheet1.getLastRowNum(); rowNumber++){
            XSSFRow currentRow = sheet1.getRow(rowNumber);
            String description = currentRow.getCell(13).toString();
            if(description != null && !description.equals("") && !description.equals(" ")){
                Document htmlDom = Jsoup.parse(description);
                Elements imgElements = htmlDom.select("img");
                for(int imgIndex=0; imgIndex<imgElements.size(); imgIndex++){
                    String resourceURL = imgElements.get(imgIndex).attr("src");
                    JAMAUtils jAMAUtils = new JAMAUtils(this.username, this.password, resourceURL);
                    jAMAUtils.extractImageToDisk();
                }
            }
            if(new Integer(rowNumber).equals(progressStack.peek())){
                this.setProgress(this.getProgress() + 15);
                progressStack.pop();
            }
        }
        this.infoTextArea.setText(this.infoTextArea.getText() + "\nExtracted Images Location: " + System.getProperty("java.io.tmpdir") + "JAMA" + File.separator + "attachments" + File.separator);
        this.fileSelectorButton.setEnabled(true);
        this.setProgress(100);
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            JOptionPane.showMessageDialog(null, "Somethin went wrong.\nError Message: " + ex.getMessage() + "\nPlease restart the application.", "Alert", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
}
