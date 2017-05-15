package CBSsportSpider;

public class ModifyThePBPDetails {
	public CBSpbpData modify(String inputDetails) {
		CBSpbpData data = new CBSpbpData();
		if (inputDetails.contains("makes")) {
			if (inputDetails.contains("free")) {
				String[] str = inputDetails.split("makes");
				data.setName(str[0].trim());
				data.setPlay("Freethrow");
				data.setPts(1);
			} else if (inputDetails.contains("layup") || inputDetails.contains("dunk")
					|| inputDetails.contains("tip")) {
				String[] str = inputDetails.split("makes|\\(|assists");
				if (str.length > 2) {
					data.setName(str[0].trim());
					data.setPlay("Shot");
					data.setPts(2);
					data.setName_re(str[2].trim());
				} else {
					data.setName(str[0].trim());
					data.setPlay("Shot");
					data.setPts(2);
				}
			} else {
				String[] str = inputDetails.split("makes|-foot|\\(|assists|point");
				if (str.length == 5) {
					data.setName(str[0].trim());
					data.setDistance(str[1].trim());
					data.setPlay("Shot");
					if (str[2].contains("three")) {
						data.setPts(3);
					} else {
						data.setPts(2);
					}
					data.setName_re(str[3].trim());
				} else if (str.length > 2) {
					data.setName(str[0].trim());
					data.setDistance(str[1].trim());
					data.setPlay("Shot");
					if (str[2].contains("three")) {
						data.setPts(3);
					} else {
						data.setPts(2);
					}
				}
			}
		} else if (inputDetails.contains("misses")) {
			if (inputDetails.contains("free")) {
				String[] str = inputDetails.split("misses");
				data.setName(str[0].trim());
				data.setPlay("FreeThrow");
				data.setPts(0);
			} else {
				String[] str = inputDetails.split("misses");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setDistance(str[1].trim());
					data.setPlay("miss");
					data.setPts(0);
				} else if (str.length > 0) {
					data.setName(str[0].trim());
					data.setPlay("miss");
					data.setPts(0);
				} else {
					data.setName("ERROR");
					data.setPlay("miss");
					data.setPts(0);
				}
			}
		} else if (inputDetails.contains("rebound")) {
			if (inputDetails.contains("team")) {
				String[] str = inputDetails.split("defensive|offensive");
				if (str.length > 1) {
					data.setPlay("Rebound");
					data.setDetailPlay(inputDetails);
				}
			} else {
				if (inputDetails.contains("defensive")) {
					String[] str = inputDetails.split("defensive");
					data.setName(str[0].trim());
					data.setPlay("Rebound");
					data.setDetailPlay("defensive rebound");
				} else {
					String[] str = inputDetails.split("offensive");
					data.setName(str[0].trim());
					data.setPlay("Rebound");
					data.setDetailPlay("offensive rebound");
				}
			}
		} else if (inputDetails.contains("enters")) {
			String[] str = inputDetails.split("enters the game for");
			if (str.length > 1) {
				data.setName(str[0].trim());
				data.setPlay("Substitution");
				data.setName_re(str[1].trim());
			}
		} else if (inputDetails.contains("timeout")) {
			data.setPlay("Timeout");
		} else if (inputDetails.contains("foul")) {
			if (inputDetails.contains("personal")) {
				String[] str = inputDetails.split("personal|\\(|draws");
				if (str.length == 4) {
					data.setName(str[0].trim());
					data.setPlay("Foul");
					data.setDetailPlay("personal foul");
					data.setName_re(str[2].trim());
				}
			} else if (inputDetails.contains("shooting")) {
				String[] str = inputDetails.split("shooting|\\(|draws");
				if (str.length == 4) {
					data.setName(str[0].trim());
					data.setPlay("Foul");
					data.setDetailPlay("shooting block foul");
					data.setName_re(str[2].trim());
				}
			} else if (inputDetails.contains("loose")) {
				String[] str = inputDetails.split("loose|\\(|draws");
				if (str.length == 4) {
					data.setName(str[0].trim());
					data.setPlay("Foul");
					data.setDetailPlay("shooting block foul");
					data.setName_re(str[2].trim());
				}
			} else if (inputDetails.contains("technical")) {
				String[] str = inputDetails.split("technical");
				data.setName(str[0].trim());
				data.setPlay("Foul");
				data.setDetailPlay("technical foul");
			} else if (inputDetails.contains("bound")) {
				String[] str = inputDetails.split("in|\\(|draws");
				if (str.length == 4) {
					data.setName(str[0].trim());
					data.setPlay("Foul");
					data.setDetailPlay("In bound foul");
					data.setName_re(str[2].trim());
				}
			} else if (inputDetails.contains("offensive")) {
				String[] str = inputDetails.split("offensive|\\(|draws");
				if (str.length > 2) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("offensive foul");
					data.setName_re(str[2].trim());
				}
			} else if (inputDetails.contains("flagrant")) {
				String[] str = inputDetails.split("flagrant|\\(|draws");
				if (str.length > 2) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay(" flagrant foul type");
					data.setName_re(str[2].trim());
				}
			}
		} else if (inputDetails.contains("blocks")) {
			// Paul Pierce blocks Luol Deng's shot
			String[] str = inputDetails.split("blocks|\\'s");
			if (str.length > 1) {
				data.setName_re(str[0].trim());
				data.setPlay("Block");
				data.setName(str[1].trim());
			}
		} else if (inputDetails.contains("turnover")) {
			// Out-of-Bounds Bad Pass Turnover
			// lost ball turnover(steals contains)
			// back court turnover
			// out of bounds lost ball turnover
			// turnover
			// steps out of bounds turnover
			// shot clock turnover
			// kicked ball turnover
			if (inputDetails.contains("lost ball turnover")) {
				if (inputDetails.contains("steals")) {
					String[] str = inputDetails.split("lost|\\(|steals");
					if (str.length > 2) {
						data.setName(str[0].trim());
						data.setPlay("Turnover");
						data.setDetailPlay("lost ball turnover");
						data.setName_re(str[2].trim());
					}
				} else {
					String[] str = inputDetails.split("out of|lost|possession");
					if (str.length > 0) {
						data.setName(str[0].trim());
						data.setPlay("Turnover");
						data.setDetailPlay("lost ball turnover");
					}
				}
			} else if (inputDetails.contains("shot clock")) {
				data.setPlay("Turnover");
				data.setDetailPlay("shot clock turnover");
			} else if (inputDetails.contains("Out-of")) {
				String[] str = inputDetails.split("Out-of");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("Out-of-Bounds Bad Pass Turnover");
				}
			} else if (inputDetails.contains("kicked")) {
				String[] str = inputDetails.split("kicked");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("kicked ball turnover");
				}
			} else if (inputDetails.contains("back")) {
				String[] str = inputDetails.split("back");
				if (str.length == 2) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("back court turnover");
				}
			} else if (inputDetails.contains("steps")) {
				String[] str = inputDetails.split("steps");
				if (str.length == 2) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("steps out of bounds turnover");
				}
			} else if (inputDetails.contains("offensive")) {
				String[] str = inputDetails.split("offensive");
				if (str.length == 2) {
					data.setName(str[0].trim());
					data.setPlay("Turnover");
					data.setDetailPlay("offensive goaltending turnover");
				}
			} else if (inputDetails.contains("second")) {
				String[] str = inputDetails.split("3");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("3 second");
				}
			} else if (inputDetails.contains("double")) {
				String[] str = inputDetails.split("double");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("double tribble turnover");
				}
			} else if (inputDetails.contains("palming")) {
				String[] str = inputDetails.split("palming");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("palming turnover");
				}
			} else if (inputDetails.contains("disc")) {
				String[] str = inputDetails.split("disc");
				if (str.length > 1) {
					data.setName(str[0].trim());
					data.setPlay("disc dribble turnover");
				}
			} else if (inputDetails.contains("inbound")) {
				data.setPlay("Turnover");
				data.setDetailPlay("5 sec inbound turnover");
			} else if (inputDetails.contains("illegal")) {
				data.setPlay("Turnover");
				data.setDetailPlay("illegal assist turnover");
			} else {
				data.setPlay("Turnover");
			}
		} else if (inputDetails.contains("steals")) {
			String[] str = inputDetails.split("bad|\\(|steals|lost");
			if (str.length > 1) {
				data.setName(str[0].trim());
				data.setPlay("Turnover");
				data.setDetailPlay("Steals");
				data.setName_re(str[2].trim());
			}
		} else if (inputDetails.toLowerCase().contains("charge")) {
			String[] str = inputDetails.split("offensive|\\(|draws");
			if (str.length > 2) {
				data.setName(str[0].trim());
				data.setPlay("Turnover");
				data.setName_re(str[2].trim());
				data.setDetailPlay("Charge turnover");
			}
		} else if (inputDetails.contains("traveling")) {
			String[] str = inputDetails.split("traveling");
			if (str.length > 0) {
				data.setName(str[0].trim());
			}
			data.setPlay("Turnover");
			data.setDetailPlay("traveling turnover");
		} else if (inputDetails.contains("bad pass")) {
			String[] str = inputDetails.split("bad");
			data.setName(str[0].trim());
			data.setPlay("Turnover");
			data.setDetailPlay("traveling turnover");
		} else if (inputDetails.contains("vs.")) {
			String[] str = inputDetails.split("\\(|gains");
			if (str.length > 1) {
				data.setPlay(str[0].trim());
			}
		} else {
			data.setPlay(inputDetails);
		}
		return data;
	}
}
