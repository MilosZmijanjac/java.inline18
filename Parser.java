import java.lang.String;
import java.util.regex.*;

public class Parser {
   private  int pos = -1, ch;
    private String str;
    private Sintaksa a = new Sintaksa();
    private void nextChar()
    {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;

    }

    private boolean eat(int charToEat)
    {
        while (ch == ' ') nextChar();
        if (ch == charToEat)
        {
            nextChar();
            return true;
        }
        return false;
    }

    private double parse(String s) throws Exception {

        str = s;
        pos = -1; ch = 0;
        nextChar();

        double x = parseExpression();

        if (pos < str.length()) throw new Exception("Unexpected: " + (char)ch);
        return x;
    }
    private double parseExpression() throws Exception {
        double x = parseTerm();

        for (;;)
        {
            if (eat('+')) x += parseTerm(); // addition
            else if (eat('-')) x -= parseTerm(); // subtraction
            else return x;
        }
    }

    private double parseTerm() throws Exception {

        double x = parseFactor();

        for (;;)
        {
            if (eat('*')) x *= parseFactor(); // multiplication
            else if (eat('/')) x /= parseFactor();// division
            else if (eat('%')) x %= parseFactor();// mod
            else return x;
        }
    }
   private  double parseFactor() throws Exception {
        if (eat('+')) return parseFactor(); // unary plus
        if (eat('-')) return -parseFactor(); // unary minus

        double x;
        int startPos = this.pos;
        if (eat('('))
        { // parentheses
            x = parseExpression();
            eat(')');
        }
        else if ((ch >= '0' && ch <= '9') || ch == '.')
        { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();

            String pom = str.substring(startPos, this.pos);
            x = Double.parseDouble(pom);
        }

        else

            throw  new Exception();

        if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

        return x;
    }

    public String parseString(String input)
    {
      Pattern pat1= Pattern.compile("\".*?\"");
      Pattern pat2= Pattern.compile("\\((.*?)\\)");
      Matcher literal_str=pat1.matcher(input);
        Matcher zagrade=pat2.matcher(input);

        input = input.replace(" ", "");

        try
        {
            if (literal_str.find())
            {
                while (zagrade.find())
                {
                    String x = zagrade.group(0);

                    int indexX = zagrade.start()+ x.length();
                    int indexY = zagrade.start() + x.length() - 1;
                    String y = zagrade.group(1);


                    while (!a.IsBalanced("(", ")", x))
                    {
                        y += input.charAt(indexY++);
                        x += input.charAt(indexX++);
                    }

                    Matcher m=pat1.matcher(zagrade.group(1));
                    if (m.find())
                    {
                        input = input.replace(x, y);
                    }
                    else
                    {
                    double value =parse(input);
                    if((value % 1) == 0)
                        input = input.replace(x, Integer.toString((int)Math.round(value)));
                    else
                        input = input.replace(x, Double.toString(value));

                    }

                }

                input = input.replace("+","");
                input = input.replace("\"", "");
                input = "\""+input+"\"";
            }
            else
            { double value =parse(input);
                if((value%1)==0)
                    input=Integer.toString((int)Math.round(value));
                else
                input = Double.toString(value);
            }

        }
        catch(Exception e) { System.out.println(e.getMessage()+" casted to String by default"); }
        return input;
    }
   private Boolean IsBalanced(Character x, Character y, String k)
    {

        return a.count(k,x)==a.count(k,y);

    }
}
