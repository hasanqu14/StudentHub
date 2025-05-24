import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TeacherScheduleServlet")
public class TeacherScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");
            stmt = con.createStatement();
            
            // Query to fetch schedule
            String sql = "SELECT * FROM schedule";
            rs = stmt.executeQuery(sql);

            // Check if data exists
            boolean hasData = false;
            StringBuilder tableHtml = new StringBuilder();
            tableHtml.append("<table>");
            tableHtml.append("<thead><tr><th>Subject</th><th>Teacher</th><th>Role</th><th>Time Slot</th></tr></thead>");
            tableHtml.append("<tbody>");

            while (rs.next()) {
                hasData = true;
                tableHtml.append("<tr>");
                tableHtml.append("<td>").append(rs.getString("subject")).append("</td>");
                tableHtml.append("<td>").append(rs.getString("teacher_name")).append("</td>");
                tableHtml.append("<td>").append(rs.getString("role")).append("</td>");
                tableHtml.append("<td>").append(rs.getString("time_slot")).append("</td>");
                tableHtml.append("</tr>");
            }

            tableHtml.append("</tbody></table>");

            if (hasData) {
                out.println(tableHtml.toString());
            }

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error fetching schedule: " + e.getMessage() + "</p>");
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
