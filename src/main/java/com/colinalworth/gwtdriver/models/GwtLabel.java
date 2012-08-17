package com.colinalworth.gwtdriver.models;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GwtLabel extends GwtWidget {

	public GwtLabel(WebDriver driver, WebElement element) {
		super(driver, element);
	}
	
	public String getText() {
		return getElement().getText();
	}

}
