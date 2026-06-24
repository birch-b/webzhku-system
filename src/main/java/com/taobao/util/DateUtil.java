package com.taobao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 * 提供常用的日期格式化和解析方法
 * 
 * @author taobao
 * @version 1.0
 */
public class DateUtil {

    /** 日期格式: yyyy-MM-dd */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /** 时间格式: HH:mm:ss */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /** 日期时间格式: yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** 时间戳格式: yyyyMMddHHmmss */
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    
    /** 中文日期格式: yyyy年MM月dd日 */
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    
    /** 中文日期时间格式: yyyy年MM月dd日 HH:mm:ss */
    public static final String DATETIME_FORMAT_CN = "yyyy年MM月dd日 HH:mm:ss";

    // ==================== 格式化方法 ====================

    /**
     * 格式化日期为 yyyy-MM-dd
     * @param date Date对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * 格式化时间为 HH:mm:ss
     * @param date Date对象
     * @return 格式化后的时间字符串
     */
    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }

    /**
     * 格式化日期时间为 yyyy-MM-dd HH:mm:ss
     * @param date Date对象
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }

    /**
     * 格式化日期时间为 yyyy年MM月dd日 HH:mm:ss
     * @param date Date对象
     * @return 格式化后的中文日期时间字符串
     */
    public static String formatDateTimeCN(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_CN);
        return sdf.format(date);
    }

    /**
     * 格式化日期为指定格式
     * @param date Date对象
     * @param pattern 日期格式模式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取时间戳字符串
     * @param date Date对象
     * @return yyyyMMddHHmmss格式的时间戳
     */
    public static String formatTimestamp(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
        return sdf.format(date);
    }

    // ==================== 解析方法 ====================

    /**
     * 解析日期字符串为Date对象
     * @param dateStr 日期字符串(yyyy-MM-dd)
     * @return Date对象
     * @throws ParseException
     */
    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.parse(dateStr);
    }

    /**
     * 解析日期时间字符串为Date对象
     * @param dateTimeStr 日期时间字符串(yyyy-MM-dd HH:mm:ss)
     * @return Date对象
     * @throws ParseException
     */
    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.parse(dateTimeStr);
    }

    /**
     * 解析日期字符串为Date对象(自动识别格式)
     * @param dateStr 日期字符串
     * @param pattern 日期格式模式
     * @return Date对象
     * @throws ParseException
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateStr);
    }

    // ==================== 获取当前时间 ====================

    /**
     * 获取当前日期
     * @return 当前日期(yyyy-MM-dd)
     */
    public static String getCurrentDate() {
        return formatDate(new Date());
    }

    /**
     * 获取当前时间
     * @return 当前时间(HH:mm:ss)
     */
    public static String getCurrentTime() {
        return formatTime(new Date());
    }

    /**
     * 获取当前日期时间
     * @return 当前日期时间(yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        return formatDateTime(new Date());
    }

    /**
     * 获取当前时间戳
     * @return 当前时间戳字符串
     */
    public static String getCurrentTimestamp() {
        return formatTimestamp(new Date());
    }

    /**
     * 获取当前Date对象
     * @return Date对象
     */
    public static Date getNow() {
        return new Date();
    }

    // ==================== 日期计算 ====================

    /**
     * 日期加减天数
     * @param date 原始日期
     * @param days 要加减的天数(正数为加，负数为减)
     * @return 计算后的日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate result = localDate.plusDays(days);
        return Date.from(result.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 日期加减小时
     * @param date 原始日期
     * @param hours 要加减的小时数
     * @return 计算后的日期
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime result = localDateTime.plusHours(hours);
        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 日期加减分钟
     * @param date 原始日期
     * @param minutes 要加减的分钟数
     * @return 计算后的日期
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime result = localDateTime.plusMinutes(minutes);
        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个日期之间的天数差
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    public static long daysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(start, end);
    }

    // ==================== 日期比较 ====================

    /**
     * 判断日期是否为今天
     * @param date 要判断的日期
     * @return 是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        LocalDate target = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        return target.equals(today);
    }

    /**
     * 判断日期是否在指定日期之前
     * @param date 要判断的日期
     * @param compareDate 比较日期
     * @return 是否在之前
     */
    public static boolean isBefore(Date date, Date compareDate) {
        if (date == null || compareDate == null) {
            return false;
        }
        return date.before(compareDate);
    }

    /**
     * 判断日期是否在指定日期之后
     * @param date 要判断的日期
     * @param compareDate 比较日期
     * @return 是否在之后
     */
    public static boolean isAfter(Date date, Date compareDate) {
        if (date == null || compareDate == null) {
            return false;
        }
        return date.after(compareDate);
    }

    /**
     * 获取日期的开始时间(00:00:00)
     * @param date 日期
     * @return 当天开始时间
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取日期的结束时间(23:59:59)
     * @param date 日期
     * @return 当天结束时间
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取相对时间描述(如：刚刚、5分钟前、2小时前等)
     * @param date 要描述的日期
     * @return 相对时间描述
     */
    public static String getRelativeTime(Date date) {
        if (date == null) {
            return "";
        }
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        
        if (diff < 0) {
            return "未来";
        }
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (seconds < 60) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            return formatDate(date);
        }
    }
}
