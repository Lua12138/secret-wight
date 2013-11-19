package c.e.a.dao.imp;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class GenericDaoImp<T, I extends Serializable> {
    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public GenericDaoImp() {//Class<T> entityClass) {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//		this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public T insert(T o) {
        return o;
    }

    public void insert(T[] os) {
    }

    public T get(I id) {
        return null;
    }

    public T[] findAll() {
        return null;
    }

    public T[] findBySql(String sql, Object... os) {
        return null;
    }

    public void remove(I id) {
    }

    public void remove(T o) {
    }

    public void removeBySql(String sql, Object... os) {
    }
}