package nl.defrog;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.xsp.designer.context.XSPContext;

public class Utils {

	public static String getResource(String bundleVar, String name) {
		XSPContext xspContext = XSPContext.getXSPContext(FacesContext.getCurrentInstance());

		try {
			// There has to be bundle defined with var name <bundleVar>
			ResourceBundle bundle;
			bundle = xspContext.bundle(bundleVar);

			if (bundle != null) {
				return bundle.getString(name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

/*	public static Database getCurrentDatabase() {
		Database database = (Database) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "database");
		return database;
	}*/

	public static String getCookies() {
		return Utils.getRequest().getHeader("cookie");
	}

	public static void setCookie(String name, String value, int maxAge) {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static String getUuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the
	 * specified value binding expression and returns its current value.<br>
	 * <br>
	 * If the expression references a managed bean or its properties and the
	 * bean has not been created yet, it gets created by the JSF runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @return value of ValueBinding throws
	 *         javax.faces.el.ReferenceSyntaxException if the specified
	 *         <code>ref</code> has invalid syntax
	 */
	public static Object getBindingValue(String ref) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return application.createValueBinding(ref).getValue(context);
	}

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the
	 * specified value binding expression and sets a new value for it.<br>
	 * <br>
	 * If the expression references a managed bean and the bean has not been
	 * created yet, it gets created by the JSF runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @param newObject
	 *            new value for the ValueBinding throws
	 *            javax.faces.el.ReferenceSyntaxException if the specified
	 *            <code>ref</code> has invalid syntax
	 */
	public static void setBindingValue(String ref, Object newObject) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ValueBinding binding = application.createValueBinding(ref);
		binding.setValue(context, newObject);
	}

	/**
	 * The method returns the value of a global JavaScript variable.
	 * 
	 * @param varName
	 *            variable name
	 * @return value
	 * @throws javax.faces.el.EvaluationException
	 *             if an exception is thrown while resolving the variable name
	 */
	public static Object getVariableValue(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(context, varName);
	}

	/**
	 * Finds an UIComponent by its component identifier in the current component
	 * tree.
	 * 
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	public static UIComponent findComponent(String compId) {
		return findComponent(FacesContext.getCurrentInstance().getViewRoot(), compId);
	}

	/**
	 * Finds an UIComponent by its component identifier in the component tree
	 * below the specified <code>topComponent</code> top component.
	 * 
	 * @param topComponent
	 *            first component to be checked
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	@SuppressWarnings("unchecked")
	public static UIComponent findComponent(UIComponent topComponent, String compId) {
		if (compId == null)
			throw new NullPointerException("Component identifier cannot be null");

		if (compId.equals(topComponent.getId()))
			return topComponent;

		if (topComponent.getChildCount() > 0) {
			List<UIComponent> childComponents = topComponent.getChildren();

			for (UIComponent currChildComponent : childComponents) {
				UIComponent foundComponent = findComponent(currChildComponent, compId);
				if (foundComponent != null)
					return foundComponent;
			}
		}
		return null;
	}

	public static String toMD5(String input) {
		String hashtext = "";

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes("UTF8"));
			byte[] digest = md.digest();

			BigInteger bigInt = new BigInteger(1, digest);
			hashtext = bigInt.toString(16);

			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hashtext;
	}

/*	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCurrentSessionScope() {

		FacesContext context = FacesContext.getCurrentInstance();

		return (Map<String, Object>) context.getApplication().getVariableResolver().resolveVariable(context, "sessionScope");

	}*/

	/*
	 * Returns the current Notes session instance of the Javascript engine
	 * 
	 * @return Session
	 */
/*	public static Session getCurrentSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Session) context.getApplication().getVariableResolver().resolveVariable(context, "session");
	}*/
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}