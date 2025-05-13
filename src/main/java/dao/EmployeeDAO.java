package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.EmployeeBean;

public class EmployeeDAO {
	/**
     * 従業員の登録機能
     */
    public boolean registerEmployee(EmployeeBean emp) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO employee (empid,empfname,emplname,emppasswd,salt,emprole)VALUES (?, ?, ?, ?, ?, ?)";
        boolean result = false;

        try {
            conn = DBManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, emp.getEmpid());
            ps.setString(2, emp.getEmpfname());
            ps.setString(3, emp.getEmplname());
            ps.setString(4, emp.getEmppasswd());
            ps.setString(5, emp.getSalt());
            ps.setInt(6, emp.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 実際にはログファイルに出力するなど、より詳細なエラーハンドリングを
            // 例外が発生した場合、登録は失敗
            return false;
        } finally {
            DBManager.close(conn, ps);
        }
        return result;
    }
    
    
 // パスワード更新メソッド
    public boolean updatePassword(String empId, String newHashedPassword, String newSalt) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE employee SET emppasswd = ?, salt = ? WHERE empid = ?";
        
        
        try {
            conn = DBManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newHashedPassword);
            ps.setString(2, newSalt);
            ps.setString(3, empId);
            int result = ps.executeUpdate();
            return result > 0;
            
        }catch(SQLException e) {
        	e.printStackTrace();
        	return false;
        }finally {
        	DBManager.close(conn, ps);
        }
    }

    
    
    /**
     * 指定されたロールプレフィックスを持つ従業員の最大のIDを取得します。
     * @param rolePrefix ロールプレフィックス (例: "RC", "DR")
     * @return 最大の従業員ID文字列。存在しない場合はnull。
     */
    public String findLastEmployeeIdForRole(String rolePrefix) {
        // IDは 'RC000001' のような形式と想定
    	Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT MAX(empid) FROM employee WHERE empid LIKE ?";
        String lastId = null;
        try {
            // ... (DB接続取得: con)
        	con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, rolePrefix + "%"); // 例: "RC%"
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lastId = rs.getString(1); // 最大IDを取得 (なければnullになる)
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            // ... (リソース解放)
        }
        return lastId;
    }
    
    
}
