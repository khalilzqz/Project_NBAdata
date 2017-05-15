package CBSsportSpider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class GetMatchToken {
	public static void main(String[] args) throws IOException, ParseException {
		// 日期循环
		ArrayList<String> dateList = new ArrayList<>();
		Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-11-02");// 定义起始日期
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-11-03");// 定义结束日期
		Calendar dd = Calendar.getInstance();// 定义日期实例
		dd.setTime(d1);// 设置日期起始时间
		while (dd.getTime().before(d2)) {// 判断是否到结束日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(dd.getTime());
			// System.out.println(str.replace("-", ""));// 输出日期结果
			dateList.add(str.replace("-", ""));
			dd.add(Calendar.DATE, 1);// 进行当前日期月份加1
		}
		String urlBasic = "http://www.cbssports.com/nba/scoreboard/";
		for (String strList : dateList) {
			String url = urlBasic + strList;
			Connection conn = Jsoup.connect(url);
			Document doc = conn.get();
			Elements links = doc.select("table[class=scoreLinks]");
			for (Element link : links) {
				String[] str = link.toString().split("/");
				String[] str1 = str[4].split("\"");
				System.out.println(str1[0]);
			}
		}
	}
}
