import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class main {
    private JTextField folderPane;
    private JButton browseBtn;
    private JButton startBtn;
    private JPanel pdfprinter_panel;
    private JLabel statusLabel;
    private JComboBox printerSelector;

    public main() {
        ArrayList<String> filePaths = new ArrayList<>();
//        String printerName = "Xprinter XP-460B"; // printer name
//        String printerName = "LABEL"; // printer name

        PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);
        for(int i=0; i<ps.length; i++){
            printerSelector.addItem(ps[i].getName());
        }

        // size in cm
        double width = 10;
        double height = 15;

        browseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                File workingDirectory = new File(System.getProperty("user.dir"));
                fileChooser.setCurrentDirectory(workingDirectory);
                int result = fileChooser.showOpenDialog(browseBtn);
                if(result == JFileChooser.APPROVE_OPTION){
                    File[] f = fileChooser.getSelectedFile().listFiles();
                    folderPane.setText(fileChooser.getSelectedFile().toString());
                    for(File i:f){
                        filePaths.add(i.toString());
                    }

                }
            }
        });



        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerSelector.getSelectedItem().toString();

                int docNum = 0;
                statusLabel.setText("Printing. Please wait.");
                for(int j=0; j<filePaths.size(); j++){
                    PDDocument document = null;
                    try {
                        document = PDDocument.load(new File(filePaths.get(j)));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

//                    PrintService myPrintService = findPrintService(printerName);
                    PrintService myPrintService = findPrintService(selectedPrinter);
                    PrinterJob job = PrinterJob.getPrinterJob();
                    Paper paper = new Paper();
//                    paper.setSize((width/2.54)*72d, (height/2.54)*72d);
                    paper.setSize(283.46472, 425.19672); // width = 10cm (3.93701 inches), height = 15cm (5.90551 inches)
                    paper.setImageableArea(12, 5, paper.getWidth(), paper.getHeight());
                    PageFormat pageFormat = new PageFormat();
                    pageFormat.setPaper(paper);
                    Book book = new Book();
                    book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.getNumberOfPages());
                    job.setPageable(book);

                    // Commented out to test printing with specified size
//                    job.setPageable(new PDFPageable(document));
                    try {
                        job.setPrintService(myPrintService);
                    } catch (PrinterException printerException) {
                        printerException.printStackTrace();
                    }
                    try {
                        job.print();
                    } catch (PrinterException printerException) {
                        printerException.printStackTrace();
                    }
                    try {
                        document.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    docNum++;
                }
                statusLabel.setText("Finished printing "+docNum+" documents.");
                pdfprinter_panel.validate();
            }
        });
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static void main(String[] args){
        System.out.printf( "java.home = %s\n", System.getProperty( "java.home" ) );

        JFrame frame = new JFrame("main");
        frame.setContentPane(new main().pdfprinter_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
