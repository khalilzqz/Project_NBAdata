package com.spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NBAcompare {
	// ϣ���ҵ�js���������Ӳ�������Ϊ��Ϊ��������
	public static void main(String[] args) throws IOException {
		String path2 = "D:/nbaguan.txt";
		String path = "D:/nbafu.txt";
		String com = "";
		String lineTxt2 = "";
		String lineTxt = "";
		try {
			String encoding = "GBK";
			File file = new File(path);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] str = lineTxt.split("#");
					if (str.length > 1) {
						com = str[0];
					}
					Boolean Flag = false;
					try {
						String encoding2 = "GBK";
						File file2 = new File(path2);
						if (file2.isFile() && file2.exists()) { // �ж��ļ��Ƿ����
							InputStreamReader read2 = new InputStreamReader(new FileInputStream(file2), encoding2);// ���ǵ������ʽ
							BufferedReader bufferedReader2 = new BufferedReader(read2);
							while ((lineTxt2 = bufferedReader2.readLine()) != null) {
								if (lineTxt2.contains(com)) {
									Flag = true;
								}
							}
							if (Flag) {
								System.out.println(com);
							}
							read2.close();
						} else {
							System.out.println("�Ҳ���ָ�����ļ�");
						}
					} catch (Exception e) {
						System.out.println("��ȡ�ļ����ݳ���");
						e.printStackTrace();
					}
				}
				read.close();
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}

	}

}
