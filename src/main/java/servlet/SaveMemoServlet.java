package servlet; // パッケージは適宜変更してください

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.MemoDAO;

@WebServlet("/SaveMemoServlet")
public class SaveMemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // リクエストボディからJSONデータを読み取る
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String jsonInput = sb.toString();
        
        JsonObject result = new JsonObject();
        
        try {
            JsonObject saveData = JsonParser.parseString(jsonInput).getAsJsonObject();
            String title = saveData.get("title").getAsString();
            String text = saveData.get("text").getAsString();
            String image = saveData.get("image").getAsString();

            int newId = MemoDAO.saveMemoAs(title, text, image);

            if (newId != -1) {
                result.addProperty("status", "success");
                result.addProperty("newId", newId);
                result.addProperty("newTitle", title);
            } else {
                throw new Exception("DAOでの保存に失敗しました。");
            }
        } catch (Exception e) {
            result.addProperty("status", "error");
            result.addProperty("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(result));
    }
}