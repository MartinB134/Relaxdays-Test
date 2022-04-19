import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.util.KeywordUtil as Log
import mbPackage.TestsuiteVariables as Var
import mbPackage.MbKeywords

Functions = new MbKeywords()

/* The Global Variable will determine the selectors of this test and the number of products added.
 * Using the global Varible from Profiles>default is a bad implementation, but:
 * 1. The datadriven/keyworddriven approach is not available in the free version to use flexible mainainable datasets
 * 2. Writing a Class with optional fields instead needs a nested builder etc. and also bad implementation, which is overkill for a throw away dataset
 * 3. These Hashmaps work for the usecase
 *
 * Example structure of GlobalVariable.product1 in Profiles>default
 * HashMap product1= [
 * 			'heading': 'Pavillion 3x3 2 Seitenteile', 
 *		  	'attribute': 'Transparent | Weiß', 
 *		   	'quantity': '1', 
 * 		   	'price': '49,90 €',
 *			'sku': '1002081', 
 *			'url': 'https://relaxdays.de/pavillon-3x3-m-mit-2-seitenteilen-10020810']
*/
// Using keywords or variables is forbidden in the free version of Katalon. Datadriven Testing is only available in the enterprise version. 
// This is the only implementation that worked: A Hashmap is populated with a list of products defined from the environment profile "default" and added to a Hashmap in Testsuitescope

Var.products.put(1, GlobalVariable.product1)
Var.products.put(2, GlobalVariable.product2)
Var.products.put(3, GlobalVariable.product3)
Var.counters.merge("iterations", 1, { a, b -> a + b}) // This seems to be the best and ugliest way to update an iterator in a Map

// Each Product is tested in this loop
for (def index : (1..Var.products.size())) {
    // Create a new product for this testcase iteration
    // Prepare all neccessary variables
    HashMap product = Var.products.get(index)
    String productPrice = product.get('price')
	int iteration = Var.counters.get("iterations")
	String minibasketPrice = Functions.priceNumberToString( Functions.priceToNumber(productPrice) * iteration )
	Log.logInfo("minibasket price: $minibasketPrice")
    Log.logInfo("Starting Testcase with: '${product.get('heading')}'.")

    /*
	* Find Product via Searchbar and SKU
	* Verify that the product page is finished loading
	*/
    WebUI.waitForElementVisible(findTestObject('Relaxdays/Page_General/input_Suche_q'), 3)

    WebUI.setText(findTestObject('Object Repository/Relaxdays/Page_General/input_Suche_q'), product.get('sku'))

    WebUI.verifyElementText(findTestObject('Object Repository/Relaxdays/Page_General/option_first_result'), product.get(
            'heading'))

    WebUI.click(findTestObject('Object Repository/Relaxdays/Page_General/option_first_result'))

    WebUI.waitForElementVisible(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/span_Price_Final'), 
        5)

    // Handling the two ways to select attributes of a product: 1. Colors 2. Pack-sizes
    def productAttribute = product.get('attribute')
	
	// This needs refactoring to be readable, but it handles all cases of selects
    if (productAttribute != '') {
		// Working with Failure Handling and if/else would have been enough
        try {
            def check_dropdown_amount_absent = WebUI.verifyElementNotPresent(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/div_select_amount'), 2)
			def check_color_select_absent = 
			WebUI.verifyElementNotPresent( findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/div_options_selectable'), 2, FailureHandling.OPTIONAL)
			
			if (check_color_select_absent == true) {
				break
			}
            if (check_dropdown_amount_absent == true) {
                TestObject testObj = Functions.returnCustomTestobjectViaXpath("//*[@id='product-options-wrapper']//*[@data-option-label='$productAttribute']")
                WebUI.click(testObj)

                // To make sure the option is actually selected, try to get the text of the option text to match
                for (def i : (0..8)) {
                    if (WebUI.getText(
						findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/span_product_color_info'), FailureHandling.OPTIONAL) == productAttribute) {
                        break
                    } else {
                        Thread.sleep(300)
                    }
                }
                // Final Check if the correct option is selected
                WebUI.verifyElementText(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/span_product_color_info'), 
                    productAttribute)
            } else {
                Log.logInfo("No Exception was thrown and check for dropdown amount was '$check_dropdown_amount_absent'")
            }
        }
        catch (Exception e) {
            WebUI.verifyElementPresent(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/div_select_amount'), 4)

            WebUI.click(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/div_select_amount'))

            TestObject selectable_amount = Functions.returnCustomTestobjectViaXpath("(//*[@data-attribute-code]/*[.='Anzahl']/..)[1]//option[contains(.,'$productAttribute')]")

            WebUI.verifyElementPresent(selectable_amount, 4)

            WebUI.verifyElementText(selectable_amount, productAttribute)

            WebUI.click(selectable_amount)
        } 
        
        TestObject testObj = Functions.returnCustomTestobjectViaXpath("//*[@id='product-options-wrapper']//*[@data-option-label='$productAttribute']")

        WebUI.verifyElementPresent(testObj, 4)

        WebUI.verifyElementText(testObj, '')

        WebUI.click(testObj)
    } else {
        Log.logInfo('No Product attribute was specified. Product will be put in Basket without any selected additional option.')
    }
    
    WebUI.waitForElementVisible(findTestObject('Object Repository/Relaxdays/Page_Product/Product_AddToCart_Form/button_In den Warenkorb'), 
        2)

    WebUI.click(findTestObject('Object Repository/Relaxdays/Page_Product/Product_AddToCart_Form/button_In den Warenkorb'))

 /*
 * Minibasket will be checked for:
 * 1. price,
 * 2. color,
 * 3. heading,
 * to verify the correct product was added.
 * Then it will be closed.
 */
    WebUI.verifyElementPresent(findTestObject('Object Repository/Relaxdays/Page_General/Minibasket/span_successmessage_item_added'), 
        5)

    WebUI.verifyElementText(findTestObject('Relaxdays/Page_General/Minibasket/span_new_item_price'), minibasketPrice)

    WebUI.verifyElementText(findTestObject('Object Repository/Relaxdays/Page_Product/Page_Single_Product/a_Pavillon 3x3 m mit 2 Seitenteilen'), 
        product.get('heading'))

    if (productAttribute != '') {
        WebUI.verifyElementText(findTestObject('Relaxdays/Page_General/Minibasket/span_new_item_attribute_value'), product.get('attribute'))
    }
    
    WebUI.verifyElementText(findTestObject('Relaxdays/Page_General/Minibasket/span_new_item_qty'), (product.get("quantity").asType(int) * iteration).toString() )

    if (WebUI.verifyElementVisible(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'), FailureHandling.OPTIONAL) == true) {
        WebUI.click(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'))
        WebUI.waitForElementNotVisible(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'), 3)
    }
}

