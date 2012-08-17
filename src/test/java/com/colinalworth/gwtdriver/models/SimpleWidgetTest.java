package com.colinalworth.gwtdriver.models;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.dev.shell.jetty.JettyLauncher;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.user.client.ui.RootPanel;

public class SimpleWidgetTest {
	public static class SmokeTestWidget {
		@Child(type = RootPanel.class)
		private GwtWidget widget;
	}
	
	public void testSmokeTestWidget() {
		SmokeTestWidget test = new SmokeTestWidget();
		
		
	}
	
	public void testWithDriver() throws Exception {
		//start up server
		JettyLauncher launcher = new JettyLauncher();
		launcher.setBindAddress("localhost");
		ServletContainer server = launcher.start(new PrintWriterTreeLogger(), 1234, new File("target/www"));
		
		//start up browser
		WebDriver wd = new FirefoxDriver();//= new HtmlUnitDriver(true);
		wd.get("http://localhost:1234/");
		
		Thread.sleep(1000);
		WidgetContainer widget = new GwtRootPanel(wd);
		System.out.println(widget.getElement().toString());
		
		GwtLabel label = widget.findWidgets(By.xpath("//*")).get(0).as(GwtLabel.class);
		
		assert label != null;
		assert label.getText().equals("testing");
//		assert widget.as(clazz)
		server.stop();
	}
}
