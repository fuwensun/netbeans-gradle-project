package org.netbeans.gradle.project.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jtrim.collections.CollectionsEx;
import org.jtrim.concurrent.GenericUpdateTaskExecutor;
import org.jtrim.concurrent.TaskExecutor;
import org.jtrim.concurrent.UpdateTaskExecutor;
import org.jtrim.event.InitLaterListenerRef;
import org.jtrim.event.ListenerRef;
import org.jtrim.event.ListenerRegistries;
import org.jtrim.property.PropertySource;
import org.jtrim.utils.ExceptionHelper;

public final class SwingPropertyChangeForwarder {
    public static final class Builder {
        private final UpdateTaskExecutor eventExecutor;
        private final List<NamedProperty> properties;

        public Builder() {
            this((UpdateTaskExecutor)null);
        }

        public Builder(TaskExecutor eventExecutor) {
            this(new GenericUpdateTaskExecutor(eventExecutor));
        }

        private Builder(UpdateTaskExecutor eventExecutor) {
            this.eventExecutor = eventExecutor;
            this.properties = new LinkedList<>();
        }

        public void addProperty(String name, PropertySource<?> property, Object source) {
            properties.add(new NamedProperty(name, property, source));
        }

        public void addProperty(String name, PropertySource<?> property) {
            addProperty(name, property, property);
        }

        public SwingPropertyChangeForwarder create() {
            return new SwingPropertyChangeForwarder(this);
        }
    }

    private final Lock mainLock;
    private final Map<PropertyChangeListener, RegistrationRef> listeners;
    private final List<NamedProperty> properties;
    private final UpdateTaskExecutor eventExecutor;

    private SwingPropertyChangeForwarder(Builder builder) {
        this.mainLock = new ReentrantLock();
        this.listeners = new HashMap<>();
        this.properties = CollectionsEx.readOnlyCopy(builder.properties);
        this.eventExecutor = builder.eventExecutor;
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        InitLaterListenerRef combinedRef = new InitLaterListenerRef();
        RegisteredListener registeredListener = new RegisteredListener(name, listener, combinedRef);

        mainLock.lock();
        try {
            RegistrationRef currentRef = listeners.get(listener);
            if (currentRef != null) {
                currentRef.incRegCount(registeredListener);
            }
            else {
                listeners.put(listener, new RegistrationRef(registeredListener));
            }
        } finally {
            mainLock.unlock();
        }

        List<ListenerRef> refs = new ArrayList<>(properties.size());
        for (NamedProperty namedProperty: properties) {
            if (name == null || Objects.equals(name, namedProperty.name)) {
                refs.add(namedProperty.property.addChangeListener(namedProperty.forwarderTask(eventExecutor, listener)));
            }
        }

        combinedRef.init(ListenerRegistries.combineListenerRefs(refs));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy listenerProxy = (PropertyChangeListenerProxy)listener;
            addPropertyChangeListener(listenerProxy.getPropertyName(), listenerProxy.getListener());
        }
        else {
            addPropertyChangeListener(null, listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        ListenerRef toUnregister = null;
        mainLock.lock();
        try {
            RegistrationRef regRef = listeners.get(listener);
            if (regRef != null) {
                toUnregister = regRef.decRegCount(listener);
                if (regRef.isCompletelyUnregistered()) {
                    listeners.remove(listener);
                }
            }
        } finally {
            mainLock.unlock();
        }

        if (toUnregister != null) {
            toUnregister.unregister();
        }
    }

    private static final class RegistrationRef {
        private final List<RegisteredListener> listeners;
        private int regCount;

        public RegistrationRef(RegisteredListener listener) {
            this.regCount = 1;
            this.listeners = new LinkedList<>();
            this.listeners.add(listener);
        }

        public void incRegCount(RegisteredListener listener) {
            regCount++;

            listeners.add(listener);
        }

        public ListenerRef decRegCount(PropertyChangeListener listener) {
            regCount--;

            Iterator<RegisteredListener> listenersItr = listeners.iterator();
            while (listenersItr.hasNext()) {
                RegisteredListener currentListener = listenersItr.next();
                if (Objects.equals(currentListener.listener, listener)) {
                    listenersItr.remove();
                    return currentListener.listenerRef;
                }
            }

            return null;
        }

        public boolean isCompletelyUnregistered() {
            return regCount <= 0;
        }
    }

    private static final class RegisteredListener {
        private final String name;
        private final PropertyChangeListener listener;
        private final ListenerRef listenerRef;

        public RegisteredListener(String name, PropertyChangeListener listener, ListenerRef listenerRef) {
            this.name = name;
            this.listener = listener;
            this.listenerRef = listenerRef;
        }
    }

    private static final class NamedProperty {
        public final String name;
        public final PropertySource<?> property;
        public final Object source;

        public NamedProperty(String name, PropertySource<?> property, Object source) {
            ExceptionHelper.checkNotNullArgument(name, "name");
            ExceptionHelper.checkNotNullArgument(property, "property");
            ExceptionHelper.checkNotNullArgument(source, "source");

            this.name = name;
            this.property = property;
            this.source = source;
        }

        private PropertyChangeEvent getChangeEvent() {
            return new PropertyChangeEvent(source, name, null, property.getValue());
        }

        public Runnable forwarderTask(final UpdateTaskExecutor eventExecutor, PropertyChangeListener listener) {
            final Runnable forwardNowTask = directForwarderTask(listener);
            if (eventExecutor == null) {
                return forwardNowTask;
            }

            return new Runnable() {
                @Override
                public void run() {
                    eventExecutor.execute(forwardNowTask);
                }
            };
        }

        public Runnable directForwarderTask(final PropertyChangeListener listener) {
            return new Runnable() {
                @Override
                public void run() {
                    listener.propertyChange(getChangeEvent());
                }
            };
        }
    }
}
