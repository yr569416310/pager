package com.imooc.page;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateSessionFactory {

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	private static org.hibernate.SessionFactory sessionFactory;
	
	private static Configuration configuration = new Configuration();
	private static ServiceRegistry serviceRegistry;
	
	static{
		try{
			configuration.configure();
//			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory();
		}catch(Exception e){
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
	private HibernateSessionFactory(){
		
	}
	
	public static Session getSession() throws HibernateException{
		Session session = (Session)threadLocal.get();
		
		if(session==null||!session.isOpen()){
			if(sessionFactory==null){
				rebuildSessionFactory();
			}
			session = (sessionFactory!=null)?sessionFactory.openSession():null;
			threadLocal.set(session);
		}
		return session;
	}
	
	public static void rebuildSessionFactory(){
		try{
			configuration.configure();
//			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory();
		}catch(Exception e){
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
	
	public static void closeSession() throws HibernateException{
		Session session = (Session)threadLocal.get();
		threadLocal.set(null);
		
		if(session!=null){
			session.close();
		}
	}
}
