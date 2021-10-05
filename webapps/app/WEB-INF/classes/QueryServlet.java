import com.google.gson.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/query")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class QueryServlet extends HttpServlet {
    // The doGet() runs once per HTTP GET request to this servlet.
    private Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bank?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        "root", "mysql123");   // For MySQL
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement()
        ) {
            // Step 3: Execute a SQL SELECT query
            ResultSet rset = stmt.executeQuery("select * from books where author = "
                    + "'" + request.getParameter("author") + "'"   // Single-quote SQL string
                    + " and qty > 0 order by price desc");  // Send the query to the server
            // Step 4: Process the query result set
            int count = 0;
            while(rset.next()) {

                String author = this.gson.toJson(rset.getString("author"));
                String title = this.gson.toJson(rset.getString("title"));
                String price = this.gson.toJson(Float.toString(rset.getFloat("price")));
                out.print("{");
                out.print("\""+"author"+  "\"" + ":"+ author +  ","+ '\n' );
                out.print("\""+"title"+"\"" + ":" + title +  ","+ '\n');
                out.print("\""+"price"+"\"" + ":"+ price + '\n');
                out.print("}");

                count++;
            }


        } catch(Exception ex) {
           out.println(ex.getMessage());
           ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
        out.close();
    }
}