package dao; // パッケージは適宜変更

 // DBManagerクラスをインポート
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.MedicineBean; // パスを合わせる

public class MedicineDAO {

    /**
     * 登録されている全ての薬剤情報を取得します。
     * @return 薬剤情報のリスト (MedicineBeanのリスト)。取得できない場合は空のリスト。
     */
    public List<MedicineBean> getAllMedicines() {
        List<MedicineBean> medicineList = new ArrayList<>();
        String sql = "SELECT medicineid, medicinename, unit FROM medicine ORDER BY medicineid ASC"; // ID順で表示
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                MedicineBean medicine = new MedicineBean();
                medicine.setMedicineId(rs.getString("medicineid"));
                medicine.setMedicineName(rs.getString("medicinename"));
                medicine.setUnit(rs.getString("unit"));
                medicineList.add(medicine);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング (ログ記録など)
        } finally {
            DBManager.close(con, ps, rs);
        }
        return medicineList;
    }

    /**
     * 指定された薬剤IDに対応する薬剤情報を取得します。
     * @param medicineId 検索する薬剤ID
     * @return 薬剤情報を持つMedicineBean、見つからなければnull。
     */
    public MedicineBean getMedicineById(String medicineId) {
        MedicineBean medicine = null;
        String sql = "SELECT medicineid, medicinename, unit FROM medicine WHERE medicineid = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, medicineId);
            rs = ps.executeQuery();

            if (rs.next()) {
                medicine = new MedicineBean();
                medicine.setMedicineId(rs.getString("medicineid"));
                medicine.setMedicineName(rs.getString("medicinename"));
                medicine.setUnit(rs.getString("unit"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps, rs);
        }
        return medicine;
    }
}