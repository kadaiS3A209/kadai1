package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
    	String sql = "UPDATE employee SET emppasswd = ?, salt = ? WHERE empid = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean success = false;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, newHashedPassword);
            ps.setString(2, newSalt);
            ps.setString(3, empId);
            int rowsAffected = ps.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps);
        }
        return success;
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

    

    /**
     * 指定されたロールIDのリストに合致し、かつオプションで従業員IDにも合致する従業員のリストを取得します。
     *
     * @param roleIdsToInclude 取得対象のロールIDのリスト。nullまたは空の場合はロールで絞り込まない（※このメソッドの用途では通常指定）。
     * @param searchEmpId 検索する従業員ID。nullまたは空の場合はIDで絞り込まない。
     * @return 条件に一致する従業員のリスト。
     */
    public List<EmployeeBean> getEmployees(List<Integer> roleIdsToInclude, String searchEmpId) {
        List<EmployeeBean> employeeList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT empid, empfname, emplname, emprole FROM employee WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (roleIdsToInclude != null && !roleIdsToInclude.isEmpty()) {
            sqlBuilder.append(" AND emprole IN (");
            StringJoiner rolePlaceholders = new StringJoiner(",");
            for (Integer roleId : roleIdsToInclude) {
                rolePlaceholders.add("?");
                params.add(roleId);
            }
            sqlBuilder.append(rolePlaceholders.toString());
            sqlBuilder.append(")");
        }

        if (searchEmpId != null && !searchEmpId.trim().isEmpty()) {
            sqlBuilder.append(" AND empid LIKE ?");
            params.add("%" + searchEmpId.trim() + "%"); // 部分一致検索の場合
            // 完全一致の場合は params.add(searchEmpId.trim()); sqlBuilder.append(" AND empid = ?");
        }

        sqlBuilder.append(" ORDER BY empid ASC");

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sqlBuilder.toString());

            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }

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
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps, rs);
        }
        return employeeList;
    }
    
    
    /**
     * 指定された従業員の氏名（姓と名）を更新します。
     * @param empId 更新対象の従業員ID
     * @param newEmpLname 新しい姓
     * @param newEmpFname 新しい名
     * @return 更新に成功した場合は true、失敗した場合は false
     */
    public boolean updateEmployeeName(String empId, String newEmpLname, String newEmpFname) {
        String sql = "UPDATE employee SET emplname = ?, empfname = ? WHERE empid = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean success = false;

        // 入力値の基本的なチェック (nullや空文字など) はサーブレット側で行う前提
        if (empId == null || empId.trim().isEmpty() ||
            newEmpLname == null || newEmpLname.trim().isEmpty() ||
            newEmpFname == null || newEmpFname.trim().isEmpty()) {
            return false; // 不正な入力
        }

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, newEmpLname.trim());
            ps.setString(2, newEmpFname.trim());
            ps.setString(3, empId.trim());

            int rowsAffected = ps.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps);
        }
        return success;
    }
    
    
    /**
     * 全ての従業員を取得します (ロールによる絞り込みなし)。
     * @return 全従業員のリスト
     */
    public List<EmployeeBean> getAllEmployeesRegardlessOfRole() {
        // 既存のgetEmployeesメソッドを呼び出す (roleIdsToInclude に null を渡すか、
        // 全ての可能性のあるロールIDのリストを渡す。あるいは専用SQLを記述)
        // ここでは、getEmployeesが roleIdsToInclude = null で全件取得できる前提
        return getEmployees(null, null); // 既存のgetEmployeesメソッドを検索条件なしで呼び出す
                                             // (getEmployees の第3引数がBoolean listExpiredOnly だった場合)
                                             // PatientDAOのgetPatients(String, String, Boolean)を参考にした場合、
                                             // こちらは getEmployees(null, null) のような形になる。
                                             // EmployeeDAO.getEmployees の引数構成に合わせてください。
                                             // 仮に EmployeeDAO.getEmployees(List<Integer> roleIds, String searchEmpId) の場合:
                                             // return getEmployees(null, null);
    }
    
    
}
