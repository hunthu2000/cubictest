/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.selenese.converters;

import java.util.Iterator;
import java.util.List;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Class to convert transitions to selenium commands.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<SeleneseDocument> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir doc.
	 * 
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(SeleneseDocument doc, UserInteractionsTransition transition) {
		List actions = transition.getUserInteractions();
		Iterator it = actions.iterator();
		while(it.hasNext()) {
			UserInteraction action = (UserInteraction) it.next();
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + action);
				continue;
			}
			handleUserInteraction(doc, action);
		}
	}
	
	
	/**
	 * Converts a single user interaction to a Selenium command (selenese row).
	 */
	private void handleUserInteraction(SeleneseDocument doc, UserInteraction userInteraction) {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);
		String commandDesc = SeleniumUtils.getCommandDescription(actionType, element);

		String locator = "";
		String inputValue = "";
		
		if (element instanceof Option) {
			Option option = (Option) element;
			Identifier optionMainId = option.getMainIdentifier();
			Select select = option.getParent();
			//get locator of select-box:
			locator = "xpath=" + doc.getFullContext() + SeleniumUtils.getXPath(select, doc, true);
			inputValue = SeleniumUtils.getIdType(optionMainId) + "=" + optionMainId.getValue();
		}
		else {
			//all other elements
			String context = doc.getFullContext();
			if (element instanceof PageElement) {
				//get context of page element
				context = doc.getFullContext((PageElement) element);
			}
			locator = "xpath=" + context + SeleniumUtils.getXPath(element, doc, true);
			inputValue = SeleniumUtils.getValue(userInteraction, doc);
		}
		
		doc.addCommand(commandName, locator, inputValue).setDescription(commandDesc);
	}

	
	
}
