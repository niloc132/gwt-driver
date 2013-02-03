package com.colinalworth.gwtdriver.models;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

public class GwtWidgetFinder<W extends GwtWidget<?>> {
	protected WebDriver driver;
	protected WebElement elt;
	public GwtWidgetFinder<W> withDriver(WebDriver driver) {
		this.driver = driver;
		if (elt == null) {
			elt = driver.findElement(By.xpath(".//body"));
		}
		return this;
	}
	public GwtWidgetFinder<W> withElement(WebElement element) {
		if (driver == null && elt instanceof WrapsDriver) {
			driver = ((WrapsDriver) elt).getWrappedDriver();
		}
		this.elt = element;
		return this;
	}
	@SuppressWarnings("unchecked")
	public W done() {
		assert getClass() == GwtWidgetFinder.class : "GwtWidgetFinder.done() must be overridden in all subclasses";
		return (W) new GwtWidget<GwtWidgetFinder<?>>(driver, elt);
	}
}
