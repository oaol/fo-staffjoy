package tech.staffjoy.sms;

import java.util.HashMap;
import java.util.Map;

public class test {

    public static void main(String[] args) {
        Map <String,Integer> map = new HashMap<String,Integer>();

        Integer put = map.put("语文", 100);
        
        System.out.println(put);
        Integer put2 = map.put("数学", 90);
        System.out.println(put2);
        put=map.put("英文", 80);
        System.out.println(put);
        put2=map.put("数学", 60);
        System.out.println(put2);
        Integer integer = map.get("语文");
        System.out.println(integer);
        
        
        
        
        
    }

}
