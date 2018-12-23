package com.github.situx.cunei;

import java.util.*;

/**
 * Created by timo on 14.12.18 .
 */
public class LocationCalc {

    public static Map<StrokeType,Set<Integer>> stroketypeLength=new TreeMap<>();

    public static Integer getAverageStrokeLength(StrokeType type){
        Integer sum=0;
        for(Integer len:stroketypeLength.get(type)) {
            sum+=len;
        }
        System.out.println("Sum: "+sum+" - "+sum/stroketypeLength.get(type).size());
        return sum/stroketypeLength.get(type).size();
    }


    public static Boolean crosses(LineParameters one, LineParameters two){
        return checkIntersection(one.startX.intValue(),one.startY.intValue(),one.endX.intValue(),one.endY.intValue(),two.startX.intValue(),two.startY.intValue(),two.endX.intValue(),two.endY.intValue());
    }

    public static Double distance(Float x1,Float x2,Float y1,Float y2){
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }

    public static Boolean startsDiagonallyToAndIntersects(LineParameters one,LineParameters two){
        return distance(one.startX,two.startX, one.startY,two.startY) + distance(one.endX,two.startX,one.endY, two.startY) == distance(one.startX,one.endX,one.startY,one.endY);
    }

    public static String smallNormalOrBigStroke(StrokeType type,float delta){
        if(delta<0)
            delta*=-1;
        if(stroketypeLength.get(type).size()==1){
            return type.toString();
        }
        int avglength=getAverageStrokeLength(type);
        System.out.println("Average length: "+avglength+" - Big: "+1.5*avglength+" Small: "+avglength/1.5+" Delta: "+delta);
        System.out.println(delta+">="+1.5*avglength+" Big? "+(delta>=(1.5*avglength)));
        System.out.println(delta+"<="+avglength/1.5+" Small? "+(delta<=avglength/2));
        if(delta>=(1.5*avglength)){
            return type.toString().toUpperCase();
        }else if(delta<=avglength/2){
            return "s"+type.toString();
        }
        return type.toString();
    }

    static Boolean checkIntersection(int ax, int ay, int bx, int by, int cx, int cy, int dx, int dy) {
        int det = (bx - ax)*(dy - cy) - (by - ay)*(dx - cx);
        if (det != 0) {
            /*
             * Lines intersect. Check if intersection point is on both segments:
             */
            int detu = (cx - ax)*(dy - cy) - (cy - ay)*(dx - cx);
            int detv = (cx - ax)*(by - ay) - (cy - ay)*(bx - ax);
            if (det < 0) {
                // Normalise to det>0 to simplify the following check.
                det = -det;
                detu = -detu;
                detv = -detv;
            }
            if (detu >= 0 && detu <= det && detv >= 0 && detv <= det) {
                System.out.println("INTERSECT");
                return true;
            } else {
                System.out.println("NO NOT INTERSECT");
                return false;
            }
        } else {
            /*
             * Lines are parallel (or identical):
             */
            System.out.println("PARALLEL");
        }
        return false;
    }

    public static String calculateRelationBetweenTwoLines(LineParameters line,LineParameters newline){
        StringBuilder returnString=new StringBuilder();
        switch(line.type){
            case A:
                System.out.println("Case A: "+crosses(newline,line));
                if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !newline.startX.equals(line.startX)) {
                    returnString.append(":");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }else if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && crosses(newline,line)){
                    returnString.append("-");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }else if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && !crosses(newline,line) && newline.startX>=line.endX){
                    returnString.append("_");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }else if((newline.type==StrokeType.C || newline.type==StrokeType.D) && !crosses(newline,line)){

                }else if((newline.type==StrokeType.C || newline.type==StrokeType.D) && crosses(newline,line)){

                }else if((newline.type==StrokeType.E || newline.type==StrokeType.F) && !crosses(newline,line)){

                }else if((newline.type==StrokeType.E || newline.type==StrokeType.F) && crosses(newline,line)){

                }
                //lines.add(newline);
                return returnString.toString();
            case B:
                System.out.println("Case B: "+crosses(newline,line));
                if((newline.type==StrokeType.B || newline.type==StrokeType.INV_B) && !newline.startX.equals(line.startX) && !newline.startY.equals(line.startY)) {
                    //Parallel horizontal
                    //lines.add(newline);
                    returnString.append(":");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                    returnString.append("-");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line) && newline.startX>=line.endX){
                    returnString.append("_");
                    returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                }
                return returnString.toString();
            case C:
                if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                    if(newline.deltaY==0){
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("'");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("-");
                    }
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){
                    if(newline.deltaY==0){
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("-");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("_");
                    }
                }else if(newline.type==StrokeType.C && crosses(newline,line)){
                    if(startsDiagonallyToAndIntersects(line,newline)){
                        return ".";
                    }else{
                        return ",";
                    }
                }
                break;
            case D:
                if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && crosses(newline,line)){
                    if(newline.deltaY==0){
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("'");
                    }else{
                        returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                        returnString.append("-");
                    }
                }else if((newline.type==StrokeType.A || newline.type==StrokeType.INV_A) && !crosses(newline,line)){

                }
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                break;
            case E:
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
                break;
            case F:
                returnString.append(smallNormalOrBigStroke(newline.type,newline.deltaX));
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
        drawnSign.append(smallNormalOrBigStroke(line.type,line.deltaX));
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
        lines.add(new LineParameters((float)0.,(float)0.,(float)0.,(float)5.,(float)0.,(float)5.,StrokeType.B));
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
