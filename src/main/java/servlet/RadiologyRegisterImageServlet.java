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
        List<String> rejectedFileNames = new ArrayList<>(); // ★却下されたファイル名を保持するリスト

        try {
            for (Part filePart : request.getParts()) {
                if ("fileUpload".equals(filePart.getName()) && filePart.getSize() > 0) {
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    
                    // ▼▼▼ ★★★ ここからが追加・修正箇所 ★★★ ▼▼▼
                    
                    // 1. コンテントタイプを取得
                    String contentType = filePart.getContentType();
                    System.out.println("アップロードされたファイル: " + submittedFileName + ", コンテントタイプ: " + contentType); // デバッグ用

                    // 2. コンテントタイプが画像であるかを確認
                    if (contentType != null && contentType.startsWith("image/")) {
                        // 許可された画像ファイルの場合のみ保存処理を行う
                        String extension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
                        String uniqueFileName = UUID.randomUUID().toString() + extension;
                        savedFileNames.add(uniqueFileName);

                        File uploadDir = new File(UPLOAD_DIRECTORY);
                        if (!uploadDir.exists()) uploadDir.mkdirs();
                        File file = new File(UPLOAD_DIRECTORY + File.separator + uniqueFileName);
                        
                        try (InputStream input = filePart.getInputStream()) {
                            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else {
                        // 画像以外のファイルだった場合は、処理をスキップし、却下リストに追加
                        rejectedFileNames.add(submittedFileName);
                    }
                    // ▲▲▲ ここまで追加・修正 ▲▲▲
                }
            }

            // バリデーションとDB登録
            if (savedFileNames.isEmpty()) {
                String errorMessage = "画像ファイルが1つも選択されていません。";
                if (!rejectedFileNames.isEmpty()) {
                    errorMessage += " 却下されたファイル: " + String.join(", ", rejectedFileNames);
                }
                throw new ServletException(errorMessage);
            }
            
            XrayOrderDAO dao = new XrayOrderDAO();
            boolean success = dao.completeXrayOrder(xrayOrderId, technicianId, savedFileNames);

            if (success) {
                String successMessage = "指示ID: " + xrayOrderId + " の写真登録を完了しました。";
                if (!rejectedFileNames.isEmpty()) {
                    successMessage += " (注意: " + String.join(", ", rejectedFileNames) + " は画像ファイルではないため無視されました)";
                }
                session.setAttribute("listSuccessMessage", successMessage);
                response.sendRedirect("RadiologyOrderListServlet");
            } else {
                throw new ServletException("データベースへの登録に失敗しました。");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("formError", "登録処理中にエラーが発生しました: " + e.getMessage());
            XrayOrderDAO dao = new XrayOrderDAO();
            request.setAttribute("orderDetails", dao.getXrayOrderDetailsById(xrayOrderId));
            request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
        }
    }
}
