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

@WebServlet("/courses")
public class CourseServlet extends HttpServlet {
    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/smsdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Manage Courses</title></head><body>");
        out.println("<h1>Course Management System</h1>");
        out.println("<h3>Course List</h3>");
        out.println("<table border='1'><tr><th>ID</th><th>Course Name</th><th>Course Code</th><th>Duration</th><th>Department</th><th>Actions</th></tr>");

        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM course_manage";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                out.println("<tr><td>" + id + "</td>"
                        + "<td>" + rs.getString("course_name") + "</td>"
                        + "<td>" + rs.getString("course_code") + "</td>"
                        + "<td>" + rs.getString("duration") + "</td>"
                        + "<td>" + rs.getString("department") + "</td>"
                        + "<td><a href='courses?delete=" + id + "' onclick='return confirm(\"Are you sure?\");'>Delete</a></td></tr>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("</table>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String id = request.getParameter("id");
        String courseName = request.getParameter("course_name");
        String courseCode = request.getParameter("course_code");
        String duration = request.getParameter("duration");
        String department = request.getParameter("department");

        try (Connection con = getConnection()) {
            String sql = "INSERT INTO course_manage (id, course_name, course_code, duration, department) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, courseName);
            pst.setString(3, courseCode);
            pst.setString(4, duration);
            pst.setString(5, department);
            pst.executeUpdate();
        } catch (Exception e) {
            response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        response.sendRedirect("courses");
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String id = request.getParameter("delete");

        if (id != null) {
            try (Connection con = getConnection()) {
                String sql = "DELETE FROM course_manage WHERE id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
            } catch (Exception e) {
                response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            }
        }

        response.sendRedirect("courses");
    }
}
