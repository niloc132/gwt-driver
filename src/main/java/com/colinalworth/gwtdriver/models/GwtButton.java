package com.colinalworth.gwtdriver.models;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GwtButton extends GwtWidget {

	public GwtButton(WebDriver driver, WebElement element) {
		super(driver, element);
	}

	public void click() {
		getElement().click();
	}
	
	public String getText() {
		return getElement().getText();
	}
}
