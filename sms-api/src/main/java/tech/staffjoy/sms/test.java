package tech.staffjoy.sms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;


public class test {

    public static void main(String[] args) throws FileNotFoundException {
        //当前工程路径
        String path = System.getProperty("user.dir");
        System.out.println("path"+path);
        //当前包的路径
        String outpath = test.class.getPackage().getName();
        System.out.println("outpath"+outpath);
        System.out.println(path+File.separator+outpath);

        
      /*  
        Properties properties = new Properties();
        InputStream in = new FileInputStream(new File(System.getProperty("user.dir")+File.separator+"db"+File.separator+"application.yml"));
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //String activemqConnection = properties.getProperty("activemq.connection");
        //System.out.println(activemqConnection);
         
        
        
        
    }

}
