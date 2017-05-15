package CBSsportSpider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetPlayByPlayData {
	public static void main(String[] args) throws IOException {
		String directoryPath = "F:/";
		FileWriter fr = new FileWriter(directoryPath + "pbp.txt");

		String urlBasic = "http://www.espn.com/nba/playbyplay?gameId=";
		String strList = "400828156";
		String url = urlBasic + strList;
		Connection conn = Jsoup.connect(url);
		ArrayList<String> zhan_time = new ArrayList<>();
		ArrayList<String> zhan_score = new ArrayList<>();
		int count0 = 0;
		int count1 = 0;
		Document doc = conn.get();
		Elements links = doc.select("li[class=accordion-item]");
		for (Element link : links) {
			CBSpbpData data = new CBSpbpData();
			if (count0++ > 0) {
				// 两个先处理进list
				Elements times = link.select("td[class=time-stamp]");
				for (Element time : times) {
					zhan_time.add(time.html().toString());
				}
				Elements scores = link.select("td[class~=^combined-score]");
				for (Element score : scores) {
					zhan_score.add(score.html().toString());
				}

				Elements plays = link.select("td[class=game-details]");
				for (Element play : plays) {
					if (count1 < zhan_time.size()) {
						String playDetail = play.html().toString();
						ModifyThePBPDetails test = new ModifyThePBPDetails();
						data = test.modify(playDetail);
						System.out.println(count1 + "|" + zhan_time.get(count1) + "|" + zhan_score.get(count1) + "|"
								+ playDetail + "|" + data.getPlay());
						// 关于投篮的data
						if (playDetail.contains("misses") || playDetail.contains("makes")
								|| playDetail.contains("blocks")) {
							fr.append(count1 + "|" + zhan_time.get(count1) + "|" + zhan_score.get(count1) + "|"
									+ playDetail + "|" + data.getPlay() + "\r\n");
						}
						count1++;
					}
				}
			}
		}
		fr.close();
	}
}
