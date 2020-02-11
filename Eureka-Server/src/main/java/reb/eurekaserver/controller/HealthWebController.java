package reb.eurekaserver.controller;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Endpoint(id = "eureka-health-endpoint")
@Component
public class HealthWebController {
	
	/*
	@RequestMapping(value = "/healthcheck", method = RequestMethod.GET)
	public String getHealthcheck() {
		//curl -X GET http://eureka.host/eureka/health
		//	HTTP/1.1 200 OK
		//	HEALTHY
		return "forward:/eureka/health";
	}
	*/	
	
	/*
	 	http://localhost:8761/actuator/jolokia/read/org.springframework.boot:name=Eureka-health-endpoint,type=Endpoint
	 	http://localhost:8761/actuator/jolokia/exec/org.springframework.boot:name=Eureka-health-endpoint,type=Endpoint/health
		http://localhost:8761/actuator/eureka-health-endpoint
	 */
	@ReadOperation(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String health() {
		return "{\"status\":\"I am online\"}";
	}

}