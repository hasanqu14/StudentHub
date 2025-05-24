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

@WebServlet("/viewTeachers") // This will be the URL to access this page
public class GetAllTeachersServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Load JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Establish connection to database
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");

            // Query to get teacher details
            String sql = "SELECT * FROM teachers";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            // Generate HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>View All Teachers</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }");
            out.println(".container { max-width: 800px; margin: 50px auto; padding: 20px; background: #fff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { text-align: center; color: #333; }");
            out.println(".table-container { margin-top: 30px; }");
            out.println("table { width: 100%; border-collapse: collapse; }");
            out.println("table, th, td { border: 1px solid #ddd; }");
            out.println("th, td { padding: 10px; text-align: center; }");
            out.println("th { background-color: #f4f4f4; color: #333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>All Registered Teachers</h1>");
            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<thead><tr><th>Teacher ID</th><th>Teacher Name</th><th>Email</th></tr></thead>");
            out.println("<tbody>");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("</tr>");
            }

            if (!hasData) {
                out.println("<tr><td colspan='3' style='text-align:center;'>No teachers found</td></tr>");
            }

            out.println("</tbody></table></div></div>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<p style='color:red; text-align:center;'>Error: " + e.getMessage() + "</p>");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (pst != null) pst.close(); } catch (Exception ignored) {}
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

    @Override
    public String getServletInfo() {
        return "Servlet that retrieves all teachers from the database";
    }
}
