
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Inline {
    private HashMap<String, String> variables = new HashMap<>();
    private ArrayList<String> list=new ArrayList<>();
    private ArrayList<String> busyFiles=new ArrayList<>();
    private Sintaksa syntax = new Sintaksa();
    private Parser expPars = new Parser();

    private Boolean IsVarDeclared(String var)
    {
        return variables.containsKey(var);
    }
    private String VarReturn(String input)
    {
        String oldStr, newStr,varName;
        Pattern p=Pattern.compile("\\[([A-z]+[0-9]*[_.$#]*?)]");
        Matcher varRex=p.matcher(input);
        try
        {
            while (varRex.find())
            {
                oldStr = varRex.group(0);
                varName = varRex.group(1);
                newStr = variables.get(varName);
                if (IsVarDeclared(varName))
                    input = input.replace(oldStr, newStr);
                else
                {
                    System.out.println("Variable " + varName + " not declared"); return "";
                }
            }

        }catch(Exception e)
        { System.out.println("Error"); System.out.println(e.getMessage()); }
        return input;
    }
    private String VarOutput(String input)
    {
        String oldStr,newStr,varName;
        Pattern p=Pattern.compile("=\\[(.+?)]");
        Matcher varRex=p.matcher(input);

        try
        {
            while (varRex.find())
            {

                oldStr = varRex.group(0);
                varName = varRex.group(1);
                newStr=variables.get(varName);


                if (IsVarDeclared(varName))
                    input = input.replace(oldStr, newStr);
                else
                { System.out.println("Variable " + varName + " not declared"); return ""; }

            }
        }
        catch (Exception e) { System.out.println("Error"); System.out.println(e.getMessage()); }
        input = input.replace("\"", "");
        return input;
    }
    private void DeclareVar(String input)
    {
Pattern p=Pattern.compile("^\\[(.*)] *= *([^}]*)");
Matcher decRex=p.matcher(input);
if(!decRex.find())return;
        String varName = decRex.group(1);//0
        String restLine = decRex.group(2);//1
        String temp = restLine;
        restLine = VarReturn(restLine);
        if (restLine.isEmpty()) return ;

        if (!IsVarDeclared(varName))  variables.put(varName, expPars.parseString(restLine));

        else
        {
            String t = expPars.parseString(restLine);
            if (t.contains("\"") == variables.get(varName).contains("\""))   variables.put(varName,t);

            else System.out.println("<< Error variable " + varName + " declared with different type >>");

        }
        input = input.replace(temp, "");
        input=input.split("=")[0];
    }
    private String InlineBlock(String input)
    {Pattern p=Pattern.compile("@\\{(.*)} ?");
    Matcher inRex=p.matcher(input);

        String oldStr,newStr;
        while(inRex.find())
        {
            oldStr = inRex.group(0);
            newStr = inRex.group(1);
            input = input.replace(oldStr, "");
            DeclareVar(newStr);
        }
        return input;
    }
   private void IncludeFile(String input)
    {
     Pattern p=Pattern.compile("include (.+\\.txt)");
     Matcher incRex=p.matcher(input);

        String oldStr, newStr;
        while(incRex.find())
        {
            oldStr = incRex.group(0);
            newStr = incRex.group(1);
            try
            {

                if (!Files.exists(Paths.get(newStr), LinkOption.NOFOLLOW_LINKS))
                    System.out.println("The file " + newStr + " doesn't exist");
                Run(newStr);
                input = input.replace(oldStr, "");
            }
            catch (Exception e)
            {
                System.out.println("The file "+newStr+" could not be read");
                System.out.println(e.getMessage());

            }

        }
    }
   private String Tags(String input)
    { Pattern p=Pattern.compile("<all_caps>+(.*)</all_caps>+");
    Matcher tagRex=p.matcher(input);

        String oldStr, newStr;
        while(tagRex.find())
        {
            oldStr = tagRex.group(0);
            newStr = tagRex.group(1);
            input = input.replace(oldStr, newStr.toUpperCase());
        }
        return input;
    }
    public void Output()
    {int max=0;
        for(String x : list) {
            System.out.println(x);
            if (x.length() > max)
         max=x.length();}
        for(int i=0;i<max;i++)
        System.out.print("=");
    }

    public Boolean Run(String putanja )throws Exception
    { if(busyFiles.contains(putanja)) {System.out.println("File "+putanja+" is already opened"); return false;}
    busyFiles.add(putanja);

        Scanner sr = new Scanner(new File(putanja));

            while (sr.hasNextLine())
            {
                String line = sr.nextLine();//works
                if (!syntax.IsValidLine(line))//works
                {busyFiles.remove(putanja);
                return false;
                }

                if (syntax.IsValidPath(line))//works
                    IncludeFile(line);
                if(syntax.IsDeclaration(line))
                    DeclareVar(line);
                if (!syntax.IsDeclaration(line) && !syntax.IsValidPath(line))
                {
                    String x = Tags(VarOutput(InlineBlock(line)));//works

                    if (syntax.IsValidOutputCharacters(x))
                        list.add(x);
                    else
                        list.add("<<< Invalid character used >>>");

                }
            }
        busyFiles.remove(putanja);
        return true;
    }

}
