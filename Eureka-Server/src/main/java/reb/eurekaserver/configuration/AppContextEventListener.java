package reb.eurekaserver.configuration;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppContextEventListener {

    @EventListener
    public void handleContextRefreshed(@SuppressWarnings("unused") ContextRefreshedEvent event) {
    	
    }
    
}