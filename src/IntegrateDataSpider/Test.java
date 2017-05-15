package IntegrateDataSpider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi.ecCVCDSA;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import CBSsportSpider.CBSpbpData;
import CBSsportSpider.ModifyThePBPDetails;

public class Test {
	public static void main(String[] args) throws IOException, ParseException {
		String urlBasic = "http://www.espn.com/nba/boxscore?gameId=";
		String strList = "271215018";
		String url = urlBasic + strList;
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		Elements links = doc.select("div[id=custom-nav]");
		for (Element link : links) {
			Elements teams = link.select("span[class=abbrev]");
			for (Element team : teams) {
				System.out.println(team.text());
			}
		}
		String str = "as|dawa";
		System.out.println(str.substring(str.indexOf("|") + 1));
	}

	public static String ProcessPopName(String str) {
		String[] names_pop = str.split("<br>|</p>|Period|<span");
		String Period = "";
		String out_pop = "";
		if (names_pop.length == 10) {
			Period = "Period " + names_pop[1].trim();
			for (int i = 4; i < 9; i++) {
				String process = names_pop[i].replace("IV", "").replace("III", "").replace("II", "").replace("Jr.", "")
						.replace("Jr", "").replace("?", "").trim();
				String[] names = process.split("\\s+|\\-|\\'|\\?");
				if (names.length > 1) {
					if (!names[0].equals("")) {
						out_pop = out_pop + (names[0].subSequence(0, 1) + names[names.length - 1]) + "|";
					} else {
						out_pop = out_pop + names[1] + "|";
					}
				} else {
					out_pop = out_pop + process + "|";
				}
			}
		}
		return Period.trim() + "#" + out_pop;
	}
}
