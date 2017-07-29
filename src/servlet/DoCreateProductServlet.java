package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
 
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import dataObj.Product;
import utils.DBUtils;
import utils.MyUtils;
 
@WebServlet(urlPatterns = { "/doCreateProduct" })
public class DoCreateProductServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
 
  public DoCreateProductServlet() {
      super();
  }
 
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
      Connection conn = MyUtils.getStoredConnection(request);
 
      String code = (String) request.getParameter("code");
      String name = (String) request.getParameter("name");
      String priceStr = (String) request.getParameter("price");
      float price = 0;
      try {
          price = Float.parseFloat(priceStr);
      } catch (Exception e) {
      }
      Product product = new Product(code, name, price);
 
      String errorString = null;
 
      // Mã sản phẩm phải là chuỗi chữ [a-zA-Z_0-9]
      // Có ít nhất một ký tự.
      String regex = "\\w+";
 
      if (code == null || !code.matches(regex)) {
          errorString = "Product Code invalid!";
      }
 
      if (errorString == null) {
          try {
              DBUtils.insertProduct(conn, product);
          } catch (SQLException e) {
              e.printStackTrace();
              errorString = e.getMessage();
          }
      }
      
      // Lưu thông tin vào request attribute trước khi forward sang views.
      request.setAttribute("errorString", errorString);
      request.setAttribute("product", product);
 
      // Nếu có lỗi forward sang trang edit
      if (errorString != null) {
          RequestDispatcher dispatcher = request.getServletContext()
                  .getRequestDispatcher("/WEB-INF/views/createProductView.jsp");
          dispatcher.forward(request, response);
      }
      // Nếu mọi thứ tốt đẹp.
      // Redirect sang trang danh sách sản phẩm.
      else {
          response.sendRedirect(request.getContextPath() + "/productList");
      }
 
  }
 
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
      doGet(request, response);
  }
 
}