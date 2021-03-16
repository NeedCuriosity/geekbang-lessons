package org.geektimes.projects.user.management;

import javax.management.IntrospectionException;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class ModelMBeanFactory {


    public static <T> RequiredModelMBean createModelMBean(T t, String type) {
        try {
            Class<?> c = t.getClass();
            RequiredModelMBean modelMBean = new RequiredModelMBean();
            modelMBean.setManagedResource(t, type);

            BeanInfo beanInfo = Introspector.getBeanInfo(c, Object.class);

            ModelMBeanInfoSupport modelBeanInfo = new ModelMBeanInfoSupport((c.getName()),
                    c.getName() + " ModelBean",
                    getAttributes(beanInfo),
                    getConstructors(c),
                    getOperations(beanInfo),
                    null);
            modelMBean.setModelMBeanInfo(modelBeanInfo);
            return modelMBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ModelMBeanAttributeInfo[] getAttributes(BeanInfo beanInfo) throws IntrospectionException {
        List<ModelMBeanAttributeInfo> attributeInfos = new LinkedList<>();
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            ModelMBeanAttributeInfo modelMBeanAttributeInfo =
                    new ModelMBeanAttributeInfo(propertyDescriptor.getName()
                            , propertyDescriptor.getShortDescription()
                            , propertyDescriptor.getReadMethod()
                            , propertyDescriptor.getWriteMethod());
            attributeInfos.add(modelMBeanAttributeInfo);
        }
        return attributeInfos.toArray(new ModelMBeanAttributeInfo[attributeInfos.size()]);
    }

    private static ModelMBeanConstructorInfo[] getConstructors(Class c) {
        List<ModelMBeanConstructorInfo> constructorInfos = new LinkedList<>();
        for (Constructor<?> constructor : c.getConstructors()) {
            List<MBeanParameterInfo> infos = Arrays.stream(constructor.getParameterTypes())
                    .map(pam -> new MBeanParameterInfo(pam.getCanonicalName(), pam.getTypeName(), null))
                    .collect(Collectors.toList());

            ModelMBeanConstructorInfo constructorInfo = new ModelMBeanConstructorInfo(constructor.getName(),
                    "", infos.toArray(new MBeanParameterInfo[infos.size()]));
            constructorInfos.add(constructorInfo);
        }
        return constructorInfos.toArray(new ModelMBeanConstructorInfo[constructorInfos.size()]);
    }

    private static ModelMBeanOperationInfo[] getOperations(BeanInfo beanInfo) {
        List<ModelMBeanOperationInfo> operationInfos = new LinkedList<>();
        for (MethodDescriptor methodDescriptor : beanInfo.getMethodDescriptors()) {
            ModelMBeanOperationInfo info = new ModelMBeanOperationInfo(methodDescriptor.getName(), methodDescriptor.getMethod());
            operationInfos.add(info);
        }
        return operationInfos.toArray(new ModelMBeanOperationInfo[operationInfos.size()]);
    }

}
