package com.lijangop.sdk.utils;

import android.content.Context;

/**
 * @Author lijiangop
 * @CreateTime 2020/6/12 10:10
 */
public class Gid {

    /**
     * R.string.name||0x7f0a0018-->value<br>
     */
    public static String getS(Context act, Object obj){
        String result = null;
        if(obj instanceof String){
            result = (String) obj;
        }else if(obj instanceof Integer){
            try{
                result = act.getResources().getString((Integer) obj);
            }catch(Exception e){
                //				System.err.println(e.toString());
            };
        }
        return result;
    }

    public static Object getValueDef(Context ctx, String resID, String def){
        Object obj = getValue(ctx, resID, new Object[]{});
        if(obj == null) obj = def;
        return obj;
    }

    /**
     * R.layout.name/R.string.name/R.attr.name/R.color.name<br>
     * /R.dimen.name/R.id.name/R.drawable.name<br>
     * /R.integer.name/R.menu.name/R.array.name<br>
     * /R.styleable.name/R.style.name<br>
     */
    public static Object getValue(Context ctx, String resID){
        return getValue(ctx, resID, new Object[]{});
    }
    public static Object getValue(Context ctx, String resID, Object... formatArgs){
        Object obj = null;
        if((resID != null && resID.contains(".")) && ctx != null){
            try{
                if(resID.contains("R.layout")){
                    obj = ctx.getResources().getLayout(getID(ctx, resID));
                }else if(resID.contains("R.string")){
                    if(formatArgs == null || formatArgs.length < 1){
                        obj = ctx.getResources().getString(getID(ctx, resID));
                    }else{
                        obj = ctx.getResources().getString(getID(ctx, resID), formatArgs);
                    }
                }else if(resID.contains("R.color")){
                    obj = ctx.getResources().getColor(getID(ctx, resID));
                }else if(resID.contains("R.dimen")){
					/*obj = ctx.getResources().getDimensionPixelOffset(getID(ctx, resID));
					obj = ctx.getResources().getDimensionPixelSize(getID(ctx, resID));*/
                    obj = ctx.getResources().getDimension(getID(ctx, resID));
                }else if(resID.contains("R.drawable")){
                    obj = ctx.getResources().getDrawable(getID(ctx, resID));
                }else if(resID.contains("R.integer")){
                    obj = ctx.getResources().getInteger(getID(ctx, resID));
                }else if(resID.contains("R.array")){
                    try{
                        obj = ctx.getResources().getStringArray(getID(ctx, resID));
                    }catch(Exception e){
                        try{
                            obj = ctx.getResources().getIntArray(getID(ctx, resID));
                        }catch(Exception ee){
                            obj = null;
                        }
                    }
                }else{
                    obj = null;
                }
            }catch (Exception e) {
                obj = null;
            }
        }
        return obj;
    }

    /**
     * "R.type.name-->id"<br>
     * @param resID "R.layout.main_ui"
     */
    public static int getID(Context ctx, String resID){
        int id = -1;
        if((resID != null && resID.contains(".")) && ctx != null){
            String[] ary = resID.split("\\.");
            if(resID.contains("R.styleable")){
                id = getIdByName(ctx, ary[1], ary[2]);
            }else{
                try{
                    id = ctx.getResources().getIdentifier(ary[2], ary[1], ctx.getPackageName());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return id;
    }
    /**
     * "R.type.name-->ids"<br>
     * @param resID "R.styleable.params"
     */
    public static int[] getIDs(Context ctx, String resID){
        int[] ids = new int[]{};
        if((resID != null && resID.contains(".")) && ctx != null){
            String[] ary = resID.split("\\.");
            ids = getIdsByName(ctx, ary[1], ary[2]);
        }
        return ids;
    }

    public static int getIdByName(Context ctx, String className, String name){
        int id = 0;
        try {
            String packageName = ctx.getPackageName();
            Class cl = Class.forName(packageName + ".R");
            Class[] cls = cl.getClasses();
            Class desireClass = null;
            for (int i = 0; i < cls.length; ++i) {
                if (cls[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = cls[i];
                    break;
                }
            }
            if (desireClass != null) id = desireClass.getField(name).getInt(desireClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return id;
    }
    public static int[] getIdsByName(Context ctx, String className, String name){
        int[] ids = null;
        try {
            String packageName = ctx.getPackageName();
            Class cl = Class.forName(packageName + ".R");
            Class[] cls = cl.getClasses();
            Class desireClass = null;
            for (int i = 0; i < cls.length; ++i) {
                if (cls[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = cls[i];
                    break;
                }
            }
            if ((desireClass != null)
                    && (desireClass.getField(name).get(desireClass) != null)
                    && (desireClass.getField(name).get(desireClass).getClass().isArray())){
                ids = (int[]) desireClass.getField(name).get(desireClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
