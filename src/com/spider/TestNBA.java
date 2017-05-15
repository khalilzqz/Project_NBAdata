package com.spider;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestNBA {

	public static void main(String[] args) throws IOException {
		String url = "http://www.cbssports.com/nba/scoreboard/20141006";
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
