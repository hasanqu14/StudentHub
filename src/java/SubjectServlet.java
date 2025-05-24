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

@WebServlet("/subjects")
public class SubjectServlet extends HttpServlet {
    
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

        out.println("<html><head><title>Manage Subjects</title></head><body>");
        out.println("<h1>Subject Management System</h1>");
        out.println("<h3>Subject List</h3>");
        out.println("<table border='1'><tr><th>ID</th><th>Subject Name</th><th>Subject Code</th><th>Department</th><th>Actions</th></tr>");

        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM subject_manage";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                out.println("<tr><td>" + id + "</td>"
                        + "<td>" + rs.getString("subject_name") + "</td>"
                        + "<td>" + rs.getString("subject_code") + "</td>"
                        + "<td>" + rs.getString("department") + "</td>"
                        + "<td><a href='subjects?delete=" + id + "' onclick='return confirm(\"Are you sure?\");'>Delete</a></td></tr>");
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
        String subjectName = request.getParameter("subject_name");
        String subjectCode = request.getParameter("subject_code");
        String department = request.getParameter("department");

        try (Connection con = getConnection()) {
            String sql = "INSERT INTO subject_manage (id, subject_name, subject_code, department) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, subjectName);
            pst.setString(3, subjectCode);
            pst.setString(4, department);
            pst.executeUpdate();
        } catch (Exception e) {
            response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        response.sendRedirect("subjects");
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String id = request.getParameter("delete");

        if (id != null) {
            try (Connection con = getConnection()) {
                String sql = "DELETE FROM subject_manage WHERE id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
            } catch (Exception e) {
                response.getWriter().println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            }
        }

        response.sendRedirect("subjects");
    }
}
