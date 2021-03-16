package org.geektimes.ioc;

/**
 * @author zhouzy
 * @since 2021-03-15
 */
public interface Container {


    Object getObject(String name);

    <C> C getComponent(String name);

    Container getParentContainer();

    void setParentContainer(Container container);


}
