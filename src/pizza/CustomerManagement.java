/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pizza;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Vincent
 */
public class CustomerManagement extends JInternalFrame {

    private JTable custTable;
    private JScrollPane scrollPane;

    public CustomerManagement() {
        initComponent();
    }

    public void initComponent() {
        setPreferredSize(new Dimension(1366, 600));
        DBConnection dbc = new DBConnection();
        try {
            Connection conn = dbc.connect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT username, Customers . * , LastLogin\n"
                    + "FROM Customers\n"
                    + "Left JOIN User ON User.cid = Customers.cid\n"
                    + "ORDER BY Customers.cid";
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            Vector<String> columnNames = new Vector<String>();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(meta.getColumnName(i));
            }
            columnNames.add("Save Changes");
            columnNames.add("Delete");
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            int activeIndex = 0;
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    if (columnIndex != 7) {
                        vector.add(rs.getObject(columnIndex));
                    } else {
                        activeIndex = columnIndex;
                        if (rs.getString("active_status").equals("a")) {
                            vector.add("De-active");
                        } else {
                            vector.add("Active");
                        }
                    }
                }

                vector.add("Save Change");
                vector.add("Delete");
                data.add(vector);
            }
            int secondLast = columnCount;
            columnCount++;
            DefaultTableModel dtm = new DefaultTableModel(data, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    if (column == 0 || column == 1 || column == 9) {
                        return false;
                    }
                    return super.isCellEditable(row, column);
                }
            };
            custTable = new JTable(dtm) {
                //  Returning the Class of each column will allow different
                //  renderers to be used based on Class
                public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
                }
            };

            custTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
            custTable.setFillsViewportHeight(true);
            custTable.getTableHeader().setReorderingAllowed(false);
            dbc.disconnect();
            ButtonColumn buttonColumn = new ButtonColumn(custTable, activeIndex - 1, secondLast, columnCount);
            scrollPane = new JScrollPane(custTable);
            getContentPane().add(scrollPane);
            setClosable(true);
            pack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ButtonColumn extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor, ActionListener {

        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;

        public ButtonColumn(JTable table, int activeColumn, int secondLast, int column) {
            super();
            this.table = table;
            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
            columnModel.getColumn(secondLast).setCellRenderer(this);
            columnModel.getColumn(secondLast).setCellEditor(this);
            columnModel.getColumn(activeColumn).setCellRenderer(this);
            columnModel.getColumn(activeColumn).setCellEditor(this);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (hasFocus) {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            } else if (isSelected) {
                renderButton.setForeground(table.getSelectionForeground());
                renderButton.setBackground(table.getSelectionBackground());
            } else {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }

            renderButton.setText((value == null) ? "" : value.toString());
            return renderButton;
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            text = (value == null) ? "" : value.toString();
            editButton.setText(text);
            return editButton;
        }

        public Object getCellEditorValue() {
            return text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
            try {
                DBConnection dbc = new DBConnection();
                Connection conn = dbc.connect();
                Statement stmt = conn.createStatement();
                switch (e.getActionCommand()) {
                    case "De-active":
                        Object cidObj = custTable.getModel().getValueAt(custTable.getSelectedRow(), 1);
                        int cid = Integer.parseInt(cidObj.toString());
                        stmt.executeUpdate("Update Customers set active_status='d'"
                                + "where cid=" + cid);
                        editButton.setText("Active");
                        break;
                    case "Active":
                        cidObj = custTable.getModel().getValueAt(custTable.getSelectedRow(), 1);
                        cid = Integer.parseInt(cidObj.toString());
                        stmt.executeUpdate("Update Customers set active_status='a'"
                                + "where cid=" + cid);
                        editButton.setText("De-active");
                        break;
                    case "Save Change":
                        DefaultTableModel model = (DefaultTableModel) custTable.getModel();
                        int columnCount = model.getColumnCount() - 2;
                        String sql = "Update Customers set ";
                        String value,
                         columnName;
                        for (int i = 2; i < columnCount; i++) {
                            if (i != 6 && i != 9) {
                                if (model.getValueAt(custTable.getSelectedRow(), i) != null) {
                                    value = model.getValueAt(custTable.getSelectedRow(), i).toString();
                                    columnName = model.getColumnName(i);
                                    if (value.equals("")) {
                                        sql += (columnName + "=NULL, ");
                                    } else {
                                        sql += (columnName + "='" + value + "',");
                                    }
                                }
                            }
                        }
                        sql = sql.substring(0, sql.length() - 2);
                        sql += "' where cid=" + model.getValueAt(custTable.getSelectedRow(), 1);
                        JOptionPane.showMessageDialog(null, "Change is Saved",
                                "Saved", JOptionPane.INFORMATION_MESSAGE);
                        stmt.executeUpdate(sql);
                        break;
                    case "Delete":
                        DefaultTableModel tmodel = (DefaultTableModel) custTable.getModel();
                        int row = custTable.getSelectedRow();
                        System.out.println(row);
                        tmodel.removeRow(row);
                        //stmt.executeUpdate("Delete from Customers where cid="
                        //        + cid);
                        break;
                }
                stmt.close();
                dbc.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

}
