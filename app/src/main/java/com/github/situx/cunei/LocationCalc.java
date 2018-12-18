package com.github.situx.cunei;

import android.graphics.Path;

import java.util.*;

/**
 * Created by timo on 14.12.18 .
 */
public class LocationCalc {

    public static Map<StrokeType,Set<Integer>> stroketypeLength=new TreeMap<>();

    public static Boolean crosses(LineParameters one, LineParameters two){
        Path path=new Path();
        path.moveTo(one.startX,one.startY);
        path.lineTo(one.endX,one.endY);
        Path path2=new Path();
        path2.moveTo(two.startX,two.startY);
        path2.lineTo(two.endX,two.endY);
        return path.op(path,path2,Path.Op.INTERSECT);
    }

    public static String smallNormalOrBigStroke(StrokeType type,float delta1,float delta2){
        if(stroketypeLength.get(type).size()==1){
            return type.toString();
        }
        if(delta1>=(2*delta2)){
            return type.toString().toUpperCase();
        }else if(delta1<=delta2/2){
            return "s"+type.toString();
        }
        return type.toString();
    }

    public static String calculateRelationBetweenTwoLines(LineParameters line,LineParameters newline){
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
                //lines.add(newline);
                return returnString.toString();
            case B:
                if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && newline.startX!=line.startX) {
                    //Parallel horizontal
                    //lines.add(newline);
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
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("'");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("-");
                    }
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){
                    if(newline.deltaY==0){
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("-");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("_");
                    }
                }
                break;
            case D:
                if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                    if(newline.deltaY==0){
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("'");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                        returnString.append("-");
                    }
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){

                }
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                break;
            case E:
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                break;
            case F:
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX,line.deltaX));
                break;
        }
        return returnString.toString();
    }

    public static String calculateRelation(List<LineParameters> lines){
        //find smallest y
        Collections.sort(lines);
        if(lines.isEmpty())
            return "";
        for(LineParameters line : lines){
            if(!stroketypeLength.containsKey(line.type)){
                stroketypeLength.put(line.type,new TreeSet<Integer>());
            }
            stroketypeLength.get(line.type).add(line.deltaX.intValue());
        }
        LineParameters line=lines.iterator().next();
        Iterator<LineParameters> linit=lines.iterator();
        LineParameters line1=linit.next();
        LineParameters line2=null;
        StringBuilder drawnSign=new StringBuilder();
        drawnSign.append(smallNormalOrBigStroke(line.type,line.deltaX,line.deltaY));
        System.out.println("Lines: "+lines);
        while(linit.hasNext()){
            if(linit.hasNext()){
                line2=linit.next();
            }
            drawnSign.append(calculateRelationBetweenTwoLines(line1,line2));
            System.out.println("DrawSign: "+drawnSign.toString());
            line1=line2;
        }

        return drawnSign.toString();
    }

    public static void main (String[] args){
        List<LineParameters> lines=new LinkedList<>();
        lines.add(new LineParameters((float)0.,(float)0.,(float)5.,(float)0.,(float)5.,(float)0.,StrokeType.A));
        String res=LocationCalc.calculateRelation(lines);
        System.out.println(lines);
        System.out.println(lines.get(0).type+res);
        lines=new LinkedList<>();
        lines.add(new LineParameters((float)0.,(float)0.,(float)0.,(float)5.,(float)0.,(float)5.,StrokeType.B));
        res=LocationCalc.calculateRelation(lines);
        System.out.println(lines);
        System.out.println(lines.get(0).type+res);
    }
}
