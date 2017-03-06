package anaptyksi;

class BSMessage{
    public String type;
    public String IMEI;
    public String IMSI;
    public int x;
    public int y;
    
    public BSMessage(String str){   //parsing the string
        int i = 0;
        type = "";
        while (str.charAt(i) != '#'){
            type += str.charAt(i);
            i++;
        }
        
        IMEI = "";i++;
        while ((str.charAt(i) != '#') && (str.charAt(i) != '%')){
            IMEI += str.charAt(i);
            i++;
        }
        
        if (type.equals("CONNECT")){
            IMSI = "";i++;
            while (str.charAt(i) != '#'){
                IMSI += str.charAt(i);
                i++;   
            }

            String sx = "";i++;
            while (str.charAt(i) != '#'){
                sx += str.charAt(i);
                i++;   
            }
            x = Integer.parseInt(sx);

            String sy = "";i++;
            while (str.charAt(i) != '%'){
                sy += str.charAt(i);
                i++;   
            }
            y = Integer.parseInt(sy);
       }            
        
    }
    
    public String toString(){   //unparsing the String
        String result = type+"#"+IMEI;
        if (type.equals("CONNECT")){
            result += "#"+IMSI+"#"+x+"#"+y+"%";
        }
        else{
            result +="%";
        }
        return result;
    }
    
    public static BSMessage fromString(String s){   //dhmiourgei antikeimeno mesw tou constructor BSMessage o opoios to kanei parsing
	BSMessage msg = new BSMessage(s);
	return msg;
    }
      
}

