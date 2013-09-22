/**
 *
 * LiChenxing
 * 2013-9-18 下午4:27:12
 */
package com.ruyicai.actioncenter.util;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author LiChenxing
 * @date 2013-9-18 下午4:27:12
 */
public class HtmlFilter {
	
	private static Logger logger = LoggerFactory.getLogger(HtmlFilter.class);
	
	public static String Html2Text(String inputString) { 
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
   
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
																										 
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
																								 
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			logger.error("Html2Text: " + e.getMessage(), e);
		}

		return textStr;// 返回文本字符串
	}

	public static void main(String[] args) {
		String text = "<html><script>var d = 10;function aa() { alert(d);}</script><style>a:hour: {color: blue}</style><body><p>test My name is LiChenxing. I am glade to meet you!" +
				"nbsp;nbsp;</p></body>test20xE6, 0xB1, 0x89, 0xE5, 0xAD, 0x97 1760-177F</html>";
		System.out.println(Html2Text(text));
	}

}
