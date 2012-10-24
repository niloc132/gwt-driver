package com.colinalworth.gwtdriver.invoke;

import org.openqa.selenium.WebElement;

public interface ExportedMethods extends ClientMethods {
	String isWidget(WebElement elt);
	
	String instanceofwidget(WebElement elt, String type);
	
	String getContainingWidgetClass(WebElement elt);
	
	WebElement getContainingWidgetElt(WebElement elt);
	
//	String getClass(Object obj);
	
//	String instanceOf(String type, Object instance);
}
