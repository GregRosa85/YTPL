    
package ytpl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 *
 * @author hemp85
 */
public class YTPLCounter extends JFrame {
    
    List<JComponent> allComponents = new ArrayList<>();
    JTextField tf;
    JButton theButton;
    JTextArea ta;
    String lastLine = "";
    JSplitPane splitPane;
    JProgressBar bp;
    WebDriver driver;
    JPopupMenu rightClick;
    List<String> timeL = new ArrayList<>();
    List<String> titleL = new ArrayList<>();
    List<String> linkL= new ArrayList<>();
    JMenuBar menuBar;
    JMenu menuFile, menuInfo;
    JMenuItem exitItem, saveItem, about, count;
    File pathToBinaryL;
    //File pathToBinaryW = new File("/home/hemp85/Pulpit/firefox/firefox");
    FirefoxBinary ffBinaryL;
   // FirefoxBinary ffBinaryW = new FirefoxBinary(pathToBinaryW);
    FirefoxProfile firefoxProfile;
     
    
    public YTPLCounter(){
        
        menuBar = new JMenuBar();
        String[] menusNames = {"File","Info"};
        ArrayList<JMenu> menus = new ArrayList<>();
        for(String name:menusNames)
        {
            menus.add(new JMenu(name));
        }
       
        saveItem = new JMenuItem("Save results ...");
        count = new JMenuItem("Count time");
        exitItem = new JMenuItem("Exit");
        ListenForButton lForButton = new ListenForButton();
        saveItem.addActionListener(lForButton);
        saveItem.setMnemonic('s');
        exitItem.addActionListener(lForButton);
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        count.addActionListener(lForButton);
        count.setMnemonic('c');
        menuFile = menus.get(0);
        menuFile.add(saveItem);
        menuFile.add(count);
        menuFile.addSeparator();
        menuFile.add(exitItem);
        
        about = new JMenuItem("About");
        about.addActionListener(lForButton);
        about.setMnemonic('a');
        menuInfo = menus.get(1);
        menuInfo.add(about);
        
        Iterator<JMenu> menuIterator = menus.iterator();
        
        while(menuIterator.hasNext())
        {
           menuBar.add(menuIterator.next());
        }    
        
        
        JPanel[] panelList = {new JPanel(), new JPanel()};
        allComponents.add(new JTextField("Please, paste link to YT plalist"));//0
        allComponents.add(new JButton("Count total time"));//1
        allComponents.add(new JTextArea(""));//2
        
        for(JComponent jComponent:allComponents){
            if(!(jComponent instanceof JTextArea)){
                panelList[0].add(jComponent);
            } else{
                panelList[1].setBorder(BorderFactory.createTitledBorder("Results"));
                panelList[1].setLayout(new BorderLayout());
                bp = new JProgressBar();
                panelList[1].add(bp, BorderLayout.NORTH);
                bp.setVisible(false);
                ta = (JTextArea)jComponent;
                panelList[1].add(new JScrollPane(ta,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),BorderLayout.CENTER);
                
            }
        }
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelList[0], panelList[1]);
        splitPane.setEnabled(false);
        panelList[0].setBorder(BorderFactory.createTitledBorder("Link"));
       
        theButton = (JButton)allComponents.get(1);
        theButton.addActionListener(lForButton);
        
        
        
        tf = (JTextField)allComponents.get(0);
        tf.addActionListener(lForButton);
        ListenForMouse lForMouse = new ListenForMouse();
        tf.addMouseListener(lForMouse);
        
        tf.setColumns(20);
        rightClick = new JPopupMenu();
        JMenuItem pasteAction = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteAction.setText("Paste ...");
        rightClick.add(pasteAction);
        tf.setComponentPopupMenu(rightClick);
       
        Dimension dim = new Dimension(400,400);
        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
        this.setMinimumSize(dim);
        this.setResizable(false);
        this.setTitle("YouTube Plalist Counter");
        this.setJMenuBar(menuBar);
        this.add(splitPane);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theButton.requestFocus();
        
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new YTPLCounter());
    }

    private class ListenForMouse implements MouseListener{


        @Override
        public void mouseClicked(MouseEvent e)
        {
            
            if (e.getClickCount() == 2) 
            {
                tf.setText("");
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            
        }
    }

    private class ListenForButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(e.getSource()==saveItem)
            {
                try 
                {
                    fileSaver();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(YTPLCounter.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } 
            else if(e.getSource()==about)
            {
                JOptionPane.showMessageDialog(null, "YT Playlist Counter\nVersion 1.0\nAuthor:Grzegorz Rosa");
            }
            else if(e.getSource()==exitItem)
            {
                dispose();
            }
            else 
            {
            
            try 
            {
                lastLine = "Invalid link";
                ta.setText("");
                timeL.clear();
                titleL.clear();
                linkL.clear();
                bp.setVisible(true);
                bp.setIndeterminate(true);
                  
                SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        ta.setText("Loading. Please wait... "); 
                        loadWeb(tf.getText());
                        return null;     
                    } 

                    @Override
                    protected void done() {
                        JOptionPane.showMessageDialog(YTPLCounter.this, lastLine);
                        bp.setVisible(false);
                        driver.close();
                        
                        if(lastLine.equalsIgnoreCase("Invalid Link"))
                        {
                            ta.setText("");
                        }
                        
                        bp.setStringPainted(false);
                        bp.setValue(0);
                        bp.setString("0%");
                    }
                };
                worker.execute();
                
            }
            
            catch(Exception ex)
            {
                System.out.print(ex.getMessage());
            }
            }
        }
    }
    
    private void loadWeb(String url) 
    {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);
        pathToBinaryL = new File(s+"/firefox_linux/firefox");
            //File pathToBinaryW = new File("/home/hemp85/Pulpit/firefox/firefox");
        ffBinaryL = new FirefoxBinary(pathToBinaryL);
           // FirefoxBinary ffBinaryW = new FirefoxBinary(pathToBinaryW);
        firefoxProfile = new FirefoxProfile(); 
        String os = System.getProperty("os.name");
        if(os.equals("Linux"))
        {
            driver = new FirefoxDriver(ffBinaryL,firefoxProfile);
        }
        else 
        {
           // driver = new FirefoxDriver(ffBinaryW,firefoxProfile);
        }
        
        driver.get(url);
        
        try 
        {
            WebElement element = driver.findElement(By.className("browse-items-load-more-button"));
            
            while(element.isDisplayed())
            {
                    element.click();
                    Thread.sleep(2000);
                    element = driver.findElement(By.className("browse-items-load-more-button"));
            }
            
        } 
        
        catch (NoSuchElementException | InterruptedException ex)
        {
            System.out.println(ex.getMessage());
        }
       
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        List<WebElement> span= new ArrayList<>();
        List<WebElement> titles = new ArrayList<>();
        List<WebElement> links = new ArrayList<>();
        span=driver.findElements(By.className("yt-uix-tile"));
        ta.setText("");
        
        if(!span.isEmpty())
        {
            bp.setValue(0);
            int i = 0;
            bp.setFont(new Font("Serif", Font.BOLD, 8));
            bp.setIndeterminate(false);
            bp.setMinimum(0);
            bp.setMaximum(span.size()-1);
            bp.setStringPainted(true);
            for(WebElement x:span){
            int spanSize = span.size();
            
            try
            {
                String time = x.findElement(By.className("timestamp")).getText();
                String title = x.findElement(By.className("pl-video-title-link")).getText();
                String link = x.findElement(By.className("pl-video-title-link")).getAttribute("href");
                timeL.add(time);
                titleL.add(title);
                linkL.add(link);
                ta.append(title+": "+" "+time+"\n");
                bp.setValue(i);
                i++;
                bp.setString(((100*i)/(span.size()))+"%");
            } 
            
            catch (NoSuchElementException e)
            {
                i++;
            }
            
            }
        }
  
        for(int i=0;i<timeL.size();i++){
            minutes += Integer.parseInt(timeL.get(i).split(":")[0]);
            seconds += Integer.parseInt(timeL.get(i).split(":")[1]);
            
        }
        int countSeconds=seconds%60;
        int countMinutes=minutes+(seconds/60);
        int howManyMinutes=countMinutes%60;
        int totalHours=countMinutes/60;
        if(countSeconds!=0&&countMinutes!=0&&totalHours!=0){
        lastLine = "Total time: " +totalHours+" h "+howManyMinutes+" m " +countSeconds+" s";
        }
        ta.append(lastLine);
    }
    
    private void fileSaver() throws IOException
    { 
        
        if(timeL.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Nothing to save!");
        } 
         else 
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Excel .xls", ".xls"));

            if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                String filePath = file.getAbsolutePath();
                
                    if(!file.getName().endsWith(".xls"))
                    {
                        file = new File(filePath+".xls");
                    }
            
            HSSFWorkbook workbook = new HSSFWorkbook();
            CreationHelper ch = workbook.getCreationHelper();
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFFont hyperLinkFont = workbook.createFont();
            hyperLinkFont.setUnderline(HSSFFont.U_SINGLE);
            hyperLinkFont.setColor(IndexedColors.BLUE.getIndex());
            cellStyle.setFont(hyperLinkFont);
            HSSFSheet sheet = workbook.createSheet("YT playlist");
            HSSFRow row;
            Cell cell;
            Hyperlink link;
            
            for(int i=0;i<=timeL.size();i++)
            {   
                if((timeL.size())==i)
                {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(lastLine);
                }
                else
                {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(titleL.get(i));
                    link = ch.createHyperlink(Hyperlink.LINK_URL);
                    link.setAddress(linkL.get(i));
                    cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
                    cell.setCellStyle(cellStyle);
                    row.createCell(1).setCellValue(timeL.get(i));
                
                }
            }
            workbook.write(new FileOutputStream(file));
            workbook.close();
            JOptionPane.showMessageDialog(null, "Done !");
            }
        }
    }
    
}
