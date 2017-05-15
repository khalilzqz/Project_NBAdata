package com.spider;

import java.io.File;

import org.jboss.netty.util.Timeout;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium {

	public static void main(String[] args) throws InterruptedException {
		File pathToFirefoxBinary = new File("D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		FirefoxBinary firefoxbin = new FirefoxBinary(pathToFirefoxBinary);
		WebDriver driver = new FirefoxDriver(firefoxbin, null);// ����ʹ��������췽����

		driver.get("https://jiashenliu.shinyapps.io/NBAShotChart/");// �򿪰ٶ�
		// ��λ����򣬷���һ��ҳ��elementԪ�ض���
		WebElement alink = driver.findElement(By.linkText("Shot Log Download"));
		alink.click();
		// WebElement element = driver.findElement(By.id("pid"));
		// element.sendKeys("2772");
		WebElement blink = driver.findElement(By.id("downloadData"));

		WebElement e = (new WebDriverWait(driver, 10)).until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				return d.findElement(By.id("id locator"));
			}
		});
		driver.close();
	}

	public void test() throws InterruptedException {

		File pathToFirefoxBinary = new File("D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		FirefoxBinary firefoxbin = new FirefoxBinary(pathToFirefoxBinary);
		WebDriver wd = new FirefoxDriver(firefoxbin, null);// ����ʹ��������췽����

		// ��12306��ҳ
		wd.get("http://www.12306.cn/");

		// �����Ʊ/ԤԼ��ť
		Thread.sleep(1000);
		wd.findElement(By.cssSelector("img[alt=\"��Ʊ\"]")).click();

		// �����û��������룬�ȴ��ֹ�������֤��
		Thread.sleep(1000);
		wd.switchTo().frame("iframepage");
		wd.switchTo().frame("main");
		wd.findElement(By.id("UserName")).sendKeys("khalilzqz");
		wd.findElement(By.id("password")).sendKeys("fabregas910511");
		Thread.sleep(10000);

		wd.findElement(By.id("subLink")).click();

		// ���붩Ʊ���棬�����ƱԤ����ť
		Thread.sleep(2000);
		wd.findElement(By.partialLinkText("��ƱԤ��")).click();

		// ��������غ�Ŀ�ĵ�
		// �������޷�ֱ�����룬��Ҫ�������̵����¼��ͻس���
		Thread.sleep(1000);
		wd.findElement(By.id("fromStationText")).clear();
		wd.findElement(By.id("fromStationText")).sendKeys("�人");
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.ARROW_DOWN);
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.ARROW_DOWN);
		// ע�����ﲻ����Keys.ENTER
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.RETURN);

		Thread.sleep(1000);
		wd.findElement(By.id("toStationText")).clear();
		wd.findElement(By.id("toStationText")).sendKeys("��ɳ");
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.RETURN);

		// �����������
		// ��������ط�������ֱ�������ˣ�����ͨ��js������
		String str = "document.getElementById(\"startdatepicker\").readonly=false";
		String strDate = "document.getElementById(\"startdatepicker\").value=\"2016-10-28\"";
		((JavascriptExecutor) wd).executeScript(str);
		((JavascriptExecutor) wd).executeScript(strDate);
		// ������ѯ��ť
		wd.findElement(By.id("submitQuery")).click();

		// �ر������
		wd.close();
	}
}
