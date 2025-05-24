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

@WebServlet("/getAllStudents")
public class GetAllStudentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Database connection
            Class.forName("com.mysql.jdbc.Driver"); // Updated JDBC driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");
            Statement stmt = con.createStatement();

            // Fetch student details
            String query = "SELECT * FROM students";
            ResultSet rs = stmt.executeQuery(query);

            // Start HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>View All Students</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; text-align: center; }");
            out.println(".container { max-width: 800px; margin: 50px auto; padding: 20px; background: #fff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }");
            out.println("h1 { color: #333; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 10px; border: 1px solid #ddd; text-align: center; }");
            out.println("th { background-color: #f4f4f4; color: #333; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>All Registered Students</h1>");

            // Create table structure
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr><th>Student ID</th><th>User ID</th><th>Class ID</th><th>Email</th></tr>");
            out.println("</thead>");
            out.println("<tbody>");

            // Insert student records
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("students_id") + "</td>");
                out.println("<td>" + rs.getString("user_id") + "</td>");
                out.println("<td>" + rs.getString("class_id") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("</tr>");
            }

            // If no records found, show message
            if (!hasData) {
                out.println("<tr><td colspan='4' style='text-align:center;'>No students found</td></tr>");
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            // Close connections
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error fetching students: " + e.getMessage() + "</p>");
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
        return "Servlet that retrieves all students from the database and displays them as an HTML page.";
    }
}
