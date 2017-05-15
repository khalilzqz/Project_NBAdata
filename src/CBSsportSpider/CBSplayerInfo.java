package CBSsportSpider;

/**
 * @author zhouqizhao
 */
@SuppressWarnings("rawtypes")
public class CBSplayerInfo implements java.lang.Comparable {
	public String id;
	public String Name;

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

	public String display() {
		return this.id + "|" + this.Name;
	}

	@Override
	public int compareTo(Object o) { // 实现 Comparable 接口的抽象方法，定义排序规则
		CBSplayerInfo p = (CBSplayerInfo) o;
		return Integer.valueOf(this.id) - Integer.valueOf(p.id); // 升序排列，反之降序
	}

}
