package com.colinalworth.gwtdriver.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.colinalworth.gwtdriver.ModuleUtilities;

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
			allArgs[0] = method;
			System.arraycopy(args, 0, allArgs, 1, args.length);
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
		return create(type, driver, ModuleUtilities.findModules(driver).get(0));
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
}
