package CBSsportSpider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//�����������ͬʱͬһ����Ա�ظ�Ͷ�����ݣ���ȥ��
@SuppressWarnings("unused")
public class ShotDataParser {
	public static void main(String[] args) throws IOException {
		String gameKey = "NBA_20071215_CHA@ATL";
		String url = "http://www.cbssports.com/nba/gametracker/shotchart/" + gameKey;
		TreeSet<CBSplayerInfo> playerInfoSet = new TreeSet<CBSplayerInfo>();
		Connection conn = Jsoup.connect(url);
		String pageContent = conn.get().toString();

		// shotData
		int cur = pageContent.indexOf("currentShotData = new String");
		int lcur = pageContent.indexOf("\"", cur);
		int rcur = pageContent.indexOf("\"", lcur + 1);
		String shotData = gameKey + "," + pageContent.substring(lcur + 1, rcur).replaceAll("~", "\r\n" + gameKey + ",");

		// System.out.println(shotData);
		// TimeChange
		/*
		 * Ĭ������£�CBS��period�����еĵ�4�ںͼ�ʱ������3�����ν������θ�Ϊ4��5��6����
		 * 20101026HOULAL,0,5.0,3,1622542,1,0,25,40,25
		 * 20101026HOULAL,0,11:41,3,1622542,5,1,0,42,0 period >=
		 * 3,ͬһgameID����ǰһ��shotʱ��Ϊ�롰.������һ�������֡�:��ʱ��period++
		 */
		ArrayList<String> CBSShotData = new ArrayList<>();
		String[] zhan = shotData.split("\r\n");
		String compare = "nba";
		String[] lastShot = new String[] { "", "", "", "", "", "", "", "", "", "" };
		for (int i = 0; i < zhan.length; i++) {
			// �Ƚ�ȥ��
			if (!zhan[i].contains(compare)) {
				compare = zhan[i];
				String[] newShot = zhan[i].split(",");
				if (lastShot[0].equals(newShot[0]) && lastShot[3].compareTo("3") >= 0 && lastShot[2].contains(".")
						&& newShot[2].contains(":")) {
					Integer tmp = Integer.parseInt(lastShot[3]) + 1;
					newShot[3] = tmp.toString();
				}
				if (lastShot[0].equals(newShot[0]) && newShot[3].compareTo(lastShot[3]) < 0) {
					newShot[3] = lastShot[3];
				}

				lastShot = newShot;
				String aShot = lastShot[0] + "," + lastShot[1] + "," + lastShot[2] + "," + lastShot[3] + ","
						+ lastShot[4] + "," + lastShot[5] + "," + lastShot[6] + "," + lastShot[7] + "," + lastShot[8]
						+ "," + lastShot[9];
				if (!CBSShotData.contains(aShot)) {
					CBSShotData.add(aShot);
					System.out.println(aShot);
				}
			}
		}

		//
		cur = pageContent.indexOf("playerDataHomeString = new String", rcur);
		lcur = pageContent.indexOf("\"", cur);
		rcur = pageContent.indexOf("\"", lcur + 1);
		String homePlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-");

		cur = pageContent.indexOf("playerDataAwayString = new String", rcur);
		lcur = pageContent.indexOf("\"", cur);
		rcur = pageContent.indexOf("\"", lcur + 1);
		String awayPlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-");

		String players = homePlayers + "\n" + awayPlayers;
		System.out.println(players);

		for (int j = 0; j < players.length(); j++) {
			CBSplayerInfo aPlayer = new CBSplayerInfo();
			int cur1 = players.indexOf(":", j);
			aPlayer.id = players.substring(j, cur1);
			int cur2 = players.indexOf(",", cur1);

			aPlayer.Name = players.substring(cur1 + 1, cur2);
			playerInfoSet.add(aPlayer); // �����ԱID��Ϣ
			j = players.indexOf("|", cur2);
			if (j == -1)
				break;
		}

		// ������ԱID����
		Iterator<CBSplayerInfo> it = playerInfoSet.iterator();
		while (it.hasNext()) {
			CBSplayerInfo nextPlayer = it.next();
			String playerInfo = nextPlayer.id + "\t" + nextPlayer.Name;
			System.out.println(playerInfo);
		}
	}
}
