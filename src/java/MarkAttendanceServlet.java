import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MarkAttendanceServlet")
public class MarkAttendanceServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        String studentId = request.getParameter("student_id");
        String otpInput = request.getParameter("otp");

        if (studentId == null || otpInput == null || studentId.isEmpty() || otpInput.isEmpty()) {
            response.getWriter().println("Invalid Input: Student ID and OTP are required!");
            return;
        }

        HttpSession session = request.getSession();
        String sessionOtp = (String) session.getAttribute("otp");

        if (sessionOtp == null || !otpInput.equals(sessionOtp)) {
            response.getWriter().println("Invalid OTP!");
            return;
        }

        try {
            // Validate Student ID
            if (!studentId.matches("\\d+")) {
                response.getWriter().println("Invalid Student ID format!");
                return;
            }

            // Load JDBC Driver
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root")) {

                // Check if student exists
                String checkQuery = "SELECT students_id FROM students WHERE students_id = ?";
                try (PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
                    checkPst.setInt(1, Integer.parseInt(studentId));
                    ResultSet rs = checkPst.executeQuery();
                    if (!rs.next()) {
                        response.getWriter().println("Error: Student ID not found in database!");
                        return;
                    }
                }

                // Store attendance
                String insertQuery = "INSERT INTO student_attendance (student_id, date, method) VALUES (?, CURDATE(), 'OTP')";
                try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
                    pst.setInt(1, Integer.parseInt(studentId));
                    pst.executeUpdate();
                    response.getWriter().println("Attendance Marked Successfully via OTP");
                    session.removeAttribute("otp"); // Clear OTP after successful attendance marking
                }

            }
        } catch (Exception e) {
            response.getWriter().println("Database Error: " + e.getMessage());
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
        return "Marks student attendance via OTP verification.";
    }
}
