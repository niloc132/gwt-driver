package com.colinalworth.gwtdriver.models;

import com.google.gwt.user.client.ui.Widget;

public @interface Child {
	Class<? extends Widget> type() default Widget.class;
}
