package CBSsportSpider;

/**
 * @author zhouqizhao
 */
@SuppressWarnings("rawtypes")
public class CBSpbpData implements java.lang.Comparable {
	public String id;
	public String Name;
	public String Team;
	public String Play;
	public String DetailPlay;
	public Float TimeLeft;
	public String id_re;
	public String Name_re;
	public String distance;
	public int Pts;

	public String getDetailPlay() {
		return DetailPlay;
	}

	public void setDetailPlay(String detailPlay) {
		DetailPlay = detailPlay;
	}

	public int getPts() {
		return Pts;
	}

	public void setPts(int pts) {
		Pts = pts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getTeam() {
		return Team;
	}

	public void setTeam(String team) {
		Team = team;
	}

	public String getPlay() {
		return Play;
	}

	public void setPlay(String play) {
		Play = play;
	}

	public Float getTimeLeft() {
		return TimeLeft;
	}

	public void setTimeLeft(Float timeLeft) {
		TimeLeft = timeLeft;
	}

	public String getId_re() {
		return id_re;
	}

	public void setId_re(String id_re) {
		this.id_re = id_re;
	}

	public String getName_re() {
		return Name_re;
	}

	public void setName_re(String name_re) {
		Name_re = name_re;
	}

	public String display() {
		return this.id + "|" + this.Name;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Object o) { // 实现 Comparable 接口的抽象方法，定义排序规则
		CBSpbpData p = (CBSpbpData) o;
		return Integer.valueOf(this.id) - Integer.valueOf(p.id); // 升序排列，反之降序
	}

	public void printData(CBSpbpData data) {
		System.out.println(data.getTimeLeft() + "|" + data.getName() + "|" + data.getPts() + "|" + data.getPlay() + "|"
				+ data.getName_re());
	}

}
