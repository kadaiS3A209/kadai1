<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>処方入力</title>
<style>
    body { font-family: 'Segoe UI', Meiryo, sans-serif; background-color: #f4f7f6; color: #333; margin: 0; padding: 20px; }
    .container { width: 90%; max-width: 900px; margin: 20px auto; padding: 25px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); }
    h1, h2 { text-align: center; color: #007bff; }
    h2 { color: #333; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-top: 30px; }
    .info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 25px; padding: 15px; border-radius: 4px; font-size: 1.1em; }
    table { width: 100%; border-collapse: collapse; margin-top: 15px; }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background-color: #f2f2f2; font-weight: bold; }
    .add-form { margin-top: 20px; padding: 20px; background-color: #fdfdfd; border: 1px solid #ddd; border-radius: 5px; display: flex; align-items: center; gap: 15px; }
    .add-form select, .add-form input { padding: 8px; font-size: 1em; border: 1px solid #ccc; border-radius: 4px; }
    .add-form select { flex: 3; } /* 薬剤選択を広く */
    .add-form input { flex: 1; }  /* 数量を狭く */
    .button { display: inline-block; padding: 8px 15px; background-color: #28a745; color: white !important; border: none; border-radius: 4px; font-size: 1em; cursor: pointer; text-decoration: none; }
    .back-link { display: block; text-align: center; margin-top: 30px; }
    .no-data { color: #777; }
</style>
</head>
<body>
    <div class="container">
        <h1>処方入力・確認</h1>

        <%-- 要件: 処方入力の際、指定した患者の疾病名が表示される --%>
        <c:if test="${not empty consultation}">
            <div class="info-bar">
                <strong>患者:</strong> <c:out value="${consultation.patientName}"/> <br>
                <strong>確定疾病名:</strong> <c:out value="${consultation.diseaseName}"/> (<c:out value="${consultation.diseaseCode}"/>)
            </div>
        </c:if>

        <section>
            <h2>現在の処方内容</h2>
            <c:choose>
                <c:when test="${not empty prescribedList}">
                    <table>
                        <thead><tr><th>薬剤名</th><th>数量</th><th>単位</th></tr></thead>
                        <tbody>
                            <c:forEach var="treatment" items="${prescribedList}">
                                <tr>
                                    <td><c:out value="${treatment.medicineName}"/></td>
                                    <td><c:out value="${treatment.quantity}"/></td>
                                    <td><c:out value="${treatment.unit}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p class="no-data">現在、この診察に対する処方はありません。</p>
                </c:otherwise>
            </c:choose>
        </section>

        <section>
            <h2>薬剤の追加</h2>
            <form class="add-form" action="DoctorPrescriptionServlet" method="post">
                <input type="hidden" name="action" value="addPrescription">
                <input type="hidden" name="consultationId" value="<c:out value='${consultation.consultationId}'/>">
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                
                <label for="medicineId">薬剤:</label>
                <select id="medicineId" name="medicineId" required>
                    <option value="">-- 薬剤を選択 --</option>
                    <c:forEach var="medicine" items="${medicineList}">
                        <option value="<c:out value='${medicine.medicineId}'/>">
                            <c:out value="${medicine.medicineName}"/>
                        </option>
                    </c:forEach>
                </select>

                <label for="quantity">数量:</label>
                <input type="number" id="quantity" name="quantity" min="1" value="1" required>

                <button type="submit" class="button">処方に追加</button>
            </form>
        </section>

        <div class="back-link">
            <%-- 処方対象の診察を選択する画面に戻る --%>
            <a href="DoctorPrescriptionServlet?action=selectConsultation&patId=${consultation.patientId}">診察選択画面へ戻る</a>
        </div>
    </div>
</body>
</html>
