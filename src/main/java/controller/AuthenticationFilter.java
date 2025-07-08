package controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.EmployeeBean;

/**
 * ログイン認証を確認するフィルタ
 */
// ★保護したいURLパターンをここに記述します
/**
 * ログイン認証とロールベースのアクセス認可を確認するフィルタ
 */
// ★保護したいURLパターンをここに記述します
@WebFilter(urlPatterns = {
	    /* ----- 管理者機能サーブレット ----- */
	    "/AdminAddSupplierServlet",
	    "/AdminAddTabyouinServlet",
	    "/AdminChangeUserPasswordServlet",
	    "/AdminListAdministratorsServlet",
	    "/AdminListStaffServlet",
	    "/AdminManageEmployeesServlet",
	    "/AdminManageSuppliersServlet",
	    "/AdminManageTabyouinServlet",

	    /* ----- 受付機能サーブレット ----- */
	    "/ReceptionChangeInsuranceServlet",
	    "/ReceptionListPatientsServlet",
	    "/ReceptionRegisterPatientServlet",

	    /* ----- 医師機能サーブレット ----- */
	    "/DoctorDrugAdministrationServlet",
	    "/DoctorListAllPatientsServlet",
	    "/DoctorViewTreatmentHistoryServlet",

	    /* ----- 共通機能サーブレット（ログイン後） ----- */
	    "/ReturnToMenuServlet",
	    "/EmployeeChangeOwnPasswordServlet",
	    "/ShowWhiteboardServlet"
	})

public class AuthenticationFilter extends HttpFilter implements Filter {

    // ▼▼▼ doFilterメソッドをこの内容で上書きします ▼▼▼
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResponse = (HttpServletResponse) response;
	    
	    String servletPath = httpRequest.getServletPath(); // ★リクエストされたサーブレットのパスを取得

	    // ★ログ1: どのURLにアクセスしようとしているか確認
	    System.out.println("--- AuthenticationFilter ---");
	    System.out.println("Servlet Path: " + servletPath);

	    // ログインページ、ログアウト処理などはフィルタをバイパス
	    if ("/LoginServlet".equals(servletPath) || 
	        "/LogoutServlet".equals(servletPath) ||
	        servletPath.startsWith("/css/") || 
	        servletPath.startsWith("/js/")) {
	        System.out.println("Bypass: ログイン/ログアウト/リソースへのアクセスです。");
	        chain.doFilter(request, response);
	        return;
	    }

	    HttpSession session = httpRequest.getSession(false);

	    // ★ログインチェック
	    if (session == null || session.getAttribute("loggedInUser") == null) {
	        System.out.println("判定: 未ログインです。ログインページにリダイレクトします。");
	        httpResponse.sendRedirect(httpRequest.getContextPath() + "/LoginServlet");
	        return;
	    }

	    // ★ロールチェック
	    EmployeeBean loggedInUser = (EmployeeBean) session.getAttribute("loggedInUser");
	    int userRole = loggedInUser.getRole();
	    System.out.println("判定: ログイン済みです。ユーザーロール: " + userRole);

	    // ▼▼▼ ロールチェックのロジックを修正 ▼▼▼
	    // 管理者ページへのアクセスチェック
	    if (servletPath.startsWith("/Admin")) { // サーブレットパスが "/Admin" で始まるか
	        System.out.println("チェック: 管理者機能へのアクセスです。");
	        if (userRole != 3) { // 管理者ロール(ID:3)でなければ
	            System.err.println("★不正アクセス検知★: 管理者機能へのアクセス (ユーザーロール: " + userRole + ") -> アクセスを拒否します。");
	            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセスが許可されていません。");
	            return;
	        }
	    }

	    // 受付ページへのアクセスチェック
	    if (servletPath.startsWith("/Reception")) { // サーブレットパスが "/Reception" で始まるか
	        System.out.println("チェック: 受付機能へのアクセスです。");
	        if (userRole != 1) { // 受付ロール(ID:1)でなければ
	            System.err.println("★不正アクセス検知★: 受付機能へのアクセス (ユーザーロール: " + userRole + ") -> アクセスを拒否します。");
	            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセスが許可されていません。");
	            return;
	        }
	    }
	    
	    // 医師ページへのアクセスチェック
	    if (servletPath.startsWith("/Doctor")) { // サーブレットパスが "/Doctor" で始まるか
	        System.out.println("チェック: 医師機能へのアクセスです。");
	        if (userRole != 2) { // 医師ロール(ID:2)でなければ
	            System.err.println("★不正アクセス検知★: 医師機能へのアクセス (ユーザーロール: " + userRole + ") -> アクセスを拒否します。");
	            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセスが許可されていません。");
	            return;
	        }
	    }
	    
	    // レントゲン技師ページへのアクセスチェック
	    if (servletPath.startsWith("/Radiology")) { // サーブレットパスが "/Doctor" で始まるか
	        System.out.println("チェック: レントゲン技師機能へのアクセスです。");
	        if (userRole != 4) { // レントゲン技師ロール(ID:4)でなければ
	            System.err.println("★不正アクセス検知★: レントゲン技師機能へのアクセス (ユーザーロール: " + userRole + ") -> アクセスを拒否します。");
	            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセスが許可されていません。");
	            return;
	        }
	    }
	    
	    
	    // 臨床検査技師ページへのアクセスチェック
	    if (servletPath.startsWith("/Lab")) { // サーブレットパスが "/Doctor" で始まるか
	        System.out.println("チェック: 臨床検査技師機能へのアクセスです。");
	        if (userRole != 5) { // 臨床検査技師ロール(ID:5)でなければ
	            System.err.println("★不正アクセス検知★: 臨床検査技師機能へのアクセス (ユーザーロール: " + userRole + ") -> アクセスを拒否します。");
	            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "アクセスが許可されていません。");
	            return;
	        }
	    }
	    
	    // ▲▲▲ ここまで修正 ▲▲▲

	    System.out.println("フィルタのチェックを通過しました。");
	    // 全てのチェックを通過した場合、リクエストを続行
	    httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    httpResponse.setHeader("Pragma", "no-cache");
	    httpResponse.setDateHeader("Expires", 0);
	    
	    chain.doFilter(request, response);
	}


}

