package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    
    
    public EmployeeBean getEmployeeById(String empId) {
        EmployeeBean employee = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        //[span_1](start_span)// SQLクエリ: employee テーブルから指定された empid のレコードを取得[span_1](end_span)
        String sql = "SELECT empid, empfname, emplname, emppasswd, salt, emprole FROM employee WHERE empid = ?"; //[span_2](start_span)//[span_2](end_span)

        try {
            // データベースへの接続 (実際にはコネクションプーリングなどを検討)
            // Class.forName("com.mysql.cj.jdbc.Driver"); // JDBCドライバのロード (古い方法、不要な場合も)
            con = DBManager.getConnection();

            ps = con.prepareStatement(sql);
            ps.setString(1, empId);

            rs = ps.executeQuery();

            if (rs.next()) {
                employee = new EmployeeBean();
                employee.setEmpid(rs.getString("empid")); //[span_3](start_span)//[span_3](end_span)
                employee.setEmpfname(rs.getString("empfname")); //[span_4](start_span)//[span_4](end_span)
                employee.setEmplname(rs.getString("emplname")); //[span_5](start_span)//[span_5](end_span)
                employee.setEmppasswd(rs.getString("emppasswd")); //[span_6](start_span)//[span_6](end_span)
                employee.setSalt(rs.getString("salt")); //[span_7](start_span)//[span_7](end_span)
                employee.setRole(rs.getInt("emprole")); //[span_8](start_span)//[span_8](end_span)
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 実際にはより適切なエラーハンドリングを行う
            // 例: throw new RuntimeException("Database error fetching employee by ID", e);
        } finally {
            // リソースの解放
        	DBManager.close(con, ps, rs);
        }
        return employee; // 見つからなければ employee は null のまま返される
    }


 // --- Method to get all employees ---
    public List<EmployeeBean> getAllEmployees() {
        List<EmployeeBean> employeeList = new ArrayList<>();
        String sql = "SELECT empid, empfname, emplname, emprole FROM employee ORDER BY empid ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
        	con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeBean employee = new EmployeeBean();
                employee.setEmpid(rs.getString("empid"));
                employee.setEmpfname(rs.getString("empfname"));
                employee.setEmplname(rs.getString("emplname"));
                employee.setRole(rs.getInt("emprole"));
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider more robust error handling
        } finally {
        	DBManager.close(con, ps, rs);
        }
        return employeeList;
    }

    

    
    
    
   
    
    
}
