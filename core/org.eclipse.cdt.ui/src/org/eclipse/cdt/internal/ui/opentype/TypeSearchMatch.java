/*******************************************************************************
 * Copyright (c) 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.opentype;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.search.BasicSearchMatch;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TypeSearchMatch extends BasicSearchMatch
{
	public TypeSearchMatch() {
		super();
	}

	public String getFileName() {
		if (resource != null)
			return resource.getName();
		return "";
	}

	public String getFilePath() {
		if (resource != null)
			return resource.getFullPath().toString();
		return "";
	}

	public String getFileExtension() {
		if (resource != null)
			return resource.getFileExtension();
		return "";
	}

	public String getQualifiedParentName() {
		StringBuffer buf= new StringBuffer();
		if (getFileName().length() > 0)
			buf.append(getFileName());
		if (getParentName().length() > 0) {
			buf.append(OpenTypeMessages.getString("TypeInfoLabelProvider.colon")); //$NON-NLS-1$
			buf.append(getParentName());
		}
		return buf.toString();
	}
	
	public String getFullyQualifiedName() {
		StringBuffer buf= new StringBuffer();
		if (getFilePath().length() > 0)
			buf.append(getFilePath());
		if (getParentName().length() > 0) {
			buf.append(OpenTypeMessages.getString("TypeInfoLabelProvider.colon")); //$NON-NLS-1$
			buf.append(getParentName());
			buf.append("::");
		}
		buf.append(getName());
		return buf.toString();
	}
	
	private boolean matchesCType(ICElement celement, String name) {
		switch (celement.getElementType())
		{
		case ICElement.C_NAMESPACE:
		case ICElement.C_TEMPLATE_CLASS:
		case ICElement.C_CLASS:
		case ICElement.C_STRUCT:
		case ICElement.C_UNION:
		case ICElement.C_ENUMERATION:
		case ICElement.C_TYPEDEF:
			return celement.getElementName().equals(name);

		default:
			return false;
		}
	}
	
	private ICElement findCElement(ICElement celement, String name) {
		if (matchesCType(celement, name))
			return celement;
		else if (celement instanceof IParent) {
			ICElement[] children = ((IParent)celement).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (matchesCType(children[i], name))
					return children[i];
			}
		}
		return null;
	}
	
	public ICElement getCElement() {
		if (resource != null && resource.getType() == IResource.FILE) {
			ICElement parentElement = CoreModel.getDefault().create((IFile)resource);
			if (parentElement instanceof IParent) {
				String parentName = getParentName();
				while (parentElement != null && parentName != null && parentName.length() > 0)
				{
					int pos = parentName.indexOf("::");
					if (pos >= 0) {
						parentElement = findCElement(parentElement, parentName.substring(0, pos));
						parentName = parentName.substring(pos + 2);
					}
					else {
						parentElement = findCElement(parentElement, parentName);
						parentName = null;
					}
				}
				
				if (parentElement != null)
					return findCElement(parentElement, getName());
			}
		}
		return null;
	}
}
