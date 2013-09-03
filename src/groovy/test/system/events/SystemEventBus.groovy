package test.system.events

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class SystemEventBus implements ApplicationContextAware {

    ApplicationContext applicationContext

    public void publish(final SystemEvent event) {
        applicationContext.publishEvent(event)
    }
}
