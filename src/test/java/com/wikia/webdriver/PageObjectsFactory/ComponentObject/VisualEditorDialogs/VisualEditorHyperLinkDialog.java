package com.wikia.webdriver.PageObjectsFactory.ComponentObject.VisualEditorDialogs;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.wikia.webdriver.Common.Core.Assertion;
import com.wikia.webdriver.Common.Logging.PageObjectLogging;
import com.wikia.webdriver.PageObjectsFactory.PageObject.WikiBasePageObject;
import com.wikia.webdriver.PageObjectsFactory.PageObject.VisualEditor.VisualEditorPageObject;

/**
 * @author Karol 'kkarolk' Kujawiak
 * @author Robert 'rochan' Chan
 *
 */
public class VisualEditorHyperLinkDialog extends WikiBasePageObject {

	@FindBy(css=".oo-ui-widget .oo-ui-frame")
	private WebElement hyperLinkIFrame;
	@FindBy(css=".oo-ui-icon-previous")
	private WebElement previousButton;
	@FindBy(css=".oo-ui-icon-remove")
	private WebElement removeButton;
	@FindBy(css=".ve-ui-mwLinkTargetInputWidget input")
	private WebElement linkInput;
	@FindBy(css=".oo-ui-texture-pending")
	private WebElement inputPending;
	@FindBy(css=".oo-ui-optionWidget-selected")
	private WebElement selectedResult;
	@FindBy(css=".ve-ui-desktopContext")
	private WebElement desktopContext;
	@FindBy(css=".ve-ui-mwLinkTargetInputWidget")
	private WebElement linkInputDiv;
	@FindBy(css=".oo-ui-window-title")
	private WebElement title;
	@FindBy(css=".oo-ui-frame-html")
	private WebElement frame;
	@FindBy(css=".ve-ui-mwLinkTargetInputWidget-menu")
	private WebElement resultsMenu;

	private By linkResultMenuBy = By.cssSelector(".ve-ui-mwLinkTargetInputWidget-menu");
	private By matchingResultBy = By.cssSelector(".oo-ui-optionWidget-selected span");
	private By linkResultsBy = By.cssSelector("li");
	private By linkCategoryBy = By.cssSelector(".oo-ui-labeledElement-label");
	private By selectedResultsBy = By.cssSelector(".oo-ui-optionWidget-selected");

	private String menuSectionItemText = "oo-ui-menuSectionItemWidget";

	private int[] pageCategoryIndex = new int[4];
	private final int NEWPAGEINDEX = 0;
	private final int MATCHINGPAGEINDEX = 1;
	private final int EXTERNALLINKINDEX = 2;
	private final int REDIRECTPAGEINDEX = 3;

	public VisualEditorHyperLinkDialog(WebDriver driver) {
		super(driver);
		pageCategoryIndex[NEWPAGEINDEX] = -1;
		pageCategoryIndex[MATCHINGPAGEINDEX] = -1;
		pageCategoryIndex[EXTERNALLINKINDEX] = -1;
		pageCategoryIndex[REDIRECTPAGEINDEX] = -1;
	}

	public void typeInLinkInput(String text) {
		waitForElementByElement(hyperLinkIFrame);
		driver.switchTo().frame(hyperLinkIFrame);
		waitForElementVisibleByElement(linkInput);
		waitForElementClickableByElement(linkInput);
		linkInput.sendKeys(text);
		driver.switchTo().defaultContent();
	}

	private void viewLinkResults() {
		waitForElementNotVisibleByElement(inputPending);
		waitForElementByElement(desktopContext);
		WebElement selectedResult = desktopContext.findElement(selectedResultsBy);
		waitForElementByElement(selectedResult);
		WebElement linkResultMenu = desktopContext.findElement(linkResultMenuBy);
		List<WebElement> linkResults = linkResultMenu.findElements(linkResultsBy);
		for (int i = 0; i< linkResults.size(); i++) {
			WebElement linkResult = linkResults.get(i);
			String elementClassName = linkResult.getAttribute("class");
			if (elementClassName.contains(menuSectionItemText)) {
				String linkCategory = linkResult.findElement(linkCategoryBy).getAttribute("title");
				switch(linkCategory) {
				case "New page":
					pageCategoryIndex[NEWPAGEINDEX] = i;
					break;
				case "Matching page":
					pageCategoryIndex[MATCHINGPAGEINDEX] = i;
					break;
				case "Matching pages":
					pageCategoryIndex[MATCHINGPAGEINDEX] = i;
					break;
				case "Redirect page":
					pageCategoryIndex[REDIRECTPAGEINDEX] = i;
					break;
				case "External link":
					pageCategoryIndex[EXTERNALLINKINDEX] = i;
					break;
				}
			}
		}
		PageObjectLogging.log("viewResults", "Category indexes sorted", true);
	}

	private boolean isCategoryResult(int category) {
		viewLinkResults();
		if (pageCategoryIndex[category] == -1)
			return false;
		else
			return true;
	}

	public void isNewPage() {
		Assertion.assertTrue(isCategoryResult(NEWPAGEINDEX), "New page index not found");
		PageObjectLogging.log("isNewPage", "New page index found", true);
	}

	public void isMatchingPage() {
		Assertion.assertTrue(isCategoryResult(MATCHINGPAGEINDEX), "Matching page index not found");
		PageObjectLogging.log("isMatchingPage", "Matching page index found", true);
	}

	public void isExternalLink() {
		Assertion.assertTrue(isCategoryResult(EXTERNALLINKINDEX), "External link index not found");
		PageObjectLogging.log("isExternalLink", "External link index found", true);
	}

	public void isRedirectPage() {
		Assertion.assertTrue(isCategoryResult(REDIRECTPAGEINDEX), "Redirect page index not found");
		PageObjectLogging.log("isRedirectPage", "Redirect page index found", true);
	}

	public void verifyNewPageIsTop() {
		viewLinkResults();
		Assertion.assertNumber(0, pageCategoryIndex[NEWPAGEINDEX], "Checking New Page is on the top of the results.");
	}

	public void verifyMatchingPageIsTop() {
		viewLinkResults();
		Assertion.assertNumber(0, pageCategoryIndex[MATCHINGPAGEINDEX], "Checking Matching Page is on the top of the results.");
	}

	public void verifyExternalLinkIsTop() {
		viewLinkResults();
		Assertion.assertNumber(0, pageCategoryIndex[EXTERNALLINKINDEX], "Checking External Link is on the top of the results.");
	}

	public void verifyRedirectPageIsTop() {
		viewLinkResults();
		Assertion.assertNumber(0, pageCategoryIndex[REDIRECTPAGEINDEX], "Checking Redirect Page is on the top of the results.");
	}

	public VisualEditorPageObject clickLinkResult() {
		waitForElementNotVisibleByElement(inputPending);
		waitForElementByElement(selectedResult);
		WebElement matchingResult = desktopContext.findElement(matchingResultBy);
		waitForElementByElement(matchingResult);
		waitForElementClickableByElement(matchingResult);
		matchingResult.click();
		waitForElementByElement(hyperLinkIFrame);
		driver.switchTo().frame(hyperLinkIFrame);
		waitForElementClickableByElement(previousButton);
		previousButton.click();
		driver.switchTo().defaultContent();
		return new VisualEditorPageObject(driver);
	}
}