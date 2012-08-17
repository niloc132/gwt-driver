package com.colinalworth.gwtdriver.models.client;

import com.colinalworth.gwtdriver.client.SeleniumExporter;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class SimpleWidgetsEP implements EntryPoint {

	@Override
	public void onModuleLoad() {
		SeleniumExporter.export();
		
		RootPanel.get().add(new Label("testing"));
	}

}
