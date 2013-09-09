package test.system.events

import org.springframework.context.ApplicationEvent

abstract class SystemEvent extends ApplicationEvent {

    SystemEvent(Object source) {
        super(source)
    }
}

