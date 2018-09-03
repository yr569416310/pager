package com.imooc.page.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.imooc.page.Constant;
import com.imooc.page.HibernateSessionFactory;
import com.imooc.page.model.Pager;
import com.imooc.page.model.Student;

public class HibernateStudentDaoImpl implements StudentDao {

	@SuppressWarnings("unchecked")
	@Override
	public Pager<Student> findStudent(Student searchModel, int pageNum, int pageSize) {
		Pager<Student> result = null;
		//��Ų�ѯ����
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String stuName = searchModel.getStuName();
		int gender = searchModel.getGender();
		
		StringBuilder hql = new StringBuilder("from Student where 1=1");
		StringBuilder countHql = new StringBuilder("select count(id) from Student where 1=1");
		if(stuName !=null&&!stuName.equals("")){
			hql.append(" and stuName like :stuName");
			countHql.append(" and stuName like :stuName");
			paramMap.put("stuName", "%"+stuName+"%");
		}
		if(gender==Constant.GENDER_FEMALE||gender==Constant.GENDER_MALE){
			hql.append(" and gender = :gender");
			countHql.append(" and gender = :gender");
			paramMap.put("gender",gender);
		}
		// ��ʼ����
		int fromIndex = pageSize * (pageNum - 1);
		
		//������в�ѯ����ѧ������
		List<Student> studentList = new ArrayList<Student>();
		
		Session session = null;
		try {
			session = HibernateSessionFactory.getSession();
			//��ȡquery����
			Query hqlQuery = session.createQuery(hql.toString());
			Query countHqlQuery = session.createQuery(countHql.toString());
			
			//���ò�ѯ����
			setQueryParams(hqlQuery,paramMap);
			setQueryParams(countHqlQuery,paramMap);
			//�ӵڼ�����¼��ʼ��ѯ
			hqlQuery.setFirstResult(fromIndex);
			
			//ÿҳ��ʾ��������¼
			hqlQuery.setMaxResults(pageSize);
			//��ȡ��ѯ���
			studentList = hqlQuery.list();
			//��ȡ�ܼ�¼����
			List<?> countResult = countHqlQuery.list();
			int totalRecord = ((Number)countResult.get(0)).intValue();
		
			// ��ȡ��ҳ��
			int totalPage = totalRecord / pageSize;
			if (totalRecord % pageSize != 0) {
				totalPage = totalPage + 1;
			}
			//��װpager����
			result = new Pager<>(pageSize, pageNum, totalRecord, totalPage, studentList);
		} catch (Exception e) {
			throw new RuntimeException("��ѯ���������쳣��",e);
		}finally{
			if(session != null){
				HibernateSessionFactory.closeSession();
			}
		}
		return result;
	}

	/**
	 * ���ò�ѯ�Ĳ���
	 * @param query
	 * @param paramMap
	 * @return
	 */
	private Query setQueryParams(Query query,Map<String,Object> paramMap){
		if(paramMap!=null&&!paramMap.isEmpty()){
			for(Map.Entry<String, Object> param:paramMap.entrySet()){
				query.setParameter(param.getKey(), param.getValue());
			}
		}
		return query;
	}

}
