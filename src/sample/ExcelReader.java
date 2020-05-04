package sample;

import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import sample.models.Person;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelReader {
    public static void saveFile(ObservableList<Person> listPerson, String filename) throws IOException {
        Workbook book = new HSSFWorkbook(); //створення книги
        Sheet sheet = book.createSheet("zarplata");  //створення листа

        // Нумерация починається з нуля
        Row row = sheet.createRow(0);

        //створення комірок у рядку почерзі починаючи із 0,1,2,...
        Cell RLSUM = row.createCell(0);
        RLSUM.setCellValue("RLSUM");
        Cell LSTBL = row.createCell(1);
        LSTBL.setCellValue("LSTBL");
        Cell INN = row.createCell(2);
        INN.setCellValue("INN");
        Cell RLKOD = row.createCell(3);
        RLKOD.setCellValue("RLKOD");
        Cell CARD_NO = row.createCell(4);
        CARD_NO.setCellValue("CARD_NO");
        int num = 0;
        for(Person elem:listPerson)
        {
            Double res = Double.parseDouble(elem.getRlsum());
            if(res > 0){
                num++;
                Row rowElem = sheet.createRow(num);

                rowElem.createCell(0).setCellValue(String.valueOf(res).replace(".",","));
                rowElem.createCell(1).setCellValue(elem.getLstbl());
                rowElem.createCell(2).setCellValue(elem.getInn());
                rowElem.createCell(3).setCellValue("");
                rowElem.createCell(4).setCellValue(elem.getCard_no());

            }
        };

        // Меняем размер столбца
        sheet.autoSizeColumn(1);
        // Записываем всё в файл
        FileOutputStream fs = new FileOutputStream(filename);
        book.write(fs);
        book.close();
        fs.close();
    }
}
