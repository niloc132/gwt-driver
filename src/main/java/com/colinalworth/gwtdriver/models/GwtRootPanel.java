package com.colinalworth.gwtdriver.models;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Represents a {@link RootPanel}. Has convinience constructors for the body tag and elements with an id.
 * @author colin
 *
 */
public class GwtRootPanel extends WidgetContainer {

	public GwtRootPanel(WebDriver driver, WebElement element) {
		super(driver, element);
	}
	public GwtRootPanel(WebDriver driver, String id) {
		this(driver, driver.findElement(By.id(id)));
	}
	public GwtRootPanel(WebDriver driver) {
		this(driver, driver.findElement(By.tagName("body")));
	}
	
	
}
