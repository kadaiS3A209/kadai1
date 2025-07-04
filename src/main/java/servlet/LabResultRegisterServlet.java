package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dao.LabTestOrderDAO;
import dao.PatientDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listener.MasterDataManager;
import model.EmployeeBean;
import model.LabTestBean;
import model.LabTestItemBean;

@WebServlet("/LabResultRegisterServlet")
public class LabResultRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String labTestOrderIdStr = request.getParameter("labTestOrderId");
        int labTestOrderId = Integer.parseInt(labTestOrderIdStr);

        LabTestOrderDAO dao = new LabTestOrderDAO();
        // 指示に含まれる検査項目リストを取得
        List<LabTestItemBean> items = dao.getLabTestItemsByOrderId(labTestOrderId);
        
        // ★各検査項目に、マスタデータから検査名などを補完する
        for(LabTestItemBean item : items) {
            // MasterDataManagerから検査コードに一致するマスタ情報を探す (このヘルパーメソッドはMasterDataManagerに要実装)
            LabTestBean masterData = MasterDataManager.findLabTestByCode(item.getTestCode());
            if (masterData != null) {
                item.setTestName(masterData.getJlacTestName());
                item.setSalesName(masterData.getSalesName());
                item.setUnit(masterData.getUnit());
                item.setReferenceValue(masterData.getReferenceValue());
            }
        }
        
        // 親オーダーの情報も取得して渡す（患者名など）
        // (簡単のため、ここでは省略。必要ならLabTestOrderDAOにgetXxxメソッドを追加)
        request.setAttribute("labTestOrderId", labTestOrderId);
        request.setAttribute("testItems", items);
        request.getRequestDispatcher("/WEB-INF/jsp/lab_result_register_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        int labTestOrderId = Integer.parseInt(request.getParameter("labTestOrderId"));
        Map<String, String[]> parameterMap = request.getParameterMap();
        LabTestOrderDAO dao = new LabTestOrderDAO();

        // フォームから送信された各検査結果をループで処理
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().startsWith("result_")) {
                String labTestItemIdStr = entry.getKey().substring("result_".length());
                int labTestItemId = Integer.parseInt(labTestItemIdStr);
                String resultValue = entry.getValue()[0];
                
                // 入力があった項目のみ更新
                if (resultValue != null && !resultValue.trim().isEmpty()) {
                    dao.updateTestItemResult(labTestItemId, resultValue.trim());
                }
            }
        }

        // 全ての項目が完了したかチェックし、親オーダーのステータスを更新
        dao.checkAndUpdateOrderStatus(labTestOrderId);

        session.setAttribute("listSuccessMessage", "検査指示ID: " + labTestOrderId + " の結果を登録しました。");
        response.sendRedirect("LabOrderListServlet");
    }
}
