import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class TableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private int rowCount;
    private int columnCount;
    private ArrayList<Object> data = new ArrayList();
    private ResultSet result;

    public TableModel(ResultSet rs) throws Exception {
        this.result = rs;
        ResultSetMetaData metaData = rs.getMetaData();
        this.rowCount = 0;

        for(this.columnCount = metaData.getColumnCount(); rs.next(); ++this.rowCount) {
            Object[] row = new Object[this.columnCount];

            for(int j = 0; j < this.columnCount; ++j) {
                row[j] = rs.getObject(j + 1);
            }

            this.data.add(row);
        }

    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = (Object[]) this.data.get(rowIndex);
        return row[columnIndex];
    }

    public String getColumnName(int columnIndex) {
        try {
            ResultSetMetaData metaData = this.result.getMetaData();
            return metaData.getColumnName(columnIndex + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
