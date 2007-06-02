/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.utils;

import static org.junit.Assert.assertEquals;

import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.TextArea;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Context Holder.
 * 
 * @author Christian Schwarz
 *
 */
public class ContextHolderTest {

	ContextHolder holder;
	SimpleContext outerContext;
	SimpleContext innerContext1;
	SimpleContext innerContext2;
	Link link;
	TextArea textArea;
	Image image;
	
	@Before
	public void setUp() throws CoreException {
		holder = new ContextHolder();

		outerContext = new SimpleContext();
		Identifier id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("outerId");
		id.setProbability(100);
		outerContext.addIdentifier(id);

		innerContext1 = new SimpleContext();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("inner1Id");
		id.setProbability(100);
		innerContext1.addIdentifier(id);

		innerContext2 = new SimpleContext();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("inner2Id");
		id.setProbability(100);
		innerContext2.addIdentifier(id);

		link = new Link();
		id = new Identifier();
		id.setType(IdentifierType.HREF);
		id.setValue("linkHref");
		id.setProbability(100);
		link.addIdentifier(id);
		
		textArea = new TextArea();
		id = new Identifier();
		id.setType(IdentifierType.NAME);
		id.setValue("textAreaName");
		id.setProbability(100);
		textArea.addIdentifier(id);
		
		image = new Image();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("imageId");
		id.setProbability(100);
		image.addIdentifier(id);
	}
		
	@Test
	public void testSingleElement() {
		String exp = "//a[@href=\"linkHref\"]";
		assertEquals(exp, holder.getXPathWithFullContextAndPreviousElements(link));
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	@Test
	public void testContextWithOneElement() {
		outerContext.addElement(link);
		holder.pushContext(outerContext);
		String exp = "//*[@id=\"outerId\"]//a[@href=\"linkHref\"]";
		assertEquals(exp, holder.getXPathWithFullContextAndPreviousElements(link));
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	@Test
	public void testNestedContextWithOneElement() {
		outerContext.addElement(innerContext1);
		innerContext1.addElement(link);
		holder.pushContext(outerContext);
		holder.pushContext(innerContext1);
		String exp = "//*[@id=\"outerId\"]//*[@id=\"inner1Id\"]//a[@href=\"linkHref\"]";
		assertEquals(exp, holder.getXPathWithFullContextAndPreviousElements(link));
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	

	
	@Test
	public void testContextWithThreeElements() {
		outerContext.addElement(link);
		outerContext.addElement(image);
		outerContext.addElement(textArea);
		holder.pushContext(outerContext);
		String expPrev = "//*[@id=\"outerId\"][.//a[@href=\"linkHref\"]]//img[@id=\"imageId\"]";
		assertEquals(expPrev, holder.getXPathWithFullContextAndPreviousElements(image));

		String expAll = "//*[@id=\"outerId\"][.//a[@href=\"linkHref\"] and .//img[@id=\"imageId\"] and .//textarea[@name=\"textAreaName\"]]//img[@id=\"imageId\"]";
		assertEquals(expAll, holder.getFullContextWithAllElements(image));
	}
	
	
	@Test
	public void testNestedContextWithThreeElements() {
		outerContext.addElement(innerContext1);
		innerContext1.addElement(link);
		innerContext1.addElement(image);
		innerContext1.addElement(textArea);
		holder.pushContext(outerContext);
		holder.pushContext(innerContext1);
		String expPrev = "//*[@id=\"outerId\"]//*[@id=\"inner1Id\"][.//a[@href=\"linkHref\"]]//img[@id=\"imageId\"]";
		assertEquals(expPrev, holder.getXPathWithFullContextAndPreviousElements(image));

		String expAll = "//*[@id=\"outerId\"]//*[@id=\"inner1Id\"][.//a[@href=\"linkHref\"] and .//img[@id=\"imageId\"] and .//textarea[@name=\"textAreaName\"]]//img[@id=\"imageId\"]";
		assertEquals(expAll, holder.getFullContextWithAllElements(image));
	}
	
	@Test
	public void testNestedContextWithThreeInnerContexts() throws CloneNotSupportedException {
		SimpleContext outerContext1 = outerContext;
		SimpleContext outerContext2 = (SimpleContext) outerContext.clone();
		SimpleContext outerContext3 = (SimpleContext) outerContext.clone();
		
		SimpleContext innerContext1 = (SimpleContext) this.innerContext1.clone();
		SimpleContext innerContext2 = (SimpleContext) innerContext1.clone();
		SimpleContext innerContext3 = (SimpleContext) innerContext1.clone();

		outerContext1.addElement(innerContext1);
		outerContext2.addElement(innerContext2);
		outerContext3.addElement(innerContext3);
		outerContext1.addElement(link);
		outerContext2.addElement(image);
		outerContext3.addElement(textArea);
		
		Link innerLink = new Link();
		Identifier id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("innderLinkId");
		id.setProbability(100);
		innerLink.addIdentifier(id);
		
		innerContext1.addElement(innerLink);innerContext1.addElement(textArea);
		innerContext2.addElement(innerLink);innerContext2.addElement(textArea);
		innerContext3.addElement(innerLink);innerContext3.addElement(textArea);
		
		holder.pushContext(outerContext2);
		holder.pushContext(innerContext2);
		
		String expPrev = "//*[@id=\"outerId\"][.//*[@id=\"inner1Id\"] and .//img[@id=\"imageId\"]]//*[@id=\"inner1Id\"]//a[@id=\"innderLinkId\"]";
		assertEquals(expPrev, holder.getXPathWithFullContextAndPreviousElements(innerLink));

		String expAll =  "//*[@id=\"outerId\"][.//*[@id=\"inner1Id\"] and .//img[@id=\"imageId\"]]//*[@id=\"inner1Id\"][.//a[@id=\"innderLinkId\"] and .//textarea[@name=\"textAreaName\"]]//a[@id=\"innderLinkId\"]";
		assertEquals(expAll, holder.getFullContextWithAllElements(innerLink));
	}
	
}