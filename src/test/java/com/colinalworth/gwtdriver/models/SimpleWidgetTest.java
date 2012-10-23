package com.colinalworth.gwtdriver.models;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.dev.shell.jetty.JettyLauncher;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Simple initial tests to make sure the basics pass a smoke test, so lots of manual setup
 *
 */
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
		ServletContainer server = launcher.start(new PrintWriterTreeLogger(new File("target/www-out.log")), 0, new File("target/www"));

		//start up browser
		WebDriver wd = new FirefoxDriver();//= new HtmlUnitDriver(true);
		wd.manage().timeouts().setScriptTimeout(1000, TimeUnit.SECONDS);
		wd.get("http://" + server.getHost() + ":" + server.getPort() + "/index.html");

		Thread.sleep(1000);

		try {
			WidgetContainer widget = new GwtRootPanel(wd);
			assert widget.as(GwtRootPanel.class) != null;

			List<GwtWidget<?>> children = widget.findWidgets(By.xpath("*"));
			assert children.size() == 1;
			GwtLabel label = children.get(0).as(GwtLabel.class);

			assert label != null;
			assert label.getText().equals("testing");

		} finally {
			wd.close();
			server.stop();
		}
	}
}
