package com.imooc.page.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.imooc.page.Constant;
import com.imooc.page.model.Pager;
import com.imooc.page.model.Student;
import com.imooc.page.service.HibernateStudentServiceImpl;
import com.imooc.page.service.StudentService;


public class HibernateDataServlet extends HttpServlet {
	
	private static final long serialVersionUID = -4873699362194465829L;
	private StudentService studentService = new HibernateStudentServiceImpl();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//����request��Ĳ���
		String stuName = request.getParameter("stuName");
		
		int gender = Constant.DEFAULT_GENDER;
		String genderStr = request.getParameter("gender");
		if(genderStr!=null&&!"".equals(genderStr.trim())){
			gender = Integer.parseInt(genderStr);
		}
		String pageNumStr = request.getParameter("pageNum");
		int pageNum = Constant.DEFAULT_PAGE_NUM;
		
		if(pageNumStr!=null&&!"".equals(pageNumStr.trim())){
			pageNum = Integer.parseInt(pageNumStr);
		}
		
		int pageSize = Constant.DEFAULT_PAGE_SIZE;
		String pageSizeStr = request.getParameter("pageSize");
		if(pageSizeStr!=null&&!"".equals(pageSizeStr.trim())){
			pageSize = Integer.parseInt(pageSizeStr);
		}
		//��װ��ѯ����
		Student searchModel = new Student();
		searchModel.setStuName(stuName);
		searchModel.setGender(gender);
		//����service��ȡ��ѯ���
		Pager<Student> result = studentService.findStudent(searchModel, pageNum, pageSize);
		//��ʹ�û���
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		//���ó�ʱʱ��Ϊ0
		response.addDateHeader("Expires", 0);
		//���ñ����ʽΪutf-8
		response.setContentType("text/html;charset=utf-8");
		//��ȡ��ѯ���ݵ�json��ʽ
		String responseStr = JSON.toJSONString(result);
		
		Writer writer = response.getWriter();
		writer.write(responseStr);
		writer.flush();
	}

}
