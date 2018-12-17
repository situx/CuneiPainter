package com.github.situx.cunei;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by timo on 14.12.18 .
 */
public class LocationCalc {


    public static Boolean crosses(LineParameters one, LineParameters two){
        return true;
    }

    public static String smallNormalOrBigStroke(StrokeType type,float delta1,float delta2){
        if(delta1>=(2*delta2)){
            return type.toString().toUpperCase();
        }else if(delta1<=delta2/2){
            return "s"+type.toString();
        }
        return type.toString();
    }

    public static String calculateRelation(List<LineParameters> lines,LineParameters newline){
        //find smallest y
        LineParameters smallyparam;
        for(LineParameters line:lines){
            StringBuilder returnString=new StringBuilder();
            switch(line.type){
                case A:
                    if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && newline.startY!=line.startY) {
                        //Parallel horizontal
                        returnString.append(":");
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                    }else if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && crosses(newline,line)){
                        returnString.append("-");
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                    }else if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && !crosses(newline,line)){
                        returnString.append("_");
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                    }
                    lines.add(newline);
                    return returnString.toString();
                case B:
                        if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && newline.startX!=line.startX) {
                            //Parallel horizontal
                            lines.add(newline);
                            returnString.append(":");
                            returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaY,line.deltaY));
                        }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                            returnString.append("-");
                            returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaY,line.deltaY));
                        }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){
                            returnString.append("_");
                            returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaY,line.deltaY));
                        }
                        return returnString.toString();
                case C:
                    if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                        if(newline.deltaY==0){
                            return "'";
                        }else{
                            return "-";
                        }
                    }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){

                    }
                    break;
            }


        }

        return "";
    }

    public static void main (String[] args){
        List<LineParameters> lines=new LinkedList<>();
        lines.add(new LineParameters((float)0.,(float)0.,(float)5.,(float)0.,(float)5.,(float)0.,StrokeType.A));
        String res=LocationCalc.calculateRelation(lines,new LineParameters((float)5.,(float)1.,(float)0.,(float)1.,(float)5.,(float)0.,StrokeType.INV_A));
        System.out.println(lines);
        System.out.println(lines.get(0).type+res);
        lines=new LinkedList<>();
        lines.add(new LineParameters((float)0.,(float)0.,(float)0.,(float)5.,(float)0.,(float)5.,StrokeType.B));
        res=LocationCalc.calculateRelation(lines,new LineParameters((float)1.,(float)1.,(float)1.,(float)0.,(float)0.,(float)1.,StrokeType.B));
        System.out.println(lines);
        System.out.println(lines.get(0).type+res);
    }
}
