/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Damien Metzler (Leroy Merlin, http://www.leroymerlin.fr/)
 */
package org.eclipse.ecr.testlib.runner.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.testlib.runner.FeaturesRunner;
import org.eclipse.ecr.testlib.runner.SimpleFeature;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Scopes;

public class WebDriverFeature extends SimpleFeature {

    private static final Log log = LogFactory.getLog(WebDriverFeature.class);

    protected Configuration config;

    protected Class<? extends WebPage> home;

    @Override
    public void initialize(FeaturesRunner runner) throws Exception {
        Class<?> classToTest = runner.getTargetTestClass();
        Browser browser = FeaturesRunner.getScanner().getFirstAnnotation(
                classToTest, Browser.class);
        DriverFactory factory;
        // test here if the driver factory is specified by environment
        String fcName = System.getProperty(DriverFactory.class.getName());
        if (fcName != null) {
            factory = (DriverFactory) Class.forName(fcName).newInstance();
        } else {
            if (browser == null) {
                factory = BrowserFamily.HTML_UNIT.getDriverFactory();
            } else {
                Class<? extends DriverFactory> fc = browser.factory();
                if (fc == DriverFactory.class) {
                    factory = browser.type().getDriverFactory();
                } else {
                    factory = fc.newInstance();
                }
            }
        }
        config = new Configuration(factory);

        // get the home page and the url - first check for an url from the
        // environment
        String url = System.getProperty(HomePage.class.getName() + ".url");
        HomePage home = FeaturesRunner.getScanner().getFirstAnnotation(
                classToTest, HomePage.class);
        if (home != null) {
            config.setHomePageClass(home.type());
            if (url == null) {
                url = home.url();
            }
        }
        config.setHome(url);
        try {
            runner.filter(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    SkipBrowser skip = description.getAnnotation(SkipBrowser.class);
                    if (skip == null) {
                        return true;
                    }
                    for (BrowserFamily family : skip.value()) {
                        if (config.getBrowserFamily().equals(family)) {
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                public String describe() {
                    return "Filtering tests according to current browser settings";
                }
            });
        } catch (ClassCastException e) {
            // OK - just skip
        } catch (NoTestsRemainException e) {
            log.error(e.toString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(final FeaturesRunner runner, Binder binder) {
        binder.bind(Configuration.class).toInstance(config);
        binder.bind(WebDriver.class).toProvider(new Provider<WebDriver>() {
            @Override
            public WebDriver get() {
                return config.getDriver();
            }
        });
        if (config.getHomePageClass() != null) {
            binder.bind(config.getHomePageClass()).toProvider(new Provider() {
                @Override
                public Object get() {
                    Object obj = PageFactory.initElements(config.getDriver(),
                            config.getHomePageClass());
                    runner.getInjector().injectMembers(obj);
                    if (obj instanceof WebPage) {
                        ((WebPage) obj).ensureLoaded();
                    }
                    return obj;
                }
            }).in(Scopes.SINGLETON);
        }
    }

    @Override
    public void stop(FeaturesRunner runner) throws Exception {
        config.resetDriver();
        WebPage.flushPageCache();
    }

}
