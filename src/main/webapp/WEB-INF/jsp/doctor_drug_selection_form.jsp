<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.DrugOrderItemBean" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>薬剤投与指示</title>
<style>
    body { font-family: sans-serif; font-size: 0.95em; }
    .page-container { width: 90%; margin: 20px auto; }
    .main-layout { display: flex; gap: 20px; }
    .patient-info, .drug-selection-form, .current-cart {
        padding: 15px; border: 1px solid #ccc; border-radius: 5px;
        box-sizing: border-box;
    }
    .patient-info { width: 25%; background-color: #f0f8ff; }
    .drug-selection-form { width: 35%; }
    .current-cart { width: 40%; background-color: #f5f5f5; }
    h1, h2, h3 { margin-top: 0; color: #333; }
    .form-group { margin-bottom: 18px; }
    label { display: block; margin-bottom: 6px; font-weight: bold; }
    select, input[type="number"] { /* 数量入力用 */
        width: 100%; padding: 10px; border: 1px solid #ccc;
        border-radius: 4px; box-sizing: border-box; font-size: 1em;
    }
    .button {
        padding: 10px 18px; color: white; border: none;
        border-radius: 4px; cursor: pointer; margin-right: 8px;
        font-size: 0.95em; text-decoration: none; display: inline-block;
    }
    .button-add { background-color: #007bff; }
    .button-add:hover { background-color: #0056b3; }
    .button-confirm { background-color: #28a745; }
    .button-confirm:hover { background-color: #1e7e34; }
    .button-back { background-color: #6c757d; }
    .button-back:hover { background-color: #545b62;}
    .button-bar { margin-top: 25px; margin-bottom: 15px; }
    .error-message { color: #D8000C; background-color: #FFD2D2; padding: 10px; margin-bottom: 15px; border-radius: 4px; border: 1px solid #D8000C;}
    .cart-table { width: 100%; border-collapse: collapse; margin-top:10px;}
    .cart-table th, .cart-table td { border: 1px solid #bbb; padding: 8px; text-align: left; }
    .cart-table th { background-color: #d8e6ff; }
    .cart-empty { text-align: center; color: #777; padding: 20px; }
</style>
</head>
<body>
    <div class="page-container">
        <h1>薬剤投与指示</h1>

        <div class="button-bar">
            <a href="DoctorListAllPatientsServlet" class="button button-back">患者一覧へ戻る</a>
        </div>

        <c:if test="${not empty formError}">
            <p class="error-message"><c:out value="${formError}"/></p>
        </c:if>

        <div class="main-layout">
            <div class="patient-info">
                <h3>対象患者情報</h3>
                <c:if test="${not empty patient}">
                    <p><strong>ID:</strong> <c:out value="${patient.patId}"/></p>
                    <p><strong>氏名:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/></p>
                    <p><strong>保険証:</strong> <c:out value="${patient.hokenmei}"/></p>
                    <p><strong>有効期限:</strong> <fmt:formatDate value="${patient.hokenexp}" pattern="yyyy年MM月dd日"/></p>
                </c:if>
                <c:if test="${empty patient}">
                    <p>患者情報が選択されていません。</p>
                </c:if>
            </div>

            <div class="drug-selection-form">
                <h3>薬剤選択と数量</h3>
                <c:if test="${not empty patient}">
                    <form action="DoctorDrugAdministrationServlet" method="post">
                        <input type="hidden" name="action" value="addDrugToCart">
                        <input type="hidden" name="patientId" value="<c:out value='${patient.patId}'/>">

                        <div class="form-group">
                            <label for="medicineId">薬剤:</label>
                            <select id="medicineId" name="medicineId" required>
                                <option value="">-- 薬剤を選択 --</option>
                                <c:forEach var="med" items="${allMedicines}">
                                    <option value="<c:out value='${med.medicineId}'/>">
                                        <c:out value="${med.medicineName} (${med.unit})"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="quantity">数量:</label>
                            <select id="quantity" name="quantity" required>
                                <option value="">-- 数量を選択 --</option>
                                <c:forEach var="i" begin="1" end="30"> <%-- 例: 1から10まで --%>
                                    <option value="${i}">${i}</option>
                                </c:forEach>
                                <%-- 必要ならさらに大きな数量や手入力も検討 --%>
                            </select>
                        </div>
                        <button type="submit" class="button button-add">カートに追加</button>
                    </form>
                </c:if>
            </div>

            <div class="current-cart">
                <h3>現在の指示カート (患者ID: <c:out value="${patient.patId}"/>)</h3>
                <%
                    // このJSPスクリプトレット部分は、サーブレットでリクエスト属性に "currentCartItems" として
                    // セットされていれば不要になります。サーブレットのreloadFormAttributesで
                    // request.setAttribute("drugCart", drugCart); としているので、JSTLで直接参照可能です。
                    // List<DrugOrderItemBean> cartForDisplay = null;
                    // if (session.getAttribute("drugCart_" + pageContext.findAttribute("patient").getValue("patId")) != null) {
                    //      cartForDisplay = (List<DrugOrderItemBean>) session.getAttribute("drugCart_" + pageContext.findAttribute("patient").getValue("patId"));
                    // }
                %>
                <%-- <c:set var="currentCartItems" value="<%= cartForDisplay %>" /> --%>
                <%-- ↑上記の代わりに、サーブレットから渡されたリクエスト属性 "drugCart" を使う --%>
                <c:set var="currentCartItems" value="${drugCart}" />


                <c:choose>
                    <c:when test="${not empty currentCartItems}">
                        <table class="cart-table">
                            <thead>
                                <tr>
                                    <th>薬剤名</th>
                                    <th>数量</th>
                                    <th>単位</th>
                                    <th>操作</th> <%-- 「操作」列を追加 --%>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${currentCartItems}">
                                    <tr>
                                        <td><c:out value="${item.medicineName}"/></td>
                                        <td style="text-align:right;"><c:out value="${item.quantity}"/></td>
                                        <td><c:out value="${item.unit}"/></td>
                                        <td>
                                            <%-- ★★★ 削除ボタンのためのフォームを追加 ★★★ --%>
                                            <form action="DoctorDrugAdministrationServlet" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="removeDrugFromCart">
                                                <input type="hidden" name="patientId" value="<c:out value='${patient.patId}'/>">
                                                <input type="hidden" name="medicineIdToRemove" value="<c:out value='${item.medicineId}'/>">
                                                <button type="submit" class="button" style="background-color:#dc3545; font-size:0.8em; padding:3px 6px;" onclick="return confirm('この薬剤をカートから削除しますか？');">削除</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <div class="button-bar">
                             <form action="DoctorDrugAdministrationServlet" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="goToConfirm">
                                <input type="hidden" name="patientId" value="<c:out value='${patient.patId}'/>">
                                <button type="submit" class="button button-confirm">指示内容の確認へ進む</button>
                            </form>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="cart-empty">現在、カートに薬剤はありません。</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div> <%-- .main-layout の閉じタグ --%>
    </div> <%-- .page-container の閉じタグ --%>
</body>
</html>