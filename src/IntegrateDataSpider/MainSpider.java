package IntegrateDataSpider;

import java.io.FileWriter;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import CBSsportSpider.CBSpbpData;
import CBSsportSpider.ModifyThePBPDetails;

public class MainSpider {
	public static void main(String[] args) throws ParseException, IOException {
		String directoryPath = "F:/";
		FileWriter fr = null;
		FileWriter snfr = null;
		fr = new FileWriter(directoryPath + "NBAdataCrawler/2010-11x.txt");
		snfr = new FileWriter(directoryPath + "NBAdataCrawler/ExceptionRecord.txt");
		FileWriter CBSfr = new FileWriter(directoryPath + "NBAdataCrawler/CBSshotCom.txt");

		// 先去ESPN拿到比赛列表
		HashMap<String, String> GameMap = new HashMap<>();

		// 日期循环
		ArrayList<String> dateList = new ArrayList<>();
		// 从2005-2006赛季开始可以正常输出
		Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2011-01-01");// 定义起始日期
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse("2011-04-20");// 定义结束日期
		Calendar dd = Calendar.getInstance();// 定义日期实例
		dd.setTime(d1);// 设置日期起始时间
		// Document startdoc=Jsoup.connect(urlstr).userAgent("Mozilla/5.0
		// (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko)
		// Chrome/26.0.1410.64 Safari/537.31").timeout(10000).get();
		while (dd.getTime().before(d2)) {// 判断是否到结束日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(dd.getTime());
			// System.out.println(str.replace("-", ""));// 输出日期结果
			dateList.add(str.replace("-", ""));
			dd.add(Calendar.DATE, 1);// 进行当前日期月份加1
		}
		String ESPNurlBasic = "http://www.espn.com/nba/schedule/_/date/";
		for (String strList : dateList) {
			String url = ESPNurlBasic + strList;
			Connection conn = Jsoup.connect(url).timeout(20000);
			Document doc = null;
			try {
				doc = conn.get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Elements links = doc.select("div[class=responsive-table-wrap]");
			int count = 0;
			String zhan_away = "";
			String zhan_home = "";
			String CBSKey = "";
			String POPKey = "";
			String GameId = "";
			// 最初是ESPN缩写为准
			for (Element link : links) {
				if (count++ < 1) {
					Elements match = link.select("a[name=&lpos=nba:schedule:score]");
					for (Element sin : match) {
						String[] str = sin.attr("href").toString().split("/");
						String[] str1 = str[2].split("=");
						GameId = str1[1];
						String url_geturl = "http://www.espn.com/nba/game?gameId=" + GameId;
						Connection conn_geturl = Jsoup.connect(url_geturl).timeout(100000);
						Document doc_geturl = conn_geturl.get();
						Elements ddd = doc_geturl.select("td[class=legendLabel]");
						int count_geturl = 0;
						for (Element ss : ddd) {
							if (count_geturl == 0) {
								zhan_away = ss.text();
								count_geturl++;
							} else {
								zhan_home = ss.text();
							}
						}
						// 需要生成NBA_20161225_GS@CLE
						if (zhan_away.equals("PHX")) {
							zhan_away = "PHO";
						} else if (zhan_away.equals("WSH")) {
							zhan_away = "WAS";
						} else if (zhan_away.equals("UTAH")) {
							zhan_away = "UTA";
						}
						if (zhan_home.equals("PHX")) {
							zhan_home = "PHO";
						} else if (zhan_home.equals("WSH")) {
							zhan_home = "WAS";
						} else if (zhan_home.equals("UTAH")) {
							zhan_home = "UTA";
						} else if (zhan_home.equals("NJ")) {
							zhan_home = "NJN";
						}
						CBSKey = "NBA_" + strList + "_" + zhan_away + "@" + zhan_home;
						// 20161205&game=UTHLAL
						if (zhan_away.equals("UTA")) {
							zhan_away = "UTH";
						} else if (zhan_home.equals("UTA")) {
							zhan_home = "UTH";
						}
						if (zhan_away.equals("SA")) {
							zhan_away = "SAS";
						} else if (zhan_home.equals("SA")) {
							zhan_home = "SAS";
						}
						if (zhan_away.equals("NO")) {
							zhan_away = "NOR";
						} else if (zhan_home.equals("NO")) {
							zhan_home = "NOR";
						}
						if (zhan_away.equals("GS")) {
							zhan_away = "GSW";
						} else if (zhan_home.equals("GS")) {
							zhan_home = "GSW";
						}
						if (zhan_away.equals("NY")) {
							zhan_away = "NYK";
						} else if (zhan_home.equals("NY")) {
							zhan_home = "NYK";
						}
						if (zhan_away.equals("NJ")) {
							zhan_away = "NJN";
						} else if (zhan_home.equals("NJ")) {
							zhan_home = "NJN";
						}
						POPKey = strList + "&game=" + zhan_away + zhan_home;
						System.out.println(GameId + "|" + CBSKey + "|" + POPKey);
						GameMap.put(str1[1], CBSKey + "|" + POPKey);
					}
				}
			}

		}

		@SuppressWarnings("rawtypes")
		Iterator iter = GameMap.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			HashMap.Entry entry = (HashMap.Entry) iter.next();

			ArrayList<String> EspnData = new ArrayList<>();
			HashMap<String, String> CBSShothm = new HashMap<>();
			ArrayList<String> CBSShotData = new ArrayList<>();
			HashMap<String, String> HomePlayerName = new HashMap<>();
			HashMap<String, String> AwayPlayerName = new HashMap<>();

			// CBS数据
			String gameKey = entry.getValue().toString();
			String gameKey_cbs = "";
			String gameKey_pop = "";
			String[] gameKeysp = gameKey.split("\\|");
			if (gameKeysp.length > 1) {
				gameKey_cbs = gameKeysp[0];
				gameKey_pop = gameKeysp[1];
			}

			String url_cbs = "http://www.cbssports.com/nba/gametracker/shotchart/" + gameKey_cbs;
			Connection conn_cbs = Jsoup.connect(url_cbs).timeout(100000);
			String pageContent = "";
			try {
				pageContent = conn_cbs.get().toString();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// String HomeTeamName = "";
			// String AwayTeamName = "";
			// String[] str_teamname = gameKey_cbs.split("\\_|@");
			// if (str_teamname.length > 3) {
			// HomeTeamName = str_teamname[3];
			// AwayTeamName = str_teamname[2];
			// }

			// shotData
			int cur = pageContent.indexOf("currentShotData = new String");
			int lcur = pageContent.indexOf("\"", cur);
			int rcur = pageContent.indexOf("\"", lcur + 1);
			String shotData = "";
			if (rcur != -1) {
				shotData = gameKey_cbs + ","
						+ pageContent.substring(lcur + 1, rcur).replaceAll("~", "\r\n" + gameKey_cbs + ",");
			}

			// TimeChange
			/*
			 * 默认情况下，CBS的period数据中的第4节和加时赛都是3，本段将其依次改为4，5，6……
			 * 20101026HOULAL,0,5.0,3,1622542,1,0,25,40,25
			 * 20101026HOULAL,0,11:41,3,1622542,5,1,0,42,0 period >=
			 * 3,同一gameID，当前一条shot时间为秒“.”，下一条包含分“:”时，period++
			 */
			String[] zhan = shotData.split("\r\n");
			String[] lastShot = new String[] { "", "", "", "", "", "", "", "", "", "" };
			for (int i = 0; i < zhan.length; i++) {
				// 比较去重
				String[] newShot = zhan[i].split(",");
				if (newShot.length == 10) {
					if (lastShot[2].contains(".") && newShot[2].contains(".")) {
						if (lastShot[2].split("\\.")[0].compareTo(newShot[2].split("\\.")[0]) < 0) {
							continue;
						}
					}

					if (lastShot[0].equals(newShot[0]) && lastShot[3].compareTo("3") >= 0 && lastShot[2].contains(".")
							&& newShot[2].contains(":")) {
						Integer tmp = Integer.parseInt(lastShot[3]) + 1;
						newShot[3] = tmp.toString();
					}
					if (lastShot[0].equals(newShot[0]) && newShot[3].compareTo(lastShot[3]) < 0)
						newShot[3] = lastShot[3];
					lastShot = newShot;
					String aShot = lastShot[0] + "," + lastShot[1] + "," + lastShot[2] + "," + lastShot[3] + ","
							+ lastShot[4] + "," + lastShot[5] + "," + lastShot[6] + "," + lastShot[7] + ","
							+ lastShot[8] + "," + lastShot[9];
					if (!lastShot[4].equals("0")) {
						if (!CBSShotData.contains(aShot)) {
							CBSShotData.add(aShot);
						}

						// 新思路
						Integer time_c = 0;
						if (lastShot[2].contains(".")) {
							String[] time_cbs = lastShot[2].toString().split("\\.");
							time_c = (4 - Integer.valueOf(lastShot[3])) * 720 + Integer.valueOf(time_cbs[0]);
							if (!CBSShothm.containsKey(time_c)) {
								CBSShothm.put(time_c + "|" + lastShot[4] + "|" + lastShot[6] + "|" + lastShot[3],
										aShot);
								CBSfr.append(time_c + "|" + lastShot[4] + "|" + lastShot[6] + "|" + lastShot[3] + "#"
										+ aShot + "\n");
							}
						} else {
							String[] time_cbs = lastShot[2].split("\\:");
							if (!time_cbs[0].equals("12")) {
								time_c = (4 - Integer.valueOf(lastShot[3])) * 720 + Integer.valueOf(time_cbs[0]) * 60
										+ Integer.valueOf(time_cbs[1]);
								if (!CBSShothm.containsKey(time_c)) {
									CBSShothm.put(time_c + "|" + lastShot[4] + "|" + lastShot[6] + "|" + lastShot[3],
											aShot);
									CBSfr.append(time_c + "|" + lastShot[4] + "|" + lastShot[6] + "|" + lastShot[3]
											+ "#" + aShot + "\n");
								}
							}
						}
					}
				}
			}

			// 名单list：
			// wanzheng:Name,id+position
			// bufen:Scurry,id+postion
			cur = pageContent.indexOf("playerDataHomeString = new String", rcur);
			lcur = pageContent.indexOf("\"", cur);
			rcur = pageContent.indexOf("\"", lcur + 1);
			// 一共三个位置需要替换
			String homePlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-").replace("&#039;", "-")
					.replace("IV", "").replace("III", "").replace("II", "").replace("ê", "e").replace("Jr.", "").trim();
			String[] homezhan = homePlayers.split("\\|");
			for (int i = 0; i < homezhan.length; i++) {
				String out = SplitNameAndID(homezhan[i]);
				String[] homezhan1 = out.split("\\|");
				if (homezhan1.length > 2) {
					String[] name1 = homezhan1[1].split("\\-|\\s+|\\'");
					if (name1.length > 1) {
						if (!name1[0].equals("")) {
							HomePlayerName.put(name1[0].subSequence(0, 1) + name1[name1.length - 1],
									homezhan1[0] + "|" + homezhan1[2]);
						} else {
							HomePlayerName.put(name1[name1.length - 1], homezhan1[0] + "|" + homezhan1[2]);
						}
					} else {
						HomePlayerName.put(homezhan1[1].trim(), homezhan1[0] + "|" + homezhan1[2]);
					}
				}
			}

			cur = pageContent.indexOf("playerDataAwayString = new String", rcur);
			lcur = pageContent.indexOf("\"", cur);
			rcur = pageContent.indexOf("\"", lcur + 1);
			String awayPlayers = pageContent.substring(lcur + 1, rcur).replace("&nbsp;", "-").replace("&#039;", "-")
					.replace("IV", "").replace("III", "").replace("II", "").replace("ê", "e").replace("Jr.", "");
			String[] awayzhan = awayPlayers.split("\\|");
			for (int i = 0; i < awayzhan.length; i++) {
				String out = SplitNameAndID(awayzhan[i]);
				String[] awayzhan1 = out.split("\\|");
				if (awayzhan1.length > 2) {
					String[] name1 = awayzhan1[1].split("\\-|\\s+|\\'");
					if (name1.length > 1) {
						if (!name1[0].equals("")) {
							AwayPlayerName.put(name1[0].subSequence(0, 1) + name1[name1.length - 1],
									awayzhan1[0] + "|" + awayzhan1[2]);
						} else {
							AwayPlayerName.put(name1[name1.length - 1], awayzhan1[0] + "|" + awayzhan1[2]);
						}
					} else {
						AwayPlayerName.put(awayzhan1[1].trim(), awayzhan1[0] + "|" + awayzhan1[2]);
					}
				}
			}

			// 输出名单
			@SuppressWarnings("rawtypes")
			Iterator iter1 = AwayPlayerName.entrySet().iterator();
			while (iter1.hasNext()) {
				@SuppressWarnings("rawtypes")
				HashMap.Entry entry1 = (HashMap.Entry) iter1.next();
				// snfr.append(entry1.getKey() + "|" + entry1.getValue() +
				// "\n");
				System.out.println(entry1.getKey() + "|" + entry1.getValue());
			}
			System.out.println("+++++++++++");
			@SuppressWarnings("rawtypes")
			Iterator iter2 = HomePlayerName.entrySet().iterator();
			while (iter2.hasNext()) {
				@SuppressWarnings("rawtypes")
				HashMap.Entry entry2 = (HashMap.Entry) iter2.next();
				// snfr.append(entry1.getKey() + "|" + entry1.getValue() +
				// "\n");
				System.out.println(entry2.getKey() + "|" + entry2.getValue());
			}

			// 先拿到ESPN的Detail数据,并根据标记shotdata匹配0，1
			ArrayList<String> HomeStarters = new ArrayList<>();
			ArrayList<String> HomePlayers = new ArrayList<>();
			ArrayList<String> AwayStarters = new ArrayList<>();
			ArrayList<String> AwayPlayers = new ArrayList<>();
			String urlBasic = "http://www.espn.com/nba/playbyplay?gameId=";
			String urlStart = "http://www.espn.com/nba/boxscore?gameId=";
			String EspnHomeTeam = "";
			String EspnAwayTeam = "";
			int count_team = 0;
			String strList = entry.getKey().toString();
			String otherKey = entry.getValue().toString();
			String url = urlBasic + strList;
			Connection conn = Jsoup.connect(url).timeout(20000);
			// StartersGet
			Document doc_s = null;
			try {
				doc_s = Jsoup.connect(urlStart + strList).timeout(20000).get();
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				Elements team_links = doc_s.select("div[id=custom-nav]");
				for (Element team_link : team_links) {
					Elements teams = team_link.select("span[class=abbrev]");
					for (Element team : teams) {
						if (count_team < 1) {
							EspnAwayTeam = team.text();
							count_team++;
						} else {
							EspnHomeTeam = team.text();
						}
					}
				}
			}
			Elements in1 = doc_s.select("div[class=sub-module]");
			int count_startlist = 0;
			int count_players_home = 0;
			int count_players_away = 0;
			for (Element in : in1) {
				if (count_startlist == 0) {
					Elements inputs = in.select("a[name=&lpos=nba:game:boxscore:playercard]");
					for (Element away_in : inputs) {
						String[] name_away = away_in.select("span[class=abbr]").text().replace("IV", "")
								.replace("III", "").replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
								.split("\\s+|\\-|\\'");
						if (count_players_away < 5) {
							AwayStarters.add(name_away[0].subSequence(0, 1) + name_away[name_away.length - 1]);
							AwayPlayers.add(name_away[0].subSequence(0, 1) + name_away[name_away.length - 1]);
							count_players_away++;
						} else if (count_players_away < 12) {
							AwayPlayers.add(name_away[0].subSequence(0, 1) + name_away[name_away.length - 1]);
							count_players_away++;
						} else if (count_players_away == 12) {
							count_startlist++;
							count_players_away++;
						}
					}
				} else if (count_startlist == 1) {
					Elements inputs = in.select("a[name=&lpos=nba:game:boxscore:playercard]");
					for (Element home_in : inputs) {
						String[] name_home = home_in.select("span[class=abbr]").text().replace("IV", "")
								.replace("III", "").replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
								.split("\\s+|\\-|\\'");
						if (count_players_home < 5) {
							HomeStarters.add(name_home[0].subSequence(0, 1) + name_home[name_home.length - 1]);
							HomePlayers.add(name_home[0].subSequence(0, 1) + name_home[name_home.length - 1]);
							count_players_home++;
						} else if (count_players_home < 12) {
							HomePlayers.add(name_home[0].subSequence(0, 1) + name_home[name_home.length - 1]);
							count_players_home++;
						} else if (count_players_home == 12) {
							count_players_home++;
							count_startlist++;
						}
					}
				} else {
					break;
				}
			}

			// 修正正在场上球员的数据
			String url_pop = "http://popcornmachine.net/gf?date=" + gameKey_pop;
			Connection conn_pop = Jsoup.connect(url_pop).timeout(1000000);
			Document doc_pop;
			Elements links_pop = null;
			HashMap<String, String> popNameList = new HashMap<>();
			try {
				doc_pop = conn_pop.get();
				links_pop = doc_pop.select("div[class=lilineuptt]");
				for (Element link : links_pop) {
					String text_pop = link.toString();
					if (!text_pop.contains("run")) {
						String[] split_pop = ProcessPopName(text_pop).split("\\#");
						if (split_pop.length > 1) {
							if (!popNameList.containsKey(split_pop[0])) {
								popNameList.put(split_pop[0], split_pop[1]);
							} else {
								popNameList.put("home" + split_pop[0], split_pop[1]);
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// 基本pbp数据
				ArrayList<String> zhan_time = new ArrayList<>();
				ArrayList<String> zhan_score = new ArrayList<>();
				ArrayList<String> zhan_team = new ArrayList<>();
				int count0 = 0;
				int count1 = 0;
				Document doc = null;
				Elements links = null;
				try {
					doc = conn.get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					links = doc.select("li[class=accordion-item]");
				}
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
						Elements teams = link.select("img[class=team-logo]");
						for (Element team : teams) {
							String[] split_teamname = team.toString().split("/500/|.png");
							if (split_teamname.length > 1) {
								zhan_team.add(split_teamname[1].toUpperCase());
							} else {
								if (zhan_team.contains(EspnHomeTeam)) {
									zhan_team.add(EspnAwayTeam);
								} else {
									zhan_team.add(EspnHomeTeam);
								}
							}
						}

						Elements plays = link.select("td[class=game-details]");
						for (Element play : plays) {
							if (count1 < zhan_time.size()) {
								String playDetail = play.html().toString();
								ModifyThePBPDetails test = new ModifyThePBPDetails();
								data = test.modify(playDetail);
								// 记录匹配的name
								String PlayerName = "";
								String PlayerName_re = "";
								// name匹配id
								String PalyerID = null;
								String PalyerID_re = null;
								if (count1 < zhan_team.size() && count1 < zhan_score.size()) {
									if (zhan_team.get(count1).equals(EspnHomeTeam)) {
										if (playDetail.contains("assists") || playDetail.contains("enters")) {
											// 主player
											if (data.getName() != null) {
												String[] name1 = data.getName().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName().equals("")) {
															snfr.append("1" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("2" + "|" + otherKey + "|"
																	+ data.getName().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (HomePlayerName.containsKey(data.getName())) {
														PalyerID = HomePlayerName.get(data.getName()).split("\\|")[0];
														PlayerName = data.getName();
													}
												}
											}
											// re Player
											if (data.getName_re() != null) {
												String[] name1 = data.getName_re().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID_re = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID_re = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName_re().equals("")) {
															snfr.append("3" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("4" + "|" + otherKey + "|"
																	+ data.getName_re().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (HomePlayerName.containsKey(data.getName_re())) {
														PalyerID_re = HomePlayerName.get(data.getName_re())
																.split("\\|")[0];
														PlayerName_re = data.getName_re();
													}
												}
											}
										} else {
											// 主player
											if (data.getName() != null) {
												String[] name1 = data.getName().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName().equals("")) {
															snfr.append("5" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("6" + "|" + otherKey + "|"
																	+ data.getName().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (HomePlayerName.containsKey(data.getName())) {
														PalyerID = HomePlayerName.get(data.getName()).split("\\|")[0];
														PlayerName = data.getName();
													}
												}
											}
											// re Player
											if (data.getName_re() != null) {
												String[] name1 = data.getName_re().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID_re = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID_re = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName_re().equals("")) {
															snfr.append("7" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("8" + "|" + otherKey + "|"
																	+ data.getName_re().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (AwayPlayerName.containsKey(data.getName_re())) {
														PalyerID_re = AwayPlayerName.get(data.getName_re())
																.split("\\|")[0];
														PlayerName_re = data.getName_re();
													}
												}
											}

										}
									} else {
										if (playDetail.contains("assists") || playDetail.contains("enters")) {
											// 主player
											if (data.getName() != null) {
												String[] name1 = data.getName().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName().equals("")) {
															snfr.append("9" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("10" + "|" + otherKey + "|"
																	+ data.getName().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (AwayPlayerName.containsKey(data.getName())) {
														PalyerID = AwayPlayerName.get(data.getName()).split("\\|")[0];
														PlayerName = data.getName();
													}
												}
											}
											// re Player
											if (data.getName_re() != null) {
												String[] name1 = data.getName_re().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID_re = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID_re = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName_re().equals("")) {
															snfr.append("11" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("12" + "|" + otherKey + "|"
																	+ data.getName_re().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (AwayPlayerName.containsKey(data.getName_re())) {
														PalyerID_re = AwayPlayerName.get(data.getName_re())
																.split("\\|")[0];
														PlayerName_re = data.getName_re();
													}
												}
											}
										} else {
											// 主player
											if (data.getName() != null) {
												String[] name1 = data.getName().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (AwayPlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID = AwayPlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName().equals("")) {
															snfr.append("13" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("14" + "|" + otherKey + "|"
																	+ data.getName().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (AwayPlayerName.containsKey(data.getName())) {
														PalyerID = AwayPlayerName.get(data.getName()).split("\\|")[0];
														PlayerName = data.getName();
													}
												}
											}
											// re Player
											if (data.getName_re() != null) {
												String[] name1 = data.getName_re().replace("IV", "").replace("III", "")
														.replace("II", "").replace("Jr.", "").replace("ê", "e").trim()
														.split("\\s+|\\-|\\'");
												if (name1.length > 1) {
													if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 1])) {
														PalyerID_re = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 1])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 1];
													} else if (HomePlayerName.containsKey(
															name1[0].subSequence(0, 1) + name1[name1.length - 2])) {
														PalyerID_re = HomePlayerName.get(
																name1[0].subSequence(0, 1) + name1[name1.length - 2])
																.split("\\|")[0];
														PlayerName_re = name1[0].subSequence(0, 1)
																+ name1[name1.length - 2];
													} else {
														if (!data.getName_re().equals("")) {
															snfr.append("15" + "|" + otherKey + "|"
																	+ name1[0].subSequence(0, 1)
																	+ name1[name1.length - 1] + "\n");
															snfr.append("16" + "|" + otherKey + "|"
																	+ data.getName_re().replace("IV", "")
																			.replace("III", "").replace("II", "")
																			.replace("Jr.", "").trim()
																	+ "|" + playDetail + "\n");
														}
													}
												} else {
													if (HomePlayerName.containsKey(data.getName_re())) {
														PalyerID_re = HomePlayerName.get(data.getName_re())
																.split("\\|")[0];
														PlayerName_re = data.getName_re();
													}
												}
											}

										}
									}

									// 首发阵容
									// HomeStarters.isEmpty() &&
									// AwayStarters.isEmpty()
									// &&
									if (playDetail.contains("vs.") && (zhan_score.get(count1).equals("0 - 0"))) {
										int period = 1;
										String Pop_key = "Period " + period + " 12:00";
										if (popNameList.containsKey(Pop_key)) {
											String[] names = popNameList.get(Pop_key).split("\\|");
											if (popNameList.containsKey("home" + Pop_key)) {
												String[] names_home = popNameList.get("home" + Pop_key).split("\\|");
												HomeStarters.clear();
												AwayStarters.clear();
												for (int i = 0; i < 5; i++) {
													HomeStarters.add(names_home[i]);
													AwayStarters.add(names[i]);
												}
											}
										}

										for (String str : HomeStarters) {
											System.out.println(str);
										}
										System.out.println("========");
										for (String str : AwayStarters) {
											System.out.println(str);
										}
										System.out.println("========");
									} else if (playDetail.contains("End of the")) {
										// 到节中更新list
										if (playDetail.contains("Quarter")) {
											int period = count0;
											String Pop_key = "Period " + period + " 12:00";
											if (popNameList.containsKey(Pop_key)) {
												String[] names = popNameList.get(Pop_key).split("\\|");
												if (popNameList.containsKey("home" + Pop_key)) {
													String[] names_home = popNameList.get("home" + Pop_key)
															.split("\\|");
													HomeStarters.clear();
													AwayStarters.clear();
													for (int i = 0; i < 5; i++) {
														HomeStarters.add(names_home[i]);
														AwayStarters.add(names[i]);
													}
												}
											}
										} else {
											int period = count0;
											String Pop_key = "Period " + period + " 05:00";
											if (popNameList.containsKey(Pop_key)) {
												String[] names = popNameList.get(Pop_key).split("\\|");
												if (popNameList.containsKey("home" + Pop_key)) {
													String[] names_home = popNameList.get("home" + Pop_key)
															.split("\\|");
													HomeStarters.clear();
													AwayStarters.clear();
													for (int i = 0; i < 5; i++) {
														HomeStarters.add(names_home[i]);
														AwayStarters.add(names[i]);
													}
												}
											}
										}

										// 输出
										for (String str : HomeStarters) {
											System.out.println(str);
										}
										System.out.println("========");
										for (String str : AwayStarters) {
											System.out.println(str);
										}
										System.out.println("========");
									}
									// else if ((count0 - 1 > 4) &&
									// (zhan_time.get(count1).equals("05:00")))
									// {
									// int period = count0 - 1;
									// String Pop_key = "Period " + period + " "
									// + "
									// 05:00";
									// if (popNameList.containsKey(Pop_key)) {
									// String[] names =
									// popNameList.get(Pop_key).split("\\|");
									// String[] names_home =
									// popNameList.get("home"
									// +
									// Pop_key).split("\\|");
									// HomePlayers.clear();
									// AwayPlayers.clear();
									// for (int i = 0; i < 5; i++) {
									// HomePlayers.add(names_home[i]);
									// AwayPlayers.add(names[i]);
									// }
									// }
									// }

									// substitude
									if (playDetail.contains("enters the game for")) {
										if (zhan_team.get(count1).equals(EspnHomeTeam)) {
											if (HomeStarters.contains(PlayerName_re)) {
												HomeStarters.remove(PlayerName_re);
												HomeStarters.add(PlayerName);
											} else {
												snfr.append("SubError#" + EspnHomeTeam + "|" + playDetail + "|"
														+ PlayerName + "|" + PlayerName_re + "\n");
											}
										} else {
											if (AwayStarters.contains(PlayerName_re)) {
												AwayStarters.remove(PlayerName_re);
												AwayStarters.add(PlayerName);
											} else {
												snfr.append("SubError#" + EspnAwayTeam + "|" + playDetail + "|"
														+ PlayerName + "|" + PlayerName_re + "\n");
											}
										}
									}
									// 转化为id,输出

									String PrintHomeStarters = "";
									String PrintHomeStartersID = "";
									for (String str_h : HomeStarters) {
										PrintHomeStarters += "|" + str_h;
										if (HomePlayerName.containsKey(str_h)) {
											PrintHomeStartersID += "|" + HomePlayerName.get(str_h).split("\\|")[0];
										}

									}
									String PrintAwayStarters = "";
									String PrintAwayStartersID = "";
									for (String str_a : AwayStarters) {
										PrintAwayStarters += "|" + str_a;
										if (AwayPlayerName.containsKey(str_a)) {
											PrintAwayStartersID += "|" + AwayPlayerName.get(str_a).split("\\|")[0];
										}
									}

									// String TeamOut = null;
									// // 判定记录归属
									// if (HomePlayers.contains(PlayerName)) {
									// TeamOut = HomeTeamName;
									// } else if
									// (AwayPlayers.contains(PlayerName))
									// {
									// TeamOut = AwayTeamName;
									// }
									// 输出
									if (playDetail.contains("misses") || playDetail.contains("makes")
											|| playDetail.contains("blocks")) {
										String EspnAdd1 = "1" + "|" + (count0 - 1) + "|" + count1 + "|"
												+ zhan_time.get(count1) + "|" + zhan_score.get(count1) + "|"
												+ zhan_team.get(count1) + "|" + playDetail + "|" + data.getPlay() + "|"
												+ data.getName() + "|" + PalyerID + "|" + data.getName_re() + "|"
												+ PalyerID_re + PrintHomeStarters + PrintHomeStartersID + "|vs"
												+ PrintAwayStarters + PrintAwayStartersID + "|" + zhan_time.get(count1);
										if (!EspnData.contains(EspnAdd1)) {
											EspnData.add(EspnAdd1);
										}
									} else {
										EspnData.add("0" + "|" + (count0 - 1) + "|" + count1 + "|"
												+ zhan_time.get(count1) + "|" + zhan_score.get(count1) + "|"
												+ zhan_team.get(count1) + "|" + playDetail + "|" + data.getPlay() + "|"
												+ data.getName() + "|" + PalyerID + "|" + data.getName_re() + "|"
												+ PalyerID_re + PrintHomeStarters + PrintHomeStartersID + "|vs"
												+ PrintAwayStarters + PrintAwayStartersID + "|"
												+ zhan_time.get(count1));
									}
									count1++;

								}

							}
						}
					}
				}

				// 输出poplist
				// @SuppressWarnings("rawtypes")
				// Iterator itere_pop = popNameList.entrySet().iterator();
				// while (itere_pop.hasNext()) {
				// @SuppressWarnings("rawtypes")
				// HashMap.Entry entrye_pop = (HashMap.Entry) itere_pop.next();
				// System.out.println(entrye_pop.getKey());
				// }

				// 连接两个表
				int count_espn_shot = 0;
				for (String str1 : EspnData) {
					if (str1.startsWith("1")) {
						count_espn_shot++;
					}
				}

				if (!CBSShotData.isEmpty()) {
					System.out.println(CBSShotData.get(0) + "|" + CBSShotData.size() + "|" + count_espn_shot);
				} else {
					System.out.println("CBSShotData Size Is Empty!");
				}

				ArrayList<String> shuchu = new ArrayList<>();
				int tongji1 = 0;
				int tongji1has = 0;
				for (String str1 : EspnData) {
					if (str1.startsWith("1")) {
						tongji1++;
						Boolean INorOUT = false;
						if (str1.contains("makes")) {
							INorOUT = true;
						}
						String[] match = str1.split("\\|");
						int time_e = 0;
						Boolean containFlag = false;
						String ctKey = "";
						HashMap<Integer, String> jishu = new HashMap<>();
						if (match.length > 9) {
							String[] time_espn = match[3].split("\\:");
							if (!time_espn[0].equals("12")) {
								time_e = (4 - Integer.valueOf(match[1])) * 720 + Integer.valueOf(time_espn[0]) * 60
										+ Integer.valueOf(time_espn[1]);
								int time_lag = 10;
								int count_min = 0;
								for (int i = time_e - time_lag; i <= time_e + time_lag; i++) {
									if (!INorOUT) {
										if (CBSShothm.containsKey(i + "|" + match[9] + "|0|" + match[1])) {
											ctKey = i + "|" + match[9] + "|0|" + match[1];
											containFlag = true;
											jishu.put(i, ctKey);
											count_min++;
										}
									} else if (INorOUT) {
										if (CBSShothm.containsKey(i + "|" + match[9] + "|1|" + match[1])) {
											ctKey = i + "|" + match[9] + "|1|" + match[1];
											containFlag = true;
											jishu.put(i, ctKey);
											count_min++;
										}
									}
								}
								if (containFlag) {
									tongji1has++;
									int min_e = 360;
									if (count_min == 1) {
										shuchu.add(str1 + "|" + CBSShothm.get(ctKey));
									} else {
										@SuppressWarnings("rawtypes")
										Iterator itere = jishu.entrySet().iterator();
										while (itere.hasNext()) {
											@SuppressWarnings("rawtypes")
											HashMap.Entry entrye = (HashMap.Entry) itere.next();
											if (Math.abs(
													Integer.valueOf(entrye.getKey().toString()) - time_e) < min_e) {
												min_e = Math.abs(Integer.valueOf(entrye.getKey().toString()) - time_e);
											}
										}

										if (jishu.containsKey(time_e - min_e)) {
											shuchu.add(str1 + "|" + CBSShothm.get(jishu.get(time_e - min_e)));
										} else if (jishu.containsKey(time_e + min_e)) {
											shuchu.add(str1 + "|" + CBSShothm.get(jishu.get(time_e + min_e)));
										} else {
											shuchu.add(str1 + "|" + min_e);
										}
									}
								} else {
									shuchu.add(str1);
								}
							}
						}
					} else {
						// fr.append(str1 + "\n");
						shuchu.add(str1);
					}
				}
				System.out.println("MatchRate is|" + (double) tongji1has / tongji1);
				// 重新来
				if (((double) tongji1has / tongji1) > 0.9) {
					for (String shuchumen : shuchu) {
						fr.append(shuchumen + "\n");
					}
				} else {
					int tongji2 = 0;
					int tongji2has = 0;
					for (String str1 : EspnData) {
						if (str1.startsWith("1")) {
							tongji2++;
							Boolean INorOUT = false;
							if (str1.contains("makes")) {
								INorOUT = true;
							}
							// 加个距离判定
							int distance = 0;
							Boolean SkipFlag = false;
							Boolean FreeFlag = false;
							if (str1.contains("free throw")) {
								distance = 14;
								FreeFlag = true;
							} else if (str1.contains("foot")) {
								String pdd = str1.split("\\|")[6];
								String regEx = "[^0-9]";
								Pattern p = Pattern.compile(regEx);
								Matcher m = p.matcher(pdd);
								distance = Integer.valueOf(m.replaceAll("").trim());
							} else {
								SkipFlag = true;
							}
							// 开始匹配
							String[] match = str1.split("\\|");
							int time_e = 0;
							Boolean containFlag = false;
							String ctKey = "";
							HashMap<Integer, String> jishu = new HashMap<>();
							if (match.length > 9) {
								String[] time_espn = match[3].split("\\:");
								if (!time_espn[0].equals("12")) {
									time_e = (4 - Integer.valueOf(match[1])) * 720 + Integer.valueOf(time_espn[0]) * 60
											+ Integer.valueOf(time_espn[1]);
									int time_lag = 180;
									int count_min = 0;
									for (int i = time_e - time_lag; i <= time_e + time_lag; i++) {
										if (!INorOUT) {
											if (CBSShothm.containsKey(i + "|" + match[9] + "|0|" + match[1])) {
												ctKey = i + "|" + match[9] + "|0|" + match[1];
												int cbs_distance = Integer
														.valueOf(CBSShothm.get(ctKey).split("\\,")[9]);
												if (SkipFlag) {
													containFlag = true;
													jishu.put(i, ctKey);
													count_min++;
												} else if (FreeFlag) {
													if (cbs_distance == 14) {
														containFlag = true;
														jishu.put(i, ctKey);
														count_min++;
													}
												} else {
													if (Math.abs(cbs_distance - distance) < 10) {
														containFlag = true;
														jishu.put(i, ctKey);
														count_min++;
													}
												}
											}
										} else if (INorOUT) {
											if (CBSShothm.containsKey(i + "|" + match[9] + "|1|" + match[1])) {
												ctKey = i + "|" + match[9] + "|1|" + match[1];
												int cbs_distance = Integer
														.valueOf(CBSShothm.get(ctKey).split("\\,")[9]);
												if (SkipFlag) {
													containFlag = true;
													jishu.put(i, ctKey);
													count_min++;
												} else if (FreeFlag) {
													if (cbs_distance == 14) {
														containFlag = true;
														jishu.put(i, ctKey);
														count_min++;
													}
												} else {
													if (Math.abs(cbs_distance - distance) < 10) {
														containFlag = true;
														jishu.put(i, ctKey);
														count_min++;
													}
												}
											}
										}
									}
									if (containFlag) {
										tongji2has++;
										// 这里也要改
										int min_e = 1000;
										if (count_min == 1) {
											fr.append(str1 + "|" + CBSShothm.get(ctKey) + "\n");
											// shuchu.add(str1 + "|" +
											// CBSShothm.get(ctKey));
										} else {
											@SuppressWarnings("rawtypes")
											Iterator itere = jishu.entrySet().iterator();
											while (itere.hasNext()) {
												@SuppressWarnings("rawtypes")
												HashMap.Entry entrye = (HashMap.Entry) itere.next();
												if (Math.abs(
														Integer.valueOf(entrye.getKey().toString()) - time_e) < min_e) {
													min_e = Math
															.abs(Integer.valueOf(entrye.getKey().toString()) - time_e);
												}
											}

											if (jishu.containsKey(time_e - min_e)) {
												fr.append(str1 + "|" + CBSShothm.get(jishu.get(time_e - min_e)) + "\n");
												// snfr.append("####" +
												// CBSShothm.get(jishu.get(time_e
												// - min_e)) + "\n");
												// shuchu.add(str1 + "|" +
												// CBSShothm.get(time_e -
												// min_e));
											} else if (jishu.containsKey(time_e + min_e)) {
												fr.append(str1 + "|" + CBSShothm.get(jishu.get(time_e + min_e)) + "\n");
												// snfr.append("#####" +
												// CBSShothm.get(jishu.get(time_e
												// + min_e)) + "\n");
												// shuchu.add(str1 + "|" +
												// CBSShothm.get(time_e +
												// min_e));
											} else {
												fr.append(str1 + "|" + min_e + "\n");
												// shuchu.add(str1 + "|" +
												// min_e);
											}
										}
									} else {
										fr.append(str1 + "\n");
										// shuchu.add(str1);
									}
								}
							}
						} else {
							fr.append(str1 + "\n");
							// shuchu.add(str1);
						}
					}
					System.out.println("ReMatchRate is|" + (double) tongji2has / tongji2);
				}
			}
		}
		fr.close();
		snfr.close();
		CBSfr.close();
	}

	// name输入
	public static String ProcessBasicName(String str) {
		String[] name1 = str.split("\\-|\\s+");
		String out = "";
		if (name1.length > 1) {
			if (!name1[0].equals("")) {
				out = name1[0].subSequence(0, 1) + name1[name1.length - 1];
			} else {
				out = name1[1];
			}
		}
		return out;
	}

	// 修正在场球员辅助
	public static String ProcessPopName(String str) {
		String[] names_pop = str.split("<br>|</p>|Period|<span");
		String Period = "";
		String out_pop = "";
		if (names_pop.length == 10) {
			Period = "Period " + names_pop[1].trim();
			for (int i = 4; i < 9; i++) {
				String process = "";
				if (names_pop[i].startsWith("Jr")) {
					process = names_pop[i].replace("IV", "").replace("III", "").replace("II", "").replace("Jr.", "")
							.replace("ê", "e").trim();
				} else {
					process = names_pop[i].replace("IV", "").replace("III", "").replace("II", "").replace("Jr.", "")
							.replace("ê", "e").replace("Jr", "").trim();
				}
				if (process.contains("Kyle") && process.contains("Quinn")) {
					process = "KQuinn";
					out_pop = out_pop + process + "|";
				} else if (process.contains("Jermaine") && process.contains("Neal")) {
					process = "JNeal";
					out_pop = out_pop + process + "|";
				} else if (process.contains("Shaquille") && process.contains("Neal")) {
					process = "SNeal";
					out_pop = out_pop + process + "|";
				} else if (process.contains("Patrick") && process.contains("Bryant")) {
					process = "PBryant";
					out_pop = out_pop + process + "|";
				} else if (process.contains("Timothe") && process.contains("Luwawu")) {
					process = "TLuwawu";
					out_pop = out_pop + process + "|";
				} else {
					String[] names = process.split("\\s+|\\-|\\'");
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
		}
		return Period.trim() + "#" + out_pop;
	}

	public static String SplitNameAndID(String str) {
		String out = "";
		String[] zhan1 = str.split("\\:");
		out = zhan1[0];
		if (zhan1.length > 1) {
			String[] zhan2 = zhan1[1].split("\\,");
			if (zhan2.length > 2) {
				out = out + "|" + zhan2[0] + "|" + zhan2[2];
			}
		}
		return out;
	}

	public static Boolean EspnMCBSMatch(String espn, String cbs) {
		String[] str_espn = espn.split("\\|");
		String[] str_cbs = cbs.split("\\,");
		int time_c = 0;
		if (str_cbs[2].contains(".")) {
			String[] time_cbs = str_cbs[2].split("\\.");
			time_c = Integer.valueOf(time_cbs[0]);
		} else {
			String[] time_cbs = str_cbs[2].split("\\:");
			if (time_cbs[0].equals("12")) {
				return false;
			} else {
				time_c = Integer.valueOf(time_cbs[0]) * 60 + Integer.valueOf(time_cbs[1]);
			}
		}
		String[] time_espn = str_espn[2].split("\\:");
		Integer time_e = Integer.valueOf(time_espn[0]) * 60 + Integer.valueOf(time_espn[1]);
		if (str_espn[7].equals(str_cbs[4]) && (Math.abs(time_e - time_c) <= 7)) {
			// if ((Math.abs(time_e - time_c) <= 3)) {
			return true;
		} else {
			return false;
		}

	}
}
