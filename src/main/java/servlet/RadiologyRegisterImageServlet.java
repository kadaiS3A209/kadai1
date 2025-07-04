package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // ★追加
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // ★追加

import dao.XrayOrderDAO;
import model.EmployeeBean;

// ★URLパターンを "/Radiology..." とすることで、AuthenticationFilterの保護対象になります
@MultipartConfig
@WebServlet("/RadiologyRegisterImageServlet")
public class RadiologyRegisterImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // ★★★ ファイルを保存するディレクトリのパス (環境に合わせて変更) ★★★
    private static final String UPLOAD_DIRECTORY = "C:/app_data/xray_images";

    /**
     * GETリクエストは、写真登録フォームを表示します。
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xrayOrderIdStr = request.getParameter("xrayOrderId");
        if (xrayOrderIdStr == null) {
            response.sendRedirect("RadiologyOrderListServlet");
            return;
        }

        XrayOrderDAO dao = new XrayOrderDAO();
        int xrayOrderId = Integer.parseInt(xrayOrderIdStr);
        Map<String, Object> orderDetails = dao.getXrayOrderDetailsById(xrayOrderId);

        request.setAttribute("orderDetails", orderDetails);
        request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
    }

    /**
     * POSTリクエストは、入力されたファイル名をデータベースに登録します。
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        EmployeeBean technician = (EmployeeBean) session.getAttribute("loggedInUser");
        int technicianId = Integer.parseInt(technician.getEmpid());
        int xrayOrderId = Integer.parseInt(request.getParameter("xrayOrderId"));
        
        List<String> savedFileNames = new ArrayList<>();

        try {
            // --- 1. アップロードされたファイルを取得 ---
            for (Part filePart : request.getParts()) {
                // "fileUpload" というname属性を持つ、かつファイルが選択されているものだけを処理
                if ("fileUpload".equals(filePart.getName()) && filePart.getSize() > 0) {
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // ファイル名を取得

                    // --- 2. 安全なファイル名を生成 ---
                    // 同じファイル名での上書きを防ぎ、ファイル名を推測されにくくするために、UUIDなどでユニークな名前を生成
                    String extension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + extension;
                    savedFileNames.add(uniqueFileName); // DBに保存するのはこのユニークなファイル名

                    // --- 3. ファイルをサーバーのディスクに保存 ---
                    File uploadDir = new File(UPLOAD_DIRECTORY);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs(); // 保存用ディレクトリがなければ作成
                    }
                    File file = new File(UPLOAD_DIRECTORY + File.separator + uniqueFileName);
                    
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("ファイルが保存されました: " + file.getAbsolutePath());
                    }
                }
            }

            // --- 4. バリデーションとDB登録 ---
            if (savedFileNames.isEmpty()) {
                throw new ServletException("ファイルが1つも選択されていません。");
            }
            
            XrayOrderDAO dao = new XrayOrderDAO();
            boolean success = dao.completeXrayOrder(xrayOrderId, technicianId, savedFileNames);

            if (success) {
                session.setAttribute("listSuccessMessage", "指示ID: " + xrayOrderId + " の写真登録を完了しました。");
                response.sendRedirect("RadiologyOrderListServlet");
            } else {
                throw new ServletException("データベースへの登録に失敗しました。");
            }

        } catch (Exception e) {
            // エラー処理
            e.printStackTrace();
            request.setAttribute("formError", "登録処理中にエラーが発生しました: " + e.getMessage());
            // エラー時もフォームを再表示するために必要な情報を再度セット
            XrayOrderDAO dao = new XrayOrderDAO();
            request.setAttribute("orderDetails", dao.getXrayOrderDetailsById(xrayOrderId));
            request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
        }
    }
}
