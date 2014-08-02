/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pizza;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * To Do: Missing add order button, choose type of order, and current price
 *
 * @author Vincent
 */
public class Order extends JInternalFrame{
    private final int CONNECTED = 1;
    private final int DISCONNECTED = 0;
    private Connection conn = null;
    private JLabel searchLabel;
    private JLabel pidLabel;
    private JTextField pidText;
    private JLabel productNameLabel;
    private JTextField productNameText;
    private JLabel sizeLabel;
    private JTextField sizeText;
    private JLabel category;
    private JComboBox switcher;
    private JLabel suppLabel;
    private JTextField suppText;
    private JLabel manuLabel;
    private JTextField manuText;
    private JLabel priceMin;
    private JLabel priceMax;
    private JTextField minText;
    private JTextField maxText;
    private JButton searchButton;
    private JButton reset;
    private JLabel topLabel;
    private JComboBox typeOfPayment;
    private JLabel dateDeliLabel;
    private JComboBox dateDeli;
    private JComboBox ftCombo = new JComboBox(new String[]{"Today", "Tomorrow"});
    private JComboBox timeCombo = new JComboBox(new String[]{
        "9:00", "9:30", 
        "10:00", "10:30", 
        "11:00", "11:30",
        "12:00", "12:30",
        "13:00", "13:30", 
        "14:00", "14:30", 
        "15:00", "15:30",
        "16:00", "16:30", 
        "17:00", "17:30",
        "18:00", "18:30", 
        "19:00", "19:30", 
        "20:00", "20:30"
    });
    private JLabel stadingOrderLabel;
    private JComboBox standingOrder;
    private JPanel cards;
    private JButton cancel;
    private JButton[] buttons;
    private JTable orderTable;
    private final DBConnection dbc = new DBConnection();
    private Statement stmt;
    private final String pizzaSql = "select pro.pid, piz.name, piz.size, pro.price, pro.tax, "
            + "pro.manufacture, pro.supplier, pro.onSale "
            + "from Product pro "
            + "inner join Pizza piz on pro.pid = piz.pid";
    private final String toppingSql = "select pro.pid, top.name, pro.price, pro.tax, "
            + "pro.manufacture, pro.supplier, pro.onSale "
            + "from Product pro "
            + "inner join Topping top on pro.pid = top.pid";
    private final String drinkSql = "select pro.pid, drk.name, drk.vol, pro.price, pro.tax, "
            + "pro.manufacture, pro.supplier, pro.onSale "
            + "from Product pro "
            + "inner join Drink drk on pro.pid = drk.pid";
    private JLabel subTotal;
    private JLabel tax;
    private JLabel currentPriceLabel;
    public double sub = 0, t = 0;
    private double currentPrice = 0;
    private JButton checkOut;
    private String customerName;
    private int cid;
    private JLabel customerLabel;
    private int addrId;
    
    public Order(String customerName, int cid, int addrId) {
        this.customerName = customerName;
        this.cid = cid;
        this.addrId = addrId;
        initComponent();
        
    }

    private void initComponent() {
        setPreferredSize(new Dimension(1366, 600));
        setLayout(null);
        searchLabel = new JLabel("Search Criteria");
        searchLabel.setSize(100, 30);
        searchLabel.setLocation(0, 0);
        getContentPane().add(searchLabel);
        pidLabel = new JLabel("PID: ");
        pidLabel.setSize(100, 30);
        pidLabel.setLocation(0, 30);
        getContentPane().add(pidLabel);
        pidText = new JTextField();
        pidText.setSize(100, 30);
        pidText.setLocation(100, 30);
        getContentPane().add(pidText);
        productNameLabel = new JLabel("Product Name: ");
        productNameLabel.setSize(100, 30);
        productNameLabel.setLocation(210, 30);
        getContentPane().add(productNameLabel);
        productNameText = new JTextField();
        productNameText.setSize(100, 30);
        productNameText.setLocation(310, 30);
        getContentPane().add(productNameText);
        sizeLabel = new JLabel("Size/Volume: ");
        sizeLabel.setSize(100, 30);
        sizeLabel.setLocation(0, 60);
        getContentPane().add(sizeLabel);
        sizeText = new JTextField();
        sizeText.setSize(100, 30);
        sizeText.setLocation(100, 60);
        getContentPane().add(sizeText);
        category = new JLabel("Category:");
        switcher = new JComboBox(new String[]{"Pizza", "Toppings", "Drink"});
        category.setLocation(210, 60);
        category.setSize(100, 30);
        getContentPane().add(category);
        switcher.setSize(100, 30);
        switcher.setLocation(310, 60);
        getContentPane().add(switcher);
        suppLabel = new JLabel("Supplier: ");
        suppLabel.setSize(100, 30);
        suppLabel.setLocation(0, 90);
        getContentPane().add(suppLabel);
        suppText = new JTextField();
        suppText.setSize(100, 30);
        suppText.setLocation(100, 90);
        getContentPane().add(suppText);
        manuLabel = new JLabel("Manufacture: ");
        manuLabel.setSize(100, 30);
        manuLabel.setLocation(210, 90);
        getContentPane().add(manuLabel);
        manuText = new JTextField();
        manuText.setSize(100, 30);
        manuText.setLocation(310, 90);
        getContentPane().add(manuText);
        priceMin = new JLabel("Price Range: Min ");
        priceMin.setSize(100, 30);
        priceMin.setLocation(0, 120);
        getContentPane().add(priceMin);
        minText = new JTextField();
        minText.setLocation(100, 120);
        minText.setSize(100, 30);
        getContentPane().add(minText);
        priceMax = new JLabel("Max: ");
        priceMax.setLocation(210, 120);
        priceMax.setSize(100, 30);
        getContentPane().add(priceMax);
        maxText = new JTextField();
        maxText.setLocation(310, 120);
        maxText.setSize(100, 30);
        getContentPane().add(maxText);
        searchButton = new JButton("Search");
        searchButton.setSize(100, 30);
        searchButton.setLocation(30, 150);
        SearchHandler sh = new SearchHandler();
        searchButton.addActionListener(sh);
        getContentPane().add(searchButton);
        
        reset = new JButton("Reset");
        reset.setLocation(210, 150);
        reset.setSize(100, 30);
        getContentPane().add(reset);
        reset.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                pidText.setText("");
                productNameText.setText("");
                sizeText.setText("");
                manuText.setText("");
                suppText.setText("");
                minText.setText("");
                maxText.setText("");
            }
            
        });
        checkOut = new JButton("Checkout");
        cancel = new JButton("Cancel");
        setTitle("Order");
        
        cards = new JPanel(new CardLayout());
        cards.add(new JScrollPane(getTable(pizzaSql)), "Pizza");
        cards.add(new JScrollPane(getTable(toppingSql)), "Toppings");
        cards.add(new JScrollPane(getTable(drinkSql)), "Drink");
        switcher.addItemListener(new CardsHandler());
        cards.setSize(683, 210);
        cards.setLocation(0, 200);
        getContentPane().add(cards);
        
        topLabel = new JLabel("Type of Payment: ");
        topLabel.setSize(100, 30);
        topLabel.setLocation(0, 410);
        getContentPane().add(topLabel);
        typeOfPayment = new JComboBox(new String[]{"Cash", "Credit", "Check"});
        typeOfPayment.setLocation(100, 410);
        typeOfPayment.setSize(100, 30);
        getContentPane().add(typeOfPayment);
        
        dateDeliLabel = new JLabel("Date/Time of Delivery:");
        dateDeliLabel.setLocation(0, 440);
        dateDeliLabel.setSize(130, 30);
        getContentPane().add(dateDeliLabel);
        dateDeli = new JComboBox(new String[]{"Order Now", "Future Time"});
        dateDeli.setLocation(130, 440);
        dateDeli.setSize(100, 30);
        getContentPane().add(dateDeli);
        dateDeli.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               if (dateDeli.getSelectedItem().equals("Future Time")){
                   ftCombo.setLocation(250, 440);
                   ftCombo.setSize(180, 30);
                   timeCombo.setSize(100, 30);
                   timeCombo.setLocation(450, 440);
                   getContentPane().add(ftCombo);
                   getContentPane().add(timeCombo);
                   repaint();
                   
               }
               else{
                   getContentPane().remove(ftCombo);
                   getContentPane().remove(timeCombo);
                   repaint();
               }
            }
        });
        
        stadingOrderLabel = new JLabel("Standing Order: ");
        stadingOrderLabel.setSize(100, 30);
        stadingOrderLabel.setLocation(0, 470);
        getContentPane().add(stadingOrderLabel);
        standingOrder = new JComboBox(new String[]{"Regular", "Daily", "Weekly"});
        standingOrder.setSize(100, 30);
        standingOrder.setLocation(130, 470);
        getContentPane().add(standingOrder);
        standingOrder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               if (dateDeli.getSelectedItem().equals("Future Time")){
                   System.out.println("Future Time");
               }
               else{
                   System.out.println("Now");
               }
            }
        });        
        checkOut.setSize(100, 50);
        checkOut.setLocation(0, 510);
        CheckoutHandler ch = new CheckoutHandler();
        checkOut.addActionListener(ch);
        cancel.setSize(100, 50);
        cancel.setLocation(130, 510);
        getContentPane().add(checkOut);
        getContentPane().add(cancel);
        
       
        // Right Panel side
        customerLabel = new JLabel("Customer Name: " + this.customerName);
        customerLabel.setLocation(683, 0);
        customerLabel.setSize(300, 20);
        getContentPane().add(customerLabel);
        getOrderTable();
        subTotal = new JLabel("Sub Total: 0");
        tax = new JLabel("Tax: 0");
        subTotal.setSize(300, 20);
        subTotal.setLocation(683, 450);
        tax.setSize(300, 20);
        tax.setLocation(683, 480);
        currentPriceLabel = new JLabel("Current Price: " + Double.toString(currentPrice));
        currentPriceLabel.setSize(300, 20);
        currentPriceLabel.setLocation(683, 510);
        getContentPane().add(subTotal);
        getContentPane().add(tax);
        getContentPane().add(currentPriceLabel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setSize(600, 400);
        scrollPane.setLocation(683, 30);
        getContentPane().add(scrollPane);
        CancelHandler cl = new CancelHandler();
        cancel.addActionListener(cl);
        pack();

    }

    private void getOrderTable() {
        Vector<String> orderColumns = new Vector<String>();
        orderColumns.add("Product ID");
        orderColumns.add("Product");
        orderColumns.add("Quantity");
        orderColumns.add("Price");
        orderColumns.add("Tax");
        orderColumns.add("Cancel Order");
        Vector<Object> orderData = new Vector<Object>();
        DefaultTableModel dtm = new DefaultTableModel(orderData, orderColumns) {
            public boolean isCellEditable(int row, int column) {
                if (column != 5) {
                    return false;
                }
                return super.isCellEditable(row, column);
            }
        ;
        };
        orderTable = new JTable(dtm) {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        //orderTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        orderTable.setFillsViewportHeight(true);
        getContentPane().add(new JScrollPane(orderTable));
        orderTable.getTableHeader().setReorderingAllowed(false);
        ButtonColumn buttonColumn2 = new ButtonColumn(orderTable, orderColumns.size() - 1);
    }
    
    private class SearchHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql;
            Object[][] data = new Object[7][2];
            data[0][0] = "pid";
            data[1][0] = "name";
            data[2][0] = "size";
            data[3][0] = "manufacture";
            data[4][0] = "supplier";
            data[5][0] = "min";
            data[6][0] = "max";
            data[0][1] = pidText.getText();
            data[1][1] = productNameText.getText();
            data[2][1] = sizeText.getText();
            data[3][1] = manuText.getText();
            data[4][1] = suppText.getText();
            data[5][1] = minText.getText();
            data[6][1] = maxText.getText();
            if (switcher.getSelectedItem().equals("Pizza")){
                sql = "Select pid, name, size, price,"
                        + "tax, manufacture, supplier, onSale from Pizza_View where ";
                for (int i = 0; i < 5; i++){
                    if (!data[i][1].equals("")){
                        sql += (data[i][0].toString() + " like '" 
                                + data[i][1].toString()+"%' and ");
                        }
                }
                if (!data[5][1].equals(""))
                    sql += ("price >= " + data[5][1] + " and ");
                if (!data[6][1].equals(""))
                    sql += (" price <= " + data[6][1] + " and ");
                sql = sql.substring(0, sql.length()-4);
                cards.add(new JScrollPane(getTable(sql)), "Pizza");
                CardLayout c1 = (CardLayout)(cards.getLayout());
                c1.show(cards, "Pizza");
            }
            else if (switcher.getSelectedItem().equals("Toppings")){
                sql = "Select pid, name, price,"
                        + "tax, manufacture, supplier, onSale from Topping_View where ";
                for (int i = 0; i < 5; i++) {
                    if ((!data[i][1].equals("")) && (!data[i][0].equals("size"))) {
                        sql += (data[i][0].toString() + " like '"
                                + data[i][1].toString() + "%' and ");
                    }
                }
                if (!data[5][1].equals("")) {
                    sql += ("price >= " + data[5][1] + " and ");
                }
                if (!data[6][1].equals("")) {
                    sql += (" price <= " + data[6][1] + " and ");
                }
                sql = sql.substring(0, sql.length() - 4);
                System.out.println(sql);
                cards.add(new JScrollPane(getTable(sql)), "Topping");
                CardLayout c1 = (CardLayout) (cards.getLayout());
                c1.show(cards, "Topping");
            }
            else{
                sql = "Select pid, name, vol, price,"
                        + "tax, manufacture, supplier, onSale from Drink_View where ";
                for (int i = 0; i < 5; i++) {
                    if (!data[i][1].equals("")) {
                        String col = data[i][0].toString();
                        if (col.equals("size")){
                            col = "vol";
                        }
                        sql += (col + " like '"
                                + data[i][1].toString() + "%' and ");
                    }
                }
                if (!data[5][1].equals("")) {
                    sql += ("price >= " + data[5][1] + " and ");
                }
                if (!data[6][1].equals("")) {
                    sql += (" price <= " + data[6][1] + " and ");
                }
                sql = sql.substring(0, sql.length() - 4);
                cards.add(new JScrollPane(getTable(sql)), "Drink");
                CardLayout c1 = (CardLayout) (cards.getLayout());
                c1.show(cards, "Drink");
            }
        }
        
    }
    
    private JTable getTable(String sql) {
        try {
            if(dbc.getConnectStatus() == DISCONNECTED){
                conn = dbc.connect();
                dbc.setConnectStatus(CONNECTED);
            }
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            buttons = new JButton[rowCount];
            for (int i = 0; i < rowCount; i++) {
                buttons[i] = new JButton("Order");
                buttons[i].setSize(30, 30);
            }
            ResultSetMetaData meta = rs.getMetaData();
            Vector<String> columnNames = new Vector<String>();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(meta.getColumnName(i));
            }
            columnNames.add("Order");
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            int i = 0;
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rs.getObject(columnIndex));
                }
                if (i < rowCount) {
                    vector.add("Order");
                }
                data.add(vector);
                i++;
            }
            DefaultTableModel dtm = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    if ((column != 8) && !switcher.getSelectedItem().equals("Toppings")) {
                        return false;
                    }
                    else if ((column != 7) && switcher.getSelectedItem().equals("Toppings")){
                            return false;
                    }
                    return super.isCellEditable(row, column);
                }
            };
            JTable table = new JTable(dtm) {
                //  Returning the Class of each column will allow different
                //  renderers to be used based on Class
                public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
                }
            };

            table.setPreferredScrollableViewportSize(new Dimension(500, 70));
            table.setFillsViewportHeight(true);
            table.getTableHeader().setReorderingAllowed(false);
            dbc.disconnect();
            dbc.setConnectStatus(DISCONNECTED);
            ButtonColumn buttonColumn = new ButtonColumn(table, columnCount);
            return table;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    private class CardsHandler implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            CardLayout c1 = (CardLayout) (cards.getLayout());
            c1.show(cards, (String) e.getItem());
        }
        
    }

    private class CancelHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (dbc.getConnectStatus() == CONNECTED){
                dbc.disconnect();
                dbc.setConnectStatus(DISCONNECTED);
            }
            dispose();
        }
    }
    
    private class CheckoutHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!customerName.equals("Guest")){
                try{
                    DBConnection dbc = new DBConnection();
                    Connection conn = dbc.connect();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("select max(orderId) as orderId from Ordered");
                    ResultSet acc;
                    rs.next();
                    int newOrder = rs.getInt("orderId") + 1;
                    DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
                    String dlvDate; 
                    if (dateDeli.getSelectedItem().equals("Order Now")){
                        dlvDate = "NOW()";
                    }
                    else{
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        if (!ftCombo.getSelectedItem().equals("Today")) {
                            cal.add(Calendar.DATE, 1);
                        }
                        dlvDate = dateFormat.format(cal.getTime()) + " "
                                + timeCombo.getSelectedItem();
                        System.out.println(dlvDate);
                    }
                    //before insert db, needc to do payment check..
                    String accSql = "select cid, aid, balance, payment"
                            + " from Account inner join Customers"
                            + " on Account.aid = Customers.cid "
                            + "where Customers.cid=" + cid;
                    if (typeOfPayment.getSelectedItem().toString().equals("Credit")){
                        accSql += " and Account.payment = 'crd'";
                    }
                    else if (typeOfPayment.getSelectedItem().toString().equals("Check")){
                        accSql += " and Account.payment = 'chk'";
                    }
                    System.out.println(accSql);
                    rs = stmt.executeQuery(accSql);
                    if (rs.next()){
                        int aid = rs.getInt("aid");
                        double balance = rs.getDouble("balance");
                        balance -= currentPrice;
                        if (balance < 0){
                            throw new Exception("Balance not enough");
                        }
                        stmt.executeUpdate("Update Account set balance=" + balance
                                            + " where aid=" + aid);
                    }
                    

                    //try to place into order
                    int rowCount = orderTable.getRowCount();
                    String sql = "Insert Into Ordered(orderId, cid, pid, addrId,"
                            + "amount, standing_status, price) values(";
                    for (int row = 0; row < rowCount; row++){
                        int pid = Integer.parseInt(orderTable.getModel().getValueAt(row, 0).toString());
                        int amount = Integer.parseInt(orderTable.getModel().getValueAt(row, 2).toString());
                        double price = Double.parseDouble(orderTable.getModel().getValueAt(row, 3).toString());
                        sql += newOrder + ", " + cid + ", " + pid + ", " 
                                + addrId + ", " + amount;
                        if (standingOrder.getSelectedItem().equals("Regular")){
                            sql += ", 'n', ";
                        }
                        sql += price + ")";
                        //get into db;
                        stmt.executeUpdate(sql);
                        sql = "Insert Into Ordered(orderId, cid, pid, addrId,"
                                + "amount, standing_status, price) values(";
                    }
                    if (dlvDate.equals("NOW()")){
                        stmt.executeUpdate("Update Ordered set DlvTime=NOW()"
                                + "where orderId=" + newOrder);
                    }
                    else{
                        String update = "Update Ordered set DlvTime='" + dlvDate
                            + "' where orderId=" + newOrder;
                        stmt.executeUpdate(update);
                    }
                    int x = addrId / 10;
                    int y = addrId % 10;
                    int distance = Math.abs(2 - x) + Math.abs(y - 7);
                    System.out.println(distance);
                    //update dispatch time and generate receipt
                    stmt.executeUpdate("Update Ordered set disptime=NOW()"
                            + " where orderId=" + newOrder);
                    stmt.executeUpdate("Update Ordered set arritime="
                            + "timestampadd(MINUTE," + distance 
                            + ", disptime) where orderId="
                            + newOrder);
                    stmt.executeUpdate("Update Ordered set retntime="
                            + "timestampadd(MINUTE," + distance
                            + ", arritime) where orderId="
                            + newOrder);
                    JOptionPane.showMessageDialog(null, "Your Ordered is Place", 
                            "Confirm Order", JOptionPane.INFORMATION_MESSAGE);
                    ReceiptPane myPanel = new ReceiptPane();
                    JScrollPane pane = new JScrollPane(myPanel.getJTextArea());
                    JPanel panel = new JPanel();
                    panel.add(pane);
                    JOptionPane.showMessageDialog(null,panel, "Receipt", JOptionPane.INFORMATION_MESSAGE);
                    dbc.disconnect();
                    dispose();
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Please log in to checkout"
                        + " and re-order", "Order", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    protected class ReceiptPane extends JPanel{
        private JTextArea textArea;
        
        public ReceiptPane() {
            setPreferredSize(new Dimension(600, 400));
            setLayout(null);
            textArea = new JTextArea();
            textArea.setEditable(false);
            add(new JScrollPane(textArea));
            generateReceipt();
        }
        
        protected void generateReceipt(){
            int rowCount = orderTable.getRowCount();
            List<List<String>> lstRows = new ArrayList<List<String>>(rowCount);
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
            for (int row = 0; row < rowCount; row++){
                int qty = Integer.parseInt(orderTable.getModel().getValueAt(row, 2).toString());
                String name = orderTable.getModel().getValueAt(row, 1).toString();
                double price = Double.parseDouble(orderTable.getModel().getValueAt(row, 3).toString()) * qty;
                String total = nf.format(price);
                lstRows.add(createRow(Integer.toString(qty), name, total));
            }
            int maxWidth = 60;
            int qtyWidth = 4;
            int totalWidth = 8;
            int descWidth = maxWidth - qtyWidth - totalWidth;
            
            StringBuilder sbHeader = new StringBuilder(maxWidth);
            sbHeader.append("Qty ");
            sbHeader.append(fillWith("Item", " ", descWidth));
            sbHeader.append(padWith(" Total", " ", totalWidth));
            textArea.append(sbHeader.toString() + "\n");
            for (List<String> row : lstRows) {
                StringBuilder sb = new StringBuilder(maxWidth);

                sb.append(padWith(row.get(0), " ", 3));
                sb.append(" ");
                String desc = row.get(1);
                if (desc.length() > descWidth) {

                    desc = desc.substring(0, descWidth);

                }
                sb.append(fillWith(desc, ".", descWidth));
                sb.append(padWith(row.get(2), " ", totalWidth));

                textArea.append(sb.toString() + "\n");
            }
            
            textArea.append(padWith("Sub-total " + padWith(nf.format(sub), " ", totalWidth), " ", maxWidth) + "\n");
            textArea.append(padWith("Tax " + padWith(nf.format(t), " ", totalWidth), " ", maxWidth) + "\n");
            textArea.append(padWith("Total " + padWith(nf.format(currentPrice), " ", totalWidth), " ", maxWidth) + "\n");
        }
        
        protected List<String> createRow(String... columns) {
            return Arrays.asList(columns);
        }
        public String fillWith(String sValue, int width) {
            StringBuilder sb = new StringBuilder(width);
            for (int index = 0; index < width; index++) {
                sb.append(sValue);
            }
            return sb.toString();

        }

        public String fillWith(String sValue, String fill, int width) {
            return sValue + fillWith(fill, width - sValue.length());
        }

        public String pad(int iValue, int iMinLength) {
            return pad(Integer.toString(iValue), iMinLength);
        }

        public String pad(String sValue, int iMinLength) {
            return padWith(sValue, "0", iMinLength);
        }

        public String padWith(String value, String padding, int iMinLength) {
            StringBuilder sb = new StringBuilder(iMinLength);
            sb.append(value);

            while (sb.length() < iMinLength) {
                sb.insert(0, padding);
            }

            return sb.toString();
        }
        
        public JTextArea getJTextArea(){
            return this.textArea;
        }
    }
    class ButtonColumn extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor, ActionListener {

        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;

        public ButtonColumn(JTable table, int column) {
            super();
            this.table = table;
            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
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

        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
            Object priObj, taxObj, onSale;
            int qty = 1;

            if (e.getActionCommand().equals("Order")) {
                DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
                double unitPrice;
                if (((String) switcher.getSelectedItem()).equals("Toppings")) {
                    priObj = table.getModel().getValueAt(table.getSelectedRow(), 2);
                    taxObj = table.getModel().getValueAt(table.getSelectedRow(), 3);
                    onSale = table.getModel().getValueAt(table.getSelectedRow(), 6);

                    
                } else {
                    priObj = table.getModel().getValueAt(table.getSelectedRow(), 3);
                    taxObj = table.getModel().getValueAt(table.getSelectedRow(), 4);
                    onSale = table.getModel().getValueAt(table.getSelectedRow(), 7);
                }
                unitPrice = (Integer.parseInt(onSale.toString()) == 0 ? 
                        Double.parseDouble(priObj.toString())
                        : Double.parseDouble(priObj.toString()) - Double.parseDouble(priObj.toString()) * 0.1);
                Object qtystr = JOptionPane.showInputDialog(getContentPane(), "Quantity:");
                if (qtystr != null){
                    qty = Integer.parseInt(qtystr.toString());
                    model.addRow(new Object[]{table.getModel().getValueAt(table.getSelectedRow(), 0),
                        table.getModel().getValueAt(table.getSelectedRow(), 1),
                        qty, unitPrice, taxObj, "Cancel"});
                    sub += unitPrice * qty;
                    t += Double.parseDouble(taxObj.toString());
                    currentPrice = sub + t;
                }
            }
            if (e.getActionCommand().equals("Cancel")) {
                DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
                qty = (Integer) orderTable.getModel().getValueAt(table.getSelectedRow(), 2);
                priObj = orderTable.getModel().getValueAt(table.getSelectedRow(), 3);
                taxObj = orderTable.getModel().getValueAt(table.getSelectedRow(), 4);
                sub -= (Double.parseDouble(priObj.toString()) * qty);
                t -= (Double.parseDouble(taxObj.toString()));
                model.removeRow(orderTable.getSelectedRow());
            }
            subTotal.setText("Sub Total: " + Double.toString(sub));
            tax.setText("Tax: " + Double.toString(t));
            currentPrice = sub + t;
            currentPriceLabel.setText("Current Price: " + Double.toString(currentPrice));
            
        }
    }

}
