package test.system.events

import org.springframework.context.ApplicationListener

public interface SystemEventListener extends ApplicationListener<SystemEvent> {

    public Map<String, String> getMapping()
}