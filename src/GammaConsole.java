/**
 * Gamma Console: An embeddable Java Swing logging console that supports HTML formatting.
 *
 * @author UnexomWid
 */
package me.unexomwid.gammaconsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class GammaConsole extends JFrame implements Runnable {

    private int logCount;
    /**
     * Gets the amount of printed logs.
     */
    public int getLogCount() {
        return this.logCount;
    }

    /**
     * Whether to print the full timestamp for each log, or just the hour, minute and second.
     */
    public boolean fullTimestamp;

    private int textSpacing;
    /**
     * Gets the amount of spaces to write between the timestamp and the message.
     */
    public int getTextSpacing() {
        return this.textSpacing;
    }
    /**
     * Sets the amount of spaces to write between the timestamp and the message.
     */
    public void setTextSpacing(int textSpacing) {
        this.textSpacing = textSpacing;
        this.halfTextSpacing = this.textSpacing / 2;
    }

    private int halfTextSpacing;

    private Color backgroundColor;
    /**
     * Gets the background color of the console.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    /**
     * Sets the background color of the console.
     *
     * @param backgroundColor The background color of the console.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private Color textColor;
    /**
     * Gets the console text color.
     */
    public Color getTextColor() {
        return this.textColor;
    }
    /**
     * Sets the console text color.
     *
     * @param textColor The console text color.
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    private Color borderColor;
    /**
     * Gets the color of the border between the lines.
     */
    public Color getBorderColor() {
        return this.borderColor;
    }
    /**
     * Sets the color of the border between the lines.
     *
     * @param borderColor The color of the border between the lines.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    private Color debugColor;
    /**
     * Gets the color for Debug messages.
     */
    public Color getDebugColor() {
        return debugColor;
    }
    /**
     * Sets the color for Debug messages.
     *
     * @param color The color for Debug messages.
     */
    public void setDebugColor(Color color) {
        this.debugColor = color;
    }

    private Color infoColor;
    /**
     * Gets the color for Info messages.
     */
    public Color getInfoColor() {
        return infoColor;
    }
    /**
     * Sets the color for Info messages.
     *
     * @param color The color for Info messages.
     */
    public void setInfoColor(Color color) {
        this.infoColor = color;
    }

    private Color warningColor;
    /**
     * Gets the color for Warning messages.
     */
    public Color getWarningColor() {
        return warningColor;
    }
    /**
     * Sets the color for Warning messages.
     *
     * @param color The color for Warning messages.
     */
    public void setWarningColor(Color color) {
        this.warningColor = color;
    }

    private Color errorColor;
    /**
     * Gets the color for Error messages.
     */
    public Color getErrorColor() {
        return errorColor;
    }
    /**
     * Sets the color for Error messages.
     *
     * @param color The color for Error messages.
     */
    public void setErrorColor(Color color) {
        this.errorColor = color;
    }

    private JTextPane textPane;
    private JScrollPane scrollPane;
    private JScrollBar verticalScrollBar;
    private JButton saveButton;
    private JButton clearButton;

    private StringBuilder contentString;
    private boolean ready;

    public GammaConsole() {
        initializeWindow();
        initializeFields();
        ready = false;
    }

    @Override
    public void run()
    {
        EventQueue.invokeLater(() -> {
            try {
                postInitialize();
                this.setVisible(true);
                ready = true;
            }
            catch (Exception ex) {

            }
        });
    }

    private void initializeFields() {
        this.logCount = 0;
        this.fullTimestamp = false;
        this.setTextSpacing(30);

        this.setBorderColor(new Color(172, 172, 172));
        this.setBackgroundColor(new Color(255, 255, 255));
        this.setTextColor(new Color(0, 0, 0));
        this.setDebugColor(new Color(225, 225, 225));
        this.setInfoColor(new Color(215, 255, 215));
        this.setWarningColor(new Color(255, 255, 215));
        this.setErrorColor(new Color(255, 215, 215));

        this.setTitle("Gamma Console");
        this.setIconImage(new ImageIcon(Base64.getDecoder().decode("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")).getImage());
    }

    private void initializeWindow() {
        this.setBounds(100, 100, 768, 768);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.textPane = new JTextPane();
        this.textPane.setForeground(this.getTextColor());
        this.textPane.setBackground(this.getBackgroundColor());
        this.textPane.setEditable(false);
        this.textPane.setContentType("text/html");
        this.textPane.setFont(new Font("monospaced", Font.PLAIN, 12));

        this.scrollPane = new JScrollPane(this.textPane);
        this.verticalScrollBar = scrollPane.getVerticalScrollBar();

        this.saveButton = new JButton("Save");
        this.saveButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.saveButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.saveButton.setToolTipText("Save the log in HTML format.");
        this.saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        this.clearButton = new JButton("Clear");
        this.clearButton.setVerticalTextPosition(AbstractButton.CENTER);
        this.clearButton.setHorizontalTextPosition(AbstractButton.CENTER);
        this.clearButton.setToolTipText("Clear the console.");
        this.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });

        SpringLayout springLayout = new SpringLayout();

        springLayout.putConstraint(SpringLayout.WEST, clearButton, 5, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, clearButton, 5, SpringLayout.NORTH, this.getContentPane());

        springLayout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.EAST, clearButton);
        springLayout.putConstraint(SpringLayout.NORTH, saveButton, 5, SpringLayout.NORTH, this.getContentPane());

        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, clearButton);
        springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, this.getContentPane());

        this.getContentPane().setLayout(springLayout);
        this.getContentPane().add(this.scrollPane);
        this.getContentPane().add(this.saveButton);
        this.getContentPane().add(this.clearButton);
    }

    private void postInitialize() {
        this.textPane.setSelectionColor(new Color(Math.abs(this.backgroundColor.getRed() - 255), Math.abs(this.backgroundColor.getGreen() - 255), Math.abs(this.backgroundColor.getBlue() - 255), 127));
        this.textPane.setBackground(backgroundColor);
        this.textPane.setForeground(textColor);
        this.setBackground(backgroundColor);

        this.contentString = new StringBuilder("<html><head><style>div { white-space: nowrap; font-family: " + textPane.getFont().getFamily() + "; width: 100%; border-top: 1px solid " + String.format("#%02x%02x%02x", this.borderColor.getRed(), this.borderColor.getGreen(), this.borderColor.getBlue()) + "; padding-top: 5px; padding-bottom: 5px; margin-top: 0px; } .debug { background-color: " + String.format("#%02x%02x%02x", this.debugColor.getRed(), this.debugColor.getGreen(), this.debugColor.getBlue()) + "; } .info { background-color: " + String.format("#%02x%02x%02x", this.infoColor.getRed(), this.infoColor.getGreen(), this.infoColor.getBlue()) + "; } .warning { background-color: " + String.format("#%02x%02x%02x", this.warningColor.getRed(), this.warningColor.getGreen(), this.warningColor.getBlue()) + "; } .error { background-color: " + String.format("#%02x%02x%02x", this.errorColor.getRed(), this.errorColor.getGreen(), this.errorColor.getBlue()) + "; }</style></head>");
        this.textPane.setText(contentString.toString());
    }

    /**
     * Prints verbose text.
     *
     * @param text The text to print.
     */
    public void printVerbose(String text) {
        printVerbose(text, "VERBOSE");
    }

    /**
     * Prints debug text.
     *
     * @param text The text to print.
     */
    public void printDebug(String text) {
        printDebug(text, "DEBUG");
    }

    /**
     * Prints info text.
     *
     * @param text The text to print.
     */
    public void printInfo(String text) {
        printInfo(text, "INFO");
    }

    /**
     * Prints warning text.
     *
     * @param text The text to print.
     */
    public void printWarning(String text) {
        printWarning(text, "WARNING");
    }

    /**
     * Prints error text.
     *
     * @param text The text to print.
     */
    public void printError(String text) {
        printError(text, "ERROR");
    }

    /**
     * Prints verbose text.
     *
     * @param text The text to print.
     * @param caller The caller of the method.
     */
    public void printVerbose(String text, String caller) {
        try {
            while(!ready)
                TimeUnit.NANOSECONDS.sleep(10);

            contentString.append("<div class=\"verbose\">");
            contentString.append(this.getTimestamp());

            int halfLength = caller.length() / 2;
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            contentString.append(caller);
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            if(caller.length() % 2 == 0)
                contentString.append("&nbsp;");

            contentString.append(text);
            contentString.append("</div>");

            boolean doFullScroll = this.verticalScrollBar.getValue() == this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount();
            int scrollValue = this.verticalScrollBar.getValue();

            textPane.setText(contentString.toString());

            if(doFullScroll)
                scrollToBottom();
            else scrollTo(scrollValue);

            logCount++;
            collectGarbage();
        }
        catch(Exception ex) {

        }
    }

    /**
     * Prints debug text.
     *
     * @param text The text to print.
     * @param caller The caller of the method.
     */
    public void printDebug(String text, String caller) {
        try {
            while(!ready)
                TimeUnit.NANOSECONDS.sleep(10);

            contentString.append("<div class=\"debug\">");
            contentString.append(this.getTimestamp());

            int halfLength = caller.length() / 2;
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            contentString.append(caller);
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            if(caller.length() % 2 == 0)
                contentString.append("&nbsp;");

            contentString.append(text);
            contentString.append("</div>");

            boolean doFullScroll = this.verticalScrollBar.getValue() == this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount();
            int scrollValue = this.verticalScrollBar.getValue();

            textPane.setText(contentString.toString());

            if(doFullScroll)
                scrollToBottom();
            else scrollTo(scrollValue);

            logCount++;
            collectGarbage();
        }
        catch(Exception ex) {

        }
    }

    /**
     * Prints info text.
     *
     * @param text The text to print.
     * @param caller The caller of the method.
     */
    public void printInfo(String text, String caller) {
        try {
            while(!ready)
                TimeUnit.NANOSECONDS.sleep(10);

            contentString.append("<div class=\"info\">");
            contentString.append(this.getTimestamp());

            int halfLength = caller.length() / 2;
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            contentString.append(caller);
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            if(caller.length() % 2 == 0)
                contentString.append("&nbsp;");

            contentString.append(text);
            contentString.append("</div>");

            boolean doFullScroll = this.verticalScrollBar.getValue() == this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount();
            int scrollValue = this.verticalScrollBar.getValue();

            textPane.setText(contentString.toString());

            if(doFullScroll)
                scrollToBottom();
            else scrollTo(scrollValue);

            logCount++;
            collectGarbage();
        }
        catch(Exception ex) {

        }
    }

    /**
     * Prints warning text.
     *
     * @param text The text to print.
     * @param caller The caller of the method.
     */
    public void printWarning(String text, String caller) {
        try {
            while(!ready)
                TimeUnit.NANOSECONDS.sleep(10);

            contentString.append("<div class=\"warning\">");
            contentString.append(this.getTimestamp());

            int halfLength = caller.length() / 2;
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            contentString.append(caller);
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            if(caller.length() % 2 == 0)
                contentString.append("&nbsp;");

            contentString.append(text);
            contentString.append("</div>");

            boolean doFullScroll = this.verticalScrollBar.getValue() == this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount();
            int scrollValue = this.verticalScrollBar.getValue();

            textPane.setText(contentString.toString());

            if(doFullScroll)
                scrollToBottom();
            else scrollTo(scrollValue);

            logCount++;
            collectGarbage();
        }
        catch(Exception ex) {

        }
    }

    /**
     * Prints error text.
     *
     * @param text The text to print.
     * @param caller The caller of the method.
     */
    public void printError(String text, String caller) {
        try {
            while(!ready)
                TimeUnit.NANOSECONDS.sleep(10);

            contentString.append("<div class=\"error\">");
            contentString.append(this.getTimestamp());

            int halfLength = caller.length() / 2;
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            contentString.append(caller);
            for(int u = 0; u < this.halfTextSpacing - halfLength; u++)
                contentString.append("&nbsp;");
            if(caller.length() % 2 == 0)
                contentString.append("&nbsp;");

            contentString.append(text);
            contentString.append("</div>");

            boolean doFullScroll = this.verticalScrollBar.getValue() == this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount();
            int scrollValue = this.verticalScrollBar.getValue();

            textPane.setText(contentString.toString());

            if(doFullScroll)
                scrollToBottom();
            else scrollTo(scrollValue);

            logCount++;
            collectGarbage();
        }
        catch(Exception ex) {

        }
    }

    /**
     * Clears the console.
     */
    public void clear() {
        this.contentString = new StringBuilder("<html><head><style>div { white-space: nowrap; font-family: " + textPane.getFont().getFamily() + "; width: 100%; border-top: 1px solid " + String.format("#%02x%02x%02x", this.borderColor.getRed(), this.borderColor.getGreen(), this.borderColor.getBlue()) + "; padding-top: 5px; padding-bottom: 5px; margin-top: 0px; } .debug { background-color: " + String.format("#%02x%02x%02x", this.debugColor.getRed(), this.debugColor.getGreen(), this.debugColor.getBlue()) + "; } .info { background-color: " + String.format("#%02x%02x%02x", this.infoColor.getRed(), this.infoColor.getGreen(), this.infoColor.getBlue()) + "; } .warning { background-color: " + String.format("#%02x%02x%02x", this.warningColor.getRed(), this.warningColor.getGreen(), this.warningColor.getBlue()) + "; } .error { background-color: " + String.format("#%02x%02x%02x", this.errorColor.getRed(), this.errorColor.getGreen(), this.errorColor.getBlue()) + "; }</style></head>");
        this.textPane.setText(contentString.toString());
    }

    private void collectGarbage() {
        if(logCount >= 65536) {
            save();
            clear();
        }
    }

    /**
     * Saves the log in a file (HTML format).
     */
    public void save() {
        try {
            File log = new File("." + File.separator + new Timestamp(System.currentTimeMillis()).toString().replace(' ', '_').replace(':', '-') + "_log.html");
            log.createNewFile();

            FileOutputStream fos = new FileOutputStream(log);
            fos.write(this.textPane.getText().getBytes());
            fos.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getTimestamp() {
        if(fullTimestamp)
            return new Timestamp(System.currentTimeMillis()).toString();

        StringBuilder sb = new StringBuilder();
        LocalTime lt = LocalTime.now();
        sb.append(String.format("%02d", lt.getHour()));
        sb.append(":");
        sb.append(String.format("%02d", lt.getMinute()));
        sb.append(":");
        sb.append(String.format("%02d", lt.getSecond()));

        return sb.toString();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> { this.verticalScrollBar.setValue(this.verticalScrollBar.getMaximum() - this.verticalScrollBar.getVisibleAmount()); });
    }

    private void scrollTo(int n) {
        SwingUtilities.invokeLater(() -> { this.verticalScrollBar.setValue(n); });
    }
}
