package reb.eurekaserver.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class);
	private static String buildVersion;
	private static String profileNotFound = "There is no profile, please set your Profile";
	
	public static void logFromProperties() throws Exception {
		/*
		 * Within Docker, the property spring.profiles.activ is not getting replaced in the dockerfile
		 * so we need to get it directly from the --e argument when AWS runs the container.
		 */
		String value = System.getenv("SPRING_ACTIVE_PROFILE");
		LOGGER.info("logFromProperties, System.getenv('SPRING_ACTIVE_PROFILE'): "+value);
		if (value==null) {
			value = System.getProperty("spring.profiles.active");
			LOGGER.info("logFromProperties, System.getProperty('spring.profiles.active'): "+value);
		}	
		
		buildVersion() ;
		if(null != value && value.length()!=0 ){
			String fileName = "/application-"+value+".yml";
			String str="\n##############################################################\n"+
					"Loading properties from the resource file: '"+fileName+"'\n"+
					"##############################################################";
			LOGGER.info(str);
			BufferedReader reader;
			InputStream in = PropertiesReader.class.getResourceAsStream(fileName);
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			StringBuffer ymlProperties = new StringBuffer();
			while (line != null) {
				ymlProperties.append(line);
				ymlProperties.append("\n");
				line = reader.readLine();
			}reader.close();
						
			LOGGER.info(ymlProperties.toString());
		}else{
			throw new Exception(profileNotFound);
		}			
	}
	
	public static String buildVersion() {
		buildVersion="N/A";
		InputStream buildInfoProperties = PropertiesReader.class.getClassLoader().getResourceAsStream("META-INF/build-info.properties");
		Properties prop = new Properties();
		if ( buildInfoProperties != null ) {				 
			try {
				prop.load(buildInfoProperties);
				buildVersion = prop.getProperty("build.version");
			}
			catch ( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
		String str="\n##############################################################\n"+
				"CURRENT FOLDER: '"+(new File(".")).getAbsolutePath()+"'\n"+
				"BUILD VERSION: "+buildVersion+"\n"+
				"##############################################################";
		LOGGER.info(str);
		return buildVersion;
	}

}
