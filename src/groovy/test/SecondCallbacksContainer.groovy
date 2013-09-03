package test

import groovy.util.logging.Slf4j
import test.system.events.ast.annotation.OnSystemEvent

@Slf4j
class SecondCallbacksContainer {

    @OnSystemEvent
    public void callback(final FooEvent event) {
        log.info('Event received {}', event)
    }

    @OnSystemEvent
    public void callback(final BarEvent event) {
        log.info('Event received {}', event)
    }
}
