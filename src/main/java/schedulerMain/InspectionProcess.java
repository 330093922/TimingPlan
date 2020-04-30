package schedulerMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * @ClassName: InspectionProcess
 * @description: 检查进程
 * @author: wrc
 * @Date: 2019年9月23日 上午9:02:32
 */
public class InspectionProcess implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    //配置文件路径
    private String path = "/ibp/ini/SchedulingDictionary.ini";
    // C:\\Users\\Administrator\\Desktop\\SchedulingDictionary.ini
    private  Set<String> set = new HashSet<String>();
    @Override
    public void run() {
        // 读取配置文件
        try {
            Set<String> set = new HashSet<String>();
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            HashMap<String, String> configureMap = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                if (line.contains("#")) {
                    continue;
                }
                String[] strs = line.split("\t");
                configureMap.put(strs[0], strs[1]);
            }
            Iterator<Entry<String, String>> entry = configureMap.entrySet().iterator();
            BufferedReader reader =null;
            while (entry.hasNext()) {
                Entry<String, String> entryed = entry.next();
                String cmd = "ps -ef | grep "+entryed.getKey();
                String res =  exeCmd(cmd);
                String[] reseds = res.split("\n");
                //活着的程序放进去
                for(String resed:reseds) {
                    if(resed.contains("java -jar ")) {
                        String[] atoms = resed.split("-jar");
                        LOGGER.info("活着的程序"+atoms[1].trim());
                        set.add(atoms[1].trim());
                    }
                }
            }
            //删除不需要重启的程序
            List<String> listed = new ArrayList<String>();
            if(set.size()>0) {
                for (String seted : set) {
                    if (configureMap.containsKey(seted)) {
                        configureMap.remove(seted);
                    }
                }
            }
            //重启程序
                Iterator<Entry<String,String>> entryed = configureMap.entrySet().iterator();
                while(entryed.hasNext()) {
                    Entry<String,String> entryeds = entryed.next();
                    LOGGER.info("被调用的程序"+entryeds.getValue());
                    String str = exeCmd(entryeds.getValue());
                    System.out.println(str);
                }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error("读取文件失败!");
        }
    }

    // 操作linux命令
    public static String exeCmd(String commandStr) {

        String result = null;
        try {
            String[] cmd = new String[]{"/bin/sh", "-c",commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println("1==="+line.toString());
                //执行结果加上回车
                sb.append(line).append("\n");
            }
            result = sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}