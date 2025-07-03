<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>患者一覧・検索 (医師用)</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 95%; margin: 20px auto; }
    h1 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 0.9em;}
    th { background-color: #f2f2f2; }
    .expired { color: red; font-weight: bold; }
    .action-button { padding: 5px 10px; background-color: #007bff; color:white; text-decoration:none; border-radius:3px; border:none; cursor:pointer; }
    .button-bar { margin-bottom: 15px; }
    .search-filter-bar { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; background-color: #f9f9f9; display: flex; flex-wrap: wrap; align-items: center; gap: 15px; }
    .search-filter-bar label { margin-right: 5px; }
    .search-filter-bar input[type="text"] { margin-right: 10px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .search-filter-bar .button { padding: 8px 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor:pointer; }
</style>
</head>
<body>
    <div class="container">
        <h1>患者一覧・検索</h1>

        <div class="button-bar">
            <a href="ReturnToMenuServlet" class="button" style="background-color:#6c757d;">医師メニューへ戻る</a>
        </div>

        <%-- 検索フォーム --%>
        <form class="search-filter-bar" action="DoctorListAllPatientsServlet" method="post"> <%-- POSTで送信 --%>
            <div>
                <label for="searchPatId">患者ID検索:</label>
                <input type="text" id="searchPatId" name="searchPatId" value="<c:out value='${searchedPatId}'/>" placeholder="患者IDを入力">
            </div>
            <div>
                <label for="searchName">患者名検索(姓または名):</label>
                <input type="text" id="searchName" name="searchName" value="<c:out value='${searchedName}'/>" placeholder="例: 山田">
            </div>
            <button type="submit" class="button">検索</button>
            <a href="DoctorListAllPatientsServlet" class="button" style="background-color:#17a2b8;">全件表示/クリア</a>
        </form>

        <c:if test="${not empty searchedPatId || not empty searchedName}">
            <p>検索結果:</p>
        </c:if>

        <c:choose>
            <c:when test="${not empty patientList}">
                <table>
                    <thead>
                        <tr>
                            <th>患者ID</th>
                            <th>氏名 (姓 名)</th>
                            <th>保険証記号番号</th>
                            <th>保険証有効期限</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <jsp:useBean id="todayForDoctorList" class="java.util.Date" />
                        <c:forEach var="patient" items="${patientList}">
                            <tr>
                                <td><c:out value="${patient.patId}" /></td>
                                <td><c:out value="${patient.patLname} ${patient.patFname}" /></td>
                                <td><c:out value="${patient.hokenmei}" /></td>
                                <td class="${patient.hokenexp.before(todayForDoctorList) ? 'expired' : ''}">
                                    <fmt:formatDate value="${patient.hokenexp}" pattern="yyyy年MM月dd日" />
                                    <c:if test="${patient.hokenexp.before(todayForDoctorList)}"> (期限切れ)</c:if>
                                </td>
                                <td>
                                    <a href="DoctorDrugAdministrationServlet?action=start&patId=<c:out value='${patient.patId}'/>" class="action-button">薬剤投与指示</a>

 <a href="DoctorCreateConsultationServlet?patId=<c:out value='${patient.patId}'/>" class="action-button">診察開始</a>
                            <%-- 処置履歴確認へのリンク --%>
                            <a href="DoctorViewTreatmentHistoryServlet?patientIdForHistory=<c:out value='${patient.patId}'/>" class="action-button" style="background-color:#6c757d;">処置履歴</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p style="text-align:center; padding:20px;">
                    <c:choose>
                        <c:when test="${not empty searchedPatId || not empty searchedName}">検索条件に一致する患者は見つかりませんでした。</c:when>
                        <c:otherwise>登録されている患者はいません。</c:otherwise>
                    </c:choose>
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>