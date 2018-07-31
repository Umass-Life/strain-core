package util;


public class Validation {
    public static void checkNotNull(Object obj, String msg) throws NullPointerException {
        if (obj == null){
            throw new NullPointerException(msg);
        }
    }

    public static void checkNotNull(Object obj) throws NullPointerException{
        checkNotNull(obj, String.format("Object of type %s is null", obj.getClass().getName()));
    }

    public static void checkNumeric(String input, String msg) throws NumberFormatException {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(msg);
        }

    }

    public static Integer parseInt(String string, String errMsg) throws NumberFormatException {
        try {
            Integer x = Integer.parseInt(string);
            return x;
        } catch (NumberFormatException e){
            throw new NumberFormatException(e.getMessage());
        }
    }

}
