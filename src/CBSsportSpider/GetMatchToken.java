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
		// ����ѭ��
		ArrayList<String> dateList = new ArrayList<>();
		Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-11-02");// ������ʼ����
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-11-03");// �����������
		Calendar dd = Calendar.getInstance();// ��������ʵ��
		dd.setTime(d1);// ����������ʼʱ��
		while (dd.getTime().before(d2)) {// �ж��Ƿ񵽽�������
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(dd.getTime());
			// System.out.println(str.replace("-", ""));// ������ڽ��
			dateList.add(str.replace("-", ""));
			dd.add(Calendar.DATE, 1);// ���е�ǰ�����·ݼ�1
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
