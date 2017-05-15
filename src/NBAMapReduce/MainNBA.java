package NBAMapReduce;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.rs.rsplat.util.Utilities;

public class MainNBA {
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		Configuration conf = new Configuration();

		String outPath = "/user/zqz/NBAProj/test1";
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "NBAMapReduce");
		job.setJarByClass(MainNBA.class);
		job.setMapOutputKeyClass(OutputKey.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setCombinerClass(NBACombiner.class);
		job.setReducerClass(NBAReducer.class);
		job.setNumReduceTasks(1);
		job.setGroupingComparatorClass(OutputKeyGroupingComparator.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileSystem fs = FileSystem.get(conf);

		String[] cachePath = { "/user/zqz/cache/NBAid.txt", };

		for (int i = 0; i < 1; i++) {
			job.addCacheFile(new Path(cachePath[i]).toUri());
		}
		String inpath = "/daas/bstl/dpifix/";
		if (fs.exists(new Path(inpath))) {
			MultipleInputs.addInputPath(job, new Path(inpath), SequenceFileInputFormat.class, NBAMapper.class);
		}

		fs.delete(new Path(outPath), true);
		FileOutputFormat.setOutputPath(job, new Path(outPath));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class NBAMapper extends Mapper<LongWritable, Text, OutputKey, Text> {
		HashSet<String> hostList = new HashSet<>();
		HashMap<String, String> reList = new HashMap<>();
		List<String> pathList = new ArrayList<String>();

		HashSet<String> reT1 = new HashSet<>();
		HashSet<String> reT2 = new HashSet<>();
		HashSet<String> reT3 = new HashSet<>();
		HashSet<String> reT4 = new HashSet<>();

		public static String getHostName(String urlString) {
			int index = urlString.indexOf("://");
			if (index != -1) {
				urlString = urlString.substring(index + 3);
			}
			index = urlString.indexOf("/");
			if (index != -1) {
				urlString = urlString.substring(0, index);
			}
			return urlString;
		}

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {

			pathList.add("NBAid");

			String tarUrl = "";
			String reToken = "";
			String tarHost = "";

			Integer reCount1 = 0;
			Integer reCount2 = 0;
			Integer reCount3 = 0;
			Integer reCount4 = 0;

			// 获取分布式缓存
			if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {

				for (String path : pathList) {
					File Info = new File("./" + path + ".txt");
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(Info));
						String line = null;
						while ((line = reader.readLine()) != null) {

							String[] temp = line.trim().split("\\|");
							if (temp.length == 5) {
								tarUrl = temp[0];
								reT1.add(temp[1]);
								reT2.add(temp[2]);
								reT3.add(temp[3]);
								reT4.add(temp[4]);
								reCount1 = reT1.size();
								reCount2 = reT2.size();
								reCount3 = reT3.size();
								reCount4 = reT4.size();
								reToken = reCount1 + "-" + temp[1] + "|" + reCount2 + "-" + temp[2] + "|" + reCount3
										+ "-" + temp[3] + "|" + reCount4 + "-" + temp[4];
								reList.put(tarUrl, reToken);

								tarHost = getHostName(tarUrl);
								hostList.add(tarHost);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		/**
		 * 进行分段，
		 */
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			OutputKey key1 = new OutputKey();
			Text value1 = new Text();

			String rIp = "";
			String URL = "";
			String userAcount = "";
			String HOST = "";
			String Cookie = "";
			Long accessTime = 0l;

			String urlToken1 = "";
			String urlToken2 = "";
			String urlToken3 = "";
			String urlToken4 = "";

			String[] cur = value.toString().trim().split("\\|");
			if (cur.length == 12) {
				byte[] deHost = Base64.decodeBase64(cur[6].replaceAll("\\s", ""));
				HOST = new String(deHost);
				byte[] deUrl = Base64.decodeBase64(cur[7].replaceAll("\\s", ""));
				URL = new String(deUrl);
				rIp = cur[2];
				userAcount = cur[0];
				Cookie = cur[10];
				if (hostList.contains(HOST)) {
					// 时间合法性
					final String reg = "\\d+\\.{0,1}\\d*";
					boolean isDigits = cur[11].trim().matches(reg);
					if (isDigits) {
						accessTime = Long.valueOf(cur[11].trim());
					}

					for (Entry<String, String> entry : reList.entrySet()) {
						String urlToken = entry.getKey();
						if (URL.contains(urlToken)) {
							// 用户-标签
							key1.setKey("0|" + userAcount + "," + Cookie);
							key1.setOrder(0l);
							value1.set(entry.getValue());
							context.write(key1, value1);

							// 统计
							String[] temp = entry.getValue().trim().split("\\|");
							if (temp.length == 4) {
								urlToken1 = temp[0];
								urlToken2 = temp[1];
								urlToken3 = temp[2];
								urlToken4 = temp[3];

								Long ipToLong = Utilities.ipToLong(rIp);
								// url
								key1.setKey("10|" + urlToken);
								key1.setOrder(ipToLong);
								value1.set(ipToLong + "|1");
								context.write(key1, value1);
								// 标签1
								key1.setKey("11|" + urlToken1);
								key1.setOrder(ipToLong);
								value1.set(ipToLong + "|1");
								// context.write(key1, value1);
								// 标签2
								key1.setKey("12|" + urlToken2);
								key1.setOrder(ipToLong);
								value1.set(ipToLong + "|1");
								// context.write(key1, value1);
								// 标签3
								key1.setKey("13|" + urlToken3);
								key1.setOrder(ipToLong);
								value1.set(ipToLong + "|1");
								// context.write(key1, value1);
								// 标签4
								key1.setKey("14|" + urlToken4);
								key1.setOrder(ipToLong);
								value1.set(ipToLong + "|1");
								// context.write(key1, value1);
							}
						}
					}
				}
			}
		}
	}

	public static class NBACombiner extends Reducer<OutputKey, Text, OutputKey, Text> {
		HashMap<String, Long> hm = new HashMap<String, Long>();

		@Override
		protected void reduce(OutputKey key, Iterable<Text> value, Context context)
				throws IOException, InterruptedException {
			String strKey = key.getKey();
			Text value2 = new Text();
			String[] split;
			hm.clear();
			if (strKey.startsWith("1")) {
				for (Text val : value) {
					split = val.toString().trim().split("\\|");
					if (hm.containsKey(split[0])) {
						hm.put(split[0], hm.get(split[0]) + Long.valueOf(split[1]));
					} else {
						hm.put(split[0], Long.valueOf(split[1]));
					}
				}
				for (Entry<String, Long> entry : hm.entrySet()) {
					value2.set(entry.getKey() + "|" + entry.getValue());
					context.write(key, value2);
				}
			} else {
				for (Text val : value) {
					context.write(key, val);
				}
			}
		}
	}

	public static class NBAReducer extends Reducer<OutputKey, Text, Text, Text> {

		private MultipleOutputs<Text, Text> mos;
		Text key3 = new Text();
		Text value3 = new Text();

		HashSet<String> reT1 = new HashSet<>();
		HashSet<String> reT2 = new HashSet<>();
		HashSet<String> reT3 = new HashSet<>();
		HashSet<String> reT4 = new HashSet<>();

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			mos = new MultipleOutputs<Text, Text>(context);
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			for (Iterator<String> it = reT1.iterator(); it.hasNext();) {
				String str = it.next();
				key3.set(str);
				value3.set("");
				mos.write(key3, value3, "token1List");
			}
			mos.close();
		}

		public static HashMap<String, Long> typeCount(HashMap<String, Long> hm, String str) {
			if (hm.containsKey(str)) {
				Long num = hm.get(str);
				num++;
				hm.put(str, num);
			} else {
				hm.put(str, 1l);
			}
			return hm;
		}

		@Override
		public void reduce(OutputKey key, Iterable<Text> value, Context context)
				throws IOException, InterruptedException {
			String strKey = key.getKey();
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			if (strKey.startsWith("1")) {
				String[] split;
				Long lon = 0l;
				Long uv = 0l;
				Long pv = 0l;
				Long tmp = 0l;
				for (Text val : value) {
					split = val.toString().trim().split("\\|");
					tmp = Long.valueOf(split[0]);
					pv += Long.valueOf(split[1]);
					if (lon == 0) {
						lon = tmp;
						uv++;
					} else {
						if (tmp != lon) {
							uv++;
							lon = tmp;
						}
					}
				}
				key3.set(key.getKey());
				value3.set(pv + "|" + uv);
				mos.write(key3, value3, "pvuv");
			} else {
				String[] split;
				HashMap<String, Long> tokenCount1 = new HashMap<>();
				HashMap<String, Long> tokenCount2 = new HashMap<>();
				HashMap<String, Long> tokenCount3 = new HashMap<>();
				HashMap<String, Long> tokenCount4 = new HashMap<>();

				for (Text val : value) {
					split = val.toString().trim().split("\\|");
					if (split.length == 4) {
						tokenCount1 = typeCount(tokenCount1, split[0]);
						tokenCount2 = typeCount(tokenCount2, split[1]);
						tokenCount3 = typeCount(tokenCount3, split[2]);
						tokenCount4 = typeCount(tokenCount4, split[3]);
					}
				}
				// 聚类需要的，1-4，2-3
				for (Entry<String, Long> entry : tokenCount1.entrySet()) {
					String[] temp = entry.getKey().split("-");
					if (temp.length == 2) {
						String type = temp[1];
						Long number = Long.valueOf(temp[0]);
						Long count = entry.getValue();
						sb1.append(number + "-" + count + ",");
						sb2.append(number + ",");
					}
				}
				// String sbb1 = sb1.toString().substring(0, sb1.length() - 1);
				key3.set(key.getKey());
				value3.set(sb1.toString());
				mos.write(key3, value3, "Ju_token1");
				// 相关性需要的
				// String sbb2 = sb2.toString().substring(0, sb2.length() - 1);
				key3.set(key.getKey());
				value3.set(sb2.toString());
				mos.write(key3, value3, "Re_token1");
			}
		}
	}

	/**
	 * 二次排序用的
	 * 
	 * @author ron
	 * 
	 */
	private static class OutputKey implements WritableComparable<OutputKey> {

		private String key = "";
		private Long order = 0l;

		public OutputKey() {
		}

		@SuppressWarnings("unused")
		public OutputKey(String key) {
			this.key = key;
			this.order = 0l;
		}

		@SuppressWarnings("unused")
		public OutputKey(String key, Long order) {
			this.key = key;
			this.order = order;
		}

		public String getKey() {
			return this.key;
		}

		public long getOrder() {
			return this.order;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setOrder(Long order) {
			this.order = order;
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			this.key = in.readUTF();
			this.order = in.readLong();
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeUTF(key);
			out.writeLong(this.order);
		}

		@Override
		public int compareTo(OutputKey other) {
			int compare = this.key.compareTo(other.key);
			if (compare != 0) {
				return compare;
			} else if (this.order != other.order) {
				return order < other.order ? -1 : 1;
			} else {
				return 0;
			}
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		static { // register this comparator
			WritableComparator.define(OutputKey.class, new OutputKeyComparator());
		}
	}

	// key的比较函数
	private static class OutputKeyComparator extends WritableComparator {
		protected OutputKeyComparator() {
			super(OutputKey.class, true);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable w1, WritableComparable w2) {

			OutputKey p1 = (OutputKey) w1;
			OutputKey p2 = (OutputKey) w2;

			int cmp = p1.getKey().compareTo(p2.getKey());
			if (cmp != 0) {
				return cmp;
			}
			return p1.getOrder() == p2.getOrder() ? 0 : (p1.getOrder() < p2.getOrder() ? -1 : 1);
		}
	}

	// reduce阶段的分组函数
	@SuppressWarnings("unused")
	private static class OutputKeyGroupingComparator extends WritableComparator {

		protected OutputKeyGroupingComparator() {
			super(OutputKey.class, true);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable o1, WritableComparable o2) {

			OutputKey p1 = (OutputKey) o1;
			OutputKey p2 = (OutputKey) o2;

			return p1.getKey().compareTo(p2.getKey());
		}
	}
}
