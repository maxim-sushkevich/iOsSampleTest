package com.ios.testhelper.demo.kpi;

import com.ios.testhelper.demo.PropertiesManager;
import com.ios.testhelper.demo.TestManager;
import com.ios.testhelper.demo.enums.ConfigurationParametersEnum;
import com.ios.testhelper.demo.enums.ProductTypeEnum;
import com.ios.testhelper.demo.helpers.ITest;
import net.bugs.testhelper.ios.enums.UIAElementType;
import net.bugs.testhelper.ios.item.Element;

import java.util.ArrayList;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

/**
 * Created by avsupport on 10/9/14.
 */
public class TestOpenItemKpi extends KpiTest {

    public TestOpenItemKpi(ITest iosTestHelper, PropertiesManager propertiesManager, TestManager testManager) {
        super(iosTestHelper, propertiesManager, testManager);
    }

    @Override
    public boolean execute(ProductTypeEnum productTypeEnum) {
        switch (productTypeEnum) {
            case BOOK:
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.BOOK.name()),
                        "Back to library",
                        -1,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.BOOK_DOWNLOAD_TIMEOUT.name()));
                break;
            case MAGAZINE:
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.WOODWIN_MAGAZINE.name()),
                        iosTestHelper.isIPad() ? "Library" : "library",
                        0,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.WOODWIN_DOWNLOAD_TIMEOUT.name()));
                break;
            case NEWSPAPER:
                break;
            case PDF:
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.PDF.name()),
                        "Back to Library",
                        -1,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.PDF_DOWNLOAD_TIMEOUT.name()));
                break;
            case COMICS:
                openItemKpi(propertiesManager.getProperty(ConfigurationParametersEnum.DRP_COMICS.name()),
                        iosTestHelper.isIPad() ? "Library" : "library",
                        0,
                        propertiesManager.getPropertyTimeout(ConfigurationParametersEnum.COMICS_DOWNLOAD_TIMEOUT.name()));
                break;
        }
        return true;
    }

    private void openItemKpi(String name, String backButton, int toolbarIndex, int downloadTimeout) {
        iosTestHelper.goToLibrary();
        if(iosTestHelper.isIphone()) {
            Element toolbar = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 10000, 1, null, 2);
            if(toolbar != null) {
                iosTestHelper.saveClickOnElement(iosTestHelper.waitForElementByNameVisible("Search", 1, 0, true, toolbar, 1));
                iosTestHelper.sleep(1000);
            }
        }
        Element element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIASearchBar, 100000, 0, null, 3);
        iosTestHelper.saveClickOnElement(element);
        ArrayList<Element> buttons = iosTestHelper.getElementChildrenByType(element, UIAElementType.UIAButton);
        if(buttons.size() > 0) {
            iosTestHelper.saveClickOnElement(buttons.get(0));
        }
        iosTestHelper.inputText(name + "\n", element);
        iosTestHelper.hideKeyboard();
        iosTestHelper.setStartTime();
        Element collection = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionView, 10000, 0, null, 2);
        if(element == null) {
            iosTestHelper.setEndTime();
            iosTestHelper.failKpi("search " + name);
            return;
        }
        ArrayList<Element> elements = iosTestHelper.getElementChildren(element);
        i("Count collection cell:" + elements.size());

        element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
        iosTestHelper.setEndTime();
        if(element == null) {
            iosTestHelper.failKpi("search " + name);
            return;
        } else {
            iosTestHelper.passKpi("search " + name);
        }

        i("element is visible?: " + element.isVisible());
        iosTestHelper.sleep(3000);
        element = iosTestHelper.waitForElementByClassVisible(UIAElementType.UIACollectionCell, 10000, 0, collection, 1);
//        iosTestHelper.longClickOnElement(element, 0.5, 0.5, 4);
        iosTestHelper.longClickOnElementByXY(element, 0.5, 0.5, 4);

        element = iosTestHelper.waitForElementByNameVisible("Download", 10000, 0, true, null, 3);
        if(element == null) {
            TestManager.setStartTime(0);
            TestManager.setEndTime(0);
            iosTestHelper.failKpi("download " + name);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return;
        }
        iosTestHelper.saveClickOnElement(element);
        iosTestHelper.setStartTime();
        element = iosTestHelper.waitForElementByNameVisible("Read", downloadTimeout, 0, true, null, 3);
        iosTestHelper.setEndTime();
        if(element == null) {
            iosTestHelper.failKpi("download " + name);
            Element closeBtn = iosTestHelper.getElementByName("com.bn.NookApplication.btn bac", 0, true, null, 3);
            iosTestHelper.clickOnElement(closeBtn);
            return;
        }
        iosTestHelper.passKpi("download " + name);
        iosTestHelper.saveClickOnElement(element);
        iosTestHelper.setStartTime();

        if(toolbarIndex > -1) {
            element = iosTestHelper.waitForElementByClassExists(UIAElementType.UIAToolBar, 60000, toolbarIndex, null, 2);

            if (element == null) {
                iosTestHelper.failKpi("open " + name);
                return;
            }
        }

        element = iosTestHelper.waitForElementByNameVisible(backButton, 60000, 0, true, toolbarIndex > -1 ? element : null, toolbarIndex > -1 ? 1 : 3);
        iosTestHelper.setEndTime();
        if(element == null) {
            iosTestHelper.failKpi("open " + name);
            return;
        }

        iosTestHelper.passKpi("open " + name);
        iosTestHelper.saveClickOnElement(element);

        iosTestHelper.sleep(3000);
    }
}