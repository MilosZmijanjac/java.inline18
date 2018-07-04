import java.lang.String;
import java.util.Stack;
import  java.util.regex.*;

public class Sintaksa {
    char[] slova ={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q',
            'R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h',
            'i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
    char[] brojevi = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    char[] operatori = { '+', '-', '*', '/', '%' };
    char[] spec_karakteriEx = { '#', '_', '.', '$', '@', '{', '}', '[', ']', '(', ')', '=','<','>',' ','"',':','\\'};
    char[] spec_karakteri = { '#', '.', '_', '$',' ' };
    private boolean has(char[]set,char c)
    {
        for(char i :set)
            if(i==c)
                return true;
        return false;
    }
    public int count(String str,Character c)
    {
        int i=0;
        for(Character x : str.toCharArray())
        {
            if(c.equals(x))
                i++;
        }
        return i;
    }
    public boolean IsBalanced(String a, String b,String  input)
    {
        Stack<Character> stack = new Stack<>();
        String str = input;
        Character x = '~';
        Character y = '^';
        str = str.replace(a,x.toString());

        str = str.replace(b,y.toString());
        for (int i = 0; i < str.length(); i++)
        {
            if (x.equals(str.charAt(i)))
            {
                stack.push(str.charAt(i));
                continue;
            }
            if ( y.equals(str.charAt(i) ))
                if (stack.empty())
                    return false;
                else
                    stack.pop();

        }

        return stack.empty();
    }
    public  boolean IsValidCharacters(String input)
    {
        for (Character x : input.toCharArray())
        {
            if (!(has(operatori,x) || has(slova,x) || has(brojevi,x) || has(spec_karakteriEx,x)))
            {
                System.out.print(x + ":");
                return false;
            }
        }
        return true;
    }
    public boolean IsValidPath(String input)
    {   String pattern ="include *(.+\\.txt)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        return m.find();
    }
    public boolean IsValidOutputCharacters(String input)
    {

        for (Character x : input.toCharArray())
        {
            if (!(has(operatori,x) || has(slova,x) || has(brojevi,x)|| has(spec_karakteri,x)))
                return false;
        }
        return true;
    }
    public boolean IsValidString(String input)
    {
        return count(input,'"')%2==0;
    }
    public boolean isValidObjectName(String input)
    {
        if (!has(slova,input.toCharArray()[0]))
            return false;
        for (Character x : input.toCharArray())
        {
            if (!(has(slova,x) || has(brojevi,x) || has(spec_karakteri,x)))
                return false;
        }
        return true;
    }
    public boolean IsDeclaration(String input)
    {  Pattern p=Pattern.compile("^\\[(.*)\\] *= *(.*)$");
        Matcher m=p.matcher(input);
        return m.find() && isValidObjectName(m.group(1));
    }

    public boolean IsValidLine(String input)
    {
        if(!IsBalanced("(",")",input))
        {
            System.out.println("Parenthesis not closed");
            return false;
        }
        if (!IsBalanced("<all_caps>", "</all_caps>", input))
        {
            System.out.println("Tags not closed");
            return false;
        }
        if (!IsBalanced("{", "}", input))
        {
            System.out.println("Inline block not closed");
            return false;
        }
        if(!IsValidString(input))
        {
            System.out.println("String invalid");
            return false;
        }
        if (!IsValidCharacters(input))
        {
            System.out.println("Invalid character used");
            return false;
        }
        return true;

    }


}
