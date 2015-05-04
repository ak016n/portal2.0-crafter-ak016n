package com.att.developer.dao;

public interface GenericDAO<T> {
	
	T create(T bean);
	T update(T bean);
	T load(T bean);
	void delete(T bean);
	
	//EntityManager getEntityManager();

}
