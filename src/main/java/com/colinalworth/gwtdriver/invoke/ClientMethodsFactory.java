package com.colinalworth.gwtdriver.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ClientMethodsFactory {
	private static final class InvocationHandlerImplementation implements
	InvocationHandler {
		private final WebDriver driver;
		private final String function;

		private InvocationHandlerImplementation(WebDriver driver, String function) {
			this.driver = driver;
			this.function = function;
		}

		@Override
		public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
			Object[] allArgs = new Object[args.length + 1];
			allArgs[0] = method.getName();
			System.arraycopy(allArgs, 1, args, 0, args.length);
			return ((JavascriptExecutor)driver).executeAsyncScript(function + ".apply(this, arguments)", allArgs);
		}
	}

	/**
	 * Creates an instance of the ClientMethods type on the first available module.
	 *
	 * @param type
	 * @param driver
	 * @return
	 */
	public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver) {
		return create(type, driver, findModules(driver).get(0));
	}

	/**
	 * Creates an instance of the ClientMethods type on the given module name.
	 * 
	 * @param type
	 * @param driver
	 * @param moduleName
	 * @return
	 */
	public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver, String moduleName) {
		assert driver instanceof JavascriptExecutor;
		final String function = "_" + moduleName + "_se";
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new InvocationHandlerImplementation(driver, function));
		return proxy;
	}

	/**
	 * Examines the current page for any window with a $moduleName defined on it. Should return
	 * a list of names if any are present - typically will return only one entry, or none if GWT
	 * isn't in use on this page.
	 * 
	 * @param driver
	 * @return a list of module names
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findModules(WebDriver driver) {
		JavascriptExecutor exec = (JavascriptExecutor) driver;

		return (List<String>) exec.executeScript("var n=[];" +
				"function a(c) {c.$modulename && n.push(c.$moduleName)};" +
				"a(window);" +
				"for (var b=0; b<frames.length;b++) {" +
				"a(frames[b]);" +
				"}" +
				"return a;");
	}
}
