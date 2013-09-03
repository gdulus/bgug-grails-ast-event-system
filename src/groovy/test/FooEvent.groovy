package test

import test.system.events.SystemEvent

class FooEvent extends SystemEvent {

    def FooEvent(Object source) {
        super(source)
    }
}
