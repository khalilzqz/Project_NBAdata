package CBSsportSpider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String[] args) throws IOException, ParseException {
		String gameKey = "NBA_20161221_MEM@DET";
		ArrayList<String> CBSShotData = new ArrayList<>();
		String url_cbs = "http://www.cbssports.com/nba/gametracker/shotchart/" + gameKey;
		Connection conn_cbs = Jsoup.connect(url_cbs).timeout(100000);
		String pageContent = conn_cbs.get().toString();

		HashMap<String, String> WanZhengName = new HashMap<>();
		HashMap<String, String> BuFenName = new HashMap<>();

		// shotData
		int cur = pageContent.indexOf("currentShotData = new String");
		int lcur = pageContent.indexOf("\"", cur);
		int rcur = pageContent.indexOf("\"", lcur + 1);
		String shotData = gameKey + "," + pageContent.substring(lcur + 1, rcur).replaceAll("~", "\r\n" + gameKey + ",");

		// TimeChange
		/*
		 * 默认情况下，CBS的period数据中的第4节和加时赛都是3，本段将其依次改为4，5，6……
		 * 20101026HOULAL,0,5.0,3,1622542,1,0,25,40,25
		 * 20101026HOULAL,0,11:41,3,1622542,5,1,0,42,0 period >=
		 * 3,同一gameID，当前一条shot时间为秒“.”，下一条包含分“:”时，period++
		 */
		String[] zhan = shotData.split("\r\n");
		for (int i = 0; i < zhan.length; i++) {
			// 比较去重
			String[] lastShot = new String[] { "", "", "", "", "", "", "", "", "", "" };
			String[] newShot = zhan[i].split(",");
			if (lastShot[0].equals(newShot[0]) && lastShot[3].compareTo("3") >= 0 && lastShot[2].contains(".")
					&& newShot[2].contains(":")) {
				Integer tmp = Integer.parseInt(lastShot[3]) + 1;
				newShot[3] = tmp.toString();
			}
			if (lastShot[0].equals(newShot[0]) && newShot[3].compareTo(lastShot[3]) < 0)
				newShot[3] = lastShot[3];
			lastShot = newShot;
			String aShot = lastShot[0] + "," + lastShot[1] + "," + lastShot[2] + "," + lastShot[3] + "," + lastShot[4]
					+ "," + lastShot[5] + "," + lastShot[6] + "," + lastShot[7] + "," + lastShot[8] + "," + lastShot[9];
			if (!lastShot[4].equals("0")) {
				if (!CBSShotData.contains(aShot)) {
					CBSShotData.add(aShot);
				}
			}
		}

		// 名单list：
		// wanzheng:Name,id+position
		// bufen:Scurry,id+postion
		cur = pageContent.indexOf("playerDataHomeString = new String", rcur);
		lcur = pageContent.indexOf("\"", cur);
		rcur = pageContent.indexOf("\"", lcur + 1);
		String homePlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-").replace("&#039;", "'");
		String[] homezhan = homePlayers.split("\\|");
		for (int i = 0; i < homezhan.length; i++) {
			String out = SplitNameAndID(homezhan[i]);
			String[] homezhan1 = out.split("\\|");
			if (homezhan1.length > 2) {
				WanZhengName.put(homezhan1[1], homezhan1[0] + "|" + homezhan1[2]);
				String[] name1 = homezhan1[1].split("-");
				if (name1.length > 2) {
					BuFenName.put(name1[0].subSequence(0, 1) + name1[name1.length - 1],
							homezhan1[0] + "|" + homezhan1[2]);
				}
			}
		}

		cur = pageContent.indexOf("playerDataAwayString = new String", rcur);
		lcur = pageContent.indexOf("\"", cur);
		rcur = pageContent.indexOf("\"", lcur + 1);
		String awayPlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-").replace("&#039;", "'");
		String[] awayzhan = awayPlayers.split("\\|");
		for (int i = 0; i < awayzhan.length; i++) {
			String out = SplitNameAndID(awayzhan[i]);
			String[] awayzhan1 = out.split("\\|");
			if (awayzhan1.length > 2) {
				WanZhengName.put(awayzhan1[1], awayzhan1[0] + "|" + awayzhan1[2]);
				String[] name1 = awayzhan1[1].split("\\-");
				if (name1.length > 1) {
					System.out.println(name1[0].subSequence(0, 1) + name1[name1.length - 1]);
					BuFenName.put(name1[0].subSequence(0, 1) + name1[name1.length - 1],
							awayzhan1[0] + "|" + awayzhan1[2]);
				}
			}
		}

		System.out.println(homePlayers);
		System.out.println(awayPlayers);

		@SuppressWarnings("rawtypes")
		Iterator iter1 = BuFenName.entrySet().iterator();
		while (iter1.hasNext()) {
			@SuppressWarnings("rawtypes")
			HashMap.Entry entry1 = (HashMap.Entry) iter1.next();
			System.out.println(entry1.getKey() + "|" + entry1.getValue());
		}
	}

	public static String SplitNameAndID(String str) {
		String[] zhan0 = str.split("\\|");
		String out = "";
		for (int i = 0; i < zhan0.length; i++) {
			String[] zhan1 = zhan0[i].split("\\:");
			out = zhan1[0];
			if (zhan1.length > 1) {
				String[] zhan2 = zhan1[1].split("\\,");
				if (zhan2.length > 2) {
					out = out + "|" + zhan2[0] + "|" + zhan2[2];
				}
			}
		}
		return out;
	}
}
