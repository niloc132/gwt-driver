package com.colinalworth.gwtdriver.models;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
		JettyLauncher server = new JettyLauncher();
		server.setBindAddress("localhost");
		server.start(new PrintWriterTreeLogger(), 1234, new File("target/www"));
		
		Thread.sleep(10000);
		//start up browser
		WebDriver wd = new HtmlUnitDriver(true);
		wd.get("http://localhost:1234/");
		
		GwtWidget widget = new GwtRootPanel(wd);
		
//		assert widget.as(clazz)
	}
}
