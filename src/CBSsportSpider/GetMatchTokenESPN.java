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
public class GetMatchTokenESPN {
	public static void main(String[] args) throws IOException, ParseException {
		// 日期循环
		ArrayList<String> dateList = new ArrayList<>();
		Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-07");// 定义起始日期
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-08");// 定义结束日期
		Calendar dd = Calendar.getInstance();// 定义日期实例
		dd.setTime(d1);// 设置日期起始时间
		while (dd.getTime().before(d2)) {// 判断是否到结束日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(dd.getTime());
			// System.out.println(str.replace("-", ""));// 输出日期结果
			dateList.add(str.replace("-", ""));
			dd.add(Calendar.DATE, 1);// 进行当前日期月份加1
		}
		String urlBasic = "http://www.espn.com/nba/schedule/_/date/";
		for (String strList : dateList) {
			String url = urlBasic + strList;
			Connection conn = Jsoup.connect(url).timeout(100000);
			Document doc = conn.get();
			Elements links = doc.select("div[class=responsive-table-wrap]");
			ArrayList<String> zhanList = new ArrayList<>();
			int count = 0;
			int count_0 = 0;
			int count_1 = 0;
			int flag = 0;
			String zhan = "";
			// 需要生成NBA_20161225_GS@CLE
			for (Element link : links) {
				if (count_0++ < 1) {
					Elements teams = link.select("a[class=team-name]");
					for (Element team : teams) {
						if (flag == 0) {
							zhan = team.toString().split("/")[5].toUpperCase();
							flag++;
						} else if (flag == 1) {
							zhanList.add(
									"NBA_" + strList + "_" + zhan + "@" + team.toString().split("/")[5].toUpperCase());
							flag = 0;
						}
					}
				}

				if (count++ < 1) {
					Elements match = link.select("a[name=&lpos=nba:schedule:score]");
					for (Element sin : match) {
						String[] str = sin.attr("href").toString().split("/");
						String[] str1 = str[2].split("=");
						System.out.println(str1[1] + "|" + zhanList.get(count_1++));
					}
				}
			}
		}

	}
}
