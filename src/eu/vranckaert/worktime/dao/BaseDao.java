package eu.vranckaert.worktime.dao;

import java.util.List;

public interface BaseDao <T> {
	public List<T> findAll();
	
	public long persist(T instance);
	
	public T update(T instance);
	
	public void remove(T instance);
	
	T findById(Object id);
}
