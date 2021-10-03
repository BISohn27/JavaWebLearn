package controller.action;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ProductDAO;
import dto.ProductVO;

public class IndexAction implements Action{
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String url = "service/Index.jsp";
		ProductDAO productDAO = ProductDAO.getInstance();
		ArrayList<ProductVO> newProductList = null, bestProductList = null;
		try {
			newProductList = productDAO.getListNewProduct();
			bestProductList = productDAO.getListBestProduct();
		}catch(Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("newProductList", newProductList);
		request.setAttribute("bestProductList", bestProductList);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, response);
	}
}
