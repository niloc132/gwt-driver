package com.colinalworth.gwtdriver.models;

import com.google.gwt.user.client.ui.RootPanel;

public class SimpleWidgetTest {
	public static class SmokeTestWidget {
		@Child(type = RootPanel.class)
		private GwtWidget widget;
	}
	
	public void testSmokeTestWidget() {
		SmokeTestWidget test = new SmokeTestWidget();
		
		
	}
}
