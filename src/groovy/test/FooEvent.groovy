package test

import test.system.events.SystemEvent

class FooEvent extends SystemEvent {

    FooEvent(Object source) {
        super(source)
    }
}
