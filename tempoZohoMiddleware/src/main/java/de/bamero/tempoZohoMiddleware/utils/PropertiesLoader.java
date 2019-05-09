package de.bamero.tempoZohoMiddleware.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
	
	
	public Properties load(String fileName) {
		
		logger.debug("PropertiesLoader.load() now working");
		
		FileInputStream in;
		Properties prop = new Properties();
		String fullFileName = "src/main/resources/"+fileName;
		try {
			in = new FileInputStream(new File(fullFileName));
			prop.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("config.properties cannot be found");
		} catch (IOException e) {
			logger.error("IO exception occured: " + e.getMessage());
		}
		
		return prop;	
	}
	
	public void saveZohoBooks(String filename, String access_token, String current_time) {
		
		Properties prop = load(filename);
		
		String fullFileName = "src/main/resources/"+filename;
		try {
			FileOutputStream out = new FileOutputStream(new File(fullFileName));
			String access_token_toWrite = access_token.substring(1, access_token.length()-1);
			prop.setProperty("access_token", access_token_toWrite);
			prop.setProperty("access_token_gather_date", current_time);
			prop.store(out, null);
			out.close();	
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
