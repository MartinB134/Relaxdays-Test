package mbPackage
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException


class MbKeywords {
	/**
	 * Create a dynamic testobject with runtime xpath
	 * @param Xpath as String
	 */
	@Keyword
	def returnCustomTestobjectViaXpath(String xpath) {
		TestObject testObj = new TestObject(xpath)
		testObj.addProperty("xpath", ConditionType.EQUALS, xpath)
		KeywordUtil.logInfo("TestObject successfully created with xpath: '$xpath'.")
		return testObj
	}

	/**
	 * Convert a Price to a number for calculation
	 * 1. String split the price at the space, take first element as it is the number
     * 2. replace the comma to finally convert it to BigDecimal for calculations
     * 3. Rounding and cutting of decimal places will be handled by BigDecimal
	 * @param String with format e.g. '12,34 €'
	 */
	@Keyword
	def priceToNumber(String price) {
		try {
			// remove " €" and replace comma for dots
			def newPrice = (price.asType(String).split(" ")[0]).replace(",", ".")
			newPrice = newPrice.asType(BigDecimal)
			return newPrice
		}
		catch(Exception e) {
			KeywordUtil.markError("Could not convert Price to correct format.")
		}
	}
	
	/**
	 * Convert a Price to a number for calculation
	 * @param to Katalon test object
	 */
	@Keyword
	def priceNumberToString(BigDecimal price) {
		try {
			// remove " €" and replace comma for dots
			String newPriceString = (price.asType(BigDecimal)).asType(String)
			String newPrice = newPriceString.replace(".", ",") + " €"
			return newPrice
		}
		catch(Exception e) {
			KeywordUtil.markError("Could not convert Price to correct format.")
		}
	}
}