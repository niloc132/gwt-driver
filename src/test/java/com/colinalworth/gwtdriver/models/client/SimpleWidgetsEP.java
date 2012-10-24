package com.colinalworth.gwtdriver.models.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SimpleWidgetsEP implements EntryPoint {

	@Override
	public void onModuleLoad() {

		RootPanel.get().add(new Label("testing"));

		FlowPanel panel = new FlowPanel();
		RootPanel.get().add(panel);

		panel.add(new TextBox());
	}

}
