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

// Make sure the testcase have all preconditions set.
// @ToDo Use variables to make this work for other Websites

// 1. Make sure the Browser is opened
try { 
	WebUI.getWindowTitle(FailureHandling.STOP_ON_FAILURE) 
	} 
catch(Exception e) { 
	WebUI.comment 'Browser was not opened'
	if (e.toString().contains('Unable to get the title of the current window')) {
		WebUI.openBrowser('https://relaxdays.de/')
		}
	} 


// 2. Minibasket sidebar is closed
if (WebUI.verifyElementVisible(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'), FailureHandling.OPTIONAL)) {
	WebUI.click(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'), 3)
	WebUI.waitForElementNotVisible(findTestObject('Object Repository/Relaxdays/Page_General/div_cart_sidebar_close'), 3)
	}
	
// 3. Cookie bar is not visible
if (WebUI.verifyElementPresent(findTestObject('Object Repository/Relaxdays/Page_General/button_Geht klar'), 2, FailureHandling.OPTIONAL)) {
	WebUI.click(findTestObject('Object Repository/Relaxdays/Page_General/button_Geht klar'), FailureHandling.OPTIONAL)
	WebUI.verifyElementNotVisible(findTestObject('Object Repository/Relaxdays/Page_General/button_Geht klar'))
	}
// 4. Exit intention offer is not visible
if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Relaxdays/Page_General/modal_Exit_Intent'), 0, FailureHandling.OPTIONAL)!=true){ 
		WebUI.click(findTestObject('Object Repository/Relaxdays/Page_General/button_exitintent_Schließen'))
		WebUI.waitForElementNotVisible(findTestObject('Object Repository/Relaxdays/Page_General/a_Toggle Nav_logo'), 3)
	}
		