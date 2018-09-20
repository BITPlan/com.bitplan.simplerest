/**
 * Copyright (c) 2016-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplerest
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.rest.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
   
/**
 * see http://blog.bdoughan.com/2012/03/moxy-as-your-jax-rs-json-provider.html
 * @author wf
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JsonProvider implements
    MessageBodyReader<Object>, MessageBodyWriter<Object>{
   
    private static final String CHARSET = "charset";
 
    @Context
    protected Providers providers;
   
    public static List<Class<?>> registeredTypes=new ArrayList<Class<?>>();
    /**
     * register a type
     * @param clazz
     */
    public static void registerType(Class<?> clazz) {
      registeredTypes.add(clazz);
    }
    
    /**
     * check whether the genericType is readable
     */
    public boolean isReadable(Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType) {
        boolean readable=registeredTypes.contains(getDomainClass(genericType));
        return readable;
    }
   
    /**
     * read
     */
    public Object readFrom(Class<Object> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
            try {
                Class<?> domainClass = getDomainClass(genericType);
                Unmarshaller u = getJAXBContext(domainClass, mediaType).createUnmarshaller();
                u.setProperty("eclipselink.media-type", MediaType.APPLICATION_JSON);
                u.setProperty("eclipselink.json.include-root", false);
 
                StreamSource jsonSource;
                Map<String, String> mediaTypeParameters = mediaType.getParameters();
                if(mediaTypeParameters.containsKey(CHARSET)) {
                    String charSet = mediaTypeParameters.get(CHARSET);
                    Reader entityReader = new InputStreamReader(entityStream, charSet);
                    jsonSource = new StreamSource(entityReader);
                } else {
                    jsonSource = new StreamSource(entityStream);
                }
 
                return u.unmarshal(jsonSource, domainClass).getValue();
            } catch(JAXBException jaxbException) {
                throw new WebApplicationException(jaxbException);
            }
    }
   
    public boolean isWriteable(Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType) {
        return isReadable(type, genericType, annotations, mediaType);
    }
   
    /**
     * write
     */
    public void writeTo(Object object, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream) throws IOException,
        WebApplicationException {
        try {
            Class<?> domainClass = getDomainClass(genericType);
            Marshaller m = getJAXBContext(domainClass, mediaType).createMarshaller();
            m.setProperty("eclipselink.media-type", MediaType.APPLICATION_JSON);
            m.setProperty("eclipselink.json.include-root", false);
 
            Map<String, String> mediaTypeParameters = mediaType.getParameters();
            if(mediaTypeParameters.containsKey(CHARSET)) {
                String charSet = mediaTypeParameters.get(CHARSET);
                m.setProperty(Marshaller.JAXB_ENCODING, charSet);
            }
 
            m.marshal(object, entityStream);
        } catch(JAXBException jaxbException) {
            throw new WebApplicationException(jaxbException);
        }
    }
   
    public long getSize(Object t, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }
   
    private JAXBContext getJAXBContext(Class<?> type, MediaType mediaType)
        throws JAXBException {
        ContextResolver<JAXBContext> resolver
            = providers.getContextResolver(JAXBContext.class, mediaType);
        JAXBContext jaxbContext;
        if(null == resolver || null == (jaxbContext = resolver.getContext(type))) {
            return JAXBContext.newInstance(type);
        } else {
            return jaxbContext;
        }
    }
   
    private Class<?> getDomainClass(Type genericType) {
        if(genericType instanceof Class) {
            return (Class<?>) genericType;
        } else if(genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            return null;
        }
    }
   
}
