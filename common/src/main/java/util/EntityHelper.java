package util;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Consumer;

public class EntityHelper {
    public static class Options<T> {
        private final Object object;
        private final Class<T> clazz;
        private final String key;
        private final Map<String, Object> schema;
        public Options(final Object object, final Class<T> clazz, final String key, final Map<String, Object> schema){
            this.object = object;
            this.clazz = clazz;
            this.key = key;
            this.schema = schema;
        }

        public Options<T> required(){
            if (this.object == null) throw new IllegalArgumentException(String.format("[FIELD REQUIREMENT ERR]: null field \"%s\" for json: %s", key, schema));
            if (!clazz.isAssignableFrom(object.getClass())) throw new IllegalArgumentException(
                    String.format("[FIELD REQUIREMENT ERR]: type mismatch for required type %s and data objects type: %s", clazz.getName(), object.getClass().getName())
            );
            return this;
        }

        public T get(){
            return (T) this.object;
        }
    }

    public static <T> Options<T> updateBlock(String key, Map<String, Object> updateSchema, Class<T> c, Consumer<T> f){
        if (updateSchema.containsKey(key)){
            T o = (T)getFieldFromJSON(updateSchema, key, c);
            if (o!=null){
                f.accept(o);
            }
            return new Options<T>(o, c, key, updateSchema);
        }
        return new Options<T>(null, c, key, updateSchema);
    }


    public static <T> T getFieldFromJSON(Map<String, Object> json, String key, Class<T> clazz){
        if (json.containsKey(key)){
            T value = (T)json.get(key);
//            System.out.format("key:%s value:%s class:%s\n", key, value, value.getClass().getSimpleName());
            if (value != null && clazz.isAssignableFrom(value.getClass())){
                return value;
            }
        }
        return null;
    }

    public static Date MIN_DATE(){
        return java.sql.Date.valueOf((LocalDate.now().minusYears(1000)));
    }

    public static Date MAX_DATE(){
        return java.sql.Date.valueOf((LocalDate.now().plusYears(1000)));
    }

    public static Date localDateToDate(LocalDate localDate){
        return java.sql.Date.valueOf(localDate);
    }


    // d1 < d2 -> -1
    // d1 == d2 -> 0
    // d1 > d2 -> 1
    public static int compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        Integer c1_date = c1.get(Calendar.DATE);
        Integer c1_month = c1.get(Calendar.MONTH);
        Integer c1_year = c1.get(Calendar.YEAR);
//        System.out.format("[%s/%s/%s] vs " ,c1_date,c1_month,c1_year);

        Integer c2_date = c2.get(Calendar.DATE);
        Integer c2_month = c2.get(Calendar.MONTH);
        Integer c2_year = c2.get(Calendar.YEAR);
//        System.out.format("[%s/%s/%s] -> (%s/%s/%s)\n",c2_date,c2_month,c2_year, c1_date.compareTo(c2_date), c1_month.compareTo(c2_month),c1_year.compareTo(c2_year));

        if (c1_year.compareTo(c2_year) == 0) {
            if (c1_month.compareTo(c2_month) == 0) {
                return c1_date.compareTo(c2_date);
            } else return c1_month.compareTo(c2_month);

        } else return c1_year.compareTo(c2_year);

    }

    public static boolean validateDateAfterToday(Date date){
        Date today = Calendar.getInstance().getTime();
        return compareDate(today, date) != 1;
    }

    public static <T> List<T> iterableToList(Iterable<T> itr){
        List<T> Out = new ArrayList<>();
        itr.forEach(x -> Out.add(x));
        return Out;
    }

    public static <T, K> List<K> iterableToList(Iterable<T> itr, Class<K> typeSuper){
        List<K> Out = new ArrayList<>();
        itr.forEach(x -> {
            if (!(typeSuper.isAssignableFrom(x.getClass())))
                throw new IllegalArgumentException("[Utilities.iterableToList] cannot cast iterable of type" +
                        x.getClass().getSimpleName() + " to type " + typeSuper.getSimpleName());
            Out.add(typeSuper.cast(x));
        });
        return Out;
    }

    public static <T, K> Map<T, K> buildMap(Pair<T,K>[] pairs){
        Map<T, K> m = new HashMap<>();
        for(Pair<T, K> edge : pairs){
            m.put(edge.getKey(), edge.getValue());
        }
        return m;
    }

    public static final String ANSI_RESET = "\u001B[0m";

    public static String SUCCESS_STR(String str){
        return "\u001B[32m"+str+ANSI_RESET;
    }

    public static String FAIL_STR(String str){
        return "\u001B[31m"+str+ANSI_RESET;
    }

    public static long toEpochMilli(LocalDateTime localDateTime)
    {
        return localDateTime.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    public static String epochToDateString(Long epoch){
        LocalDateTime d =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
        return d.toString();

    }

    public static int iterableSize(Iterable A){
        if (A==null) return 0;
        int cnt = 0;
        Iterator itr = A.iterator();
        while(itr.hasNext()){
            cnt++;
            itr.next();
        }
        return cnt;
    }
//
//    public static void main(String[] args){
//        System.out.println(epochToDateString(1532389067948L));
//    }
}

