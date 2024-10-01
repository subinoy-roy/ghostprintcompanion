import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.Loader;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

public class PDFPrinter {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: PDFPrinter.jar -path <path-to-pdf> -printer [<printer-name>]");
            System.exit(1);
        }

        String pdfPath = null;
        String printerName = null;

        for(int i = 0; i<args.length; i=i+2){
            String flag = args[i];
            String value = args[i+1];
            switch (flag){
                case "-path":
                    if(value!=null){
                        pdfPath = value;
                    } else {
                        System.err.println("File Not Found.");
                    }
                    break;
                case "-printer":
                    if(value==null || value.isBlank()){
                        System.err.println("Printer name can not be blank.");
                        System.exit(1);
                        return;
                    }else {
                        printerName = value;
                    }
                    break;
            }
        }

        printPDF(pdfPath, printerName);
    }

    public static void printPDF(String pdfPath, String printerName) {
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PrinterJob job = PrinterJob.getPrinterJob();

            // Find the specified print service
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService printService = null;
            if(printerName!=null && !printerName.isBlank()) {
                for (PrintService service : printServices) {
                    if (service.getName().equalsIgnoreCase(printerName.replace("\"",""))) {
                        printService = service;
                        break;
                    }
                }
                if (printService == null) {
                    System.err.println("Printer not found: " + printerName);
                    System.exit(1);
                } else {
                    job.setPrintService(printService);
                }
            } else{
                // Find the default print service
                printService = PrintServiceLookup.lookupDefaultPrintService();
                if (printService != null) {
                    job.setPrintService(printService);
                }
            }

            // Set the document to be printed
            job.setPageable(new PDFPageable(document));

            // Print the document
            job.print();

        } catch (IOException e) {
            System.err.println("Failed to load the PDF document. "+"Parameters: [PDF: " + pdfPath + " Printer: " + printerName +"]"+"Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Failed to print the document. Error: "+"Parameters: [PDF: "+ pdfPath +" Printer: "+printerName + "]"+"Error: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Document printed successfully. "+"Parameters: [PDF: "+pdfPath + " Printer: " + printerName + "]");
        System.exit(0);
    }
}
