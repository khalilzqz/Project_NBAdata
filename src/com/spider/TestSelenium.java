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
		WebDriver driver = new FirefoxDriver(firefoxbin, null);// 这里使用这个构造方法。

		driver.get("https://jiashenliu.shinyapps.io/NBAShotChart/");// 打开百度
		// 定位输入框，返回一个页面element元素对象
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
		WebDriver wd = new FirefoxDriver(firefoxbin, null);// 这里使用这个构造方法。

		// 打开12306主页
		wd.get("http://www.12306.cn/");

		// 点击购票/预约按钮
		Thread.sleep(1000);
		wd.findElement(By.cssSelector("img[alt=\"购票\"]")).click();

		// 输入用户名、密码，等待手工输入验证码
		Thread.sleep(1000);
		wd.switchTo().frame("iframepage");
		wd.switchTo().frame("main");
		wd.findElement(By.id("UserName")).sendKeys("khalilzqz");
		wd.findElement(By.id("password")).sendKeys("fabregas910511");
		Thread.sleep(10000);

		wd.findElement(By.id("subLink")).click();

		// 进入订票界面，点击车票预定按钮
		Thread.sleep(2000);
		wd.findElement(By.partialLinkText("车票预订")).click();

		// 输入出发地和目的地
		// 出发地无法直接输入，需要借助键盘的上下键和回车键
		Thread.sleep(1000);
		wd.findElement(By.id("fromStationText")).clear();
		wd.findElement(By.id("fromStationText")).sendKeys("武汉");
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.ARROW_DOWN);
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.ARROW_DOWN);
		// 注意这里不能用Keys.ENTER
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.RETURN);

		Thread.sleep(1000);
		wd.findElement(By.id("toStationText")).clear();
		wd.findElement(By.id("toStationText")).sendKeys("长沙");
		((HasInputDevices) wd).getKeyboard().sendKeys(Keys.RETURN);

		// 输入出发日期
		// 现在这个地方不可以直接输入了，可以通过js来输入
		String str = "document.getElementById(\"startdatepicker\").readonly=false";
		String strDate = "document.getElementById(\"startdatepicker\").value=\"2016-10-28\"";
		((JavascriptExecutor) wd).executeScript(str);
		((JavascriptExecutor) wd).executeScript(strDate);
		// 单击查询按钮
		wd.findElement(By.id("submitQuery")).click();

		// 关闭浏览器
		wd.close();
	}
}
