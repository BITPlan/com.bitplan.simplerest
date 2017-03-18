package com.bitplan.rest.providers;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.stream.StreamSource;

import com.bitplan.rest.users.UserManagerImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import javax.xml.bind.*;
   
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
