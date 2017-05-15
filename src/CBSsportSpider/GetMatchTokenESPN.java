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
		// ����ѭ��
		ArrayList<String> dateList = new ArrayList<>();
		Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-07");// ������ʼ����
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-08");// �����������
		Calendar dd = Calendar.getInstance();// ��������ʵ��
		dd.setTime(d1);// ����������ʼʱ��
		while (dd.getTime().before(d2)) {// �ж��Ƿ񵽽�������
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(dd.getTime());
			// System.out.println(str.replace("-", ""));// ������ڽ��
			dateList.add(str.replace("-", ""));
			dd.add(Calendar.DATE, 1);// ���е�ǰ�����·ݼ�1
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
			// ��Ҫ����NBA_20161225_GS@CLE
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
