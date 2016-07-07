/*
 * Created on 2005-6-28
 */
package com.pabula.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;


/**
 * @author Dekn
 *         ���ڹ�����
 */
public class DateUtil {

    public static Timestamp getNowTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String getYear() {
        SimpleDateFormat date = new SimpleDateFormat("yy");
        return date.format(new Date());
    }

    public static String getYearYYYY() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        return date.format(new Date());
    }

    public static String getMonth() {
        SimpleDateFormat date = new SimpleDateFormat("MM");
        return date.format(new Date());
    }

    public static String getDay() {
        SimpleDateFormat date = new SimpleDateFormat("dd");
        return date.format(new Date());
    }

    public static String getHour() {
        SimpleDateFormat date = new SimpleDateFormat("HH");
        return date.format(new Date());
    }

    public static String getMinute() {
        SimpleDateFormat date = new SimpleDateFormat("mm");
        return date.format(new Date());
    }

    public static String getSecond() {
        SimpleDateFormat date = new SimpleDateFormat("ss");
        return date.format(new Date());
    }

    public static String getMillisecond() {
        SimpleDateFormat date = new SimpleDateFormat("SSS");
        return date.format(new Date());
    }

    /**
     * �ж��ṩ�������Ƿ�Ϊ����
     *
     * @param time
     * @return
     */
    public static boolean isToday(Timestamp time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

        if (format.format(time).equals(format.format(new Date()))) {
            return true;
        }

        return false;
    }

    /**
     * ȡ�õ�ǰ����
     *
     * @param dateFormat
     * @return
     */
    public static String getCurrentDay(String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(new Date());
    }

    /**
     * ����2������֮����������,�����ڵ����ڼ�ȥ��ǰ������
     *
     * @param now  ���ڵ�ʱ�䣬��ʽΪyyyy-MM-dd
     * @param fore ��ǰ��ʱ�䣬��ʽΪyyyy-MM-dd
     * @return
     */
    public static long getDayBetween(String now, String fore) {
        long count = 0;

        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date nowDate = myFormatter.parse(now);
            Date foreDate = myFormatter.parse(fore);

            count = (long) (nowDate.getTime() - foreDate.getTime()) / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            count = 0;
            e.printStackTrace();
        }

        return count;
    }

    /**
     * ȡ�ý����Timestamp������,ȡ���յ���,��,��,����ʱ,��,��,���붼Ϊ0
     *
     * @return
     * @author Dekn  2006-2-16
     */
    public static Timestamp getTodayTimestamp() {
        String nowDateStr = getCurrentDay("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(sdf.parse(nowDateStr).getTime());
        } catch (ParseException e) {
            // TODO �Զ���� catch ��
            e.printStackTrace();
        }

        //System.err.println("Now day is:  " + timestamp.toString());

        return timestamp;
    }


    /**
     * ȡǰN���N���Timestamp������,amountΪ����ʱȡ��N��,Ϊ��ʱ,ȡǰN��
     * ˼·:
     * ��ȡyyyy-MM-dd��ʽ�ĵ�ǰ�����ַ�,
     * �ٽ�����Date����,�ṩ��Calendar,
     * ����Calendar��add��������ǰN���N��
     * ���ȡCalendar��TimeInMillis,
     * ���ṩ��Timestampe�Ĺ���
     *
     * @param dateStr ����,��ʽ������yyyy-MM-dd
     * @param amount
     * @return
     * @author Dekn  2006-2-16
     */
    public static String rollDayStrForToday(String dateFormat, int amount) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(rollDayTimestamp(null, amount));
    }

    /**
     * ȡǰN���N���Timestamp������,amountΪ����ʱȡ��N��,Ϊ��ʱ,ȡǰN��
     * ˼·:
     * ��ȡyyyy-MM-dd��ʽ�ĵ�ǰ�����ַ�,
     * �ٽ�����Date����,�ṩ��Calendar,
     * ����Calendar��add��������ǰN���N��
     * ���ȡCalendar��TimeInMillis,
     * ���ṩ��Timestampe�Ĺ���
     *
     * @param dateStr ����,��ʽ������yyyy-MM-dd
     * @param amount
     * @return
     * @author Dekn  2006-2-16
     */
    public static Timestamp rollDayTimestamp(String dateStr, int amount) {

        //��ȡyyyy-MM-dd��ʽ�ĵ�ǰ�����ַ�
        String nowDate = dateStr;

        if (null == nowDate || nowDate.trim().equals("")) {
            nowDate = getCurrentDay("yyyy-MM-dd");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //�ٽ�����Date����,�ṩ��Calendar
        Calendar nowCalendar = Calendar.getInstance();

        try {
            nowCalendar.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO �Զ���� catch ��
            e.printStackTrace();
        }

        //����Calendar��add��������ǰN���N��
        nowCalendar.add(Calendar.DATE, amount);    //����

        //���ȡCalendar��TimeInMillis,���ṩ��Timestampe�Ĺ���
        Timestamp timestamp = new Timestamp(nowCalendar.getTimeInMillis());

//		System.err.println("rollDayTimestamp: " + timestamp.toString());

        return timestamp;
    }


    /**
     * ȡǰN���N�µ�Timestamp������,amountΪ����ʱȡ��N��,Ϊ��ʱ,ȡǰN��
     * ˼·:
     * ��ȡyyyy-MM-dd��ʽ�ĵ�ǰ�����ַ�,
     * �ٽ�����Date����,�ṩ��Calendar,
     * ����Calendar��add��������ǰN���N��
     * ���ȡCalendar��TimeInMillis,
     * ���ṩ��Timestampe�Ĺ���
     *
     * @param dateStr ����,��ʽ������yyyy-MM-dd
     * @param amount
     * @return
     * @author Dekn  2006-2-16
     */
    public static Timestamp rollMonthTimestamp(String dateStr, int amount) {

        //��ȡyyyy-MM-dd��ʽ�ĵ�ǰ�����ַ�
        String nowDate = dateStr;

        if (null == nowDate || nowDate.trim().equals("")) {
            nowDate = getCurrentDay("yyyy-MM-dd");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //�ٽ�����Date����,�ṩ��Calendar
        Calendar nowCalendar = Calendar.getInstance();

        try {
            nowCalendar.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO �Զ���� catch ��
            e.printStackTrace();
        }

        //����Calendar��add��������ǰN���N��
        nowCalendar.add(Calendar.MONTH, amount);    //����

        //���ȡCalendar��TimeInMillis,���ṩ��Timestampe�Ĺ���
        Timestamp timestamp = new Timestamp(nowCalendar.getTimeInMillis());


        return timestamp;
    }


    /**
     * ��ʽ������
     *
     * @param time
     * @param formatStr
     * @return
     */
    public static String format(Timestamp time, String formatStr) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String date = format.format(time);
        return date;
    }

    /**
     * ��ʽ������
     *
     * @param time
     * @param formatStr
     * @return
     */
    public static String format(String time, String formatStr) {
        if (StrUtil.isNull(time)) {
            return null;
        }

        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            date = format.format(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return date;
    }

    /**
     * ����ṩ���������·ݣ�ת��Ϊ�ַ��ͣ������ṩ��1������һ��
     *
     * @param month
     * @return
     * @author ZHYJJ 2007-9-5 11:29:09
     */
    public static String getMonthStr(int month) {
        String monthStr = "";

        switch (month) {
            case 1:
                monthStr = "һ��";
                break;
            case 2:
                monthStr = "����";
                break;
            case 3:
                monthStr = "����";
                break;
            case 4:
                monthStr = "����";
                break;
            case 5:
                monthStr = "����";
                break;
            case 6:
                monthStr = "����";
                break;
            case 7:
                monthStr = "����";
                break;
            case 8:
                monthStr = "����";
                break;
            case 9:
                monthStr = "����";
                break;
            case 10:
                monthStr = "ʮ��";
                break;
            case 11:
                monthStr = "ʮһ��";
                break;
            case 12:
                monthStr = "ʮ����";
                break;
        }
        return monthStr;
    }

    /**
     * ȡ�õ�ǰ�ǽ����еĵڼ���
     *
     * @return
     */
    public static int getWeekOfYear() {
        int week = 0;

        Calendar c = Calendar.getInstance();
        week = c.get(Calendar.WEEK_OF_YEAR);

        return week;
    }

    /**
     * ����ṩ����(year),����(weekNo)������ܶ�Ӧ����ʼ����
     *
     * @param year
     * @param weekNo
     * @return
     */
    public static String getStartDate(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat returnDate = new SimpleDateFormat("yyyy��MM��dd��");

        return returnDate.format(cal.getTime());
    }

    /**
     * ����ṩ����(year),����(weekNo)������ܶ�Ӧ�Ľ�������
     *
     * @param year
     * @param weekNo
     * @return
     */
    public static String getEndDate(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo + 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat returnDate = new SimpleDateFormat("yyyy��MM��dd��");

        return returnDate.format(cal.getTime());
    }

    /**
     * ȡ����������֮��������б�
     *
     * @param endDate
     * @param beginDate
     * @return
     * @author ZHYJJ   2007-9-29 0:51:08
     */
    public static Collection getTwoDateDayList(String endDate, String beginDate) {
        Collection resultList = new ArrayList();
        long dayCount = DateUtil.getDayBetween(endDate, beginDate);
        resultList.add(beginDate);
        for (int i = 0; i < dayCount; i++) {
            Timestamp now = DateUtil.rollDayTimestamp(beginDate, 1);
            beginDate = DateUtil.format(now, "yyyy-MM-dd");
            //resultList.add(beginDate.substring(0,4)+"��"+beginDate.substring(5,7)+"��"+beginDate.substring(8,10)+"��");
            resultList.add(beginDate);
        }
        return resultList;

    }

    /**
     * ȡ����������֮���������
     *
     * @param endDate
     * @param beginDate
     * @return
     * @author ZHYJJ   2007-9-29 0:51:08
     */
    public static Collection getTwoDateDayListDesc(String endDate, String beginDate) {
        Collection resultList = new ArrayList();
        long dayCount = DateUtil.getDayBetween(endDate, beginDate);
        resultList.add(endDate);
        for (long i = dayCount; i > 0; i--) {
            Timestamp now = DateUtil.rollDayTimestamp(endDate, -1);
            endDate = DateUtil.format(now, "yyyy-MM-dd");
            //resultList.add(beginDate.substring(0,4)+"��"+beginDate.substring(5,7)+"��"+beginDate.substring(8,10)+"��");
            resultList.add(endDate);
        }
        return resultList;

    }

    /**
     * �������,ȡ�����ڼ�
     *
     * @param date
     * @return
     * @author ZHYJJ   2007-9-29 0:41:19
     */
    public static String getWeekForDate(String date, String format) {
        String week = "";

        //mydate= myFormatter.parse("2001-1-1");
        SimpleDateFormat f1 = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = f1.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("E");
        week = formatter.format(d);


        return week;
    }

    /**
     * ȡ�ñ�׼������ (yyyy-MM-dd)
     *
     * @param year
     * @param month
     * @param day
     * @return
     * @author lw 2007-10-31 ����05:33:11
     */
    public static String getStandardDate(int year, int month, int day) {
        String yearStr = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr = String.valueOf(day);
        if (month < 10) {
            monthStr = "0" + month;
        }
        if (day < 10) {
            dayStr = "0" + day;
        }
        return yearStr + "-" + monthStr + "-" + dayStr;
    }


    /**
     * ȡĳ��ʱ���ǰNСʱ���NСʱ��String������,
     *
     * @param dateStr ʱ��,��ʽ�� yyyy-MM-dd HH:mm:ss,Ϊ��ʱȡ��ǰʱ��
     * @param amount  Сʱ������Ϊ��NСʱ������ΪǰNСʱ
     * @return
     * @author wdc 2007-11-4 ����12:29:40
     */
    public static String rollHoursTimestamp(String dateStr, String amount) {
        //��ȡyyyy-MM-dd HH:mm:ss��ʽ�ĵ�ǰ�����ַ�
        String nowDate = dateStr;

        /********************************************************
         * ��������ֵ����ȷ��Ϊ�գ���ȡ��ǰʱ��
         ********************************************************/
        if (null == nowDate || nowDate.trim().equals("") || !checkDate(dateStr, "yyyy-MM-dd HH:mm:ss")) {
            nowDate = getCurrentDay("yyyy-MM-dd HH:mm:ss");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //�ٽ�����Date����,�ṩ��Calendar
        Calendar nowCalendar = Calendar.getInstance();

        try {
            nowCalendar.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO �Զ���� catch ��
            e.printStackTrace();
        }

        //����Calendar��add��������ǰN���N����
        nowCalendar.add(Calendar.HOUR, StrUtil.getNotNullIntValue(amount));    //����

        //���ȡCalendar��TimeInMillis,���ṩ��Timestampe�Ĺ���
        Timestamp timestamp = new Timestamp(nowCalendar.getTimeInMillis());

//		System.err.println("rollDayTimestamp: " + timestamp.toString());

        return timestamp.toString();
    }


    /**
     * ȡĳ��ʱ���ǰN���ӻ��N���ӵ�String������
     *
     * @param dateStr ʱ��,��ʽ�� yyyy-MM-dd HH:mm:ss,Ϊ��ʱȡ��ǰʱ��
     * @param amount  ����������Ϊ��N���ӣ�����ΪǰN����
     * @return
     * @author lh 20120323 ����16:46
     */
    public static String rollMinutesTimestamp(String dateStr, String amount) {
        //��ȡyyyy-MM-dd HH:mm:ss��ʽ�ĵ�ǰ�����ַ�
        String nowDate = dateStr;

        /********************************************************
         * ��������ֵ����ȷ��Ϊ�գ���ȡ��ǰʱ��
         ********************************************************/
        if (null == nowDate || nowDate.trim().equals("") || !checkDate(dateStr, "yyyy-MM-dd HH:mm:ss")) {
            nowDate = getCurrentDay("yyyy-MM-dd HH:mm:ss");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //�ٽ�����Date����,�ṩ��Calendar
        Calendar nowCalendar = Calendar.getInstance();

        try {
            nowCalendar.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO �Զ���� catch ��
            e.printStackTrace();
        }

        //����Calendar��add��������ǰN���N����
        nowCalendar.add(Calendar.MINUTE, StrUtil.getNotNullIntValue(amount));    //����

        //���ȡCalendar��TimeInMillis,���ṩ��Timestampe�Ĺ���
        Timestamp timestamp = new Timestamp(nowCalendar.getTimeInMillis());

//		System.err.println("rollDayTimestamp: " + timestamp.toString());

        return timestamp.toString();
    }


    /**
     * �Ƚ϶�ʱ��Ĵ�С  ���ȵ�ǰʱ���(��) ����true  ���� false
     *
     * @param dateStr
     * @return
     * @author wdc 2007-11-7 ����07:59:24
     */
    public static boolean compareTotime(String dateStr) {
        boolean isOK = false;
        long count = 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowtimeStr = getCurrentDay("yyyy-MM-dd HH:mm:ss");
        try {
            Date nowDate = myFormatter.parse(nowtimeStr);
            Date foreDate = myFormatter.parse(dateStr);
            count = (long) (nowDate.getTime() - foreDate.getTime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            count = 0;
            e.printStackTrace();
        }
        if (count > 0) {
            isOK = true;
        }
        return isOK;
    }


    /**
     * �Ƚ�����ʱ��Ĵ�С
     *
     * @param oneStr
     * @param twoStr
     * @return
     */
    public static boolean compareTotime(String oneStr, String twoStr) {
        boolean isOK = false;
        long count = 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date nowDate = myFormatter.parse(oneStr);
            Date foreDate = myFormatter.parse(twoStr);
            count = (long) (nowDate.getTime() - foreDate.getTime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            count = 0;
            e.printStackTrace();
        }
        if (count > 0) {
            isOK = true;
        }
        return isOK;
    }

    /**
     * ȡ�õ�ǰ��ʱ��
     *
     * @return
     * @author pabula 2015-6-28 ����08:23:56
     */
    public static Timestamp getCurrTime() {
        return getNotNullTimestampValue(null, "yyyy-MM-dd HH:mm:ss:SSS");
    }


    /**
     * ȡ��һ���ǿյ�ʱ���������ṩ�����Ϊ�ջ�ΪNULL�����Զ�ȡ��ǰʱ��
     *
     * @param data
     * @param dateFormat TODO
     * @return
     */
    public static Timestamp getNotNullTimestampValue(String data, String dateFormat) {
        Timestamp value;
        try {
            if (null == data || data.equals("")) {
                value = new Timestamp(System.currentTimeMillis());
            } else {
                SimpleDateFormat smd = new SimpleDateFormat(dateFormat);
                value = new Timestamp(smd.parse(data).getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = new Timestamp(System.currentTimeMillis());
        }

        return value;
    }

    /***
     * �Ƚ����������ַ�Ĵ�С
     * sourceStrС��destStr ���� -1
     * sourceStr����destStr ���� 1
     * sourceStr����destStr ���� 0
     * �����쳣���� Integer.MAX_VALUE
     *
     * @param sourceStr
     * @param destStr
     * @param formart
     * @return
     * @author lala 2009-1-8 ����05:50:31
     */
    public static int compareTimstampStr(String sourceStr, String destStr, String formartStr) {
        int result = 10;

        SimpleDateFormat dateFormatter = new SimpleDateFormat(formartStr);
        long temp = 10;
        try {
            Date beginDate = dateFormatter.parse(sourceStr);
            Date endDate = dateFormatter.parse(destStr);
            temp = (long) (beginDate.getTime() - endDate.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = Integer.MAX_VALUE;
            e.printStackTrace();
        }
        if (temp > 0) {
            result = 1;
        } else if (temp == 0) {
            result = 0;
        } else if (temp < 0) {
            result = -1;
        }

        return result;
    }


    /**
     * ���������Ѷ�ռ������������������ʱ�����ͣ�����ֻ��ʾ���죬ǰ�죬�������ʾ��������
     * ���ڸ�ʽ������ yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateFormat(String date) {
        //�������͵ĵ�ǰʱ��
        Timestamp nowDay = getTodayTimestamp();
        String hour = date.substring(11, date.length());
        date = format(date, "yyyy-MM-dd");
        //����ǵ����return����
        if (date.equals(getCurrentDay("yyyy-MM-dd"))) {
            return hour;
        }
        //����ľͷ������켸��
        if (nowDay.equals(rollDayTimestamp(date, 1))) {
            return "���� " + hour;
        }
        //ǰ��ľͷ���ǰ�켸��
        if (nowDay.equals(rollDayTimestamp(date, 2))) {
            return "ǰ�� " + hour;
        }
        //����3���ֱ�ӷ���ʱ��
        return date + " " + hour;
    }


    /**
     * �������ʱ��֮ǰ������������¶�����,��������,����������
     *
     * @param endDate
     * @param beginDate
     * @return ���� �� �� ��
     */
    public static int[] getDayBetweenForYMD(String endDate, String beginDate) {

        int endDateY = Integer.parseInt(format(endDate, "yyyy", "yyyy-MM-dd"));
        int endDateM = Integer.parseInt(format(endDate, "MM", "yyyy-MM-dd"));
        int endDateD = Integer.parseInt(format(endDate, "dd", "yyyy-MM-dd"));
        int beginDateY = Integer.parseInt(format(beginDate, "yyyy", "yyyy-MM-dd"));
        int beginDateM = Integer.parseInt(format(beginDate, "MM", "yyyy-MM-dd"));
        int beginDateD = Integer.parseInt(format(beginDate, "dd", "yyyy-MM-dd"));

        int mS = (endDateY - beginDateY) * 12 + endDateM - beginDateM;
        int y = mS / 12;
        int m = mS % 12;

        int d = (int) DateUtil.getDayBetween(getStandardDate(endDateY, endDateM, endDateD), getStandardDate(endDateY, endDateM, beginDateD));
        if (d < 0) {
            m -= 1;
            d += getMonthLastDay(endDateY, endDateM - 1);
        }
        if (m < 0) {
            y -= 1;
            m += 12;
        }
        return new int[]{y, m, d};
    }

    /**
     * ȡ��ָ���µ�����
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//����������Ϊ���µ�һ��
        a.roll(Calendar.DATE, -1);//���ڻع�һ�죬Ҳ�������һ��
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * ָ��ʱ�䷵��ָ����ʽ��Ҫ������ָ��ʱ��ĸ�ʽ
     * �� time: 20120416 Ҫ���� 2012�� ��ô�ĸ�ʽ���� yyyyMMdd
     * �� time: 2012-04-16 Ҫ���� 2012�� ��ô�ĸ�ʽ���� yyyy-MM-dd
     *
     * @param time          2012-04-16
     * @param formatStr     yyyy  ���ظ�ʽ
     * @param timeformatStr yyyy-MM-dd   �����ʽ
     * @return
     */
    public static String format(String time, String formatStr, String timeformatStr) {
        if (StrUtil.isNull(time)) {
            return null;
        }
        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            date = format.format(new Timestamp(new SimpleDateFormat(timeformatStr).parse(time).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return date;
    }


    /**
     * �ж�ʱ���Ƿ���ȷ��ʽ
     *
     * @param date
     * @param format
     * @return
     */
    public static boolean checkDate(String date, String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = df.parse(date);
        } catch (Exception e) {
            //�����ת��,�϶��Ǵ����ʽ
            return false;
        }
        String s1 = df.format(d);
        // ת�����������ת����String,����,�߼�����.��formatΪ"yyyy-MM-dd",dateΪ
        // "2006-02-31",ת��Ϊ���ں���ת�����ַ�Ϊ"2006-03-03",˵����ʽ��Ȼ��,������
        // �߼��ϲ���.
        return date.equals(s1);
    }


    /**
     * ʱ���ʽ HH:mm
     * �ж�ʱ���Ƿ���һ��ʱ������ڣ��ڷ���0���ڿ�ʼ֮ǰ����-1�� �ڿ�ʼ֮�󷵻�1
     *
     * @param start
     * @param end
     * @param curr
     * @return
     */
    public static int isInBeginAndEndTime(String start, String end, String curr) {
        int a = curr.compareTo(start);
        if (a < 0) {
            return -1;
        }
        int b = curr.compareTo(end);
        if (b > 0) {
            return 1;
        }

        return 0;

    }

    public static String getStartAndEndTime(String time) {
        String split = " ";
        String split1 = "~";
        String split2 = "-";

        if (StrUtil.isNull(time)) {
            return null;
        }
        String times[] = time.split(split);

        if (times.length > 1) {


            String t[] = times[1].split(split1);
            if (t.length <= 1) {
                t = times[1].split(split2);
            }

            if (t.length > 1) {
                return t[0] + "-" + t[1];
            }

        }

        return null;
    }

    /**
     * 得到X天之前的时间
     *
     * @return
     */
    public static String getXDaysBefore(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) - num);
        Date date = curr.getTime();
        java.text.DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return format2.format(date);
    }

    /**
     * 得到X月之前的时间
     *
     * @return
     */
    public static String getXMonthsBefore(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)-num);
        Date date = curr.getTime();
        java.text.DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return format2.format(date);
    }

    /**
     * 得到X年之前的时间
     *
     * @return
     */
    public static String getXYearsBefore(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)-num);
        Date date = curr.getTime();
        java.text.DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return format2.format(date);
    }


    public static void main(String[] args) {

    }


}
