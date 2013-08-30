## Browser testing in GWT

GwtDriver brings GWT support to WebDriver tests. We do this by exposing a few type details from the running application
and letting the tests ask questions of the structure of the widgets, rather than forcing them to rely on just the DOM
structure, CSS classes, or element IDs. This way, you can write the application and the tests separatly, without either
becoming dependent on the structure and expectations of the other.

When you test a screen in an application, you usually don't look at the DOM structure and locate an element with the
right ID or XPATH before you click it, but instead rely on the text content or other nearby (usually parent or sibling)
widgets. The gwt-driver project tries to encourage WebDriver tests that are structure and landmark based. This has the
result of writing tests that are more easily maintained, and easier to refactor and reuse as the application continues
to grow.

## Concept

The basic idea is to refer to models that describe the widgets in the application, rather that the dom elements
themselves. It is still possible (and often necessary) to refer to those elements, but for the most part this is only
needed for specific one-off features that don't make sense to build into the models themselves.

Models all extend from `org.senchalabs.gwt.gwtdriver.models.GwtWidget`. They have a few paths from there:

 * Decorated with `org.senchalabs.gwt.gwtdriver.models.GwtWidget.ForWidget`, indicating the gwt Widget type that they
 should represent. Useful for various assertions.
 * Specify a GwtWidgetFinder in their generics, describing the common ways that this widget can be found in a parent.
 More on this below in Finders.

## Locating Widgets

### Finders
TODO

### Using findElement and GwtWidget.as
TODO

## Interacting with Widgets
As each `GwtWidget` model has an internal reference to the WebElement it is based on, interacting with the widget is
usually about as simple as interacting with that element. Typically, each GwtWidget should provide some basic methods
for interaction, but inevitably there will be the need to provide extra functionality - you can invoke `getElement()`
to get the root element in the widget.

## Building your own models

If you've developed your own widgets, you will need to build your own widget models and finders based on the structure
you expect your widgets to be used in and how you expect to need to search for them. The gxt-driver project includes
a number of models and finders that can serve as a basic example of how to find and interact with widgets in a running
application.