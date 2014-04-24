package nl.defrog;

/*

 <!-- 
 Copyright 2010 Tim Tripcony
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License
 -->

 */

import java.util.Map;
import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.openntf.domino.*;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.application.DesignerApplicationEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.designer.context.XSPContext;

public class JSFUtil {

	// private static OpenLogItem oli = new OpenLogItem();
	private static Session _signerSess;

	public static DesignerApplicationEx getApplication() {
		return (DesignerApplicationEx) getFacesContext().getApplication();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getApplicationScope() {
		return getServletContext().getApplicationMap();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCompositeData() {
		return (Map<String, Object>) getVariableResolver().resolveVariable(getFacesContext(), "compositeData");
	}

	public static Database getCurrentDatabase() {
		return (Database) getVariableResolver().resolveVariable(getFacesContext(), "database");
	}

	public static Session getCurrentSession() {
		return (Session) getVariableResolver().resolveVariable(getFacesContext(), "session");
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequestScope() {
		return getServletContext().getRequestMap();
	}

	public static ExternalContext getServletContext() {
		return getFacesContext().getExternalContext();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSessionScope() {
		return getServletContext().getSessionMap();
	}

	private static VariableResolver getVariableResolver() {
		return getApplication().getVariableResolver();
	}

	public static UIViewRootEx getViewRoot() {
		return (UIViewRootEx) getFacesContext().getViewRoot();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getViewScope() {
		return getViewRoot().getViewMap();
	}

	public static XSPContext getXSPContext() {
		return XSPContext.getXSPContext(getFacesContext());
	}

	public static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), variable);
	}

	@SuppressWarnings("unchecked")
	public static String getProfileUNID(String profileKey) {
		Vector profHash = getCurrentSession().evaluate("@Text(@Password(\"" + profileKey + "\"))");
		String tmpUNID = profHash.firstElement().toString();
		tmpUNID = tmpUNID.substring(1, tmpUNID.length() - 1);
		return tmpUNID;
	}
}