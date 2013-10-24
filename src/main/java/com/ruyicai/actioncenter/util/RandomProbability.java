package com.ruyicai.actioncenter.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import com.ruyicai.actioncenter.domain.PrizeInfo;

/**
 * 根据随机概率获取奖品信息.
 * @author hzf
 * @version 1.0v 2013-10-22
 */
public class RandomProbability {

	/**
	 * 获取随机概率在概率数组中的位置(即奖品在list中的位置)
	 * @param piList 奖品列表
	 * @return 奖品在列表中的位置
	 */
	public static int getPrizeRandomPosition(List<PrizeInfo> piList)
	{
		// list 已按概率升序排序
		int[] proArr = new int[piList.size()]; // 概率数组
		double remainSum = 0; // 剩余奖品总数
		for(int i=0;i<piList.size();i++)
		{
			proArr[i] = piList.get(i).getAriseProbability();
			remainSum += piList.get(i).getRemainNum();
		}
		int sumProbability = getSumInt(proArr); // 概率总和
		int resultPosition = -1;
		while(resultPosition < 0)
		{
			resultPosition = getPositionByRandomProbability(proArr, sumProbability, remainSum, piList);
		}

		return resultPosition;
	}

	/**
	 * 根据随机概率返回奖品在list上的位置.
	 * 
	 * @param proArr
	 * @param sumProbability
	 * @param remainSum
	 * @param piList
	 * @return
	 */
	public static int getPositionByRandomProbability(int[] proArr, int sumProbability, double remainSum, List<PrizeInfo> piList)
	{
		int resultPosition = -1;
		for(int i=0;i<proArr.length;i++)
		{
			int randomPro = getRandomPro(1, sumProbability);
			// 奖品发生概率与其延迟率共同影响结果
			if(randomPro < proArr[i])   // 小于其概率
			{
				double remainNum = piList.get(i).getRemainNum();
				double currentDelayPro = div(remainNum, remainSum, 10);
				String delayPro = piList.get(i).getDelayProbability();
				if(Double.compare(currentDelayPro, Double.valueOf(delayPro)) >= 0) // 大于或等于其延迟率
				{
					resultPosition = i;
					break;
				}
			}else
			{
				sumProbability -= proArr[i];
			}
		}

		return resultPosition;
	}

	/**
	 * 返回整型数组值总和.
	 * @param arry
	 * @return
	 */
	public static int getSumInt(int[] arry)
	{
		int sumValue = 0;
		for(int i=0; i<arry.length; i++)
		{
			sumValue += arry[i];
		}
		return sumValue;
	}

	/**
	 * 在最值区间产生随机概率
	 * @param minProbability
	 * @param maxProbability
	 * @return
	 */
	public static int getRandomPro(int minProbability, int maxProbability)
	{
		int r = new Random().nextInt(maxProbability - minProbability);
		return minProbability + r;
	}

	/** 
	 * 两Double数相除.
	 * 
	 * @param dividend 
	 *            被除数 
	 * @param divisor 
	 *            除数 
	 * @param scale 
	 *            表示需要精确到小数点以后几位。 
	 * @return 两个参数的商
	 */
	public static Double div(Double dividend, Double divisor, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(dividend));
		BigDecimal b2 = new BigDecimal(Double.toString(divisor)); 
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
