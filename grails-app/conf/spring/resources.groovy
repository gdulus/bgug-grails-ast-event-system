import org.springframework.context.event.SimpleApplicationEventMulticaster
import test.system.events.SystemEventBus

import java.util.concurrent.Executors

beans = {
    xmlns context: "http://www.springframework.org/schema/context"

    applicationEventMulticaster(SimpleApplicationEventMulticaster) {
        taskExecutor = Executors.newCachedThreadPool()
    }

    systemEventBus(SystemEventBus)

    context.'component-scan'('base-package': "test") { scan ->
        scan.'include-filter'(type: 'assignable', expression: 'test.system.events.SystemEventListener')
    }
}
