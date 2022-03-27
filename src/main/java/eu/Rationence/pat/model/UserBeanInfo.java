package eu.Rationence.pat.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class UserBeanInfo extends SimpleBeanInfo {
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor username = new PropertyDescriptor("username", User.class);
            PropertyDescriptor[] descriptors = {username};
            return descriptors;
        } catch (IntrospectionException e) {
            throw new Error(e.toString());
        }
    }
}