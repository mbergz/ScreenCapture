package Eventhandlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventHandler {
    private List<Object> handlers = new ArrayList<>();
    private static EventHandler instance;

    private EventHandler() {}

    public static synchronized EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    public void addHandler(Object instance) {
        handlers.add(instance);
    }

    public boolean removeHandler(Object instance) {
        return handlers.remove(instance);
    }

    public void dispatchEvent(Event eventType, Object payload) {
        // kolla vilka handlers som har metod med annotation + eventtype
        // spara en map på handlers som har viss typ av metod med eventtype för performance
        handlers.forEach(handler -> {
            Method[] methods = handler.getClass().getMethods();
            List<Method> methodsWithAnnotationMatchingEvent = Arrays.stream(methods)
                    .filter(method -> {
                        SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                        return annotation!= null && annotation.event().equals(eventType);
                    }).collect(Collectors.toList());
            methodsWithAnnotationMatchingEvent.forEach(method -> {
                try {
                    method.invoke(handler, payload);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
