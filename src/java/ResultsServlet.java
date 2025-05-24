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

@WebServlet("/ResultsServlet")
public class ResultsServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String subject = request.getParameter("subject");
        String studentId = request.getParameter("student");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");

            // Construct SQL query with filters
            String query = "SELECT id, student_id, student_name, subject, marks, grade FROM results WHERE 1=1";
            
            if (!"All".equals(subject)) {
                query += " AND subject = ?";
            }
            if (!"All".equals(studentId)) {
                query += " AND student_id = ?";
            }

            PreparedStatement st = con.prepareStatement(query);

            int paramIndex = 1;
            if (!"All".equals(subject)) {
                st.setString(paramIndex++, subject);
            }
            if (!"All".equals(studentId)) {
                st.setInt(paramIndex++, Integer.parseInt(studentId));
            }

            ResultSet rs = st.executeQuery();

            // Generate HTML table dynamically
            out.println("<table border='1'><tr><th>ID</th><th>Student ID</th><th>Student Name</th><th>Subject</th><th>Marks</th><th>Grade</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td>"
                        + "<td>" + rs.getInt("student_id") + "</td>"
                        + "<td>" + rs.getString("student_name") + "</td>"
                        + "<td>" + rs.getString("subject") + "</td>"
                        + "<td>" + rs.getInt("marks") + "</td>"
                        + "<td>" + rs.getString("grade") + "</td></tr>");
            }
            out.println("</table>");

            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error retrieving results.</p>");
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
