package com.imooc.page.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imooc.page.Constant;
import com.imooc.page.model.Pager;
import com.imooc.page.model.Student;
import com.imooc.page.service.JdbcSqlStudentServiceImpl;
import com.imooc.page.service.StudentService;
import com.imooc.page.util.StringUtil;


public class JdbcSqlServlet extends HttpServlet {
	
	private static final long serialVersionUID = -4873699362194465829L;
	private StudentService studentService = new JdbcSqlStudentServiceImpl();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//接收request里的参数
		String stuName = request.getParameter("stuName");
		
		int gender = Constant.DEFAULT_GENDER;
		String genderStr = request.getParameter("gender");
		if(genderStr!=null&&!"".equals(genderStr.trim())){
			gender = Integer.parseInt(genderStr);
		}
		//校验pageNum参数输入的合法性
		String pageNumStr = request.getParameter("pageNum");
		if(pageNumStr!=null&&!StringUtil.isNum(pageNumStr)){
			request.setAttribute("errorMsg", "参数传输错误");
			request.getRequestDispatcher("jdbcSqlStudent.jsp").forward(request, response);
			return;
		}
		int pageNum = Constant.DEFAULT_PAGE_NUM;
		
		if(pageNumStr!=null&&!"".equals(pageNumStr.trim())){
			pageNum = Integer.parseInt(pageNumStr);
		}
		
		int pageSize = Constant.DEFAULT_PAGE_SIZE;
		String pageSizeStr = request.getParameter("pageSize");
		if(pageSizeStr!=null&&!"".equals(pageSizeStr.trim())){
			pageSize = Integer.parseInt(pageSizeStr);
		}
		//组装查询条件
		Student searchModel = new Student();
		searchModel.setStuName(stuName);
		searchModel.setGender(gender);
		//调用service获取查询结果
		Pager<Student> result = studentService.findStudent(searchModel, pageNum, pageSize);
		
		//返回结果到页面
		request.setAttribute("result", result);
		request.setAttribute("stuName", stuName);
		request.setAttribute("gender", gender);
		request.getRequestDispatcher("jdbcSqlStudent.jsp").forward(request, response);
	}

}
