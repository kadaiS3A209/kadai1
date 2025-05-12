package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.EmployeeBean;

public class EmployeeDAO {
	/**
     * 他病院情報を登録します（管理者機能：レコード追加）。
     * @param tabyouin 登録する他病院情報Bean
     * @return 登録に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean addEmployee(EmployeeBean emp) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO employee (empid,empfname,emplname,emppasswd,empsalt,emprole)VALUES (?, ?, ?, ?, ?, ?)";
        boolean result = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, emp.getEmpid());
            pstmt.setString(2, emp.getEmpfname());
            pstmt.setString(3, emp.getEmplname());
            pstmt.setString(4, emp.getEmppasswd());
            pstmt.setString(5, emp.getSalt());
            pstmt.setInt(6, emp.getRole());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 実際にはログファイルに出力するなど、より詳細なエラーハンドリングを
            // 例外が発生した場合、登録は失敗
            return false;
        } finally {
            DBManager.close(conn, pstmt);
        }
        return result;
    }

}
