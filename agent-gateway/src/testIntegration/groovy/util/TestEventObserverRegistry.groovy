package util


import java.util.function.Consumer

class TestEventObserverRegistry {
    final Map<Class, Consumer<Object>> observers = new HashMap<>()

    void register(Class aClass, Consumer<Object> observer) {
        observers.put(aClass, observer)
    }

    void reset() {
        observers.clear()
    }

    void notifyAll(Class aClass, Object message) {
        observers.get(aClass).accept(message)
    }
}
