package anaptyksi;

public class BSResponse{
    public String type; //tupos msg
    public String IMEI;
    
    public BSResponse(String str){ //parsing tou msg
        type = "";
        int i=0;
        while (str.charAt(i) != '#'){
            type += str.charAt(i);
            i++;
        }

        IMEI = ""; i++;
        while (str.charAt(i) != '%'){
            IMEI += str.charAt(i);
            i++;
        }    
    }
    
    public String toString(){ //unparsing tou msg
        String result = type + "#" + IMEI + "%";
        return result;
    }
    
    public static BSResponse fromString(String str){ //dhmiourgia antikeimenou BSResponse
        return (new BSResponse(str));
    }
}
