package reb.eurekaserver.configuration;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

@Controller
public class EurekaConfigController {

	@Autowired
	private EurekaClient eurekaClient;
	
	@Autowired
	private EurekaClientConfigBean eurekaClientConfigBean;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppContextEventListener.class);
	
	@RequestMapping(value = "/eurekaConfig", method = RequestMethod.GET)
	@ResponseBody
	public String getLogEurekaConfig() {
		logEurekaConfig(eurekaClient,eurekaClientConfigBean);
				
		return "Configuration was logged!";
	}
	
	public static void logEurekaConfig(EurekaClient eurekaClient,EurekaClientConfigBean eurekaClientConfigBean) {
		LOGGER.debug("### Getting request on the home url '/'...");
		EurekaClientConfig eurekaClientConfig=eurekaClient.getEurekaClientConfig();
		LOGGER.debug("### eurekaClientConfig.getRegion: "+ eurekaClientConfig.getRegion());
		LOGGER.debug("### eurekaClientConfig.shouldPreferSameZoneEureka: "+ eurekaClientConfig.shouldPreferSameZoneEureka());
		String region=eurekaClientConfig.getRegion();
		String[] availabilityZonesArray=eurekaClientConfig.getAvailabilityZones(region);
		List<String> availabilityZones = Arrays.asList(availabilityZonesArray);
		LOGGER.debug("### eurekaClientConfig.getAvailabilityZones: "+availabilityZones+" for region: '"+region+"'");
		LOGGER.debug("### eurekaClientConfigBean: "+eurekaClientConfigBean.toString());
	}

}
