package com.carhub.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {

    private static final Color HEADER_BACKGROUND = new Color(42, 45, 53);
    private static final Color ROW_BACKGROUND = new Color(47, 51, 73);
    private static final Color ALTERNATE_ROW_BACKGROUND = new Color(52, 56, 78);
    private static final Color SELECTION_BACKGROUND = new Color(222, 255, 41, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color HEADER_TEXT_COLOR = new Color(161, 161, 170);

    public ModernTable() {
        super(new DefaultTableModel());
        initializeTable();
    }

    public ModernTable(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        initializeTable();
    }

    public ModernTable(DefaultTableModel model) {
        super(model);
        initializeTable();
    }

    private void initializeTable() {
        setBackground(ROW_BACKGROUND);
        setForeground(TEXT_COLOR);
        setSelectionBackground(SELECTION_BACKGROUND);
        setSelectionForeground(TEXT_COLOR);
        setGridColor(new Color(55, 65, 81));
        setRowHeight(40);
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1, 1));

        // Header styling
        JTableHeader header = getTableHeader();
        header.setBackground(HEADER_BACKGROUND);
        header.setForeground(HEADER_TEXT_COLOR);
        header.setFont(new Font("SF Pro Text", Font.BOLD, 12));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Cell renderer
        setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        // Font
        setFont(new Font("SF Pro Text", Font.PLAIN, 14));
    }

    private class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(ROW_BACKGROUND);
                } else {
                    c.setBackground(ALTERNATE_ROW_BACKGROUND);
                }
            } else {
                c.setBackground(SELECTION_BACKGROUND);
            }

            c.setForeground(TEXT_COLOR);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            return c;
        }
    }
}
