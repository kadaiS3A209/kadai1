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
    
    
    /*
     * 指定された従業員IDが既に存在するかを検証します
     * @param empid 確認する従業員ID
     * @return 存在する場合はtrue　存在しない場合はfalse
     */
    
    public boolean isEmpidExists(String empid) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
    	String sql = "select count(*) from employee where empid = ?";
    	boolean exists = false;
    	try {
    		conn = DBManager.getConnection();
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, empid);
    		rs = ps.executeQuery();
    		
    		if(rs.next()) {
    			if(rs.getInt(1)>0) {
    				exists = true;
    			}
    		}
    		
    	}catch(SQLException e) {
        	e.printStackTrace();
        	return false;
        }finally {
        	DBManager.close(conn, ps, rs);
        }
    	return exists;
    }
    
    public int checkUser(String empid) {
    	Connection conn = null;
        PreparedStatement ps = null;
        String sql = "select role";
        int a = 0;
        return 0;
    }

    
    
   
    
    
}
