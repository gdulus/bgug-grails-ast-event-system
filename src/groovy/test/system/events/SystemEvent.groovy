package test.system.events

import org.springframework.context.ApplicationEvent

abstract class SystemEvent extends ApplicationEvent {

    def SystemEvent(Object source) {
        super(source)
    }
}
