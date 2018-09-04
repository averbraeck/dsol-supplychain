package nl.tudelft.simulation.supplychain.content.database;

import java.io.Serializable;

/**
 * String functions.<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class StringUtility implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Private constructor - static methods.
     */
    private StringUtility()
    {
        super();
    }

    /**
     * Adds slashes to text
     * @param text text
     * @return modified text
     */
    public static String addSlashes(final String text)
    {
        String result = text.replace("\'", "\\\'");
        return result;
    }

    /**
     * Replaces html characters in a text
     * @param text a text
     * @return a text with replaced characters
     */
    public static String replaceHTMLChars(final String text)
    {
        String result = "";
        result = text.replaceAll("&", "&amp;");
        result = result.replaceAll("<", "&lt;");
        result = result.replaceAll(">", "&gt;");

        return result;
    }

    /**
     * Replaces newline characters by a br-tag
     * @param text a text
     * @return concerted text
     */
    public static String newLineToBR(final String text)
    {
        char nl = '\n';
        String newLine = "" + nl;
        return text.replaceAll(newLine, "<br>");
    }

    /**
     * Do markup code
     * @param text a text
     * @return marked-up text
     */
    public static String doMarkup(final String text)
    {
        String result = text;
        // [b]
        result = result.replaceAll("\\x5bb\\x5d", "<b>");
        // [/b]
        result = result.replaceAll("\\x5b/b\\x5d", "</b>");
        // [i]
        result = result.replaceAll("\\x5bi\\x5d", "<i>");
        // [/i]
        result = result.replaceAll("\\x5b/i\\x5d", "</i>");
        // [url="URL"]LINK[/url]
        result = result.replaceAll("\\x5burl=", "<a href=");
        // => <a href="URL"]LINK[/url]
        // TODO this would replace all occurances of "] with "> which is not
        // correct!
        result = result.replaceAll("\"\\x5d", "\">");
        // => <a href="URL">LINK[/url]
        result = result.replaceAll("\\x5b/url\\x5d", "</a>");
        return result;
    }

    /**
     * @param text the text to format
     * @return the text with excaped special characters
     */
    public static String formatDatabase(final String text)
    {
        String result = text;
        if (result == null)
        {
            result = "";
        }
        result = result.replace("\"", "\\\"");
        result = result.replace("'", "\\'");
        result = result.replace("`", "\\`");
        result = result.replace("(", "\\(");
        result = result.replace(")", "\\)");
        result = result.replace("<", "\\<");
        result = result.replace(">", "\\>");
        result = result.replace("{", "\\{");
        result = result.replace("}", "\\}");
        result = result.replace("|", "\\|");
        result = result.replace("$", "\\$");
        result = result.replace("?", "\\?");
        result = result.replace(",", "\\,");
        return result;
    }

    /**
     * @param text the text to format
     * @return the text with excaped special characters
     */
    public static String formatScreen(final String text)
    {
        String result = text;
        if (result == null)
        {
            result = "";
        }
        result = result.replace("\\\"", "\"");
        result = result.replace("\\'", "'");
        result = result.replace("\\`", "`");
        result = result.replace("\\(", "(");
        result = result.replace("\\)", ")");
        result = result.replace("\\<", "<");
        result = result.replace("\\>", ">");
        result = result.replace("\\{", "{");
        result = result.replace("\\}", "}");
        result = result.replace("\\|", "|");
        result = result.replace("\\,", ",");
        result = result.replace("\\?", "?");
        return result;
    }

    /**
     * @param text the text to format
     * @return the text with excaped special characters
     */
    public static String formatHTML(final String text)
    {
        String result = StringUtility.formatScreen(text);
        result = result.replace("\n", "<BR>");
        return result;
    }

    /**
     * round a number to a string with a certain number of digits
     * @param number the double input
     * @param digits the number of digits
     * @return the formatted string
     */
    public static String round(final double number, final int digits)
    {
        if (digits == 0)
        {
            return "" + (long) number;
        }
        String formatted = "" + number;
        if (formatted.contains("."))
        {
            formatted += "0000000000";
        }
        else
        {
            formatted += ".0000000000";
        }
        formatted = formatted.substring(0, formatted.indexOf(".") + digits + 1);
        return formatted;
    }
}
