package com.ruyicai.actioncenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	private static Date date_1000 = null;
	
	static {
		try {
			date_1000 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			.parse("1000-01-01 00:00:00");
		} catch (ParseException e) {
		}
	}

	public static Date get1000Date() {
		return date_1000;
	}

	public static Date parse(String timeStr) {
		return parse("yyyy-MM-dd HH:mm:ss", timeStr);
	}

	public static Date parse(String pattern, String timeStr) {
		try {
			return new SimpleDateFormat(pattern).parse(timeStr);
		} catch (ParseException e) {
		}
		return null;
	}

	public static String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	public static String format(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 取得下一个更新统计缓存时间，即明日0点
	 * @return
	 */
	public static Date nextTaskTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
	
	/**
	 * 取得当前月份
	 * @return
	 */
	public static Date getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	/**
	 * 取得当日0点
	 * @return
	 */
	public static Date getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取某个时间0点
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateTheZero(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 如果没有这种设定的话回去系统的当期的时间
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date 日期
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}
	
	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date 日期
	 * @param dateType 类型
	 * @param amount 数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(dateType, amount);
		return calendar.getTime();
	}
}
