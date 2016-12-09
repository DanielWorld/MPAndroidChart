package com.github.mikephil.charting.utils.date;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-02.
 */

public class ChartDateUtil {

	/**
	 * 현재 ?월 인지 알려줌
	 * @return
	 */
	public static int getCurrentMonthOfYear() {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(System.currentTimeMillis());
		// Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * yyyy 년 MM 월 dd 일 을 milliseconds 로 바꿔줌
	 * @return
	 */
	public static long getMillisFromDate(int year, int month, int day) {
		// Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.set(year, month - 1, day);
		return calendar.getTimeInMillis();
	}

	/**
	 * yyyy 년 MM 월 을 milliseconds 로 바꿔줌
	 * @param year
	 * @param month
	 * @return
	 */
	public static long getMillisFromDate(int year, int month) {
		// Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, year);
		// Month 의 경우 Month = 0 이 1월이다.
		calendar.set(Calendar.MONTH, month - 1);
		return calendar.getTimeInMillis();
	}

	/**
	 * 해당 millis 의 Year 가져오기
	 * @param milliseconds
	 * @return
	 */
	public static int getYear(long milliseconds){
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 해당 millis 의 month 가져오기 (1월 = 1)
	 * @param milliseconds
	 * @return
	 */
	public static int getMonthOfYear(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		// Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 해당 millis 의 day 가져오기
	 * @param milliseconds
	 * @return
	 */
	public static int getDayOfMonth(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 해당 millis 의 Year of Day 가져오기
	 * @param milliseconds
	 * @return
	 */
	public static int getDayOfYear(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 해당 millis 의 Actual maximum year of Day 가져오기
	 * @param milliseconds
	 * @return
	 */
	public static int getDaysOfYearMaximum(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 해당 millis 의 Actual maximum month of Day 가져오기
	 * @param milliseconds
	 * @return
	 */
	public static int getDaysOfMonthMaximum(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 해당 millis 에서 months 만큼 추가한 날을 millis 로 재반환
	 * @param milliseconds
	 * @param months 추가할 month 갯수 <b>- 값이면 반대로 제거</b>
	 * @return new milliseconds
	 */
	public static long addMonths(long milliseconds, int months) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTimeInMillis();
	}

	/**
	 * 해당 millis 에서 days 만큼 추가한 날을 millis 로 재반환
	 * @param milliseconds
	 * @param days 추가할 day 갯수 <b>- 값이면 반대로 제거</b>
	 * @return new milliseconds
	 */
	public static long addDays(long milliseconds, int days) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTimeInMillis();
	}

	/**
	 * 두 milliseconds 사이의 month 갯수 구하기
	 * @param startTime
	 * @param endTime
	 * @param includeTarget startTime 과 endTime 의 month 포함 여부
	 * @return
	 */
	public static int getMonthCount(long startTime, long endTime, boolean includeTarget) {
		int startYear = getYear(startTime);
		int startMonth = getMonthOfYear(startTime);

		int endYear = getYear(endTime);
		int endMonth = getMonthOfYear(endTime);

		if (startMonth <= endMonth) {
			return (endYear - startYear) * 12 + endMonth - startMonth + (includeTarget ? 1 : 0);
		}
		else {
			// startMonth 가 endMonth 보다 클 경우
			// 1. endTime 의 Year 에서 startTime 의 Year 를 뺀 뒤, 그 값 * 12 해서 endMonth 에 붙인다.
			// 2. 그 변형된 endMonth - startMonth 하면 됨
			endMonth += (endYear - startYear) * 12;
			return endMonth - startMonth + (includeTarget ? 1 : 0);
		}
	}

	/**
	 * year 와 month 를 이용해서 month 기준의 index 를 만든다.
	 * <p>Chart 기준의 month 계산에 사용되는 method() </p>
	 * @return
	 */
	public static int getDanielMonthIndex(long milliseconds) {
		int standardYear = 1700;

		int targetYear = getYear(milliseconds);
		int targetMonth = getMonthOfYear(milliseconds);

		return (targetYear - standardYear) * 12 + targetMonth - 1;
	}

	/**
	 * {@link ChartDateUtil#getDanielMonthIndex(long)} 를 통해서 만든 index 를 milliseconds 로 변환
	 * @param monthIndex
	 * @return
	 */
	public static long getTimeFromDanielMonthIndex(int monthIndex) {
		int standardYear = 1700;
		int getYear = monthIndex / 12;
		int getMonth = monthIndex % 12;

		return ChartDateUtil.getMillisFromDate(standardYear + getYear, getMonth + 1);
	}
}
