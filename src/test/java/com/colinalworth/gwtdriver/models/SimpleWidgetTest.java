package com.colinalworth.gwtdriver.models;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.seleniumhq.jetty7.server.Server;
import org.seleniumhq.jetty7.server.handler.ResourceHandler;

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

	@Test
	public void testSmokeTestWidget() {
		SmokeTestWidget test = new SmokeTestWidget();


	}

	@Test
	public void testWithDriver() throws Exception {
		Server server = null;
		WebDriver wd = null;
		try {
			//start webserver
			server = new Server(0);
			ResourceHandler handler = new ResourceHandler();
			handler.setResourceBase("target/www");
			handler.setDirectoriesListed(true);
			server.setHandler(handler);

			server.start();


			//start up browser
			wd = new FirefoxDriver();//= new HtmlUnitDriver(true);
			wd.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
			String url = "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/index.html";
			System.out.println(url);
			wd.get(url);

			WidgetContainer widget = new GwtRootPanel(wd);
			assert widget.as(GwtRootPanel.class) != null;

			List<GwtWidget<?>> children = widget.findWidgets(By.xpath(".//*"));
			assert children.size() == 3 : children.size();
			GwtLabel label = children.get(0).as(GwtLabel.class);

			assert label != null;
			assert label.getText().equals("testing") : label.getText();

		} finally {
			if (wd != null) {
				wd.close();
			}
			if (server != null) {
				server.stop();
			}
		}
	}
}
