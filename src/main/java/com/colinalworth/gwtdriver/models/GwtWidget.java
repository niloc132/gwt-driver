package com.colinalworth.gwtdriver.models;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * Represents a GWT Widget class, allowing some 
 * @author colin
 *
 */
public class GwtWidget {
	private final WebDriver driver;
	private final WebElement element;
	
	public GwtWidget(WebDriver driver, WebElement element) {
		this.driver = driver;
		this.element = element;
	}
	
	public WebElement getElement() {
		return element;
	}
	public WebDriver getDriver() {
		return driver;
	}
	

	
	public <W extends GwtWidget> W as(Class<W> clazz) {
		try {
			W instance;
			instance = clazz.getConstructor(WebDriver.class, WebElement.class).newInstance(getDriver(), getElement());
			return instance;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static class ByWidget extends By {
		@Override
		public List<WebElement> findElements(SearchContext context) {
		      return ((FindsByXPath) context).findElementsByXPath(".//*[@__listener]");
		}
		@Override
		public String toString() {
			return "isWidget";
		}
	}
}
