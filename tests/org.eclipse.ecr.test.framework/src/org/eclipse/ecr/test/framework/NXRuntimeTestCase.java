/* 
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Nuxeo - initial API and implementation
 * $Id$
 */

package org.eclipse.ecr.test.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.MockObjectTestCase;
import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.osgi.BundleFile;
import org.nuxeo.osgi.BundleImpl;
import org.nuxeo.osgi.DirectoryBundleFile;
import org.nuxeo.osgi.JarBundleFile;
import org.nuxeo.osgi.OSGiAdapter;
import org.nuxeo.osgi.application.StandaloneBundleLoader;
import org.eclipse.ecr.runtime.AbstractRuntimeService;
import org.eclipse.ecr.runtime.RuntimeService;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.runtime.api.ServiceManager;
import org.eclipse.ecr.runtime.model.RuntimeContext;
import org.eclipse.ecr.runtime.osgi.OSGiRuntimeContext;
import org.eclipse.ecr.test.framework.runner.RuntimeHarness;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;

/**
 * Abstract base class for test cases that require a test runtime service.
 * <p>
 * The runtime service itself is conveniently available as the
 * <code>runtime</code> instance variable in derived classes.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
// Make sure this class is kept in sync with with RuntimeHarness
public class NXRuntimeTestCase extends MockObjectTestCase implements
        RuntimeHarness {

    static {
        // jul to jcl redirection may pose problems (infinite loops) in some
        // environment
        // where slf4j to jul, and jcl over slf4j is deployed
        System.setProperty(AbstractRuntimeService.REDIRECT_JUL, "false");
    }

    private static final Log log = LogFactory.getLog(NXRuntimeTestCase.class);

    protected RuntimeService runtime;

    protected URL[] urls; // classpath urls, used for bundles lookup

    protected File workingDir;

    private static int counter = 0;

    protected StandaloneBundleLoader bundleLoader;

    private Set<URI> readUris;

    protected Map<String, BundleFile> bundles;

    protected boolean restart = false;

    protected OSGiAdapter osgi;

    protected Bundle runtimeBundle;

    protected final List<WorkingDirectoryConfigurator> wdConfigs = new ArrayList<WorkingDirectoryConfigurator>();

    public NXRuntimeTestCase() {
    }

    public NXRuntimeTestCase(String name) {
        super(name);
    }

    @Override
    public void addWorkingDirectoryConfigurator(
            WorkingDirectoryConfigurator config) {
        wdConfigs.add(config);
    }

    @Override
    public File getWorkingDir() {
        return workingDir;
    }

    /**
     * Restarts the runtime and preserve homes directory.
     */
    protected void restart() throws Exception {
        restart = true;
        try {
            tearDown();
            setUp();
        } finally {
            restart = false;
        }
    }

    @Override
    public void start() throws Exception {
        setUp();
    }

    @Override
    public void setUp() throws Exception {
        System.setProperty("org.nuxeo.runtime.testing", "true");
        super.setUp();
        wipeRuntime();
        initUrls();
        if (urls == null) {
            initTestRuntime();
        } else {
            initOsgiRuntime();
        }
    }

    /**
     * Fire the event {@code FrameworkEvent.STARTED}.
     */
    @Override
    public void fireFrameworkStarted() throws Exception {
        osgi.fireFrameworkEvent(new FrameworkEvent(FrameworkEvent.STARTED,
                runtimeBundle, null));
    }

    @Override
    public void tearDown() throws Exception {
        wipeRuntime();
        if (workingDir != null) {
            if (!restart) {
                FileUtils.deleteTree(workingDir);
                workingDir = null;
            }
        }
        readUris = null;
        bundles = null;
        ServiceManager.getInstance().reset();
        super.tearDown();
    }

    @Override
    public void stop() throws Exception {
        tearDown();
    }

    @Override
    public boolean isStarted() {
        return runtime != null;
    }

    private static synchronized String generateId() {
        long stamp = System.currentTimeMillis();
        counter++;
        return Long.toHexString(stamp) + '-'
                + System.identityHashCode(System.class) + '.' + counter;
    }

    protected void initOsgiRuntime() throws Exception {
        try {
            if (!restart) {
                Environment.setDefault(null);
                workingDir = File.createTempFile("NXOSGITestFramework",
                        generateId());
                workingDir.delete();
            }
        } catch (IOException e) {
            log.error("Could not init working directory", e);
            throw e;
        }
        osgi = new OSGiAdapter(workingDir);
        bundleLoader = new StandaloneBundleLoader(osgi,
                NXRuntimeTestCase.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(
                bundleLoader.getSharedClassLoader().getLoader());

        for (WorkingDirectoryConfigurator cfg : wdConfigs) {
            cfg.configure(this, workingDir);
        }

        bundleLoader.setScanForNestedJARs(false); // for now
        bundleLoader.setExtractNestedJARs(false);

        BundleFile bundleFile = lookupBundle("org.eclipse.ecr.runtime");
        runtimeBundle = new RootRuntimeBundle(osgi, bundleFile,
                bundleLoader.getClass().getClassLoader(), true);
        runtimeBundle.start();

        runtime = Framework.getRuntime();
        assertNotNull(runtime);

        // avoid Streaming and Remoting services: useless and can't work
        deployContrib(bundleFile, "OSGI-INF/DeploymentService.xml");
        deployContrib(bundleFile, "OSGI-INF/LoginComponent.xml");
        deployContrib(bundleFile, "OSGI-INF/ServiceManagement.xml");
        deployContrib(bundleFile, "OSGI-INF/EventService.xml");
        deployContrib(bundleFile, "OSGI-INF/ResourceService.xml");
        deployContrib(bundleFile, "OSGI-INF/DefaultJBossBindings.xml");
        deployContrib(bundleFile, "OSGI-INF/ContributionPersistence.xml");
    }

    protected void initTestRuntime() throws Exception {
        runtime = new TestRuntime();
        Framework.initialize(runtime);
        deployContrib("org.nuxeo.runtime.test", "EventService.xml");
        deployContrib("org.nuxeo.runtime.test", "DeploymentService.xml");
    }

    protected void initUrls() throws Exception {
        ClassLoader classLoader = NXRuntimeTestCase.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            urls = ((URLClassLoader) classLoader).getURLs();
        } else if (classLoader.getClass().getName().equals(
                "org.apache.tools.ant.AntClassLoader")) {
            Method method = classLoader.getClass().getMethod("getClasspath");
            String cp = (String) method.invoke(classLoader);
            String[] paths = cp.split(File.pathSeparator);
            urls = new URL[paths.length];
            for (int i = 0; i < paths.length; i++) {
                urls[i] = new URL("file:" + paths[i]);
            }
        } else {
            log.warn("Unknow classloader type: "
                    + classLoader.getClass().getName()
                    + "\nWon't be able to load OSGI bundles");
            return;
        }
        // special case for maven surefire with useManifestOnlyJar
        if (urls.length == 1) {
            try {
                URI uri = urls[0].toURI();
                if (uri.getScheme().equals("file")
                        && uri.getPath().contains("surefirebooter")) {
                    JarFile jar = new JarFile(new File(uri));
                    try {
                        String cp = jar.getManifest().getMainAttributes().getValue(
                                Attributes.Name.CLASS_PATH);
                        if (cp != null) {
                            String[] cpe = cp.split(" ");
                            URL[] newUrls = new URL[cpe.length];
                            for (int i = 0; i < cpe.length; i++) {
                                // Don't need to add 'file:' with maven surefire
                                // >= 2.4.2
                                String newUrl = cpe[i].startsWith("file:") ? cpe[i]
                                        : "file:" + cpe[i];
                                newUrls[i] = new URL(newUrl);
                            }
                            urls = newUrls;
                        }
                    } finally {
                        jar.close();
                    }
                }
            } catch (Exception e) {
                // skip
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("URLs on the classpath: ");
        for (URL url : urls) {
            sb.append(url.toString());
            sb.append('\n');
        }
        log.debug(sb.toString());
        readUris = new HashSet<URI>();
        bundles = new HashMap<String, BundleFile>();
    }

    /**
     * Makes sure there is no previous runtime hanging around.
     * <p>
     * This happens for instance if a previous test had errors in its
     * <code>setUp()</code>, because <code>tearDown()</code> has not been
     * called.
     */
    protected void wipeRuntime() throws Exception {
        // Make sure there is no active runtime (this might happen if an
        // exception is raised during a previous setUp -> tearDown is not called
        // afterwards).
        runtime = null;
        if (Framework.getRuntime() != null) {
            Framework.shutdown();
        }
    }

    public static URL getResource(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(
                resource);
    }

    /**
     * @deprecated use <code>deployContrib()</code> instead
     */
    @Override
    @Deprecated
    public void deploy(String contrib) {
        deployContrib(contrib);
    }

    protected void deployContrib(URL url) {
        assertEquals(runtime, Framework.getRuntime());
        log.info("Deploying contribution from " + url.toString());
        try {
            runtime.getContext().deploy(url);
        } catch (Exception e) {
            log.error(e);
            fail("Failed to deploy contrib " + url.toString());
        }
    }

    /**
     * Deploys a contribution file by looking for it in the class loader.
     * <p>
     * The first contribution file found by the class loader will be used. You
     * have no guarantee in case of name collisions.
     *
     * @deprecated use the less ambiguous
     *             {@link #deployContrib(BundleFile,String)}
     * @param contrib the relative path to the contribution file
     */
    @Override
    @Deprecated
    public void deployContrib(String contrib) {
        URL url = getResource(contrib);
        assertNotNull("Test contribution not found: " + contrib, url);
        deployContrib(url);
    }

    protected void deployContrib(BundleFile bundleFile, String contrib) {
        URL url = bundleFile.getEntry(contrib);
        if (url == null) {
            fail(String.format("Could not find entry %s in bundle '%s",
                    contrib, bundleFile.getURL()));
        }
        deployContrib(url);
    }

    /**
     * Deploys a contribution from a given bundle.
     * <p>
     * The path will be relative to the bundle root. Example: <code>
     * deployContrib("org.nuxeo.ecm.core", "OSGI-INF/CoreExtensions.xml")
     * </code>
     * <p>
     * For compatibility reasons the name of the bundle may be a jar name, but
     * this use is discouraged and deprecated.
     *
     * @param bundle the name of the bundle to peek the contrib in
     * @param contrib the path to contrib in the bundle.
     */
    @Override
    public void deployContrib(String bundle, String contrib) throws Exception {
        deployContrib(lookupBundle(bundle), contrib);
    }

    /**
     * Deploy an XML contribution from outside a bundle.
     * <p>
     * This should be used by tests wiling to deploy test contribution as part
     * of a real bundle.
     * <p>
     * The bundle owner is important since the contribution may depend on
     * resources deployed in that bundle.
     * <p>
     * Note that the owner bundle MUST be an already deployed bundle.
     *
     * @param bundle the bundle that becomes the contribution owner
     * @param contrib the contribution to deploy as part of the given bundle
     */
    @Override
    public RuntimeContext deployTestContrib(String bundle, String contrib)
            throws Exception {
        Bundle b = bundleLoader.getOSGi().getRegistry().getBundle(bundle);
        if (b != null) {
            OSGiRuntimeContext ctx = new OSGiRuntimeContext(runtime, b);
            ctx.deploy(contrib);
            return ctx;
        } else {
            throw new IllegalArgumentException("Bundle not deployed " + bundle);
        }
    }

    @Override
    public RuntimeContext deployTestContrib(String bundle, URL contrib)
            throws Exception {
        Bundle b = bundleLoader.getOSGi().getRegistry().getBundle(bundle);
        if (b != null) {
            OSGiRuntimeContext ctx = new OSGiRuntimeContext(runtime, b);
            ctx.deploy(contrib);
            return ctx;
        } else {
            throw new IllegalArgumentException("Bundle not deployed " + bundle);
        }
    }

    /**
     * @deprecated use {@link #undeployContrib(String, String)} instead
     */
    @Override
    @Deprecated
    public void undeploy(String contrib) {
        undeployContrib(contrib);
    }

    /**
     * @deprecated use {@link #undeployContrib(String, String)} instead
     */
    @Override
    @Deprecated
    public void undeployContrib(String contrib) {
        URL url = getResource(contrib);
        assertNotNull("Test contribution not found: " + contrib, url);
        deployContrib(url);
    }

    /**
     * Undeploys a contribution from a given bundle.
     * <p>
     * The path will be relative to the bundle root. Example: <code>
     * undeployContrib("org.nuxeo.ecm.core", "OSGI-INF/CoreExtensions.xml")
     * </code>
     *
     * @param bundle the bundle
     * @param contrib the contribution
     */
    @Override
    public void undeployContrib(String bundle, String contrib) throws Exception {
        BundleFile b = lookupBundle(bundle);
        URL url = b.getEntry(contrib);
        if (url == null) {
            fail(String.format("Could not find entry %s in bundle '%s'",
                    contrib, b.getURL()));
        }
        runtime.getContext().undeploy(url);
    }

    // TODO: Never used. Remove?
    @Deprecated
    protected void undeployContrib(URL url, String contrib) {
        assertEquals(runtime, Framework.getRuntime());
        log.info("Undeploying contribution from " + url.toString());
        try {
            runtime.getContext().undeploy(url);
        } catch (Exception e) {
            log.error(e);
            fail("Failed to undeploy contrib " + url.toString());
        }
    }

    protected static boolean isVersionSuffix(String s) {
        if (s.length() == 0) {
            return true;
        }
        return s.matches("-(\\d+\\.?)+(-SNAPSHOT)?(\\.\\w+)?");
    }

    /**
     * Resolves an URL for bundle deployment code.
     * <p>
     * TODO: Implementation could be finer...
     *
     * @return the resolved url
     */
    protected URL lookupBundleUrl(String bundle) {
        for (URL url : urls) {
            String[] pathElts = url.getPath().split("/");
            for (int i = 0; i < pathElts.length; i++) {
                if (pathElts[i].startsWith(bundle)
                        && isVersionSuffix(pathElts[i].substring(bundle.length()))) {
                    // we want the main version of the bundle
                    boolean isTestVersion = false;
                    for (int j = i + 1; j < pathElts.length; j++) {
                        // ok for Eclipse (/test) and Maven (/test-classes)
                        if (pathElts[j].startsWith("test")) {
                            isTestVersion = true;
                            break;
                        }
                    }
                    if (!isTestVersion) {
                        log.info("Resolved " + bundle + " as " + url.toString());
                        return url;
                    }
                }
            }
        }
        throw new RuntimeException("Could not resolve bundle " + bundle);
    }

    /**
     * Deploys a whole OSGI bundle.
     * <p>
     * The lookup is first done on symbolic name, as set in
     * <code>MANIFEST.MF</code> and then falls back to the bundle url (e.g.,
     * <code>nuxeo-platform-search-api</code>) for backwards compatibility.
     *
     * @param bundle the symbolic name
     */
    @Override
    public void deployBundle(String bundle) throws Exception {
        // install only if not yet installed
        if (bundleLoader.getOSGi().getRegistry().getBundle(bundle) == null) {
            BundleFile bundleFile = lookupBundle(bundle);
            bundleLoader.loadBundle(bundleFile);
            bundleLoader.installBundle(bundleFile);
        }
    }

    protected String readSymbolicName(BundleFile bf) {
        Manifest manifest = bf.getManifest();
        if (manifest == null) {
            return null;
        }
        Attributes attrs = manifest.getMainAttributes();
        String name = attrs.getValue("Bundle-SymbolicName");
        if (name == null) {
            return null;
        }
        String[] sp = name.split(";", 2);
        return sp[0];
    }

    public BundleFile lookupBundle(String bundleName) throws Exception {
        BundleFile bundleFile = bundles.get(bundleName);
        if (bundleFile != null) {
            return bundleFile;
        }
        for (URL url : urls) {
            URI uri = url.toURI();
            if (readUris.contains(uri)) {
                continue;
            }
            File file = new File(uri);
            readUris.add(uri);
            try {
                if (file.isDirectory()) {
                    bundleFile = new DirectoryBundleFile(file);
                } else {
                    bundleFile = new JarBundleFile(file);
                }
            } catch (IOException e) {
                // no manifest => not a bundle
                continue;
            }
            String symbolicName = readSymbolicName(bundleFile);
            if (symbolicName != null) {
                log.info(String.format("Bundle '%s' has URL %s", symbolicName,
                        url));
                bundles.put(symbolicName, bundleFile);
            }
            if (bundleName.equals(symbolicName)) {
                return bundleFile;
            }
        }
        log.warn(String.format(
                "No bundle with symbolic name '%s'; Falling back to deprecated url lookup scheme",
                bundleName));
        return oldLookupBundle(bundleName);
    }

    @Deprecated
    protected BundleFile oldLookupBundle(String bundle) throws Exception {
        URL url = lookupBundleUrl(bundle);
        File file = new File(url.toURI());
        BundleFile bundleFile;
        if (file.isDirectory()) {
            bundleFile = new DirectoryBundleFile(file);
        } else {
            bundleFile = new JarBundleFile(file);
        }
        log.warn(String.format(
                "URL-based bundle lookup is deprecated. Please use the symbolic name from MANIFEST (%s) instead",
                readSymbolicName(bundleFile)));
        return bundleFile;
    }

    @Override
    public void deployFolder(File folder, ClassLoader loader) throws Exception {
        DirectoryBundleFile bf = new DirectoryBundleFile(folder);
        BundleImpl bundle = new BundleImpl(osgi, bf, loader);
        osgi.install(bundle);
    }

    @Override
    public Properties getProperties() {
        return runtime.getProperties();
    }

    @Override
    public RuntimeContext getContext() {
        return runtime.getContext();
    }

    @Override
    public OSGiAdapter getOSGiAdapter() {
        return osgi;
    }

}
