package com.carhub.ui.components;

import com.carhub.util.CurrencyUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MonthlyRevenueChart extends JPanel {
    
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;

    public MonthlyRevenueChart() {
        setLayout(new BorderLayout());
        setBackground(new Color(42, 45, 53));
        createChart();
    }

    private void createChart() {
        dataset = new DefaultCategoryDataset();
        
        chart = ChartFactory.createBarChart(
            "Monthly Revenue - Last 6 Months",
            "Month",
            "Revenue",
            dataset,
            PlotOrientation.VERTICAL,
            false, // No legend
            true,  // Tooltips
            false  // URLs
        );

        // Customize chart appearance
        chart.setBackgroundPaint(new Color(42, 45, 53));
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("SF Pro Display", Font.BOLD, 16));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(42, 45, 53));
        plot.setDomainGridlinePaint(new Color(60, 63, 71));
        plot.setRangeGridlinePaint(new Color(60, 63, 71));
        plot.setOutlinePaint(new Color(60, 63, 71));

        // Customize axes
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelFont(new Font("SF Pro Text", Font.PLAIN, 12));
        
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelFont(new Font("SF Pro Text", Font.PLAIN, 12));

        // Customize bars
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(34, 197, 94)); // Green color for revenue bars
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.15);

        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(new Color(42, 45, 53));
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBorder(null);
        
        add(chartPanel, BorderLayout.CENTER);
    }

    public void updateData(List<Object[]> monthlyRevenueData) {
        dataset.clear();
        
        // Create a map to ensure we have data for the last 6 months
        Map<String, BigDecimal> monthlyData = new LinkedHashMap<>();
        
        // Initialize last 6 months with zero values
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthDate = current.minusMonths(i);
            String monthKey = monthDate.format(monthFormatter);
            monthlyData.put(monthKey, BigDecimal.ZERO);
        }

        // Fill in actual data
        for (Object[] data : monthlyRevenueData) {
            Integer year = ((Number) data[0]).intValue();
            Integer month = ((Number) data[1]).intValue();
            BigDecimal revenue = (BigDecimal) data[2];
            
            LocalDateTime monthDate = LocalDateTime.of(year, month, 1, 0, 0);
            String monthKey = monthDate.format(monthFormatter);
            
            if (monthlyData.containsKey(monthKey)) {
                monthlyData.put(monthKey, revenue);
            }
        }

        // Add data to dataset
        for (Map.Entry<String, BigDecimal> entry : monthlyData.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
        }

        // Update chart title with total revenue
        BigDecimal totalRevenue = monthlyData.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String title = String.format("Monthly Revenue - Last 6 Months (Total: %s)", 
            CurrencyUtils.formatCurrency(totalRevenue));
        chart.setTitle(title);
    }
}
