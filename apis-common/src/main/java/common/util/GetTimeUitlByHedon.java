package common.util;

import common.code.ResultCode;
import common.exception.ServiceException;
import common.util.timenlp.nlp.stringPreHandlingModule;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽取类似于："几分钟后"这样的任务
 *
 * @author Hedon Wang
 * @create 2020-12-17 20:13
 */
public class GetTimeUitlByHedon implements Serializable {

    static char[] cnArr = new char [] {'一','二','三','四','五','六','七','八','九'};
    static char[] chArr = new char [] {'十','百','千','万','亿'};
    static String allChineseNum = "零一二三四五六七八九十百千万亿";



    /**
     * 识别时间
     *
     * 例句：十年一个月八周二十天三小时四十分钟五十秒后提醒我去食堂吃饭
     *
     * @param str  目标串
     * @return
     */
    public static Map<String, Object> getTime(String str){

        Map<String, Object> result = new HashMap<>();

        result.put("success",true);


        //存放单位信息
        boolean[] units = {false,false,false,false,false,false,false};

        //存放数字信息
        String[] numbers = {"", "", "", "", "", "", ""};

        boolean hasHalfYear = false;
        boolean hasHalfMonth = false;
        boolean hasHalfDay = false;
        boolean hasHalfHour = false;
        boolean hasHalfMinute = false;

        //一年一个月八周二十天三小时四十分钟五十秒
        String timeStr;
        if (str.contains("后提醒我")){
            timeStr = StringUtils.substringBefore(str, "后提醒我");
            result.put("timeStr",timeStr+"后提醒我");
        }else if(str.contains("后")){
            timeStr = StringUtils.substringBefore(str, "后");
            result.put("timeStr",timeStr+"后");
        }else{
            result.put("success",false);
            return result;
        }

        timeStr = stringPreHandlingModule.numberTranslator(str);


        try {
            //年
            if (timeStr.contains("年半")){
                String[] month = timeStr.split("年半");
                numbers[0] = month[0];
                timeStr = month[1];
                units[0] = true;
                hasHalfYear = true;
            }else if (timeStr.contains("半年")){
                String[] month = timeStr.split("半年");
                //半年则说明前面没数字
                timeStr = month[0];
                units[0] = true;
                hasHalfYear = true;
            }else if (timeStr.contains("年")){
                String[] month = timeStr.split("年");
                numbers[0] = month[0];
                timeStr = month[1];
                units[0] = true;
            }

            //月
            if (timeStr.contains("半个月")){
                String[] month = timeStr.split("半个月");
                timeStr = month[0];
                units[1] = true;
                hasHalfMonth = true;
            }else if (timeStr.contains("个半月")){
                String[] month = timeStr.split("个半月");
                numbers[1] = month[0];
                timeStr = month[1];
                units[1] = true;
                hasHalfMonth = true;
            }else if (timeStr.contains("个月")){
                String[] month = timeStr.split("个月");
                numbers[1] = month[0];
                timeStr = month[1];
                units[1] = true;
            }

            //周
            if (timeStr.contains("周")){
                String[] day = timeStr.split("周");
                numbers[2] = day[0];
                timeStr = day[1];
                units[2] = true;
            }else if (timeStr.contains("个礼拜")){
                String[] day = timeStr.split("个礼拜");
                numbers[2] = day[0];
                timeStr = day[1];
                units[2] = true;
            }else if (timeStr.contains("礼拜")){
                String[] day = timeStr.split("礼拜");
                numbers[2] = day[0];
                timeStr = day[1];
                units[2] = true;
            }else if (timeStr.contains("个星期")){
                String[] day = timeStr.split("个星期");
                numbers[2] = day[0];
                timeStr = day[1];
                units[2] = true;
            }else if (timeStr.contains("星期")){
                String[] day = timeStr.split("星期");
                numbers[2] = day[0];
                timeStr = day[1];
                units[2] = true;
            }

            //天
            if (timeStr.contains("半天")){
                String[] day = timeStr.split("半天");
                timeStr = day[1];
                units[3] = true;
                hasHalfDay = true;
            }else if (timeStr.contains("天半")){
                String[] day = timeStr.split("天半");
                numbers[3] = day[0];
                timeStr = day[1];
                units[3] = true;
                hasHalfDay = true;
            } else if (timeStr.contains("天")) {
                String[] day = timeStr.split("天");
                numbers[3] = day[0];
                timeStr = day[1];
                units[3] = true;
            }

            //小时
            if (timeStr.contains("半个小时")){
                String[] hour = timeStr.split("半个小时");
                timeStr = hour[0];
                units[4] = true;
                hasHalfHour = true;
            }else if (timeStr.contains("个小时")){
                String[] hour = timeStr.split("个小时");
                numbers[4] = hour[0];
                timeStr = hour[1];
                units[4] = true;
            }else if (timeStr.contains("个半小时")){
                String[] hour = timeStr.split("个半小时");
                numbers[4] = hour[0];
                timeStr = hour[1];
                units[4] = true;
                hasHalfHour = true;
            }else if (timeStr.contains("半小时")){
                String[] hour = timeStr.split("半小时");
                timeStr = hour[0];
                units[4] = true;
                hasHalfHour = true;
            }else if (timeStr.contains("小时")){
                String[] hour = timeStr.split("小时");
                numbers[4] = hour[0];
                timeStr = hour[1];
                units[4] = true;
            }

            //分钟
            if (timeStr.contains("分钟")){
                String[] minute = timeStr.split("分钟");
                numbers[5] = minute[0];
                timeStr = minute[1];
                units[5] = true;
            }else if (timeStr.contains("分")){
                String[] minute = timeStr.split("分");
                numbers[5] = minute[0];
                timeStr = minute[1];
                units[5] = true;
            }

            //秒
            if (timeStr.contains("秒钟")){
                String[] second = timeStr.split("秒钟");
                numbers[6] = second[0];
                timeStr = second[1];
                units[6] = true;
            }else if (timeStr.contains("秒")){
                String[] second = timeStr.split("秒");
                numbers[6] = second[0];
                timeStr = second[1];
                units[6] = true;
            }
        }catch (Exception e){
            result.put("success",false);
        }

        //将中文数字转为阿拉伯数字
        int[] nums = new int[7];
        try {
            nums = convertAllChineseNumbersToArabicNumbers(numbers);
        }catch (ServiceException e){
            result.put("success",false);
        }

        //获取时间
        LocalDateTime startTime = LocalDateTime.now();

        //年
        if (units[0]){
            startTime = startTime.plusYears(nums[0]);
        }

        //月
        if (units[1]){
            startTime = startTime.plusMonths(nums[1]);
        }

        //周
        if (units[2]){
            startTime = startTime.plusWeeks(nums[2]);
        }

        //天
        if (units[3]){
            startTime = startTime.plusDays(nums[3]);
        }

        //小时
        if (units[4]){
            startTime = startTime.plusHours(nums[4]);
        }

        //分钟
        if (units[5]){
            startTime = startTime.plusMinutes(nums[5]);
        }

        //秒
        if (units[6]){
            startTime = startTime.plusSeconds(nums[6]);
        }

        if (hasHalfYear){
            startTime = startTime.plusMonths(6);
        }

        if (hasHalfMonth){
            startTime = startTime.plusDays(15);
        }

        if (hasHalfDay){
            startTime = startTime.plusHours(12);
        }

        if (hasHalfHour){
            startTime = startTime.plusMinutes(30);
        }

        if (hasHalfMinute){
            startTime = startTime.plusSeconds(30);
        }

        result.put("startTime",startTime);
        return result;
    }


    /**
     * 将全部中文数字转为阿拉伯数字
     *
     * @param numbers
     * @return
     */
    public static int[] convertAllChineseNumbersToArabicNumbers(String[] numbers){

        if (numbers.length != 7 ){
            throw new ServiceException(ResultCode.TASK_TIME_INVALID);
        }

        int[] results = {0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < numbers.length; i++) {
            try {
                if (StringUtils.isNotEmpty(numbers[i])){
                    results[i] = Integer.valueOf(numbers[i]);
                }
            }catch (Exception e){
                throw new ServiceException(ResultCode.TASK_TIME_INVALID);
            }
        }

        return results;
    }


    public static void main(String[] args) {
        Map<String, Object> map = getTime("3个半月四个半小时后到图书馆学习");
        System.out.println(map.get("timeStr"));
        System.out.println(map.get("startTime"));
        System.out.println(map.get("success"));
        System.out.println("3个半月四个半小时后到图书馆学习".replaceFirst("提问我",""));
    }
}
