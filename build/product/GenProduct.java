import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;



/**
 * Generate the equinox config.ini file for ECR application.
 * 
 * @author bstefanescu
 *
 */
public class GenProduct {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: GenProduct repositoryRoot");
			System.exit(1);
		}
		
		File root = new File(args[0]);
		File plugins = new File(root, "plugins");
		if (!plugins.isDirectory()) {
			System.out.println("Not a valid repository root. No plugins subdirectory was found. Root: "+root);
			System.exit(2);
		}

		StringBuilder bundles = new StringBuilder();
		String osgiFramework = null;
		File[] files = plugins.listFiles();
		if (files == null || files.length == 0) {
			System.out.println("nothing to do - no plugins found");
			return;
		}
		for (File file : files) {
			String name = file.getName();
			if (name.startsWith("javaws-dev")) {
				// used only for developing cmis bridge
				file.delete();
				continue;
			}
			if (name.startsWith("org.eclipse.osgi_")) {
				osgiFramework = file.getCanonicalPath();
				continue;
			}
			String path = file.getCanonicalPath();
			bundles.append(",reference\\:file\\:").append(path);
			if (name.startsWith("org.eclipse.ecr.application")) {
				bundles.append("@start");
			}
		}
		if (osgiFramework == null) {
			System.out.println("No system bundle found");
			System.exit(2);			
		}
		if (bundles.length() == 0) {
			System.out.println("No bundles found");
			System.exit(2);
		}
		String osgiBundles = bundles.substring(1);

		File cfg = new File(root, "configuration");
		cfg = new File(cfg, "config.ini");
		genConfigurationFile(cfg, osgiFramework, osgiBundles);
	}
	
	private static void genConfigurationFile(File file, String osgiFramework, String osgiBundles) throws IOException {
		file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		try {
			writer.write("osgi.configuration.cascaded=false\r\n");
			writer.write("osgi.framework="+osgiFramework+"\r\n");
			writer.write("osgi.bundles="+osgiBundles+"\r\n");
		} finally {
			writer.close();
		}
	}

}