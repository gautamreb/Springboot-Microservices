package reb.eurekaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.AmazonInfo.MetaDataKey;
import com.netflix.discovery.EurekaClient;

import io.micrometer.core.instrument.MeterRegistry;
import reb.eurekaserver.configuration.AppContextEventListener;
import reb.eurekaserver.configuration.EurekaConfigController;
import reb.eurekaserver.utility.PropertiesReader;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppContextEventListener.class);
	private String instanceId="localdev";
	
	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private EurekaClientConfigBean eurekaClientConfigBean;
	
	public static void main(String[] args) throws Exception {
		try{
			SpringApplication.run(EurekaServerApplication.class, args);
		}
		finally {
			PropertiesReader.logFromProperties();
		}
	}

	@Bean
	@Autowired
	@Profile({"staging-aws","prod-aws"})
	public EurekaServerConfigBean eurekaServerConfigBean() {
		/*
		 *  commenting out the EIP components in the application.yml because this is flaky sometimes the EIP does not get deassociated!!!
		 *  instead we use the bean to set the EIP binder retry and interval to be very long so the log does not
		 *  get splashed with errors every 5 mins.
		 */

		LOGGER.debug("### AWS code activated, eurekaServerConfigBean #########");
		EurekaServerConfigBean eurekaServerConfigBean = new EurekaServerConfigBean();
		int MINUTES = 60 * 1000;
		int HOURS = 60 * MINUTES;
		eurekaServerConfigBean.setEIPBindRebindRetries(0);
		eurekaServerConfigBean.setEIPBindingRetryIntervalMs(24*HOURS);
		eurekaServerConfigBean.setEIPBindingRetryIntervalMsWhenUnbound(24*HOURS);

		EurekaConfigController.logEurekaConfig(eurekaClient,eurekaClientConfigBean);

		return eurekaServerConfigBean;      
	}

	@Bean
	@Autowired
	@Profile({"staging-aws","prod-aws"})
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
		LOGGER.debug("### AWS code activated, eurekaInstanceConfigBean !!!!!!!!!!! #########");
		EurekaInstanceConfigBean eurekaInstanceConfigBean = new EurekaInstanceConfigBean(inetUtils);
		AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
		eurekaInstanceConfigBean.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
		eurekaInstanceConfigBean.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicIpv4));
		eurekaInstanceConfigBean.setDataCenterInfo(info);

		instanceId = info.get(MetaDataKey.instanceId);

		return eurekaInstanceConfigBean;      
	}
		
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Value("${spring.application.name}")
	private String applicationName;
    
	 @Bean
	    @DependsOn({"eurekaInstanceConfigBean"})
		public MeterRegistryCustomizer<MeterRegistry> configureMeterRegistry() {
			MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer=registry -> registry.config().commonTags("service",applicationName,"instanceId",instanceId);				
			return meterRegistryCustomizer;
		}
    
}