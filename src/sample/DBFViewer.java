package sample;

import org.jamel.dbf.DbfReader;
import sample.models.Person;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DBFViewer {
    public static ArrayList<Person> load(String filename) throws UnsupportedEncodingException {
        ArrayList<Person> list = new ArrayList<>();
        DbfReader reader = new DbfReader(new File(filename));
        Object[] row;
        while ((row = reader.nextRecord()) != null) {

            //зчитування даних із рядка
            String name = new String((byte[])row[6],"CP866").toUpperCase();
            String fam = new String((byte[]) row[5],"CP866").toUpperCase();
            String ot  = new String((byte[]) row[7],"CP866").toUpperCase();
            String inn = new String((byte[]) row[9]);
            String lstbl = new String((byte[])  row[4]);
            String card_no = new String((byte[])  row[3]);
            Double rlsum =  (double) row[8];
            Person person  = new Person(name,fam,ot,inn,lstbl,card_no,String.valueOf(rlsum));

            list.add(person);
        }
        return list;
    }
}
