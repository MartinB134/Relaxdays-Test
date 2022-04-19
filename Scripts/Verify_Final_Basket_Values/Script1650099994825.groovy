import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.util.KeywordUtil as Log
import mbPackage.TestsuiteVariables as Var
import mbPackage.MbKeywords
Functions = new MbKeywords()

BigDecimal finalPrice = 0
BigDecimal productPriceNumber = 0
Integer iteration = Var.counters.get('iterations')

WebUI.navigateToUrl('https://relaxdays.de/checkout/cart/')

// Looping over items in the product list to calculate final Basketprice and to check individual attributes
for (def index : (1..Var.products.size())) {
	Log.logInfo("FINAL Price forstart: $finalPrice")
	HashMap product = Var.products.get(index)
	String productPriceString = product.get("price")
	String productAttribute= product.get("attribute")
	String productHeading = product.get('heading')
	
	productPriceNumber = Functions.priceToNumber(productPriceString) * iteration 
	productPriceString = Functions.priceNumberToString(productPriceNumber)
	// Update the final basket price outside of this for-loop with values of this product type
	finalPrice = finalPrice + productPriceNumber
	Log.logInfo("Final price updated: $finalPrice")
	Log.logInfo("Product price checked for arcticle '$productHeading': ${productPriceString}")
	
	/*
	 * Check for strings of prices and attributes in Basket row identified by Heading of the item
	 * Verify that the objects with selected texts are visible in the corresponding row.
	 */
	TestObject productPriceTextElement = CustomKeywords.'mbPackage.MbKeywords.returnCustomTestobjectViaXpath'("//*[@id='form-validate']//*[contains(text(),'$productHeading')]/ancestor::*[contains(@class, 'divider')]//*[contains(text(), '${productPriceString.split(' ')[0]}')]")
	WebUI.verifyElementPresent(productPriceTextElement, 2, FailureHandling.CONTINUE_ON_FAILURE)
	 
	TestObject attributeTextElement = CustomKeywords.'mbPackage.MbKeywords.returnCustomTestobjectViaXpath'("//*[@id='form-validate']//*[contains(text(),'$productHeading')]/ancestor::*[contains(@class, 'divider')]//*[contains(text(), '$productAttribute')]")
	WebUI.verifyElementPresent(attributeTextElement, 2, FailureHandling.CONTINUE_ON_FAILURE)

}

/*
 * Final Check of the price
 */

Log.logInfo("FINAL BASKET check to match price of: ${Functions.priceNumberToString(finalPrice)}")
WebUI.verifyElementText(findTestObject('Object Repository/Relaxdays/Page_Warenkorb/span_final_price'), Functions.priceNumberToString(finalPrice))
