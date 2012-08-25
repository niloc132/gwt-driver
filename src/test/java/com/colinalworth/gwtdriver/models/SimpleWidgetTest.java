package com.colinalworth.gwtdriver.models;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

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
		ServletContainer server = launcher.start(new PrintWriterTreeLogger(), 0, new File("target/www"));
		
		//start up browser
		WebDriver wd = new FirefoxDriver();//= new HtmlUnitDriver(true);
		wd.get("http://" + server.getHost() + ":" + server.getPort() + "/index.html");
		
		Thread.sleep(100000);
		WidgetContainer widget = new GwtRootPanel(wd);
		System.out.println(widget.getElement().toString());
		
		List<GwtWidget> children = widget.findWidgets(By.xpath("//*"));
		assert children.size() == 1;
		GwtLabel label = children.get(0).as(GwtLabel.class);
		
		assert label != null;
		assert label.getText().equals("testing");
//		assert widget.as(clazz)
		
		wd.close();
		
		server.stop();
	}
}
