package test.system.events

final class SystemEventDispatcher {

    public static void dispatch(final SystemEventListener listener, final SystemEvent event) {
        String key = event.class.name
        String callback = listener.getMapping().get(key)

        if (callback) {
            listener."${callback}"(event)
        }
    }
}

