import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addresult")
public class addresult extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        try {
            String id = request.getParameter("student_id");
            String name = request.getParameter("student_name");
            String subject = request.getParameter("subject");
            String marks = request.getParameter("marks");
            String grade = request.getParameter("grade");

            // Load MySQL JDBC Driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");

            // Insert query
            String sql = "INSERT INTO results (student_id, student_name, subject, marks, grade) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, subject);
            pst.setString(4, marks);
            pst.setString(5, grade);

            // Execute update
            int rowsAffected = pst.executeUpdate();

            // Close resources
            pst.close();
            con.close();

            // If data was inserted successfully, redirect back to the form
            if (rowsAffected > 0) {
                response.sendRedirect("add_result.html"); // Redirects user back to form page
            } else {
                response.getWriter().println("<h3>Failed to add result. Please try again.</h3>");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Database driver not found!</h3>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Error connecting to database: " + e.getMessage() + "</h3>");
        }
    }



    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
