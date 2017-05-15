package Lottery;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainSpider {
	public static void main(String[] args) throws IOException, ParseException {
		String TimeToday = "2017-04-28/";
		String urlMain = "http://www.okooo.com/jingcailanqiu/hunhe/" + TimeToday;

		String urlBasic = "http://www.okooo.com";
		String urlHandicap = "";
		String urlHistory = "";
		HashMap<Integer, String> hm_away_history = new HashMap<>();
		HashMap<Integer, String> hm_home_history = new HashMap<>();
		HashMap<Integer, String> hm_ha_history = new HashMap<>();
		Connection conn = Jsoup.connect(urlMain).timeout(10000);
		Document doc = conn.get();

		Elements links_handicap = doc.select("a[onClick=_gaq.push(['_trackEvent', '篮彩混合过关投注页','数据链接', '欧']);]");
		Elements links_history = doc.select("a[onClick=_gaq.push(['_trackEvent', '篮彩混合过关投注页','数据链接', '析']);]");
		for (Element link : links_handicap) {
			urlHandicap = urlBasic + link.attr("href");
			System.out.println(urlHandicap);
		}
		System.out.println("==================");
		for (Element link : links_history) {
			// match参数
			String home_ave_pts = "";
			String away_ave_pts = "";
			String away_away_result = "";
			String home_home_result = "";
			String matchId = "";
			String matchTime = "";

			// 循环history
			urlHistory = urlBasic + link.attr("href");
			// id
			String[] cur = urlHistory.split("/");
			if (cur.length > 5) {
				matchId = cur[5];
			}

			System.out.println(urlHistory);
			Connection conn1 = Jsoup.connect(urlHistory).timeout(10000);
			Document doc1 = conn1.get();
			// 一场比赛
			// title
			System.out.println(doc1.title());

			// 比赛基本赔率
			Elements links_detail = doc1.select("[class=topjfbg][attr=event]");
			int count_basic = 0;
			String odds = "";
			for (Element link_history : links_detail) {
				if (count_basic++ == 0) {
					odds = link_history.text();
				}
			}
			System.out.println(odds);
			String[] oddsplit = odds.split("\\s+");
			if (oddsplit.length > 3) {
				matchTime = oddsplit[1];
			}

			// 时间基本
			// Elements links_time = doc1.select("div[class=qbx]");
			// for (Element link_time : links_time) {
			// System.out.println(link_time.text());
			// }

			// 两队基本
			Elements links_match = doc1.select("div[class=shuj]");
			for (Element link_match : links_match) {
				System.out.println(link_match.select("span[class=blacktxt]").text());
			}

			// 历史100场+当前一场
			Elements home_historys1 = doc1.select("tr[class=trBgBlue]");
			Elements home_historys2 = doc1.select("tr[class= ]");
			int count_history_match = 1;
			// homehistory
			for (Element home_history : home_historys1) {
				if (count_history_match <= 100) {
					hm_away_history.put(count_history_match, home_history.text());
				}
				count_history_match += 2;
			}
			count_history_match = 2;
			for (Element home_history : home_historys2) {
				if (count_history_match <= 100) {
					hm_away_history.put(count_history_match, home_history.text());
				}
				count_history_match += 2;
			}
			// Iterator iter = hm_away_history.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry entry = (Map.Entry) iter.next();
			// Object key = entry.getKey();
			// Object val = entry.getValue();
			// System.out.println(key + "|" + val);
			// }

			System.out.println("==================");
			// awayhistory
			count_history_match = 2;
			for (Element home_history : home_historys1) {
				if (count_history_match > 101 && count_history_match <= 201) {
					hm_home_history.put(count_history_match - 101, home_history.text());
				}
				count_history_match += 2;
			}
			count_history_match = 3;
			for (Element home_history : home_historys2) {
				if (count_history_match > 101 && count_history_match <= 201) {
					hm_home_history.put(count_history_match - 101, home_history.text());
				}
				count_history_match += 2;
			}
			// iter = hm_home_history.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry entry = (Map.Entry) iter.next();
			// Object key = entry.getKey();
			// Object val = entry.getValue();
			// System.out.println(key + "|" + val);
			// }

			System.out.println("==================");
			// hahistory
			count_history_match = 2;
			for (Element home_history : home_historys1) {
				if (count_history_match > 201) {
					hm_ha_history.put(count_history_match - 201, home_history.text());
				}
				count_history_match += 2;
			}
			count_history_match = 3;
			for (Element home_history : home_historys2) {
				if (count_history_match > 201) {
					hm_ha_history.put(count_history_match - 201, home_history.text());
				}
				count_history_match += 2;
			}
			// iter = hm_ha_history.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry entry = (Map.Entry) iter.next();
			// Object key = entry.getKey();
			// Object val = entry.getValue();
			// System.out.println(key + "|" + val);
			// }

			// bet365盘口变化是真的？
			// 让分变化ou/ah
			String urlBet365_dx = "http://www.okooo.com/basketball/match/" + matchId + "/ou/change/27/";
			Connection conn2_dx = Jsoup.connect(urlBet365_dx).timeout(10000);
			Document doc2_dx = conn2_dx.get();
			Elements handicaps_dx = doc2_dx.select("table[width=450]");
			String handicapDX_dx = "";
			for (Element handicap_dx : handicaps_dx) {
				handicapDX_dx = handicap_dx.select("tr[class= ]").text();
				// bright/空格
				// System.out.println(handicapDX_dx);
			}
			String[] handicapSpilt_dx = handicapDX_dx.split("2017");
			float lastHandicap_dx = 0f;
			float firstHandicap_dx = 0f;
			int countsw_dx = 0;
			for (int i = 0; i < handicapSpilt_dx.length; i++) {
				String[] handicapDetail_dx = handicapSpilt_dx[i].split("/|\\s+");
				String timeNow_dx = "";
				String scoreDX_dx = "";
				if (handicapDetail_dx.length > 2) {
					timeNow_dx = "2017-" + handicapDetail_dx[1] + "-" + handicapDetail_dx[2];
					scoreDX_dx = handicapDetail_dx[6];
				}
				long lastDay_dx = dateToStamp(matchTime);
				if (!timeNow_dx.equals("")) {
					if (lastDay_dx - 86400000 >= dateToStamp(timeNow_dx)) {
						if (countsw_dx++ == 0) {
							lastHandicap_dx = Float.valueOf(scoreDX_dx);
						}
						System.out.println(timeNow_dx);
						System.out.println(scoreDX_dx);
						firstHandicap_dx = Float.valueOf(scoreDX_dx);
					}
				}
			}

			// 输出初末盘口比较
			System.out.println("===========");
			System.out.println(odds);
			System.out.println("初盘:" + firstHandicap_dx + "|末盘：" + lastHandicap_dx);

			System.out.println("============================================");

			// 让分变化ou/ah
			String urlBet365 = "http://www.okooo.com/basketball/match/" + matchId + "/ah/change/27/";
			Connection conn2 = Jsoup.connect(urlBet365).timeout(10000);
			Document doc2 = conn2.get();
			Elements handicaps = doc2.select("table[width=450]");
			String handicapDX = "";
			for (Element handicap : handicaps) {
				handicapDX = handicap.select("tr[class=bright]").text();
				// bright/空格
				// System.out.println(handicapDX);
			}
			String[] handicapSpilt = handicapDX.split("2017");
			float lastHandicap = 0f;
			float firstHandicap = 0f;
			int countsw = 0;
			for (int i = 0; i < handicapSpilt.length; i++) {
				String[] handicapDetail = handicapSpilt[i].split("/|\\s+");
				String timeNow = "";
				String scoreDX = "";
				if (handicapDetail.length > 2) {
					timeNow = "2017-" + handicapDetail[1] + "-" + handicapDetail[2];
					scoreDX = handicapDetail[6];
				}
				long lastDay = dateToStamp(matchTime);
				if (!timeNow.equals("")) {
					if (lastDay - 86400000 >= dateToStamp(timeNow)) {
						if (countsw++ == 0) {
							lastHandicap = Float.valueOf(scoreDX);
						}
						System.out.println(timeNow);
						System.out.println(scoreDX);
						firstHandicap = Float.valueOf(scoreDX);
					}
				}
			}

			// 输出初末盘口比较
			System.out.println("===========");
			System.out.println(odds);
			System.out.println("初盘:" + firstHandicap + "|末盘：" + lastHandicap);

		}
	}

	public static long dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		return ts;
	}
}
