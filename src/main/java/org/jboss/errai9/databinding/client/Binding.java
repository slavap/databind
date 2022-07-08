/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai9.databinding.client;

import java.util.List;
import java.util.Map;

import org.jboss.errai9.databinding.client.api.Converter;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Represents the binding of a bean property to a widget and holds all relevant binding-specific
 * metadata.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public final class Binding {

  private final String property;
  private final Object component;
  private final Converter<?, ?> converter;
  @SuppressWarnings("rawtypes")
  private final Map<Class<? extends GwtEvent>, HandlerRegistration> handlerMap;
  private final boolean needsKeyUpBinding;

  public Binding(String property, Object component, Converter<?, ?> converter,
                  @SuppressWarnings("rawtypes") Map<Class<? extends GwtEvent>, HandlerRegistration> handlerMap) {
    this.property = property;
    this.component = component;
    this.converter = converter;
    this.handlerMap = handlerMap;
    needsKeyUpBinding = (handlerMap != null && handlerMap.containsKey(KeyUpEvent.class));
  }

  public String getProperty() {
    return property;
  }

  public Converter<?, ?> getConverter() {
    return converter;
  }

  public Object getComponent() {
    return component;
  }

  @SuppressWarnings("rawtypes")
  public Map<Class<? extends GwtEvent>, HandlerRegistration> getHandlerMap() {
    return handlerMap;
  }

  public void removeHandlers() {
   if (handlerMap != null) {
     for (@SuppressWarnings("rawtypes") Map.Entry entry : handlerMap.entrySet()) {
       HandlerRegistration hr = (HandlerRegistration) entry.getValue();
       hr.removeHandler();
     }
     handlerMap.clear();
   }
  }

  @Override
  public String toString() {
    return "Binding [property=" + property + ", widget=" + component + "]";
  }

  public boolean needsKeyUpBinding() {
    return needsKeyUpBinding;
  }

  public boolean propertyIsList() {
    return List.class.equals(converter.getModelType());
  }

  @Override
  public int hashCode() { // MUST for preventing duplicates in Multimap, see DataBinder
    final int prime = 31;
    int result = 1;
    result = prime * result + ((component == null) ? 0 : component.hashCode());
    result = prime * result + ((property == null) ? 0 : property.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Binding other = (Binding) obj;
    if (component == null) {
      if (other.component != null) return false;
    } else if (!component.equals(other.component)) return false;

    if (property == null) {
      if (other.property != null) return false;
    } else if (!property.equals(other.property)) return false;

    return true;
  }

}
