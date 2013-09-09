package ast.test

import test.BarEvent
import test.FooEvent
import test.system.events.SystemEventBus

class TestController {

    SystemEventBus systemEventBus

    def index() {
    }

    def dispatchFoo() {
        systemEventBus.publish(new FooEvent(this))
        redirect(action: 'index')
    }

    def dispatchBar() {
        systemEventBus.publish(new BarEvent(this))
        redirect(action: 'index')
    }
}
