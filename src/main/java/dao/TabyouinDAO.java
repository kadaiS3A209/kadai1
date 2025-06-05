package dao; // パッケージ名は適宜変更してください

 // DBManagerのパッケージに合わせてください
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // 一覧表示などで後ほど使用
import java.util.List;    // 一覧表示などで後ほど使用

import model.TabyouinBean; // 作成したBean

public class TabyouinDAO {

    /**
     * 指定された他病院IDが既に存在するかを確認します。
     * @param tabyouinId 確認する他病院ID
     * @return 存在する場合は true、存在しない場合は false
     */
    public boolean isTabyouinIdExists(String tabyouinId) {
        String sql = "SELECT 1 FROM tabyouin WHERE tabyouinid = ? LIMIT 1";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, tabyouinId);
            rs = ps.executeQuery();
            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps, rs);
        }
        return exists;
    }

    /**
     * 新しい他病院情報をデータベースに登録します。
     * 基本設計書 ID:H101 他病院登録機能 に対応
     * @param tabyouin 登録する他病院情報を持つTabyouinBean
     * @return 登録に成功した場合は true、失敗した場合は false
     */
    public boolean registerTabyouin(TabyouinBean tabyouin) {
        String sql = "INSERT INTO tabyouin (tabyouinid, tabyouinmei, tabyouinaddrss, tabyouintel, tabyouinshihonkin, kyukyu) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean success = false;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, tabyouin.getTabyouinId());
            ps.setString(2, tabyouin.getTabyouinMei());
            ps.setString(3, tabyouin.getTabyouinAddrss());
            ps.setString(4, tabyouin.getTabyouinTel());
            ps.setInt(5, tabyouin.getTabyouinShihonkin());
            ps.setInt(6, tabyouin.getKyukyu()); // 救急対応 (0 or 1)
            int result = ps.executeUpdate();
            success = (result > 0);
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps);
        }
        return success;
    }

    // 今後の機能追加（一覧表示、検索、更新など）でここにメソッドを追加していきます。
    
    /**
     * 全ての他病院情報を取得します。 (H101-A の一覧表示に対応)
     * @return 他病院リスト (TabyouinBeanのリスト)
     */
    public List<TabyouinBean> getAllTabyouin() {
        List<TabyouinBean> list = new ArrayList<>();
        String sql = "SELECT tabyouinid, tabyouinmei, tabyouinaddrss, tabyouintel, tabyouinshihonkin, kyukyu FROM tabyouin ORDER BY tabyouinid ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TabyouinBean t = new TabyouinBean();
                t.setTabyouinId(rs.getString("tabyouinid"));
                t.setTabyouinMei(rs.getString("tabyouinmei"));
                t.setTabyouinAddrss(rs.getString("tabyouinaddrss"));
                t.setTabyouinTel(rs.getString("tabyouintel"));
                t.setTabyouinShihonkin(rs.getInt("tabyouinshihonkin"));
                t.setKyukyu(rs.getInt("kyukyu"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps, rs);
        }
        return list;
    }

    /**
     * 指定された他病院IDの他病院情報を取得します。 (電話番号変更フォーム表示用)
     * @param tabyouinId 他病院ID
     * @return TabyouinBean オブジェクト、見つからなければ null
     */
    public TabyouinBean getTabyouinById(String tabyouinId) {
        TabyouinBean t = null;
        String sql = "SELECT tabyouinid, tabyouinmei, tabyouinaddrss, tabyouintel, tabyouinshihonkin, kyukyu FROM tabyouin WHERE tabyouinid = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, tabyouinId);
            rs = ps.executeQuery();
            if (rs.next()) {
                t = new TabyouinBean();
                t.setTabyouinId(rs.getString("tabyouinid"));
                t.setTabyouinMei(rs.getString("tabyouinmei"));
                t.setTabyouinAddrss(rs.getString("tabyouinaddrss"));
                t.setTabyouinTel(rs.getString("tabyouintel"));
                t.setTabyouinShihonkin(rs.getInt("tabyouinshihonkin"));
                t.setKyukyu(rs.getInt("kyukyu"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps, rs);
        }
        return t;
    }

    /**
     * 他病院の電話番号を更新します。 (H101-D 他病院情報変更機能 の一部)
     * @param tabyouinId 更新対象の他病院ID
     * @param newTel 新しい電話番号
     * @return 更新に成功した場合は true、失敗した場合は false
     */
    public boolean updateTabyouinTel(String tabyouinId, String newTel) {
        String sql = "UPDATE tabyouin SET tabyouintel = ? WHERE tabyouinid = ?";
        Connection con = null;
        PreparedStatement ps = null;
        boolean success = false;
        if (tabyouinId == null || tabyouinId.trim().isEmpty() || newTel == null) { // newTelが空を許容するかは要件次第
            return false; // 不正な入力
        }
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, newTel.trim());
            ps.setString(2, tabyouinId.trim());
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
     * 住所の部分一致で他病院を検索します。 (H101-E 住所→他病院検索機能)
     * @param partialAddress 検索する住所の一部。nullまたは空の場合は全件を返す。
     * @return 条件に一致する他病院リスト
     */
    public List<TabyouinBean> searchTabyouinByAddress(String partialAddress) {
        if (partialAddress == null || partialAddress.trim().isEmpty()) {
            return getAllTabyouin(); // 検索語が空なら全件表示
        }
        List<TabyouinBean> list = new ArrayList<>();
        String sql = "SELECT tabyouinid, tabyouinmei, tabyouinaddrss, tabyouintel, tabyouinshihonkin, kyukyu FROM tabyouin WHERE tabyouinaddrss LIKE ? ORDER BY tabyouinid ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + partialAddress.trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                TabyouinBean t = new TabyouinBean();
                t.setTabyouinId(rs.getString("tabyouinid"));
                t.setTabyouinMei(rs.getString("tabyouinmei"));
                t.setTabyouinAddrss(rs.getString("tabyouinaddrss"));
                t.setTabyouinTel(rs.getString("tabyouintel"));
                t.setTabyouinShihonkin(rs.getInt("tabyouinshihonkin"));
                t.setKyukyu(rs.getInt("kyukyu"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps, rs);
        }
        return list;
    }

    /**
     * 指定された最小資本金額以上の他病院情報を検索します。 (H101-F 資本金→他病院検索機能)
     * @param minCapital 検索する最小資本金額
     * @return 条件に一致する他病院情報のリスト。見つからない場合は空のリスト。
     */
    public List<TabyouinBean> searchTabyouinByCapital(int minCapital) {
        List<TabyouinBean> list = new ArrayList<>();
        String sql = "SELECT tabyouinid, tabyouinmei, tabyouinaddrss, tabyouintel, tabyouinshihonkin, kyukyu " +
                     "FROM tabyouin WHERE tabyouinshihonkin >= ? " +
                     "ORDER BY tabyouinshihonkin ASC, tabyouinid ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, minCapital);
            rs = ps.executeQuery();
            while (rs.next()) {
                TabyouinBean t = new TabyouinBean();
                t.setTabyouinId(rs.getString("tabyouinid"));
                t.setTabyouinMei(rs.getString("tabyouinmei"));
                t.setTabyouinAddrss(rs.getString("tabyouinaddrss"));
                t.setTabyouinTel(rs.getString("tabyouintel"));
                t.setTabyouinShihonkin(rs.getInt("tabyouinshihonkin"));
                t.setKyukyu(rs.getInt("kyukyu"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリングを推奨
        } finally {
            DBManager.close(con, ps, rs);
        }
        return list;
    }
}
