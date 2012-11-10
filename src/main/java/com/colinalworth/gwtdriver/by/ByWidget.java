package com.colinalworth.gwtdriver.by;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.colinalworth.gwtdriver.invoke.ClientMethodsFactory;
import com.colinalworth.gwtdriver.invoke.ExportedMethods;
import com.google.gwt.user.client.ui.Widget;

public class ByWidget extends By {
	private final WebDriver driver;
	private final String type;
	public ByWidget(WebDriver driver) {
		this(driver, Widget.class);
	}
	public ByWidget(WebDriver driver, Class<? extends Widget> widgetType) {
		this.driver = driver;
		this.type = widgetType.getName();
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		List<WebElement> elts = context.findElements(By.xpath(".//*"));

		System.out.println("Searching in " + context + " for any widget");
		List<WebElement> ret = new ArrayList<WebElement>();
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		for (WebElement elt : elts) {
			String matches = m.instanceofwidget(elt, type);

			System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
			if ("true".equals(matches)) {
				ret.add(elt);
			}
		}
		System.out.println("Done, found " + ret.size());

		return ret;
	}
	@Override
	public WebElement findElement(SearchContext context) {
		List<WebElement> elts = context.findElements(By.xpath(".//*"));
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		for (WebElement elt : elts) {
			String matches = m.instanceofwidget(elt, type);

			System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
			if ("true".equals(matches)) {
				return elt;
			}
		}
		throw new NoSuchElementException("Can't find widget of type " + type);
	}
	@Override
	public String toString() {
		return "isWidget(" + type + ")";
	}
}