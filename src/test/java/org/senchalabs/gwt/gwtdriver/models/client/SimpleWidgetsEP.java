package org.senchalabs.gwt.gwtdriver.models.client;

/*
 * #%L
 * gwt-driver
 * %%
 * Copyright (C) 2012 - 2013 Sencha Labs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SimpleWidgetsEP implements EntryPoint {

	@Override
	public void onModuleLoad() {

		RootPanel.get().add(new Label("testing"));

		FlowPanel panel = new FlowPanel();
		RootPanel.get().add(panel);

		final TextBox textBox = new TextBox();
		textBox.setValue("asdf");
		panel.add(textBox);

		panel.add(new Button("Open dialog", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogBox box = new DialogBox();
				box.setText("Heading Text For Dialog");
				box.add(new HTML(textBox.getValue()));
				box.show();
			}
		}));
	}

}
