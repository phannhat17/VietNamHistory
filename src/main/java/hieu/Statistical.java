import crawler.DataManage.DataHandling;

import java.util.*;

public class Statistical {

    public static String dataFolderPath = "data/";
    public static final String[] BIG_CATEGORIES = {"triều đại lịch sử","địa điểm du lịch, di tích lịch sử", "lễ hội văn hóa", "sự kiện lịch sử", "nhân vật lịch sử"};

    public static void main(String[] args) throws Exception {
        new Statistical();
    }

    HashSet<String> entityHashSet = new HashSet<>();

    Statistical() throws Exception{
        
    }
}
