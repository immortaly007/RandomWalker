package com.basdado.randomwalker.config;


import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.AbsoluteNameLocationStrategy;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ConfigService {

	private final Logger logger = LoggerFactory.getLogger(ConfigService.class);
	
	private OpenStreetMapConfiguration openStreetMapConfig;
	private RandomWalkerConfiguration randomWalkerConfig;
	
	@PostConstruct
	public void init() {
		
		String configDir = System.getProperty("jboss.server.config.dir");
		
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> defaultConfigBuilder =
			    new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
			    .configure(params.xml()
			        .setFileName("DefaultConfiguration.xml")
			        .setLocationStrategy(new ClasspathLocationStrategy())
			        .setValidating(false)); 
		
		final XMLConfiguration defaultConfig;
		try {
			defaultConfig = defaultConfigBuilder.getConfiguration();
		} catch (ConfigurationException e) {
			throw new RuntimeException("Default configuration is not valid or not found. This shouldn't happen", e);
		}
		
		String userConfigFile = configDir + "/applications/randomwalker/Configuration.xml";
		XMLConfiguration userConfig = null;
		
		if (Files.exists(Paths.get(userConfigFile))) {
			FileBasedConfigurationBuilder<XMLConfiguration> userConfigBuilder =
				    new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
				    .configure(params.xml()
				        .setFileName(userConfigFile)
				        .setLocationStrategy(new AbsoluteNameLocationStrategy())
				        .setValidating(false)); 
			
	
			try {
				userConfig = userConfigBuilder.getConfiguration();
			} catch (ConfigurationException e) {
				logger.warn("Could not load user configuration: " + e.getMessage(), e);
				userConfig = null;
			}
		}
		
		NodeCombiner combiner = new MergeCombiner();
		
		CombinedConfiguration config = new CombinedConfiguration(combiner);
		if (userConfig != null) {
			config.addConfiguration(userConfig);
		}
		config.addConfiguration(defaultConfig);
		
		openStreetMapConfig = new OpenStreetMapConfiguration(config);
		randomWalkerConfig = new RandomWalkerConfiguration(config);
		
	}
	
	public OpenStreetMapConfiguration getOpenStreetMapConfiguration() {
		return openStreetMapConfig;
	}
	
	public RandomWalkerConfiguration getRandomWalkerConfig() {
		return randomWalkerConfig;
	}
	
}
