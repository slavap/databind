package org.jboss.errai9.databinding.client;

import java.util.Map;

import org.jboss.errai9.databinding.client.api.Converter;

import com.google.gwt.user.client.ui.Widget;

public interface BindableSupport {

    /**
     * Returns a proxy for the provided model instance, bound to the provided component.
     *
     * @param model - the model to proxy.
     * @return proxy instance
     */
    public <T> T observe(T model);

    /** @return true in case this object is already proxied. */
    public boolean isProxy(Object obj);

    /**
     * Returns the value of the JavaBean property with the given name.
     *
     * @param propertyName
     *          the name of the JavaBean property, must not be null.
     * @return the current value of the corresponding property.
     * @throws NonExistingPropertyException
     *           if the implementing bean does not have a property with the given
     *           name.
     */
    public Object get(Object proxy, String propertyName);

    /**
     * Sets the value of the JavaBean property with the given name.
     *
     * @param propertyName
     *          the name of the JavaBean property, must not be null.
     * @param value
     *          the value to set.
     * @throws NonExistingPropertyException
     *           if the implementing bean does not have a property with the given
     *           name.
     */
    public void set(Object proxy, String propertyName, Object value);
    public void set(Object proxy, String propertyName, Object value, boolean fireUpdateEvent);

    /**
     * Returns a map of JavaBean property names to their {@link PropertyType}.
     *
     * @return an immutable map of property names to their types. Never null.
     */
    public Map<String, PropertyType> getBeanProperties(Object proxy);

    public PropertyType getBeanProperty(Object proxy, String property);

    /**
     * Returns the {@link BindableProxyAgent} of this proxy.
     *
     * @return the proxy's agent, never null.
     */
    public <T> BindableProxyAgent<T> getBindableProxyAgent(Object proxy);

    /**
     * Updates all widgets bound to the model instance associated with this proxy (see
     * {@link BindableProxyAgent#bind(Widget, String, Converter)}). This method is only useful if the model instance has
     * undergone changes that were not caused by calls to methods on this proxy and were therefore not visible to this
     * proxy (e.g direct field access by JPA).
     */
    public void updateWidgets(Object proxy);

    /**
     * Unwraps and returns the actual portable/target instance.
     *
     * @return the portable/target object, never null.
     */
    public <T> T unwrap(T proxy);

    /**
     * Returns a new(cloned) non-proxied instance with state copied recursively from this target.
     *
     * @return A recursively unwrapped (i.e. non-proxied) instance with state copied from the proxy target.
     */
    public <T> T deepUnwrap(T proxy);


    /** Means that this agent has no bindings anymore and some optional cleanup can be done for it. */
    public <T> void agentUnbound(BindableProxyAgent<T> agent);

}
