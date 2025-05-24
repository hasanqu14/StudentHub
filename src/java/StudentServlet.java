import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/smsdb";
    private static final String JDBC_USER = "root"; // Change if required
    private static final String JDBC_PASSWORD = "root"; // Change if required

    // Utility method for DB connection
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Manage Students</title></head><body>");
        out.println("<h1>Student Management System</h1>");
        
        // Add Student Form
        
        
        // Student Table
        out.println("<h3>Student List</h3>");
        out.println("<table border='1'><tr><th>Roll No</th><th>Name</th><th>Email</th><th>Mobile</th><th>Course</th><th>Actions</th></tr>");

        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM student_manage";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String roll = rs.getString("roll_number");
                out.println("<tr><td>" + roll + "</td>"
                        + "<td>" + rs.getString("name") + "</td>"
                        + "<td>" + rs.getString("email") + "</td>"
                        + "<td>" + rs.getString("mobile") + "</td>"
                        + "<td>" + rs.getString("course") + "</td>"
                        + "<td><a href='students?delete=" + roll + "' onclick='return confirm(\"Are you sure?\");'>Delete</a></td></tr>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("</table>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roll = request.getParameter("roll");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String course = request.getParameter("course");

        try (Connection con = getConnection()) {
            String sql = "INSERT INTO student (roll_number, name, email, mobile, course) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, roll);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, mobile);
            pst.setString(5, course);
            pst.executeUpdate();
        } catch (Exception e) {
            response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        response.sendRedirect("students");
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roll = request.getParameter("delete");

        if (roll != null) {
            try (Connection con = getConnection()) {
                String sql = "DELETE FROM students WHERE roll_number=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, roll);
                pst.executeUpdate();
            } catch (Exception e) {
                response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            }
        }

        response.sendRedirect("students");
    }
}
