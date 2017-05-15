package CBSsportSpider;

/*
 * 默认情况下，CBS的period数据中的第4节和加时赛都是3，本程序依次改为4，5，6……
 * 20101026HOULAL,0,5.0,3,1622542,1,0,25,40,25
 * 20101026HOULAL,0,11:41,3,1622542,5,1,0,42,0
 * period >= 3,同一gameID，当前一条shot时间为秒“.”，下一条包含分“:”时，period++
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CBSTime {
	public static void main(String args[]) throws Exception {
		String directoryPath = "F:/2006-07shotdata/";
		File directory = new File(directoryPath);
		String[] shotdata = directory.list();
		for (int i = 0; i < shotdata.length; i++) {
			BufferedReader br = new BufferedReader(new FileReader(directoryPath + shotdata[i]));
			String aLine = br.readLine();
			FileWriter fr = new FileWriter(directoryPath + "CBS" + shotdata[i]);
			String[] lastShot = new String[] { "", "", "", "", "", "", "", "", "", "" };
			while (aLine != null) {
				String[] newShot = aLine.split(",");
				if (lastShot[0].equals(newShot[0]) && lastShot[3].compareTo("3") >= 0 && lastShot[2].contains(".")
						&& newShot[2].contains(":")) {
					Integer tmp = Integer.parseInt(lastShot[3]) + 1;
					newShot[3] = tmp.toString();
				}
				if (lastShot[0].equals(newShot[0]) && newShot[3].compareTo(lastShot[3]) < 0)
					newShot[3] = lastShot[3];
				lastShot = newShot;
				String aShot = lastShot[0] + "," + lastShot[1] + "," + lastShot[2] + "," + lastShot[3] + ","
						+ lastShot[4] + "," + lastShot[5] + "," + lastShot[6] + "," + lastShot[7] + "," + lastShot[8]
						+ "," + lastShot[9];
				fr.append(aShot + "\r\n");
				System.out.println(aShot);
				aLine = br.readLine();
			}
			br.close();
			fr.close();
		}
	}
}
