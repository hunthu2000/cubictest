/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import static org.cubictest.model.ActionType.BLUR;
import static org.cubictest.model.ActionType.FOCUS;
import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.TITLE;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class for form elements.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public abstract class FormElement extends PageElement{

	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.addAll(super.getActionTypes());
		actions.add(FOCUS);
		actions.add(BLUR);
		return actions;
	}

	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(IdentifierType.NAME);
		list.add(ID);
		list.add(TITLE);
		list.add(CLASS);
		return list;
	}
	
	@Override
	protected void setDefaultIdentifierValues() {
		//leave the ID's as the constructor made them
	}
}
