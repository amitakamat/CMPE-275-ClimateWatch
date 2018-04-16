package data;

import gash.obs.madis.MesonetProcessor;
/*import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;*/
import gash.router.client.MessageClient;

import java.io.File;
import java.util.List;

public class ReadData
{
	public MessageClient mc;
	public ReadData(){
		
	}
	public ReadData(MessageClient mc){
		this.mc=mc;
	}
    MesonetProcessor mesonetProcessor = new MesonetProcessor();

    public void getFile()
    {
        File folder = new File("/Users/mulumoodi/Documents/CMPE-275-ClimateWatch/mesonetParser/sample_data/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
//            System.out.println(listOfFiles[i].getName());

            if (listOfFiles[i].getName().lastIndexOf(".") != -1) {
                String name = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf("."));
                System.out.println(name);
                //mesonetProcessor.main(new String[]{listOfFiles[i].toString(), "./catalog.csv", "./output"});
            }
        }
    }
    
    public void getFile(MessageClient mc,List<String> otherNodes)
    {
        File folder = new File("/Users/mulumoodi/Documents/CMPE-275-ClimateWatch/mesonetParser/sample_data/");
        File[] listOfFiles = folder.listFiles();
        System.out.println("No of file"+listOfFiles.length);

        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i].getName());

            if (listOfFiles[i].getName().lastIndexOf(".") != -1) {
                String name = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf("."));
                System.out.println(name);
                mesonetProcessor.main(new String[]{listOfFiles[i].toString(), "./catalog.csv", "./output"},mc,otherNodes);
            }
        }
    }

    public static void main(String[] args)
    {
        ReadData readData = new ReadData();
        readData.getFile();
    }
}
