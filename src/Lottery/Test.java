package Lottery;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test {
	public static void main(String[] args) throws IOException, ParseException {
		// String url =
		// "http://www.okooo.com/basketball/match/5346886/history/";
		// Connection conn = Jsoup.connect(url);
		// Document doc = conn.get();
		System.out.println(dateToStamp("2017-05-09"));
		System.out.println(dateToStamp("2017-05-10"));
	}

	public static String dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}
}
