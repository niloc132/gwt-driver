package org.senchalabs.gwt.gwtdriver.models;

/*
 * #%L
 * gwt-driver
 * %%
 * Copyright (C) 2012 - 2013 Sencha Labs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.seleniumhq.jetty7.server.Server;
import org.seleniumhq.jetty7.server.handler.ResourceHandler;
import org.senchalabs.gwt.gwtdriver.models.Dialog.DialogFinder;

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
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setBrowserName("firefox");
			cap.setJavascriptEnabled(true);
			wd = new HtmlUnitDriver(cap);
			wd.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
			String url = "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/index.html";
			System.out.println(url);
			wd.get(url);

			WidgetContainer widget = new GwtRootPanel(wd);
			assert widget.as(GwtRootPanel.class) != null;

			List<GwtWidget<?>> children = widget.findWidgets(By.xpath(".//*"));
			//RootPanel
			//*Label
			//*FlowPanel
			//**TextBox
			//**Button
			assert children.size() == 4 : children.size();
			
			//find Label by iterating through sub-widgets and as'ing
			GwtLabel label = children.get(0).as(GwtLabel.class);
			assert label != null;
			assert label.getText().equals("testing") : label.getText();

			//find label by finder
			GwtLabel label2 = widget.find(GwtLabel.class).withText("testing").done();
			assert label2 != null;
			assert label.getElement().equals(label2.getElement());
			assert label.getText().equals("testing");

			//find, as TextBox as input, verify text and enter new
			Input textBox = children.get(2).as(Input.class);
			assert "asdf".equals(textBox.getValue());
			textBox.sendKeys("fdsa");

			//find, click button
			children.get(3).getElement().click();

			//find dialog by heading
			Dialog headingDialog = new DialogFinder().withHeading("Heading").withDriver(wd).done();
			assert headingDialog != null;
			assert headingDialog.getHeadingText().equals("Heading Text For Dialog");
			//find dialog by top
			Dialog topDialog = new DialogFinder().atTop().withDriver(wd).done();
			assert topDialog != null;
			assert topDialog.getHeadingText().equals("Heading Text For Dialog");

			assert headingDialog.getElement().equals(topDialog.getElement());
			
			Point initialHeaderLoc = topDialog.getElement().getLocation();

			Actions actions = new Actions(wd);
			actions.dragAndDrop(topDialog.getHeaderElement(), children.get(3).getElement());
			actions.build().perform();
			Point movedHeaderLoc = topDialog.getElement().getLocation();

			assert !movedHeaderLoc.equals(initialHeaderLoc);
			//this line is a little screwy in htmlunit
//			assert movedHeaderLoc.equals(children.get(3).getElement().getLocation());

			assert topDialog.getElement().getText().contains("fdsa");
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
