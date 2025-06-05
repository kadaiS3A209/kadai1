package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.ShiiregyoshaBean; // Beanのパスに合わせて修正

public class ShiiregyoshaDAO {

 

 /**
  * 指定された仕入先IDが既に存在するかを確認します。
  * @param shiireId 確認する仕入先ID
  * @return 存在する場合は true、存在しない場合は false
  */
 public boolean isShiireIdExists(String shiireId) {
     String sql = "SELECT 1 FROM shiiregyosha WHERE shiireid = ? LIMIT 1";
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;
     boolean exists = false;
     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         ps.setString(1, shiireId);
         rs = ps.executeQuery();
         if (rs.next()) {
             exists = true;
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
         DBManager.close(con, ps, rs);
     }
     return exists;
 }

 /**
  * 新しい仕入先情報をデータベースに登録します。
  * @param supplier 登録する仕入先情報を持つShiiregyoshaBean
  * @return 登録に成功した場合は true、失敗した場合は false
  */
 public boolean registerSupplier(ShiiregyoshaBean supplier) {
     String sql = "INSERT INTO shiiregyosha (shiireid, shiiremei, shiireaddress, shiiretel, shihonkin, nouki) VALUES (?, ?, ?, ?, ?, ?)";
     Connection con = null;
     PreparedStatement ps = null;
     boolean success = false;
     try {
    	 con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         ps.setString(1, supplier.getShiireId());
         ps.setString(2, supplier.getShiireMei());
         ps.setString(3, supplier.getShiireAddress());
         ps.setString(4, supplier.getShiireTel());
         ps.setInt(5, supplier.getShihonkin());
         ps.setInt(6, supplier.getNouki());
         int result = ps.executeUpdate();
         success = (result > 0);
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
    	 DBManager.close(con, ps);// rsは使わないのでnull
     }
     return success;
 }



 public List<ShiiregyoshaBean> getAllSuppliers() {
     List<ShiiregyoshaBean> supplierList = new ArrayList<>();
     String sql = "SELECT shiireid, shiiremei, shiireaddress, shiiretel, shihonkin, nouki FROM shiiregyosha ORDER BY shiireid ASC"; // ID順で表示
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;

     try {
         con = DBManager.getConnection(); // 実際の接続方法に合わせる
         ps = con.prepareStatement(sql);
         rs = ps.executeQuery();

         while (rs.next()) {
             ShiiregyoshaBean supplier = new ShiiregyoshaBean();
             supplier.setShiireId(rs.getString("shiireid"));
             supplier.setShiireMei(rs.getString("shiiremei"));
             supplier.setShiireAddress(rs.getString("shiireaddress"));
             supplier.setShiireTel(rs.getString("shiiretel"));
             supplier.setShihonkin(rs.getInt("shihonkin"));
             supplier.setNouki(rs.getInt("nouki"));
             supplierList.add(supplier);
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング (ログ記録など)
         // テストケース「仕入れ先一覧表示不良」を考慮し、エラー発生時は空リストを返すか、
         // nullを返してサーブレット側でエラー処理を明示的に行うかなどを検討。
         // ここでは空リストを返すようにしています。
     } finally {
         DBManager.close(con, ps, rs);
     }
     return supplierList;
 }
 
 
 /**
  * 指定された最小資本金額以上の仕入先情報を検索します。
  * @param minCapital 検索する最小資本金額
  * @return 条件に一致する仕入先情報のリスト。見つからない場合は空のリスト。
  */
 public List<ShiiregyoshaBean> searchSuppliersByCapital(int minCapital) {
     List<ShiiregyoshaBean> supplierList = new ArrayList<>();
     // 資本金が指定額以上で、資本金の昇順、次にIDの昇順で並び替え
     String sql = "SELECT shiireid, shiiremei, shiireaddress, shiiretel, shihonkin, nouki " +
                  "FROM shiiregyosha WHERE shihonkin >= ? " +
                  "ORDER BY shihonkin ASC, shiireid ASC";
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;

     try {
         con = DBManager.getConnection(); // 実際の接続方法に合わせる
         ps = con.prepareStatement(sql);
         ps.setInt(1, minCapital);
         rs = ps.executeQuery();

         while (rs.next()) {
             ShiiregyoshaBean supplier = new ShiiregyoshaBean();
             supplier.setShiireId(rs.getString("shiireid"));
             supplier.setShiireMei(rs.getString("shiiremei"));
             supplier.setShiireAddress(rs.getString("shiireaddress"));
             supplier.setShiireTel(rs.getString("shiiretel"));
             supplier.setShihonkin(rs.getInt("shihonkin"));
             supplier.setNouki(rs.getInt("nouki"));
             supplierList.add(supplier);
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
         DBManager.close(con, ps, rs);
     }
     return supplierList;
 }
 
 
 
 /**
  * 指定された仕入先IDの仕入先情報を取得します。 (電話番号変更フォーム表示用)
  * @param shiireId 仕入先ID
  * @return ShiiregyoshaBean オブジェクト、見つからなければ null
  */
 public ShiiregyoshaBean getShiiregyoshaById(String shiireId) {
     ShiiregyoshaBean s = null;
     String sql = "SELECT shiireid, shiiremei, shiireaddress, shiiretel, shihonkin, nouki FROM shiiregyosha WHERE shiireid = ?";
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;
     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         ps.setString(1, shiireId);
         rs = ps.executeQuery();
         if (rs.next()) {
             s = new ShiiregyoshaBean();
             s.setShiireId(rs.getString("shiireid"));
             s.setShiireMei(rs.getString("shiiremei"));
             s.setShiireAddress(rs.getString("shiireaddress"));
             s.setShiireTel(rs.getString("shiiretel"));
             s.setShihonkin(rs.getInt("shihonkin"));
             s.setNouki(rs.getInt("nouki"));
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリングを推奨
     } finally {
         DBManager.close(con, ps, rs);
     }
     return s;
 }

 /**
  * 仕入先の電話番号を更新します。 (A102-D)
  * @param shiireId 更新対象の仕入先ID
  * @param newTel 新しい電話番号
  * @return 更新に成功した場合は true、失敗した場合は false
  */
 public boolean updateShiiregyoshaTel(String shiireId, String newTel) {
     String sql = "UPDATE shiiregyosha SET shiiretel = ? WHERE shiireid = ?";
     Connection con = null;
     PreparedStatement ps = null;
     boolean success = false;
     if (shiireId == null || shiireId.trim().isEmpty() || newTel == null ) { // newTelが空でもDBにはセットする（空を許容する場合）
         return false; // IDが不正な場合は更新しない
     }
     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         ps.setString(1, newTel.trim()); // 電話番号はトリムして保存
         ps.setString(2, shiireId.trim());
         int rowsAffected = ps.executeUpdate();
         success = (rowsAffected > 0);
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリングを推奨
     } finally {
         DBManager.close(con, ps);
     }
     return success;
 }

 /**
  * 住所の部分一致で仕入先を検索します。 (A102-E)
  * @param partialAddress 検索する住所の一部。nullまたは空の場合は全件を返す。
  * @return 条件に一致する仕入先リスト
  */
 public List<ShiiregyoshaBean> searchShiiregyoshaByAddress(String partialAddress) {
     List<ShiiregyoshaBean> list = new ArrayList<>();
     String sql;
     List<Object> params = new ArrayList<>();

     if (partialAddress == null || partialAddress.trim().isEmpty()) {
         // 検索語が空なら全件表示 (既存のgetAllShiiregyoshaを呼び出すか、同様のSQLを実行)
         return getAllSuppliers();
     } else {
         sql = "SELECT shiireid, shiiremei, shiireaddress, shiiretel, shihonkin, nouki FROM shiiregyosha WHERE shiireaddress LIKE ? ORDER BY shiireid ASC";
         params.add("%" + partialAddress.trim() + "%");
     }

     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;
     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         for (int i = 0; i < params.size(); i++) {
             ps.setObject(i + 1, params.get(i));
         }
         rs = ps.executeQuery();
         while (rs.next()) {
             ShiiregyoshaBean s = new ShiiregyoshaBean();
             s.setShiireId(rs.getString("shiireid"));
             s.setShiireMei(rs.getString("shiiremei"));
             s.setShiireAddress(rs.getString("shiireaddress"));
             s.setShiireTel(rs.getString("shiiretel"));
             s.setShihonkin(rs.getInt("shihonkin"));
             s.setNouki(rs.getInt("nouki"));
             list.add(s);
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリングを推奨
     } finally {
         DBManager.close(con, ps, rs);
     }
     return list;
 }
}
