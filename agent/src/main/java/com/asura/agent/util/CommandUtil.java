package com.asura.agent.util;

import com.asura.agent.thread.RunCmdThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.aspectj.bridge.MessageUtil.info;

/**
 * <p></p>
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author zhaozq
 * @version 1.0
 * @since 1.0
 */

public class CommandUtil {

    public static Logger logger = LoggerFactory.getLogger(CommandUtil.class);
    private static final Runtime runtime = Runtime.getRuntime();

    public static String execScript(String command){
        String result = "";
        String line = "";
        try {
            info("run 获取到脚本 " + command);
            Process process = runtime.exec(command);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                info(line);
                result += line;
            }
            is.close();
            isr.close();
            br.close();
        } catch (Exception e) {
            logger.error("脚本执行出错", e);
        }
        return result;
    }

    /**
     * @param command
     *
     * @return
     */
    public static String runScript(String command) {
        List<String> list = new ArrayList();
        RunCmdThread thread = new RunCmdThread(command, list);
        thread.start();
        long start = System.currentTimeMillis() / 1000;
        while (1 == 1) {
            if (list.size() > 0) {
                if (list.get(0).equals("00")) {
                    list.remove(0);
                }
                break;
            }
            if (System.currentTimeMillis() / 1000 - start > 10) {
                logger.error("线程超时:" + command);
                if (thread.isAlive()) {
                    try {
                        thread.interrupt();
                    } catch (Exception e) {
                    }
                }
                list.add("time out ");
                break;
            }
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return "";
        }
    }
}